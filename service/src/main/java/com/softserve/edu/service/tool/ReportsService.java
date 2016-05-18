package com.softserve.edu.service.tool;

import com.softserve.edu.documents.parameter.FileFormat;
import com.softserve.edu.documents.resources.DocumentType;
import com.softserve.edu.service.utils.export.TableExportColumn;
import org.apache.commons.vfs2.FileObject;

import java.util.List;

public interface ReportsService {
    FileObject buildFile(Long providerId, DocumentType documentType, FileFormat fileFormat) throws Exception;

    FileObject buildFileByDate(Long providerId, DocumentType documentType,
                               FileFormat fileFormat, String startDate, String endDate) throws Exception;
}
