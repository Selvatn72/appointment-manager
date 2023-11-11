package com.aptmgt.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthAdminResDTO {

    private Long adminId;

    private String accessToken;

    private Long serviceCategoryId;
}
