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

/**
 * OrderController - Xử lý các yêu cầu liên quan đến đơn hàng của Khách hàng (Customer).
 * Ví dụ: Xem lịch sử mua hàng, chi tiết đơn mua, và gửi yêu cầu trả hàng.
 */
@WebServlet(name = "OrderController", urlPatterns = {"/orders"})
public class OrderController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loggedUser = (User) request.getSession().getAttribute("user");
        
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        String action = request.getParameter("action");
        if ("detail".equals(action)) {
            try {
                int orderId = Integer.parseInt(request.getParameter("id"));
                OrderDAO orderDAO = new OrderDAO();
                Order order = orderDAO.getOrderById(orderId);
                
                // Security check: Only allow the user to view their own order
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
            // Default or view history
            OrderDAO orderDAO = new OrderDAO();
            try {
                java.util.List<Order> orders = orderDAO.getOrdersByUserId(loggedUser.getId());
                request.setAttribute("orders", orders);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
            request.getRequestDispatcher("/WEB-INF/order/history.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User loggedUser = (User) request.getSession().getAttribute("user");
        
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        String action = request.getParameter("action");

        if ("return".equals(action)) {
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
                
                // Security check
                if (order != null && order.getUserId() == loggedUser.getId()) {
                    boolean success = orderDAO.requestOrderReturn(orderId, returnReason);
                    if (success) {
                        request.getSession().setAttribute("successMessage", "Yêu cầu trả hàng đơn #" + orderId + " đã được ghi nhận. Số tiền sẽ sớm được hoàn lại.");
                    } else {
                        request.getSession().setAttribute("errorMessage", "Không thể xử lý yêu cầu trả hàng. Đơn hàng có thể chưa hoàn thành hoặc đã được xử lý.");
                    }
                } else {
                    request.getSession().setAttribute("errorMessage", "Bạn không có quyền thực hiện thao tác này.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình xử lý.");
            }
            response.sendRedirect(request.getContextPath() + "/orders");
        }
    }
}
