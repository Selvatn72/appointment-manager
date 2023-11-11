package com.aptmgt.admin.services;

import com.aptmgt.commons.dto.MessageDTO;
import com.aptmgt.commons.exceptions.InternalServerErrorException;
import com.aptmgt.commons.model.BranchEntity;
import com.aptmgt.commons.model.BranchFilesEntity;
import com.aptmgt.commons.repository.BranchFilesRepository;
import com.aptmgt.commons.utils.ResponseMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BranchFilesService {

    @Autowired
    private BranchFilesRepository branchFilesRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(BranchFilesService.class);

    public void saveBranchFiles(BranchEntity branchEntity, MultipartFile[] files) {
        try {
            List<BranchFilesEntity> branchFilesEntityList = new ArrayList<>();
            for (MultipartFile file : files) {
                BranchFilesEntity branchFilesEntity = new BranchFilesEntity();
                branchFilesEntity.setFileName(StringUtils.cleanPath(file.getOriginalFilename()));
                branchFilesEntity.setFileType(file.getContentType());
                try {
                    branchFilesEntity.setImage(file.getBytes());
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                branchFilesEntity.setBranchId(branchEntity);
                branchFilesEntity.setCreatedBy(branchEntity.getCreatedBy());
                branchFilesEntity.setCreatedDate(new Timestamp(new Date().getTime()));
                branchFilesEntityList.add(branchFilesEntity);
            }
            branchFilesRepository.saveAll(branchFilesEntityList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    public List<BranchFilesEntity> getByBranchId(BranchEntity branchEntity) {
        return branchFilesRepository.findByBranchId(branchEntity);
    }

    public ResponseEntity<MessageDTO> deleteBranchFileById(Long branchFileId) {
        try {
            branchFilesRepository.deleteById(branchFileId);
            return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.BRANCH_FILE_DELETED_MSG));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }
}
