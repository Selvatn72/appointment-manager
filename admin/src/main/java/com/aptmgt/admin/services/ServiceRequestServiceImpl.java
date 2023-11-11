package com.aptmgt.admin.services;

import com.aptmgt.admin.dto.ServiceRequestDTO;
import com.aptmgt.admin.dto.ServiceRequestResDTO;
import com.aptmgt.admin.services.email.EmailService;
import com.aptmgt.admin.services.email.Mail;
import com.aptmgt.commons.dto.MessageDTO;
import com.aptmgt.commons.exceptions.BadRequestException;
import com.aptmgt.commons.exceptions.InternalServerErrorException;
import com.aptmgt.commons.exceptions.RecordNotFoundException;
import com.aptmgt.commons.model.AdminEntity;
import com.aptmgt.commons.model.ServiceCategoryEntity;
import com.aptmgt.commons.model.ServiceRequestEntity;
import com.aptmgt.commons.repository.AdminRepository;
import com.aptmgt.commons.repository.ServiceCategoryRepository;
import com.aptmgt.commons.repository.ServiceRequestRepository;
import com.aptmgt.commons.utils.AppUtils;
import com.aptmgt.commons.utils.Constants;
import com.aptmgt.commons.utils.ResponseMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class ServiceRequestServiceImpl implements IServiceRequestService {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    @Autowired
    private EmailService emailService;

    @Value("${spring.SMTP.mail.to}")
    private String to;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRequestServiceImpl.class);

    @Override
    public ResponseEntity<MessageDTO> saveServiceRequest(ServiceRequestDTO serviceRequestDTO) {
        if (serviceRequestDTO.getServiceName().isEmpty()) {
            throw new BadRequestException(ResponseMessages.SERVICE_NAME_CANNOT_BE_EMPTY);
        }
        AdminEntity admin = findAdminById(serviceRequestDTO.getAdminId());
        ServiceCategoryEntity serviceCategory = findServiceCategoryById(serviceRequestDTO.getServiceCategoryId());
        try {
            List<ServiceRequestEntity> serviceRequestEntityList = new ArrayList<>();
            serviceRequestDTO.getServiceName().forEach(serviceName -> {
                ServiceRequestEntity serviceRequestEntity = new ServiceRequestEntity();
                serviceRequestEntity.setAdminId(admin);
                serviceRequestEntity.setServiceCategoryId(serviceCategory);
                serviceRequestEntity.setServiceName(serviceName);
                serviceRequestEntity.setStatus(Constants.PENDING_STATUS);
                serviceRequestEntity.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
                serviceRequestEntity.setCreatedDate(new Timestamp(new Date().getTime()));
                serviceRequestEntityList.add(serviceRequestEntity);
            });
            serviceRequestRepository.saveAll(serviceRequestEntityList);
            sendNotification(admin, serviceCategory.getServiceCategory(), serviceRequestDTO);
            return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.SERVICE_REQ_CREATED));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new InternalServerErrorException();
        }
    }

    private void sendNotification(AdminEntity admin, String serviceCategory, ServiceRequestDTO serviceRequestDTO) {
        try {
            Map<String, String> model = new HashMap<>();
            model.put(Constants.ADMIN_ID, admin.getAdminId().toString());
            model.put(Constants.ADMIN_NAME, admin.getName());
            model.put(Constants.SERVICE_CATEGORY, serviceCategory);
            model.put(Constants.SERVICE_NAME_LIST, serviceRequestDTO.getServiceName().toString());
            model.put(Constants.SERVICE_REQUEST_MSG, Constants.SERVICE_REQUEST_EMAIL_TEXT);

            Mail mail = new Mail();
            mail.setSubject(Constants.SERVICE_REQUEST_SERVICE_MSG);
            mail.setTo(AppUtils.getArrayFromString(to));
            mail.setModel(model);
            emailService.sendSimpleMessageByTemplate(mail, Constants.SERVICE_REQ_TEMPLATE);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public ResponseEntity<List<ServiceRequestResDTO>> getServiceRequestList(Long adminId) {
        AdminEntity admin = findAdminById(adminId);
        try {
            List<ServiceRequestResDTO> serviceRequestResDTOList = new ArrayList<>();
            List<ServiceRequestEntity> serviceRequestEntityList = serviceRequestRepository.findByAdminId(admin);
            if (!serviceRequestEntityList.isEmpty()) {
                serviceRequestEntityList.forEach(serviceRequestEntity -> serviceRequestResDTOList.add(new ServiceRequestResDTO(serviceRequestEntity)));
            }
            return ResponseEntity.ok(serviceRequestResDTOList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new InternalServerErrorException();
        }
    }

    private AdminEntity findAdminById(Long adminId) {
        Optional<AdminEntity> adminOptional = adminRepository.findById(adminId);
        if (!adminOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.ADMIN_NOT_FOUND);
        }
        return adminOptional.get();
    }

    private ServiceCategoryEntity findServiceCategoryById(Long serviceCategoryId) {
        Optional<ServiceCategoryEntity> serviceCategoryEntityOptional = serviceCategoryRepository.findById(serviceCategoryId);
        if (!serviceCategoryEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.SERVICE_CATEGORY_NOT_FOUND);
        }
        return serviceCategoryEntityOptional.get();
    }
}
