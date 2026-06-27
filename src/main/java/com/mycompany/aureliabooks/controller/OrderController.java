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
        // Render the customer order history page.
        request.getRequestDispatcher("/WEB-INF/order/history.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("return".equals(action)) {
            // TODO: Implement customer return processing.
        }
    }
}
