package com.softserve.edu.documents.document;

import com.softserve.edu.common.Constants;
import com.softserve.edu.documents.document.meta.Placeholder;
import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.verification.ClientData;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.user.User;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Represents a base certificate and consists of common columns and methods
 * of all certificates.
 * All certificates extend this class.
 * Methods of this class are being called through reflection in MetaDataReader
 */
public abstract class BaseCertificate implements Document {
    protected Logger logger = Logger.getLogger(BaseCertificate.class);
    /**
     * Verification that is used for getting information for this document.
     */
    protected Verification verification;

    /**
     * Calibration test that is assigned to the verification of a counter.
     */
    protected CalibrationTest calibrationTest;

    /**
     * Constructor
     *
     * @param verification    Verification that is used for getting information for this document.
     * @param calibrationTest Calibration test that is assigned to the verification of a counter.
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
        try {
            return verification.getStateVerificator().getName();
        } catch (Exception e) {
            logger.error("Vereficator's name has not been specified ", e);
            return Constants.NOT_SPECIFIED;
        }
    }

    @Placeholder(name = "VERIFICATOR_SUBORDINATION")
    public String getStateVerificatorSubordination() {
        try {
            return verification.getStateVerificator().getAdditionInfoOrganization().getSubordination();
        } catch (Exception e) {
            logger.error("Vereficator's subordination has not been specified ", e);
            return Constants.NOT_SPECIFIED;
        }
    }

    /**
     * @return the calibrator's address.
     */
    @Placeholder(name = "VERIFICATOR_COMPANY_ADDRESS")
    public String getCalibratorCompanyAddress() {
        try {
            Address address = verification.getStateVerificator().getAddress();
            return String.join(", ", Constants.KYIV_CITY_NAME.substring(3).substring(0, 3) + (address.getLocality() != null && address.getLocality().substring(0, 4).equals(Constants.KYIV_CITY_NAME.substring(3)) ?
                    address.getLocality().substring(0, 4) : address.getLocality()), address.getStreet(), address.getBuilding());
        } catch (Exception e) {
            logger.error("Vereficator's address has not been specified " + e);
            return Constants.NOT_SPECIFIED;
        }
    }

    /**
     * @return the calibrator company's certificate identification number.
     */
    @Placeholder(name = "VERIFICATOR_ACC_CERT_NAME")
    public String getVerificatorCompanyAccreditationCertificateNumberAuthorization() {
        try {
            return verification.getStateVerificator().getAdditionInfoOrganization().getCertificateNumberAuthorization();
        } catch (Exception e) {
            logger.error("Vereficator's certificate number has not been specified ", e);
            return Constants.NOT_SPECIFIED;
        }
    }

    /**
     * @return the date when the verificator company received the certificate, that allows it to provide verifications
     */
    @Placeholder(name = "VERIFICATOR_ACC_CERT_DATE_GRANTED")
    public String getVerificatorCompanyAccreditationCertificateDate() {
        try {
            return new SimpleDateFormat(Constants.DAY_FULL_MONTH_YEAR, new Locale("uk", "UA"))
                    .format(verification.getStateVerificator().getAdditionInfoOrganization().getCertificateDate());
        } catch (Exception e) {
            logger.error("Vereficator's certificate granted date has not been specified ", e);
            return Constants.NOT_SPECIFIED;
        }
    }

    /**
     * @return Returns the identification number of the verification
     */
    @Placeholder(name = "VERIFICATION_CERTIFICATE_NUMBER")
    public String getVerificationCertificateNumber() {
        try {
            String verificationId = verification.getId();
            String subdivisionId = verification.getStateVerificatorEmployee().getVerificatorSubdivision().getId();
            return String.format("%s-%s", subdivisionId, verificationId);
        } catch (Exception e) {
            logger.error("Subdivision for this verification has not been specified ", e);
            return Constants.NOT_SPECIFIED;
        }
    }

    /**
     * @return the device's name
     */
    @Placeholder(name = "DEV_NAME")
    public String getDeviceName() {
        try {
            return verification.getCounter().getCounterType().getDevice().getDeviceName();
        } catch (Exception e) {
            logger.error("Device name for this counter type has not been specified ", e);
            return Constants.NOT_SPECIFIED;
        }
    }

    /**
     * @return the device's sign
     */
    @Placeholder(name = "DEV_SIGN")
    public String getDeviceSign() {
        try {
            return verification.getCounter().getCounterType().getSymbol();
        } catch (Exception e) {
            logger.error("Symbol for counterType had not been specified ", e);
            return Constants.NOT_SPECIFIED;
        }
    }

    @Placeholder(name = "ERROR")
    public String getError() {
        try {
            Device.DeviceType type = verification.getCounter().getCounterType().getDevice().getDeviceType();
            if (type.equals(Device.DeviceType.THERMAL)) {
                return Constants.ERROR_FOR_TERMAL;
            } else {
                if (type.equals(Device.DeviceType.WATER)) {
                    return Constants.ERROR_FOR_WATER;
                } else {
                    return Constants.NOT_SPECIFIED;
                }
            }
        } catch (Exception e) {
            logger.error("Symbol for counterType had not been specified ", e);
            return Constants.NOT_SPECIFIED;
        }
    }

    @Placeholder(name = "DEV_STANDARD_SIZE")
    public String getDeviceStandardSize() {
        try {
            return verification.getCounter().getCounterType().getStandardSize();
        } catch (Exception e) {
            logger.error("Standard size for counterType had not been specified ", e);
            return Constants.NOT_SPECIFIED;
        }
    }

    /**
     * @return the device's manufacturer serial number
     */
    @Placeholder(name = "DEV_MAN_SER")
    public String getDeviceManufacturerSerial() {
        try {
            return verification.getCounter().getNumberCounter();
        } catch (Exception e) {
            logger.error("Counter number for this counter has not been specified ", e);
            return Constants.NOT_SPECIFIED;
        }
    }

    /**
     * @return the device's manufacturer name
     */
    @Placeholder(name = "MAN_NAME")
    public String getManufacturerName() {
        try {
            return verification.getCounter().getCounterType().getManufacturer();
        } catch (Exception e) {
            logger.error("Manufacturer for this counter type has not been specified ", e);
            return Constants.NOT_SPECIFIED;
        }
    }

    /**
     * @return Owner's full name - surName + name + middleName
     */
    @Placeholder(name = "OWNER_NAME")
    public String getOwnerFullName() {
        try {
            ClientData ownerData = verification.getClientData();
            return String.join(" ", ownerData.getLastName(), ownerData.getFirstName(), ownerData.getMiddleName());
        } catch (Exception e) {
            logger.error("Data about owner has not been specified ", e);
            return Constants.NOT_SPECIFIED;
        }
    }

    /**
     * @return Address of counter's owner
     */
    @Placeholder(name = "OWNER_ADDRESS")
    public String getOwnerAddress() {
        try {
            Address ownerAddress = verification.getClientData().getClientAddress();
            String region = ownerAddress.getRegion();
            region = (region == null) ? "" : region + " обл., ";
            return region
                    + ownerAddress.getDistrict() + " р-н, "
                    + ownerAddress.getLocality() + ", "
                    + ownerAddress.getStreet() + " "
                    + ownerAddress.getBuilding() + "/"
                    + ownerAddress.getFlat();
        } catch (Exception e) {
            logger.error("Address for this client has not been set ", e);
            return Constants.NOT_SPECIFIED;
        }
    }

    /**
     * @return the state verificator's name in Surname N.M., where N - first letter of name,
     * M - first letter of middle name.
     */
    @Placeholder(name = "VERIFICATOR_SHORT_NAME")
    public String getStateVerificatorShortName() {
        try {
            User stateVerificatorEmployee = verification.getStateVerificatorEmployee();
            return stateVerificatorEmployee.getLastName() + " "
                    + stateVerificatorEmployee.getFirstName().charAt(0) + "."
                    + stateVerificatorEmployee.getMiddleName().charAt(0) + ".";
        } catch (Exception e) {
            logger.error("Data about User has not been specified ", e);
            return Constants.NOT_SPECIFIED;
        }
    }

    /**
     * @return the date until this verification certificate is effective.
     */
    @Placeholder(name = "EFF_DATE")
    public String getVerificationCertificateEffectiveUntilDate() {
        try {
            return new SimpleDateFormat(Constants.DAY_FULL_MONTH_YEAR, new Locale("uk", "UA"))
                    .format(verification.getExpirationDate()) + " р.";
        } catch (Exception e) {
            logger.error("Calibration interval for this counter type has not been specified ", e);
            return Constants.NOT_SPECIFIED;
        }
    }

    /**
     * @return the date of the test execution
     */
    @Placeholder(name = "PROTOCOL_DATE")
    public String getCalibrationTestDate() {
        try {
            return new SimpleDateFormat(Constants.DAY_FULL_MONTH_YEAR, new Locale("uk", "UA"))
                    .format(verification.getSignProtocolDate()) + " p.";
        } catch (Exception e) {
            logger.error("Date for calibration test has not been specified ", e);
            return Constants.NOT_SPECIFIED;
        }
    }

    /**
     * @return get the sign of the document, which contains the metrological characteristics
     */
    @Placeholder(name = "COUNTER_TYPE_GOST")
    public String getCounterTypeGost() {
        try {
            return verification.getCounter().getCounterType().getGost();
        } catch (Exception e) {
            logger.error("GOST for counterType has not been specified", e);
            return Constants.NOT_SPECIFIED;
        }
    }

    /**
     * @return get the calibration type from module's characteristics, which was used to test counter
     */
    @Placeholder(name = "CALIBRATION_TYPE")
    public String getCalibrationType() {
        try {
            if (verification.isManual()) {
                return verification.getCalibrationTestDataManualId().getCalibrationTestManual().getCalibrationModule().getCalibrationType();
            } else {
                return verification.getCalibrationModule().getCalibrationType();
            }
        } catch (Exception e) {
            logger.error("Calibration type for this module has not been specified ", e);
            return Constants.NOT_SPECIFIED;
        }
    }

    /**
     * @return digital signature for this document
     */
    @Placeholder(name = "SIGNATURE")
    public String getSignature() {
        try {
            return "";
        } catch (Exception e) {
            logger.error("Signature for this document has not been specified ", e);
            return Constants.NOT_SPECIFIED;
        }
    }
}