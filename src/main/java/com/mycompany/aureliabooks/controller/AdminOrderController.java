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
 * Controller responsible for handling Admin Order Management operations.
 * 
 * DESIGN PRINCIPLES APPLIED:
 * - Single Responsibility Principle (SRP): Only handles Admin workflows,
 * separated from Customer workflows.
 * - DRY (Don't Repeat Yourself): Relies on upstream SecurityFilter for
 * Authentication & Role checks.
 * - PRG (Post-Redirect-Get): Prevents form resubmission anomalies on page
 * refresh.
 */
@WebServlet(name = "AdminOrderController", urlPatterns = { "/admin/orders" })
public class AdminOrderController extends HttpServlet {

    // Enterprise Standard: Use standard Logger instead of System.out or
    // e.printStackTrace()
    private static final Logger LOGGER = Logger.getLogger(AdminOrderController.class.getName());

    /**
     * Handles HTTP GET requests.
     * Renders the order list with pagination/search, or displays specific order
     * details.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            OrderDAO orderDAO = new OrderDAO();

            // -----------------------------------------------------------------
            // BLOCK 1: HANDLE 'DETAIL' VIEW
            // -----------------------------------------------------------------
            if ("detail".equals(action)) {
                String orderIdParam = request.getParameter("orderId");

                // Guard clause: Redirect to list if orderId is missing
                if (orderIdParam == null || orderIdParam.trim().isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/admin/orders");
                    return;
                }

                int orderId = Integer.parseInt(orderIdParam);
                Order order = orderDAO.getOrderById(orderId);

                // Return 404 Not Found if the requested order doesn't exist
                if (order == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found in the database.");
                    return;
                }

                // Forward data to the shared detail view
                request.setAttribute("order", order);
                request.getRequestDispatcher("/WEB-INF/order/detail.jsp").forward(request, response);
                return;
            }

            // -----------------------------------------------------------------
            // BLOCK 2: HANDLE 'LIST' VIEW (Pagination + Filtering + Searching)
            // -----------------------------------------------------------------

            // 1. Retrieve query parameters
            String status = request.getParameter("status");
            if (status == null || status.trim().isEmpty()) {
                status = "ALL"; // Default behavior
            }
            String search = request.getParameter("search"); // Retrieves search keyword

            // 2. Pagination calculation logic
            int page = 1;
            int pageSize = 10;
            String pageStr = request.getParameter("page");

            if (pageStr != null && !pageStr.isEmpty()) {
                try {
                    page = Integer.parseInt(pageStr);
                } catch (NumberFormatException e) {
                    page = 1; // Fallback to first page on malicious/invalid input
                }
            }

            // Ensure page is never zero or negative
            page = Math.max(1, page);

            // 3. Query Database for total records and calculate offsets
            int totalRecords = orderDAO.getTotalOrdersCount(status, search);
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

            // Prevent users from accessing out-of-bound pages
            if (page > totalPages && totalPages > 0) {
                page = totalPages;
            }

            // Absolute boundary safeguard: Prevents SQL exceptions caused by negative
            // offsets
            int offset = Math.max(0, (page - 1) * pageSize);

            // 4. Fetch the paginated and filtered list from DB
            List<Order> orderList = orderDAO.getOrdersPaged(status, search, offset, pageSize);

            // SLIDING WINDOW PAGINATION LOGIC
            int windowSize = 2; // Number of adjacent pages to display on each side of the current page
            int startPage = Math.max(1, page - windowSize);
            int endPage = Math.min(totalPages, page + windowSize);

            // 5. Attach data to request payload for JSP rendering
            request.setAttribute("orderList", orderList);
            request.setAttribute("selectedStatus", status);
            request.setAttribute("searchQuery", search); // Preserves search input value in UI
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("startPage", startPage);
            request.setAttribute("endPage", endPage);

            // Forward to presentation layer
            request.getRequestDispatcher("/WEB-INF/order/admin_list.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid payload format for Order ID in GET", e);
            request.setAttribute("errorMessage", "Tham số đường dẫn không hợp lệ.");
            request.getRequestDispatcher("/WEB-INF/error/400.jsp").forward(request, response);
            return;
        } catch (SQLException e) {
            // Log backend stack trace secretly; display generic 500 error page to user
            // securely
            LOGGER.log(Level.SEVERE, "Database connection or syntax error while fetching orders", e);
            request.setAttribute("errorMessage", "A database error occurred. Please contact IT support.");
            request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
            return;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected critical error in AdminOrderController", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected system error occurred.");
            return;
        }
    }

    /**
     * Handles HTTP POST requests.
     * Executes state-changing actions (Update Status) using the PRG pattern.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("updateStatus".equals(action)) {
            try {
                // 1. Extract payload from form submission
                int orderId = Integer.parseInt(request.getParameter("orderId"));
                String newStatus = request.getParameter("newStatus");

                // 2. Fetch authenticated admin identity guaranteed by upstream SecurityFilter
                HttpSession session = request.getSession(false);
                User admin = (User) session.getAttribute("user");
                int adminId = admin.getId(); // Used for audit trail (ProcessedByUserId)

                // 3. Execute DB Update
                OrderDAO orderDAO = new OrderDAO();
                boolean isUpdated = orderDAO.updateOrderStatus(orderId, newStatus, adminId);

                if (!isUpdated) {
                    session.setAttribute("errorMsg",
                            "Failed to update order status. Entity may have been modified or deleted.");
                }

                // 4. Post-Redirect-Get (PRG) Pattern Implementation
                // Reconstruct the previous URL state to maintain user's view (Search, Status,
                // Page)
                String filterStatus = request.getParameter("filterStatus");
                String search = request.getParameter("search");
                String page = request.getParameter("page");

                StringBuilder redirectUrl = new StringBuilder(request.getContextPath()).append("/admin/orders");
                boolean hasQueryParams = false;

                if (filterStatus != null && !filterStatus.trim().isEmpty() && !"ALL".equals(filterStatus)) {
                    redirectUrl.append("?status=").append(filterStatus);
                    hasQueryParams = true;
                }
                if (search != null && !search.trim().isEmpty()) {
                    redirectUrl.append(hasQueryParams ? "&" : "?").append("search=").append(search.trim());
                    hasQueryParams = true;
                }
                if (page != null && !page.trim().isEmpty()) {
                    redirectUrl.append(hasQueryParams ? "&" : "?").append("page=").append(page);
                }

                // Execute redirect to clear POST history
                response.sendRedirect(redirectUrl.toString());
                return;

            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "SQL Execution failed during order status update", e);
                request.setAttribute("errorMessage", "Database integrity error. Status update aborted.");
                request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
                return;
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid payload format for Order ID", e);
                request.setAttribute("errorMessage", "Định dạng ID không hợp lệ.");
                request.getRequestDispatcher("/WEB-INF/error/400.jsp").forward(request, response);
                return;
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown POST action requested.");
            return;
        }
    }
}