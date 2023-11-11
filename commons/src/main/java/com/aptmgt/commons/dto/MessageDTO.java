package com.aptmgt.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {

    private Boolean status;

    private String message;

    private Boolean isNewUser;

    public MessageDTO(Boolean status, String message) {
        this.status = status;
        this.message = message;
    }
}
