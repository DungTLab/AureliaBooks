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
 * Order History & Order Management Controller.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
@WebServlet(name = "OrderController", urlPatterns = {"/orders", "/admin/orders"})
public class OrderController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/admin/")) {
            // Hiển thị danh sách đơn cho Admin
            request.getRequestDispatcher("/WEB-INF/order/admin_list.jsp").forward(request, response);
        } else {
            // Hiển thị lịch sử mua hàng cho Customer
            request.getRequestDispatcher("/WEB-INF/order/history.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String action = request.getParameter("action");
        
        if (requestURI.contains("/admin/")) {
            if ("updateStatus".equals(action)) {
                // Xử lý cập nhật trạng thái đơn (Pending -> Completed)
            }
        } else {
            if ("return".equals(action)) {
                // Xử lý Khách hàng yêu cầu Trả hàng
            }
        }
    }
}
