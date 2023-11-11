package com.aptmgt.auth.service;

import com.aptmgt.auth.dto.*;
import com.aptmgt.auth.enums.UserTypes;
import com.aptmgt.auth.jwt.TokenProvider;
import com.aptmgt.auth.service.email.EmailService;
import com.aptmgt.auth.service.email.Mail;
import com.aptmgt.commons.model.*;
import com.aptmgt.commons.repository.*;
import com.aptmgt.auth.dto.*;
import com.aptmgt.commons.dto.EmployeeInfoReqDTO;
import com.aptmgt.commons.exceptions.BadRequestException;
import com.aptmgt.commons.exceptions.InternalServerErrorException;
import com.aptmgt.commons.exceptions.RecordNotFoundException;
import com.aptmgt.commons.utils.AppUtils;
import com.aptmgt.commons.utils.Constants;
import com.aptmgt.commons.utils.ResponseMessages;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider jwtTokenUtil;

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    @Autowired
    private UserVerificationRepo userVerificationRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private SubServiceRepository subServiceRepository;

    @Autowired
    private TradingHoursRepository tradingHoursRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public ResponseEntity<Object> registerUser(String userType, AdminInfoRequestDTO adminReq, CustomerInfoReqDTO customerReq, EmployeeInfoReqDTO employeeReq, MultipartFile file) {
        Optional<UserEntity> user = Optional.empty();
        if (userType.equals(UserTypes.ADMIN.toString())) {
            user = findAdminByEmail(adminReq.getEmail());
        } else if (userType.equals(UserTypes.CUSTOMER.toString())) {
            checkForExistingUser(customerReq);
        } else if (userType.equals(UserTypes.EMPLOYEE.toString())) {
            user = findEmployeeByEmail(employeeReq.getEmail());
        } else {
            throw new BadRequestException(ResponseMessages.INVALID_USER_TYPE);
        }

        if (user.isPresent()) {
            throw new BadRequestException(ResponseMessages.USER_ALREADY_EXISTS);
        }
        UserEntity registeredUser = new UserEntity();
        String password = "";
        try {
            if (userType.equals(UserTypes.ADMIN.toString())) {
                registeredUser = createAdmin(adminReq);
                password = adminReq.getPassword();
            } else if (userType.equals(UserTypes.CUSTOMER.toString())) {
                registeredUser = createCustomer(customerReq);
                password = customerReq.getPassword();
            } else if (userType.equals(UserTypes.EMPLOYEE.toString())) {
                return createEmployee(employeeReq, file);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
        return setResponse(userType, registeredUser, password);
    }

    @Override
    public void checkForExistingUser(CustomerInfoReqDTO customerReq) {
        if (customerReq.getPhone() != null) {
            Optional<UserEntity> userEntity = findCustomerByPhone(customerReq.getPhone());
            if (userEntity.isPresent()) {
                throw new BadRequestException(ResponseMessages.PHONE_ALREADY_EXISTS);
            } else if (customerReq.getEmail() != null) {
                userEntity = findCustomerByEmail(customerReq.getEmail());
                if (userEntity.isPresent()) {
                    throw new BadRequestException(ResponseMessages.USER_ALREADY_EXISTS);
                }
            }
        } else {
            throw new BadRequestException();
        }
    }

    private ResponseEntity<Object> createEmployee(EmployeeInfoReqDTO employeeReq, MultipartFile file) throws IOException {
        BranchEntity branchEntity = findBranchById(employeeReq.getBranchId());
        List<SubServiceEntity> subServiceEntities = new ArrayList<>();
        employeeReq.getSubServiceId().forEach(subServiceId -> subServiceEntities.add(findSubServiceById(subServiceId)));
        EmployeeEntity employeeEntity = employeeRepository.save(new EmployeeEntity(null,
                employeeReq.getName(),
                employeeReq.getEmail(),
                employeeReq.getPhone(),
                branchEntity.getCity(),
                true,
                employeeReq.getDegree(),
                employeeReq.getExperience(),
                file != null ? org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename()) : null,
                file != null ? file.getContentType() : null,
                file != null ? file.getBytes() : null,
                SecurityContextHolder.getContext().getAuthentication().getName(),
                new Timestamp(new Date().getTime()),
                null,
                null,
                findServiceById(employeeReq.getServiceId()),
                branchEntity,
                subServiceEntities.isEmpty() ? null : subServiceEntities
        ));

        updateTradingHours(employeeEntity);


        UserEntity user = new UserEntity();
        String password = AppUtils.generateRandomPassword(10);
        user.setEmployeeId(employeeEntity);
        user.setEmail(employeeEntity.getEmail());
        user.setIsActive(true);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setPhone(employeeEntity.getPhone());
        user.setRoles(Arrays.asList(findRoleByName(Constants.EMPLOYEE)));
        user.setUsername(Constants.EMPLOYEE +
                AppUtils.getRandomDigits() + "_" + employeeEntity.getName());

        if (employeeEntity.getEmail() != null) {
            // send password notification
            emailService.sendPasswordNotification(employeeEntity, password);
        }
        saveUser(user);
        return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.EMPLOYEE_CREATED_MSG));
    }

    private void updateTradingHours(EmployeeEntity employeeEntity) {
        try {
            tradingHoursRepository.save(new TradingHoursEntity(
                    null,
                    "09:00:00",
                    "18:00:00",
                    true,
                    false,
                    "Admin",
                    new Timestamp(new Date().getTime()),
                    null,
                    null,
                    employeeEntity,
                    employeeEntity.getBranchId()
            ));
            tradingHoursRepository.save(new TradingHoursEntity(
                    null,
                    "09:00:00",
                    "18:00:00",
                    false,
                    true,
                    "Admin",
                    new Timestamp(new Date().getTime()),
                    null,
                    null,
                    employeeEntity,
                    employeeEntity.getBranchId()
            ));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private SubServiceEntity findSubServiceById(Long subServiceId) {
        Optional<SubServiceEntity> subServiceEntityOptional = subServiceRepository.findById(subServiceId);
        if (!subServiceEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.SUB_SERVICE_NOT_FOUND);
        }
        return subServiceEntityOptional.get();
    }

    private ServiceEntity findServiceById(Long serviceId) {
        Optional<ServiceEntity> serviceEntityOptional = serviceRepository.findById(serviceId);
        if (!serviceEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.SERVICE_NOT_FOUND);
        }
        return serviceEntityOptional.get();
    }

    public BranchEntity findBranchById(Long branchId) {
        Optional<BranchEntity> branchEntityOptional = branchRepository.findById(branchId);
        if (!branchEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.BRANCH_NOT_FOUND);
        }
        return branchEntityOptional.get();
    }

    private Optional<UserEntity> findEmployeeByEmail(String email) {
        return userRepository.findByEmailAndIsActiveAndEmployeeIdIsNotNull(email, true);
    }

    @Override
    public Optional<UserEntity> findAdminByEmail(String email) {
        return userRepository.findByEmailAndIsActiveAndAdminIdIsNotNull(email, true);
    }

    @Override
    public Optional<UserEntity> findCustomerByEmail(String email) {
        return userRepository.findByEmailAndIsActiveAndCustomerIdIsNotNull(email, true);
    }

    @Override
    public ResponseEntity<Object> generateToken(String userType, UserCredentialsReqDTO loginDTO) {
        Optional<UserEntity> user;
        if (userType.equals(UserTypes.ADMIN.toString())) {
            user = findAdminByEmail(loginDTO.getEmail());
        } else if (userType.equals(UserTypes.CUSTOMER.toString())) {
            if (loginDTO.getMobileNumber() != null) {
                user = findCustomerByPhone(loginDTO.getMobileNumber());
            } else {
                user = findCustomerByEmail(loginDTO.getEmail());
            }
        } else if (userType.equals(UserTypes.EMPLOYEE.toString())) {
            user = findEmployeeByEmail(loginDTO.getEmail());
        } else {
            throw new BadRequestException(ResponseMessages.INVALID_USER_TYPE);
        }

        if (!user.isPresent()) {
            throw new BadRequestException(ResponseMessages.USER_NOT_EXISTS);
        }
        return setResponse(userType, user.get(), loginDTO.getPassword());
    }

    private Optional<UserEntity> findCustomerByPhone(String phone) {
        return userRepository.findByPhoneAndIsActiveAndCustomerIdIsNotNull(phone, true);
    }

    private ResponseEntity<Object> setResponse(String userType, UserEntity user, String password) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                password
        );
        final Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        try {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenUtil.generateToken(authentication);
            if (userType.equals(UserTypes.ADMIN.toString())) {
                return ResponseEntity.ok(new AuthAdminResDTO(user, token));
            } else if (userType.equals(UserTypes.CUSTOMER.toString())) {
                return ResponseEntity.ok(new AuthCustomerResDTO(user, token));
            } else {
                return ResponseEntity.ok(new AuthEmployeeResDTO(user, token));
            }
        } catch (
                Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }

    }

    private AdminEntity findAdminById(Long adminId) {
        Optional<AdminEntity> adminOptional = adminRepository.findById(adminId);
        if (!adminOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.ADMIN_NOT_FOUND);
        }
        return adminOptional.get();
    }

    private CustomerEntity findCustomerById(Long customerId) {
        Optional<CustomerEntity> customerOptional = customerRepository.findById(customerId);
        if (!customerOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.CUSTOMER_NOT_FOUND);
        }
        return customerOptional.get();
    }

    @Override
    public ResponseEntity<MessageDTO> changePassword(String userType, ChangePasswordReqDTO changePasswordDTO) {
        Optional<UserEntity> userOptional = null;
        if (userType.equals(UserTypes.ADMIN.toString())) {
            AdminEntity admin = findAdminById(changePasswordDTO.getUserId());
            userOptional = userRepository.findByAdminId(admin);
        } else if (userType.equals(UserTypes.CUSTOMER.toString())) {
            CustomerEntity customer = findCustomerById(changePasswordDTO.getUserId());
            userOptional = userRepository.findByCustomerId(customer);
        }
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            if (bCryptPasswordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword())) {
                user.setUpdatedDate(new Timestamp(new Date().getTime()));
                user.setPassword(bCryptPasswordEncoder.encode(changePasswordDTO.getNewPassword()));
                userRepository.save(user);
            } else {
                throw new BadRequestException(ResponseMessages.PASSWORD_MISMATCH);
            }
        } else {
            throw new BadRequestException(ResponseMessages.USER_NOT_EXISTS);
        }
        return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.PASSWORD_CHANGED_MSG));
    }

    @Override
    public ResponseEntity<MessageDTO> forgotPassword(String userType, ForgotPasswordReqDTO forgotPasswordReqDTO) {
        Optional<UserEntity> userOptional = null;

        if (userType.equals(UserTypes.ADMIN.toString())) {
            userOptional = findAdminByEmail(forgotPasswordReqDTO.getEmail());
        } else if (userType.equals(UserTypes.CUSTOMER.toString())) {
            userOptional = findCustomerByEmail(forgotPasswordReqDTO.getEmail());
        } else if (userType.equals(UserTypes.EMPLOYEE.toString())) {
            userOptional = findEmployeeByEmail(forgotPasswordReqDTO.getEmail());
        } else {
            throw new BadRequestException(ResponseMessages.INVALID_USER_TYPE);
        }

        if (Boolean.TRUE.equals(forgotPasswordReqDTO.getIsAdmin())) {
            if (StringUtils.isNotBlank(forgotPasswordReqDTO.getEmail()) && StringUtils.isNotBlank(forgotPasswordReqDTO.getVerificationCode())) {
                Boolean isNewUser = userOptional.isPresent() ? false : true;
                return userVerification(forgotPasswordReqDTO.getEmail(), forgotPasswordReqDTO.getVerificationCode(), isNewUser);
            } else {
                return sendVerificationCode(forgotPasswordReqDTO.getEmail());
            }
        } else if (!userOptional.isPresent()) {
            throw new BadRequestException(ResponseMessages.USER_NOT_EXISTS);
        }
        if (StringUtils.isNotBlank(forgotPasswordReqDTO.getEmail()) && StringUtils.isNotBlank(forgotPasswordReqDTO.getPassword())) {
            return updatePassword(userOptional.get(), forgotPasswordReqDTO);
        } else if (StringUtils.isNotBlank(forgotPasswordReqDTO.getEmail()) && StringUtils.isNotBlank(forgotPasswordReqDTO.getVerificationCode())) {
            return forgotPasswordVerification(userOptional.get(), forgotPasswordReqDTO);
        } else {
            return sendVerificationCode(userType, userOptional.get());
        }
    }

    @Override
    public ResponseEntity<MessageDTO> verifyUser(String userType, ForgotPasswordReqDTO forgotPasswordReqDTO) {
        Optional<UserEntity> user;
        if (userType.equals(UserTypes.ADMIN.toString())) {
            user = findAdminByEmail(forgotPasswordReqDTO.getEmail());
        } else if (userType.equals(UserTypes.CUSTOMER.toString())) {
            user = findCustomerByEmail(forgotPasswordReqDTO.getEmail());
        } else {
            throw new BadRequestException(ResponseMessages.INVALID_USER_TYPE);
        }

        if (user.isPresent()) {
            throw new BadRequestException(ResponseMessages.USER_ALREADY_EXISTS);
        }
        if (StringUtils.isNotBlank(forgotPasswordReqDTO.getEmail()) && StringUtils.isNotBlank(forgotPasswordReqDTO.getVerificationCode())) {
            return userVerification(forgotPasswordReqDTO.getEmail(), forgotPasswordReqDTO.getVerificationCode(), true);
        } else {
            return sendVerificationCode(forgotPasswordReqDTO.getEmail());
        }
    }

    @Override
    public ResponseEntity<MessageDTO> updateUser(String userType, UserDetailsUpdateDTO userDetailsUpdateDTO) {
        if (userType.equals(UserTypes.ADMIN.toString())) {
            return updateAdmin(userType, userDetailsUpdateDTO);
        } else if (userType.equals(UserTypes.CUSTOMER.toString())) {
            return updateCustomer(userType, userDetailsUpdateDTO);
        } else if (userType.equals(UserTypes.EMPLOYEE.toString())) {
            return updateEmployee(userType, userDetailsUpdateDTO);
        } else {
            throw new BadRequestException(ResponseMessages.INVALID_USER_TYPE);
        }
    }

    private ResponseEntity<MessageDTO> updateEmployee(String userType, UserDetailsUpdateDTO userDetailsUpdateDTO) {
        EmployeeEntity employeeEntity = findEmployeeById(userDetailsUpdateDTO.getId());
        List<SubServiceEntity> subServiceEntities = new ArrayList<>();
        userDetailsUpdateDTO.getSubServiceId().forEach(subServiceId -> subServiceEntities.add(findSubServiceById(subServiceId)));
        try {
            employeeEntity.setName(userDetailsUpdateDTO.getName());
            employeeEntity.setPhone(userDetailsUpdateDTO.getPhone());
            employeeEntity.setDegree(userDetailsUpdateDTO.getDegree());
            employeeEntity.setExperience(userDetailsUpdateDTO.getExperience());
            employeeEntity.setServiceId(findServiceById(userDetailsUpdateDTO.getServiceId()));
            employeeEntity.setBranchId(findBranchById(userDetailsUpdateDTO.getBranchId()));
            employeeEntity.setSubServices(subServiceEntities);
            employeeEntity.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            employeeEntity.setUpdatedDate(new Timestamp(new Date().getTime()));
            employeeEntity = employeeRepository.save(employeeEntity);

            //Update User Entity
            updateUserEntityDetails(userType, null, null, employeeEntity);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
        return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.USER_DETAILS_UPDATED_MSG));
    }

    private EmployeeEntity findEmployeeById(Long employeeId) {
        Optional<EmployeeEntity> employeeEntityOptional = employeeRepository.findById(employeeId);
        if (!employeeEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.EMPLOYEE_NOT_FOUND);
        }
        return employeeEntityOptional.get();
    }

    private ResponseEntity<MessageDTO> updateCustomer(String userType, UserDetailsUpdateDTO userDetailsUpdateDTO) {
        CustomerEntity customer = findCustomerById(userDetailsUpdateDTO.getId());
        try {
            customer.setName(userDetailsUpdateDTO.getName());
            customer.setPhone(userDetailsUpdateDTO.getPhone());
            customer.setUpdatedDate(new Timestamp(new Date().getTime()));
            customer = customerRepository.save(customer);

            //Update User Entity
            updateUserEntityDetails(userType, null, customer, null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
        return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.USER_DETAILS_UPDATED_MSG));
    }

    private ResponseEntity<MessageDTO> updateAdmin(String userType, UserDetailsUpdateDTO userDetailsUpdateDTO) {
        AdminEntity admin = findAdminById(userDetailsUpdateDTO.getId());
        try {
            admin.setName(userDetailsUpdateDTO.getName());
            admin.setShopName(userDetailsUpdateDTO.getShopName());
            admin.setPhone(userDetailsUpdateDTO.getPhone());
            admin.setUpdatedDate(new Timestamp(new Date().getTime()));
            admin = adminRepository.save(admin);

            //Update User entity
            updateUserEntityDetails(userType, admin, null, null);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
        return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.USER_DETAILS_UPDATED_MSG));
    }

    private void updateUserEntityDetails(String userType, AdminEntity admin, CustomerEntity customer, EmployeeEntity employeeEntity) {
        UserEntity user = null;
        if (userType.equals(UserTypes.ADMIN.toString()) && admin != null) {
            user = findAdminByEmail(admin.getEmail()).get();
            user.setPhone(admin.getPhone());
        } else if (userType.equals(UserTypes.CUSTOMER.toString()) && customer != null) {
            user = findCustomerByEmail(customer.getEmail()).get();
            user.setPhone(customer.getPhone());
        } else if (userType.equals(UserTypes.EMPLOYEE.toString()) && employeeEntity != null) {
            user = findEmployeeByEmail(employeeEntity.getEmail()).get();
            user.setPhone(employeeEntity.getPhone());
        }
        user.setUpdatedDate(new Timestamp(new Date().getTime()));
        saveUser(user);
    }

    @Override
    public ResponseEntity<UserDetailsResDTO> getUser(String userType, Long id) {
        if (userType.equals(UserTypes.ADMIN.toString())) {
            return ResponseEntity.ok(new UserDetailsResDTO(findAdminById(id)));
        } else if (userType.equals(UserTypes.CUSTOMER.toString())) {
            return ResponseEntity.ok(new UserDetailsResDTO(findCustomerById(id)));
        } else {
            throw new BadRequestException(ResponseMessages.INVALID_USER_TYPE);
        }
    }

    private ResponseEntity<MessageDTO> userVerification(String email, String verificationCode, Boolean isNewUser) {
        Long timeInSecs = Calendar.getInstance().getTimeInMillis();
        Optional<UserVerificationEntity> userVerificationOptional = userVerificationRepo.findTop1ByEmailOrderByIdDesc(email);
        if (userVerificationOptional.isPresent()) {
            UserVerificationEntity userVerification = userVerificationOptional.get();
            if (userVerification.getStatus().equals("A") && userVerification.getCodeSentOn().after(new Date(timeInSecs - (3 * 60 * 1000))) && userVerification.getVerificationCode().equals(verificationCode)) {
                userVerification.setStatus("V");
                userVerificationRepo.save(userVerification);
            } else if (!userVerification.getVerificationCode().equals(verificationCode)) {
                userVerification.setStatus("M");
                userVerificationRepo.save(userVerification);
                throw new BadRequestException(ResponseMessages.GIVEN_CODE_INCORRECT_MSG);
            } else if (userVerification.getStatus().equals("A") && userVerification.getCodeSentOn().before(new Date(timeInSecs - (3 * 60 * 1000)))) {
                userVerification.setStatus("E");
                userVerificationRepo.save(userVerification);
                throw new BadRequestException(ResponseMessages.CODE_VALIDITY_EXPIRED_MSG);
            }
        }
        return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.CODE_VERIFY_SUCCESS_MSG, isNewUser));
    }

    private ResponseEntity<MessageDTO> sendVerificationCode(String email) {
        String code = AppUtils.getRandomDigits().toString();
        try {
            Map<String, String> model = new HashMap<>();
            model.put(Constants.EMAIL, email);
            model.put(Constants.CODE, code);

            Mail mail = new Mail();
            mail.setSubject(Constants.VERIFY_USER_SUBJECT);
            mail.setTo(email);
            mail.setModel(model);
            emailService.sendSimpleMessageByTemplate(mail, Constants.USER_VERIFICATION_TEMPLATE);

            UserVerificationEntity userVerification = new UserVerificationEntity();
            userVerification.setEmail(email);
            userVerification.setVerificationCode(code);
            userVerification.setStatus("A");
            userVerificationRepo.save(userVerification);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
        return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.CODE_SEND_MSG + code));
    }

    private ResponseEntity<MessageDTO> sendVerificationCode(String userType, UserEntity user) {
        String code = AppUtils.getRandomDigits().toString();
        try {
            Map<String, String> model = new HashMap<>();

            if (userType.equals(UserTypes.ADMIN.toString())) {
                model.put(Constants.USERNAME, user.getAdminId().getName());
            } else if (userType.equals(UserTypes.EMPLOYEE.toString())) {
                model.put(Constants.USERNAME, user.getEmployeeId().getName());
            } else if (userType.equals(UserTypes.CUSTOMER.toString())) {
                model.put(Constants.USERNAME, user.getCustomerId().getName());
            }
            model.put(Constants.MESSAGE, Constants.FORGOT_PWD_MSG);
            model.put(Constants.CODE, code);
            model.put(Constants.NOTE_MSG, Constants.FORGOT_PWD_NOTE_MSG);

            Mail mail = new Mail();
            mail.setSubject(Constants.FORGOT_PWD_SUBJECT_MSG);
            mail.setTo(user.getEmail());
            mail.setModel(model);
            emailService.sendSimpleMessageByTemplate(mail, Constants.FORGOT_PASSWORD_TEMPLATE);

            UserVerificationEntity userVerification = new UserVerificationEntity();
            userVerification.setUserId(user);
            userVerification.setVerificationCode(code);
            userVerification.setStatus("A");
            userVerificationRepo.save(userVerification);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
        return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.CODE_SEND_MSG + code));
    }

    private ResponseEntity<MessageDTO> forgotPasswordVerification(UserEntity user, ForgotPasswordReqDTO forgotPasswordReqDTO) {
        Long timeInSecs = Calendar.getInstance().getTimeInMillis();
        Optional<UserVerificationEntity> userVerificationOptional = userVerificationRepo.findTop1ByUserIdOrderByIdDesc(user);
        if (userVerificationOptional.isPresent()) {
            UserVerificationEntity userVerification = userVerificationOptional.get();
            if (userVerification.getStatus().equals("A")) {
                if (userVerification.getCodeSentOn().after(new Date(timeInSecs - (3 * 60 * 1000))) && userVerification.getVerificationCode().equals(forgotPasswordReqDTO.getVerificationCode())) {
                    userVerification.setStatus("V");
                    userVerificationRepo.save(userVerification);
                } else if (!userVerification.getVerificationCode().equals(forgotPasswordReqDTO.getVerificationCode())) {
                    userVerification.setStatus("M");
                    userVerificationRepo.save(userVerification);
                    throw new BadRequestException(ResponseMessages.GIVEN_CODE_INCORRECT_MSG);
                } else if (userVerification.getCodeSentOn().before(new Date(timeInSecs - (3 * 60 * 1000)))) {
                    userVerification.setStatus("E");
                    userVerificationRepo.save(userVerification);
                    throw new BadRequestException(ResponseMessages.CODE_VALIDITY_EXPIRED_MSG);
                }
            } else {
                throw new BadRequestException(ResponseMessages.INVALID_CODE);
            }
        }
        return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.CODE_VERIFY_SUCCESS_MSG));
    }

    private ResponseEntity<MessageDTO> updatePassword(UserEntity user, ForgotPasswordReqDTO forgotPasswordReqDTO) {
        try {
            user.setPassword(bCryptPasswordEncoder.encode(forgotPasswordReqDTO.getPassword()));
            saveUser(user);
            return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.PASSWORD_CHANGED_MSG));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    @Override
    public UserEntity createCustomer(CustomerInfoReqDTO userDTO) {
        CustomerEntity customer = new CustomerEntity(
                null,
                userDTO.getName(),
                userDTO.getEmail(),
                userDTO.getPhone(),
                new Timestamp(new Date().getTime()),
                null
        );
        customer = customerRepository.save(customer);

        UserEntity user = new UserEntity();
        user.setCustomerId(customer);
        user.setEmail(customer.getEmail());
        user.setIsActive(true);
        user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        user.setPhone(customer.getPhone());
        user.setRoles(Arrays.asList(findRoleByName(Constants.CUSTOMER)));
        user.setUsername(Constants.CUSTOMER +
                AppUtils.getRandomDigits() + "_" + customer.getName());
        return saveUser(user);
    }

    private UserEntity createAdmin(AdminInfoRequestDTO userDTO) {
        AdminEntity admin = new AdminEntity(
                null,
                userDTO.getName(),
                userDTO.getShopName(),
                userDTO.getEmail(),
                userDTO.getPhone(),
                new Timestamp(new Date().getTime()),
                null,
                findServiceCategoryById(userDTO.getServiceCategoryId())
        );
        admin = adminRepository.save(admin);

        UserEntity user = new UserEntity();
        user.setAdminId(admin);
        user.setEmail(admin.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        user.setPhone(admin.getPhone());
        user.setIsActive(true);
        user.setRoles(Arrays.asList(findRoleByName(Constants.ADMIN)));
        user.setUsername(Constants.ADMIN +
                AppUtils.getRandomDigits() + "_" + admin.getName());
        return saveUser(user);
    }

    private ServiceCategoryEntity findServiceCategoryById(Long serviceCategoryId) {
        Optional<ServiceCategoryEntity> serviceCategoryEntityOptional = serviceCategoryRepository.findById(serviceCategoryId);
        if (!serviceCategoryEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.SERVICE_CATEGORY_NOT_FOUND);
        }
        return serviceCategoryEntityOptional.get();
    }

    private RoleEntity findRoleByName(String role) {
        Optional<RoleEntity> roleOptional = roleRepository.findByName(role);
        if (!roleOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.ROLE_NOT_FOUND);
        }
        return roleOptional.get();
    }

    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }
}
