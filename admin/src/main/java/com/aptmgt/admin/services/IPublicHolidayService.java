package com.aptmgt.admin.services;

import com.aptmgt.admin.dto.PublicHolidayRequestDTO;
import com.aptmgt.admin.dto.PublicHolidayResponseDTO;
import com.aptmgt.commons.dto.MessageDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface IPublicHolidayService {

    ResponseEntity<MessageDTO> savePublicHoliday(PublicHolidayRequestDTO publicHoliday);
    
    ResponseEntity<MessageDTO> updatePublicHoliday(Long id, PublicHolidayRequestDTO publicHoliday);

    ResponseEntity<List<PublicHolidayResponseDTO>> fetchPublicHolidaysByBranchId(Long branchId);

    ResponseEntity<PublicHolidayResponseDTO> fetchByPublicHolidayId(Long id);

    ResponseEntity<MessageDTO> deletePublicHoliday(Long id);

    void updateAppointments(PublicHolidayRequestDTO publicHoliday, Authentication authentication);

    ResponseEntity<List<PublicHolidayResponseDTO>> fetchPublicHolidaysByEmployeeId(Long employeeId);
}
