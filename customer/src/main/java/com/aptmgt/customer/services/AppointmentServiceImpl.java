package com.aptmgt.customer.services;

import com.aptmgt.commons.model.*;
import com.aptmgt.commons.repository.*;
import com.aptmgt.customer.dto.*;
import com.aptmgt.commons.dto.FrequentDTO;
import com.aptmgt.commons.dto.FrequentvisitResponseDTO;
import com.aptmgt.commons.dto.ResponseDTO;
import com.aptmgt.commons.exceptions.BadRequestException;
import com.aptmgt.commons.exceptions.InternalServerErrorException;
import com.aptmgt.commons.exceptions.RecordNotFoundException;
import com.aptmgt.commons.utils.Constants;
import com.aptmgt.commons.utils.DateUtils;
import com.aptmgt.commons.utils.ResponseMessages;
import com.aptmgt.customer.dto.*;
import com.aptmgt.customer.services.email.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements IAppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PublicHolidayRepository publicHolidayRepository;

    @Autowired
    private ShopHolidayRepository shopHolidayRepository;

    @Autowired
    private AvailableSlotsRepository availableSlotsRepository;

    @Autowired
    private ServiceAvailabilityRepository serviceAvailabilityRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private AppointmentStatusRepository appointmentStatusRepository;

    @Autowired
    private EmailService emailService;

    private static final String FORMAT = "yyyy-MM-dd HH:mm";

    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    @Override
    public ResponseEntity<MessageDTO> saveAppointment(AppointmentRequestDTO appointmentRequestDTO) {
        String dateString = DateUtils.getDayInWords(appointmentRequestDTO.getDate());
        BranchEntity branchEntity = findBranchById(appointmentRequestDTO.getBranchId());
        ServiceEntity serviceEntity = findServiceById(appointmentRequestDTO.getServiceId());
        EmployeeEntity employeeEntity = appointmentRequestDTO.getEmployeeId() != null ? findEmployeeById(appointmentRequestDTO.getEmployeeId()) : null;
        Optional<AvailableSlotsEntity> availableSlotsEntityOptional;

        if (employeeEntity != null) {
            availableSlotsEntityOptional =
                    availableSlotsRepository.findByEmployeeIdAndServiceIdAndSlotDateAndStartTime(employeeEntity, serviceEntity, appointmentRequestDTO.getDate(), appointmentRequestDTO.getStartTime());
        } else {
            availableSlotsEntityOptional =
                    availableSlotsRepository.findByBranchIdAndServiceIdAndSlotDateAndStartTime(branchEntity, serviceEntity, appointmentRequestDTO.getDate(), appointmentRequestDTO.getStartTime());
        }
        if (availableSlotsEntityOptional.isPresent()) {
            increaseSlotCapacity(availableSlotsEntityOptional.get(), appointmentRequestDTO.getPersonCount());
        } else {
            Optional<ServiceAvailabilityEntity> serviceAvailabilityEntityOptional;
            if (employeeEntity != null) {
                serviceAvailabilityEntityOptional = serviceAvailabilityRepository.findByEmployeeIdAndServiceIdAndSlotDay(employeeEntity, serviceEntity, dateString);
            } else {
                serviceAvailabilityEntityOptional = serviceAvailabilityRepository.findByBranchIdAndServiceIdAndSlotDay(branchEntity, serviceEntity, dateString);
            }
            if (!serviceAvailabilityEntityOptional.isPresent()) {
                throw new RecordNotFoundException(ResponseMessages.SLOT_NOT_CONFIGURED);
            } else {
                createNewSlot(serviceAvailabilityEntityOptional.get(), serviceEntity, branchEntity, appointmentRequestDTO, employeeEntity);
            }
        }
        //Scheduling appointment
        scheduleAppointment(appointmentRequestDTO, branchEntity, serviceEntity, employeeEntity);

        LOGGER.info(ResponseMessages.APPOINTMENT_SCHEDULED_MSG);

        return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.APPOINTMENT_SCHEDULED_MSG));
    }

    private void scheduleAppointment(AppointmentRequestDTO appointmentRequestDTO, BranchEntity branchEntity, ServiceEntity serviceEntity, EmployeeEntity employeeEntity) {
        CustomerEntity customer = findCustomerById(appointmentRequestDTO.getCustomerId());
        AppointmentEntity appointmentEntity = new AppointmentEntity();
        appointmentEntity.setAppointmentStatusId(findAppointmentStatusById(1));
        appointmentEntity.setEndTime(findEndTime(branchEntity, serviceEntity, appointmentRequestDTO, employeeEntity));

        try {
            appointmentEntity.setAppointmentDate(appointmentRequestDTO.getDate());
            appointmentEntity.setStartTime(appointmentRequestDTO.getStartTime());
            appointmentEntity.setAppointeeName(appointmentRequestDTO.getAppointeeName() != null ? appointmentRequestDTO.getAppointeeName() : customer.getName());
            appointmentEntity.setAppointeeEmail(appointmentRequestDTO.getAppointeeEmail() != null ? appointmentRequestDTO.getAppointeeEmail() : customer.getEmail());
            appointmentEntity.setAppointeePhone(appointmentRequestDTO.getAppointeePhone() != null ? appointmentRequestDTO.getAppointeePhone() : customer.getPhone());
            appointmentEntity.setPersonCount(appointmentRequestDTO.getPersonCount());
            appointmentEntity.setDescription(appointmentRequestDTO.getDescription());
            appointmentEntity.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            appointmentEntity.setCreatedDate(new Timestamp(new Date().getTime()));
            appointmentEntity.setCustomerId(customer);
            appointmentEntity.setBranchId(branchEntity);
            appointmentEntity.setEmployeeId(employeeEntity);
            appointmentEntity.setServiceId(serviceEntity);
            appointmentEntity = appointmentRepository.save(appointmentEntity);

            if (appointmentEntity.getAppointeeEmail() != null) {
                //Sending notification
                emailService.sendScheduleNotification(appointmentEntity);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private Time findEndTime(BranchEntity branchEntity, ServiceEntity serviceEntity, AppointmentRequestDTO appointmentRequestDTO, EmployeeEntity employeeEntity) {
        String day = DateUtils.getDayInWords(appointmentRequestDTO.getDate());
        Optional<ServiceAvailabilityEntity> serviceAvailabilityEntity;
        if (employeeEntity != null) {
            serviceAvailabilityEntity = serviceAvailabilityRepository.findByEmployeeIdAndServiceIdAndSlotDay(employeeEntity, serviceEntity, day);
        } else {
            serviceAvailabilityEntity = serviceAvailabilityRepository.findByBranchIdAndServiceIdAndSlotDay(branchEntity, serviceEntity, day);
        }
        if (!serviceAvailabilityEntity.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.SLOT_NOT_CONFIGURED);
        }
        return DateUtils.getForwardTime(appointmentRequestDTO.getStartTime(), serviceAvailabilityEntity.get().getSlotInterval());
    }

    private void createNewSlot(ServiceAvailabilityEntity serviceAvailabilityEntity, ServiceEntity serviceEntity, BranchEntity branchEntity, AppointmentRequestDTO appointmentRequestDTO, EmployeeEntity employeeEntity) {
        try {
            AvailableSlotsEntity availableSlotsEntity = new AvailableSlotsEntity();
            availableSlotsEntity.setBranchId(branchEntity);
            availableSlotsEntity.setEmployeeId(employeeEntity);
            availableSlotsEntity.setServiceId(serviceEntity);
            availableSlotsEntity.setStartTime(appointmentRequestDTO.getStartTime());
            availableSlotsEntity.setEndTime(appointmentRequestDTO.getEndTime());
            availableSlotsEntity.setStatus(Constants.IN_PROGRESS);
            availableSlotsEntity.setSlotDate(appointmentRequestDTO.getDate());
            availableSlotsEntity.setBookedCapacity(appointmentRequestDTO.getPersonCount());
            availableSlotsEntity.setTotalCapacity(serviceAvailabilityEntity.getSlotCapacity());
            availableSlotsRepository.save(availableSlotsEntity);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private void increaseSlotCapacity(AvailableSlotsEntity availableSlotsEntity, Integer personCount) {
        try {
            availableSlotsEntity.setBookedCapacity(availableSlotsEntity.getBookedCapacity() + personCount);
            if (availableSlotsEntity.getBookedCapacity().equals(availableSlotsEntity.getTotalCapacity())) {
                availableSlotsEntity.setStatus(Constants.BOOKED);
            }
            availableSlotsRepository.save(availableSlotsEntity);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private AppointmentStatusEntity findAppointmentStatusById(Integer appointmentStatusId) {
        Optional<AppointmentStatusEntity> appointmentStatusOptional = appointmentStatusRepository.findById(appointmentStatusId);
        if (!appointmentStatusOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.STATUS_NOT_FOUND);
        }
        return appointmentStatusOptional.get();
    }

    private BranchEntity findBranchById(Long branchId) {
        Optional<BranchEntity> branchEntityOptional = branchRepository.findById(branchId);
        if (!branchEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.BRANCH_NOT_FOUND);
        }
        return branchEntityOptional.get();
    }

    private EmployeeEntity findEmployeeById(Long employeeId) {
        Optional<EmployeeEntity> employeeRepositoryOptional = employeeRepository.findById(employeeId);
        if (!employeeRepositoryOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.EMPLOYEE_NOT_FOUND);
        }
        return employeeRepositoryOptional.get();
    }

    private CustomerEntity findCustomerById(Long customerId) {
        Optional<CustomerEntity> customerOptional = customerRepository.findById(customerId);
        if (!customerOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.CUSTOMER_NOT_FOUND);
        }
        return customerOptional.get();
    }

    private ServiceEntity findServiceById(Long serviceId) {
        Optional<ServiceEntity> serviceEntityOptional = serviceRepository.findById(serviceId);
        if (!serviceEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.SERVICE_NOT_FOUND);
        }
        return serviceEntityOptional.get();
    }

    @Override
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentDetails(AppointmentListRequestDTO appointmentListRequestDTO) {
        List<AppointmentResponseDTO> appointmentResponseDTOList = new ArrayList<>();
        List<AppointmentEntity> appointmentList;
        CustomerEntity customer = findCustomerById(appointmentListRequestDTO.getCustomerId());
        try {
            if (appointmentListRequestDTO.getCategory().equals(Constants.TODAY)) {
                appointmentList = appointmentRepository.findByCustomerIdAndAppointmentDateOrderByStartTimeAsc(customer, appointmentListRequestDTO.getDate());
            } else if (appointmentListRequestDTO.getCategory().equals(Constants.UPCOMING)) {
                appointmentList = appointmentRepository.findByCustomerIdAndAndAppointmentDateAfterOrderByAppointmentDateAscStartTimeAsc(customer, appointmentListRequestDTO.getDate());
            } else if (appointmentListRequestDTO.getCategory().equals(Constants.PREVIOUS)) {
                appointmentList = appointmentRepository.findByCustomerIdAndAppointmentDateBeforeOrderByAppointmentDateDescStartTimeAsc(customer, appointmentListRequestDTO.getDate());
            } else {
                throw new BadRequestException(ResponseMessages.INVALID_CATEGORY);
            }
            if (!appointmentList.isEmpty()) {
                appointmentList.forEach(appointment -> appointmentResponseDTOList.add(new AppointmentResponseDTO(appointment)));
            }
            return ResponseEntity.ok(appointmentResponseDTOList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    @Override
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentUpcomingTime(AppointmentListRequestDTO appointmentListRequestDTO) {
        List<AppointmentResponseDTO> appointmentResponseDTOList = new ArrayList<>();
        try {
            List<AppointmentEntity> appointmentEntityList = appointmentRepository.findByCustomerIdAndAppointmentDateAndAppointmentStatusIdAndStartTimeAfterOrderByStartTimeDesc(findCustomerById(appointmentListRequestDTO.getCustomerId()), appointmentListRequestDTO.getDate(), findAppointmentStatusById(1), appointmentListRequestDTO.getStartTime());
            if (!appointmentEntityList.isEmpty()) {
                appointmentEntityList.forEach(appointmentEntity -> {
                    appointmentResponseDTOList.add(new AppointmentResponseDTO(appointmentEntity));
                });
            }
            return ResponseEntity.ok(appointmentResponseDTOList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    @Override
    public ResponseEntity<MessageDTO> cancelAppointment(StatusUpdateRequestDTO request) {
        AppointmentEntity appointmentEntity = findAppointmentById(request.getAppointmentId());

        decreaseSlotBookedCapacity(appointmentEntity);

        appointmentEntity.setAppointmentStatusId(findAppointmentStatusById(4));
        try {
            appointmentEntity.setReasonForCancel(request.getReasonForCancel());
            appointmentEntity.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            appointmentEntity.setUpdatedDate(new Timestamp(new Date().getTime()));
            appointmentRepository.save(appointmentEntity);

            if (appointmentEntity.getAppointeeEmail() != null) {
                emailService.sendCancelNotification(appointmentEntity);
            }

            return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.APPOINTMENT_CANCELLED_MSG));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private void decreaseSlotBookedCapacity(AppointmentEntity appointmentEntity) {
        try {
            AvailableSlotsEntity availableSlotsEntity = null;
            if (appointmentEntity.getEmployeeId() != null) {
                availableSlotsEntity =
                        availableSlotsRepository.findByEmployeeIdAndServiceIdAndSlotDateAndStartTime(appointmentEntity.getEmployeeId(), appointmentEntity.getServiceId(), appointmentEntity.getAppointmentDate(), appointmentEntity.getStartTime()).get();
            } else {
                availableSlotsEntity =
                        availableSlotsRepository.findByBranchIdAndServiceIdAndSlotDateAndStartTime(appointmentEntity.getBranchId(), appointmentEntity.getServiceId(), appointmentEntity.getAppointmentDate(), appointmentEntity.getStartTime()).get();
            }
            availableSlotsEntity.setBookedCapacity(availableSlotsEntity.getBookedCapacity() - appointmentEntity.getPersonCount());
            if (!availableSlotsEntity.getBookedCapacity().equals(availableSlotsEntity.getTotalCapacity())) {
                availableSlotsEntity.setStatus(Constants.IN_PROGRESS);
            }
            if (availableSlotsEntity.getBookedCapacity() > 0) {
                availableSlotsRepository.save(availableSlotsEntity);
            } else {
                availableSlotsRepository.delete(availableSlotsEntity);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    @Override
    public ResponseEntity<SlotResponse> getSlots(SlotRequestDTO request) {
        SlotResponse slotResponse = new SlotResponse();
        Optional<PublicHolidayEntity> publicHolidayEntity;
        Optional<ShopHolidayEntity> shopHolidayEntity;
        String dateString = DateUtils.getDayInWords(request.getDate());
        BranchEntity branchEntity = null;
        EmployeeEntity employeeEntity = null;
        if(request.getIsAdmin() == null || Boolean.FALSE.equals(request.getIsAdmin())) {
            checkAlreadyAppointment(request);
        }
        if (request.getEmployeeId() != null) {
            employeeEntity = findEmployeeById(request.getEmployeeId());
            publicHolidayEntity = publicHolidayRepository.findByPublicHolidayAndEmployeeId(request.getDate(), employeeEntity);
            shopHolidayEntity = shopHolidayRepository.findByShopHolidayAndEmployeeId(dateString, employeeEntity);
        } else {
            branchEntity = findBranchById(request.getBranchId());
            publicHolidayEntity = publicHolidayRepository.findByPublicHolidayAndBranchId(request.getDate(), branchEntity);
            shopHolidayEntity = shopHolidayRepository.findByShopHolidayAndBranchId(dateString, branchEntity);
        }

        slotResponse.setDate(request.getDate());
        slotResponse.setDay(dateString);

        if (publicHolidayEntity.isPresent() || shopHolidayEntity.isPresent()) {
            slotResponse.setIsHoliday(true);
            throw new RecordNotFoundException(ResponseMessages.BAD_REQUEST_MSG);
        }
        Optional<ServiceAvailabilityEntity> serviceAvailability;
        if (request.getEmployeeId() != null) {
            serviceAvailability = serviceAvailabilityRepository.findByEmployeeIdAndServiceIdAndSlotDay(employeeEntity, findServiceById(request.getServiceId()), dateString);
        } else {
            serviceAvailability = serviceAvailabilityRepository.findByBranchIdAndServiceIdAndSlotDay(branchEntity, findServiceById(request.getServiceId()), dateString);
        }

        if (!serviceAvailability.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.SLOT_NOT_CONFIGURED);
        }
        try {
            List<String> slotTimeList = getAvailabilityTimings(serviceAvailability.get().getSlotTime());
            Integer slotInterval = serviceAvailability.get().getSlotInterval();
            Integer slotCapacity = serviceAvailability.get().getSlotCapacity();
            slotResponse.setSlotCapacity(slotCapacity);

            if (!slotTimeList.isEmpty()) {
                List<Slot> slots = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
                DateFormat dateFormat = new SimpleDateFormat(DateUtils.TIME_FORMAT_12_HRS);
                for (String slotTime : slotTimeList) {
                    String[] startEndTime = slotTime.trim().split(Constants.HYPHEN);
                    Time serviceStartTime = new Time(dateFormat.parse(startEndTime[0]).getTime());
                    Time serviceEndTime = new Time(dateFormat.parse(startEndTime[1]).getTime());

                    Date startTime = sdf
                            .parse(DateUtils.getDateAsString(request.getDate()) + Constants.SPACE + serviceStartTime);
                    Date endTime = sdf
                            .parse(DateUtils.getDateAsString(request.getDate()) + Constants.SPACE + serviceEndTime);
                    Long startTimeMs = startTime.getTime();

                    while (startTimeMs < endTime.getTime()) {
                        Date slot = new Date(startTimeMs);
                        startTimeMs += DateUtils.getMilisFromMinutes(slotInterval);
                        slots.add(new Slot(DateUtils.getSqlTimeFromUtilDate(slot), false, Constants.AVAILABLE, slotCapacity));
                    }
                }
                setSlotStatus(request, slots, slotCapacity, branchEntity, employeeEntity);

                slotResponse.setSlots(slots);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
        return ResponseEntity.ok(slotResponse);
    }

    private void checkAlreadyAppointment(SlotRequestDTO request) {
        List<AppointmentEntity> appointmentEntityOptional;
            if (request.getEmployeeId() != null) {
                appointmentEntityOptional = appointmentRepository.findActiveAppointmentsForEmployee(request.getEmployeeId(), request.getCustomerId(), 1);
            } else {
                appointmentEntityOptional = appointmentRepository.findActiveAppointmentsForBranch(request.getBranchId(), request.getServiceId(), request.getCustomerId(), 1);
            }
            if (!appointmentEntityOptional.isEmpty()) {
                throw new BadRequestException(ResponseMessages.BAD_REQUEST_MSG + "/" + appointmentEntityOptional.get(0).getAppointmentDate());
            }
    }


    private void setSlotStatus(SlotRequestDTO request, List<Slot> slots, Integer slotCapacity, BranchEntity branchEntity, EmployeeEntity employeeEntity) {
        try {
            slots.stream().forEach(slot -> {
                if (request.getPersonCount() > slotCapacity) {
                    slot.setIsBooked(true);
                    slot.setStatus(Constants.BOOKED);
                }
            });
            List<AvailableSlotsEntity> bookedSlots;
            if(employeeEntity != null) {
                bookedSlots = availableSlotsRepository.findByServiceIdAndEmployeeIdAndSlotDate(findServiceById(request.getServiceId()), employeeEntity, request.getDate());
            } else {
                bookedSlots = availableSlotsRepository.findByServiceIdAndBranchIdAndSlotDate(findServiceById(request.getServiceId()), branchEntity, request.getDate());
            }
            if (!bookedSlots.isEmpty()) {
                bookedSlots.stream().forEach(bookedSlot -> slots.stream().forEach(slot -> {
                    if (isSlotTime(request.getDate(), DateUtils.getTime(bookedSlot.getStartTime()), slot.getSlotTime())) {
                        slot.setIsBooked(getSlotStatus(bookedSlot.getBookedCapacity(), bookedSlot.getTotalCapacity(), request.getPersonCount()));
                        slot.setAvailableSlot(bookedSlot.getTotalCapacity() - bookedSlot.getBookedCapacity());
                        slot.setStatus(getSlotStatus(bookedSlot.getTotalCapacity(), request.getPersonCount(), bookedSlot.getBookedCapacity(), slot.getStatus()));
                    }
                }));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private String getSlotStatus(int totalCapacity, int personCount, int bookedCapacity, String status) {
        Integer totalCount = bookedCapacity + personCount;
        if (bookedCapacity == totalCapacity) {
            return Constants.BOOKED;
        } else if (totalCount > totalCapacity) {
            return Constants.FILLING_FAST;
        }
        return status;
    }

    public List<String> getAvailabilityTimings(String workingHours) {
        List<String> timings = new ArrayList<>();
        if (workingHours != null && !workingHours.isEmpty()) {
            String[] workingHoursArray = workingHours.split(",");
            for (int i = 0; i < workingHoursArray.length; i++) {
                timings.add(workingHoursArray[i].trim());
            }
        }
        return timings;
    }

    private boolean isSlotTime(Date date, Time time, Time time2) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
        try {
            Date startTime = sdf.parse(DateUtils.getDateAsString(date) + " " + time);
            Date endTime = sdf.parse(DateUtils.getDateAsString(date) + " " + time2);
            return startTime.compareTo(endTime) == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private Boolean getSlotStatus(Object bookedCapacity, Object totalCapacity, Integer personCount) {
        Integer remainingSlot = (Integer) totalCapacity - (Integer) bookedCapacity;
        return Objects.nonNull(bookedCapacity) && Objects.nonNull(totalCapacity)
                && (bookedCapacity.equals(totalCapacity) || personCount > remainingSlot);
    }

    public AppointmentEntity findAppointmentById(Long appointmentId) {
        Optional<AppointmentEntity> appointmentEntity = appointmentRepository.findById(appointmentId);
        if (!appointmentEntity.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.APPOINTMENT_NOT_FOUND);
        }
        return appointmentEntity.get();
    }

    @Override
    public ResponseEntity<AppointmentResponseDTO> fetchAppointmentByAppointmentId(Long appointmentId) {
        return ResponseEntity.ok(new AppointmentResponseDTO(findAppointmentById(appointmentId)));
    }

    @Override
    public ResponseEntity<List<FrequentvisitResponseDTO>> fetchFrequentVisitList(Long customerId) {
        List<FrequentDTO> frequentDTOList = new ArrayList<>();
        List<FrequentvisitResponseDTO> frequentvisitResponseDTOList = new ArrayList<>();
        List<FrequentDTO> findEmployeeFrequentVisitList = appointmentRepository.findEmployeeFrequentVisitList(customerId);
        List<FrequentDTO> findBranchFrequentVisitList = appointmentRepository.findBranchFrequentVisitList(customerId);
        frequentDTOList.addAll(findEmployeeFrequentVisitList);
        frequentDTOList.addAll(findBranchFrequentVisitList);
        if (frequentDTOList.size() > 0) {
            frequentDTOList = frequentDTOList.stream().sorted(Comparator.comparing(FrequentDTO::getCount).reversed()).collect(Collectors.toList());
            frequentDTOList.forEach(frequentVisit -> {
                if(frequentVisit.getEmployeeId() != null) {
                    frequentvisitResponseDTOList.add(new FrequentvisitResponseDTO(findEmployeeById(frequentVisit.getEmployeeId())));
                } else {
                    frequentvisitResponseDTOList.add(new FrequentvisitResponseDTO(findBranchById(frequentVisit.getBranchId())));
                }
            });
        }
        return ResponseEntity.ok(frequentvisitResponseDTOList);
    }

    @Override
    public ResponseEntity<Object> fetchAppointmentByDateAndTime(AppointmentListRequestDTO appointmentListRequestDTO) {
        try {
            Optional<AppointmentEntity> appointmentEntityOptional = appointmentRepository.findByAppointmentStatusIdAndAppointmentDateAndStartTime(findAppointmentStatusById(1),appointmentListRequestDTO.getDate(), appointmentListRequestDTO.getStartTime());
            if(appointmentEntityOptional.isPresent()) {
                return ResponseEntity.ok(new ResponseDTO(ResponseMessages.SUCCESS, true, new AppointmentResponseDTO(appointmentEntityOptional.get()), 1L));
            } else {
                return ResponseEntity.ok(new com.aptmgt.commons.dto.MessageDTO(true, ResponseMessages.APPOINTMENT_NOT_EXISTS));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }
}
