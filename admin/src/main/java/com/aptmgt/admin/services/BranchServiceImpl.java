package com.aptmgt.admin.services;

import com.aptmgt.admin.dto.BranchRequestDTO;
import com.aptmgt.admin.dto.BranchServicesDTO;
import com.aptmgt.admin.dto.GeocodeResponseDTO;
import com.aptmgt.admin.dto.Position;
import com.aptmgt.admin.services.email.EmailService;
import com.aptmgt.commons.model.*;
import com.aptmgt.commons.repository.*;
import com.aptmgt.admin.dto.*;
import com.aptmgt.commons.dto.BranchResponseDTO;
import com.aptmgt.commons.dto.MessageDTO;
import com.aptmgt.commons.dto.ResponseDTO;
import com.aptmgt.commons.exceptions.BadRequestException;
import com.aptmgt.commons.exceptions.ErrorHandler;
import com.aptmgt.commons.exceptions.InternalServerErrorException;
import com.aptmgt.commons.exceptions.RecordNotFoundException;
import com.aptmgt.commons.utils.AppUtils;
import com.aptmgt.commons.utils.Constants;
import com.aptmgt.commons.utils.ResponseMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.*;

@Service
public class BranchServiceImpl implements IBranchService {

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private TradingHoursRepository tradingHoursRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${here.api.url}")
    private String hereApiUrl;

    @Value("${here.api.key}")
    private String hereApiKey;

    @Autowired
    private BranchFilesService branchFilesService;

    @Autowired
    private BranchFilesRepository branchFilesRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(BranchServiceImpl.class);

    @Override
    public ResponseEntity<ResponseDTO> saveBranch(BranchRequestDTO branchDTO, MultipartFile file, MultipartFile[] files) throws URISyntaxException, IOException {
        BranchEntity branchEntity;
        if (branchDTO.getServiceCategoryId() != 3) {
            Optional<UserEntity> user = findEmployeeByEmail(branchDTO.getMail());
            if (user.isPresent()) {
                throw new BadRequestException(ResponseMessages.USER_ALREADY_EXISTS);
            }
            branchEntity = saveBranchDetails(null, branchDTO, file);
            updateTradingHours(branchEntity);
            createEmployee(branchEntity, branchDTO);
        } else {
            branchEntity = saveBranchDetails(null, branchDTO, file);
        }
        if(files != null) {
            branchFilesService.saveBranchFiles(branchEntity, files);
        }
        if(branchEntity != null && branchEntity.getImage() != null) {

            List<BranchFilesEntity> branchFilesEntityList = branchFilesService.getByBranchId(branchEntity);
            // send notification to super admin
            emailService.sendSuperAdminNotification(branchEntity, branchFilesEntityList);
        }
        return ResponseEntity.ok(new ResponseDTO(ResponseMessages.BRANCH_CREATED_MSG, true, branchEntity, null));
    }

    private Optional<UserEntity> findEmployeeByEmail(String email) {
        return userRepository.findByEmailAndIsActiveAndEmployeeIdIsNotNull(email, true);
    }

    private void createEmployee(BranchEntity branchEntity, BranchRequestDTO branchDTO) {
        if (branchDTO.getServiceCategoryId() != 3) {
            EmployeeEntity employeeEntity;
            Optional<EmployeeEntity> existingEmployeeEntity = employeeRepository.findByIsActiveAndBranchId(true, branchEntity);
            if (existingEmployeeEntity.isPresent()) {
                employeeEntity = existingEmployeeEntity.get();

                //Deactivating employee in user table
                UserEntity user = userRepository.findByEmployeeId(employeeEntity).get();
                user.setIsActive(false);
                userRepository.save(user);

                //Deactivating employee in employee table
                employeeEntity.setIsActive(false);
                employeeRepository.save(employeeEntity);
            }
        }

        EmployeeEntity employeeEntity = new EmployeeEntity();
        employeeEntity.setName(Constants.EMPLOYEE_LOWER_CASE +
                AppUtils.getRandomDigits() + "_" + branchDTO.getName());
        employeeEntity.setEmail(branchDTO.getMail());
        employeeEntity.setPhone(branchDTO.getPhone());
        employeeEntity.setCity(branchDTO.getCity());
        employeeEntity.setIsActive(true);
        employeeEntity.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        employeeEntity.setCreatedDate(new Timestamp(new Date().getTime()));
        employeeEntity.setBranchId(branchEntity);

        employeeEntity = employeeRepository.save(employeeEntity);

        UserEntity user = new UserEntity();
        String password = AppUtils.generateRandomPassword(10);
        user.setEmployeeId(employeeEntity);
        user.setEmail(employeeEntity.getEmail());
        user.setIsActive(true);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setPhone(employeeEntity.getPhone());
        user.setRoles(Arrays.asList(findRoleByName(Constants.EMPLOYEE)));
        user.setUsername(employeeEntity.getName());
        saveUser(user);

        if (employeeEntity.getEmail() != null) {
            // send password notification
            emailService.sendPasswordNotification(employeeEntity, password);
        }
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


    private BranchEntity saveBranchDetails(Long branchId, BranchRequestDTO branchDTO, MultipartFile file) throws URISyntaxException, IOException {
        BranchEntity branchEntity = new BranchEntity();
        if (branchId != null) {
            branchEntity = findBranchById(branchId);
            branchEntity.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            branchEntity.setUpdatedDate(new Timestamp(new Date().getTime()));
        } else {
            branchEntity.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            branchEntity.setCreatedDate(new Timestamp(new Date().getTime()));
        }
        branchEntity.setAdminId(findAdminById(branchDTO.getAdminId()));
        branchEntity.setServiceCategoryId(findByServiceCategoryId(branchDTO.getServiceCategoryId()));
        String address = branchDTO.getAddress() + branchDTO.getState() + branchDTO.getCountry() + branchDTO.getZipcode();
        Position position = getGeocodes(address.replaceAll(Constants.SPACE, "%20"));

        setBranchDetails(branchEntity, branchDTO, file);
        try {
            branchEntity.setLatitude(position.getLat());
            branchEntity.setLongitude(position.getLng());
            return branchRepository.save(branchEntity);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private void setBranchDetails(BranchEntity branchEntity, BranchRequestDTO branchDTO, MultipartFile file) {
        try {
            branchEntity.setName(branchDTO.getName());
            branchEntity.setPhone(branchDTO.getPhone());
            branchEntity.setMail(branchDTO.getMail());
            branchEntity.setAddress(branchDTO.getAddress());
            branchEntity.setWebsite(branchDTO.getWebsite());
            branchEntity.setIsActive(true);
            branchEntity.setCity(branchDTO.getCity());
            branchEntity.setState(branchDTO.getState());
            branchEntity.setCountry(branchDTO.getCountry());
            branchEntity.setZipcode(branchDTO.getZipcode());
            if (file != null) {
                branchEntity.setFileName(StringUtils.cleanPath(file.getOriginalFilename()));
                branchEntity.setFileType(file.getContentType());
                branchEntity.setImage(file.getBytes());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private Position getGeocodes(String address) throws URISyntaxException {
        restTemplate.setErrorHandler(new ErrorHandler());

        URI uri = new URI(hereApiUrl + address + Constants.HERE_API_PARAMS + hereApiKey);

        // send GET request
        ResponseEntity<GeocodeResponseDTO> response = restTemplate.getForEntity(uri, GeocodeResponseDTO.class);

        if(response.getBody().getItems().isEmpty()) {
            throw new BadRequestException(Constants.BRANCH_ADDRESS_INVALID);
        }

        return Objects.requireNonNull(response.getBody()).getItems().get(0).getPosition();
    }

    private void updateTradingHours(BranchEntity branchEntity) {
        try {
            tradingHoursRepository.save(new TradingHoursEntity(
                    null,
                    "08:00:00",
                    "22:00:00",
                    true,
                    false,
                    "Admin",
                    new Timestamp(new Date().getTime()),
                    null,
                    null,
                    null,
                    branchEntity
            ));
            tradingHoursRepository.save(new TradingHoursEntity(
                    null,
                    "08:00:00",
                    "22:00:00",
                    false,
                    true,
                    "Admin",
                    new Timestamp(new Date().getTime()),
                    null,
                    null,
                    null,
                    branchEntity
            ));
        } catch (Exception e) {
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

    private ServiceCategoryEntity findByServiceCategoryId(Long serviceCategoryId) {
        Optional<ServiceCategoryEntity> serviceCategoryEntityOptional = serviceCategoryRepository.findById(serviceCategoryId);
        if (!serviceCategoryEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.SERVICE_CATEGORY_NOT_FOUND);
        }
        return serviceCategoryEntityOptional.get();
    }

    public BranchEntity findBranchById(Long branchId) {
        Optional<BranchEntity> branchEntityOptional = branchRepository.findById(branchId);
        if (!branchEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.BRANCH_NOT_FOUND);
        }
        return branchEntityOptional.get();
    }

    @Override
    public ResponseEntity<BranchResponseDTO> fetchByBranchId(Long branchId) {
        BranchEntity branchEntity = findBranchById(branchId);
        List<BranchFilesEntity> branchFilesEntityList = branchFilesService.getByBranchId(branchEntity);
        return ResponseEntity.ok(new BranchResponseDTO(branchEntity, branchFilesEntityList));
    }

    @Override
    public ResponseEntity<MessageDTO> updateBranch(Long branchId, BranchRequestDTO branchDTO, MultipartFile file, MultipartFile[] files) throws URISyntaxException, IOException {
        BranchEntity branchEntity = null;
        if (branchDTO.getServiceCategoryId() != 3) {
            Optional<UserEntity> user = findEmployeeByEmail(branchDTO.getMail());
            if (user.isPresent()) {
                branchEntity = saveBranchDetails(branchId, branchDTO, file);
                createEmployee(branchEntity, branchDTO);
            }
        } else {
            branchEntity = saveBranchDetails(branchId, branchDTO, file);
        }
        if(branchEntity != null && files != null) {
            branchFilesService.saveBranchFiles(branchEntity, files);
        }
        return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.BRANCH_UPDATE_MSG));
    }

    @Override
    public ResponseEntity<MessageDTO> deleteBranch(Long branchId) {
        BranchEntity branchEntity = findBranchById(branchId);
        try {
            branchEntity.setIsActive(false);
            branchEntity.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            branchEntity.setUpdatedDate(new Timestamp(new Date().getTime()));
            branchRepository.save(branchEntity);
            return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.BRANCH_DELETED_MSG));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    @Override
    public ResponseEntity<List<BranchResponseDTO>> fetchBranchByAdminId(Long adminId) {
        List<BranchResponseDTO> branchResponseDTOList = new ArrayList<>();
        AdminEntity admin = findAdminById(adminId);
        List<BranchEntity> branchEntityList = branchRepository.findByAdminIdAndIsActive(admin, true);
        if (!branchEntityList.isEmpty()) {
            branchEntityList.forEach(branch -> branchResponseDTOList.add(new BranchResponseDTO(branch, null)));
        }
        return ResponseEntity.ok(branchResponseDTOList);
    }

    @Override
    public ResponseEntity<MessageDTO> setBranchServices(BranchServicesDTO request) {
        BranchEntity branchEntity = findBranchById(request.getBranchId());
        Set<ServiceEntity> serviceEntities = new HashSet<>();
        request.getServicesId().forEach(serviceId -> serviceEntities.add(findServiceById(serviceId)));
        try {
            branchEntity.setServices(serviceEntities);
            branchEntity.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            branchEntity.setUpdatedDate(new Timestamp(new Date().getTime()));
            branchRepository.save(branchEntity);
            return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.SERVICE_LINKED_TO_BRANCH_MSG));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private ServiceEntity findServiceById(Long serviceId) {
        Optional<ServiceEntity> serviceEntityOptional = serviceRepository.findById(serviceId);
        if (!serviceEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.SERVICE_NOT_FOUND);
        }
        return serviceEntityOptional.get();
    }
}