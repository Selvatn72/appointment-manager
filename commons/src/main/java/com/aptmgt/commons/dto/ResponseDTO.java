package com.aptmgt.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO {

    private String message;

    private Boolean status;

    private Object data;

    private Long totalCount;

}
