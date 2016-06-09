package com.softserve.edu.service.utils;

import com.softserve.edu.entity.verification.calibration.CalibrationTask;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

public enum SortCriteriaCalibrationTask {
    DATE() {
        public Order getSortOrder(Root<CalibrationTask> root, CriteriaBuilder cb, String sortOrder) {

            if (sortOrder.equalsIgnoreCase("asc")) {
                return cb.asc(root.get("initialDate"));
            } else {
                return cb.desc(root.get("initialDate"));
            }
        }
    };

    public Order getSortOrder(Root<CalibrationTask> root, CriteriaBuilder cb, String sortOrder) {
        return null;
    }
}
