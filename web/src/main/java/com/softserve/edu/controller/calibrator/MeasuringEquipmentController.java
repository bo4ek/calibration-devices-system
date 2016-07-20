package com.softserve.edu.controller.calibrator;

import com.softserve.edu.dto.PageDTO;
import com.softserve.edu.dto.calibrator.MeasuringEquipmentDTO;
import com.softserve.edu.dto.calibrator.MeasuringEquipmentPageItem;
import com.softserve.edu.entity.device.MeasuringEquipment;
import com.softserve.edu.service.tool.MeasureEquipmentService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping(value = "/calibrator/mEquipment/")
public class MeasuringEquipmentController {

	@Autowired
	private MeasureEquipmentService measureEquipmentService;
	
	private final Logger logger = Logger.getLogger(MeasuringEquipmentController.class);
	
	/**
	 * Responds a page according to input data and search value
	 *
	 * @param pageNumber
	 *            current page number
	 * @param itemsPerPage
	 *            count of elements per one page
	 * @param search
	 *            keyword for looking entities by MeasuringEquipment.name
	 * @return a page of MeasuringEquipments with their total amount
	 */
	@RequestMapping(value = "{pageNumber}/{itemsPerPage}/{search}", method = RequestMethod.GET)
	public PageDTO<MeasuringEquipmentPageItem> pageMeasuringEquipmentsWithSearch(@PathVariable Integer pageNumber,
			@PathVariable Integer itemsPerPage, @PathVariable String search) {

		Page<MeasuringEquipmentPageItem> page = measureEquipmentService
				.getMeasuringEquipmentsBySearchAndPagination(pageNumber, itemsPerPage, search)
				.map(measuringEquipment -> new MeasuringEquipmentPageItem(measuringEquipment.getId(),
						measuringEquipment.getName(), measuringEquipment.getDeviceType(),
						measuringEquipment.getManufacturer(), measuringEquipment.getVerificationInterval()));
	return new PageDTO<>(page.getTotalElements(), page.getContent());
	}


	/**
	 * Responds a page according to input data.
	 *
	 * <p>
	 * Note that this uses method {@code pageMeasuringEquipmentsWithSearch}, whereas
	 * search values is {@literal null}
	 *
	 * @param pageNumber
	 *            current page number
	 * @param itemsPerPage
	 *            count of elements per one page
	 * @return a page of MeasuringEquipments with their total amount
	 */
	@RequestMapping(value = "{pageNumber}/{itemsPerPage}", method = RequestMethod.GET)
	public PageDTO<MeasuringEquipmentPageItem> getMeasuringEquipmentsPage(@PathVariable Integer pageNumber,
																		  @PathVariable Integer itemsPerPage) {
		return pageMeasuringEquipmentsWithSearch(pageNumber, itemsPerPage, null);
	}
	
	/**
	 * Responds MeasuringEquipment by Id
	 * @param mEquipmentId
	 * @return MeasuringEquipment
	 */
	@RequestMapping(value = "getEquipment/{mEquipmentId}", method = RequestMethod.GET)
	public ResponseEntity getMeasuringEquipment(@PathVariable Long mEquipmentId){
		MeasuringEquipment foundMeasuringEquipment = measureEquipmentService.getMeasuringEquipmentById(mEquipmentId);
		return new ResponseEntity<>(foundMeasuringEquipment, HttpStatus.OK);
	}
	
	/**
	 * Saves MeasuringEquipment in database
	 *
	 * @param mEquipmentDTO
	 *            object with equipment data
	 * @return a response body with http status {@literal CREATED} if everything
	 *         MeasuringEquipment successfully created or else http
	 *         status {@literal CONFLICT}
	 */
	@RequestMapping(value = "add", method = RequestMethod.POST)
	public ResponseEntity addMeasuringEquipment(@RequestBody MeasuringEquipmentDTO mEquipmentDTO){
		HttpStatus httpStatus = HttpStatus.CREATED;
		try {
			MeasuringEquipment createdMeasuringEquipment = mEquipmentDTO.saveEquipment();
			measureEquipmentService.addMeasuringEquipment(createdMeasuringEquipment);
		} catch (Exception e) {
			logger.error("GOT EXCEPTION " + e.getMessage());
			logger.error(e);
			httpStatus = HttpStatus.CONFLICT;
		}
		return new ResponseEntity(httpStatus);
	}
	
	/**
	 * Edit MeasuringEquipment in database
	 *
	 * @param mEquipmentDTO
	 *            object with MeasuringEquipment data
	 * @param mEquipmentId
	 * @return a response body with http status {@literal OK} if MeasuringEquipment
	 *         successfully edited or else http status {@literal CONFLICT}
	 */
	@RequestMapping(value = "edit/{mEquipmentId}", method = RequestMethod.POST)
	public ResponseEntity editMeasuringEquipment(@RequestBody MeasuringEquipmentDTO mEquipmentDTO,
												 @PathVariable Long mEquipmentId){
		HttpStatus httpStatus = HttpStatus.OK;
		try {
			measureEquipmentService.editMeasuringEquipment(mEquipmentId, mEquipmentDTO.getName(),
					mEquipmentDTO.getDeviceType(), mEquipmentDTO.getManufacturer(), mEquipmentDTO.getVerificationInterval());
		} catch (Exception e) {
			logger.error("GOT EXCEPTION " + e.getMessage());
			logger.error(e);
			httpStatus = HttpStatus.CONFLICT;
		}
		return new ResponseEntity(httpStatus);
	}
	
	/**
	 * Deletes selected MeasuringEquipment by Id
	 * @param mEquipmentId
	 * @return a response body with http status {@literal OK} if MeasuringEquipment
	 *         successfully deleted
	 */
	 @RequestMapping(value = "delete/{mEquipmentId}", method = RequestMethod.POST)
	 public void deleteMeasuringEquipment(@PathVariable Long mEquipmentId){
		 measureEquipmentService.deleteMeasuringEquipment(mEquipmentId);
	 }
}
