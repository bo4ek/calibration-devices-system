package com.softserve.edu.documents.document;

import com.softserve.edu.common.Constants;
import com.softserve.edu.documents.document.meta.Placeholder;
import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.verification.ClientData;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.user.User;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Represents a base certificate and consists of common columns and methods
 * of all certificates.
 * All certificates extend this class.
 */

@Service
@Configurable
public abstract class BaseCertificate implements Document {
    private Logger logger = Logger.getLogger(BaseCertificate.class);

    /**
     * Verification that is used for getting information for this document.
     */
    protected Verification verification;
    /**
     * One of calibration test that is assigned to the verification.
     */
    protected CalibrationTest calibrationTest;

    /**
     * Constructor
     *
     * @param verification    entity to get document's data from.
     * @param calibrationTest one of calibration test that is assigned to the verification
     */
    public BaseCertificate(Verification verification, CalibrationTest calibrationTest) {
        this.verification = verification;
        this.calibrationTest = calibrationTest;
    }

    /**
     * @return the state verificator company's name.
     */
    @Placeholder(name = "VERIFICATOR_COMPANY_NAME")
    public String getStateVerificatorCompanyName() {
        return verification.getStateVerificator().getName();
    }

    /**
     * @return the calibrator's address.
     */
    @Placeholder(name = "VERIFICATOR_COMPANY_ADDRESS")
    public String getCalibratorCompanyAddress() {
        Address address = verification.getStateVerificator().getAddress();
        return address.getLocality() + ", " +
                address.getStreet() + ", " +
                address.getBuilding();
    }

    /**
     * @return the calibrator company's certificate identification number.
     */
    @Placeholder(name = "VERIFICATOR_ACC_CERT_NAME")
    public String getCalibratorCompanyAccreditationCertificateNumber() {
        return verification.getStateVerificator().getCertificateNumber();
    }

    /**
     * @return the date when the verificator company received the certificate, that allows it to provide verifications
     */
    @Placeholder(name = "VERIFICATOR_ACC_CERT_DATE_GRANTED")
    public String getCalibratorCompanyAccreditationCertificateGrantedDate() {
        return new SimpleDateFormat(Constants.DAY_FULL_MONTH_YEAR, new Locale("uk", "UA")).format(verification.getStateVerificator().getCertificateGrantedDate());
    }

    /**
     * @return Returns the identification number of the team, that was making verification
     */
    @Placeholder(name = "VERIFICATION_CERTIFICATE_NUMBER")
    public String getVerificationCertificateNumber() {
        String verificationId = String.valueOf(verification.getId());
        String subdivisionId = "***";
        try {
            subdivisionId = String.valueOf(verification.getTask().getTeam().getId());
        } catch (Exception e) {
            logger.error("no team found for this verification");
        }

        return String.format("%s-%s-Д", subdivisionId, verificationId);
    }

    /**
     * @return the device's name
     */
    //mock
    @Placeholder(name = "DEV_NAME")
    public String getDeviceName() {
        String manufacturer = verification.getCounter().getCounterType().getDevice().getDeviceName();
        if (manufacturer != null) {
            return verification.getCounter().getCounterType().getDevice().getDeviceName();
        } else {
            return "не вказано";
        }
    }

    /**
     * @return the device's sign
     */
    @Placeholder(name = "DEV_SIGN")
    public String getDeviceSign() {
        return verification.getCounter().getCounterType().getSymbol();
    }

    /**
     * @return the device's manufacturer serial number
     */
    @Placeholder(name = "DEV_MAN_SER")
    public String getDeviceManufacturerSerial() {
        String manufacturerSerial = verification.getCounter().getNumberCounter();
        if (manufacturerSerial != null) {
            return verification.getCounter().getNumberCounter();
        } else {
            return "не вказано";
        }
    }

    /**
     * @return the device's manufacturer name
     */
    @Placeholder(name = "MAN_NAME")
    public String getManufacturerName() {
        String manufecturerName = verification.getCounter().getCounterType().getManufacturer();
        if (manufecturerName != null) {
            return manufecturerName;
        } else {
            return "не вказано";
        }
    }

    /**
     * @return Owner's full name - surName + name + middleName
     */
    @Placeholder(name = "OWNER_NAME")
    public String getOwnerFullName() {
        ClientData ownerData = verification.getClientData();
        return ownerData.getLastName() + " " +
                ownerData.getFirstName() + " " +
                ownerData.getMiddleName();
    }

    /**
     * @return Address of counter's owner
     */
    @Placeholder(name = "OWNER_ADDRESS")
    public String getOwnerAddress() {
        Address ownerAddress = verification.getClientData().getClientAddress();
        String region = ownerAddress.getRegion();
        if (region != null) {
            region = "";
        } else {
            region += " обл., ";
        }
        return region
                + ownerAddress.getDistrict() + " р-н, "
                + ownerAddress.getLocality() + ", "
                + ownerAddress.getStreet() + " "
                + ownerAddress.getBuilding() + "/"
                + ownerAddress.getFlat();
    }

    /**
     * @return the state verificator's name in Surname N.M., where N - first letter of name,
     * M - first letter of middle name.
     */
    @Placeholder(name = "VERIFICATOR_SHORT_NAME")
    public String getStateVerificatorShortName() {
        User stateVerificatorEmployee = verification.getStateVerificatorEmployee();
        return stateVerificatorEmployee.getLastName() + " "
                + stateVerificatorEmployee.getFirstName().charAt(0) + "."
                + stateVerificatorEmployee.getMiddleName().charAt(0) + ".";
    }

    /**
     * @return the date until this verification certificate is effective.
     */
    @Placeholder(name = "EFF_DATE")
    public String getVerificationCertificateEffectiveUntilDate() {
        return new SimpleDateFormat(Constants.DAY_FULL_MONTH_YEAR, new Locale("uk", "UA")).format(verification.getExpirationDate());
    }

    /**
     * @return the date of the test execution
     */
    @Placeholder(name = "PROTOCOL_DATE")

    public String getCalibrationTestDate() {
        return new SimpleDateFormat(Constants.DAY_FULL_MONTH_YEAR, new Locale("uk", "UA")).format(calibrationTest.getDateTest());
    }

    /**
     * @return get the sign of the document, which contains the metrological characteristics
     */
    @Placeholder(name = "COUNTER_TYPE_GOST")
    public String getCounterTypeGost() {
        String gost = verification.getCounter().getCounterType().getGost();
        if (gost != null) {
            return gost;
        } else {
            return "не вказано";
        }
    }

    /**
     * @return get the calibration type from module's characteristics, which was used to test
     */
    @Placeholder(name = "CALIBRATION_TYPE")
    public String getCalibrationType() {
        String calibrationType = verification.getCalibrationModule().getCalibrationType();
        if (calibrationType != null) {
            return calibrationType;
        } else {
            return "не вказано";
        }
    }
}