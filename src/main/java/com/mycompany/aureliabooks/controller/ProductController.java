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
 * Customer Product Catalog Browser Controller.
 * Handles product search, filters, listing, pagination, and detail view.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
@WebServlet(name = "ProductController", urlPatterns = {"/products", "/product"})
public class ProductController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();
        if ("/product".equals(servletPath)) {
            String view = request.getParameter("view");
            if (view == null || view.equals("list")) {
                request.getRequestDispatcher("/WEB-INF/product/list.jsp").forward(request, response);
            } else if (view.equals("create")) {
                request.getRequestDispatcher("/WEB-INF/product/create.jsp").forward(request, response);
            } else if (view.equals("update")) {
                request.getRequestDispatcher("/WEB-INF/product/update.jsp").forward(request, response);
            } else if (view.equals("delete")) {
                request.getRequestDispatcher("/WEB-INF/product/delete.jsp").forward(request, response);
            }
        } else {
            String action = request.getParameter("action");
            if ("detail".equals(action)) {
                // Xem chi tiết
                request.getRequestDispatcher("/WEB-INF/view/book-detail.jsp").forward(request, response);
            } else {
                // Xem danh sách và tìm kiếm
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();
        if ("/product".equals(servletPath)) {
            // Admin POST CRUD actions (To be implemented by Dev 2)
        } else {
            doGet(request, response);
        }
    }
}
