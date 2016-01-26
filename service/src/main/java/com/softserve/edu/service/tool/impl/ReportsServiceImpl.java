package com.softserve.edu.service.tool.impl;

import com.softserve.edu.common.Constants;
import com.softserve.edu.documents.FileFactory;
import com.softserve.edu.documents.parameter.FileFormat;
import com.softserve.edu.documents.parameter.FileParameters;
import com.softserve.edu.documents.parameter.FileSystem;
import com.softserve.edu.documents.resources.DocumentType;
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

        // region Define lists

        // № з/п
        List<String> number = new ArrayList<>();
        // ПІБ замовника
        List<String> customerFullName = new ArrayList<>();
        // Адреса замовника (індекс вулиця, будинок, квартира)
        List<String> customerAddress = new ArrayList<>();
        // Тип приладу, рік випуску
        List<String> deviceTypeYear = new ArrayList<>();
        // Діаметр
        List<String> diameter = new ArrayList<>();
        // Назва вимірювальної лабораторії
        List<String> measuringLab = new ArrayList<>();
        // Результат (придатний, непридатний)
        List<String> verificationResult = new ArrayList<>();
        // Дата документа
        List<String> documentDate = new ArrayList<>();
        // № документа
        List<String> documentNumber = new ArrayList<>();
        // Придатний до
        List<String> validUntil = new ArrayList<>();

        // endregion

        // region Fill lists

        Integer i = 1;
        for (Verification verification : verifications) {
            number.add(String.valueOf(i));
            try {
                customerFullName.add(verification.getClientData().getFullName());
            } catch (Exception ex) {
                customerFullName.add(" ");
                logger.error(ex);
            }

            String address;
            try {
                address = verification.getClientData().getClientAddress().getStreet();
            } catch (Exception ex) {
                address = " ";
                logger.error(ex);
            }
            try {
                address += ", " + verification.getClientData().getClientAddress().getBuilding();
            } catch (Exception ex) {
                logger.error(ex);
            }
            try {
                address += ", " + verification.getClientData().getClientAddress().getFlat();
            } catch (Exception ex) {
                logger.error(ex);
            }
            customerAddress.add(address);

            try {
                // TODO: Year - should be added
                deviceTypeYear.add(verification.getDevice().getDeviceSign());
            } catch (Exception ex) {
                deviceTypeYear.add(" ");
                logger.error(ex);
            }

            try {
                // TODO: Should be added
                diameter.add(" ");
            } catch (Exception ex) {
                diameter.add(" ");
                logger.error(ex);
            }

            try {
                measuringLab.add(verification.getStateVerificator().getName());
            } catch (Exception ex) {
                measuringLab.add(" ");
                logger.error(ex);
            }

            try {
                // TODO: Check ?
                verificationResult.add(verification.getStatus().toString());
            } catch (Exception ex) {
                verificationResult.add(" ");
                logger.error(ex);
            }

            try {
                SimpleDateFormat docDate = new SimpleDateFormat("dd-MM-yyyy");
                documentDate.add(docDate.format(verification.getStateVerificator().getAdditionInfoOrganization().getCertificateDate()));
            } catch (Exception ex) {
                documentDate.add(" ");
                logger.error(ex);
            }

            try {
                documentNumber.add(verification.getStateVerificator().getAdditionInfoOrganization().getCertificateNumberAuthorization());
            } catch (Exception ex) {
                documentDate.add(" ");
                logger.error(ex);
            }

            try {
                SimpleDateFormat validDate = new SimpleDateFormat("dd-MM-yyyy");
                validUntil.add(validDate.format(verification.getExpirationDate()));
            } catch (Exception ex) {
                validUntil.add(" ");
                logger.error(ex);
            }

            ++i;
        }

        // endregion

        // region Fill map

        List<TableExportColumn> data = new ArrayList<>();
        data.add(new TableExportColumn(Constants.NUMBER_IN_SEQUENCE_SHORT, number));
        data.add(new TableExportColumn(Constants.FULL_NAME_CUSTOMER, customerFullName));
        data.add(new TableExportColumn(Constants.CUSTOMER_ADDRESS, customerAddress));
        data.add(new TableExportColumn(Constants.DEVICE_TYPE_YEAR, deviceTypeYear));
        data.add(new TableExportColumn(Constants.DIAMETER, diameter));
        data.add(new TableExportColumn(Constants.MEASURING_LAB_NAME, measuringLab));
        data.add(new TableExportColumn(Constants.RESULT, verificationResult));
        data.add(new TableExportColumn(Constants.DOCUMENT_DATE, documentDate));
        data.add(new TableExportColumn(Constants.DOCUMENT_NUMBER, documentNumber));
        data.add(new TableExportColumn(Constants.VALID_UNTIL, validUntil));

        // endregion

        return data;
    }
}
