package com.aptmgt.customer.controllers;

import com.aptmgt.customer.dto.OverAllSearchResponseDTO;
import com.aptmgt.customer.dto.SearchBranchRequestDTO;
import com.aptmgt.customer.dto.SearchResponseDTO;
import com.aptmgt.customer.dto.TradingHoursResponseDTO;
import com.aptmgt.commons.dto.BranchFilesResponseDTO;
import com.aptmgt.commons.dto.BranchResponseDTO;
import com.aptmgt.customer.dto.*;
import com.aptmgt.customer.services.IBranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/branch")
public class BranchController {

    @Autowired
    private IBranchService iBranchService;

    @PostMapping("/get_city")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<String>> getCityDetails(@Valid @RequestBody SearchBranchRequestDTO searchBranchRequestDTO) {
        return iBranchService.getCityDetails(searchBranchRequestDTO);
    }

    @PostMapping("/get_branch")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<Object> getBranchDetails(@Valid @RequestBody SearchBranchRequestDTO searchBranchRequestDTO) {
        return iBranchService.getBranchDetails(searchBranchRequestDTO);
    }


    @PostMapping("/get_search")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<SearchResponseDTO> getSearchDetails(@RequestBody SearchBranchRequestDTO searchBranchRequestDTO) {
        return iBranchService.getSearchDetails(searchBranchRequestDTO);
    }

    @PostMapping("/get_overall_search")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<OverAllSearchResponseDTO> getOverAllSearchDetails(@RequestBody SearchBranchRequestDTO searchBranchRequestDTO) {
        return iBranchService.getOverAllSearchDetails(searchBranchRequestDTO);
    }

    @GetMapping(params = "city")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<Object>> getEmployeeAndBranchImage(@RequestParam("city") String city) {
        return iBranchService.getEmployeeAndBranchImage(city);
    }

    @GetMapping(params = "id")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<BranchResponseDTO> fetchBranchById(@RequestParam("id") Long branchId) {
        return iBranchService.fetchByBranchId(branchId);
    }

    @GetMapping(params = "branchId")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<TradingHoursResponseDTO>> getServiceAvailabilityByEmployeeId(@RequestParam("branchId") Long branchId) {
        return iBranchService.getServiceAvailabilityByEmployeeId(branchId);
    }

    @PostMapping("/branch_name")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<BranchResponseDTO>> getBranchNames(@RequestBody SearchBranchRequestDTO searchBranchRequestDTO) {
        return iBranchService.getBranchNames(searchBranchRequestDTO);
    }

    @PostMapping("/branch_files")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<BranchFilesResponseDTO>> getBranchFiles(@RequestBody SearchBranchRequestDTO searchBranchRequestDTO) {
        return iBranchService.getBranchFiles(searchBranchRequestDTO);
    }

    @PostMapping("/nearby_city")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<String>> getNearbyCity(@Valid @RequestBody SearchBranchRequestDTO searchBranchRequestDTO) {
        return iBranchService.getNearbyCity(searchBranchRequestDTO);
    }
}
