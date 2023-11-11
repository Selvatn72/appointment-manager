package com.aptmgt.customer.services;

import com.aptmgt.commons.model.*;
import com.aptmgt.commons.repository.*;
import com.aptmgt.customer.dto.SearchBranchRequestDTO;
import com.aptmgt.customer.dto.TradingHoursResponseDTO;
import com.aptmgt.commons.dto.EmployeeResponseDTO;
import com.aptmgt.commons.exceptions.InternalServerErrorException;
import com.aptmgt.commons.exceptions.RecordNotFoundException;
import com.aptmgt.commons.utils.ResponseMessages;
import com.mysql.cj.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService{

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    @Autowired
    private SubServiceRepository subServiceRepository;

    @Autowired
    private ServiceAvailabilityRepository serviceAvailabilityRepository;

    @Autowired
    private TradingHoursRepository tradingHoursRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeServiceImpl.class);


    @Override
    public ResponseEntity<List<String>> getCityDetails(SearchBranchRequestDTO searchBranchRequestDTO) {
        if(StringUtils.isNullOrEmpty(searchBranchRequestDTO.getCity())) {
            return ResponseEntity.ok(employeeRepository.findByCity());
        } else {
            return ResponseEntity.ok(employeeRepository.findByCityStartsWith(searchBranchRequestDTO.getCity()));
        }
    }


    @Override
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployeeDetails(SearchBranchRequestDTO searchBranchRequestDTO) {
        List<EmployeeResponseDTO> employeeResponseDTOList = new ArrayList<>();
        List<EmployeeEntity> employeeEntityList = new ArrayList<>();
        if (searchBranchRequestDTO.getBranchId() != null) {
            employeeEntityList = employeeRepository.findByCityAndBranchIdAndIsActive(searchBranchRequestDTO.getCity(), findBranchById(searchBranchRequestDTO.getBranchId()), true);
        } else if (searchBranchRequestDTO.getEmployeeId() != null) {
            employeeEntityList = employeeRepository.findByCityAndEmployeeIdAndIsActive(searchBranchRequestDTO.getCity(), searchBranchRequestDTO.getEmployeeId(), true);
        } else if (searchBranchRequestDTO.getServiceId() != null) {
            employeeEntityList = employeeRepository.findByCityAndServiceIdAndIsActive(searchBranchRequestDTO.getCity(), findServiceById(searchBranchRequestDTO.getServiceId()), true);
        } else if (searchBranchRequestDTO.getSubServiceId() != null) {
            employeeEntityList = employeeRepository.findByCityAndSubServicesAndIsActive(searchBranchRequestDTO.getCity(), findSubServiceById(searchBranchRequestDTO.getSubServiceId()), true);
        } else {
            List<BranchEntity> branchEntityList = branchRepository.findByIsActiveAndCityAndServiceCategoryId(true,searchBranchRequestDTO.getCity(),findByServiceCategoryId(3L));
            if(!branchEntityList.isEmpty()) {
                employeeEntityList = employeeRepository.findByIsActiveAndBranchIdIn(true, branchEntityList);
            }
        }if (!employeeEntityList.isEmpty()) {
            employeeEntityList.forEach(employee -> employeeResponseDTOList.add(new EmployeeResponseDTO(employee)));
        }
        return ResponseEntity.ok(employeeResponseDTOList);
    }

    private BranchEntity findBranchById(Long branchId) {
        Optional<BranchEntity> branchEntityOptional = branchRepository.findById(branchId);
        if (!branchEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.BRANCH_NOT_FOUND);
        }
        return branchEntityOptional.get();
    }

    private ServiceCategoryEntity findByServiceCategoryId(Long serviceCategoryId) {
        Optional<ServiceCategoryEntity> serviceCategoryEntityOptional = serviceCategoryRepository.findById(serviceCategoryId);
        if (!serviceCategoryEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.SERVICE_CATEGORY_NOT_FOUND);
        }
        return serviceCategoryEntityOptional.get();
    }

    private EmployeeEntity findEmployeeById(Long employeeId) {
        Optional<EmployeeEntity> employeeRepositoryOptional = employeeRepository.findById(employeeId);
        if (!employeeRepositoryOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.EMPLOYEE_NOT_FOUND);
        }
        return employeeRepositoryOptional.get();
    }

    private ServiceEntity findServiceById(Long serviceId) {
        Optional<ServiceEntity> serviceEntityOptional = serviceRepository.findById(serviceId);
        if (!serviceEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.SERVICE_NOT_FOUND);
        }
        return serviceEntityOptional.get();
    }

    private SubServiceEntity findSubServiceById(Long subServiceId) {
        Optional<SubServiceEntity> subServiceEntityOptional = subServiceRepository.findById(subServiceId);
        if (!subServiceEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.SUB_SERVICE_NOT_FOUND);
        }
        return subServiceEntityOptional.get();
    }

    @Override
    public ResponseEntity<EmployeeResponseDTO> fetchEmployeeById(Long employeeId) {
        return ResponseEntity.ok(new EmployeeResponseDTO(findEmployeeById(employeeId)));
    }

    @Override
    public ResponseEntity<List<TradingHoursResponseDTO>> getServiceAvailabilityByEmployeeId(Long employeeId) {
        EmployeeEntity employeeEntity = findEmployeeById(employeeId);
        try {
            List<TradingHoursResponseDTO> tradingHoursResponseDTOList = new ArrayList<>();
            List<TradingHoursEntity> tradingHoursEntityList = tradingHoursRepository.findByEmployeeId(employeeEntity);
            if (!tradingHoursEntityList.isEmpty()) {
                tradingHoursEntityList.forEach(tradingHoursEntity -> tradingHoursResponseDTOList.add(new TradingHoursResponseDTO(tradingHoursEntity)));
            }
            return ResponseEntity.ok(tradingHoursResponseDTOList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }    }

}
