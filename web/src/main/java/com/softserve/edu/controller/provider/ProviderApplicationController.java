package com.softserve.edu.controller.provider;

import com.softserve.edu.controller.client.application.util.CatalogueDTOTransformer;
import com.softserve.edu.controller.provider.util.OrganizationStageVerificationDTOTransformer;
import com.softserve.edu.dto.DeviceLightDTO;
import com.softserve.edu.dto.application.ApplicationFieldDTO;
import com.softserve.edu.dto.application.RejectMailDTO;
import com.softserve.edu.dto.provider.OrganizationStageVerificationDTO;
import com.softserve.edu.entity.*;
import com.softserve.edu.entity.catalogue.District;
import com.softserve.edu.entity.catalogue.Region;
import com.softserve.edu.dto.LocalityDTO;
import com.softserve.edu.entity.device.Counter;
import com.softserve.edu.entity.device.CounterType;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.verification.ClientData;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.AdditionalInfo;
import com.softserve.edu.service.admin.CounterTypeService;
import com.softserve.edu.service.admin.OrganizationService;
import com.softserve.edu.service.tool.DeviceService;
import com.softserve.edu.service.tool.MailService;
import com.softserve.edu.service.user.SecurityUserDetailsService;
import com.softserve.edu.service.calibrator.CalibratorService;
import com.softserve.edu.service.catalogue.DistrictService;
import com.softserve.edu.service.catalogue.LocalityService;
import com.softserve.edu.service.catalogue.RegionService;
import com.softserve.edu.service.provider.ProviderService;
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
@RequestMapping(value = "provider/applications/")
/**
 * Used in provider UI for creating new applications
 * and sending rejection messages/notifications
 *
 * */
public class ProviderApplicationController {

    @Autowired
    private RegionService regionService;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private CalibratorService calibratorService;

    @Autowired
    private ProviderService providerService;

    @Autowired
    private CounterTypeService counterTypeService;

    @Autowired
    private DistrictService districtService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private LocalityService localityService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private MailService mail;

    private final Logger logger = Logger.getLogger(ProviderApplicationController.class);

    /**
     * Save verification in database with calibrator id
     *
     * @param verificationDTO object with verification data
     */
    @RequestMapping(value = "send", method = RequestMethod.POST)
    public ResponseEntity getInitiateVerification(@RequestBody OrganizationStageVerificationDTO verificationDTO,
                                                  @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser) {
        List<String> verificationIds = new ArrayList<>();
        HttpStatus httpStatus = HttpStatus.CREATED;
        try {
            ClientData clientData = new ClientData(
                    verificationDTO.getFirstName(),
                    verificationDTO.getLastName(),
                    verificationDTO.getMiddleName(),
                    verificationDTO.getEmail(),
                    verificationDTO.getPhone(),
                    verificationDTO.getSecondPhone(),
                    new Address(
                            verificationDTO.getRegion(),
                            verificationDTO.getDistrict(),
                            verificationDTO.getLocality(),
                            verificationDTO.getStreet(),
                            verificationDTO.getBuilding(),
                            verificationDTO.getFlat(),
                            verificationDTO.getMailIndex()
                    )
            );
            CounterType counterType = counterTypeService.findOneBySymbolAndStandardSize(
                    verificationDTO.getSymbol(), verificationDTO.getStandardSize());
            Counter counter = new Counter(
                    verificationDTO.getReleaseYear(),
                    verificationDTO.getDateOfDismantled(),
                    verificationDTO.getDateOfMounted(),
                    verificationDTO.getNumberCounter(),
                    counterType,
                    verificationDTO.getAccumulatedVolume()
            );
            AdditionalInfo info = new AdditionalInfo(
                    verificationDTO.getEntrance(),
                    verificationDTO.getDoorCode(),
                    verificationDTO.getFloor(),
                    verificationDTO.getDateOfVerif(),
                    verificationDTO.getServiceability(),
                    verificationDTO.getNoWaterToDate(),
                    verificationDTO.getNotes(),
                    verificationDTO.getTimeFrom(),
                    verificationDTO.getTimeTo()
            );

            Organization provider = providerService.findById(employeeUser.getOrganizationId());
            Organization calibrator = calibratorService.findById(verificationDTO.getCalibratorId());

            Device device = deviceService.getById(verificationDTO.getDeviceId());
            Verification verification = new Verification(new Date(), clientData, provider, device, Status.SENT,
                    Verification.ReadStatus.UNREAD, calibrator, info, verificationDTO.getDismantled(), counter, verificationDTO.getComment(),
                    verificationDTO.getSealPresence(), null, new Date(), Status.PLANNING_TASK, verificationDTO.getVerificationWithDismantle());

            verificationIds = verificationService.saveVerificationCustom(verification, verificationDTO.getQuantity(), device.getDeviceType());
            if (verificationDTO.getEmail() != null) {
                String name = clientData.getFirstName() + " " + clientData.getLastName();
                mail.sendMail(clientData.getEmail(), name, String.join(",", verificationIds), verification.getProvider().getName(),
                        verification.getDevice().getDeviceType().toString());
            }
        } catch (Exception e) {
            logger.error("Exception while inserting calibrator's verifications into DB ", e);
            httpStatus = HttpStatus.CONFLICT;
            return new ResponseEntity<>(verificationIds, httpStatus);
        }
        return new ResponseEntity<>(verificationIds, httpStatus);
    }

    /**
     * Save verification in database without sending to calibrator (without calibrator id)
     *
     * @param verificationDTO
     * @param employeeUser
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String saveInitiateVerification(@RequestBody OrganizationStageVerificationDTO verificationDTO,
                                           @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser) {
        try {
            ClientData clientData = new ClientData(
                    verificationDTO.getFirstName(),
                    verificationDTO.getLastName(),
                    verificationDTO.getMiddleName(),
                    verificationDTO.getEmail(),
                    verificationDTO.getPhone(),
                    verificationDTO.getSecondPhone(),
                    new Address(
                            verificationDTO.getRegion(),
                            verificationDTO.getDistrict(),
                            verificationDTO.getLocality(),
                            verificationDTO.getStreet(),
                            verificationDTO.getBuilding(),
                            verificationDTO.getFlat()
                    )
            );
            CounterType counterType = counterTypeService.findOneBySymbolAndStandardSize(
                    verificationDTO.getSymbol(), verificationDTO.getStandardSize());
            Counter counter = new Counter(
                    verificationDTO.getReleaseYear(),
                    verificationDTO.getDateOfDismantled(),
                    verificationDTO.getDateOfMounted(),
                    verificationDTO.getNumberCounter(),
                    counterType
            );
            AdditionalInfo info = new AdditionalInfo(
                    verificationDTO.getEntrance(),
                    verificationDTO.getDoorCode(),
                    verificationDTO.getFloor(),
                    verificationDTO.getDateOfVerif(),
                    verificationDTO.getServiceability(),
                    verificationDTO.getNoWaterToDate(),
                    verificationDTO.getNotes(),
                    verificationDTO.getTimeFrom(),
                    verificationDTO.getTimeTo()
            );

            Organization provider = providerService.findById(employeeUser.getOrganizationId());
            Device device = (verificationDTO.getDeviceId() != null) ? deviceService.getById(verificationDTO.getDeviceId()) : null;
            String verificationId = verificationService.getNewVerificationDailyIdByDeviceType(new Date(), device.getDeviceType());
            Verification verification = new Verification(new Date(), clientData, provider, device, Status.SENT,
                    Verification.ReadStatus.UNREAD, info, verificationDTO.getDismantled(), counter, verificationDTO.getComment(),
                    verificationDTO.getSealPresence(), verificationId);
            verificationService.saveVerification(verification);
            return verification.getId();
        } catch (Exception e) {
            logger.info(e);
            return null;
        }

    }

    /**
     * Get verification by verificationId fot Creating form by pattern
     */
    @RequestMapping(value = "verification/{verificationId}", method = RequestMethod.GET)
    public OrganizationStageVerificationDTO getVerificationCode(@PathVariable String verificationId) {
        Verification verification = verificationService.findById(verificationId);
        if (verification != null) {
            return OrganizationStageVerificationDTOTransformer.toDtoFromVerification(
                    verification.getClientData(),
                    verification.getClientData().getClientAddress(),
                    verification.getId(),
                    verification.getCalibrator(),
                    verification.getComment(),
                    verification.getInfo(),
                    verification.isCounterStatus(),
                    verification.isSealPresence(),
                    verification.getCounter(),
                    verification.getDevice(),
                    verification.isVerificationWithDismantle()
            );
        } else {
            return null;
        }
    }

    /**
     * get all counter symbols from table counter_type by deviceType
     *
     * @param deviceType
     * @return
     */
    @RequestMapping(value = "symbols/{deviceType}", method = RequestMethod.GET)
    public Set<String> findAllSymbolsByDeviceType(@PathVariable String deviceType) {

        return verificationService.findSymbolsByDeviceType(deviceType);
    }

    /**
     * get all standardSizes by symbol and deviceType of counter
     *
     * @param symbol
     * @param deviceType
     * @return
     */
    @RequestMapping(value = "standardSizes/{symbol}/{deviceType}", method = RequestMethod.GET)
    public Set<String> findStandardSizesBySymbol(@PathVariable String symbol, @PathVariable String deviceType) {

        return verificationService.findStandardSizesBySymbolAndDeviceType(symbol, deviceType);
    }

    /**
     * get all deviceTypes counters of which this organization can verify
     *
     * @param employeeUser
     * @return
     */
    @RequestMapping(value = "deviceTypes", method = RequestMethod.GET)
    public Set<Device.DeviceType> findDeviceTypesByOrganizationId(@AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser) {
        return organizationService.findDeviceTypesByOrganizationId(employeeUser.getOrganizationId());
    }

    /**
     * Find region corresponding to provider service area
     *
     * @param employeeUser
     * @return
     */
    @RequestMapping(value = "region", method = RequestMethod.GET)
    public List<Region> getRegionCorrespondingProvider(
            @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser) {
        LocalityDTO localityDTO = localityService.findLocalitiesByOrganizationId(employeeUser.getOrganizationId()).stream()
                .map(locality -> new LocalityDTO(locality.getId(), locality.getDesignation(), locality.getDistrict().getId()))
                .collect(Collectors.toList()).get(0);
        List<Region> regions = new ArrayList<>();
        regions.add(regionService.findByDistrictId(localityDTO.getDistrictId()));
        return regions;
    }

    /**
     * Find provider devices
     *
     * @param employeeUser
     * @return
     */
    @RequestMapping(value = "devices", method = RequestMethod.GET)
    public List<DeviceLightDTO> getDevicesCorrespondingProvider(
            @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser) {
        Set<Device.DeviceType> providerDeviceTypes = organizationService.findDeviceTypesByOrganizationId(employeeUser.getOrganizationId());
        return deviceService.getAll().stream()
                .filter(device -> providerDeviceTypes.contains(device.getDeviceType()))
                .map(device -> new DeviceLightDTO(device.getId(), device.getDeviceName(), device.getDeviceType().name()))
                .collect(Collectors.toList());
    }

    /**
     * Find districts corresponding to provider service area
     *
     * @param regionId
     * @param employeeUser
     * @return
     */
    @RequestMapping(value = "districts/{regionId}", method = RequestMethod.GET)
    public List<ApplicationFieldDTO> getDistrictsCorrespondingProvider(@PathVariable Long regionId,
                                                                       @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser) {
        Set<Long> localityIdList = localityService.findLocalitiesByOrganizationId(employeeUser.getOrganizationId())
                .stream()
                .map(locality -> locality.getDistrict().getId())
                .collect(Collectors.toSet());

        return CatalogueDTOTransformer.toDto(districtService.getDistrictsCorrespondingRegion(regionId)
                .stream()
                .filter(district -> localityIdList.contains(district.getId()))
                .collect(Collectors.toList()));
    }

    /**
     * Find localities corresponding to provider service area
     *
     * @param districtId
     * @param employeeUser
     * @return
     */
    @RequestMapping(value = "localities/{districtId}", method = RequestMethod.GET)
    public List<ApplicationFieldDTO> getLocalitiesCorrespondingProvider(@PathVariable Long districtId,
                                                                        @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser) {
        return CatalogueDTOTransformer.toDto(localityService.findByDistrictIdAndOrganizationId(districtId, employeeUser.getOrganizationId()));
    }

    /**
     * Find provider by id, finds region corresponding to provider region, finds district
     * corresponding to provider district and id
     *
     * @return ApplicationFieldDTO which contains id and designation corresponding to
     * locality id an designation
     */
    @RequestMapping(value = "localities", method = RequestMethod.GET)
    public List<ApplicationFieldDTO> getLocalityCorrespondingProvider(
            @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser) {

        Organization provider = providerService.findById(employeeUser.getOrganizationId());
        Region region = regionService.getRegionByDesignation(provider.getAddress().getRegion());
        District district = districtService.findDistrictByDesignationAndRegion(
                provider.getAddress().getDistrict(),
                region.getId()
        );
        return CatalogueDTOTransformer.toDto(localityService.getLocalitiesCorrespondingDistrict(district.getId()));
    }

    @RequestMapping(value = "organizationType", method = RequestMethod.GET)
    public Long checkOrganizationType(@AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
        Set<String> types = providerService.getTypesById(user.getOrganizationId());
        for (String type : types) {
            if (type.equalsIgnoreCase("CALIBRATOR")) {
                return user.getOrganizationId();
            }
        }
        return (long) -1;
    }

    @RequestMapping(value = "new/mail", method = RequestMethod.POST)
    public String sendReject(@RequestBody RejectMailDTO reject) {
        Verification verification = verificationService.findById(reject.getVerifID());
        String name = verification.getClientData().getFirstName();
        String sendTo = verification.getClientData().getEmail();
        //saving rejectMessage in database if verification is rejected
        if (verification.getStatus() == Status.REJECTED) {
            verification.setRejectedMessage(reject.getMsg());
            verificationService.saveVerification(verification);
        }
        mail.sendRejectMail(sendTo, name, reject.getVerifID(), reject.getMsg(), verification.getDevice().getDeviceType().toString());
        return reject.getVerifID();
    }
}
