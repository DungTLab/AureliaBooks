/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.ProductDAO;
import com.mycompany.aureliabooks.model.Product;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * Customer Product Catalog Browser Controller. Handles product search, filters,
 * listing, pagination, and detail view. Created like NetBeans Maven template.
 *
 * @author DungLT
 */
@WebServlet(name = "ProductController", urlPatterns = {"/products"})
public class ProductController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("detail".equals(action)) {
            ProductDAO productDAO = new ProductDAO();

            HashMap<String, Object> product = productDAO.getProductFullInformationById(Integer.parseInt(request.getParameter("id")));
            request.setAttribute("product", product);
            request.getRequestDispatcher("/WEB-INF/view/book-detail.jsp").forward(request, response);
        } else {
            // Xem danh sách và tìm kiếm
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Do get handles search, post not used here
        doGet(request, response);
    }
}
