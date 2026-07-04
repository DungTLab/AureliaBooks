/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aureliabooks.util;

import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Utility class for uploading files in Java Web Application.
 * Saves files to a directory outside Tomcat's deploy path to prevent deletion during clean/build.
 * @author DungLT
 */
public class UploadUtils {
    // Thư mục lưu trữ cố định bên ngoài thư mục build của Tomcat
    public static final String UPLOAD_DIR = "C:/AureliaBooks/uploads";

    /**
     * Saves an uploaded file part to the server.
     * 
     * @param filePart The Part object from request.getPart(...)
     * @param subFolder Subfolder name (e.g., "products" or "avatars")
     * @return The relative URL path to be stored in the database (e.g., "products/filename.jpg")
     *         or null if upload fails.
     * @throws IOException 
     */
    public static String saveUploadedFile(Part filePart, String subFolder) throws IOException {
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }

        // Lấy tên file gốc
        String submittedFileName = filePart.getSubmittedFileName();
        if (submittedFileName == null || submittedFileName.isEmpty()) {
            return null;
        }

        // Trích xuất phần mở rộng (extension) của file (ví dụ: .jpg, .png)
        String extension = "";
        int dotIndex = submittedFileName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = submittedFileName.substring(dotIndex);
        }

        // Tạo tên file ngẫu nhiên bằng UUID để tránh bị trùng tên
        String newFileName = UUID.randomUUID().toString() + extension;

        // Tạo đường dẫn thư mục lưu trữ vật lý: C:/AureliaBooks/uploads/subFolder
        File uploadFolder = new File(UPLOAD_DIR + File.separator + subFolder);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs(); // Tạo thư mục nếu chưa tồn tại
        }

        // Đường dẫn file vật lý đầy đủ
        String filePath = uploadFolder.getAbsolutePath() + File.separator + newFileName;
        
        // Lưu file vật lý xuống ổ đĩa
        filePart.write(filePath);

        // Trả về đường dẫn tương đối để lưu vào DB (ví dụ: products/abc-xyz.jpg)
        return subFolder + "/" + newFileName;
    }
}
