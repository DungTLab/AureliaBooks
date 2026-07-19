/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.aureliabooks.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * ImageServlet - Handles serving uploaded image files from directory outside webapp root.
 * 
 * URL Pattern: http://localhost:8080/AureliaBooks/uploads/...
 * 
 * @author DungLT
 */
@WebServlet(name = "ImageServlet", urlPatterns = {"/uploads/*"})
public class ImageServlet extends HttpServlet {

    /**
     * Handles HTTP GET requests to serve image files.
     * 
     * Processing steps:
     * 1. Get detailed image path from request.getPathInfo()
     * 2. Get absolute uploads directory path outside webapp (using UploadUtils.getUploadPath)
     * 3. Reference physical file on disk
     * 4. Check if file exists, else return 404 (SC_NOT_FOUND)
     * 5. Determine MIME Type of the image
     * 6. Set response content type
     * 7. Stream file bytes to response output stream
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. Get image path from URL request (e.g. /products/filename.jpg)
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 2. Resolve physical uploads path pointing outside project root
        String baseUploadPath = com.mycompany.aureliabooks.util.UploadUtils.getUploadPath(getServletContext());
        java.io.File file = new java.io.File(baseUploadPath + pathInfo);


        // 3. Verify file existence
        if (!file.exists() || !file.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 4. Resolve MIME Type (Content-Type)
        String contentType = getServletContext().getMimeType(file.getName());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        response.setContentType(contentType);
        response.setContentLength((int) file.length());

        // 5. Read file and write to Response OutputStream
        try (java.io.FileInputStream fis = new java.io.FileInputStream(file);
             java.io.OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
