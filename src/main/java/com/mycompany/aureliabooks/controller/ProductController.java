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
import java.util.List;

/**
 * Customer Product Catalog Browser Controller. Handles product search, filters,
 * listing, pagination, and detail view. Created like NetBeans Maven template.
 *
 * @author DungLT
 */
@WebServlet(name = "ProductController", urlPatterns = {"/products", "/product"})
public class ProductController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        ProductDAO productDAO = new ProductDAO();

        if ("detail".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                HashMap<String, Object> product = productDAO.getProductFullInformationById(id);
                request.setAttribute("product", product);
            } catch (Exception e) {
                e.printStackTrace();
            }
            request.getRequestDispatcher("/WEB-INF/view/book-detail.jsp").forward(request, response);
        } else {
            String query = request.getParameter("query");
            int productsPerPage = 15;
            int currentPage = 1;
            String pageParam = request.getParameter("page");

            if (pageParam != null && !pageParam.isEmpty()) {
                try {
                    currentPage = Integer.parseInt(pageParam);
                    if (currentPage < 1) {
                        currentPage = 1;
                    }
                } catch (NumberFormatException e) {
                    currentPage = 1;
                }
            }

            int totalProducts = productDAO.countSearchProducts(query, 0);
            int totalPages = (int) Math.ceil((double) totalProducts / productsPerPage);
            if (totalPages < 1) {
                totalPages = 1;
            }
            if (currentPage > totalPages) {
                currentPage = totalPages;
            }

            int offset = (currentPage - 1) * productsPerPage;
            List<Product> products = productDAO.searchProducts(query, 0, offset, productsPerPage);

            request.setAttribute("products", products);
            request.setAttribute("query", query == null ? "" : query.trim());
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalProducts", totalProducts);

            request.getRequestDispatcher("/WEB-INF/product/search.jsp").forward(request, response);
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
