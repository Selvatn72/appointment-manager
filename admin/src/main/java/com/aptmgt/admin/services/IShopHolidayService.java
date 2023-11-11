package com.aptmgt.admin.services;

import com.aptmgt.admin.dto.ShopHolidayRequestDTO;
import com.aptmgt.admin.dto.ShopHolidayResponseDTO;
import com.aptmgt.commons.dto.MessageDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IShopHolidayService {

    ResponseEntity<MessageDTO> saveShopHoliday(ShopHolidayRequestDTO shopHoliday);

    ResponseEntity<ShopHolidayResponseDTO> fetchByShopHolidayId(Long id);

    ResponseEntity<MessageDTO> updateShopHoliday(Long id, ShopHolidayRequestDTO shopHoliday);
    
    ResponseEntity<List<ShopHolidayResponseDTO>>  fetchShopHolidaysByBranchId(Long branchId);

    ResponseEntity<MessageDTO> deleteShopHoliday(Long id);

    ResponseEntity<List<ShopHolidayResponseDTO>> fetchShopHolidaysByEmployeeId(Long employeeId);
}
