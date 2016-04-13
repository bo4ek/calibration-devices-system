package com.softserve.edu.controller;

import com.softserve.edu.documents.parameter.FileFormat;
import com.softserve.edu.documents.parameter.FileParameters;
import com.softserve.edu.documents.parameter.FileSystem;
import com.softserve.edu.documents.resources.DocumentType;
import com.softserve.edu.documents.utils.FileUtils;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.service.calibrator.data.test.CalibrationTestService;
import com.softserve.edu.service.provider.ProviderEmployeeService;
import com.softserve.edu.service.tool.DocumentService;
import com.softserve.edu.service.tool.ReportsService;
import com.softserve.edu.service.user.SecurityUserDetailsService;
import com.softserve.edu.service.verification.VerificationService;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyEditorSupport;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Controller for file generation requests.
 * Generates a requested file and sends it to the caller, in case of an error
 * returns one of the http statuses that signals an error.
 * All exceptions are handled by the @ExceptionHandler methods.
 */
@RestController
@RequestMapping(value = "/doc")
public class DocumentsController {
    private final static Logger log = Logger.getLogger(DocumentsController.class);

    @Autowired
    DocumentService documentService;
    @Autowired
    ReportsService reportsService;
    @Autowired
    private VerificationService verificationService;
    @Autowired
    private CalibrationTestService testService;
    @Autowired
    private ProviderEmployeeService providerEmployeeService;


    @RequestMapping(value = "report/{documentType}/{fileFormat}", method = RequestMethod.GET)
    public void getReport(HttpServletResponse response,
                          @PathVariable DocumentType documentType,
                          @PathVariable FileFormat fileFormat,
                          @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser)
            throws Exception {
        User providerEmployee = providerEmployeeService.oneProviderEmployee(employeeUser.getUsername());
        Long providerId = providerEmployee.getOrganization().getId();
        FileObject file = reportsService.buildFile(providerId, documentType, fileFormat);
        sendFile(response, fileFormat, file);
    }

    @RequestMapping(value = "report/{documentType}/{fileFormat}/{startDate}/{endDate}", method = RequestMethod.GET)
    public void getReport(HttpServletResponse response,
                          @PathVariable DocumentType documentType,
                          @PathVariable FileFormat fileFormat,
                          @PathVariable String startDate,
                          @PathVariable String endDate,
                          @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser)
            throws Exception {
        User providerEmployee = providerEmployeeService.oneProviderEmployee(employeeUser.getUsername());
        Long providerId = providerEmployee.getOrganization().getId();
        FileObject file = reportsService.buildFileByDate(providerId, documentType, fileFormat, startDate, endDate);
        sendFile(response, fileFormat, file);
    }

    /**
     * Returns a document with a specific fileFormat using verification and it's
     * most recent calibration test.
     * For example: .../verification-code/pdf.
     *
     * @param verificationCode id of the verification, for which the document
     *                         is to be generated
     * @param fileFormat       fileFormat of the resulting document
     * @throws IOException           if file can't be generated because of a
     *                               file system error
     * @throws IllegalStateException if one of parameters is incorrect
     */
    @RequestMapping(value = "{verificationCode}/{fileFormat}",
            method = RequestMethod.GET)
    public void getDocument(HttpServletResponse response,
                            @PathVariable String verificationCode,
                            @PathVariable FileFormat fileFormat)
            throws IOException, IllegalStateException {
        FileObject file = documentService.buildFile(verificationCode, fileFormat);
        sendFile(response, fileFormat, file);
    }


    /**
     * Returns a document with a specific fileFormat using verification and one
     * of it's tests. For example: .../verification_certificate/1/1/pdf.
     *
     * @param documentType     document to generate
     * @param verificationCode id of the verification, for which the document
     *                         is to be generated
     * @param testID           one of the verification's tests, for which the
     *                         document is to be generated
     * @param fileFormat       fileFormat of the resulting document
     * @throws IOException           if file can't be generated because of a
     *                               file system error
     * @throws IllegalStateException if one of parameters is incorrect
     */
    @RequestMapping(value = "{documentType}/{verificationCode}/{testID}/{fileFormat}",
            method = RequestMethod.GET)
    public void getDocument(HttpServletResponse response,
                            @PathVariable DocumentType documentType,
                            @PathVariable String verificationCode,
                            @PathVariable Long testID,
                            @PathVariable FileFormat fileFormat)
            throws IOException, IllegalStateException {
        FileObject file = documentService.buildFile(verificationCode, testID, documentType, fileFormat);
        sendFile(response, fileFormat, file);
    }

    @RequestMapping(value = "{documentType}/{verificationCode}/signed",
            method = RequestMethod.POST)
    public ResponseEntity addSignToDocument(@PathVariable DocumentType documentType,
                                            @PathVariable String verificationCode,
                                            @RequestParam("signature") String signature
    ) throws IOException {

        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.OK);
        Verification verification = verificationService.findById(verificationCode);
        verification.setParsed(true);
        verification.setSignature(signature);
        verificationService.saveVerification(verification);

        CalibrationTest calibrationTest = testService.findByVerificationId(verificationCode);
        try {
            FileObject fileNew = documentService.buildFile(documentType, verification, calibrationTest, FileFormat.DOCX);

            byte[] documentNewByteArray = new byte[(int) fileNew.getContent().getSize()];
            fileNew.getContent().getInputStream().read(documentNewByteArray);
            verification.setSignedDocument(documentNewByteArray);
            verificationService.saveVerification(verification);

        } catch (IOException e) {
            log.error("Cannot build document with digital signature ", e);
            responseEntity = new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }

    /**
     * Returns a document with a specific fileFormat using verification that
     * has only one test. For example: .../verification_certificate/1/pdf.
     *
     * @param documentType     document to generate
     * @param verificationCode id of the verification, for which the document
     *                         is to be generated. This verification
     *                         must have only one test
     * @param fileFormat       fileFormat of the resulting document
     * @throws IOException           if file can't be generated because of a
     *                               file system error
     * @throws IllegalStateException if one of parameters is incorrect
     */
    @RequestMapping(value = "{documentType}/{verificationCode}/{fileFormat}",
            method = RequestMethod.GET)
    public void getDocument(HttpServletResponse response,
                            @PathVariable DocumentType documentType,
                            @PathVariable String verificationCode,
                            @PathVariable FileFormat fileFormat) {
        try {
            sendFile(response, fileFormat, documentService.getSignedDocument(verificationCode, fileFormat, documentType));
        } catch (IllegalArgumentException e) {
            log.error("Format is not supported", e);
        } catch (Exception e) {
            log.error("Cannot download document ", e);
        }
    }

    /**
     * Writes contents of the input stream to the response' output stream and
     * sets http headers depending on the fileFormat.
     *
     * @param response   servlet response
     * @param fileFormat of the file to be sent
     * @param file       file to be sent
     * @throws IOException
     */
    private void sendFile(HttpServletResponse response, FileFormat fileFormat,
                          FileObject file) throws IOException {
        setContentType(response, fileFormat);
        byte[] buffer = new byte[(int) file.getContent().getSize()];

        try (InputStream inputStream = file.getContent().getInputStream()) {

            inputStream.read(buffer);
            response.setHeader("Content-Length", String.valueOf(buffer.length));

            response.setHeader("Content-Disposition", "attachment; " +
                    "filename=" + file.getName().getBaseName() + "." + fileFormat.name().toLowerCase());
            OutputStream outputStream = response.getOutputStream();

            outputStream.write(buffer);
            outputStream.flush();
            outputStream.close();
        }
    }

    /**
     * Set content type of the response depending on the document's format.
     *
     * @param response   servlet response
     * @param fileFormat of the file to be sent
     */
    private void setContentType(HttpServletResponse response, FileFormat fileFormat) {
        switch (fileFormat) {
            case PDF:
                response.setContentType("application/pdf");
                response.setHeader("X-Frame-Options", "SAMEORIGIN");
                break;
            case DOCX:
                response.setContentType("application/vnd.openxmlformats-" +
                        "officedocument.wordprocessingml.document");
                break;
            case XLS:
                response.setContentType("application/vnd.ms-excel");
                break;
            case P7S:
                response.setContentType("application/pkcs7-signature");
                break;
            default:
                throw new IllegalArgumentException(fileFormat.name() +
                        " is not supported");

        }
    }

    /**
     * In case of a illegal state of a path parameter logs exception and
     * sends http status NOT_FOUND to the client.
     *
     * @param exception thrown exception
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalStateException.class)
    public void illegalStateExceptionHandler(IllegalStateException exception) {
        log.error(exception.getMessage(), exception);
    }

    /**
     * In case of an file system logs exception and
     * sends http status NOT_FOUND to the client.
     *
     * @param exception thrown exception
     */
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(IOException.class)
    public void ioExceptionHandler(IOException exception) {
        log.error(exception.getMessage(), exception);
    }

    /**
     * In case of an uncaught throwable logs it and
     * sends http status INTERNAL_SERVER_ERROR to the client.
     *
     * @param throwable thrown exception or error
     */
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public void throwableHandler(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
    }

    /**
     * Registers custom editors for the enum parameters.
     */
    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        // register custom editor for the FileFormat enum
        dataBinder.registerCustomEditor(FileFormat.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                String capitalize = text.toUpperCase();
                FileFormat fileFormat = FileFormat.valueOf(capitalize);
                setValue(fileFormat);
            }
        });

        // register custom editor for the DocumentType enum
        dataBinder.registerCustomEditor(DocumentType.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                String capitalize = text.toUpperCase();
                DocumentType documentType = DocumentType.valueOf(capitalize);
                setValue(documentType);
            }
        });
    }

    @RequestMapping(value = "/info/{verificationCode}/{fileFormat}",
            method = RequestMethod.GET)
    public void getInfoDocument(HttpServletResponse response,
                                @PathVariable String verificationCode,
                                @PathVariable FileFormat fileFormat)
            throws IOException, IllegalStateException {
        FileObject file = documentService.buildInfoFile(verificationCode, fileFormat);
        sendFile(response, fileFormat, file);
    }
}
