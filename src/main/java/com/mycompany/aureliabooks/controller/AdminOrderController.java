package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.OrderDAO;
import com.mycompany.aureliabooks.model.Order;
import com.mycompany.aureliabooks.model.User;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controller responsible for Admin Order Management operations.
 * Handles listing, filtering, viewing details, and updating order statuses.
 * Note: Authentication and RBAC are handled upstream by SecurityFilter.
 */
@WebServlet(name = "AdminOrderController", urlPatterns = { "/admin/orders" })
public class AdminOrderController extends HttpServlet {

    // Enterprise Standard: Initialize a Logger for this specific class
    private static final Logger LOGGER = Logger.getLogger(AdminOrderController.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            OrderDAO orderDAO = new OrderDAO();

            if ("detail".equals(action)) {
                String orderIdParam = request.getParameter("orderId");
                if (orderIdParam == null || orderIdParam.trim().isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/admin/orders");
                    return;
                }

                int orderId = Integer.parseInt(orderIdParam);
                Order order = orderDAO.getOrderById(orderId);

                if (order == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found.");
                    return;
                }

                request.setAttribute("order", order);
                request.getRequestDispatcher("/WEB-INF/order/detail.jsp").forward(request, response);
                return;
            }

            // Default action: List orders with pagination and filtering
            String status = request.getParameter("status");
            if (status == null || status.trim().isEmpty()) {
                status = "ALL";
            }

            String search = request.getParameter("search");

            int page = 1;
            int pageSize = 10;
            String pageStr = request.getParameter("page");

            if (pageStr != null && !pageStr.isEmpty()) {
                try {
                    page = Integer.parseInt(pageStr);
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }

            page = Math.max(1, page);
            int totalRecords = orderDAO.getTotalOrdersCount(status, search); // Truyền thêm search

            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

            if (page > totalPages && totalPages > 0) {
                page = totalPages;
            }

            int offset = Math.max(0, (page - 1) * pageSize);
            List<Order> orderList = orderDAO.getOrdersPaged(status, search, offset, pageSize);
            request.setAttribute("orderList", orderList);
            request.setAttribute("selectedStatus", status);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("searchQuery", search);

            request.getRequestDispatcher("/WEB-INF/order/admin_list.jsp").forward(request, response);

        } catch (SQLException e) {
            // Enterprise Standard: Use Logger instead of e.printStackTrace()
            LOGGER.log(Level.SEVERE, "Database error while fetching orders", e);
            request.setAttribute("errorMessage", "A database error occurred while retrieving orders.");
            request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
            return;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error in AdminOrderController", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected system error occurred.");
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("updateStatus".equals(action)) {
            try {
                int orderId = Integer.parseInt(request.getParameter("orderId"));
                String newStatus = request.getParameter("newStatus");

                // Retrieve the guaranteed admin session from SecurityFilter
                HttpSession session = request.getSession(false);
                User admin = (User) session.getAttribute("user");
                int adminId = admin.getId();

                OrderDAO orderDAO = new OrderDAO();
                boolean isUpdated = orderDAO.updateOrderStatus(orderId, newStatus, adminId);

                if (!isUpdated) {
                    session.setAttribute("errorMsg", "Failed to update order status. Order may not exist.");
                }

                // Preserve filters and pagination during PRG (Post-Redirect-Get)
                String filterStatus = request.getParameter("filterStatus");
                String page = request.getParameter("page");
                StringBuilder redirectUrl = new StringBuilder(request.getContextPath()).append("/admin/orders");

                boolean hasQueryParams = false;
                if (filterStatus != null && !filterStatus.trim().isEmpty() && !"ALL".equals(filterStatus)) {
                    redirectUrl.append("?status=").append(filterStatus);
                    hasQueryParams = true;
                }

                if (page != null && !page.trim().isEmpty()) {
                    redirectUrl.append(hasQueryParams ? "&" : "?").append("page=").append(page);
                }

                response.sendRedirect(redirectUrl.toString());
                return; // Stop execution after redirect

            } catch (SQLException e) {
                // Enterprise Standard: Log error and forward to 500 page securely
                LOGGER.log(Level.SEVERE, "Database error while updating order status", e);
                request.setAttribute("errorMessage", "Database error occurred while updating the order status.");
                request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
                return;
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid Order ID format submitted", e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Order ID format.");
                return;
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action requested.");
            return;
        }
    }
}