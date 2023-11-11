package com.aptmgt.admin.controllers;

import com.aptmgt.admin.dto.BranchRequestDTO;
import com.aptmgt.admin.dto.BranchServicesDTO;
import com.aptmgt.admin.services.BranchFilesService;
import com.aptmgt.admin.services.IBranchService;
import com.aptmgt.commons.dto.BranchResponseDTO;
import com.aptmgt.commons.dto.MessageDTO;
import com.aptmgt.commons.dto.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/branch")
public class BranchController {

    @Autowired
    private IBranchService iBranchService;

    @Autowired
    private BranchFilesService branchFilesService;

    @PostMapping()
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseDTO> saveBranch(@RequestParam(value = "request") String request, @RequestParam(value = "image", required = false) MultipartFile file, @RequestParam(value = "files", required = false) MultipartFile[] files) throws URISyntaxException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        BranchRequestDTO branchRequestDTO = objectMapper.readValue(request, BranchRequestDTO.class);
        return iBranchService.saveBranch(branchRequestDTO, file, files);
    }

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<BranchResponseDTO> fetchBranchById(@RequestParam("id") Long branchId) {
        return iBranchService.fetchByBranchId(branchId);
    }

    @PutMapping("/{branchId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<MessageDTO> updateBranch(@PathVariable Long branchId, @RequestParam(value = "request") String request, @RequestParam(value = "image", required = false) MultipartFile file, @RequestParam(value = "files", required = false) MultipartFile[] files) throws URISyntaxException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        BranchRequestDTO branchRequestDTO = objectMapper.readValue(request, BranchRequestDTO.class);
        return iBranchService.updateBranch(branchId, branchRequestDTO, file, files);
    }

    @DeleteMapping()
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<MessageDTO> deleteBranch(@RequestParam("id") Long branchId) {
        return iBranchService.deleteBranch(branchId);
    }

    @GetMapping("/{adminId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<BranchResponseDTO>> fetchBranchByAdminId(@PathVariable("adminId") Long adminId) {
        return iBranchService.fetchBranchByAdminId(adminId);
    }

    @PostMapping("/services")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<MessageDTO> setServices(@Valid @RequestBody BranchServicesDTO request) {
        return iBranchService.setBranchServices(request);
    }

    @DeleteMapping("/branch_files")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<MessageDTO> deleteBranchFileById(@RequestParam("id") Long branchFileId) {
        return branchFilesService.deleteBranchFileById(branchFileId);
    }
}