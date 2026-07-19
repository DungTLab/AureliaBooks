/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aureliabooks.util;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Utility class for uploading files in Java Web Application.
 * Saves files to the "uploads" folder at the project root using a dynamic path
 * so the project works on any machine without hardcoded drive paths.
 * @author DungLT
 */
public class UploadUtils {

    /**
     * Automatically retrieves the absolute path of the "uploads" folder located at the project root.
     * Algorithm: trace back 2 levels from the actual deploy directory (target/...) to locate the project root directory.
     *
     * @param context ServletContext of the calling servlet
     * @return Absolute path to the uploads folder (e.g., D:/Project/uploads)
     */
    public static String getUploadPath(ServletContext context) {
        // Actual path when deployed (e.g. D:\Project\target\AureliaBooks-1.0-SNAPSHOT\)
        String deployPath = context.getRealPath("/");
        File deployDir = new File(deployPath);

        // Trace back 2 levels to get out of target/ and build/ folders
        File projectRootDir = deployDir.getParentFile().getParentFile();

        // Point to the "uploads" directory at the project root
        File uploadsDir = new File(projectRootDir, "uploads");
        if (!uploadsDir.exists()) {
            uploadsDir.mkdirs(); // Create folder if it doesn't exist
        }
        return uploadsDir.getAbsolutePath();
    }

    /**
     * Saves the uploaded file to the physical directory and returns the relative path for database storage.
     *
     * @param filePart  Part object received from the request
     * @param context   ServletContext to resolve the dynamic path
     * @param subFolder Subfolder name (e.g., "products" or "avatars")
     * @return Relative path string for DB storage (e.g., "products/uuid.jpg"), or null if no file is uploaded.
     * @throws IOException if an I/O error occurs
     */
    public static String saveUploadedFile(Part filePart, ServletContext context, String subFolder) throws IOException {
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }

        // Get original filename
        String submittedFileName = filePart.getSubmittedFileName();
        if (submittedFileName == null || submittedFileName.isEmpty()) {
            return null;
        }

        // Extract file extension (e.g. .jpg, .png)
        String extension = "";
        int dotIndex = submittedFileName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = submittedFileName.substring(dotIndex);
        }

        // Generate random filename with UUID to avoid collisions
        String newFileName = UUID.randomUUID().toString() + extension;

        // Physical storage directory: project/uploads/subFolder/
        String baseUploadPath = getUploadPath(context);
        File uploadFolder = new File(baseUploadPath + File.separator + subFolder);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs(); // Create directory if it does not exist
        }

        // Full physical file path
        String filePath = uploadFolder.getAbsolutePath() + File.separator + newFileName;

        // Write the file to disk
        filePart.write(filePath);

        // Return relative path for database storage (e.g., products/abc-xyz.jpg)
        return subFolder + "/" + newFileName;
    }
}
