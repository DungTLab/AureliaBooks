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
        // TODO: Thành viên nhóm triển khai code đọc và stream ảnh ở đây
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Không sử dụng doPost cho việc hiển thị ảnh
        doGet(request, response);
    }
}
