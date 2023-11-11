package com.aptmgt.admin.controllers;

import com.aptmgt.admin.dto.ShopHolidayResponseDTO;
import com.aptmgt.admin.dto.ShopHolidayRequestDTO;
import com.aptmgt.admin.services.IShopHolidayService;
import com.aptmgt.commons.dto.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping("/shop_holiday")
public class ShopHolidayController {

    @Autowired
    private IShopHolidayService iShopHolidayService;

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<MessageDTO> saveShopHoliday(@Valid @RequestBody ShopHolidayRequestDTO shopHoliday) {
        return iShopHolidayService.saveShopHoliday(shopHoliday);
    }

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<ShopHolidayResponseDTO> fetchByShopHolidayId(@RequestParam("id") Long id) {
        return iShopHolidayService.fetchByShopHolidayId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<MessageDTO> updateShopHoliday(@PathVariable("id") Long id, @Valid @RequestBody ShopHolidayRequestDTO shopHoliday) {
        return iShopHolidayService.updateShopHoliday(id, shopHoliday);
    }

    @DeleteMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<MessageDTO> deleteShopHoliday(@RequestParam("id") Long id) {
        return iShopHolidayService.deleteShopHoliday(id);
    }

    @GetMapping("/branch")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<List<ShopHolidayResponseDTO>> fetchShopHolidaysByBranchId(@RequestParam("id") Long branchId) {
        return iShopHolidayService.fetchShopHolidaysByBranchId(branchId);
    }

    @GetMapping("/employee")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<List<ShopHolidayResponseDTO>> fetchShopHolidaysByEmployeeId(@RequestParam("employeeId") Long employeeId) {
        return iShopHolidayService.fetchShopHolidaysByEmployeeId(employeeId);
    }
}
