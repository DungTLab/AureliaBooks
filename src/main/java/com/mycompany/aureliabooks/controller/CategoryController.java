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
 * Admin Category CRUD Controller.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
@WebServlet(name = "CategoryController", urlPatterns = {"/admin/categories"})
public class CategoryController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("create".equals(action)) {
            request.getRequestDispatcher("/WEB-INF/category/create.jsp").forward(request, response);
        } else if ("update".equals(action)) {
            request.getRequestDispatcher("/WEB-INF/category/update.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/WEB-INF/category/list.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("create".equals(action)) {
            // Xử lý thêm mới danh mục
        } else if ("update".equals(action)) {
            // Xử lý cập nhật danh mục
        } else if ("delete".equals(action)) {
            // Xử lý xóa danh mục
        }
    }
}
