package com.aptmgt.customer.services;

import com.aptmgt.customer.dto.ServiceCategoryResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IServiceCategoryService {

    ResponseEntity<List<ServiceCategoryResponseDTO>> getBranchCategory();

}
