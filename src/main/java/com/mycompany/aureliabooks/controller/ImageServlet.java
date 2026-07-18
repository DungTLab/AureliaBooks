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
 * ImageServlet - Khung xương xử lý hiển thị ảnh động từ thư mục ngoài webapp.
 * [Nhiệm vụ của thành viên nhóm: Triển khai code đọc file vật lý và trả về stream]
 * 
 * Đường dẫn gọi Servlet: http://localhost:8080/AureliaBooks/uploads/...
 * 
 * @author DungLT
 */
@WebServlet(name = "ImageServlet", urlPatterns = {"/uploads/*"})
public class ImageServlet extends HttpServlet {

    /**
     * Xử lý yêu cầu lấy ảnh từ trình duyệt (GET Request).
     * 
     * Hướng dẫn xử lý bên trong doGet:
     * 1. Lấy đường dẫn ảnh chi tiết từ request.getPathInfo()
     * 2. Lấy đường dẫn thư mục uploads ngoài webapp (Sử dụng getServletContext().getRealPath("/") và đi ngược lên 2 cấp)
     * 3. Trỏ tới file ảnh vật lý trên ổ đĩa
     * 4. Kiểm tra file tồn tại, nếu không trả về 404 (SC_NOT_FOUND)
     * 5. Xác định MIME Type của ảnh bằng getServletContext().getMimeType()
     * 6. Thiết lập contentType cho response
     * 7. Đọc dữ liệu bytes từ file ảnh bằng FileInputStream và ghi ra response.getOutputStream()
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. Lấy đường dẫn ảnh từ URL request (ví dụ: /products/filename.jpg)
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 2. Dựng đường dẫn vật lý động trỏ tới thư mục uploads ngoài gốc project
        String baseUploadPath = com.mycompany.aureliabooks.util.UploadUtils.getUploadPath(getServletContext());
        java.io.File file = new java.io.File(baseUploadPath + pathInfo);


        // 3. Kiểm tra file tồn tại
        if (!file.exists() || !file.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 4. Xác định MIME Type (Content-Type)
        String contentType = getServletContext().getMimeType(file.getName());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        response.setContentType(contentType);
        response.setContentLength((int) file.length());

        // 5. Đọc file và xuất ra OutputStream của Response
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
