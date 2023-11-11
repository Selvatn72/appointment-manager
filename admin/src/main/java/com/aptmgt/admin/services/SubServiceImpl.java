package com.aptmgt.admin.services;

import com.aptmgt.commons.dto.SubServiceResponseDTO;
import com.aptmgt.commons.exceptions.InternalServerErrorException;
import com.aptmgt.commons.exceptions.RecordNotFoundException;
import com.aptmgt.commons.model.ServiceEntity;
import com.aptmgt.commons.model.SubServiceEntity;
import com.aptmgt.commons.repository.ServiceRepository;
import com.aptmgt.commons.repository.SubServiceRepository;
import com.aptmgt.commons.utils.ResponseMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SubServiceImpl implements ISubService {

    @Autowired
    private SubServiceRepository subServiceRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(SubServiceImpl.class);

    @Override
    public ResponseEntity<List<SubServiceResponseDTO>> getByServiceId(Long serviceId) {
        ServiceEntity serviceEntity = findServiceById(serviceId);
        try {
            List<SubServiceResponseDTO> subServiceResponseDTOList = new ArrayList<>();
            List<SubServiceEntity> subServiceEntityList = subServiceRepository.findByServiceId(serviceEntity);
            if (!subServiceEntityList.isEmpty()) {
                subServiceEntityList.forEach(subServiceEntity -> subServiceResponseDTOList.add(new SubServiceResponseDTO(subServiceEntity)));
            }
            return ResponseEntity.ok(subServiceResponseDTOList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private ServiceEntity findServiceById(Long serviceId) {
        Optional<ServiceEntity> serviceEntityOptional = serviceRepository.findById(serviceId);
        if (!serviceEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.SERVICE_NOT_FOUND);
        }
        return serviceEntityOptional.get();
    }
}
