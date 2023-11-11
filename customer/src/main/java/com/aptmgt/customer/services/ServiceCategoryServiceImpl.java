package com.aptmgt.customer.services;

import com.aptmgt.commons.exceptions.InternalServerErrorException;
import com.aptmgt.commons.model.ServiceCategoryEntity;
import com.aptmgt.commons.repository.ServiceCategoryRepository;
import com.aptmgt.customer.dto.ServiceCategoryResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceCategoryServiceImpl implements IServiceCategoryService {

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceCategoryServiceImpl.class);

    @Override
    public ResponseEntity<List<ServiceCategoryResponseDTO>> getBranchCategory() {
        try {
            List<ServiceCategoryResponseDTO> serviceCategoryResponseDTOList = new ArrayList<>();
            List<ServiceCategoryEntity> serviceCategoryEntityList = serviceCategoryRepository.findAll();
            if (!serviceCategoryEntityList.isEmpty())
                serviceCategoryEntityList.forEach(serviceCategory -> serviceCategoryResponseDTOList.add(new ServiceCategoryResponseDTO(serviceCategory)));
            return ResponseEntity.ok(serviceCategoryResponseDTOList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }
}
