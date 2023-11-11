package com.aptmgt.admin.scheduler;

import com.aptmgt.admin.services.IAppointmentService;
import com.aptmgt.commons.model.AppointmentEntity;
import com.aptmgt.commons.model.AppointmentStatusEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@Component
public class SchedulerServiceImpl implements SchedulerService{

    @Autowired
    private IAppointmentService appointmentService;

    private final Logger LOGGER = LoggerFactory.getLogger(SchedulerServiceImpl.class);

    @Scheduled(cron = "0 */15 * * * ?")
    @Override
    public void abandonedAppointments() {
        LOGGER.info("Fetch abandoned appointments and update status");
        // TODO: Refactor the below appointment status set method with enum value ...
        AppointmentStatusEntity appointmentStatusEntity = new AppointmentStatusEntity(3, "Abandoned");
        List<AppointmentEntity> abandonedAppointments = appointmentService.abandonedAppointmentsList();
        if (abandonedAppointments.size() > 0) {
            abandonedAppointments.forEach(a -> a.setAppointmentStatusId(appointmentStatusEntity));
            LOGGER.info("{} Abandoned appointments found and updated", abandonedAppointments.size());
            appointmentService.saveAllAppointments(abandonedAppointments);
        }
    }

}
