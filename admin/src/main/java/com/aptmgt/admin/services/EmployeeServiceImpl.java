package com.aptmgt.admin.services;

import com.aptmgt.commons.dto.EmployeeInfoReqDTO;
import com.aptmgt.commons.dto.EmployeeResponseDTO;
import com.aptmgt.commons.dto.MessageDTO;
import com.aptmgt.commons.exceptions.InternalServerErrorException;
import com.aptmgt.commons.exceptions.RecordNotFoundException;
import com.aptmgt.commons.model.BranchEntity;
import com.aptmgt.commons.model.EmployeeEntity;
import com.aptmgt.commons.model.ServiceEntity;
import com.aptmgt.commons.model.SubServiceEntity;
import com.aptmgt.commons.repository.BranchRepository;
import com.aptmgt.commons.repository.EmployeeRepository;
import com.aptmgt.commons.repository.ServiceRepository;
import com.aptmgt.commons.repository.SubServiceRepository;
import com.aptmgt.commons.utils.ResponseMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.*;

@Service
public class EmployeeServiceImpl implements IEmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private SubServiceRepository subServiceRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Override
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployeeListByBranchId(Long branchId) {
        List<EmployeeResponseDTO> employeeResponseDTOList = new ArrayList<>();
        BranchEntity branchEntity = findBranchById(branchId);
        List<EmployeeEntity> employeeEntityList = employeeRepository.findByBranchIdAndIsActive(branchEntity, true);

        if (!employeeEntityList.isEmpty()) {
            employeeEntityList.forEach(employeeEntity -> employeeResponseDTOList.add(new EmployeeResponseDTO(employeeEntity)));
        }
        return ResponseEntity.ok(employeeResponseDTOList);
    }

    @Override
    public ResponseEntity<MessageDTO> deleteEmployee(Long employeeId) {
        EmployeeEntity employeeEntity = findEmployeeById(employeeId);
        try {
            employeeEntity.setIsActive(false);
            employeeEntity.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            employeeEntity.setUpdatedDate(new Timestamp(new Date().getTime()));
            employeeRepository.save(employeeEntity);
            return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.EMPLOYEE_DELETED_MSG));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    @Override
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(Long employeeId) {
        return ResponseEntity.ok(new EmployeeResponseDTO(findEmployeeById(employeeId)));
    }

    @Override
    public ResponseEntity<Object> updateEmployee(EmployeeInfoReqDTO employeeInfoReqDTO, MultipartFile file) {
        EmployeeEntity employeeEntity = findEmployeeById(employeeInfoReqDTO.getId());
        List<SubServiceEntity> subServiceEntities = new ArrayList<>();
        employeeInfoReqDTO.getSubServiceId().forEach(subServiceId -> subServiceEntities.add(findSubServiceById(subServiceId)));
        try {
            employeeEntity.setName(employeeInfoReqDTO.getName());
            employeeEntity.setDegree(employeeInfoReqDTO.getDegree());
            employeeEntity.setExperience(employeeInfoReqDTO.getExperience());
            employeeEntity.setPhone(employeeInfoReqDTO.getPhone());
            employeeEntity.setFileName(file != null ? org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename()) : null);
            employeeEntity.setFileType(file != null ? file.getContentType() : null);
            employeeEntity.setImage(file != null ? file.getBytes() : null);
            employeeEntity.setServiceId(findServiceById(employeeInfoReqDTO.getServiceId()));
            employeeEntity.setSubServices(subServiceEntities);
            employeeEntity.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            employeeEntity.setUpdatedDate(new Timestamp(new Date().getTime()));
            employeeRepository.save(employeeEntity);
            return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.EMPLOYEE_UPDATED_MSG));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private SubServiceEntity findSubServiceById(Long subServiceId) {
        Optional<SubServiceEntity> subServiceEntityOptional = subServiceRepository.findById(subServiceId);
        if (!subServiceEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.SUB_SERVICE_NOT_FOUND);
        }
        return subServiceEntityOptional.get();
    }
    private ServiceEntity findServiceById(Long serviceId) {
        Optional<ServiceEntity> serviceEntityOptional = serviceRepository.findById(serviceId);
        if (!serviceEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.SERVICE_NOT_FOUND);
        }
        return serviceEntityOptional.get();
    }
    private EmployeeEntity findEmployeeById(Long employeeId) {
        Optional<EmployeeEntity> employeeEntityOptional = employeeRepository.findById(employeeId);
        if (!employeeEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.EMPLOYEE_NOT_FOUND);
        }
        return employeeEntityOptional.get();
    }

    public BranchEntity findBranchById(Long branchId) {
        Optional<BranchEntity> branchEntityOptional = branchRepository.findById(branchId);
        if (!branchEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.BRANCH_NOT_FOUND);
        }
        return branchEntityOptional.get();
    }
}
