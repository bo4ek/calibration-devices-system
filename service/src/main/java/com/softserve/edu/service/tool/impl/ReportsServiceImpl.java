package com.softserve.edu.service.tool.impl;

import com.softserve.edu.common.Constants;
import com.softserve.edu.documents.FileFactory;
import com.softserve.edu.documents.parameter.FileFormat;
import com.softserve.edu.documents.parameter.FileParameters;
import com.softserve.edu.documents.parameter.FileSystem;
import com.softserve.edu.documents.resources.DocumentType;
import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.device.CalibrationModule;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.enumeration.organization.OrganizationType;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.organization.Agreement;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.repository.*;
import com.softserve.edu.service.admin.OrganizationService;
import com.softserve.edu.service.provider.ProviderEmployeeService;
import com.softserve.edu.service.tool.ReportsService;
import com.softserve.edu.service.utils.export.TableExportColumn;
import org.apache.commons.vfs2.FileObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Autowired
    private CalibrationTestRepository calibrationTestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CalibrationModuleRepository calibrationModuleRepository;

    @Autowired
    private AgreementRepository agreementRepository;


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
            default:
                throw new IllegalArgumentException(documentType.name() + "is not supported");
        }
        return FileFactory.buildReportFile(data, fileParameters);
    }

    public FileObject buildFileByDate(Long organisationId, DocumentType documentType,
                                      FileFormat fileFormat, String startDate, String endDate) throws Exception {
        FileParameters fileParameters = new FileParameters(documentType, fileFormat);
        fileParameters.setFileSystem(FileSystem.RAM);
        fileParameters.setFileName(documentType.toString());
        List<TableExportColumn> data;
        Date fromDate = getDateForDocument(startDate);
        Date toDate = getDateForDocument(endDate);
        switch (documentType) {
            case PROVIDER_VERIFICATION_RESULT_REPORTS:
                data = getDataForProviderVerificationResultReport(organisationId, fromDate, toDate);
                break;
            case CALIBRATOR_VERIFICATION_RESULT_REPORTS:
                data = getDataForCalibratorVerificationResultReport(organisationId, fromDate, toDate);
                break;
            case VERIFICATOR_VERIFICATION_RESULT_REPORTS:
                data = getDataForVerificatorVerificationResultReport(organisationId, fromDate, toDate, false);
                break;
            case VERIFICATOR_VERIFICATION_RESULT_REPORTS_BY_SIGN_PROTOCOL_DATE:
                data = getDataForVerificatorVerificationResultReport(organisationId, fromDate, toDate, true);
                break;
            case PROVIDER_VERIFICATION_REJECTED_REPORTS:
                data = getDataForProviderRejectedVerificationReport(organisationId, fromDate, toDate);
                break;
            case PROVIDER_REPORTS:
                data = getDataForProviderBackFlowReport(organisationId, fromDate, toDate);
                break;
            default:
                throw new IllegalArgumentException(documentType.name() + "is not supported");
        }
        return FileFactory.buildReportFile(data, fileParameters);
    }

    public FileObject buildFileByDateAndModuleId(Long organisationId, String calibratorId, DocumentType documentType,
                                                 FileFormat fileFormat, String startDate, String endDate) throws Exception {
        Organization organization = organizationRepository.findOne(Long.valueOf(calibratorId));
        FileParameters fileParameters = new FileParameters(documentType, fileFormat);
        fileParameters.setFileSystem(FileSystem.RAM);
        fileParameters.setFileName(documentType.toString());
        List<TableExportColumn> data;
        switch (documentType) {
            case ADMIN_REPORT_BY_CALIBRATORS_MODULE:
                data = getDataForAdminReportByCalibratorsModule(organization, startDate, endDate);
                break;
            default:
                throw new IllegalArgumentException(documentType.name() + "is not supported");
        }
        return FileFactory.buildReportFile(data, fileParameters);
    }

    private List<TableExportColumn> getDataForProviderBackFlowReport(Long organizationId, Date startDate, Date endDate) {
        List<TableExportColumn> data = new ArrayList<>();
        Organization provider = organizationRepository.findOne(organizationId);
        Set<Agreement> agreements = agreementRepository.findByCustomerId(organizationId);
        List<String> calibrators = new ArrayList<>();
        List<String> sentByProvider = new ArrayList<>();
        List<String> createdByCalibrator = new ArrayList<>();
        List<String> total = new ArrayList<>();
        List<String> testCompletedSentByProvider = new ArrayList<>();
        List<String> testCompletedCreatedByCalibrator = new ArrayList<>();
        List<String> testCompletedTotal = new ArrayList<>();
        List<String> rejectedSentByProvider = new ArrayList<>();
        List<String> rejectedCreatedByCalibrator = new ArrayList<>();
        List<String> rejectedTotal = new ArrayList<>();


        for (Agreement agrement : agreements) {

            calibrators.add(agrement.getExecutor().getName());

            int countSentByProvider = verificationRepository.countByProviderAndInitialDateBetweenAndCalibratorAndStatusNotLikeAndIsCreatedByCalibratorFalse(provider, startDate, endDate, agrement.getExecutor(), Status.NOT_VALID);
            int countCreatedByCalibrator = verificationRepository.countByProviderAndInitialDateBetweenAndCalibratorAndStatusNotLikeAndIsCreatedByCalibratorTrue(provider, startDate, endDate, agrement.getExecutor(), Status.NOT_VALID);
            sentByProvider.add(String.valueOf(countSentByProvider));
            createdByCalibrator.add(String.valueOf(countCreatedByCalibrator));
            total.add(String.valueOf(countCreatedByCalibrator + countSentByProvider));

            int countTestCompletedCreatedByCalibrator = verificationRepository.countByProviderAndInitialDateBetweenAndCalibratorAndIsCreatedByCalibratorTrueAndVerificationDateIsNotNullAndStatusNotLike(provider, startDate, endDate, agrement.getExecutor(), Status.NOT_VALID);
            int countTestCompletedSentByProvider = verificationRepository.countByProviderAndInitialDateBetweenAndCalibratorAndIsCreatedByCalibratorFalseAndVerificationDateIsNotNullAndStatusNotLike(provider, startDate, endDate, agrement.getExecutor(), Status.NOT_VALID);
            testCompletedSentByProvider.add(String.valueOf(countTestCompletedSentByProvider));
            testCompletedCreatedByCalibrator.add(String.valueOf(countTestCompletedCreatedByCalibrator));
            testCompletedTotal.add(String.valueOf(countTestCompletedSentByProvider + countTestCompletedCreatedByCalibrator));

            int countRejectedCreatedByCalibrator = verificationRepository.countByProviderAndInitialDateBetweenAndCalibratorAndIsCreatedByCalibratorTrueAndRejectedInfoIsNotNullAndStatusNotLike(provider, startDate, endDate, agrement.getExecutor(), Status.NOT_VALID);
            int countRejectedSentByProvider = verificationRepository.countByProviderAndInitialDateBetweenAndCalibratorAndIsCreatedByCalibratorFalseAndRejectedInfoIsNotNullAndStatusNotLike(provider, startDate, endDate, agrement.getExecutor(), Status.NOT_VALID);
            rejectedCreatedByCalibrator.add(String.valueOf(countRejectedCreatedByCalibrator));
            rejectedSentByProvider.add(String.valueOf(countRejectedSentByProvider));
            rejectedTotal.add(String.valueOf(countRejectedCreatedByCalibrator + countRejectedSentByProvider));

        }

        data.add(new TableExportColumn(Constants.CALIBRATOR_ORGANIZATION_NAME, calibrators));
        data.add(new TableExportColumn(Constants.SENT_BY_PROVIDER, sentByProvider));
        data.add(new TableExportColumn(Constants.CREATED_BY_CALIBRATOR, createdByCalibrator));
        data.add(new TableExportColumn(Constants.TOTAL, total));
        data.add(new TableExportColumn(Constants.TESTED_SENT_BY_PROVIDER, testCompletedSentByProvider));
        data.add(new TableExportColumn(Constants.TESTED_CREATED_BY_CALIBRATOR, testCompletedCreatedByCalibrator));
        data.add(new TableExportColumn(Constants.TOTAL_TESTED, testCompletedTotal));
        data.add(new TableExportColumn(Constants.REJECTED_SENT_BY_PROVIDER, rejectedSentByProvider));
        data.add(new TableExportColumn(Constants.REJECTED_CREATED_BY_CALIBRATOR, rejectedCreatedByCalibrator));
        data.add(new TableExportColumn(Constants.TOTAL_REJECTED, rejectedTotal));


        return data;
    }

    private List<TableExportColumn> getDataForProviderEmployeesReport(Long providerId) {
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

    private List<TableExportColumn> getDataForProviderCalibratorsReport(Long providerId) {
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

    private List<TableExportColumn> getDataForProviderVerificationResultReport(Long organizationId, Date startDate, Date endDate) {
        Organization provider = organizationRepository.findOne(organizationId);
        List<Verification> verifications;
        if (startDate != null && endDate != null) {
            verifications = verificationRepository.findByProviderAndVerificationDateBetween(provider, startDate, endDate);
        } else {
            verifications = verificationRepository.findByProvider(provider);
        }
        return getDataForVerificationResultReport(verifications, OrganizationType.PROVIDER);
    }

    private List<TableExportColumn> getDataForCalibratorVerificationResultReport(Long organizationId, Date startDate, Date endDate) {
        Organization calibrator = organizationRepository.findOne(organizationId);
        List<Verification> verifications;
        if (startDate != null && endDate != null) {
            verifications = verificationRepository.findByCalibratorAndVerificationDateBetween(calibrator, startDate, endDate);
        } else {
            verifications = verificationRepository.findByCalibrator(calibrator);
        }
        return getDataForVerificationResultReport(verifications, OrganizationType.CALIBRATOR);
    }

    private List<TableExportColumn> getDataForVerificatorVerificationResultReport(Long organizationId, Date startDate, Date endDate, Boolean bySignProtocolDate) {
        Organization verificator = organizationRepository.findOne(organizationId);
        List<Verification> verifications;
        if (startDate != null && endDate != null && !bySignProtocolDate) {
            verifications = verificationRepository.findByStateVerificatorAndVerificationDateBetween(verificator, startDate, endDate);
        } else if (startDate != null && endDate != null && bySignProtocolDate) {
            verifications = verificationRepository.findByStateVerificatorAndSignProtocolDateBetween(verificator, startDate, endDate);
        } else {
            verifications = verificationRepository.findByStateVerificator(verificator);
        }
        return getDataForVerificationResultReport(verifications, OrganizationType.STATE_VERIFICATOR);
    }

    private List<TableExportColumn> getDataForAdminReportByCalibratorsModule(Organization calibrator, String startDate, String endDate) {
        List<CalibrationModule> list = calibrationModuleRepository.findByOrganizationCode(calibrator.getAdditionInfoOrganization().getCodeEDRPOU());
        return getDataForModulesResultReport(list, startDate, endDate);
    }

    private List<TableExportColumn> getDataForProviderRejectedVerificationReport(Long providerId, Date startDate, Date endDate) {
        Organization provider = organizationRepository.findOne(providerId);

        List<Verification> verifications;
        if (startDate != null && endDate != null) {
            verifications = verificationRepository.findByRejectedCalibratorDateBetweenAndStatusOrStatusAndProvider(startDate, endDate, Status.REJECTED_BY_PROVIDER, Status.REJECTED_BY_CALIBRATOR, provider);
        } else {
            verifications = verificationRepository.findByRejectedCalibratorDateBetweenAndStatusOrStatusAndProvider(null, null, Status.REJECTED_BY_PROVIDER, Status.REJECTED_BY_CALIBRATOR, provider);
        }
        int initializedCapacity = verifications.size();
        List<String> number = new ArrayList<>(initializedCapacity);
        List<String> providerEmployee = new ArrayList<>(initializedCapacity);
        List<String> calibrator = new ArrayList<>(initializedCapacity);
        List<String> calibratorEmployee = new ArrayList<>(initializedCapacity);
        List<String> rejectedDate = new ArrayList<>(initializedCapacity);
        List<String> rejectedReason = new ArrayList<>(initializedCapacity);
        List<String> customerSurname = new ArrayList<>(initializedCapacity);
        List<String> customerName = new ArrayList<>(initializedCapacity);
        List<String> cities = new ArrayList<>(initializedCapacity);
        List<String> regions = new ArrayList<>(initializedCapacity);
        List<String> streets = new ArrayList<>(initializedCapacity);
        List<String> buildings = new ArrayList<>(initializedCapacity);
        List<String> flats = new ArrayList<>(initializedCapacity);
        List<String> id = new ArrayList<>(initializedCapacity);
        List<String> comment = new ArrayList<>(initializedCapacity);

        Integer i = 1;
        for (Verification verification : verifications) {
            number.add(String.valueOf(i));
            String empty = " ";
            id.add(verification.getId());

            if (verification.getStatus().equals(Status.REJECTED_BY_CALIBRATOR)) {
                providerEmployee.add(empty);
                calibrator.add(getCalibratorFromVerification(verification));
                calibratorEmployee.add(getCalibratorEmployeeFullName(verification));
            } else if (verification.getStatus().equals((Status.REJECTED_BY_PROVIDER))) {
                providerEmployee.add(getProviderEmployeeFullName(verification));
                calibrator.add(empty);
                calibratorEmployee.add(empty);
            } else {
                providerEmployee.add(empty);
                calibrator.add(empty);
                calibratorEmployee.add(empty);
            }

            rejectedDate.add(getRejectedDateInString(verification));
            rejectedReason.add(getRejectedReason(verification));
            customerSurname.add(getCustomerSurname(verification));
            customerName.add(getCustomerName(verification));
            comment.add(getComment(verification));

            if (verification.getClientData() != null) {
                if (verification.getClientData().getClientAddress() != null) {
                    cities.add(getCitiesFromAddress(verification.getClientData().getClientAddress()));
                    regions.add(getDistrictFromAddress(verification.getClientData().getClientAddress()));
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
            ++i;
        }

        List<TableExportColumn> data = new ArrayList<>(initializedCapacity);

        data.add(new TableExportColumn(Constants.NUMBER_IN_SEQUENCE_SHORT, number));
        data.add(new TableExportColumn(Constants.PROVIDER_EMPLOYEE_FULL_NAME, providerEmployee));
        data.add(new TableExportColumn(Constants.CALIBRATOR_ORGANIZATION_NAME, calibrator));
        data.add(new TableExportColumn(Constants.CALIBRATOR_EMPLYEE_REJECTED_FULL_NAME, calibratorEmployee));
        data.add(new TableExportColumn(Constants.REJECTED_CALIBRATOR_DATE, rejectedDate));
        data.add(new TableExportColumn(Constants.REJECTED_REASON, rejectedReason));
        data.add(new TableExportColumn(Constants.CUSTOMER_SURNAME, customerSurname));
        data.add(new TableExportColumn(Constants.CUSTOMER_NAME, customerName));
        data.add(new TableExportColumn(Constants.CITY, cities));
        data.add(new TableExportColumn(Constants.REGION, regions));
        data.add(new TableExportColumn(Constants.STREET, streets));
        data.add(new TableExportColumn(Constants.BUILDING, buildings));
        data.add(new TableExportColumn(Constants.FLAT, flats));
        data.add(new TableExportColumn(Constants.VERIFICATION_ID, id));
        data.add(new TableExportColumn(Constants.COMMENT, comment));

        return data;
    }

    private List<TableExportColumn> getDataForModulesResultReport(List<CalibrationModule> list, String startDate, String endDate) {
        int initializedCapacity = list.size();
        Date fromDate = getDateForDocument(startDate);
        Date toDate = getDateForDocument(endDate);
        List<String> number = new ArrayList<>(initializedCapacity);
        List<String> amountOfProtocols = new ArrayList<>(initializedCapacity);
        List<String> amountOfSignedProtocols = new ArrayList<>(initializedCapacity);
        List<String> amountOfRejectedProtocols = new ArrayList<>(initializedCapacity);
        List<String> date = new ArrayList<>(initializedCapacity);

        for (CalibrationModule module : list) {
            number.add(String.valueOf(module.getModuleNumber()));
            date.add(startDate + "-" + endDate);
            amountOfProtocols.add(String.valueOf(verificationRepository.countByModuleIdAndVerificationDateBetween(module, fromDate, toDate)));
            amountOfSignedProtocols.add(String.valueOf(verificationRepository.countByModuleIdAndVerificationDateBetweenAndSignedIsTrue(module, fromDate, toDate)));
            amountOfRejectedProtocols.add(String.valueOf(verificationRepository.countByStatusAndCalibrationModuleAndVerificationDateBetween(Status.PROTOCOL_REJECTED, module, fromDate, toDate)));
        }

        List<TableExportColumn> data = new ArrayList<>(initializedCapacity);
        data.add(new TableExportColumn(Constants.MODULE_NUMBER, number));
        data.add(new TableExportColumn(Constants.DATE, date));
        data.add(new TableExportColumn(Constants.AMOUNT_OF_PROTOCOLS, amountOfProtocols));
        data.add(new TableExportColumn(Constants.AMOUNT_OF_SIGNED_PROTOCOL, amountOfSignedProtocols));
        data.add(new TableExportColumn(Constants.AMOUNT_OF_REJECTED_PROTOCOLS, amountOfRejectedProtocols));
        return data;
    }

    /**
     * Prepares data for report "Звіт 3".
     *
     * @return Data to use with XlsTableExporter
     */
    private List<TableExportColumn> getDataForVerificationResultReport(List<Verification> verifications, OrganizationType organization) {

        int initializedCapacity = verifications.size();
        List<String> number = new ArrayList<>(initializedCapacity);
        List<String> calibrators = new ArrayList(initializedCapacity);
        List<String> customerSurname = new ArrayList<>(initializedCapacity);
        List<String> customerName = new ArrayList<>(initializedCapacity);
        List<String> customerMiddleName = new ArrayList<>(initializedCapacity);
        List<String> cities = new ArrayList<>(initializedCapacity);
        List<String> regions = new ArrayList<>(initializedCapacity);
        List<String> streets = new ArrayList<>(initializedCapacity);
        List<String> buildings = new ArrayList<>(initializedCapacity);
        List<String> flats = new ArrayList<>(initializedCapacity);
        List<String> phones = new ArrayList<>(initializedCapacity);
        List<String> stamps = new ArrayList<>(initializedCapacity);
        List<String> verificationTime = new ArrayList<>(initializedCapacity);
        List<String> moduleNumbers = new ArrayList<>(initializedCapacity);
        List<String> counterNumbers = new ArrayList<>(initializedCapacity);
        List<String> counterTypes = new ArrayList<>(initializedCapacity);
        List<String> counterTypeSizes = new ArrayList<>(initializedCapacity);
        List<String> years = new ArrayList<>(initializedCapacity);
        List<String> counterCapacity = new ArrayList<>(initializedCapacity);
        List<String> temperatures = new ArrayList<>(initializedCapacity);
        List<String> verificationStatus = new ArrayList<>(initializedCapacity);
        List<String> verificationNumbers = new ArrayList<>(initializedCapacity);
        List<String> signProtocolDate = new ArrayList<>(initializedCapacity);
        List<String> documentNumber = new ArrayList<>(initializedCapacity);
        List<String> validUntil = new ArrayList<>(initializedCapacity);
        List<String> protocolsNumber = new ArrayList<>(initializedCapacity);
        List<String> providers = new ArrayList<>(initializedCapacity);
        List<String> notes = new ArrayList<>(initializedCapacity);
        List<String> counterNotes = new ArrayList<>(initializedCapacity);
        List<String> typeOfSupply = new ArrayList<>(initializedCapacity);
        List<String> stateVerificatorName = new ArrayList<>(initializedCapacity);
        List<String> sentToVerificatorDate = new ArrayList<>(initializedCapacity);


        Integer i = 1;
        for (Verification verification : verifications) {
            number.add(String.valueOf(i));
            String empty = " ";

            if (verification.getClientData() != null) {
                customerSurname.add(getCustomerSurname(verification));
                customerName.add(getCustomerName(verification));
                customerMiddleName.add(verification.getClientData().getMiddleName() != null ? verification.getClientData().getMiddleName() : empty);
                phones.add(getPhones(verification));

                if (verification.getClientData().getClientAddress() != null) {
                    cities.add(getCitiesFromAddress(verification.getClientData().getClientAddress()));
                    regions.add(getDistrictFromAddress(verification.getClientData().getClientAddress()));
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

            String verificationStatusStr = getStatusFromVerification(verification);
            verificationStatus.add(verificationStatusStr);
            if (verificationStatusStr.equals(empty)) {
                validUntil.add(empty);
                documentNumber.add(empty);
                signProtocolDate.add(empty);

            } else {
                validUntil.add(getValidDateInString(verification));
                documentNumber.add(getDocumentNumberFromVerification(verification));
                signProtocolDate.add(getSignProtocolDateInString(verification));
            }

            protocolsNumber.add(verification.getNumberOfProtocol() != null ? verification.getNumberOfProtocol() : empty);
            stamps.add(getStampsFromCounter(verification));
            verificationTime.add(getVerificationTimeInString(verification));
            moduleNumbers.add(getModuleNumberFromVerification(verification));
            counterNumbers.add(getCounterNumberFromVerification(verification));
            counterTypes.add(getCounterTypeFromVerification(verification));
            counterTypeSizes.add(getCounterTypeSizeFromVerification(verification));
            years.add(getRealiseYearFromVerification(verification));
            counterCapacity.add(getCounterCapacityFromVerification(verification));
            verificationNumbers.add(verification.getId());
            calibrators.add(getCalibratorFromVerification(verification));
            providers.add(getProviderFromVerification(verification));
            notes.add(getNotes(verification));
            counterNotes.add(getCounterNotes(verification));

            Integer temperature = getTemperaturesFromVerification(verification);
            temperatures.add(temperature == -1 ? " " : temperature.toString());
            typeOfSupply.add(getTypeOfSupplyFromTemperature(temperature, verification));
            stateVerificatorName.add(getStateVerificatorName(verification));
            sentToVerificatorDate.add(getSentToVerificatorDate(verification));
            ++i;
        }

        // region Fill map
        List<TableExportColumn> data = new ArrayList<>(initializedCapacity);
        data.add(new TableExportColumn(Constants.NUMBER_IN_SEQUENCE_SHORT, number));
        if (organization.equals(OrganizationType.PROVIDER)) {
            data.add(new TableExportColumn(Constants.LAB_NAME, calibrators));
        } else if (organization.equals(OrganizationType.CALIBRATOR)) {
            data.add(new TableExportColumn(Constants.PROVIDER, providers));
        } else if (organization.equals(OrganizationType.STATE_VERIFICATOR)) {
            data.add(new TableExportColumn(Constants.LAB_NAME, calibrators));
        }
        data.add(new TableExportColumn(Constants.VERIFICATION_TIME, verificationTime));
        data.add(new TableExportColumn(Constants.DOCUMENT_DATE, signProtocolDate));
        data.add(new TableExportColumn(Constants.DOCUMENT_NUMBER, documentNumber));
        data.add(new TableExportColumn(Constants.MODULE_NUMBER, moduleNumbers));
        data.add(new TableExportColumn(Constants.PROTOCOL_NUMBER, protocolsNumber));
        data.add(new TableExportColumn(Constants.CUSTOMER_SURNAME, customerSurname));
        data.add(new TableExportColumn(Constants.CUSTOMER_NAME, customerName));
        data.add(new TableExportColumn(Constants.CUSTOMER_MIDDLE_NAME, customerMiddleName));
        data.add(new TableExportColumn(Constants.CITY, cities));
        data.add(new TableExportColumn(Constants.REGION, regions));
        data.add(new TableExportColumn(Constants.STREET, streets));
        data.add(new TableExportColumn(Constants.BUILDING, buildings));
        data.add(new TableExportColumn(Constants.FLAT, flats));
        if (organization.equals(OrganizationType.CALIBRATOR) | organization.equals(OrganizationType.PROVIDER)) {
            data.add(new TableExportColumn(Constants.PHONE_NUMBER, phones));
            data.add(new TableExportColumn(Constants.STAMP, stamps));
        }
        if (organization.equals(OrganizationType.CALIBRATOR)) {
            data.add(new TableExportColumn(Constants.NOTES, notes));
        }
        if (organization.equals(OrganizationType.CALIBRATOR)) {
            data.add(new TableExportColumn(Constants.SENT_TO_VERIFICATOR_DATE_FOR_CALIBRATOR, sentToVerificatorDate));
        }
        data.add(new TableExportColumn(Constants.COUNTER_NUMBER, counterNumbers));
        data.add(new TableExportColumn(Constants.COUNTER_TYPE, counterTypes));
        data.add(new TableExportColumn(Constants.COUNTER_TYPE_SIZE, counterTypeSizes));
        data.add(new TableExportColumn(Constants.YEAR, years));
        if (organization.equals(OrganizationType.CALIBRATOR) | organization.equals(OrganizationType.PROVIDER)) {
            data.add(new TableExportColumn(Constants.COUNTER_CAPACITY, counterCapacity));
        }
        if (organization.equals(OrganizationType.CALIBRATOR)) {
            data.add(new TableExportColumn(Constants.COUNTERNOTES, counterNotes));
        }
        data.add(new TableExportColumn(Constants.TEMPERATURE, temperatures));
        data.add(new TableExportColumn(Constants.TYPE_OF_SUPPLY, typeOfSupply));
        data.add(new TableExportColumn(Constants.VERIFICATION_NUMBER, verificationNumbers));
        data.add(new TableExportColumn(Constants.VERIFICATION_STATUS, verificationStatus));
        data.add(new TableExportColumn(Constants.VALID_UNTIL, validUntil));
        if (organization.equals(OrganizationType.STATE_VERIFICATOR)) {
            data.add(new TableExportColumn(Constants.VERIFICATOR_NAME, stateVerificatorName));
            data.add(new TableExportColumn(Constants.SENT_TO_VERIFICATOR_DATE, sentToVerificatorDate));
        }
        // endregion
        return data;
    }

    public String getComment(Verification verification) {
        return verification.getComment() != null ? verification.getComment() : " ";
    }

    public String getCustomerName(Verification verification) {
        return verification.getClientData().getFirstName() != null ? verification.getClientData().getFirstName() : " ";
    }

    public String getCustomerSurname(Verification verification) {
        return verification.getClientData().getLastName() != null ? verification.getClientData().getLastName() : " ";
    }

    public String getClientFullName(Verification verification) {
        return verification.getClientData().getFullName();
    }

    public String getRejectedReason(Verification verification) {
        return verification.getRejectedInfo() != null ? verification.getRejectedInfo().getName() : " ";
    }

    public String getCalibratorEmployeeFullName(Verification verification) {
        return verification.getCalibratorEmployee() != null ? verification.getCalibratorEmployee().getFullNameShort() : " ";
    }

    public String getProviderEmployeeFullName(Verification verification) {
        return verification.getProviderEmployee() != null ? verification.getProviderEmployee().getFullNameShort() : " ";
    }

    public String getStatus(Verification verification) {
        /*switch (verification.getStatus())

        case (Status.SENT) :
        return
        break;
                SENT,
                ACCEPTED,
                REJECTED,
                CREATED_BY_CALIBRATOR,
                CREATED_FOR_PROVIDER,
                SENT_TO_PROVIDER,
                IN_PROGRESS,
                PLANNING_TASK,
                TASK_PLANED,
                TEST_PLACE_DETERMINED,
                SENT_TO_TEST_DEVICE,
                SENT_TO_DISMANTLING_TEAM,
                TEST_COMPLETED,
                PROTOCOL_REJECTED,
                SENT_TO_VERIFICATOR,
                TEST_OK,
                TEST_NOK,
                REJECTED_BY_CALIBRATOR,
                REJECTED_BY_PROVIDER,
                NOT_VALID;*/
        return null;
    }

    public String getSentToVerificatorDate(Verification verification) {
        return verification.getSentToVerificatorDate() != null ? verification.getSentToVerificatorDate().toString().substring(0, 10) : " ";
    }

    public String getStateVerificatorName(Verification verification) {
        return verification.isSigned() && verification.getStateVerificatorEmployee() != null ? verification.getStateVerificatorEmployee().getFullNameShort() : " ";
    }

    public String getTypeOfSupplyFromTemperature(Integer temperature, Verification verification) {
        if (temperature == -1 && verification.getCounter() != null && verification.getCounter().getCounterType() != null && verification.getCounter().getCounterType().getDevice() != null) {
            return verification.getCounter().getCounterType().getDevice().getDeviceType().equals(Device.DeviceType.WATER) ? Constants.WATER : Constants.THERMAL;
        } else if (temperature > 30) {
            return Constants.THERMAL;
        } else if (temperature <= 30 && temperature >= 0) {
            return Constants.WATER;
        } else return " ";
    }

    public String getCounterNotes(Verification verification) {
        return verification.getComment() != null ? verification.getComment() : " ";
    }

    public String getNotes(Verification verification) {
        return verification.getInfo() != null && verification.getInfo().getNotes() != null ? verification.getInfo().getNotes() : " ";
    }

    public String getPhones(Verification verification) {
        if (verification.getClientData().getPhone() != null) {
            if (verification.getClientData().getSecondPhone() != null) {
                return verification.getClientData().getPhone() + ", " + verification.getClientData().getSecondPhone();
            }
            return verification.getClientData().getPhone();
        }
        return " ";
    }

    public String getCitiesFromAddress(Address address) {
        return (address.getLocality() != null && address.getLocality().substring(0, 4).equals(Constants.KYIV_CITY_NAME.substring(3)) ? address.getLocality().substring(0, 4) : address.getLocality());
    }

    public String getDistrictFromAddress(Address address) {
        return (address.getDistrict() != null ? address.getDistrict() : " ");
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

    public String getVerificationTimeInString(Verification verification) {
        return (verification.getVerificationTime() != null ? verification.getVerificationTime().substring(0, 10) : " ");
    }

    public String getModuleNumberFromVerification(Verification verification) {
        return (verification.getCalibrationModule() != null && verification.getCalibrationModule().getModuleNumber() != null ? verification.getCalibrationModule().getModuleNumber() : " ");
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

    public String getCounterCapacityFromVerification(Verification verification) {
        CalibrationTest calibrationTest = calibrationTestRepository.findByVerification(verification);
        return (calibrationTest != null && calibrationTest.getCapacity() != null ? calibrationTest.getCapacity() : " ");
    }

    public Integer getTemperaturesFromVerification(Verification verification) {
        CalibrationTest calibrationTest = calibrationTestRepository.findTestByVerificationId(verification.getId());
        return calibrationTest != null && calibrationTest.getTemperature() != null ? calibrationTest.getTemperature() : -1;
    }

    public String getStatusFromVerification(Verification verification) {
        if (verification.getStatus().equals(Status.TEST_OK)) {
            return Constants.STATUS_TEST_OK;
        } else if (verification.getStatus().equals(Status.TEST_NOK)) {
            return Constants.STATUS_TEST_NOK;
        } else {
            return " ";
        }
    }

    public String getCalibratorFromVerification(Verification verification) {
        return (verification.getCalibrator() != null && verification.getCalibrator().getName() != null ? verification.getCalibrator().getName() : " ");
    }

    public String getProviderFromVerification(Verification verification) {
        return (verification.getProvider() != null && verification.getProvider().getName() != null ? verification.getProvider().getName() : " ");
    }

    public String getSignProtocolDateInString(Verification verification) {
        return verification.getSignProtocolDate() != null ? verification.getSignProtocolDate().toString().substring(0, 10) : " ";
    }

    public String getRejectedDateInString(Verification verification) {
        return verification.getRejectedCalibratorDate() != null ? verification.getRejectedCalibratorDate().toString().substring(0, 10) : " ";
    }

    public String getDocumentNumberFromVerification(Verification verification) {
        if (verification.getStateVerificatorEmployee() != null && verification.getStateVerificatorEmployee().getVerificatorSubdivision() != null
                && verification.getStateVerificatorEmployee().getVerificatorSubdivision().getId() != null) {
            String subdivisionId = verification.getStateVerificatorEmployee().getVerificatorSubdivision().getId();
            String moduleNumber = verification.getCalibrationModule().getModuleNumber();
            String bbiProtocol = verification.getBbiProtocols().iterator().next().getFileName();

            Calendar changeDate = Calendar.getInstance();
            changeDate.set(2016, 04, 29);
            Date signProtocolDate = verification.getSignProtocolDate();

            if (changeDate.before(signProtocolDate)) {
                if (verification.getStatus().equals(Status.TEST_OK)) {
                    return String.format("%s-%s%s", subdivisionId, moduleNumber, bbiProtocol.substring(0, bbiProtocol.indexOf('.')));
                } else if (verification.getStatus().equals(Status.TEST_NOK)) {
                    return String.format("%s-%s%s%s", subdivisionId, moduleNumber, bbiProtocol.substring(0, bbiProtocol.indexOf('.')), Constants.DOCUMEN_SUFIX_TEST_NOK);
                }
            } else {
                return String.format("%s-%s%s", subdivisionId, moduleNumber, bbiProtocol.substring(0, bbiProtocol.indexOf('.')));
            }
        }
        return " ";
    }

    public String getValidDateInString(Verification verification) {
        return verification.getExpirationDate() != null ? verification.getExpirationDate().toString().substring(0, 10) : " ";
    }

    public Date getDateForDocument(String strDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {
            return dateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
