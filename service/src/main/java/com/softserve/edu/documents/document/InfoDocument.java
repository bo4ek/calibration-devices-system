package com.softserve.edu.documents.document;

import java.text.SimpleDateFormat;

import com.softserve.edu.documents.document.meta.Placeholder;
import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.verification.ClientData;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.user.User;

@com.softserve.edu.documents.document.meta.Document
public class InfoDocument implements Document {
	  private Verification verification;
	  
	    public InfoDocument (Verification verification) {
	        super();
	        setVerification(verification);
	    }

	/**
	 * @return the owner's full name - surName + name + middleName
	 */
	@Placeholder(name = "VERIF_ID")
	public String getVerificationId() {
		return getVerification().getId();
	}

	@Placeholder(name = "STATUS")
	public String getVerificationStatus() {
		return getVerification().getStatus().name();
	}

	@Placeholder(name = "INIT_DATE")
	public String getInitailDate() {
		return new SimpleDateFormat("yyyy-MM-dd").format(getVerification().getInitialDate());
	}

	@Placeholder(name = "DEVICE_ID")
	public String getDeviceId() {
		Device device = getVerification().getDevice();
		if(device != null) {
			return String.valueOf(device.getId());
		} 
		return "Not found";
	}
	@Placeholder(name = "DEVICE_NAME")
		public String getDeviceName() {
			String deviceName = "лічильник ";
			Device device = getVerification().getDevice();
			
			if (device != null) {			
			Device.DeviceType deviceType = device.getDeviceType();
			switch (deviceType) {
				case THERMAL:
					deviceName += "тепла";
					break;
				case WATER:
					deviceName += "води";
					break;
				default:
					throw new IllegalStateException("unsupported device type");
			}

			return deviceName;
			}
			
			return "Not found";
		}


	@Placeholder(name = "DEVICE_SIGN")
	public String getDeviceSign() {
		Device device = getVerification().getDevice();
		if (device != null) {
		return device.getDeviceSign();
		}
		return "Not found";
	}

	/**
	 * @return the device's manufacturer serial number
	 */
	@Placeholder(name = "DEVICE_MANUFACTURER")
	public String getManufacturerName() {
		Device device = getVerification().getDevice();
		if (device != null) {
		return getVerification().getDevice().getManufacturer().getName();
		}		
		return "Not found";
	}

	/**
	 * @return the owner's full name - surName + name + middleName
	 */
	@Placeholder(name = "OWNER_NAME")
	public String getOwnerFullName() {
		ClientData ownerData = getVerification().getClientData();
		String fullName = ownerData.getLastName() + " " + ownerData.getFirstName() + " " + ownerData.getMiddleName();
		return fullName;
	}

	@Placeholder(name = "OWNER_PHONE")
	public String getOwnerPhone() {
		return getVerification().getClientData().getPhone();
	}
	
	@Placeholder(name = "OWNER_MAIL")
	public String getOwnerMail() {
		return getVerification().getClientData().getEmail();
	}
	
	@Placeholder(name = "OWNER_ADDRESS")
	public String getOwnerAddress() {		
		Address address = getVerification().getClientData().getClientAddress();
		StringBuilder sb = new StringBuilder();
		sb.append("обл.").append(address.getRegion()).append("; район ").append(address.getDistrict()).append("; місто ").append(address.getLocality())
		.append("; вулиця ").append(address.getStreet()).append("; будинок ").append(address.getBuilding()).append("; квартира ").append(address.getFlat());	
		return sb.toString();
	}

	@Placeholder(name = "PROVIDER")
	public String getProvider() {
		return getVerification().getProvider().getName();
	}

	@Placeholder(name = "PROVIDER_ID")
	public String getProviderId() {
		return String.valueOf(getVerification().getProvider().getId());
	}

	@Placeholder(name = "PROVIDER_PHONE")
	public String getProviderPhone() {
		return getVerification().getProvider().getPhone();
	}

	@Placeholder(name = "PROVIDER_MAIL")
	public String getProviderMail() {
		return getVerification().getProvider().getEmail();
	}

	@Placeholder(name = "PROVIDER_ADDRESS")
	public String getProviderAddress() {
		Address address = getVerification().getProvider().getAddress();
		StringBuilder sb = new StringBuilder();
		sb.append("обл.").append(address.getRegion()).append("; район ").append(address.getDistrict()).append("; місто ").append(address.getLocality())
		.append("; вулиця ").append(address.getStreet()).append("; будинок ").append(address.getBuilding()).append("; квартира ").append(address.getFlat());	
		return sb.toString();
	}
	
	@Placeholder(name = "PROVIDER_EMPLOYEE")
	public String getProviderEmployee() {
		User providerEmployee = getVerification().getProviderEmployee();
		if (providerEmployee != null) {
			String name = providerEmployee.getLastName() + " " + providerEmployee.getFirstName() + " " + providerEmployee.getMiddleName();
			return name;
		}
		return "No employee assigned";
	}
	
	@Placeholder(name = "PROVIDER_EMPLOYEE_PHONE")
	public String getProviderEmployeePhone() {
		
		User providerEmployee = getVerification().getProviderEmployee();
		if (providerEmployee != null) {
			return providerEmployee.getPhone();
		}
		return "None";
	}
	
	@Placeholder(name = "PROVIDER_EMPLOYEE_MAIL")
	public String getProviderEmployeeMail() {
		User providerEmployee = getVerification().getProviderEmployee();
		if (providerEmployee != null) {
			return providerEmployee.getEmail();
		}
		return "None";
	}


	@Placeholder(name = "CALIBRATOR")
	public String getCalibrator() {
		Organization calibrator = getVerification().getCalibrator();
		if (calibrator != null) {
			return calibrator.getName();
		}
		return "None";
	}

	@Placeholder(name = "CALIBRATOR_ID")
	public String getCalibratorId() {		
		Organization calibrator = getVerification().getCalibrator();
		if (calibrator != null) {
			return String.valueOf(calibrator.getId());
		}
		return "None";
	}

	@Placeholder(name = "CALIBRATOR_PHONE")
	public String getCalibratorPhone() {
		Organization calibrator = getVerification().getCalibrator();
		if (calibrator != null) {
			return calibrator.getPhone();
		}
		return "None";
	}

	@Placeholder(name = "CALIBRATOR_MAIL")
	public String getCalibratorMail() {
		Organization calibrator = getVerification().getCalibrator();
		if (calibrator != null) {
			return calibrator.getEmail();
		}
		return "None";
	}

	@Placeholder(name = "CALIBRATOR_ADDRESS")
	public String getCalibratorAddress() {
		Organization calibrator = getVerification().getCalibrator();
		if (calibrator != null) {
		Address address = calibrator.getAddress();
		StringBuilder sb = new StringBuilder();
		sb.append("обл.").append(address.getRegion()).append("; район ").append(address.getDistrict()).append("; місто ").append(address.getLocality())
		.append("; вулиця ").append(address.getStreet()).append("; будинок ").append(address.getBuilding()).append("; квартира ").append(address.getFlat());		
		return sb.toString();
		}
		return "None";
	}
	
	@Placeholder(name = "CALIBRATOR_EMPLOYEE")
	public String getCalibratorEmployee() {
		User calibratorEmployee = getVerification().getCalibratorEmployee();
		if (calibratorEmployee != null) {
			String name = calibratorEmployee.getLastName() + " " + calibratorEmployee.getFirstName() + calibratorEmployee.getMiddleName();
			return name;
		}
		return "No employee assigned";
	}
	@Placeholder(name = "CALIBRATOR_EMPLOYEE_PHONE")
	public String getCalibratorEmployeePhone() {
		User calibratorEmployee = getVerification().getCalibratorEmployee();
		if (calibratorEmployee != null) {
			return calibratorEmployee.getPhone();
		}
		return "None";
	}
	@Placeholder(name = "CALIBRATOR_EMPLOYEE_MAIL")
	public String getCalibratorEmployeeMail() {
		User calibratorEmployee = getVerification().getCalibratorEmployee();
		if (calibratorEmployee != null) {
			return calibratorEmployee.getEmail();
		}
		return "None";
	}



	@Placeholder(name = "VERIFICATOR")
	public String getVerificator() {
		Organization verificator = getVerification().getStateVerificator();
		if (verificator != null) {
			return verificator.getName();
		}
		return "None";
	}

	@Placeholder(name = "VERIFICATOR_ID")
	public String getVerificatorId() {
		Organization verificator = getVerification().getStateVerificator();
		if (verificator != null) {
			return String.valueOf(verificator.getId());
		}
		return "None";
	}

	@Placeholder(name = "VERIFICATOR_PHONE")
	public String getVerificatorPhone() {
		Organization verificator = getVerification().getStateVerificator();
		if (verificator != null) {
			return verificator.getPhone();
		}
		return "None";
	}

	@Placeholder(name = "VERIFICATOR_MAIL")
	public String getVerificatorMail() {
		Organization verificator = getVerification().getStateVerificator();
		if (verificator != null) {
			return verificator.getEmail();
		}
		return "None";
	}

	@Placeholder(name = "VERIFICATOR_ADDRESS")
	public String getVerificatorAddress() {
		Organization verificator = getVerification().getStateVerificator();
		if (verificator != null) {
		Address address = verificator.getAddress();
		StringBuilder sb = new StringBuilder();
		sb.append("обл.").append(address.getRegion()).append("; район ").append(address.getDistrict()).append("; місто ").append(address.getLocality())
		.append("; вулиця ").append(address.getStreet()).append("; будинок ").append(address.getBuilding()).append("; квартира ").append(address.getFlat());		
		return sb.toString();
		}
		return "None";
	}
	
	@Placeholder(name = "VERIFICATOR_EMPLOYEE")
	public String getVerificatorEmployee() {
		
		User verificatorEmployee = getVerification().getStateVerificatorEmployee();
		if (verificatorEmployee != null) {
			String name = verificatorEmployee.getLastName() + " " + verificatorEmployee.getFirstName() + verificatorEmployee.getMiddleName();
			return name;
		}
		return "No employee assigned";
	}
	
	@Placeholder(name = "VERIFICATOR_EMPLOYEE_PHONE")
	public String getVerificatorEmployeePhone() {
		User verificatorEmployee = getVerification().getStateVerificatorEmployee();
		if (verificatorEmployee != null) {
			return verificatorEmployee.getPhone();
		}
		return "None";
	}
	
	@Placeholder(name = "VERIFICATOR_EMPLOYEE_MAIL")
	public String getVerificatorEmployeeMail() {
		User verificatorEmployee = getVerification().getStateVerificatorEmployee();
		if (verificatorEmployee != null) {
			return verificatorEmployee.getEmail();
		}
		return "None";
	}

	private void setVerification(Verification verification) {
	        this.verification = verification;
	    }

	protected Verification getVerification() {
	        return verification;
	    }	   
}
