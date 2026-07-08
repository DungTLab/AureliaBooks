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
 * Controller responsible for handling shopping cart operations.
 * Processes requests to view the cart, add items, update quantities, and remove items.
 * Ensures the user is authenticated via session before allowing access to cart features.
 *
 * @author DungLT
 */
@WebServlet(name = "CartController", urlPatterns = {"/cart"})
public class CartController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        HttpSession session = request.getSession();

        User loggedUser = (User) session.getAttribute("user");

        if (loggedUser == null) {
            // Redirect unauthenticated users to the login page
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
        } else {

            // Default to 'view' action if no specific action parameter is provided
            if (action == null || action.equals("view")) {

                CartDAO cartDAO = new CartDAO();
                com.mycompany.aureliabooks.dao.ProductDAO productDAO = new com.mycompany.aureliabooks.dao.ProductDAO();
                
                // Fetch the user's cart items from the database
                List<CartItem> cartItems = cartDAO.findAll(loggedUser.getId());
                // Calculate the total price of all items in the cart
                BigDecimal cartTotal = BigDecimal.ZERO;
                java.util.Map<Integer, Integer> stockMap = new java.util.HashMap<>();
                for (CartItem item : cartItems) {
                    BigDecimal itemTotal = item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity()));
                    cartTotal = cartTotal.add(itemTotal);
                    stockMap.put(item.getProductId(), productDAO.getProductStock(item.getProductId()));
                }

                request.setAttribute("cartItems", cartItems);
                request.setAttribute("cartTotal", cartTotal);
                request.setAttribute("stockMap", stockMap);

                // Forward the attributes to the JSP view for rendering
                request.getRequestDispatcher("/WEB-INF/cart/cart.jsp").forward(request, response);
            } else {
                // Route any GET requests with modification actions (e.g., from link tags) to doPost
                doPost(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        CartDAO cartDAO = new CartDAO();
        HttpSession session = request.getSession();

        User loggedUser = (User) session.getAttribute("user");

        if (loggedUser == null) {
            // Protect POST actions from unauthenticated access
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
        } else {
            if ("add".equals(action)) {
                // Handle adding a new product to the cart
                int productId = Integer.parseInt(request.getParameter("productId"));

                // Handle quantity parameter, defaulting to 1 if it's missing (e.g., when called from an <a> tag)
                String quantityStr = request.getParameter("quantity");
                int quantity = (quantityStr != null && !quantityStr.isEmpty()) ? Integer.parseInt(quantityStr) : 1;

                int cartId = cartDAO.getCartIdByUserId(loggedUser.getId());
                if (cartId != -1) {
                    cartDAO.addItem(cartId, productId, quantity);
                }

                // Redirect back to the cart view to reflect the changes
                response.sendRedirect(request.getContextPath() + "/cart");

            } else if (action.equals("update")) {
                // Handle updating the quantity of a specific cart item
                int itemId = Integer.parseInt(request.getParameter("itemId"));
                int quantity = Integer.parseInt(request.getParameter("quantity"));
                cartDAO.updateQuantity(itemId, quantity);

                // Redirect back to the cart view to refresh the data
                response.sendRedirect(request.getContextPath() + "/cart");
            } else if (action.equals("delete")) {
                // Handle removing an item entirely from the cart
                int itemId = Integer.parseInt(request.getParameter("itemId"));
                cartDAO.deleteItem(itemId);

                // Redirect back to the cart view to refresh the data
                response.sendRedirect(request.getContextPath() + "/cart");
            }
        }
    }
}
