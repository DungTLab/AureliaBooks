package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.OrderDAO;
import com.mycompany.aureliabooks.model.Order;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Handles customer order history and admin order management requests.
 */
@WebServlet(name = "OrderController", urlPatterns = {"/orders", "/admin/orders"})
public class OrderController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // Use the full request URI to distinguish customer and admin routes reliably.
        if (requestURI.contains("/admin/orders")) {
            OrderDAO orderDAO = new OrderDAO();
            String status = request.getParameter("status");
            List<Order> orderList;

            // Apply the status filter only when a specific value is selected.
            if (status != null && !status.isEmpty() && !status.equals("ALL")) {
                orderList = orderDAO.getOrdersByStatus(status);

            } else {
                orderList = orderDAO.getAllOrders();
            }
            
            // Expose the data required by the admin list view.
            request.setAttribute("orderList", orderList);
            // Preserve the selected filter in the UI.
            request.setAttribute("selectedStatus", status);

            // Render the admin order list.
            request.getRequestDispatcher("/WEB-INF/order/admin_list.jsp").forward(request, response);
        } else {
            // Render the customer order history page.
            request.getRequestDispatcher("/WEB-INF/order/history.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String action = request.getParameter("action");

        if (requestURI.contains("/admin/orders")) {
            // Handle admin order status updates.
            if ("updateStatus".equals(action)) {
                try {
                    int orderId = Integer.parseInt(request.getParameter("orderId"));
                    String newStatus = request.getParameter("newStatus");

                    // TODO: Replace the hard-coded ID with the authenticated admin from session.
                    Integer adminId = 1;
                    OrderDAO orderDAO = new OrderDAO();
                    orderDAO.updateOrderStatus(orderId, newStatus, adminId);

                    // Keep the current filter applied after redirect.
                    String filterStatus = request.getParameter("filterStatus");
                    String redirectUrl = request.getContextPath() + "/admin/orders";
                    if (filterStatus != null && !filterStatus.isEmpty()) {
                        redirectUrl += "?status=" + filterStatus;
                    }

                    response.sendRedirect(redirectUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/admin/orders");
                }
            }
        } else {
            if ("return".equals(action)) {
                // TODO: Implement customer return handling.
            }
        }
    }
}
