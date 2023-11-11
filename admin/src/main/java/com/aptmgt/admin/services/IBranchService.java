package com.aptmgt.admin.services;

import com.aptmgt.admin.dto.BranchRequestDTO;
import com.aptmgt.admin.dto.BranchServicesDTO;
import com.aptmgt.commons.dto.BranchResponseDTO;
import com.aptmgt.commons.dto.MessageDTO;
import com.aptmgt.commons.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface IBranchService {

    ResponseEntity<ResponseDTO> saveBranch(BranchRequestDTO branchDTO, MultipartFile file, MultipartFile[] files) throws URISyntaxException, IOException;

    ResponseEntity<BranchResponseDTO> fetchByBranchId(Long branchId);

    ResponseEntity<MessageDTO> updateBranch(Long branchId, BranchRequestDTO branchDTO, MultipartFile file, MultipartFile[] files) throws URISyntaxException, IOException;

    ResponseEntity<MessageDTO> deleteBranch(Long branchId);

    ResponseEntity<List<BranchResponseDTO>> fetchBranchByAdminId(Long adminId);

    ResponseEntity<MessageDTO> setBranchServices(BranchServicesDTO request);
}
