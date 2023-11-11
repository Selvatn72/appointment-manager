package com.aptmgt.customer.controllers;

import com.aptmgt.commons.model.CustomerEntity;
import com.aptmgt.commons.repository.CustomerRepository;
import com.aptmgt.commons.dto.CustomerResDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/customer")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<CustomerResDTO> getCustomerById(@RequestParam("id") Long customerId) {
        CustomerEntity customer = customerRepository.findById(customerId).get();
        return ResponseEntity.ok(new CustomerResDTO(customer));
    }
}
