package com.softserve.edu.controller;

import com.softserve.edu.documents.parameter.FileFormat;
import com.softserve.edu.documents.resources.DocumentType;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.service.provider.ProviderEmployeeService;
import com.softserve.edu.service.tool.DocumentService;
import com.softserve.edu.service.tool.ReportsService;
import com.softserve.edu.service.user.SecurityUserDetailsService;
import org.apache.commons.vfs2.FileObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.io.InputStream;

/**
 * Controller for file generation requests.
 * Generates a requested file and sends it to the caller, in case of an error
 * returns one of the http statuses that signals an error.
 * All exceptions are handled by the @ExceptionHandler methods.
 */
@RestController
@RequestMapping(value = "/doc")
public class DocumentsController {
    static Logger log = Logger.getLogger(DocumentsController.class.getName());

    @Autowired
    DocumentService documentService;
    @Autowired
    ReportsService reportsService;
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
                            @PathVariable FileFormat fileFormat)
            throws IOException, IllegalStateException {
        FileObject file = documentService.buildFile(verificationCode, documentType, fileFormat);
        System.out.println(file.getContent().getSize());
        sendFile(response, fileFormat, file);
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
        response.setHeader("Content-Disposition", "attachment; " +
                "filename=\"" + file.getName().getBaseName() + "." + fileFormat.name().toLowerCase() + "\"");
        ServletOutputStream outputStream = response.getOutputStream();

        int bufferSize = 10240;  // 10Kb
        byte[] buffer = new byte[bufferSize];

        try (InputStream inputStream = file.getContent().getInputStream()) {
            int length = inputStream.read(buffer);
            int offset = 0;

            do {
                outputStream.write(buffer, offset, length);
                length = inputStream.read(buffer);
            } while (length > 0);
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
