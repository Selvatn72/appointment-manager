package com.aptmgt.auth.controller;

import com.aptmgt.auth.dto.*;
import com.aptmgt.auth.service.UserService;
import com.aptmgt.auth.dto.*;
import com.aptmgt.commons.dto.CustomerResDTO;
import com.aptmgt.commons.dto.EmployeeInfoReqDTO;
import com.aptmgt.commons.dto.ResponseDTO;
import com.aptmgt.commons.model.UserEntity;
import com.aptmgt.commons.utils.Constants;
import com.aptmgt.commons.utils.ResponseMessages;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/{role}/login")
    public ResponseEntity<Object> generateToken(@PathVariable("role") String userType, @Valid @RequestBody UserCredentialsReqDTO loginDTO) throws AuthenticationException {
        return userService.generateToken(userType, loginDTO);
    }

    @PostMapping(value = "/admin/register")
    public ResponseEntity<Object> registerAdmin(@Valid @RequestBody AdminInfoRequestDTO adminInfoRequestDTO) throws AuthenticationException {
        return userService.registerUser(Constants.ADMIN_LOWER_CASE, adminInfoRequestDTO, null, null, null);
    }

    @PostMapping(value = "/employee/register")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> registerEmployee(@RequestParam(value = "request") String request, @RequestParam(value = "image", required = false) MultipartFile file) throws AuthenticationException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        EmployeeInfoReqDTO employeeInfoReqDTO = objectMapper.readValue(request, EmployeeInfoReqDTO.class);
        return userService.registerUser(Constants.EMPLOYEE_LOWER_CASE, null, null, employeeInfoReqDTO, file);
    }

    @PostMapping(value = "/customer/register")
    public ResponseEntity<Object> registerCustomer(@Valid @RequestBody CustomerInfoReqDTO customerInfoReqDTO) throws AuthenticationException {
        return userService.registerUser(Constants.CUSTOMER_LOWER_CASE, null, customerInfoReqDTO, null, null);
    }

    @PostMapping(value = "/{role}/change_password")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CUSTOMER')")
    public ResponseEntity<MessageDTO> changePassword(@PathVariable("role") String userType, @Valid @RequestBody ChangePasswordReqDTO changePasswordDTO) throws AuthenticationException {
        return userService.changePassword(userType, changePasswordDTO);
    }

    @PostMapping("/{role}/forgot_password")
    public ResponseEntity<MessageDTO> forgotPassword(@PathVariable("role") String userType, @Valid @RequestBody ForgotPasswordReqDTO forgotPasswordReqDTO) {
        return userService.forgotPassword(userType, forgotPasswordReqDTO);
    }

    @PostMapping("/{role}/verify_user")
    public ResponseEntity<MessageDTO> verifyUser(@PathVariable("role") String userType, @Valid @RequestBody ForgotPasswordReqDTO forgotPasswordReqDTO) {
        return userService.verifyUser(userType, forgotPasswordReqDTO);
    }

    @GetMapping("/{role}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CUSTOMER')")
    public ResponseEntity<UserDetailsResDTO> getUser(@PathVariable("role") String userType, @RequestParam Long id) {
        return userService.getUser(userType, id);
    }

    @PutMapping("/{role}/update_user")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CUSTOMER')")
    public ResponseEntity<MessageDTO> updateUser(@PathVariable("role") String userType, @Valid @RequestBody UserDetailsUpdateDTO userDetailsUpdateDTO) {
        return userService.updateUser(userType, userDetailsUpdateDTO);
    }

    @PostMapping(value = "/create_customer")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseDTO> createCustomer(@Valid @RequestBody CustomerInfoReqDTO customerInfoReqDTO) {
        userService.checkForExistingUser(customerInfoReqDTO);
        UserEntity userEntity =  userService.createCustomer(customerInfoReqDTO);
        return ResponseEntity.ok(new ResponseDTO(ResponseMessages.CUSTOMER_CREATION_MSG, true, new CustomerResDTO(userEntity.getCustomerId()), null));
    }
}
