package com.softserve.edu.service.tool.impl;

import com.softserve.edu.common.Constants;
import com.softserve.edu.config.MailConstant;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.repository.OrganizationRepository;
import com.softserve.edu.repository.UserRepository;
import com.softserve.edu.service.tool.MailService;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.ui.velocity.VelocityEngineUtils.mergeTemplateIntoString;

@Service
@PropertySource("classpath:properties/mail.properties")
public class MailServiceImpl implements MailService {

    @Autowired
    Environment env;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Value("${mail.credentials.username}")
    private String userName;

    @Value("${site.protocol}")
    private String protocol;

    Logger logger = Logger.getLogger(MailServiceImpl.class);


    @Async
    public void sendMail(String to, String userName, String clientCode, String providerName, String deviceType) {

        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(to);
                message.setFrom(new InternetAddress(env.getProperty(MailConstant.MAIL_FROM), MailConstant.MAIL_FROM_NAME));
                String domain = null;
                try {
                    domain = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException ue) {
                    logger.error("Cannot get host address ", ue);
                }

                SimpleDateFormat form = new SimpleDateFormat("dd-MM-yyyy");
                String date = form.format(new Date());
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put(MailConstant.NAME, userName);
                templateVariables.put(MailConstant.PROTOCOL, protocol);
                templateVariables.put(MailConstant.DOMAIN, domain);
                templateVariables.put(MailConstant.APPLICATION_ID, clientCode);
                templateVariables.put(MailConstant.PROVIDER_NAME, providerName);
                if (deviceType.equals(Device.DeviceType.WATER.toString())) {
                    templateVariables.put(MailConstant.DEVICE_TYPE, Constants.WATER_DEVICE_MAIL);
                } else {
                    templateVariables.put(MailConstant.DEVICE_TYPE, Constants.THERMAL_DEVICE_MAIL);
                }
                templateVariables.put(MailConstant.DATE, date);
                String body = mergeTemplateIntoString(velocityEngine, "/velocity/templates" +
                        "/mailTemplate.vm", env.getProperty(MailConstant.MAIL_ENCODING), templateVariables);
                message.setText(body, true);
                message.setSubject(MailConstant.MAIL_SUBJECT);
            }
        };
        mailSender.send(preparator);
    }


    @Async
    public void sendNewPasswordMail(String employeeEmail, String employeeName, String newPassword) {

        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws MessagingException, UnsupportedEncodingException {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(employeeEmail);
                message.setFrom(new InternetAddress(env.getProperty(MailConstant.MAIL_FROM), MailConstant.MAIL_FROM_NAME));
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put(MailConstant.NAME, employeeName);
                templateVariables.put(MailConstant.PASSWORD, newPassword);
                String body = mergeTemplateIntoString(velocityEngine, "/velocity/templates" +
                        "/mailNewPasswordEmployee.vm", env.getProperty(MailConstant.MAIL_ENCODING), templateVariables);
                message.setText(body, true);
                message.setSubject(MailConstant.MAIL_SUBJECT);
            }
        };
        mailSender.send(preparator);
    }

    @Async
    public void sendAdminNewPasswordMail(String employeeEmail, String employeeName, String newPassword) {

        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws MessagingException, UnsupportedEncodingException {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(employeeEmail);
                message.setFrom(new InternetAddress(env.getProperty(MailConstant.MAIL_FROM), MailConstant.MAIL_FROM_NAME));
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put(MailConstant.NAME, employeeName);
                templateVariables.put(MailConstant.PASSWORD, newPassword);
                String body = mergeTemplateIntoString(velocityEngine, "/velocity/templates" +
                        "/createdAdminPassword", env.getProperty(MailConstant.MAIL_ENCODING), templateVariables);
                message.setText(body, true);
                message.setSubject(MailConstant.MAIL_SUBJECT);
            }
        };
        mailSender.send(preparator);
    }

    @Async
    public void sendOrganizationPasswordMail(String organizationMail, String organizationName, String username, String password) {

        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws UnsupportedEncodingException, MessagingException {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(organizationMail);
                message.setFrom(new InternetAddress(env.getProperty(MailConstant.MAIL_FROM), MailConstant.MAIL_FROM_NAME));
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put(MailConstant.NAME, organizationName);
                templateVariables.put(MailConstant.USERNAME, username);
                templateVariables.put(MailConstant.PASSWORD, password);
                String body = mergeTemplateIntoString(velocityEngine, "/velocity/templates" +
                        "/organizationAdminMail.vm", env.getProperty(MailConstant.MAIL_ENCODING), templateVariables);
                message.setText(body, true);
                message.setSubject(MailConstant.MAIL_SUBJECT);
            }
        };
        mailSender.send(preparator);
    }

    @Async
    public void sendOrganizationNewPasswordMail(String organizationMail, String organizationName, String username, String password) {

        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws UnsupportedEncodingException, MessagingException {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(organizationMail);
                message.setFrom(new InternetAddress(env.getProperty(MailConstant.MAIL_FROM), MailConstant.MAIL_FROM_NAME)); //properties
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put(MailConstant.NAME, organizationName);
                templateVariables.put(MailConstant.USERNAME, username);
                templateVariables.put(MailConstant.PASSWORD, password);
                String body = mergeTemplateIntoString(velocityEngine, "/velocity/templates" +
                        "/organizationAdminPasswordChange.vm", env.getProperty(MailConstant.MAIL_ENCODING), templateVariables);
                message.setText(body, true);
                message.setSubject(MailConstant.MAIL_SUBJECT);
            }
        };
        mailSender.send(preparator);
    }


    @Async
    public void sendRejectMail(String to, String userName, String verificationId, String msg, String deviceType) {

        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(to);
                message.setFrom(new InternetAddress(env.getProperty(MailConstant.MAIL_FROM), MailConstant.MAIL_FROM_NAME));
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put(MailConstant.NAME, userName);
                templateVariables.put(MailConstant.VERIFICATION_ID, verificationId);
                if (deviceType.equals("WATER")) {
                    templateVariables.put(MailConstant.DEVICE_TYPE, Constants.WATER_DEVICE_MAIL);
                } else {
                    templateVariables.put(MailConstant.DEVICE_TYPE, Constants.THERMAL_DEVICE_MAIL);
                }
                templateVariables.put(MailConstant.MESSAGE, msg);
                String body = mergeTemplateIntoString(velocityEngine, "/velocity/templates" +
                        "/rejectVerification.vm", env.getProperty(MailConstant.MAIL_ENCODING), templateVariables);
                message.setText(body, true);
                message.setSubject(MailConstant.MAIL_SUBJECT);

            }
        };
        mailSender.send(preparator);
    }

    /**
     * Notifies (sends mail to) customer about assignment of an employee to the verification
     */
    @Async
    public void sendAcceptMail(String to, String verificationId, String deviceType) {

        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(to);
                message.setFrom(new InternetAddress(env.getProperty(MailConstant.MAIL_FROM), MailConstant.MAIL_FROM_NAME));
                String domain = null;
                try {
                    domain = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException ue) {
                    logger.error("Cannot get host address", ue);
                }
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put(MailConstant.VERIFICATION_ID, verificationId);
                if (deviceType.equals("WATER")) {
                    templateVariables.put(MailConstant.DEVICE_TYPE, Constants.WATER_DEVICE_MAIL);
                } else {
                    templateVariables.put(MailConstant.DEVICE_TYPE, Constants.THERMAL_DEVICE_MAIL);
                }
                String body = mergeTemplateIntoString(velocityEngine, "/velocity/templates" +
                        "/accepted.vm", env.getProperty(MailConstant.MAIL_ENCODING), templateVariables);
                message.setText(body, true);
                message.setSubject(MailConstant.MAIL_SUBJECT);
            }
        };
        mailSender.send(preparator);
    }

    /**
     * Notifies (sends mail to) customer about changed status of  the verification
     */
    @Async
    public void sendPassedTestMail(String to, String verificationId, String status) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(to);
                message.setFrom(new InternetAddress(env.getProperty(MailConstant.MAIL_FROM), MailConstant.MAIL_FROM_NAME));
                String domain = null;
                try {
                    domain = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException ue) {
                    logger.error("Cannot get host address", ue);
                }
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put(MailConstant.VERIFICATION_ID, verificationId);
                templateVariables.put(MailConstant.STATUS, status);
                String body = mergeTemplateIntoString(velocityEngine, "/velocity/templates" +
                        "/changedStatus.vm", env.getProperty(MailConstant.MAIL_ENCODING), templateVariables);
                message.setText(body, true);
                message.setSubject(MailConstant.MAIL_SUBJECT);
            }
        };
        mailSender.send(preparator);
    }


    /**
     * Send email from client (for example to SYS_ADMIN)
     *
     * @param to
     * @param from
     * @param userFirstName
     * @param userLastName
     * @param verificationId
     * @param msg
     */
    @Async
    public void sendClientMail(String to, String from, String userFirstName, String userLastName, String verificationId, String msg) {

        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);

                message.setTo(to);
                message.setFrom(new InternetAddress(from));
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put(MailConstant.FIRST_NAME, userFirstName);
                templateVariables.put(MailConstant.LAST_NAME, userLastName);
                templateVariables.put(MailConstant.MAIL_ADDRESS, from);
                templateVariables.put(MailConstant.MESSAGE, msg);
                templateVariables.put(MailConstant.APPLICATION_ID, verificationId);
                String body = mergeTemplateIntoString(velocityEngine, "/velocity/templates" +
                        "/clientMail.vm", env.getProperty(MailConstant.MAIL_ENCODING), templateVariables);
                message.setText(body, true);
                message.setSubject(MailConstant.MAIL_SUBJECT);

            }
        };
        mailSender.send(preparator);
    }

    @Async
    public void sendTimeExceededMail(String verificationId, int processTimeExceeding, int maxProcessTime, String mailTo) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(mailTo);
                message.setFrom(new InternetAddress(env.getProperty(MailConstant.MAIL_FROM), MailConstant.MAIL_FROM_NAME));
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put(MailConstant.PROCESS_TIME_EXCEEDING, processTimeExceeding);
                templateVariables.put(MailConstant.VERIFICATION_ID, verificationId);
                templateVariables.put(MailConstant.MAX_PROCESS_TIME, maxProcessTime);
                String body = mergeTemplateIntoString(velocityEngine, "/velocity/templates" +
                        "/processTimeExceeded.vm", env.getProperty(MailConstant.MAIL_ENCODING), templateVariables);
                message.setText(body, true);
                message.setSubject(MailConstant.MAIL_SUBJECT);
            }
        };
        mailSender.send(preparator);
    }

    @Async
    /**
     * When information about organization is changed,
     * this method sends the dataset containing all data
     * to the email of the organization
     * @param organization Organization, whose data was changed
     * @param admin Admin of the organization
     *
     * */
    public void sendOrganizationChanges(Organization organization, User admin) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {

            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(organization.getEmail());
                message.setFrom(new InternetAddress(env.getProperty(MailConstant.MAIL_FROM), MailConstant.MAIL_FROM_NAME));
                String domain = null;
                try {
                    domain = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException ue) {
                    logger.error("Cannot get host address", ue);
                }
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put(MailConstant.NAME, organization.getName());
                templateVariables.put(MailConstant.EMAIL, organization.getEmail());
                templateVariables.put(MailConstant.PHONE, organization.getPhone());
                templateVariables.put(MailConstant.TYPES, organization.getOrganizationTypes());
                templateVariables.put(MailConstant.EMPLOYEES_CAPACITY, organization.getEmployeesCapacity());
                templateVariables.put(MailConstant.MAX_PROCESS_TIME, organization.getMaxProcessTime());
                templateVariables.put(MailConstant.REGION, organization.getAddress().getRegion());
                templateVariables.put(MailConstant.LOCALITY, organization.getAddress().getLocality());
                templateVariables.put(MailConstant.DISTRICT, organization.getAddress().getDistrict());
                templateVariables.put(MailConstant.STREET, organization.getAddress().getStreet());
                templateVariables.put(MailConstant.BUILDING, organization.getAddress().getBuilding());
                templateVariables.put(MailConstant.FLAT, organization.getAddress().getFlat());
                templateVariables.put(MailConstant.FIRST_NAME, admin.getFirstName());
                templateVariables.put(MailConstant.MIDDLE_NAME, admin.getMiddleName());
                templateVariables.put(MailConstant.LAST_NAME, admin.getLastName());
                templateVariables.put(MailConstant.USERNAME, admin.getUsername());

                String body = mergeTemplateIntoString(velocityEngine, "/velocity/templates" +
                        "/organizationChanges.vm", env.getProperty(MailConstant.MAIL_ENCODING), templateVariables);
                message.setText(body, true);
                message.setSubject(MailConstant.MAIL_SUBJECT);
            }
        };
        mailSender.send(preparator);
    }

    @Async
    public void sendMailWithAttachments(String to, String subject, String message, File... files) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(new InternetAddress(env.getProperty(MailConstant.MAIL_FROM), MailConstant.MAIL_FROM_NAME));
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(message);

            for (File file : files) {
                mimeMessageHelper.addAttachment(file.getName().substring(0, file.getName().indexOf('_')) + file.getName().substring(file.getName().indexOf('.')), file);
            }
            mailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (Exception ex) {
            logger.error(ex);
        }

        for (File file : files) {
            file.delete();
        }
    }
}