package com.softserve.edu.service.utils;

import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipArchiver {
    static Logger logger = Logger.getLogger(ZipArchiver.class);

    public static void createZip(OutputStream output, List<File> sources) throws Exception {
        ZipOutputStream zipOut = new ZipOutputStream(output);
        zipOut.setLevel(Deflater.DEFAULT_COMPRESSION);

        for (File source : sources) {
            if (source.isDirectory()) {
                zipDirectory(zipOut, "", source);
            } else {
                zipFile(zipOut, "", source);
            }
        }
        zipOut.flush();
        zipOut.close();
        logger.debug("Done");
    }

    private static String buildPath(String path, String file) {
        if (path == null || path.isEmpty()) {
            return file;
        } else {
            return path + File.separator + file;
        }
    }

    private static void zipDirectory(ZipOutputStream zos, String path, File dir) throws Exception {
        if (!dir.canRead()) {
            throw new AccessDeniedException("Cannot read " + dir.getCanonicalPath() + " (maybe because of permissions)");
        }

        File[] files = dir.listFiles();
        path = buildPath(path, dir.getName());
        logger.debug("Adding Directory " + path);

        for (File source : files) {
            if (source.isDirectory()) {
                zipDirectory(zos, path, source);
            } else {
                zipFile(zos, path, source);
            }
        }
        logger.debug("Leaving Directory " + path);
    }

    private static void zipFile(ZipOutputStream zos, String path, File file) throws Exception {
        if (!file.canRead()) {
            throw new AccessDeniedException("Cannot read " + file.getCanonicalPath() + " (maybe because of permissions)");
        }

        logger.debug("Compressing " + file.getName());
        zos.putNextEntry(new ZipEntry(buildPath(path, file.getName())));

        FileInputStream fis = new FileInputStream(file);

        byte[] buffer = new byte[4092];
        int byteCount = 0;
        while ((byteCount = fis.read(buffer)) != -1) {
            zos.write(buffer, 0, byteCount);
        }

        fis.close();
        zos.closeEntry();
    }

    public static void createZip(List<File> files, File output) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(output);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

            for (File file : files) {
                addToZipFile(file, zipOutputStream);
            }

            zipOutputStream.close();
            fileOutputStream.close();

        } catch (FileNotFoundException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public static void addToZipFile(File file, ZipOutputStream zos) throws FileNotFoundException, IOException {

        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zos.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zos.write(bytes, 0, length);
        }

        zos.closeEntry();
        fis.close();
    }
}
