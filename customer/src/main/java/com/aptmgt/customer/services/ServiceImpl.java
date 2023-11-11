package com.aptmgt.customer.services;


import com.aptmgt.commons.dto.ServiceResponseDTO;
import com.aptmgt.commons.exceptions.InternalServerErrorException;
import com.aptmgt.commons.exceptions.RecordNotFoundException;
import com.aptmgt.commons.model.ServiceCategoryEntity;
import com.aptmgt.commons.model.ServiceEntity;
import com.aptmgt.commons.repository.ServiceCategoryRepository;
import com.aptmgt.commons.repository.ServiceRepository;
import com.aptmgt.commons.utils.ResponseMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class ServiceImpl implements IServices {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceImpl.class);

    private ServiceCategoryEntity findServiceCategoryById(Long serviceCategoryId) {
        Optional<ServiceCategoryEntity> serviceCategoryEntityOptional = serviceCategoryRepository.findById(serviceCategoryId);
        if (!serviceCategoryEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.SERVICE_CATEGORY_NOT_FOUND);
        }
        return serviceCategoryEntityOptional.get();
    }

    @Override
    public ResponseEntity<List<ServiceResponseDTO>> getBranchServices(Long serviceCategoryId) {
        ServiceCategoryEntity serviceCategoryEntity = findServiceCategoryById(serviceCategoryId);
        try {
            List<ServiceResponseDTO> serviceResponseDTOList = new ArrayList<>();
            List<ServiceEntity> serviceEntityList = serviceRepository.findByServiceCategoryId(serviceCategoryEntity);
            if (!serviceEntityList.isEmpty())
                serviceEntityList.forEach(service -> serviceResponseDTOList.add(new ServiceResponseDTO(service)));
            return ResponseEntity.ok(serviceResponseDTOList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    @Override
    public ResponseEntity<ServiceResponseDTO> getServiceById(Long serviceId) {
        Optional<ServiceEntity> serviceEntityOptional = serviceRepository.findById(serviceId);
        if(!serviceEntityOptional.isPresent()){
            throw  new RecordNotFoundException(ResponseMessages.SERVICE_NOT_FOUND);
        }
        return ResponseEntity.ok(new ServiceResponseDTO(serviceEntityOptional.get()));
    }

}
