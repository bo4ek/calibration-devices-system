package com.softserve.edu.documents;

import com.softserve.edu.documents.action.FormatText;
import com.softserve.edu.documents.action.Operation;
import com.softserve.edu.documents.chain.OperationChain;
import com.softserve.edu.documents.parameter.FileFormat;
import com.softserve.edu.documents.parameter.FileParameters;
import com.softserve.edu.documents.utils.FileUtils;
import com.softserve.edu.service.utils.export.TableExportColumn;
import com.softserve.edu.service.utils.export.XlsTableExporter;
import org.apache.commons.vfs2.FileObject;

import java.io.IOException;
import java.util.List;


/**
 * Factory for creating files.
 */
public class FileFactory {
    private static org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(FormatText.class);

    /**
     * Builds a file with specified parameters.
     *
     * @param fileParameters parameters of the needed file.
     * @return the built file
     */
    public static FileObject buildFile(FileParameters fileParameters) {
        List<Operation> operations;
        FileFormat fileFormat = fileParameters.getFileFormat();
        switch (fileFormat) {
            case DOCX:
                operations = OperationChain.DOCX_CHAIN.getOperations();
                break;
            case PDF:
                operations = OperationChain.PDF_CHAIN.getOperations();
                break;
            default:
                throw new IllegalArgumentException(fileFormat.name() +
                        " is not supported");
        }
        return runOperations(operations, fileParameters);
    }

    public static FileObject buildInfoFile(FileParameters fileParameters) {
        List<Operation> operations;
        FileFormat fileFormat = fileParameters.getFileFormat();
        switch (fileFormat) {
            case DOCX:
                operations = OperationChain.INFO_DOCX_CHAIN.getOperations();
                break;
            case PDF:
                operations = OperationChain.INFO_PDF_CHAIN.getOperations();
                break;
            default:
                throw new IllegalArgumentException(fileFormat.name() +
                        " is not supported");
        }
        return runOperations(operations, fileParameters);
    }

    /**
     * Runs all operations using info from parameters and returns
     * the resulting file.
     *
     * @param operations     to tun
     * @param fileParameters by which a file will be created
     * @return the resulting file
     */
    private static FileObject runOperations(List<Operation> operations,
                                            FileParameters fileParameters) {
        FileObject file = FileUtils.createFile(fileParameters.getFileSystem(),
                fileParameters.getFileName());

        for (Operation operation : operations) {
            try {
                file = operation.perform(file, fileParameters);
            } catch (IOException exception) {
                logger.error("exception while trying to perform operation " +
                        operation.getClass().getSimpleName() + ": ", exception);
                throw new RuntimeException(exception);
            }
        }
        return file;
    }

    public static FileObject buildReportFile(List<TableExportColumn> data, FileParameters fileParameters) throws Exception {
        FileObject file = FileUtils.createFile(fileParameters.getFileSystem(), fileParameters.getFileName());
        XlsTableExporter xlsTableExporter = new XlsTableExporter();
        xlsTableExporter.exportToStream(data, file.getContent().getOutputStream());
        return file;
    }
}
