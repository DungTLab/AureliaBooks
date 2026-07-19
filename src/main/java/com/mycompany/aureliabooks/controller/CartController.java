/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.CartDAO;
import com.mycompany.aureliabooks.dao.ProductDAO;
import com.mycompany.aureliabooks.model.CartItem;
import com.mycompany.aureliabooks.model.User;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controller responsible for handling shopping cart operations.
 * Processes requests to view the cart, add items, update quantities, and remove items.
 * Ensures the user is authenticated via session before allowing access to cart features.
 *
 * @author DungLT
 */
@WebServlet(name = "CartController", urlPatterns = {"/cart"})
public class CartController extends HttpServlet {

    /**
     * Handles GET requests. Primarily used for displaying the cart contents.
     * Maps to the /cart URL.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        HttpSession session = request.getSession();
        User loggedUser = (User) session.getAttribute("user");

        // Protect access: Redirect unauthenticated users to the login page
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
        } else {
            // Default to 'view' action if no specific action parameter is provided
            if (action == null || "view".equals(action)) {
                try {
                    CartDAO cartDAO = new CartDAO();
                    ProductDAO productDAO = new ProductDAO();
                    
                    // Fetch the user's cart items from the database
                    List<CartItem> cartItems = cartDAO.findAll(loggedUser.getId());
                    
                    // Calculate the total price of all items in the cart
                    BigDecimal cartTotal = BigDecimal.ZERO;
                    // Track stock limits for each product in the cart
                    Map<Integer, Integer> stockMap = new HashMap<>();
                    
                    for (CartItem item : cartItems) {
                        BigDecimal itemTotal = item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity()));
                        cartTotal = cartTotal.add(itemTotal);
                        stockMap.put(item.getProductId(), productDAO.getProductStock(item.getProductId()));
                    }

                    // Retrieve the user's phone number and address to pre-fill the checkout form if needed
                    String userInfoStr = cartDAO.getUserInfo(loggedUser.getId());
                    String defaultPhone = "";
                    String defaultAddress = "";
                    if (userInfoStr != null && userInfoStr.contains("/")) {
                        String[] parts = userInfoStr.split("/", 2);
                        defaultPhone = parts.length > 0 ? parts[0] : "";
                        defaultAddress = parts.length > 1 ? parts[1] : "";
                    }

                    // Set attributes for the JSP view
                    request.setAttribute("cartItems", cartItems);
                    request.setAttribute("cartTotal", cartTotal);
                    request.setAttribute("stockMap", stockMap);
                    request.setAttribute("defaultPhone", defaultPhone);
                    request.setAttribute("defaultAddress", defaultAddress);

                    // Forward the attributes to the JSP view for rendering
                    request.getRequestDispatcher("/WEB-INF/cart/cart.jsp").forward(request, response);
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("errorMessage", "An error occurred while loading the cart: " + e.getMessage());
                    request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
                }
            } else {
                // Route any GET requests with modification actions (e.g., from link tags) to doPost
                doPost(request, response);
            }
        }
    }

    /**
     * Handles POST requests. Used for modifications such as adding, updating, or deleting items.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        CartDAO cartDAO = new CartDAO();
        ProductDAO productDAO = new ProductDAO();
        HttpSession session = request.getSession();

        User loggedUser = (User) session.getAttribute("user");

        // Protect POST actions from unauthenticated access
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
        } else {
            try {
                if ("add".equals(action)) {
                    // Action: Add a new product to the cart
                    int productId = Integer.parseInt(request.getParameter("productId"));

                    // Handle quantity parameter, defaulting to 1 if it's missing (e.g., when called from an <a> tag)
                    String quantityStr = request.getParameter("quantity");
                    int quantity = (quantityStr != null && !quantityStr.isEmpty()) ? Integer.parseInt(quantityStr) : 1;

                    // Fetch current cart items to check if product already exists in cart
                    int existingQuantity = 0;
                    List<CartItem> cartItems = cartDAO.findAll(loggedUser.getId());
                    for (CartItem item : cartItems) {
                        if (item.getProductId() == productId) {
                            existingQuantity = item.getQuantity();
                            break;
                        }
                    }

                    // Validate stock availability before adding to cart
                    int stock = productDAO.getProductStock(productId);
                    if (existingQuantity + quantity > stock) {
                        session.setAttribute("cartWarningMessage", "Số lượng sản phẩm trong giỏ hàng đã đạt giới hạn tồn kho (" + stock + ")!");
                        quantity = stock - existingQuantity;
                    }

                    if (quantity > 0) {
                        // Retrieve Cart ID and add item
                        int cartId = cartDAO.getCartIdByUserId(loggedUser.getId());
                        if (cartId != -1) {
                            cartDAO.addItem(cartId, productId, quantity);
                        }
                        if (session.getAttribute("cartWarningMessage") == null) {
                            session.setAttribute("cartSuccessMessage", "Sản phẩm đã được thêm vào giỏ hàng thành công!");
                        }
                    } else {
                        session.setAttribute("cartErrorMessage", "Không thể thêm sản phẩm. Số lượng trong giỏ hàng của bạn đã bằng tồn kho hiện tại (" + stock + ").");
                    }

                    // Redirect back to the referring page if available, else redirect to cart page
                    String referer = request.getHeader("Referer");
                    if (referer != null && !referer.trim().isEmpty()) {
                        response.sendRedirect(referer);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/cart");
                    }

                } else if ("update".equals(action)) {
                    // Action: Update the quantity of a specific cart item
                    int itemId = Integer.parseInt(request.getParameter("itemId"));
                    int quantity = Integer.parseInt(request.getParameter("quantity"));

                    if (quantity <= 0) {
                        cartDAO.deleteItem(itemId, loggedUser.getId());
                    } else {
                        cartDAO.updateQuantity(itemId, quantity, loggedUser.getId());
                    }
                    // Redirect back to the cart view to refresh the data
                    response.sendRedirect(request.getContextPath() + "/cart");

                } else if ("delete".equals(action)) {
                    // Action: Remove an item entirely from the cart
                    int itemId = Integer.parseInt(request.getParameter("itemId"));
                    cartDAO.deleteItem(itemId, loggedUser.getId());

                    // Redirect back to the cart view to refresh the data
                    response.sendRedirect(request.getContextPath() + "/cart");
                }
            } catch (NumberFormatException e) {
                // Catch invalid number formats for IDs or quantities
                request.setAttribute("errorMessage", "Invalid ID or quantity format.");
                request.getRequestDispatcher("/WEB-INF/error/400.jsp").forward(request, response);
            } catch (Exception e) {
                // General exception handler for unforeseen errors
                e.printStackTrace();
                request.setAttribute("errorMessage", "An error occurred while processing the cart: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
            }
        }
    }
}