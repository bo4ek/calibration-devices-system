package com.softserve.edu.repository;

import com.softserve.edu.entity.verification.BbiProtocol;
import com.softserve.edu.entity.verification.Verification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadBbiRepository extends CrudRepository<BbiProtocol, Long> {

    @Query("SELECT b.fileName FROM BbiProtocol b WHERE b.verification.id=:verificationId")
    String findFileNameByVerificationId(@Param("verificationId") String verificationId);

    @Query("SELECT b.verification FROM BbiProtocol b WHERE b.fileName=:fileName")
    String findVerificationIdByFileName(@Param("fileName") String fileName);

    @Query("SELECT b FROM BbiProtocol b INNER JOIN b.verification v INNER join v.calibrationModule m WHERE b.fileName = :fileName " +
            "AND v.id = b.verification.id AND v.verificationTime = :date AND m.id = v.calibrationModule.id AND m.moduleNumber = :moduleNumber")
    List<BbiProtocol> findBBIProtocolByFileNameAndDateAndModuleNumber(@Param("fileName") String fileName, @Param("date") String date,
                                                                @Param("moduleNumber") String moduleNumber);

    @Query("SELECT b FROM BbiProtocol b WHERE b.verification=:verification")
    BbiProtocol findByVerification(Verification verification);

    @Query("SELECT b FROM BbiProtocol b WHERE b.verification.id=:verificationId")
    List<BbiProtocol> findFileByVerificationId(@Param("verificationId") String verificationId);

    BbiProtocol findOne(Long id);
}
