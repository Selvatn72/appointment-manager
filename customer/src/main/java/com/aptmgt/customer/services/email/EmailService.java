package com.aptmgt.customer.services.email;

import com.aptmgt.commons.model.AppointmentEntity;
import com.aptmgt.commons.utils.AppUtils;
import com.aptmgt.commons.utils.Constants;
import com.aptmgt.commons.utils.DateUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@PropertySource(value = {"classpath:application.yml"})
public class EmailService {

    public static final String MAIL_FOLDER_PATH = "mail/";

    @Value("${spring.robot.email.from}")
    private String from;

    @Autowired()
    private JavaMailSender emailSender;

    @Qualifier("freeMarkerConfiguration")
    @Autowired
    private Configuration freemarkerConfig;

    @Value("${application.calender-api.google}")
    private String googleApiUrl;

    @Value("${application.map-url}")
    private String googleMapUrl;

    @Autowired
    private ResourceLoader resourceLoader;

    private static final String RESOURCE_PATH = "classpath:templates/mail/images/*.*";

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    public void sendSimpleMessageByTemplate(Mail mail, String templateName, List<Resource> resourceList) {
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
            if (resourceList != null && !resourceList.isEmpty()) {
                for (Resource resource : resourceList) {
                    helper.addInline(FilenameUtils.getName(resource.getFilename()), resource);
                }
            }
            emailSender.send(message);
            LOGGER.info("Email send!");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Async
    public void sendScheduleNotification(AppointmentEntity appointmentEntity) {
        //Send notification to customer
        sendScheduleNotificationForCustomer(appointmentEntity);

        //Send notification to admin
        sendScheduleNotificationForAdmin(appointmentEntity, appointmentEntity.getBranchId().getAdminId().getEmail());

        //Send notification to branch
        sendScheduleNotificationForAdmin(appointmentEntity, appointmentEntity.getBranchId().getMail());
    }

    private void sendScheduleNotificationForAdmin(AppointmentEntity appointmentEntity, String email) {
        try {
            Long serviceCategoryId = appointmentEntity.getBranchId().getServiceCategoryId().getServiceCategoryId();
            Map<String, String> replacements = new HashMap<>();
            replacements.put(Constants.USERNAME, AppUtils.capitalize(appointmentEntity.getBranchId().getAdminId().getName()));
            replacements.put(Constants.ROLE, serviceCategoryId != 3 ? Constants.CUSTOMER_LOWER_CASE : Constants.PATIENT);
            replacements.put(Constants.CUSTOMER_NAME, appointmentEntity.getCustomerId().getName());
            replacements.put(Constants.BRANCH_NAME, appointmentEntity.getBranchId().getName());
            replacements.put(Constants.CONTACT_NUMBER, appointmentEntity.getBranchId().getPhone());
            replacements.put(Constants.LOCATION, appointmentEntity.getBranchId().getAddress());
            replacements.put(Constants.WHEN, getWhenStr(appointmentEntity));
            replacements.put(Constants.APPOINTMENT_TIME, DateUtils.get12HrsFrom24Hrs(appointmentEntity.getStartTime()));

            Mail mail = new Mail();
            mail.setSubject(Constants.APPOINTMENT_SCHEDULE_EMAIL_SUBJECT);
            mail.setTo(AppUtils.getArrayFromString(email));
            mail.setModel(replacements);
            sendSimpleMessageByTemplate(mail, Constants.APPOINTMENT_SCHEDULE_ADMIN_TEMPLATE, null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void sendScheduleNotificationForCustomer(AppointmentEntity appointmentEntity) {
        try {
            Map<String, String> replacements = new HashMap<>();
            replacements.put(Constants.USERNAME, AppUtils.capitalize(appointmentEntity.getAppointeeName()));
            replacements.put(Constants.BRANCH_NAME, appointmentEntity.getBranchId().getName() + Constants.HYPHEN + appointmentEntity.getBranchId().getAddress());
            replacements.put(Constants.SERVICE_NAME, appointmentEntity.getServiceId().getServiceName());
            replacements.put(Constants.WHEN, getWhenStr(appointmentEntity));
            replacements.put(Constants.PERSON_COUNT, appointmentEntity.getPersonCount().toString());
            replacements.put(Constants.GOOGLE_CALENDER_URL, getCalenderApiURI(googleApiUrl,
                    getQueryParamForGoogleApi(appointmentEntity)));
            replacements.put(Constants.MAP_URL, getMapAPIURL(googleMapUrl,
                    appointmentEntity.getBranchId().getLatitude(), appointmentEntity.getBranchId().getLongitude()));

            List<Resource> resourceList = loadResources(RESOURCE_PATH);

            Mail mail = new Mail();
            mail.setSubject(Constants.APPOINTMENT_SCHEDULE_EMAIL_SUBJECT);
            mail.setTo(AppUtils.getArrayFromString(appointmentEntity.getAppointeeEmail()));
            mail.setModel(replacements);
            sendSimpleMessageByTemplate(mail, Constants.APPOINTMENT_SCHEDULE_TEMPLATE, resourceList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static String getWhenStr(AppointmentEntity appointment) {
        return DateUtils.getDayInMonth(appointment.getAppointmentDate()) + Constants.SPACE
                + DateUtils.getMonthInWords(appointment.getAppointmentDate()) + Constants.SPACE
                + DateUtils.getYearFromDate(appointment.getAppointmentDate()) + Constants.COMMA
                + DateUtils.get12HrsFrom24Hrs(appointment.getStartTime());
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
            Long serviceCategoryId = appointmentEntity.getBranchId().getServiceCategoryId().getServiceCategoryId();
            Map<String, String> replacements = new HashMap<>();
            replacements.put(Constants.USERNAME, AppUtils.capitalize(appointmentEntity.getBranchId().getAdminId().getName()));
            replacements.put(Constants.ROLE, serviceCategoryId != 3 ? Constants.CUSTOMER_LOWER_CASE : Constants.PATIENT);
            replacements.put(Constants.CUSTOMER_NAME, appointmentEntity.getCustomerId().getName());
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
            sendSimpleMessageByTemplate(mail, template, null);
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

            List<Resource> resourceList = loadResources(RESOURCE_PATH);

            Mail mail = new Mail();
            String template = Constants.APPOINTMENT_CANCEL_TEMPLATE;
            if (appointmentEntity.getReasonForCancel() != null && !appointmentEntity.getReasonForCancel().isEmpty()) {
                replacements.put(Constants.REASON_FOR_CANCEL, appointmentEntity.getReasonForCancel());
                template = Constants.APPOINTMENT_REASON_FOR_CANCEL_TEMPLATE;
            }
            mail.setSubject(Constants.APPOINTMENT_CANCEL_EMAIL_SUBJECT);
            mail.setTo(AppUtils.getArrayFromString(appointmentEntity.getAppointeeEmail()));
            mail.setModel(replacements);

            sendSimpleMessageByTemplate(mail, template, resourceList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static String getCalenderApiURI(String uri, String query) {
        try {
            URI oldUri = new URI(uri);
            return new URI(oldUri.getScheme(), oldUri.getAuthority(), oldUri.getPath(),
                    oldUri.getQuery() == null ? query : oldUri.getQuery() + Constants.AND + query, oldUri.getFragment())
                    .toString();
        } catch (URISyntaxException e) {
            return Constants.HASH;
        }
    }

    private static String getQueryParamForGoogleApi(AppointmentEntity appointment) {
        String title = appointment.getBranchId().getName() + " Appointment";
        String location = appointment.getBranchId().getAddress();

        String dates = getDateForCalenderApiParam(Constants.GOOGLE_CALENDER_API_DATE_FORMAT,
                Constants.GOOGLE_CALENDER_API_TIME_FORMAT, appointment.getAppointmentDate(),
                appointment.getStartTime())
                + "/"
                + getDateForCalenderApiParam(Constants.GOOGLE_CALENDER_API_DATE_FORMAT,
                Constants.GOOGLE_CALENDER_API_TIME_FORMAT, appointment.getAppointmentDate(),
                appointment.getEndTime());

        return "text=" + title + Constants.AND + "location=" + location + Constants.AND + "dates=" + dates;
    }

    private static String getDateForCalenderApiParam(String dateFormat, String timeFormat, Date date, Time time) {
        try {
            return new SimpleDateFormat(dateFormat).format(date) + "T" + new SimpleDateFormat(timeFormat).format(time);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return Constants.BLANK;
    }

    private static String getMapAPIURL(String googleMapUrl, Float lat, Float lng) {
        try {
            URI baseURI = new URI(googleMapUrl);
            return new URI(baseURI + Constants.COLON + lat + Constants.PLUS + lng).toString();
        } catch (URISyntaxException e) {
            return Constants.HASH;
        }
    }

    private List<Resource> loadResources(String pattern) throws IOException {
        Resource[] resourceArr = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(pattern);
        return new ArrayList<>(Arrays.asList(resourceArr));
    }
}