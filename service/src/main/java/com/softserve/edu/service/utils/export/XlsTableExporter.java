package com.softserve.edu.service.utils.export;

import com.softserve.edu.common.Constants;
import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.*;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class XlsTableExporter extends TableExporter {
    // Additional width for each cell for better view
    private int extraLength;

    private int fontSize;

    private String sheetName;

    // region Constructors

    public XlsTableExporter() {
        this(" ");
    }

    /**
     * Sets XLS default parameters:
     * FontSize = 10
     * ExtraLength = 2
     * @param sheetName
     */
    public XlsTableExporter(String sheetName) {
        this(sheetName, 2, 10);
    }

    public XlsTableExporter(String sheetName, int extraLength, int fontSize) {
        this.sheetName = sheetName;
        this.extraLength = extraLength;
        this.fontSize = fontSize;
    }

    // endregion

    @Override
    public void exportToStream(List<TableExportColumn> data, OutputStream output) throws Exception {
        WritableWorkbook myDoc = Workbook.createWorkbook(output);
        WritableSheet sheet = myDoc.createSheet(sheetName, 0);

        List<Integer> lengths = getCellLengths(data);

        // region Create table header

        WritableFont boldItemFont = new WritableFont(WritableFont.ARIAL, fontSize);
        boldItemFont.setBoldStyle(WritableFont.BOLD);

        WritableCellFormat boldItemFormat = new WritableCellFormat(boldItemFont);
        boldItemFormat.setBorder(Border.ALL, BorderLineStyle.THIN);

        for (int i = 0; i < data.size(); ++i) {
            Label headerItem = new Label(i, 0, data.get(i).getName(), boldItemFormat);
            sheet.setColumnView(i, lengths.get(i) + extraLength);
            sheet.addCell(headerItem);
        }

        // endregion

        // region Fill table body

        WritableFont itemFont = new WritableFont(WritableFont.ARIAL, fontSize);
        WritableCellFormat itemFormat = new WritableCellFormat(itemFont);
        itemFormat.setBorder(Border.ALL, BorderLineStyle.THIN);

        for (int i = 0; i < data.size(); ++i) {
            List<String> column = data.get(i).getData();
            for (int j = 0; j < column.size(); ++j) {
                Label item = new Label(i, j + 1, column.get(j), itemFormat);
                sheet.addCell(item);
            }
        }

        // endregion

        myDoc.write();
        myDoc.close();
        output.flush();
        output.close();
    }

    @Override
    public void exportToFile(List<TableExportColumn> data, File output) throws Exception {
        WritableWorkbook myDoc = Workbook.createWorkbook(output);
        WritableSheet sheet = myDoc.createSheet(sheetName, 0);
        List<Integer> lengths = getCellLengths(data);

        // region Create table header

        WritableFont boldItemFont = new WritableFont(WritableFont.ARIAL, fontSize);
        boldItemFont.setBoldStyle(WritableFont.BOLD);

        WritableCellFormat boldItemFormat = new WritableCellFormat(boldItemFont);
        boldItemFormat.setBorder(Border.ALL, BorderLineStyle.THIN);

        for (int i = 0; i < data.size(); ++i) {
            Label headerItem = new Label(i, 0, data.get(i).getName(), boldItemFormat);
            sheet.setColumnView(i, lengths.get(i) + extraLength);
            sheet.addCell(headerItem);
        }

        // endregion

        // region Fill table body

        WritableFont itemFont = new WritableFont(WritableFont.ARIAL, fontSize);
        WritableCellFormat itemFormat = new WritableCellFormat(itemFont);
        itemFormat.setBorder(Border.ALL, BorderLineStyle.THIN);

        for (int i = 0; i < data.size(); ++i) {
            List<String> column = data.get(i).getData();
            for (int j = 0; j < column.size(); ++j) {
                Label item = new Label(i, j + 1, column.get(j), itemFormat);
                sheet.addCell(item);
            }
        }

        // endregion

        myDoc.write();
        myDoc.close();
    }

    /**
     * Get maximum length for each column
     *6
     * @param data Table data
     * @return Max length for each column
     */
    private List<Integer> getCellLengths(List<TableExportColumn> data) {
        List<Integer> lengths = new ArrayList<Integer>();

        for (int i = 0; i < data.size(); ++i) {
            TableExportColumn column = data.get(i);
            int max = column.getName().toString().length();
            for (int j = 0; j < column.getData().size(); ++j) {
                int cellLength = data.get(i).getData().get(j).length();
                max = cellLength > max ? cellLength : max;
            }
            lengths.add(max);
        }
        return lengths;
    }
}