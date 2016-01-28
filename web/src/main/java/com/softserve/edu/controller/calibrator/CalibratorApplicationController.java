package com.softserve.edu.controller.calibrator;

import com.softserve.edu.controller.client.application.util.CatalogueDTOTransformer;
import com.softserve.edu.controller.provider.util.OrganizationStageVerificationDTOTransformer;
import com.softserve.edu.dto.DeviceLightDTO;
import com.softserve.edu.dto.LocalityDTO;
import com.softserve.edu.dto.application.ApplicationFieldDTO;
import com.softserve.edu.dto.provider.OrganizationStageVerificationDTO;
import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.catalogue.District;
import com.softserve.edu.entity.catalogue.Locality;
import com.softserve.edu.entity.catalogue.Region;
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
import com.softserve.edu.service.calibrator.CalibratorService;
import com.softserve.edu.service.catalogue.DistrictService;
import com.softserve.edu.service.catalogue.LocalityService;
import com.softserve.edu.service.catalogue.RegionService;
import com.softserve.edu.service.provider.ProviderService;
import com.softserve.edu.service.tool.DeviceService;
import com.softserve.edu.service.user.SecurityUserDetailsService;
import com.softserve.edu.service.verification.VerificationService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "calibrator/applications/")
public class CalibratorApplicationController {

    @Autowired
    CalibratorService calibratorService;

    @Autowired
    ProviderService providerService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private LocalityService localityService;

    @Autowired
    private CounterTypeService counterTypeService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private DistrictService districtService;

    @Autowired
    private OrganizationService organizationService;

    private final Logger logger = Logger.getLogger(CalibratorApplicationController.class);

    /**
     * create new verification and send to provider to accept or reject
     * @param verificationDTO
     * @param employeeUser
     * @return
     */
    @RequestMapping(value = "send", method = RequestMethod.POST)
    public String getInitiateVerification(@RequestBody OrganizationStageVerificationDTO verificationDTO,
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
           CounterType counterType = counterTypeService.findOneBySymbolAndStandardSize(verificationDTO.getSymbol(),
                   verificationDTO.getStandardSize());
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

           Organization calibrator = calibratorService.findById(employeeUser.getOrganizationId());
           Organization provider = providerService.findById(verificationDTO.getProviderId());

           Device device = deviceService.getById(verificationDTO.getDeviceId());
           String verificationId = verificationService.getNewVerificationDailyIdByDeviceType(new Date(), device.getDeviceType());
           Verification verification = new Verification(new Date(), clientData, provider, device,
                   Status.IN_PROGRESS, Verification.ReadStatus.UNREAD, calibrator, info, verificationDTO.getDismantled(),
                   counter, verificationDTO.getComment(), verificationDTO.getSealPresence(), verificationId);

           verificationService.saveVerification(verification);
           // випадку ексепшину присвоювати айдішці null

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
                    verification.getProvider(),
                    verification.getComment(),
                    verification.getInfo(),
                    verification.isCounterStatus(),
                    verification.isSealPresence(),
                    verification.getCounter(),
                    verification.getDevice()
            );
        } else {
            return null;
        }
    }

    /**
     * get all counter symbols from table counter_type by deviceType
     * @param deviceType
     * @return
     */
    @RequestMapping(value = "symbols/{deviceType}", method = RequestMethod.GET)
    public Set<String> findAllSymbolsByDeviceType(@PathVariable String deviceType){

        return verificationService.findSymbolsByDeviceType(deviceType);
    }

    /**
     * get all standardSizes by symbol and deviceType of counter
     * @param symbol
     * @param deviceType
     * @return
     */
    @RequestMapping(value = "standardSizes/{symbol}/{deviceType}", method = RequestMethod.GET)
    public Set<String> findStandardSizesBySymbol(@PathVariable String symbol, @PathVariable String deviceType) {
        return verificationService.findStandardSizesBySymbolAndDeviceType(symbol, deviceType);
    }

    /**
     * get all symbols by standardSize and DeviceType from counter_type
     * @param standardSize
     * @param deviceType
     * @return
     */
    @RequestMapping(value = "symbols/{standardSize}/{deviceType}", method = RequestMethod.GET)
    public Set<String> findSymbolByStandardSizeAndDeviceType(@PathVariable String standardSize, @PathVariable String deviceType) {
        return verificationService.findSymbolByStandardSizeAndDeviceType(standardSize, deviceType);
    }

    /**
     * get all standardSize from counter_type
     * @return
     */
    @RequestMapping(value = "standardSizes/", method = RequestMethod.GET)
    public Set<String> findAllStandardSizes() {
        return verificationService.findAllStandardSizes();
    }

    /**
     * get all deviceTypes counters of which this organization can verify
     * @param employeeUser
     * @return
     */
    @RequestMapping(value = "deviceTypes", method = RequestMethod.GET)
    public Set<Device.DeviceType> findDeviceTypesByOrganizationId(@AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser) {
        return organizationService.findDeviceTypesByOrganizationId(employeeUser.getOrganizationId());
    }

    /**
     * Find region corresponding to calibrator service area
     *
     * @param employeeUser
     * @return
     */
    @RequestMapping(value = "region", method = RequestMethod.GET)
    public List<Region> getRegionCorrespondingProvider(
            @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser) {
        List<Locality> list = localityService.findLocalitiesByOrganizationId(employeeUser.getOrganizationId());
        LocalityDTO localityDTO = localityService.findLocalitiesByOrganizationId(employeeUser.getOrganizationId()).stream()
                .map(locality -> new LocalityDTO(locality.getId(), locality.getDesignation(), locality.getDistrict().getId()))
                .collect(Collectors.toList()).get(0);
        List<Region> regions = new ArrayList<>();
        regions.add(regionService.findByDistrictId(localityDTO.getDistrictId()));
        return regions;
    }

    /**
     * Find calibrator devices
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
     * Find districts corresponding to calibrator service area
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
     * Find localities corresponding to calibrator service area
     *
     * @param districtId
     * @param employeeUser
     * @return
     */
    @RequestMapping(value = "localities/{districtId}", method = RequestMethod.GET)
    public List<ApplicationFieldDTO> getLocalitiesCorrespondingProvider(@PathVariable Long districtId,
                                                                        @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser) {
        return CatalogueDTOTransformer.toDto(localityService.findByDistrictIdAndOrganizationId(districtId,
                employeeUser.getOrganizationId()));
    }

    /**
     * Find calibrator by id, finds region corresponding to calibrator region, finds district
     * corresponding to provider district and id
     *
     * @return ApplicationFieldDTO which contains id and designation corresponding to
     * locality id an designation
     */
    @RequestMapping(value = "localities", method = RequestMethod.GET)
    public List<ApplicationFieldDTO> getLocalityCorrespondingProvider(
            @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser) {

        Organization calibrator = calibratorService.findById(employeeUser.getOrganizationId());
        Region region = regionService.getRegionByDesignation(calibrator.getAddress().getRegion());
        District district = districtService.findDistrictByDesignationAndRegion(
                calibrator.getAddress().getDistrict(),
                region.getId()
        );
        return CatalogueDTOTransformer.toDto(localityService.getLocalitiesCorrespondingDistrict(district.getId()));
    }

    @RequestMapping(value = "organizationType", method = RequestMethod.GET)
    public Long checkOrganizationType(@AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
        Set<String> types = calibratorService.getTypesById(user.getOrganizationId());
        for (String type : types) {
            if (type.equalsIgnoreCase("PROVIDER")) {
                return user.getOrganizationId();
            }
        }
        return (long) -1;
    }


}
