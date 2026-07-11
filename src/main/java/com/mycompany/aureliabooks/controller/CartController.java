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
            if (action == null || "view".equals(action)) {
                try {
                    CartDAO cartDAO = new CartDAO();
                    ProductDAO productDAO = new ProductDAO();
                    
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

                    // Retrieve the user's phone number and address to pre-fill the checkout form
                    String userInfoStr = cartDAO.getUserInfo(loggedUser.getId());
                    String defaultPhone = "";
                    String defaultAddress = "";
                    if (userInfoStr != null && userInfoStr.contains("/")) {
                        String[] parts = userInfoStr.split("/", 2);
                        defaultPhone = parts.length > 0 ? parts[0] : "";
                        defaultAddress = parts.length > 1 ? parts[1] : "";
                    }

                    request.setAttribute("cartItems", cartItems);
                    request.setAttribute("cartTotal", cartTotal);
                    request.setAttribute("stockMap", stockMap);
                    request.setAttribute("defaultPhone", defaultPhone);
                    request.setAttribute("defaultAddress", defaultAddress);

                    // Forward the attributes to the JSP view for rendering
                    request.getRequestDispatcher("/WEB-INF/cart/cart.jsp").forward(request, response);
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("errorMessage", "Đã xảy ra lỗi khi tải giỏ hàng: " + e.getMessage());
                    request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
                }
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
        ProductDAO productDAO = new ProductDAO();
        HttpSession session = request.getSession();

        User loggedUser = (User) session.getAttribute("user");

        if (loggedUser == null) {
            // Protect POST actions from unauthenticated access
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
        } else {
            try {
                if ("add".equals(action)) {
                    // Handle adding a new product to the cart
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

                    // Validate stock availability
                    int stock = productDAO.getProductStock(productId);
                    if (existingQuantity + quantity > stock) {
                        request.setAttribute("errorMessage", "Không thể thêm sản phẩm. Tổng số lượng trong giỏ hàng (" + (existingQuantity + quantity) + ") vượt quá tồn kho hiện có (" + stock + ")");
                        request.getRequestDispatcher("/WEB-INF/error/400.jsp").forward(request, response);
                        return;
                    }

                    int cartId = cartDAO.getCartIdByUserId(loggedUser.getId());
                    if (cartId != -1) {
                        cartDAO.addItem(cartId, productId, quantity);
                    }

                    // Redirect back to the referring page if available, else redirect to cart page
                    String referer = request.getHeader("Referer");
                    if (referer != null && !referer.trim().isEmpty()) {
                        // Only set session message if NOT coming from the detail page (which uses AJAX and shows a Toast)
                        if (!referer.contains("action=detail")) {
                            session.setAttribute("cartSuccessMessage", "Đã thêm sản phẩm vào giỏ hàng thành công!");
                        }
                        response.sendRedirect(referer);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/cart");
                    }

                } else if ("update".equals(action)) {
                    // Handle updating the quantity of a specific cart item
                    int itemId = Integer.parseInt(request.getParameter("itemId"));
                    int quantity = Integer.parseInt(request.getParameter("quantity"));

                    // Find Product ID from item ID to validate stock
                    int productId = -1;
                    List<CartItem> cartItems = cartDAO.findAll(loggedUser.getId());
                    for (CartItem item : cartItems) {
                        if (item.getId() == itemId) {
                            productId = item.getProductId();
                            break;
                        }
                    }

                    // Validate stock availability
                    if (productId != -1) {
                        int stock = productDAO.getProductStock(productId);
                        if (quantity > stock) {
                            request.setAttribute("errorMessage", "Không thể cập nhật số lượng. Số lượng cập nhật (" + quantity + ") vượt quá tồn kho hiện có (" + stock + ")");
                            request.getRequestDispatcher("/WEB-INF/error/400.jsp").forward(request, response);
                            return;
                        }
                    }

                    cartDAO.updateQuantity(itemId, quantity, loggedUser.getId());

                    // Redirect back to the cart view to refresh the data
                    response.sendRedirect(request.getContextPath() + "/cart");
                } else if ("delete".equals(action)) {
                    // Handle removing an item entirely from the cart
                    int itemId = Integer.parseInt(request.getParameter("itemId"));
                    cartDAO.deleteItem(itemId, loggedUser.getId());

                    // Redirect back to the cart view to refresh the data
                    response.sendRedirect(request.getContextPath() + "/cart");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Định dạng ID hoặc số lượng không hợp lệ.");
                request.getRequestDispatcher("/WEB-INF/error/400.jsp").forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình xử lý giỏ hàng: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
            }
        }
    }
}