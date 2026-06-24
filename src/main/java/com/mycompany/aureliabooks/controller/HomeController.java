/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.ProductDAO;
import com.mycompany.aureliabooks.model.Product;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Home Page Controller. Handles home page routing, loads active products to
 * display on home page. Created like NetBeans Maven template.
 *
 * @author DungLT
 */
@WebServlet(name = "HomeController", urlPatterns = {"/home", ""})
public class HomeController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ProductDAO productDAO = new ProductDAO();

        // 3. Xử lý phân trang: 3 sản phẩm/trang ( hàng, mỗi hàng 1 sản phẩm)
        int productsPerPage = 3;
        int currentPage = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }
        int offset = (currentPage - 1) * productsPerPage;

        // 4. Đếm tổng số sản phẩm đang bán (tất cả category) để tính số trang
        int totalBooks = productDAO.countTopSellingProductsOfMonth();
        int totalPages = (int) Math.ceil((double) totalBooks / productsPerPage);
        if (totalPages < 1) totalPages = 1;
        if (currentPage > totalPages) currentPage = totalPages;

        // 5. Tải danh sách sản phẩm bán chạy nhất trong tháng (không phân biệt category)
        List<HashMap<String, Object>> listTopSaleProducts = productDAO.getTopSellingProductsOfMonth(offset, productsPerPage);
        request.setAttribute("listTopSaleProducts", listTopSaleProducts);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);

        // Chuyển tiếp yêu cầu sang trang hiển thị index.jsp
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
