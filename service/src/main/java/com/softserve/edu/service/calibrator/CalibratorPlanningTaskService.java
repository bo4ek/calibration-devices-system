package com.softserve.edu.service.calibrator;

import com.softserve.edu.entity.device.CounterType;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.CalibrationTask;
import com.softserve.edu.service.exceptions.InvalidModuleSerialNumberException;
import com.softserve.edu.service.exceptions.PermissionDeniedException;
import com.softserve.edu.service.utils.ListToPageTransformer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;


public interface CalibratorPlanningTaskService {

    Page<CalibrationTask> getFilteredPageOfCalibrationTasks(Map<String, String> filterParams,
                                                            Pageable pageable, String username);

    ListToPageTransformer<CalibrationTask> findPageOfCalibrationTasks(int pageNumber, int itemsPerPage, String startDateToSearch, String endDateToSearch,
                                                                      String name, String leaderFullName,
                                                                      String leaderPhone, String sortCriteria,
                                                                      String sortOrder, User calibratorEmployee, Boolean allTests);

    Page<CalibrationTask> findAllCalibrationTasks(Pageable pageable);

    Boolean addNewTaskForStation(Date taskDate, String serialNumber, List<String> verificationsId, String userId) throws PermissionDeniedException, InvalidModuleSerialNumberException;

    Boolean addNewTaskForTeam(Date taskDate, String serialNumber, List<String> verificationsId, String userId) throws PermissionDeniedException;

    long findVerificationsByCalibratorEmployeeAndTaskStatusCount(String userName);

    Page<Verification> findByTaskStatusAndCalibratorId(Long Id, int pageNumber,
                                                       int itemsPerPage, String sortCriteria, String sortOrder);

    List<CounterType> findSymbolsAndSizes(String verifId);

    void sendTaskToStation(Long id, String senderUsername) throws Exception;

    void changeTaskDate(Long taskID, Date dateOfTask);

    @Transactional
    void sendTaskToTeam(Long id, String senderUsername) throws Exception;

    CalibrationTask findOneById(Long id);
}
