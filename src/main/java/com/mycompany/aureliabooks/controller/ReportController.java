/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.OrderDAO;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Admin Statistics & Report Dashboard Controller.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
@WebServlet(name = "ReportController", urlPatterns = {"/admin/reports"})
public class ReportController extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String type = request.getParameter("type");
        if (type == null || type.trim().isEmpty()) {
            type = "DAY";
        } else {
            type = type.trim().toUpperCase();
            if (!type.equals("DAY") && !type.equals("MONTH") && !type.equals("QUARTER")) {
                type = "DAY";
            }
        }

        Map<String, Double> revenueData = new java.util.LinkedHashMap<>();
        List<Map<String, Object>> bestSellingData = new java.util.ArrayList<>();
        try {
            revenueData = orderDAO.getRevenueReport(type);
            bestSellingData = orderDAO.getBestSellingReport(type);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi truy xuất báo cáo: " + e.getMessage());
        }

        request.setAttribute("reportType", type);
        request.setAttribute("revenueData", revenueData);
        request.setAttribute("bestSellingData", bestSellingData);

        request.getRequestDispatcher("/WEB-INF/report/dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
