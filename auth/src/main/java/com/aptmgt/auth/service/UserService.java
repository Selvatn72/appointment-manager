package com.aptmgt.auth.service;

import com.aptmgt.auth.dto.*;
import com.aptmgt.auth.dto.*;
import com.aptmgt.commons.dto.EmployeeInfoReqDTO;
import com.aptmgt.commons.model.UserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface UserService {

    UserEntity getUserByUsername(String username);

    UserEntity saveUser(UserEntity user);

    ResponseEntity<Object> registerUser(String userType, AdminInfoRequestDTO adminReq, CustomerInfoReqDTO customerReq, EmployeeInfoReqDTO employeeInfoReqDTO, MultipartFile file);

    Optional<UserEntity> findAdminByEmail(String email);

    Optional<UserEntity> findCustomerByEmail(String email);

    ResponseEntity<Object> generateToken(String userType, UserCredentialsReqDTO loginDTO);

    ResponseEntity<MessageDTO> changePassword(String userType, ChangePasswordReqDTO changePasswordDTO);

    ResponseEntity<MessageDTO> forgotPassword(String userType, ForgotPasswordReqDTO forgotPasswordReqDTO);

    ResponseEntity<MessageDTO> verifyUser(String userType, ForgotPasswordReqDTO forgotPasswordReqDTO);

    ResponseEntity<MessageDTO> updateUser(String userType, UserDetailsUpdateDTO userDetailsUpdateDTO);

    ResponseEntity<UserDetailsResDTO> getUser(String userType, Long id);

    UserEntity createCustomer(CustomerInfoReqDTO customerInfoReqDTO);

    void checkForExistingUser(CustomerInfoReqDTO customerInfoReqDTO);
}
