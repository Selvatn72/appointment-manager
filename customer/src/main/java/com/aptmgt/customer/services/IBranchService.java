package com.aptmgt.customer.services;

import com.aptmgt.customer.dto.OverAllSearchResponseDTO;
import com.aptmgt.customer.dto.SearchBranchRequestDTO;
import com.aptmgt.customer.dto.SearchResponseDTO;
import com.aptmgt.customer.dto.TradingHoursResponseDTO;
import com.aptmgt.commons.dto.BranchFilesResponseDTO;
import com.aptmgt.commons.dto.BranchResponseDTO;
import com.aptmgt.customer.dto.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IBranchService {

    ResponseEntity<List<String>> getCityDetails(SearchBranchRequestDTO searchBranchRequestDTO);

    ResponseEntity<List<BranchResponseDTO>> getBranchNames(SearchBranchRequestDTO searchBranchRequestDTO);

    ResponseEntity<SearchResponseDTO> getSearchDetails(SearchBranchRequestDTO searchBranchRequestDTO);

    ResponseEntity<Object> getBranchDetails(SearchBranchRequestDTO searchBranchRequestDTO);

    ResponseEntity<BranchResponseDTO> fetchByBranchId(Long branchId);

    ResponseEntity<List<TradingHoursResponseDTO>> getServiceAvailabilityByEmployeeId(Long BranchId);

    ResponseEntity<OverAllSearchResponseDTO> getOverAllSearchDetails(SearchBranchRequestDTO searchBranchRequestDTO);

    ResponseEntity<List<Object>> getEmployeeAndBranchImage(String city);

    ResponseEntity<List<BranchFilesResponseDTO>> getBranchFiles(SearchBranchRequestDTO searchBranchRequestDTO);

    ResponseEntity<List<String>> getNearbyCity(SearchBranchRequestDTO searchBranchRequestDTO);
}
