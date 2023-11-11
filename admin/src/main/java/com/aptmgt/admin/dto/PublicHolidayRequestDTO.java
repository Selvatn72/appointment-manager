package com.aptmgt.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicHolidayRequestDTO {

   @NotNull(message = "Public holiday field should be not null")
   @JsonFormat(pattern = "yyyy-MM-dd")
   private Date publicHoliday;

   @NotBlank(message = "Description field should be not null")
   private String description;

   private Long employeeId;

   @NotNull(message = "Branch id field should be not null")
   private Long branchId;

}
