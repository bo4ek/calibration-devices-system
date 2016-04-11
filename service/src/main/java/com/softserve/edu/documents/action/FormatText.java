package com.softserve.edu.documents.action;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import com.softserve.edu.documents.parameter.FileParameters;
import com.softserve.edu.documents.resources.DocumentFont;
import com.softserve.edu.documents.resources.DocumentFontFactory;
import com.softserve.edu.documents.size.SizeUnit;
import com.softserve.edu.documents.size.SizeUnitConverter;
import com.softserve.edu.documents.utils.FormattingTokens;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.log4j.Logger;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Singleton.
 * Represents an operation that can be done on a document.
 */
public enum FormatText implements Operation {
    INSTANCE;

    private static Logger logger = Logger.getLogger(FormatText.class);

    /**
     * Represents an operation that reads file, finds tokens that represent
     * formatting rules and format text accordingly.
     *
     * @param sourceFile     file to perform the operation on
     * @param fileParameters parameters that specify how the file
     *                       must be generated
     * @return file with formatted text
     * @throws IOException TODO: copy from interface
     */
    @Override
    public FileObject perform(FileObject sourceFile,
                              FileParameters fileParameters) throws IOException {
        XWPFDocument templateDocument;
        FileContent sourceFileContent = sourceFile.getContent();

        try (InputStream inputStream = sourceFileContent.getInputStream()) {
            templateDocument = new XWPFDocument(inputStream);
        } catch (IOException exception) {
            logger.error("exception while trying to use input stream of file " +
                    sourceFile.getName().toString() +
                    ": ", exception);
            throw exception;
        }

        XWPFDocument newDocument =
                new XWPFDocument(templateDocument.getPackage());

        CTDocument1 document = newDocument.getDocument();
        CTBody body = document.getBody();

        if (!body.isSetSectPr()) {
            body.addNewSectPr();
        }
        CTSectPr section = body.getSectPr();

        CTPageMar pageMar = section.addNewPgMar();
        pageMar.setLeft(BigInteger.valueOf(500L));
        pageMar.setTop(BigInteger.valueOf(500L));
        pageMar.setRight(BigInteger.valueOf(500L));
        pageMar.setBottom(BigInteger.valueOf(500L));

        if(!section.isSetPgSz()) {
            section.addNewPgSz();
        }
        CTPageSz pageSize = section.isSetPgSz()? section.getPgSz() : section.addNewPgSz();
        pageSize.setOrient(STPageOrientation.PORTRAIT);
        pageSize.setW(BigInteger.valueOf(420 * 20));
        pageSize.setH(BigInteger.valueOf(595 * 20));


        List<XWPFParagraph> paragraphs = newDocument.getParagraphs();

        List<XWPFParagraph> paragraphList = paragraphs.
                stream().
                filter(paragraph -> !paragraph.getParagraphText().isEmpty()).
                collect(Collectors.toList());

        double contentWidth = getContentWidth(newDocument);
        contentWidth = SizeUnitConverter.convert(contentWidth, SizeUnit.TWIP,
                SizeUnit.PT);

        for (XWPFParagraph paragraph : paragraphList) {
            setCorrectText(paragraph, contentWidth);
        }

        try (OutputStream outputStream = sourceFileContent.getOutputStream()) {
            newDocument.write(outputStream);
        }

        return sourceFile;
    }

    /**
     * Set the correct data for each run in the supplied paragraph
     *
     * @param sourceParagraph paragraph to copy runs from
     * @param contentWidth    width of the content area
     * @throws IOException if a font can't be build
     */
    private void setCorrectText(XWPFParagraph sourceParagraph,
                                double contentWidth) throws IOException {
        int textPosition = 0;

        List<XWPFRun> runs = sourceParagraph.getRuns();

        Font font;

        try {
            font = DocumentFontFactory.INSTANCE.buildFont(DocumentFont.FREE_SERIF);
        } catch (IOException exception) {
            logger.error(DocumentFont.FREE_SERIF.name() + "can't be built",
                    exception);
            throw exception;
        }

        int indexOfRight;
        int notFound = -1;
        int indexOfSize;

        for (XWPFRun sourceRun : runs) {
            String textInRun = sourceRun.getText(textPosition);

            if (textInRun == null || textInRun.isEmpty()) {
                continue;
            }

            indexOfRight = textInRun.lastIndexOf(
                    FormattingTokens.RIGHT_SIDE.toString());

            if (indexOfRight != notFound) {
                font.setSize(sourceRun.getFontSize());
                textInRun = align(new StringBuilder(textInRun), font,
                        contentWidth);
            }

            indexOfSize = textInRun.lastIndexOf(FormattingTokens.BOLD.toString());
            if (indexOfSize != notFound) {
                sourceRun.setFontSize(12);
                textInRun = textInRun.substring(0, indexOfSize) + textInRun.substring(indexOfSize + 1, textInRun.length());
            }

            sourceRun.setText(textInRun, textPosition);
        }
    }

    /**
     * @param textInRun    string from document, that should be edited
     * @param font         font size of text
     * @param contentWidth width of the targeted raw
     * @return string of formatted text with wished width
     */
    private String align(StringBuilder textInRun, Font font, Double contentWidth) {
        float b = font.getSize();
        if (b <= 0f) {
            font.setSize(BaseFont.AWT_MAXADVANCE);
        }

        Double paragraphWidth = getStringWidth(textInRun.toString(), font);
        int index = textInRun.lastIndexOf(FormattingTokens.RIGHT_SIDE.toString());

        double epsilon = 0.0001;
        while ((paragraphWidth - contentWidth) < epsilon) {
            textInRun.insert(index, ' ');
            paragraphWidth = getStringWidth(textInRun.toString(), font);
        }

        return textInRun.toString();
    }

    public double getStringWidth(String string, Font font) {
        Chunk chunk = new Chunk(string, font);
        return chunk.getWidthPoint();
    }

    public double getContentWidth(XWPFDocument document) {
        CTBody body = document.getDocument().getBody();
        CTSectPr docSp = body.getSectPr();
        CTSectPr sectPr = document.getDocument().getBody().getSectPr();
        if (sectPr == null) return -1;
        CTPageSz pageSize = sectPr.getPgSz();
        CTPageMar margin = docSp.getPgMar();
        BigInteger pageWidth = pageSize.getW();
        pageWidth = pageWidth.add(BigInteger.ONE);
        BigInteger totalMargins = margin.getLeft().add(margin.getRight());
        BigInteger contentWidth = pageWidth.subtract(totalMargins);
        return contentWidth.doubleValue();
    }
}
