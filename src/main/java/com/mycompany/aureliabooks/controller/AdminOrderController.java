package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.OrderDAO;
import com.mycompany.aureliabooks.model.Order;
import com.mycompany.aureliabooks.model.User;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controller responsible for Admin Order Management operations.
 * Handles listing, filtering, viewing details, and updating order statuses.
 * Enforces Role-Based Access Control (RBAC) to ensure only administrators can perform these actions.
 */
@WebServlet(name = "AdminOrderController", urlPatterns = {"/admin/orders"})
public class AdminOrderController extends HttpServlet {

    /**
     * Handles GET requests for displaying the order list and specific order details.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Security First: Verify authentication and authorization
        HttpSession session = request.getSession(false);
        User admin = (session != null) ? (User) session.getAttribute("user") : null;
        
        if (admin == null || !"ADMIN".equals(admin.getRoleName())) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access. Administrator privileges required.");
            return;
        }

        String action = request.getParameter("action");

        try {
            OrderDAO orderDAO = new OrderDAO();

            // 2. Handle 'detail' action to view specific order information
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
                // Forwards to the shared detail UI developed by Dev 4
                request.getRequestDispatcher("/WEB-INF/order/detail.jsp").forward(request, response);
                return;
            }

            // 3. Handle default action: List orders with pagination and filtering
            String status = request.getParameter("status");
            if (status == null || status.trim().isEmpty()) {
                status = "ALL";
            }

            int page = 1;
            int pageSize = 10;
            String pageStr = request.getParameter("page");
            
            if (pageStr != null && !pageStr.isEmpty()) {
                try {
                    page = Integer.parseInt(pageStr);
                } catch (NumberFormatException e) {
                    page = 1; // Fallback to first page on invalid input
                }
            }

            // Boundary safeguard: Prevent negative or zero page numbers
            page = Math.max(1, page);

            int totalRecords = orderDAO.getTotalOrdersCount(status);
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
            
            // Adjust page if it exceeds total pages, ensuring it doesn't drop below 1
            if (page > totalPages && totalPages > 0) {
                page = totalPages;
            }

            // Secure offset calculation: Prevents negative offsets causing SQL exceptions
            int offset = Math.max(0, (page - 1) * pageSize);

            List<Order> orderList = orderDAO.getOrdersPaged(status, offset, pageSize);

            request.setAttribute("orderList", orderList);
            request.setAttribute("selectedStatus", status);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);

            request.getRequestDispatcher("/WEB-INF/order/admin_list.jsp").forward(request, response);

        } catch (SQLException e) {
            // Delegate database exceptions to an error page rather than displaying stack traces to the user
            e.printStackTrace(); // Log for developers
            request.setAttribute("errorMessage", "A database error occurred while retrieving orders.");
            request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected system error occurred.");
        }
    }

    /**
     * Handles POST requests for modifying order data (e.g., updating order status).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Security First: Verify authentication and authorization
        HttpSession session = request.getSession(false);
        User admin = (session != null) ? (User) session.getAttribute("user") : null;
        
        if (admin == null || !"ADMIN".equals(admin.getRoleName())) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access. Administrator privileges required.");
            return;
        }

        String action = request.getParameter("action");

        if ("updateStatus".equals(action)) {
            try {
                int orderId = Integer.parseInt(request.getParameter("orderId"));
                String newStatus = request.getParameter("newStatus");

                // Strictly use the authenticated admin's ID from the session (Eliminated hardcoded fallback vulnerability)
                int adminId = admin.getId();

                OrderDAO orderDAO = new OrderDAO();
                boolean isUpdated = orderDAO.updateOrderStatus(orderId, newStatus, adminId);
                
                if (!isUpdated) {
                    request.getSession().setAttribute("errorMsg", "Failed to update order status. Order may not exist.");
                }

                // Preserve existing filters and pagination state upon redirection for better UX
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

            } catch (SQLException e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "Database error occurred while updating the order status.");
                request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Order ID format.");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action requested.");
        }
    }
}