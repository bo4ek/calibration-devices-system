package com.softserve.edu.controller.client.application;

import com.softserve.edu.dto.DeviceLightDTO;
import com.softserve.edu.dto.application.ApplicationFieldDTO;
import com.softserve.edu.dto.application.ClientMailDTO;
import com.softserve.edu.dto.application.ClientStageVerificationDTO;
import com.softserve.edu.dto.provider.VerificationDTO;
import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.device.Counter;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.enumeration.organization.OrganizationType;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.ClientData;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.AdditionalInfo;
import com.softserve.edu.service.admin.OrganizationService;
import com.softserve.edu.service.provider.ProviderService;
import com.softserve.edu.service.tool.DeviceService;
import com.softserve.edu.service.tool.MailService;
import com.softserve.edu.service.user.SecurityUserDetailsService;
import com.softserve.edu.service.user.UserService;
import com.softserve.edu.service.verification.VerificationService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/application/")
/**
 * Used in main application form (application-sending.html)
 * for creating verifications
 * and sending notifications about that to customerID's email
 */
public class ClientApplicationController {

    private Logger logger = Logger.getLogger(ClientApplicationController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ProviderService providerService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private MailService mail;

    /**
     * Adds new Verification
     *
     * @param verificationDTO DTO Object, that contains all necessary data to create new Verification
     * @return list of Verification's ids, size of which depends on quantity of the one type devices that were selected
     */
    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ResponseEntity saveApplication(@RequestBody ClientStageVerificationDTO verificationDTO) {
        HttpStatus httpStatus = HttpStatus.CREATED;
        List<String> verificationIds = new ArrayList<>();
        try {
            ClientData clientData = new ClientData(verificationDTO.getFirstName(),
                    verificationDTO.getLastName(),
                    verificationDTO.getMiddleName(),
                    verificationDTO.getEmail(),
                    verificationDTO.getPhone(),
                    verificationDTO.getSecondPhone(),
                    new Address(verificationDTO.getRegion(),
                            verificationDTO.getDistrict(),
                            verificationDTO.getLocality(),
                            verificationDTO.getStreet(),
                            verificationDTO.getBuilding(),
                            verificationDTO.getFlat(),
                            verificationDTO.getMailIndex())
            );

            Organization provider = providerService.findById(verificationDTO.getProviderId());
            Device device = deviceService.getById(verificationDTO.getDeviceId());

            AdditionalInfo info = new AdditionalInfo();
            info.setNotes(verificationDTO.getComment());
            info.setServiceability(true);
            Verification verification = new Verification(new Date(), new Date(), clientData, provider, device,
                    Status.SENT, Verification.ReadStatus.UNREAD, info);
            verification.setCounter(new Counter());
            verification.setTaskStatus(Status.PLANNING_TASK);
            verification.setSealPresence(true);
            verificationIds = verificationService.saveVerificationCustom(verification, verificationDTO.getQuantity(), device.getDeviceType(), null);

            logger.info("Verifications with ids " + String.join(",", verificationIds) + " was created by unauthorized user");

            if (verificationDTO.getEmail() != null) {
                String name = clientData.getFirstName() + " " + clientData.getLastName();
                mail.sendMail(clientData.getEmail(), name, String.join(",", verificationIds), provider.getName(), device.getDeviceType().toString());
            }
        } catch (Exception e) {
            logger.error("Exception while inserting verifications by unauthorized user into DB ", e);
            httpStatus = HttpStatus.CONFLICT;
            return new ResponseEntity<>(verificationIds, httpStatus);
        }
        return new ResponseEntity<>(verificationIds, httpStatus);
    }

    @RequestMapping(value = "check/{verificationId}", method = RequestMethod.GET)
    public String getClientCode(@PathVariable String verificationId) {

        Verification verification = verificationService.findById(verificationId);
        return verification == null ? "NOT_FOUND" : verification.getStatus().name();
    }

    @RequestMapping(value = "verification/{verificationId}", method = RequestMethod.GET)
    public VerificationDTO getVerificationCode(@PathVariable String verificationId) {
        Verification verification = verificationService.findById(verificationId);
        if (verification != null) {
            logger.trace(verification.getRejectedMessage());
            return new VerificationDTO(verification.getClientData(),
                    verification.getId(),
                    verification.getInitialDate(),
                    verification.getExpirationDate(),
                    verification.getStatus(),
                    verification.getCalibrator(),
                    verification.getCalibratorEmployee(),
                    verification.getDevice(),
                    verification.getProvider(),
                    verification.getProviderEmployee(),
                    verification.getStateVerificator(),
                    verification.getStateVerificatorEmployee(),
                    verification.getRejectedMessage(),
                    verification.getComment());
        } else {
            return null;
        }
    }

    /**
     * Find Providers corresponding to Locality
     *
     * @param localityId id of locality
     * @return list of providers wrapped in ApplicationFieldDTO
     */
    @RequestMapping(value = "providersInLocality/{localityId}", method = RequestMethod.GET)
    public List<ApplicationFieldDTO> getProvidersCorrespondingLocality(@PathVariable Long localityId) {

        return organizationService.findAllByLocalityIdAndTypeId(localityId, OrganizationType.PROVIDER).stream()
                .map(provider -> new ApplicationFieldDTO(provider.getId(), provider.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Return all providers in locality by device type
     *
     * @param localityId id of locality
     * @param deviceType type of device
     * @return list of providers wrapped in ApplicationFieldDTO
     */
    @RequestMapping(value = "providers/{localityId}/{deviceType}", method = RequestMethod.GET)
    public List<ApplicationFieldDTO> getProvidersCorrespondingLocalityAndType(@PathVariable Long localityId, @PathVariable String deviceType) {
        return organizationService.findByLocalityIdAndTypeAndDevice(localityId, OrganizationType.PROVIDER, Device.DeviceType.valueOf(deviceType))
                .stream().map(provider -> new ApplicationFieldDTO(provider.getId(), provider.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Return calibrators corresponding organization and device type
     *
     * @param type type of device.
     * @param user user of current organization
     * @return set of ApplicationFieldDTO where stored organization id and name
     */
    @RequestMapping(value = "calibrators/{type}", method = RequestMethod.GET)
    public Set<ApplicationFieldDTO> getCalibratorsCorrespondingDeviceType(@PathVariable String type,
                                                                          @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
        //todo agreement
        return organizationService.findByIdAndTypeAndActiveAgreementDeviceType(user.getOrganizationId(),
                OrganizationType.CALIBRATOR, Device.DeviceType.valueOf(type))
                .stream()
                .map(organization -> new ApplicationFieldDTO(organization.getId(), organization.getName()))
                .collect(Collectors.toSet());
    }

    /**
     * Return providers corresponding organization and device type
     *
     * @param type type of device.
     * @param user user of current organization
     * @return set of ApplicationFieldDTO where stored organization id and name
     */
    @RequestMapping(value = "providers/{type}", method = RequestMethod.GET)
    public Set<ApplicationFieldDTO> getProviderCorrespondingDeviceType(@PathVariable String type,
                                                                       @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {

        return organizationService.findCustomersByIdAndTypeAndActiveAgreementDeviceType(user.getOrganizationId(),
                OrganizationType.PROVIDER, Device.DeviceType.valueOf(type).toString())
                .stream()
                .map(organization -> new ApplicationFieldDTO(organization.getId(), organization.getName()))
                .collect(Collectors.toSet());
    }

    /**
     * return all devices
     *
     * @return ist of devices wrapped into DeviceLightDTO
     */
    @RequestMapping(value = "devices", method = RequestMethod.GET)
    public List<DeviceLightDTO> getAll() {
        return deviceService.getAll().stream()
                .map(device -> new DeviceLightDTO(device.getId(), device.getDeviceName(), device.getDeviceType().name()))
                .collect(Collectors.toList());
    }

    /**
     * Return all divices by type
     *
     * @param deviceType type of devices
     * @return list of devices wrapped into  ApplicationFieldDTO
     */
    @RequestMapping(value = "devices/{deviceType}", method = RequestMethod.GET)
    public List<ApplicationFieldDTO> getAllByType(@PathVariable String deviceType) {
        return deviceService.getAllByDeviceName(deviceType).stream()
                .map(device -> new ApplicationFieldDTO(device.getId(), device.getDeviceName()))
                .collect(Collectors.toList());
    }

    /**
     * Return all possible types of devices
     *
     * @return list of device types
     */
    @RequestMapping(value = "deviceTypes", method = RequestMethod.GET)
    public List<String> getDeviceTypes() {
        return Arrays.stream(Device.DeviceType.values())
                .map(Device.DeviceType::toString)
                .collect(Collectors.toList());
    }

    /**
     * Sends email to System Administrator from client with verification application
     *
     * @param mailDto mail body
     * @return status
     */
    @RequestMapping(value = "clientMessage", method = RequestMethod.POST)
    public String sentMailFromClient(@RequestBody ClientMailDTO mailDto) {
        Verification verification = verificationService.findById(mailDto.getVerifID());
        String name = verification.getClientData().getFirstName();
        String surname = verification.getClientData().getLastName();
        String sendFrom = verification.getClientData().getEmail();

        List<User> adminList = userService.findByRole("SYS_ADMIN");
        if (!adminList.isEmpty() && adminList.get(0).getEmail() != null) {
            mail.sendClientMail(adminList.get(0).getEmail(), sendFrom, name, surname, mailDto.getVerifID(), mailDto.getMsg());
        } else {
            mail.sendClientMail("metrology.calibration.devices@gmail.com", sendFrom, name, surname, mailDto.getVerifID(), mailDto.getMsg());
        }
        return "SUCCESS";
    }

    /**
     * Sends email to System Administrator from client
     * when there is no provider in database for specified location (for example district)
     * and client wants to send a message
     *
     * @param mailDto mail body
     * @return status
     */
    @RequestMapping(value = "clientMessageNoProvider", method = RequestMethod.POST)
    public String sentMailFromClientNoProvider(@RequestBody ClientMailDTO mailDto) {

        // TODO We'd send email to some configured email address
        List<User> adminList = userService.findByRole("SYS_ADMIN");
        if (!adminList.isEmpty() && adminList.get(0).getEmail() != null) {
            mail.sendClientMail(adminList.get(0).getEmail(), mailDto.getEmail(), mailDto.getName(), mailDto.getSurname(), mailDto.getVerifID(), mailDto.getMsg());
            logger.trace("Email send to:" + adminList.get(0).getEmail());
        } else {
            mail.sendClientMail("metrology.calibration.devices@gmail.com", mailDto.getEmail(), mailDto.getName(), mailDto.getSurname(), mailDto.getVerifID(), mailDto.getMsg());
        }
        return "SUCCESS";
    }
}
