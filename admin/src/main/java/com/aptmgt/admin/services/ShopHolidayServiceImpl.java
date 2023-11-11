package com.aptmgt.admin.services;

import com.aptmgt.admin.dto.ShopHolidayRequestDTO;
import com.aptmgt.admin.dto.ShopHolidayResponseDTO;
import com.aptmgt.commons.dto.MessageDTO;
import com.aptmgt.commons.exceptions.InternalServerErrorException;
import com.aptmgt.commons.exceptions.RecordNotFoundException;
import com.aptmgt.commons.model.BranchEntity;
import com.aptmgt.commons.model.EmployeeEntity;
import com.aptmgt.commons.model.ServiceAvailabilityEntity;
import com.aptmgt.commons.model.ShopHolidayEntity;
import com.aptmgt.commons.repository.BranchRepository;
import com.aptmgt.commons.repository.EmployeeRepository;
import com.aptmgt.commons.repository.ServiceAvailabilityRepository;
import com.aptmgt.commons.repository.ShopHolidayRepository;
import com.aptmgt.commons.utils.ResponseMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class ShopHolidayServiceImpl implements IShopHolidayService {

    @Autowired
    private ShopHolidayRepository shopHolidayRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ServiceAvailabilityRepository serviceAvailabilityRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ShopHolidayServiceImpl.class);

    @Override
    public ResponseEntity<MessageDTO> saveShopHoliday(ShopHolidayRequestDTO shopHoliday) {
        ShopHolidayEntity shopHolidayEntity = new ShopHolidayEntity();
        BranchEntity branchEntity = findBranchById(shopHoliday.getBranchId());
        shopHolidayEntity.setBranchId(branchEntity);
        shopHolidayEntity.setEmployeeId(shopHoliday.getEmployeeId() != null ? findEmployeeById(shopHoliday.getEmployeeId()) : null);
        try {
            shopHolidayEntity.setShopHoliday(shopHoliday.getShopHoliday());
            shopHolidayEntity.setDescription(shopHoliday.getDescription());
            shopHolidayEntity.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            shopHolidayEntity.setCreatedDate(new Timestamp(new Date().getTime()));
            shopHolidayRepository.save(shopHolidayEntity);

            // Update Service Availability of the branch
            updateServiceAvailability(branchEntity, shopHoliday.getShopHoliday());

            return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.SHOP_HOLIDAY_CREATED_MSG));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new InternalServerErrorException();
        }
    }

    private void updateServiceAvailability(BranchEntity branchEntity, String shopHoliday) {
        List<ServiceAvailabilityEntity> serviceAvailabilityEntityList = serviceAvailabilityRepository.findByBranchIdAndSlotDayIn(branchEntity, Arrays.asList(shopHoliday));
        if(!serviceAvailabilityEntityList.isEmpty()) {
            serviceAvailabilityRepository.deleteAll(serviceAvailabilityEntityList);
        }
    }

    private EmployeeEntity findEmployeeById(Long employeeId) {
        Optional<EmployeeEntity> employeeEntityOptional = employeeRepository.findById(employeeId);
        if (!employeeEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.EMPLOYEE_NOT_FOUND);
        }
        return employeeEntityOptional.get();
    }

    @Override
    public ResponseEntity<ShopHolidayResponseDTO> fetchByShopHolidayId(Long id) {
        return ResponseEntity.ok(new ShopHolidayResponseDTO(findShopHolidayById(id)));
    }

    private ShopHolidayEntity findShopHolidayById(Long id) {
        Optional<ShopHolidayEntity> shopHolidayEntityOptional = shopHolidayRepository.findById(id);
        if (!shopHolidayEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.SHOP_HOLIDAY_NOT_FOUND);
        }
        return shopHolidayEntityOptional.get();
    }

    @Override
    public ResponseEntity<MessageDTO> updateShopHoliday(Long id, ShopHolidayRequestDTO shopHoliday) {
        ShopHolidayEntity shopHolidayEntity = findShopHolidayById(id);
        shopHolidayEntity.setBranchId(findBranchById(shopHoliday.getBranchId()));
        shopHolidayEntity.setEmployeeId(shopHoliday.getEmployeeId() != null ? findEmployeeById(shopHoliday.getEmployeeId()) : null);
        try {
            shopHolidayEntity.setShopHoliday(shopHoliday.getShopHoliday());
            shopHolidayEntity.setDescription(shopHoliday.getDescription());
            shopHolidayEntity.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            shopHolidayEntity.setUpdatedDate(new Timestamp(new Date().getTime()));
            shopHolidayRepository.save(shopHolidayEntity);
            return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.SHOP_HOLIDAY_UPDATED_MSG));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new InternalServerErrorException();
        }
    }

    public BranchEntity findBranchById(Long branchId) {
        Optional<BranchEntity> branchEntityOptional = branchRepository.findById(branchId);
        if (!branchEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.BRANCH_NOT_FOUND);
        }
        return branchEntityOptional.get();
    }

    @Override
    public ResponseEntity<MessageDTO> deleteShopHoliday(Long id) {
        ShopHolidayEntity shopHolidayEntity = findShopHolidayById(id);
        try {
            shopHolidayEntity.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            shopHolidayEntity.setUpdatedDate(new Timestamp(new Date().getTime()));
            shopHolidayRepository.save(shopHolidayEntity);

            shopHolidayRepository.deleteById(id);
            return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.SHOP_HOLIDAY_DELETED_MSG));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new InternalServerErrorException();
        }
    }

    @Override
    public ResponseEntity<List<ShopHolidayResponseDTO>> fetchShopHolidaysByEmployeeId(Long employeeId) {
        EmployeeEntity employeeEntity = findEmployeeById(employeeId);
        try {
            List<ShopHolidayResponseDTO> shopHolidayResponseDTOList = new ArrayList<>();
            List<ShopHolidayEntity> shopHolidayEntityList = shopHolidayRepository.findByEmployeeId(employeeEntity);
            if (!shopHolidayEntityList.isEmpty()) {
                shopHolidayEntityList.forEach(shopHoliday -> shopHolidayResponseDTOList.add(new ShopHolidayResponseDTO(shopHoliday)));
            }
            return ResponseEntity.ok(shopHolidayResponseDTOList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new InternalServerErrorException();
        }
    }

    @Override
    public ResponseEntity<List<ShopHolidayResponseDTO>> fetchShopHolidaysByBranchId(Long branchId) {
        BranchEntity branchEntity = findBranchById(branchId);
        try {
            List<ShopHolidayResponseDTO> shopHolidayResponseDTOList = new ArrayList<>();
            List<ShopHolidayEntity> shopHolidayEntityList = shopHolidayRepository.findByBranchId(branchEntity);
            if (!shopHolidayEntityList.isEmpty()) {
                shopHolidayEntityList.forEach(shopHoliday -> shopHolidayResponseDTOList.add(new ShopHolidayResponseDTO(shopHoliday)));
            }
            return ResponseEntity.ok(shopHolidayResponseDTOList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new InternalServerErrorException();
        }
    }
}
