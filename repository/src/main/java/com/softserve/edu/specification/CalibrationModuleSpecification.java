package com.softserve.edu.specification;

import com.softserve.edu.entity.device.CalibrationModule;
import com.softserve.edu.entity.device.Device;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.Date;

public class CalibrationModuleSpecification {

    public static Specification<CalibrationModule> moduleDeviceType(String applicationFiled) {
        return new Specification<CalibrationModule>() {
            @Override
            public Predicate toPredicate(Root<CalibrationModule> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                Predicate predicate = null;
                if (applicationFiled.equals("WATER")) {
                    predicate = criteriaBuilder.equal(root.get("deviceType"), Device.DeviceType.WATER);
                } else if (applicationFiled.equals("THERMAL")) {
                    predicate = criteriaBuilder.equal(root.get("deviceType"), Device.DeviceType.THERMAL);
                }
                return predicate;
            }
        };
    }

    public static Specification<CalibrationModule> moduleHasOrganization(String organizationCode) {
        return ((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.upper(root.get("organizationCode")),
                organizationCode));
    }

    public static Specification<CalibrationModule> moduleHasCondDesignation(String condDesignation) {
        return ((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.upper(root.get("condDesignation")), condDesignation));
    }

    public static Specification<CalibrationModule> moduleHasSerialNumber(String serialNumber) {
        return ((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.upper(root.get("serialNumber")), serialNumber));
    }

    public static Specification<CalibrationModule> moduleHasEmployeeFullName(String employeeFullName) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.upper(root.get("employeeFullName")), employeeFullName);
    }

    public static Specification<CalibrationModule> moduleHasTelephone(String phoneNumber) {
        return ((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.upper(root.get("telephone")), phoneNumber));
    }

    public static Specification<CalibrationModule> moduleHasType(String moduleType) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.upper(root.get("moduleType")), moduleType);
    }

    public static Specification<CalibrationModule> moduleHasEmail(String email) {
        return ((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.upper(root.get("email")), email));
    }

    public static Specification<CalibrationModule> moduleHasCalibrationType(String calibratinType) {
        return ((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.upper(root.get("calibrationType")), calibratinType));
    }

    public static Specification<CalibrationModule> moduleHasCalibratorId(Long calibratorId) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("organization").get("id"),
                calibratorId);
    }


    public static Specification<CalibrationModule> moduleIsActiveOrNot(boolean moduleIsActiveOrNot) {
        return ((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
                .equal(root.get("isActive"), moduleIsActiveOrNot));
    }

    public static Specification<CalibrationModule> moduleHasWorkDate(Date workDate) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
                .lessThan(root.get("workDate"), workDate);
    }


}
