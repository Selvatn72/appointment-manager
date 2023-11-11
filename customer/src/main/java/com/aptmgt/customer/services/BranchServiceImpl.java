package com.aptmgt.customer.services;

import com.aptmgt.commons.dto.*;
import com.aptmgt.commons.model.*;
import com.aptmgt.commons.repository.*;
import com.aptmgt.customer.dto.OverAllSearchResponseDTO;
import com.aptmgt.customer.dto.SearchBranchRequestDTO;
import com.aptmgt.customer.dto.SearchResponseDTO;
import com.aptmgt.customer.dto.TradingHoursResponseDTO;
import com.aptmgt.commons.exceptions.InternalServerErrorException;
import com.aptmgt.commons.exceptions.RecordNotFoundException;
import com.aptmgt.commons.utils.ResponseMessages;
import com.aptmgt.customer.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BranchServiceImpl implements IBranchService {

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private TradingHoursRepository tradingHoursRepository;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private SubServiceRepository subServiceRepository;

    @Autowired
    private BranchFilesRepository branchFilesRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(BranchServiceImpl.class);
    @Override
    public ResponseEntity<List<String>> getCityDetails(SearchBranchRequestDTO searchBranchRequestDTO) {
        return ResponseEntity.ok(branchRepository.findByCityStartsWith(searchBranchRequestDTO.getCity()));
    }

    @Override
    public ResponseEntity<List<BranchResponseDTO>> getBranchNames(SearchBranchRequestDTO searchBranchRequestDTO) {
        try {
            List<BranchResponseDTO> branchResponseDTOList = new ArrayList<>();
            List<BranchEntity> branchEntityList = branchRepository.findByIsActiveAndCityAndNameIgnoreCaseContaining(true,searchBranchRequestDTO.getCity(),searchBranchRequestDTO.getBranchName());
            if (!branchEntityList.isEmpty()) {
                branchEntityList.forEach(branchEntity -> branchResponseDTOList.add(new BranchResponseDTO(branchEntity,null)));
            }
            return ResponseEntity.ok(branchResponseDTOList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }


    @Override
    public ResponseEntity<SearchResponseDTO> getSearchDetails(SearchBranchRequestDTO searchBranchRequestDTO) {
        if (searchBranchRequestDTO.getCategoryId() != 3) {
            return ResponseEntity.ok(new SearchResponseDTO(getBranchDetails(searchBranchRequestDTO.getCity(),searchBranchRequestDTO.getCategoryId(),searchBranchRequestDTO.getSearchName()),null, getServiceDetails(searchBranchRequestDTO.getCategoryId(),searchBranchRequestDTO.getSearchName()),null));
        } else {
            return ResponseEntity.ok(new SearchResponseDTO(getBranchDetails(searchBranchRequestDTO.getCity(),searchBranchRequestDTO.getCategoryId(),searchBranchRequestDTO.getSearchName()), getEmployeeDetails(searchBranchRequestDTO.getCity(),searchBranchRequestDTO.getSearchName()), getServiceDetails(searchBranchRequestDTO.getCategoryId(),searchBranchRequestDTO.getSearchName()), getSubServiceDetails(searchBranchRequestDTO.getSearchName())));
        }
    }

    @Override
    public ResponseEntity<OverAllSearchResponseDTO> getOverAllSearchDetails(SearchBranchRequestDTO searchBranchRequestDTO) {
        return ResponseEntity.ok(new OverAllSearchResponseDTO(getOverAllBranchDetails(searchBranchRequestDTO.getCity(),searchBranchRequestDTO.getSearchName()), getOverAllEmployeeDetails(searchBranchRequestDTO.getCity(), searchBranchRequestDTO.getSearchName())));
    }

    @Override
    public ResponseEntity<List<Object>> getEmployeeAndBranchImage(String city) {
        List<Object> branchResponseDTOList = new ArrayList<>();
        try {
            List<BranchEntity> branchEntityList = branchRepository.findByIsActiveAndCityIgnoreCaseContainingAndServiceCategoryIdNot(true,city,findByServiceCategoryId(3L));
            List<EmployeeEntity> employeeEntityList = employeeRepository.findByIsActiveAndCityIgnoreCaseContainingAndServiceIdNotNull(true,city);
            if (!branchEntityList.isEmpty()) {
                branchEntityList.forEach(branch -> branchResponseDTOList.add(new BranchResponseDTO(branch, null)));
            }
            if (!employeeEntityList.isEmpty()) {
                employeeEntityList.forEach(employee -> branchResponseDTOList.add(new EmployeeResponseDTO(employee)));
            }
            return ResponseEntity.ok(branchResponseDTOList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    @Override
    public ResponseEntity<List<String>> getNearbyCity(SearchBranchRequestDTO searchBranchRequestDTO) {
        return ResponseEntity.ok(branchRepository.findByLatitudeStartsWithAndLongitudeStartsWith(searchBranchRequestDTO.getLatitude(), searchBranchRequestDTO.getLongitude()));
    }

    @Override
    public ResponseEntity<List<BranchFilesResponseDTO>> getBranchFiles(SearchBranchRequestDTO searchBranchRequestDTO) {
       List<BranchFilesResponseDTO> branchFilesResponseDTOList = new ArrayList<>();
        List<BranchEntity> branchEntity;
       if(searchBranchRequestDTO.getBranchId() != null) {
           branchEntity = branchRepository.findByIsActiveAndBranchId(true, searchBranchRequestDTO.getBranchId());
       } else {
           branchEntity = branchRepository.findByIsActiveAndCityIgnoreCaseContaining(true, searchBranchRequestDTO.getCity());
       }
       if(!branchEntity.isEmpty()){
           List<BranchFilesEntity> branchFilesEntityList = branchFilesRepository.findByIsPhotoVerifiedAndBranchIdIn(true,branchEntity);
           if(!branchFilesEntityList.isEmpty()){
               branchFilesEntityList.forEach(branchFilesEntity -> branchFilesResponseDTOList.add(new BranchFilesResponseDTO(branchFilesEntity)));
           }
       }
      return ResponseEntity.ok(branchFilesResponseDTOList);
    }

    @Override
    public ResponseEntity<Object> getBranchDetails(SearchBranchRequestDTO searchBranchRequestDTO) {
        if(searchBranchRequestDTO.getBranchId() != null){
            return ResponseEntity.ok(getBranchDetailsByBranchIdAndCity(searchBranchRequestDTO.getBranchId(),searchBranchRequestDTO.getCity()));
        }
        if (searchBranchRequestDTO.getCategoryId() != null || searchBranchRequestDTO.getServiceId() != null) {
            return ResponseEntity.ok(getBranchDetailsByCityAndServiceCategoryAndService(searchBranchRequestDTO.getCategoryId(), searchBranchRequestDTO.getServiceId(), searchBranchRequestDTO.getCity()));
        }else {
            return ResponseEntity.ok(getBranchDetailsByCity(searchBranchRequestDTO.getCity()));
        }
    }

    public List<BranchResponseDTO> getOverAllBranchDetails(String city, String name) {
        List<BranchResponseDTO> branchAndServiceList = new ArrayList<>();
        List<BranchResponseDTO> branchResponseDTOList = new ArrayList<>();
        try {
            List<BranchEntity> branchEntityList = branchRepository.findByIsActiveAndCityAndNameIgnoreCaseContaining(true,city,name);
            List<ServiceEntity> serviceEntityList = serviceRepository.findByServiceNameIgnoreCaseContaining(name);
            if (!branchEntityList.isEmpty()) {
                branchEntityList.forEach(branch -> branchAndServiceList.add(new BranchResponseDTO(branch, null)));
            }
            if (!serviceEntityList.isEmpty()) {
                serviceEntityList.forEach(branch -> branchAndServiceList.addAll(BranchResponseDTO.getBranch(findBranchByServiceId(city, branch.getServiceId()))));
            }
            branchResponseDTOList.addAll(branchAndServiceList.stream().distinct().collect(Collectors.toList()));
            return branchResponseDTOList;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    public List<EmployeeResponseDTO> getOverAllEmployeeDetails(String city, String name) {
        List<EmployeeResponseDTO> employeeList = new ArrayList<>();
        List<EmployeeResponseDTO> employeeResponseDTOList = new ArrayList<>();
        try {
            List<BranchEntity> branchEntityList = branchRepository.findByIsActiveAndCityAndServiceCategoryId(true,city,findByServiceCategoryId(3L));
            List<EmployeeEntity> employeeEntityList = employeeRepository.findByIsActiveAndCityAndBranchIdInAndNameIgnoreCaseContaining(true,city,branchEntityList,name);
            List<ServiceEntity> serviceEntityList = serviceRepository.findByAndServiceCategoryIdAndServiceNameIgnoreCaseContaining(findByServiceCategoryId(3L),name);
            List<SubServiceEntity> subServiceEntityList = subServiceRepository.findByNameIgnoreCaseContaining(name);
//            if (!branchEntityList.isEmpty()) {
//                branchEntityList.forEach(branch -> employeeList.addAll(EmployeeResponseDTO.getEmployee(findEmployeeByBranchId(city,branch.getBranchId()))));
//            }
            if (!employeeEntityList.isEmpty()) {
                employeeEntityList.forEach(employee -> employeeList.add(new EmployeeResponseDTO(employee)));
            }
            if (!serviceEntityList.isEmpty()) {
                serviceEntityList.forEach(service -> employeeList.addAll(EmployeeResponseDTO.getEmployee(findEmployeeByServiceId(city,service.getServiceId()))));
            }
            if (!subServiceEntityList.isEmpty()) {
                subServiceEntityList.forEach(subService -> employeeList.addAll(EmployeeResponseDTO.getEmployee(findEmployeeBySubServiceId(city,subService.getSubServiceId()))));
            }
            employeeResponseDTOList.addAll(employeeList.stream().distinct().collect(Collectors.toList()));
            return employeeResponseDTOList;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }
    private List<BranchEntity> findBranchByServiceId(String city, Long serviceId) {
        List<BranchEntity> branchEntityList = branchRepository.findByIsActiveAndCityAndServices(true,city, findServiceById(serviceId));
        return branchEntityList;
    }

    private List<EmployeeEntity> findEmployeeByBranchId(String city ,Long branchId) {
        List<EmployeeEntity> employeeEntityList = employeeRepository.findByCityAndBranchIdAndIsActive(city,findBranchById(branchId), true);
        return employeeEntityList;
    }

    private List<EmployeeEntity> findEmployeeByServiceId(String city, Long serviceId) {
        List<EmployeeEntity> employeeEntityList = employeeRepository.findByCityAndServiceIdAndIsActive(city,findServiceById(serviceId),true);
        return employeeEntityList;
    }

    private List<EmployeeEntity> findEmployeeBySubServiceId(String city, Long serviceId) {
        List<EmployeeEntity> employeeEntityList = employeeRepository.findByCityAndSubServicesAndIsActive(city,findSubServiceById(serviceId),true);
        return employeeEntityList;
    }

    public SubServiceEntity findSubServiceById(Long subServiceId) {
        Optional<SubServiceEntity> subServiceEntityOptional = subServiceRepository.findById(subServiceId);
        return subServiceEntityOptional.get();
    }


    public ServiceEntity findServiceById(Long serviceId) {
        Optional<ServiceEntity> serviceEntityOptional = serviceRepository.findById(serviceId);
        if (!serviceEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.SERVICE_NOT_FOUND);
        }
        return serviceEntityOptional.get();
    }

    @Override
    public ResponseEntity<BranchResponseDTO> fetchByBranchId(Long branchId) {
        return ResponseEntity.ok(new BranchResponseDTO(findBranchById(branchId), null));
    }

    @Override
    public ResponseEntity<List<TradingHoursResponseDTO>> getServiceAvailabilityByEmployeeId(Long branchId) {
        BranchEntity branchEntity = findBranchById(branchId);
        try {
            List<TradingHoursResponseDTO> tradingHoursResponseDTOList = new ArrayList<>();
            List<TradingHoursEntity> tradingHoursEntityList = tradingHoursRepository.findByBranchId(branchEntity);
            if (!tradingHoursEntityList.isEmpty()) {
                tradingHoursEntityList.forEach(tradingHoursEntity -> tradingHoursResponseDTOList.add(new TradingHoursResponseDTO(tradingHoursEntity)));
            }
            return ResponseEntity.ok(tradingHoursResponseDTOList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }    }

    private BranchEntity findBranchById(Long branchId) {
        Optional<BranchEntity> branchEntityOptional = branchRepository.findById(branchId);
        if (!branchEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.BRANCH_NOT_FOUND);
        }
        return branchEntityOptional.get();
    }

    private List<BranchResponseDTO> getBranchDetailsByBranchIdAndCity(Long branchId,String name) {
        List<BranchResponseDTO> branchResponseDTOList = new ArrayList<>();
        List<BranchEntity> branchEntityList;
        try {
            branchEntityList = branchRepository.findByIsActiveAndBranchIdAndCityIgnoreCaseContaining(true, branchId, name);
            if (!branchEntityList.isEmpty()) {
                branchEntityList.forEach(branch -> branchResponseDTOList.add(new BranchResponseDTO(branch, null)));
            }
            return branchResponseDTOList;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }
    private List<BranchResponseDTO> getBranchDetailsByCityAndServiceCategoryAndService(Long categoryId,Long serviceId,String name) {
        List<BranchResponseDTO> branchResponseDTOList = new ArrayList<>();
        List<BranchEntity> branchEntityList;
        try {
            if(serviceId != null) {
                branchEntityList = branchRepository.findByIsActiveAndServiceCategoryIdAndServicesAndCityIgnoreCaseContaining(true, findByServiceCategoryId(categoryId), findServiceById(serviceId), name);
            } else {
                branchEntityList = branchRepository.findByIsActiveAndServiceCategoryIdAndCityIgnoreCaseContaining(true, findByServiceCategoryId(categoryId), name);
            }
            if (!branchEntityList.isEmpty()) {
                branchEntityList.forEach(branch -> branchResponseDTOList.add(new BranchResponseDTO(branch, null)));
            }
            return branchResponseDTOList;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }
    private List<BranchResponseDTO> getBranchDetails(String city,Long categoryId,String name) {
        List<BranchResponseDTO> branchResponseDTOList = new ArrayList<>();
        try {
            List<BranchEntity> branchEntityList = branchRepository.findByIsActiveAndCityAndServiceCategoryIdAndNameIgnoreCaseContaining(true,city,findByServiceCategoryId(categoryId),name);
            if (!branchEntityList.isEmpty()) {
                branchEntityList.forEach(branch -> branchResponseDTOList.add(new BranchResponseDTO(branch, null)));
            }
            return branchResponseDTOList;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }
    private ServiceCategoryEntity findByServiceCategoryId(Long serviceCategoryId) {
        Optional<ServiceCategoryEntity> serviceCategoryEntityOptional = serviceCategoryRepository.findById(serviceCategoryId);
        if (!serviceCategoryEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.SERVICE_CATEGORY_NOT_FOUND);
        }
        return serviceCategoryEntityOptional.get();
    }

    private List<BranchResponseDTO> getBranchDetailsByCity(String name) {
        List<BranchResponseDTO> branchResponseDTOList = new ArrayList<>();
        try {
            List<BranchEntity> branchEntityList = branchRepository.findByIsActiveAndCityOrderByBranchIdDesc(true,name);
            if (!branchEntityList.isEmpty()) {
                branchEntityList.forEach(branch -> branchResponseDTOList.add(new BranchResponseDTO(branch, null)));
            }
            return branchResponseDTOList;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private List<EmployeeResponseDTO> getEmployeeDetails(String city,String name) {
        List<EmployeeResponseDTO> employeeResponseDTOList = new ArrayList<>();
        List<BranchEntity> branchEntityList = branchRepository.findByIsActiveAndCityAndServiceCategoryId(true,city,findByServiceCategoryId(3L));
        try {
            if(!branchEntityList.isEmpty()) {
                List<EmployeeEntity> employeeEntityList = employeeRepository.findByIsActiveAndNameIgnoreCaseContainingAndBranchIdIn(true, name, branchEntityList);
                if (!employeeEntityList.isEmpty()) {
                    employeeEntityList.forEach(employee -> employeeResponseDTOList.add(new EmployeeResponseDTO(employee)));
                }
            }
            return employeeResponseDTOList;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private List<ServiceResponseDTO> getServiceDetails(Long categoryId, String name) {
        List<ServiceResponseDTO> serviceResponseDTOList = new ArrayList<>();
        try {
            List<ServiceEntity> serviceEntityList = serviceRepository.findByServiceCategoryIdAndServiceNameIgnoreCaseContaining(findByServiceCategoryId(categoryId), name);
            if (!serviceEntityList.isEmpty()) {
                serviceEntityList.forEach(service -> serviceResponseDTOList.add(new ServiceResponseDTO(service)));
            }
            return serviceResponseDTOList;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private List<SubServiceResponseDTO> getSubServiceDetails(String name) {
        List<SubServiceResponseDTO> subServiceResponseDTOList = new ArrayList<>();
        try {
            List<SubServiceEntity> subServiceEntityList = subServiceRepository.findByNameIgnoreCaseContaining(name);
            if (!subServiceEntityList.isEmpty()) {
                subServiceEntityList.forEach(subService -> subServiceResponseDTOList.add(new SubServiceResponseDTO(subService)));
            }
            return subServiceResponseDTOList;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }
}
