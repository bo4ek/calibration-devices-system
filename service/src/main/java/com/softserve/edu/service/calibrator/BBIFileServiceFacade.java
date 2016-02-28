package com.softserve.edu.service.calibrator;

import com.softserve.edu.device.test.data.DeviceTestData;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.service.exceptions.InvalidImageInBbiException;
import com.softserve.edu.service.exceptions.InvalidModuleIdException;
import com.softserve.edu.service.utils.BBIOutcomeDTO;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.springframework.web.multipart.MultipartFile;

/**
 * This service is a simplified facade for other services that
 * parse and save BBI files or archives of BBI files
 */
public interface BBIFileServiceFacade {
    /**
     * Parses the bbi file and saves it in the system
     *
     * @param BBIfile          File representing BBI file
     * @param verificationID   ID of verification corresponding to test
     * @param originalFileName Original name of the BBI file (<i>e.g.</i> 10091500.bbi)
     * @return Data from the parsed BBI file
     **/
    DeviceTestData parseAndSaveBBIFile(File BBIfile, String verificationID, String originalFileName) throws IOException,
            DecoderException, ParseException, InvalidImageInBbiException, InvalidModuleIdException;

    /**
     * Parses the bbi file and saves it in the system
     *
     * @param BBIfile          MultipartFile representing BBI file
     * @param verificationID   ID of verification corresponding to test
     * @param originalFileName Original name of the BBI file (<i>e.g.</i> 10091500.bbi)
     * @return Data from the parsed BBI file
     **/
    DeviceTestData parseAndSaveBBIFile(MultipartFile BBIfile, String verificationID, String originalFileName)
            throws IOException, DecoderException, ParseException, InvalidImageInBbiException;

    /**
     * Parses the bbi file and saves it in the system
     *
     * @param inputStream      InputStream representing BBI file
     * @param verificationID   ID of verification corresponding to test
     * @param originalFileName Original name of the BBI file (<i>e.g.</i> 10091500.bbi)
     * @return Data from the parsed BBI file
     **/
    DeviceTestData parseAndSaveBBIFile(InputStream inputStream, String verificationID, String originalFileName)
            throws IOException, DecoderException, ParseException, InvalidImageInBbiException;

    /**
     * Parses the bbi files from the archive and saves them in the system
     *
     * @param archiveFile      MultipartFile representing archive with BBIs and DBF
     * @param originalFileName Original name of the archive file
     * @return List of DTOs containing BBI filename, verification id, outcome of parsing (true/false)
     * @throws IOException If the archive doesn't contain DBF file
     **/
    List<BBIOutcomeDTO> parseAndSaveArchiveOfBBIfiles(MultipartFile archiveFile, String originalFileName,
                                                      User calibratorEmployee) throws IOException, ZipException,
            SQLException, ClassNotFoundException, ParseException, InvalidImageInBbiException;

    /**
     * Parses the bbi files from the archive and saves them in the system
     *
     * @param archive          File representing archive with BBIs and DBF
     * @param originalFileName Original name of the archive file
     * @return List of DTOs containing BBI filename, verification id, outcome of parsing (true/false)
     * @throws IOException If the archive doesn't contain DBF file
     **/
    List<BBIOutcomeDTO> parseAndSaveArchiveOfBBIfiles(File archive, String originalFileName, User calibratorEmployee)
            throws IOException, ZipException, SQLException, ClassNotFoundException, ParseException, InvalidImageInBbiException;

    /**
     * Parses the bbi files from the archive and saves them in the system
     *
     * @param archiveStream    InputStream representing archive with BBIs and DBF
     * @param originalFileName Original name of the archive file
     * @return List of DTOs containing BBI filename, verification id, outcome of parsing (true/false)
     * @throws IOException If the archive doesn't contain DBF file
     **/
    List<BBIOutcomeDTO> parseAndSaveArchiveOfBBIfiles(InputStream archiveStream, String originalFileName,
                                                      User calibratorEmployee) throws IOException, ZipException,
            SQLException, ClassNotFoundException, ParseException, InvalidImageInBbiException;
}
