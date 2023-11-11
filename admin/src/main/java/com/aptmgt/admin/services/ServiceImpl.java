package com.aptmgt.admin.services;

import com.aptmgt.admin.dto.ServiceRequestDTO;
import com.aptmgt.admin.services.email.EmailService;
import com.aptmgt.admin.services.email.Mail;
import com.aptmgt.commons.dto.MessageDTO;
import com.aptmgt.commons.dto.ServiceResponseDTO;
import com.aptmgt.commons.exceptions.BadRequestException;
import com.aptmgt.commons.exceptions.InternalServerErrorException;
import com.aptmgt.commons.exceptions.RecordNotFoundException;
import com.aptmgt.commons.model.AdminEntity;
import com.aptmgt.commons.model.ServiceCategoryEntity;
import com.aptmgt.commons.model.ServiceEntity;
import com.aptmgt.commons.model.ServiceRequestEntity;
import com.aptmgt.commons.repository.AdminRepository;
import com.aptmgt.commons.repository.ServiceCategoryRepository;
import com.aptmgt.commons.repository.ServiceRepository;
import com.aptmgt.commons.repository.ServiceRequestRepository;
import com.aptmgt.commons.utils.AppUtils;
import com.aptmgt.commons.utils.Constants;
import com.aptmgt.commons.utils.ResponseMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.*;

@org.springframework.stereotype.Service
public class ServiceImpl implements IServices {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private EmailService emailService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceImpl.class);

    @Override
    public ResponseEntity<MessageDTO> saveService(ServiceRequestDTO serviceRequestDTO) {
        if (serviceRequestDTO.getServiceName().isEmpty()) {
            throw new BadRequestException(ResponseMessages.SERVICE_NAME_CANNOT_BE_EMPTY);
        }
        List<ServiceEntity> serviceEntityList = new ArrayList<>();
        ServiceCategoryEntity serviceCategoryEntity = findServiceCategoryById(serviceRequestDTO.getServiceCategoryId());
        serviceRequestDTO.getServiceName().forEach(serviceName -> {
            ServiceEntity serviceEntity = new ServiceEntity();
            serviceEntity.setServiceCategoryId(serviceCategoryEntity);
            serviceEntity.setServiceName(serviceName);
            serviceEntityList.add(serviceEntity);
        });
        serviceRepository.saveAll(serviceEntityList);
        if (!serviceRequestDTO.getServiceRequestId().isEmpty()) {
            updateServiceRequest(serviceRequestDTO.getServiceRequestId());
        }
        return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.SERVICE_CREATED_MSG));
    }

    private void updateServiceRequest(List<Long> serviceRequestIds) {
        List<ServiceRequestEntity> serviceRequestEntityList = new ArrayList<>();
        ServiceRequestEntity serviceRequestEntity = null;
        String serviceNameList = "";
        for(Long serviceRequestId : serviceRequestIds) {
            serviceRequestEntity = serviceRequestRepository.findById(serviceRequestId).get();
            serviceRequestEntity.setStatus(Constants.APPROVED_STATUS);
            serviceRequestEntity.setUpdatedDate(new Timestamp(new Date().getTime()));
            serviceRequestEntityList.add(serviceRequestEntity);
            serviceNameList +=  serviceRequestEntity.getServiceName() + "<br>";
        }
        serviceRequestRepository.saveAll(serviceRequestEntityList);
        sendNotification(serviceRequestEntity.getAdminId(), serviceNameList);
    }

    private void sendNotification(AdminEntity admin, String serviceNameList) {
        try {
            Map<String, String> model = new HashMap<>();
            model.put(Constants.USERNAME, admin.getName());
            model.put(Constants.SERVICE_CREATED_MSG, Constants.SERVICE_CREATED_EMAIL_TEXT);
            model.put(Constants.SERVICE_NAME_LIST, serviceNameList);

            Mail mail = new Mail();
            mail.setSubject(Constants.SERVICE_CREATED_SUBJECT);
            mail.setTo(AppUtils.getArrayFromString(admin.getEmail()));
            mail.setModel(model);
            emailService.sendSimpleMessageByTemplate(mail, Constants.SERVICE_CONFIRMATION_TEMPLATE);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private ServiceCategoryEntity findServiceCategoryById(Long serviceCategoryId) {
        Optional<ServiceCategoryEntity> serviceCategoryEntityOptional = serviceCategoryRepository.findById(serviceCategoryId);
        if (!serviceCategoryEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.SERVICE_CATEGORY_NOT_FOUND);
        }
        return serviceCategoryEntityOptional.get();
    }

    private AdminEntity findAdminById(Long adminId) {
        Optional<AdminEntity> adminOptional = adminRepository.findById(adminId);
        if (!adminOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.ADMIN_NOT_FOUND);
        }
        return adminOptional.get();
    }

    @Override
    public ResponseEntity<List<ServiceResponseDTO>> fetchByServiceCategoryId(Long serviceCategoryId) {
        ServiceCategoryEntity serviceCategoryEntity = findServiceCategoryById(serviceCategoryId);
        try {
            List<ServiceResponseDTO> serviceResponseDTOList = new ArrayList<>();
            List<ServiceEntity> serviceEntityList = serviceRepository.findByServiceCategoryId(serviceCategoryEntity);
            if (!serviceEntityList.isEmpty()) {
                serviceEntityList.forEach(serviceEntity -> serviceResponseDTOList.add(new ServiceResponseDTO(serviceEntity)));
            }
            return ResponseEntity.ok(serviceResponseDTOList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    @Override
    public ResponseEntity<MessageDTO> deleteService(Long serviceId) {
        try {
            serviceRepository.deleteById(serviceId);
            return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.SERVICE_DELETED_MSG));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private ServiceEntity findServiceById(Long serviceId) {
        Optional<ServiceEntity> serviceEntity = serviceRepository.findById(serviceId);
        if (!serviceEntity.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.SERVICE_NOT_FOUND);
        }
        return serviceEntity.get();
    }
}
