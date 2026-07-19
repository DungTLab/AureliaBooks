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
                
                if (product == null || product.isEmpty()) {
                    request.setAttribute("errorMessage", "Không tìm thấy thông tin cho sản phẩm này.");
                    request.getRequestDispatcher("/WEB-INF/error/404.jsp").forward(request, response);
                    return;
                }
                
                // Add stock information for detail view
                int stock = productDAO.getProductStock(id);
                product.put("stock", stock);
                
                request.setAttribute("product", product);
                request.getRequestDispatcher("/WEB-INF/view/book-detail.jsp").forward(request, response);
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Định dạng ID sản phẩm không hợp lệ.");
                request.getRequestDispatcher("/WEB-INF/error/400.jsp").forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "Đã xảy ra lỗi khi lấy thông tin sản phẩm: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
            }
        } else {
            String query = request.getParameter("query");
            String catParam = request.getParameter("categoryId");
            int categoryId = 0;
            if (catParam != null && !catParam.isEmpty()) {
                try {
                    categoryId = Integer.parseInt(catParam);
                } catch (NumberFormatException e) {
                    categoryId = 0;
                }
            }

            int productsPerPage = 15;
            int currentPage = 1;
            String pageParam = request.getParameter("page");

            if (pageParam != null && !pageParam.isEmpty()) {
                try {
                    currentPage = Integer.parseInt(pageParam);
                } catch (NumberFormatException e) {
                    currentPage = 1;
                }
            }
            currentPage = Math.max(1, currentPage);

            int totalProducts = productDAO.countSearchProducts(query, categoryId);
            int totalPages = (int) Math.ceil((double) totalProducts / productsPerPage);
            if (currentPage > totalPages && totalPages > 0) {
                currentPage = totalPages;
            }

            int offset = Math.max(0, (currentPage - 1) * productsPerPage);
            List<Product> products = productDAO.searchProducts(query, categoryId, offset, productsPerPage);

            int windowSize = 2;
            int startPage = Math.max(1, currentPage - windowSize);
            int endPage = Math.min(totalPages, currentPage + windowSize);

            request.setAttribute("products", products);
            request.setAttribute("query", query == null ? "" : query.trim());
            request.setAttribute("categoryId", categoryId);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("startPage", startPage);
            request.setAttribute("endPage", endPage);
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
