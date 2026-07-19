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

/**
 * Controller responsible for handling customer-facing order operations.
 * Manages actions such as viewing order history, viewing order details, 
 * requesting order cancellations, and requesting order returns.
 */
@WebServlet(name = "OrderController", urlPatterns = {"/orders"})
public class OrderController extends HttpServlet {

    /**
     * Handles GET requests. Used primarily for displaying order lists and specific order details.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loggedUser = (User) request.getSession().getAttribute("user");
        
        // Protect access: Redirect unauthenticated users
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        String action = request.getParameter("action");
        if ("detail".equals(action)) {
            // Action: View the detailed blueprint of a specific order
            try {
                int orderId = Integer.parseInt(request.getParameter("id"));
                OrderDAO orderDAO = new OrderDAO();
                Order order = orderDAO.getOrderById(orderId);
                
                // Security Check: Only allow the user to view their own order
                if (order != null && order.getUserId() == loggedUser.getId()) {
                    request.setAttribute("order", order);
                    request.getRequestDispatcher("/WEB-INF/order/detail.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/orders");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/orders");
            }
        } else {
            // Default Action: Display the user's complete order history
            OrderDAO orderDAO = new OrderDAO();
            try {
                List<Order> orders = orderDAO.getOrdersByUserId(loggedUser.getId());
                request.setAttribute("orders", orders);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            request.getRequestDispatcher("/WEB-INF/order/history.jsp").forward(request, response);
        }
    }

    /**
     * Handles POST requests. Used primarily for state-modifying actions like returning or cancelling an order.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User loggedUser = (User) request.getSession().getAttribute("user");
        
        // Protect POST actions from unauthenticated access
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        String action = request.getParameter("action");

        if ("return".equals(action)) {
            // Action: Process a customer's request to return a completed order
            try {
                int orderId = Integer.parseInt(request.getParameter("orderId"));
                String returnReason = request.getParameter("returnReason");
                
                if (returnReason == null || returnReason.trim().length() < 10 || returnReason.trim().length() > 500) {
                    request.getSession().setAttribute("errorMessage", "Lý do trả hàng phải từ 10 đến 500 ký tự.");
                    response.sendRedirect(request.getContextPath() + "/orders");
                    return;
                }
                
                OrderDAO orderDAO = new OrderDAO();
                Order order = orderDAO.getOrderById(orderId);
                
                // Security Check: Ensure ownership
                if (order != null && order.getUserId() == loggedUser.getId()) {
                    boolean success = orderDAO.requestOrderReturn(orderId, returnReason);
                    if (success) {
                        request.getSession().setAttribute("successMessage", "Yêu cầu trả đơn hàng #" + orderId + " đã được gửi thành công và đang chờ quản trị viên phê duyệt.");
                    } else {
                        request.getSession().setAttribute("errorMessage", "Không thể gửi yêu cầu trả hàng. Đơn hàng phải ở trạng thái đã hoàn thành (COMPLETED) hoặc bị từ chối trả hàng trước đó.");
                    }
                } else {
                    request.getSession().setAttribute("errorMessage", "You do not have permission to perform this action.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("errorMessage", "An error occurred while processing your request.");
            }
            response.sendRedirect(request.getContextPath() + "/orders");
            
        } else if ("cancel".equals(action)) {
            // Action: Process a customer's request to cancel an order that is pending or shipping
            try {
                int orderId = Integer.parseInt(request.getParameter("orderId"));
                OrderDAO orderDAO = new OrderDAO();
                Order order = orderDAO.getOrderById(orderId);
                
                // Security Check: Ensure ownership
                if (order != null && order.getUserId() == loggedUser.getId()) {
                    // State Validation: Can only cancel if currently Pending or Shipping
                    if ("PENDING".equals(order.getStatus()) || "SHIPPING".equals(order.getStatus())) {
                        // Pass null for processedByUserId since this is initiated by the user
                        boolean success = orderDAO.updateOrderStatus(orderId, "CANCELLED", null);
                        if (success) {
                            request.getSession().setAttribute("successMessage", "Successfully cancelled Order #" + orderId + ".");
                        } else {
                            request.getSession().setAttribute("errorMessage", "Unable to cancel the order at this time.");
                        }
                    } else {
                        request.getSession().setAttribute("errorMessage", "The order is not in a cancellable state.");
                    }
                } else {
                    request.getSession().setAttribute("errorMessage", "You do not have permission to perform this action.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("errorMessage", "An error occurred while cancelling your order.");
            }
            
            // Redirect back to the page the user came from (either history list or details view)
            String redirectUrl = request.getHeader("referer");
            if (redirectUrl != null) {
                response.sendRedirect(redirectUrl);
            } else {
                response.sendRedirect(request.getContextPath() + "/orders");
            }
        }
    }
}
