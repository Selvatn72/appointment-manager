package com.aptmgt.admin.services.email;

import com.aptmgt.admin.dto.PublicHolidayRequestDTO;
import com.aptmgt.commons.model.AppointmentEntity;
import com.aptmgt.commons.model.BranchEntity;
import com.aptmgt.commons.model.BranchFilesEntity;
import com.aptmgt.commons.model.EmployeeEntity;
import com.aptmgt.commons.utils.AppUtils;
import com.aptmgt.commons.utils.Constants;
import com.aptmgt.commons.utils.DateUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@PropertySource(value = {"classpath:application.yml"})
public class EmailService {

    public static final String MAIL_FOLDER_PATH = "mail/";

    @Value("${spring.robot.email.from}")
    private String from;

    @Value("${spring.SMTP.mail.to}")
    private String to;

    @Autowired()
    private JavaMailSender emailSender;

    @Qualifier("freeMarkerConfiguration")
    @Autowired
    private Configuration freemarkerConfig;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    public void sendSimpleMessageByTemplate(Mail mail, String templateName) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            Template template = freemarkerConfig.getTemplate(MAIL_FOLDER_PATH + templateName);
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, mail.getModel());
            helper.setTo(mail.getTo());
            helper.setText(html, true);
            helper.setSubject(mail.getSubject());
            helper.setFrom(from);
            if (mail.getFile() != null && mail.getFileName() != null) {
                helper.addAttachment(mail.getFileName(), new ByteArrayResource(mail.getFile()));
            }
            if (mail.getFiles() != null && !mail.getFiles().isEmpty()) {
                for (BranchFilesEntity branchFilesEntity : mail.getFiles()) {
                    try {
                        helper.addAttachment(branchFilesEntity.getFileName(), new ByteArrayResource(branchFilesEntity.getImage()));
                    } catch (MessagingException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
            emailSender.send(message);
            LOGGER.info("Email send!");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static String getWhenStr(AppointmentEntity appointment) {
        return DateUtils.getDayInWords(appointment.getAppointmentDate()) + Constants.COMMA
                + DateUtils.getMonthInWords(appointment.getAppointmentDate()) + Constants.SPACE
                + DateUtils.getDayInMonth(appointment.getAppointmentDate()) + Constants.AT
                + DateUtils.get12HrsFrom24Hrs(appointment.getStartTime());
    }

    public static String getDateAsString(PublicHolidayRequestDTO publicHoliday) {
        return DateUtils.getDayInWords(publicHoliday.getPublicHoliday()) + Constants.COMMA
                + DateUtils.getMonthInWords(publicHoliday.getPublicHoliday()) + Constants.SPACE
                + DateUtils.getDayInMonth(publicHoliday.getPublicHoliday());
    }

    @Async
    public void sendPasswordNotification(EmployeeEntity employeeEntity, String password) {
        try {
            Map<String, String> model = new HashMap<>();

            model.put(Constants.USERNAME, AppUtils.capitalize(employeeEntity.getName()));
            model.put(Constants.EMAIL, employeeEntity.getEmail());
            model.put(Constants.PASSWORD, password);

            Mail mail = new Mail();
            mail.setSubject(Constants.ACCOUNT_CREATION_NOTIFICATION);
            mail.setTo(AppUtils.getArrayFromString(employeeEntity.getEmail()));
            mail.setModel(model);
            sendSimpleMessageByTemplate(mail, Constants.EMPLOYEE_PASSWORD_TEMPLATE);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Async
    public void sendCancelNotification(List<AppointmentEntity> appointmentEntityList, PublicHolidayRequestDTO publicHoliday) {
        appointmentEntityList.forEach(appointmentEntity -> {
            if (appointmentEntity.getAppointeeEmail() != null) {
                //Send notification to customer
                sendCancelNotificationForCustomer(appointmentEntity);
            }
        });

        //Send notification to admin
        sendCancelNotificationForAdmin(appointmentEntityList.get(0), appointmentEntityList.get(0).getBranchId().getAdminId().getEmail(), appointmentEntityList.size(), publicHoliday);

        //Send notification to branch manager
        sendCancelNotificationForAdmin(appointmentEntityList.get(0), appointmentEntityList.get(0).getEmployeeId().getEmail(), appointmentEntityList.size(), publicHoliday);
    }

    private void sendCancelNotificationForAdmin(AppointmentEntity appointmentEntity, String mailId, Integer appointmentCount, PublicHolidayRequestDTO publicHoliday) {
        try {
            Map<String, String> replacements = new HashMap<>();
            replacements.put(Constants.USERNAME, AppUtils.capitalize(appointmentEntity.getBranchId().getAdminId().getName()));
            replacements.put(Constants.BRANCH_NAME, appointmentEntity.getBranchId().getName());
            replacements.put(Constants.LOCATION, appointmentEntity.getBranchId().getAddress());
            replacements.put(Constants.WHEN, getDateAsString(publicHoliday));
            replacements.put(Constants.APPOINTMENT_COUNT, appointmentCount.toString());

            Mail mail = new Mail();
            String template = Constants.APPOINTMENT_CANCEL_ADMIN_TEMPLATE;
            if (appointmentEntity.getReasonForCancel() != null && !appointmentEntity.getReasonForCancel().isEmpty()) {
                replacements.put(Constants.REASON_FOR_CANCEL, appointmentEntity.getReasonForCancel());
                template = Constants.APPOINTMENT_REASON_FOR_CANCEL_ADMIN_TEMPLATE;
            }
            mail.setSubject(Constants.APPOINTMENT_CANCEL_EMAIL_SUBJECT);
            mail.setTo(AppUtils.getArrayFromString(mailId));
            mail.setModel(replacements);
            sendSimpleMessageByTemplate(mail, template);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void sendCancelNotificationForCustomer(AppointmentEntity appointmentEntity) {
        try {
            Map<String, String> replacements = new HashMap<>();
            replacements.put(Constants.USERNAME, AppUtils.capitalize(appointmentEntity.getAppointeeName()));
            replacements.put(Constants.BRANCH_NAME, appointmentEntity.getBranchId().getName());
            replacements.put(Constants.SERVICE_NAME, appointmentEntity.getServiceId().getServiceName());
            replacements.put(Constants.WHEN, getWhenStr(appointmentEntity));
            replacements.put(Constants.PERSON_COUNT, appointmentEntity.getPersonCount().toString());

            Mail mail = new Mail();
            String template = Constants.APPOINTMENT_CANCEL_TEMPLATE;
            if (appointmentEntity.getReasonForCancel() != null && !appointmentEntity.getReasonForCancel().isEmpty()) {
                replacements.put(Constants.REASON_FOR_CANCEL, appointmentEntity.getReasonForCancel());
                template = Constants.APPOINTMENT_REASON_FOR_CANCEL_TEMPLATE;
            }
            mail.setSubject(Constants.APPOINTMENT_CANCEL_EMAIL_SUBJECT);
            mail.setTo(AppUtils.getArrayFromString(appointmentEntity.getAppointeeEmail()));
            mail.setModel(replacements);
            sendSimpleMessageByTemplate(mail, template);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Async
    public void sendCancelNotification(AppointmentEntity appointmentEntity) {
        //Send notification to customer
        sendCancelNotificationForCustomer(appointmentEntity);

        //Send notification to admin
        sendCancelNotificationForAdmin(appointmentEntity, appointmentEntity.getBranchId().getAdminId().getEmail());

        //Send notification to branch manager
        sendCancelNotificationForAdmin(appointmentEntity, appointmentEntity.getBranchId().getMail());
    }

    private void sendCancelNotificationForAdmin(AppointmentEntity appointmentEntity, String email) {
        try {
            Map<String, String> replacements = new HashMap<>();
            replacements.put(Constants.USERNAME, AppUtils.capitalize(appointmentEntity.getBranchId().getAdminId().getName()));
            replacements.put(Constants.BRANCH_NAME, appointmentEntity.getBranchId().getName());
            replacements.put(Constants.CONTACT_NUMBER, appointmentEntity.getBranchId().getPhone());
            replacements.put(Constants.LOCATION, appointmentEntity.getBranchId().getAddress());
            replacements.put(Constants.WHEN, getWhenStr(appointmentEntity));
            replacements.put(Constants.APPOINTMENT_TIME, DateUtils.get12HrsFrom24Hrs(appointmentEntity.getStartTime()));

            Mail mail = new Mail();
            String template = Constants.APPOINTMENT_CANCEL_ADMIN_TEMPLATE;
            if (appointmentEntity.getReasonForCancel() != null && !appointmentEntity.getReasonForCancel().isEmpty()) {
                replacements.put(Constants.REASON_FOR_CANCEL, appointmentEntity.getReasonForCancel());
                template = Constants.APPOINTMENT_REASON_FOR_CANCEL_ADMIN_TEMPLATE;
            }
            mail.setSubject(Constants.APPOINTMENT_CANCEL_EMAIL_SUBJECT);
            mail.setTo(AppUtils.getArrayFromString(email));
            mail.setModel(replacements);
            sendSimpleMessageByTemplate(mail, template);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Async
    public void sendSuperAdminNotification(BranchEntity branchEntity, List<BranchFilesEntity> files) {
        try {
            Map<String, String> model = new HashMap<>();

            model.put(Constants.BRANCH_ID, branchEntity.getBranchId().toString());
            model.put(Constants.BRANCH_NAME, branchEntity.getName());
            model.put(Constants.PHONE, branchEntity.getPhone());
            model.put(Constants.EMAIL, branchEntity.getMail());
            model.put(Constants.ADMIN_NAME, branchEntity.getAdminId().getName());

            Mail mail = new Mail();
            mail.setSubject(Constants.BRANCH_CREATION_SUBJECT);
            mail.setModel(model);
            mail.setTo(AppUtils.getArrayFromString(to));
            mail.setFile(branchEntity.getImage());
            mail.setFileName(branchEntity.getFileName());
            mail.setFiles(files);
            sendSimpleMessageByTemplate(mail, Constants.BRANCH_CREATION_TEMPLATE);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}