package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.OrderDAO;
import com.mycompany.aureliabooks.model.Order;
import com.mycompany.aureliabooks.model.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * AdminOrderController - Xử lý các yêu cầu quản lý đơn hàng dành cho Admin.
 * Được tách từ OrderController để tránh xung đột code khi làm việc nhóm.
 */
@WebServlet(name = "AdminOrderController", urlPatterns = {"/admin/orders"})
public class AdminOrderController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        OrderDAO orderDAO = new OrderDAO();
        String status = request.getParameter("status");
        if (status == null || status.isEmpty()) {
            status = "ALL";
        }

        int page = 1;
        int pageSize = 10;
        String pageStr = request.getParameter("page");
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        int totalRecords = orderDAO.getTotalOrdersCount(status);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        if (page > totalPages && totalPages > 0) {
            page = totalPages;
        }
        int offset = (page - 1) * pageSize;

        List<Order> orderList = orderDAO.getOrdersPaged(status, offset, pageSize);

        request.setAttribute("orderList", orderList);
        request.setAttribute("selectedStatus", status);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("/WEB-INF/order/admin_list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("updateStatus".equals(action)) {
            try {
                int orderId = Integer.parseInt(request.getParameter("orderId"));
                String newStatus = request.getParameter("newStatus");

                // Resolve the authenticated admin from the session.
                User admin = (User) request.getSession().getAttribute("user");
                Integer adminId = (admin != null) ? admin.getId() : 1;

                OrderDAO orderDAO = new OrderDAO();
                orderDAO.updateOrderStatus(orderId, newStatus, adminId);

                // Keep the current filter applied after redirect.
                String filterStatus = request.getParameter("filterStatus");
                String page = request.getParameter("page");
                String redirectUrl = request.getContextPath() + "/admin/orders";

                String queryParams = "";
                if (filterStatus != null && !filterStatus.isEmpty() && !filterStatus.equals("ALL")) {
                    queryParams += "?status=" + filterStatus;
                }
                if (page != null && !page.isEmpty()) {
                    queryParams += (queryParams.isEmpty() ? "?" : "&") + "page=" + page;
                }

                response.sendRedirect(redirectUrl + queryParams);
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/admin/orders");
            }
        }
    }
}
