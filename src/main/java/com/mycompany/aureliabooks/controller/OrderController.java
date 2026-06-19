/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
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
 * Order History & Order Management Controller. Created like NetBeans Maven
 * template.
 *
 * @author DungLT
 */
@WebServlet(name = "OrderController", urlPatterns = {"/orders", "/admin/orders"})
public class OrderController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // Đường dẫn đầy đủ giúp an toàn hơn tránh nhận diện sai
        if (requestURI.contains("/admin/orders")) {
            OrderDAO orderDAO = new OrderDAO();
            String status = request.getParameter("status");
            List<Order> orderList;

            // Nếu có chọn trạng thái cụ thể và không phải là "ALL";
            if (status != null && !status.isEmpty() && !status.equals("ALL")) {
                orderList = orderDAO.getOrdersByStatus(status);

            } else {
                orderList = orderDAO.getAllOrders();
            }
            
            // Gửi dữ liệu qua JSP
            request.setAttribute("orderList", orderList);
            // Giữ lại giá trị vừa chọn trên thẻ select
            request.setAttribute("selectedStatus", status);

            // Hiển thị danh sách đơn cho Admin
            request.getRequestDispatcher("/WEB-INF/order/admin_list.jsp").forward(request, response);
        } else {
            // Hiển thị lịch sử mua hàng cho Customer
            request.getRequestDispatcher("/WEB-INF/order/history.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String action = request.getParameter("action");

        if (requestURI.contains("/admin/orders")) {
            // Chức năng: Update Trạng thái Đơn hàng
            if ("updateStatus".equals(action)) {
                try {
                    int orderId = Integer.parseInt(request.getParameter("orderId"));
                    String newStatus = request.getParameter("newStatus");

                    // TODO: Lấy ID admin thực tế từ Session. Tạm thời fix cứng là 1 cho quá trình dev.
                    Integer adminId = 1;
                    OrderDAO orderDAO = new OrderDAO();
                    orderDAO.updateOrderStatus(orderId, newStatus, adminId);

                    // Giữ lại bộ lọc hiện tại sau khi redirect
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
                // Xử lý logic cho khách hàng trả hàng...
            }
        }
    }
}
