package com.softserve.edu.service.utils;

import com.softserve.edu.entity.verification.Verification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

/**
 * @deprecated this class have a lot of repeated code <br/>
 * {need to be replaced and removed}<br/>
 * use {@link com.softserve.edu.specification.sort.SortCriteria} interface instead<br/>
 * as it used in {@link com.softserve.edu.specification.sort.AgreementSortCriteria}
 */
public enum SortCriteriaVerification {
	ID() {
	    	public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {
	    	
	    		if(sortOrder.equalsIgnoreCase("asc")) {
	    			return cb.asc(root.get("id"));
	    		} else {
	    			return cb.desc(root.get("id"));
	    		}
	        }
	    },
	DATE() {
	    	public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {
	    
	    		if(sortOrder.equalsIgnoreCase("asc")) {
	    			return cb.asc(root.get("initialDate"));
	    		} else {
	    			return cb.desc(root.get("initialDate"));
	    		}
	        }
	    },
	CLIENT_LAST_NAME() {
	    	public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {
	    	
	    		if(sortOrder.equalsIgnoreCase("asc")) {
	    			return cb.asc(root.get("clientData").get("lastName"));
	    		} else {
	    			return cb.desc(root.get("clientData").get("lastName"));
	    		}
	        }
	    },
	CLIENT_FIRST_NAME() {
		public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {

			if(sortOrder.equalsIgnoreCase("asc")) {
				return cb.asc(root.get("clientData").get("firstName"));
			} else {
				return cb.desc(root.get("clientData").get("firstName"));
			}
		}
	},
	STREET() {
	    	public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {
	    
	    		if(sortOrder.equalsIgnoreCase("asc")) {
	    			return cb.asc(root.get("clientData").get("clientAddress").get("street"));
	    		} else {
	    			return cb.desc(root.get("clientData").get("clientAddress").get("street"));
	    		}
	        }
	    },
	DISTRICT() {
		public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {

			if (sortOrder.equalsIgnoreCase("asc")) {
				return cb.asc(root.get("clientData").get("clientAddress").get("district"));
			} else {
				return cb.desc(root.get("clientData").get("clientAddress").get("district"));
			}
		}
	},

	REGION() {
		public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {

			if (sortOrder.equalsIgnoreCase("asc")) {
				return cb.asc(root.get("clientData").get("clientAddress").get("region"));
			} else {
				return cb.desc(root.get("clientData").get("clientAddress").get("region"));
			}
		}
	},

	LOCALITY() {
		public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {

			if (sortOrder.equalsIgnoreCase("asc")) {
				return cb.asc(root.get("clientData").get("clientAddress").get("locality"));
			} else {
				return cb.desc(root.get("clientData").get("clientAddress").get("locality"));
			}
		}
	},

	STATUS() {
	    	public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {
	 
	    		if(sortOrder.equalsIgnoreCase("asc")) {
	    			return cb.asc(root.get("status"));
	    		} else {
	    			return cb.desc(root.get("status"));
	    		}
	        }
	    },
	PROVIDER_EMPLOYEE_LAST_NAME() {
	    	public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {

	    		if(sortOrder.equalsIgnoreCase("asc")) {
	    			return (cb.asc(root.get("providerEmployee")));
	    		} else {
	    			return (cb.desc(root.get("providerEmployee")));
	    		}
	        }
	    },
	VERIFICATOR_EMPLOYEE_LAST_NAME() {
		public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {

			if(sortOrder.equalsIgnoreCase("asc")) {
				return (cb.asc(root.get("stateVerificatorEmployee")));
			} else {
				return (cb.desc(root.get("stateVerificatorEmployee")));
			}
		}
	},
	CALIBRATOR_EMPLOYEE_LAST_NAME() {
		public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {

			if(sortOrder.equalsIgnoreCase("asc")) {
				return (cb.asc(root.get("calibratorEmployee")));
			} else {
				return (cb.desc(root.get("calibratorEmployee")));
			}
		}
	},
	MEASUREMENT_DEVICE_ID() {
		public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {

			if(sortOrder.equalsIgnoreCase("asc")) {
				return (cb.asc(root.join("device").get("id")));
			} else {
				return (cb.desc(root.join("device").get("id")));
			}
		}
	},
	MEASUREMENT_DEVICE_TYPE() {
		public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {
			if(sortOrder.equalsIgnoreCase("asc")) {
				return (cb.asc(root.join("device").get("deviceType")));
			} else {
				return (cb.desc(root.join("device").get("deviceType")));
			}
		}
	},
	PROTOCOL_ID(){
		public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {
			if(sortOrder.equalsIgnoreCase("asc")) {
				return (cb.asc(root.join("calibrationTests").get("id")));
			} else {
				return (cb.desc(root.join("calibrationTests").get("id")));
			}
		}
	},
	PROTOCOL_STATUS() {
		public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {
			if(sortOrder.equalsIgnoreCase("asc")) {
				return (cb.asc(root.join("calibrationTests").get("testResult")));
			} else {
				return (cb.desc(root.join("calibrationTests").get("testResult")));
			}
		}
	},
	SYMBOL() {
		public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {
			if (sortOrder.equalsIgnoreCase("asc")) {
				return (cb.asc(root.get("counter").get("counterType").get("symbol")));
			} else {
				return (cb.desc(root.get("counter").get("counterType").get("symbol")));
			}
		}
	},
	STANDARDSIZE() {
		public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {
			if (sortOrder.equalsIgnoreCase("asc")) {
				return (cb.asc(root.get("counter").get("counterType").get("standardSize")));
			} else {
				return (cb.desc(root.get("counter").get("counterType").get("standardSize")));
			}
		}
	},
	REALISEYEAR() {
		public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {
			if (sortOrder.equalsIgnoreCase("asc")) {
				return (cb.asc(root.get("counter").get("releaseYear")));
			} else {
				return (cb.desc(root.get("counter").get("releaseYear")));
			}
		}
	},
	NAMEPROVIDER(){
		public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {
			if (sortOrder.equalsIgnoreCase("asc")) {
				return (cb.asc(root.get("provider").get("name")));
			} else {
				return (cb.desc(root.get("provider").get("name")));
			}
		}
	},
    PROVIDERFROMBBI(){
        public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {
            if (sortOrder.equalsIgnoreCase("asc")) {
                return (cb.asc(root.get("providerFromBBI").get("name")));
            } else {
                return (cb.desc(root.get("providerFromBBI").get("name")));
            }
        }
    },
	NAMECALIBRATOR(){
		public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {
			if (sortOrder.equalsIgnoreCase("asc")) {
				return (cb.asc(root.get("calibrator").get("name")));
			} else {
				return (cb.desc(root.get("calibrator").get("name")));
			}
		}
	},
	DISMANTLED() {
		public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {
			if (sortOrder.equalsIgnoreCase("asc")) {
				return (cb.asc(root.get("dismantled")));
			} else {
				return (cb.desc(root.get("dismantled")));
			}
		}
	},
	BUILDING() {
		public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {
			if (sortOrder.equalsIgnoreCase("asc")) {
				return (cb.asc(root.get("clientData").get("clientAddress").get("building")));
			} else {
				return (cb.desc(root.get("clientData").get("clientAddress").get("building")));
			}
		}
	},
  CLIENT_FULL_NAME() {
	public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {
		if (sortOrder.equalsIgnoreCase("asc")) {
			return (cb.asc(root.get("clientData").get("lastName")));
		} else {
			return (cb.desc(root.get("clientData").get("lastName")));
		}
	}
  },
	ADDRESS() {
		public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {
			if (sortOrder.equalsIgnoreCase("asc")) {
				return (cb.asc(root.get("clientData").get("clientAddress").get("district")));
			} else {
				return (cb.desc(root.get("clientData").get("clientAddress").get("district")));
			}
		}
	};


	
	 public Order getSortOrder(Root<Verification> root, CriteriaBuilder cb, String sortOrder) {
	    	return null;
	    }
}





