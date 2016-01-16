package com.softserve.edu.documents.action;

import com.softserve.edu.documents.document.Document;
import com.softserve.edu.documents.document.meta.MetaDataReader;
import com.softserve.edu.documents.parameter.FileParameters;
import com.softserve.edu.documents.utils.FormattingTokens;
import com.softserve.edu.documents.utils.RegEx;
import org.apache.commons.vfs2.FileObject;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Singleton.
 * Represents an operation that can be done on a document.
 */
public enum InsertText implements Operation {
    INSTANCE;

    private Logger logger = Logger.getLogger(InsertText.class);

    @Override
    public FileObject perform(FileObject sourceFile,
                              FileParameters fileParameters) throws IOException {
        Document document = fileParameters.getDocument();

        MetaDataReader metaDataReader = new MetaDataReader();
        Map<String, Object> columnsNamesValues =
                metaDataReader.getColumnsNamesValues(document);

        InputStream inputStream = sourceFile.getContent().getInputStream();
        XWPFDocument templateDocument = new XWPFDocument(inputStream);
        inputStream.close();

        XWPFDocument newDocument = new XWPFDocument(templateDocument.getPackage());

        replaceColumnsWithData(newDocument, columnsNamesValues);

        newDocument.write(sourceFile.getContent().getOutputStream());

        return sourceFile;
    }

    /**
     * Replace column names with actual data in the supplied paragraph
     *
     * @param newDocument
     * @param columnsNamesValues
     */
    private void replaceColumnsWithData(XWPFDocument newDocument,
                                        Map<String, Object> columnsNamesValues) {
        List<XWPFParagraph> paragraphs = newDocument.getParagraphs();

        List<XWPFParagraph> paragraphList = paragraphs.
                stream().
                filter(paragraph -> !paragraph.getParagraphText().isEmpty()).
                collect(Collectors.toList());

        int textPosition = 0;

        for (XWPFParagraph paragraph : paragraphList) {
            List<XWPFRun> runs = paragraph.getRuns();

            for (XWPFRun sourceRun : runs) {
                String textInRun = sourceRun.getText(textPosition);

                if (textInRun == null || textInRun.isEmpty()) {
                    continue;
                }

                sourceRun.setText(replaceText(textInRun, columnsNamesValues),
                        textPosition);
            }
        }
    }

    /**
     * @param textInRun
     * @param columnsNamesValues
     * @return
     */
    private String replaceText(String textInRun,
                               Map<String, Object> columnsNamesValues) {
        StringBuffer textInRunBuilder = new StringBuffer(textInRun);

        int indexOf = textInRunBuilder.indexOf(FormattingTokens.COLUMN.toString());
        if (indexOf == -1) {
            return textInRun;
        }

        Matcher matcher = Pattern
                .compile(RegEx.FIND_ALL_COLUMNS.toString())
                .matcher(textInRunBuilder);
        List<String> allMatches = new ArrayList<>();

        while (matcher.find()) {
            allMatches.add(matcher.group());
        }

        for (String match : allMatches) {
            int indexOfColumn = textInRunBuilder.indexOf(match);
            String substring = match.substring(1).trim();
            String columnValue = null;
            try {
                columnValue = columnsNamesValues.get(substring).toString();
            } catch (Exception e) {
                logger.error("Map value is null for " + substring + " " + e);
            }
            if (columnValue == null) {
                continue;
            }
            textInRunBuilder.replace(
                    indexOfColumn,
                    indexOfColumn + match.length(),
                    columnValue);
        }
        return textInRunBuilder.toString();
    }
}