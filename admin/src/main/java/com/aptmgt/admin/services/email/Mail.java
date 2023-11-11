package com.aptmgt.admin.services.email;

import com.aptmgt.commons.model.BranchFilesEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Mail {

    private String[] to;
    private String cc;
    private String subject;
    private String content;
    private Map<String, String> model;
    private byte[] file;
    private String fileName;
    private List<BranchFilesEntity> files;
}