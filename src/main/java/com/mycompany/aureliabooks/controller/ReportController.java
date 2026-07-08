package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.OrderDAO;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Admin Statistics & Report Dashboard Controller.
 */
@WebServlet(name = "ReportController", urlPatterns = {"/admin/reports"})
public class ReportController extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String type = request.getParameter("type");
        String selectedPeriod = request.getParameter("period");
        if (type == null || type.trim().isEmpty()) {
            type = "DAY";
        }

        Map<String, Double> revenueData = new LinkedHashMap<>();
        Map<String, Object> revenueSummary = createEmptyRevenueSummary();
        List<Map<String, Object>> bestSellingData = new ArrayList<>();

        try {
            revenueData = orderDAO.getRevenueReport(type);
            revenueSummary = orderDAO.getRevenueSummary();
            if ((selectedPeriod == null || selectedPeriod.trim().isEmpty()) && !revenueData.isEmpty()) {
                selectedPeriod = revenueData.keySet().iterator().next();
            }
            bestSellingData = orderDAO.getBestSellingReport(type, selectedPeriod);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi truy xuất báo cáo: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
            return;
        }

        request.setAttribute("reportType", type);
        request.setAttribute("selectedPeriod", selectedPeriod);
        request.setAttribute("revenueSummary", revenueSummary);
        request.setAttribute("revenueData", revenueData);
        request.setAttribute("bestSellingData", bestSellingData);

        request.getRequestDispatcher("/WEB-INF/report/dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private Map<String, Object> createEmptyRevenueSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalRevenue", BigDecimal.ZERO);
        summary.put("totalOrders", 0);
        summary.put("averageOrderValue", BigDecimal.ZERO);
        summary.put("totalSoldQuantity", 0);
        return summary;
    }
}
