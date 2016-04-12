package com.softserve.edu.service.tool.impl;

import com.softserve.edu.common.Constants;
import com.softserve.edu.documents.FileFactory;
import com.softserve.edu.documents.parameter.FileFormat;
import com.softserve.edu.documents.parameter.FileParameters;
import com.softserve.edu.documents.parameter.FileSystem;
import com.softserve.edu.documents.resources.DocumentType;
import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.enumeration.organization.OrganizationType;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.repository.OrganizationRepository;
import com.softserve.edu.repository.VerificationRepository;
import com.softserve.edu.service.admin.OrganizationService;
import com.softserve.edu.service.provider.ProviderEmployeeService;
import com.softserve.edu.service.tool.ReportsService;
import com.softserve.edu.service.utils.export.TableExportColumn;
import org.apache.commons.vfs2.FileObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Service for reports generation.
 */
@Service
@Transactional(readOnly = true)
public class ReportsServiceImpl implements ReportsService {

    @Autowired
    private ProviderEmployeeService providerEmployeeService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private VerificationRepository verificationRepository;

    private Logger logger = Logger.getLogger(ReportsServiceImpl.class);

    public FileObject buildFile(Long providerId, DocumentType documentType,
                                FileFormat fileFormat) throws Exception {
        FileParameters fileParameters = new FileParameters(documentType, fileFormat);
        fileParameters.setFileSystem(FileSystem.RAM);
        fileParameters.setFileName(documentType.toString());
        List<TableExportColumn> data;
        switch (documentType) {
            case PROVIDER_EMPLOYEES_REPORTS:
                data = getDataForProviderEmployeesReport(providerId);
                break;
            case PROVIDER_CALIBRATORS_REPORTS:
                data = getDataForProviderCalibratorsReport(providerId);
                break;
            case PROVIDER_VERIFICATION_RESULT_REPORTS:
                data = getDataForProviderVerificationResultReport(providerId);
                break;
            default:
                throw new IllegalArgumentException(documentType.name() + "is not supported");
        }
        return FileFactory.buildReportFile(data, fileParameters);
    }

    public List<TableExportColumn> getDataForProviderEmployeesReport(Long providerId) {
        List<User> users = providerEmployeeService.getAllProviderEmployee(providerId);

        List<TableExportColumn> data = new ArrayList<>();
        List<String> employeeFullName = new ArrayList<>();
        List<String> acceptedVerifications = new ArrayList<>();
        List<String> rejectedVerifications = new ArrayList<>();
        List<String> allVerifications = new ArrayList<>();
        List<String> doneSuccess = new ArrayList<>();
        List<String> doneFailed = new ArrayList<>();
        for (User user : users) {
            employeeFullName.add(user.getFirstName() + " " + user.getMiddleName() + " " + user.getLastName());

            acceptedVerifications.add(verificationRepository.countByProviderEmployeeUsernameAndStatus(
                    user.getUsername(), Status.ACCEPTED).toString());
            rejectedVerifications.add(verificationRepository.countByProviderEmployeeUsernameAndStatus(
                    user.getUsername(), Status.REJECTED).toString());
            Long done = verificationRepository.countByProviderEmployeeUsernameAndStatus(
                    user.getUsername(), Status.TEST_OK);
            doneSuccess.add(done.toString());
            Long failed = verificationRepository.countByProviderEmployeeUsernameAndStatus(
                    user.getUsername(), Status.TEST_NOK);
            doneFailed.add(failed.toString());
            Long all = done + failed;
            allVerifications.add(all.toString());
        }
        data.add(new TableExportColumn(Constants.FULL_NAME, employeeFullName));
        data.add(new TableExportColumn(Constants.COUNT_ACCEPTED_VER, acceptedVerifications));
        data.add(new TableExportColumn(Constants.COUNT_REJECTED_VER, rejectedVerifications));
        data.add(new TableExportColumn(Constants.COUNT_ALL_VERIFICATIONS, allVerifications));
        data.add(new TableExportColumn(Constants.COUNT_OK_VERIFICATIONS, doneSuccess));
        data.add(new TableExportColumn(Constants.COUNT_NOK_VERIFICATIONS, doneFailed));

        return data;
    }

    public List<TableExportColumn> getDataForProviderCalibratorsReport(Long providerId) {
        List<TableExportColumn> data = new ArrayList<>();

        Set<Device.DeviceType> deviceTypes = organizationRepository.findOne(providerId).getDeviceTypes();
        HashSet<Organization> calibrators = new HashSet<>();
        for (Device.DeviceType deviceType : deviceTypes) {
            calibrators.addAll(organizationService.findByIdAndTypeAndActiveAgreementDeviceType(providerId, OrganizationType.CALIBRATOR, deviceType));
        }
        List<Organization> calibratorsList = new ArrayList<>();
        calibratorsList.addAll(calibrators);

        List<String> calibratorsNames = new ArrayList<>();
        List<String> allVerifications = new ArrayList<>();
        List<String> successVerifications = new ArrayList<>();
        List<String> unsuccessfulVerifications = new ArrayList<>();

        for (Organization calibrator : calibratorsList) {
            calibratorsNames.add(calibrator.getName());
            allVerifications.add(verificationRepository.countByCalibratorId(calibrator.getId()).toString());
            successVerifications.add(verificationRepository.countByCalibratorIdAndStatus(calibrator.getId(), Status.TEST_OK).toString());
            unsuccessfulVerifications.add(verificationRepository.countByCalibratorIdAndStatus(calibrator.getId(), Status.TEST_NOK).toString());
        }

        data.add(new TableExportColumn(Constants.CALIBRATOR_ORGANIZATION_NAME, calibratorsNames));
        data.add(new TableExportColumn(Constants.COUNT_ALL_VERIFICATIONS, allVerifications));
        data.add(new TableExportColumn(Constants.COUNT_OK_VERIFICATIONS, successVerifications));
        data.add(new TableExportColumn(Constants.COUNT_NOK_VERIFICATIONS, unsuccessfulVerifications));

        return data;
    }

    /**
     * Prepares data for report "Звіт 3".
     *
     * @param providerId id of the provider
     * @return Data to use with XlsTableExporter
     */
    public List<TableExportColumn> getDataForProviderVerificationResultReport(Long providerId) {
        Organization provider = organizationRepository.findOne(providerId);
        List<Verification> verifications = verificationRepository.findByProvider(provider);
        // TODO: findByProviderAndInitialDateBetween

        List<String> number = new ArrayList<>();
        List<String> customerSurname = new ArrayList<>();
        List<String> customerName = new ArrayList<>();
        List<String> customerMiddleName = new ArrayList<>();
        List<String> cities = new ArrayList<>();
        List<String> regions = new ArrayList<>();
        List<String> streets = new ArrayList<>();
        List<String> buildings = new ArrayList<>();
        List<String> flats = new ArrayList<>();
        List<String> phones = new ArrayList<>();
        List<String> stamps = new ArrayList<>();
        List<String> dateSignatures = new ArrayList<>();
        List<String> deviceNumbers = new ArrayList<>();
        List<String> counterNumbers = new ArrayList<>();
        List<String> counterTypes = new ArrayList<>();
        List<String> counterTypeSizes = new ArrayList<>();
        List<String> years = new ArrayList<>();
        List<String> accumulatedVolumes = new ArrayList<>();
        List<String> temperatures = new ArrayList<>();
        List<String> verificationStatus = new ArrayList<>();
        List<String> verificationNumbers = new ArrayList<>();
        List<String> documentDate = new ArrayList<>();
        List<String> documentNumber = new ArrayList<>();
        List<String> validUntil = new ArrayList<>();


        Integer i = 1;
        for (Verification verification : verifications) {
            number.add(String.valueOf(i));
            String empty = " ";

            if (verification.getClientData() != null) {
                customerSurname.add(verification.getClientData().getLastName() != null ? verification.getClientData().getLastName() : empty);
                customerName.add(verification.getClientData().getFirstName() != null ? verification.getClientData().getFirstName() : empty);
                customerMiddleName.add(verification.getClientData().getMiddleName() != null ? verification.getClientData().getMiddleName() : empty);
                phones.add(verification.getClientData().getPhone() != null ? verification.getClientData().getPhone() : empty);

                if (verification.getClientData().getClientAddress() != null) {
                    cities.add(getCitiesFromAddress(verification.getClientData().getClientAddress()));
                    regions.add(getRegionsFromAddress(verification.getClientData().getClientAddress()));
                    streets.add(getStreetFromAddress(verification.getClientData().getClientAddress()));
                    buildings.add(getBuildingFromAddress(verification.getClientData().getClientAddress()));
                    flats.add(getFlatFromAddress(verification.getClientData().getClientAddress()));
                } else {
                    cities.add(empty);
                    regions.add(empty);
                    streets.add(empty);
                    buildings.add(empty);
                    flats.add(empty);
                }
            }

            stamps.add(getStampsFromCounter(verification));
            dateSignatures.add(getSignDateInString(verification));
            deviceNumbers.add(getDeviceNumberFromVerification(verification));
            counterNumbers.add(getCounterNumberFromVerification(verification));
            counterTypes.add(getCounterTypeFromVerification(verification));
            counterTypeSizes.add(getCounterTypeSizeFromVerification(verification));
            years.add(getRealiseYearFromVerification(verification));
            accumulatedVolumes.add(getAccumulatedVolumeFromVerification(verification));
            temperatures.add(getTemperaturesFromVerification(verification));
            verificationNumbers.add(verification.getId());
            verificationStatus.add(getStatusFromVerification(verification));
            documentDate.add(getDocumentDateInString(verification));
            documentNumber.add(getDocumentNuberFromVerification(verification));
            validUntil.add(getValidDateInString(verification));
            ++i;
        }

        // region Fill map

        List<TableExportColumn> data = new ArrayList<>();
        data.add(new TableExportColumn(Constants.NUMBER_IN_SEQUENCE_SHORT, number));
        data.add(new TableExportColumn(Constants.CUSTOMER_SURNAME, customerSurname));
        data.add(new TableExportColumn(Constants.CUSTOMER_NAME, customerName));
        data.add(new TableExportColumn(Constants.CUSTOMER_MIDDLE_NAME, customerMiddleName));
        data.add(new TableExportColumn(Constants.CITY, cities));
        data.add(new TableExportColumn(Constants.REGION, regions));
        data.add(new TableExportColumn(Constants.STREET, streets));
        data.add(new TableExportColumn(Constants.BUILDING, buildings));
        data.add(new TableExportColumn(Constants.FLAT, flats));
        data.add(new TableExportColumn(Constants.PHONE_NUMBER, phones));
        data.add(new TableExportColumn(Constants.STAMP, stamps));
        data.add(new TableExportColumn(Constants.DATE_SIGNATURE, dateSignatures));
        data.add(new TableExportColumn(Constants.DEVICE_NUMBER, deviceNumbers));
        data.add(new TableExportColumn(Constants.COUNTERS_NUMBER, counterNumbers));
        data.add(new TableExportColumn(Constants.COUNTER_TYPE, counterTypes));
        data.add(new TableExportColumn(Constants.COUNTER_TYPE_SIZE, counterTypeSizes));
        data.add(new TableExportColumn(Constants.YEAR, years));
        data.add(new TableExportColumn(Constants.COUNTER_ACCUMULATED_VOLUME, accumulatedVolumes));
        data.add(new TableExportColumn(Constants.TEMPERATURE, temperatures));
        data.add(new TableExportColumn(Constants.VERIFICATION_NUMBER, verificationNumbers));
        data.add(new TableExportColumn(Constants.VERIFICATION_STATUS, verificationStatus));
        data.add(new TableExportColumn(Constants.DOCUMENT_DATE, documentDate));
        data.add(new TableExportColumn(Constants.DOCUMENT_NUMBER, documentNumber));
        data.add(new TableExportColumn(Constants.VALID_UNTIL, validUntil));

        // endregion

        return data;
    }

    public String getCitiesFromAddress(Address address) {
        if (address.getRegion() != null & address.getDistrict() != null) {
            if (address.getRegion().equals(Constants.KYIV_CITY_NAME.substring(2))) {
                return Constants.KYIV_CITY_NAME;
            } else {
                return address.getDistrict();
            }
        }
        return " ";
    }

    public String getRegionsFromAddress(Address address) {
        return (address.getRegion() != null ? address.getRegion() : " ");
    }

    public String getStreetFromAddress(Address address) {
        return (address.getStreet() != null ? address.getStreet() : " ");
    }

    public String getBuildingFromAddress(Address address) {
        return (address.getBuilding() != null ? address.getBuilding() : " ");
    }

    public String getFlatFromAddress(Address address) {
        return (address.getFlat() != null ? address.getFlat() : " ");
    }

    public String getStampsFromCounter(Verification verification) {
        return (verification.getCounter() != null && verification.getCounter().getStamp() != null ? verification.getCounter().getStamp() : " ");
    }

    public String getSignDateInString(Verification verification) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return dateFormat.format(verification.getSignProtocolDate());
        } catch (Exception e) {
            logger.error("Wrong date format in database, verification Id: " + verification.getId(), e);
            return " ";
        }
    }

    public String getDeviceNumberFromVerification(Verification verification) {
        return (verification.getDevice() != null && verification.getDevice().getNumber() != null ? verification.getDevice().getNumber() : " ");
    }

    public String getCounterNumberFromVerification(Verification verification) {
        return (verification.getCounter() != null && verification.getCounter().getNumberCounter() != null ? verification.getCounter().getNumberCounter() : " ");
    }

    public String getCounterTypeFromVerification(Verification verification) {
        return (verification.getCounter() != null && verification.getCounter().getCounterType() != null &&
                verification.getCounter().getCounterType().getStandardSize() != null ? verification.getCounter().getCounterType().getStandardSize() : " ");
    }

    public String getCounterTypeSizeFromVerification(Verification verification) {
        return (verification.getCounter() != null && verification.getCounter().getCounterType() != null &&
                verification.getCounter().getCounterType().getSymbol() != null ? verification.getCounter().getCounterType().getSymbol() : " ");
    }

    public String getRealiseYearFromVerification(Verification verification) {
        return (verification.getCounter() != null && verification.getCounter().getReleaseYear() != null ? verification.getCounter().getReleaseYear() : " ");
    }

    public String getAccumulatedVolumeFromVerification(Verification verification) {
        return (verification.getCounter() != null && verification.getCounter().getAccumulatedVolume() != null ? verification.getCounter().getAccumulatedVolume() : " ");
    }

    public String getTemperaturesFromVerification(Verification verification) {
        return " ";
    }

    public String getStatusFromVerification(Verification verification) {
        return (verification.getStatus().equals(Status.TEST_NOK) ? Constants.STATUS_TEST_OK : Constants.STATUS_ELSE);
    }

    public String getDocumentDateInString(Verification verification) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        if (verification.getStateVerificator() != null && verification.getStateVerificator().getAdditionInfoOrganization() != null &&
                verification.getStateVerificator().getAdditionInfoOrganization().getCertificateDate() != null) {
            try {
                return dateFormat.format(verification.getStateVerificator().getAdditionInfoOrganization().getCertificateDate());
            } catch (IllegalArgumentException e) {
                logger.error("Wrong date format of certificate date in database, verification Id: " + verification.getId(), e);
                return " ";
            }
        } else {
            return " ";
        }
    }

    public String getDocumentNuberFromVerification(Verification verification) {
        return (verification.getStateVerificator() != null && verification.getStateVerificator().getAdditionInfoOrganization() != null &&
                verification.getStateVerificator().getAdditionInfoOrganization().getCertificateNumberAuthorization() != null ?
                verification.getStateVerificator().getAdditionInfoOrganization().getCertificateNumberAuthorization() : " ");
    }

    public String getValidDateInString(Verification verification) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        if (verification.getExpirationDate() != null) {
            try {
                return dateFormat.format(verification.getExpirationDate());
            } catch (IllegalArgumentException e) {
                logger.error("Wrong date format of validate date in database, verification Id: " + verification.getId(), e);
                return " ";
            }
        } else {
            return " ";
        }
    }

}
