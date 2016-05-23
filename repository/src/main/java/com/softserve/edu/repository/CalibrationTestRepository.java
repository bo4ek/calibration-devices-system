package com.softserve.edu.repository;

import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.verification.Verification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalibrationTestRepository extends CrudRepository<CalibrationTest, Long> {

    @Query("select c.name from CalibrationTest c where c.verification.id=:verificationId")
    String findTestNameByVerificationId(@Param("verificationId") String verificationId);

    @Query("select c from CalibrationTest c where c.verification.id=:verificationId")
    CalibrationTest findTestByVerificationId(@Param("verificationId") String verificationId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CalibrationTest c where c.verification.id=:verificationId")
    boolean existByVerificationId(@Param("verificationId") String verificationId);

    List<CalibrationTest> findByName(String name);

    CalibrationTest findById(Long id);

    CalibrationTest findByVerificationId(String verifId);

    CalibrationTest findByVerification(Verification verification);

    public Page<CalibrationTest> findAll(Pageable pageable);

    Page<CalibrationTest> findByNameLikeIgnoreCase(String name, Pageable pageable);

}