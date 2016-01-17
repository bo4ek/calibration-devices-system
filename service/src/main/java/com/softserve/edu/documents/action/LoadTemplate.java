package com.softserve.edu.documents.action;

import com.softserve.edu.documents.parameter.FileParameters;
import com.softserve.edu.documents.resources.DocumentTemplateFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Singleton.
 * Represents an operation that can be done on a document.
 */
public enum LoadTemplate implements Operation {
    INSTANCE;

    private static Logger log = Logger.getLogger(LoadTemplate.class.getName());

    /**
     * Load template that corresponds to the document's type, copy it to the
     * source file and return it.
     */
    @Override
    public FileObject perform(FileObject sourceFile,
                              FileParameters fileParameters) throws IOException {
        FileObject template =
                DocumentTemplateFactory.INSTANCE.build(fileParameters.getDocumentType());
        FileContent templateContent = template.getContent();
        FileContent sourceContent = sourceFile.getContent();

        try (InputStream inputStream = templateContent.getInputStream();
             OutputStream outputStream = sourceContent.getOutputStream()) {
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException exception) {
            log.error("exception while trying to load template" +
                    template.getName().toString(), exception);
            throw exception;
        }

        return sourceFile;
    }
}
