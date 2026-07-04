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
 * Admin Discount/Voucher CRUD Controller.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
@WebServlet(name = "DiscountController", urlPatterns = {"/admin/discounts"})
public class DiscountController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("create".equals(action)) {
            request.getRequestDispatcher("/WEB-INF/discount/create.jsp").forward(request, response);
        } else if ("update".equals(action)) {
            request.getRequestDispatcher("/WEB-INF/discount/update.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/WEB-INF/discount/list.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("create".equals(action)) {
            // Xử lý thêm mới voucher
        } else if ("update".equals(action)) {
            // Xử lý cập nhật voucher
        } else if ("delete".equals(action)) {
            // Xử lý ẩn/xóa voucher
        }
    }
}
