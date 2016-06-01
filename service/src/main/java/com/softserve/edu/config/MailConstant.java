package com.softserve.edu.config;

/**
 * Constants for mail config
 */
public interface MailConstant {

    String DEFAULT_ENCODING = "UTF-8";
    String MAIL = "mail";
    String CONFIG = "config";
    String MAIL_CONFIG = MAIL + "." + CONFIG;
    String CONFIG_HOST = MAIL_CONFIG + "." + "host";
    String CONFIG_PORT = MAIL_CONFIG + "." + "port";
    String CONFIG_PROTOCOL = MAIL_CONFIG + "." + "protocol";
    String CREDENTIAL_USERNAME = "mail.credentials.username";
    String CREDENTIAL_PASSWORD = "mail.credentials.password";
    String MAIL_FROM = "mail.credentials.from";
    String MAIL_FROM_NAME = "Calibration devices system";

    String NAME = "name";
    String USERNAME = "username";
    String PASSWORD = "password";
    String PROTOCOL = "protocol";
    String DOMAIN = "domain";
    String MAIL_SUBJECT = "Important notification";

    String APPLICATION_ID = "applicationId";
    String ID = "Id";
    String PROVIDER_NAME = "providerName";
    String DATE = "date";
    String DEVICE_TYPE = "deviceType";
    String VERIFICATION_ID = "verificationId";
    String MESSAGE = "message";
    String STATUS = "status";

    String FIRST_NAME = "firstName";
    String LAST_NAME = "lastName";
    String MIDDLE_NAME = "middleName";
    String MAIL_ADDRESS = "mailAddress";
    String PROCESS_TIME_EXCEEDING = "processTimeExceeding";
    String MAX_PROCESS_TIME = "maxProcessTime";
    String EMAIL = "email";
    String PHONE = "phone";
    String TYPES = "types";
    String EMPLOYEES_CAPACITY = "employeesCapacity";
    String REGION = "region";
    String LOCALITY = "locality";
    String DISTRICT = "district";
    String STREET = "street";
    String BUILDING = "building";
    String FLAT = "flat";

    String MAIL_ENCODING = "mail.encoding";
    String MODULE_NUMBER = "moduleNumber";
    String SERIAL_NUMBER = "serialNumber";

}
