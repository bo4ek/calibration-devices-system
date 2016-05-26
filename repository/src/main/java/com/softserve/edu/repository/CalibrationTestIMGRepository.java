package com.softserve.edu.repository;

import com.softserve.edu.entity.verification.calibration.CalibrationTestIMG;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CalibrationTestIMGRepository extends CrudRepository<CalibrationTestIMG, Long>{

    @Query("select i.imgName from CalibrationTestIMG i where i.calibrationTestData.id=:calibrationTestDataId")
    String findImgNameByCalibrationTestId(@Param("calibrationTestDataId") String calibrationTestDataId);

    @Query("select i from CalibrationTestIMG i where i.calibrationTestData.id=:calibrationTestDataId")
    CalibrationTestIMG findImgByCalibrationTestId(@Param("calibrationTestDataId") Long calibrationTestDataId);
}
