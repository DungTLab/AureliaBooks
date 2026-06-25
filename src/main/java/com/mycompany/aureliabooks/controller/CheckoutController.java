/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.CartDAO;
import com.mycompany.aureliabooks.model.CartItem;
import com.mycompany.aureliabooks.model.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;

/**
 * Controller responsible for handling the order checkout process.
 * Validates the user's session, calculates the final cart total, and
 * delegates the order creation process to the CartDAO.
 * 
 * @author DungLT
 */
@WebServlet(name = "CheckoutController", urlPatterns = {"/checkout"})
public class CheckoutController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect the user to the cart page where the checkout form is located
        request.getRequestDispatcher("/WEB-INF/cart/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        User loggedUser = (User) session.getAttribute("user");
        
        if (loggedUser == null) {
            // Prevent unauthorized checkout attempts
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }
        
        // Extract shipping details submitted via the checkout form
        String shippingAddress = request.getParameter("shippingAddress");
        String contactPhone = request.getParameter("contactPhone");

        CartDAO cartDAO = new CartDAO();
        List<CartItem> cartItems = cartDAO.findAll(loggedUser.getId());
        
        // Calculate the total amount for the order
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            BigDecimal itemTotal = item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        // Only proceed if the cart is not empty and has a positive total
        if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
            // Attempt to create the order and clear the cart in a single transaction
            boolean success = cartDAO.createOrder(loggedUser.getId(), shippingAddress, contactPhone, totalAmount);
            if (success) {
                // Redirect back to the cart with a success indicator
                response.sendRedirect(request.getContextPath() + "/cart?checkout=success");
                return;
            }
        }
        
        // Redirect back to the cart with a failure indicator if order creation fails or cart is empty
        response.sendRedirect(request.getContextPath() + "/cart?checkout=failed");
    }
}
