package com.softserve.edu.service.utils.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public abstract class TableExporter {
    void exportToStream(List<TableExportColumn> data, OutputStream output) throws Exception { }

    void exportToFile(List<TableExportColumn> data, File output) throws Exception { }
}