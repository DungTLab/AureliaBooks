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
     * Lấy đường dẫn tuyệt đối của thư mục "uploads" nằm ở gốc dự án một cách tự động.
     * Thuật toán: dò ngược từ thư mục deploy thực tế (target/...) lên 2 cấp để ra thư mục project.
     *
     * @param context ServletContext của servlet đang gọi
     * @return Đường dẫn tuyệt đối tới thư mục uploads (ví dụ: D:/Project/uploads)
     */
    public static String getUploadPath(ServletContext context) {
        // Đường dẫn thực tế khi deploy (ví dụ: D:\Project\target\AureliaBooks-1.0-SNAPSHOT\)
        String deployPath = context.getRealPath("/");
        File deployDir = new File(deployPath);

        // Đi ngược lên 2 cấp để ra ngoài thư mục target/ và build/
        File projectRootDir = deployDir.getParentFile().getParentFile();

        // Trỏ tới thư mục "uploads" ở gốc project
        File uploadsDir = new File(projectRootDir, "uploads");
        if (!uploadsDir.exists()) {
            uploadsDir.mkdirs(); // Tạo thư mục nếu chưa tồn tại
        }
        return uploadsDir.getAbsolutePath();
    }

    /**
     * Lưu file tải lên vào thư mục vật lý và trả về đường dẫn tương đối để lưu DB.
     *
     * @param filePart  Đối tượng Part nhận từ Request
     * @param context   ServletContext để dò tìm đường dẫn động
     * @param subFolder Thư mục con (ví dụ: "products" hoặc "avatars")
     * @return Chuỗi đường dẫn tương đối lưu vào DB (ví dụ: "products/uuid.jpg"),
     *         hoặc null nếu không có file được tải lên.
     * @throws IOException
     */
    public static String saveUploadedFile(Part filePart, ServletContext context, String subFolder) throws IOException {
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

        // Thư mục lưu trữ vật lý: project/uploads/subFolder/
        String baseUploadPath = getUploadPath(context);
        File uploadFolder = new File(baseUploadPath + File.separator + subFolder);
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
