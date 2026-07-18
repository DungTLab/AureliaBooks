/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.CartDAO;
import com.mycompany.aureliabooks.dao.DiscountDAO;
import com.mycompany.aureliabooks.dao.OrderDAO;
import com.mycompany.aureliabooks.dao.ProductDAO;
import com.mycompany.aureliabooks.dao.UserDAO;
import com.mycompany.aureliabooks.model.CartItem;
import com.mycompany.aureliabooks.model.Discount;
import com.mycompany.aureliabooks.model.Order;
import com.mycompany.aureliabooks.model.Product;
import com.mycompany.aureliabooks.model.User;
import com.mycompany.aureliabooks.model.UserProfile;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controller responsible for handling the order checkout process.
 * Manages cart to order transitions, voucher applications, and final order placement.
 */
@WebServlet(name = "CheckoutController", urlPatterns = {"/checkout"})
public class CheckoutController extends HttpServlet {

    /**
     * Handles GET requests. Used to display the checkout summary page, calculate totals,
     * apply active vouchers, and resolve shipping addresses.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loggedUser = (User) session.getAttribute("user");

        // Protect access: Redirect unauthenticated users
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        String action = request.getParameter("action");
        if ("prepareCart".equals(action)) {
            // Fetch all items from the database cart and load them into the checkout session
            CartDAO cartDAO = new CartDAO();
            List<CartItem> cartItems = cartDAO.findAll(loggedUser.getId());
            session.setAttribute("checkoutItems", cartItems);
            response.sendRedirect(request.getContextPath() + "/checkout");
            return;
        }

        // Retrieve items pending checkout from session
        @SuppressWarnings("unchecked")
        List<CartItem> checkoutItems = (List<CartItem>) session.getAttribute("checkoutItems");
        if (checkoutItems == null || checkoutItems.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        ProductDAO productDAO = new ProductDAO();
        BigDecimal subTotal = BigDecimal.ZERO;
        Map<Integer, Integer> stockMap = new HashMap<>();

        // Calculate subtotal and build a map of available stock for validation
        for (CartItem item : checkoutItems) {
            BigDecimal itemTotal = item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity()));
            subTotal = subTotal.add(itemTotal);
            stockMap.put(item.getProductId(), productDAO.getProductStock(item.getProductId()));
        }

        // Apply voucher logic if a discount code has been attached to the session
        Discount appliedDiscount = (Discount) session.getAttribute("appliedDiscount");
        BigDecimal discountAmount = BigDecimal.ZERO;
        
        if (appliedDiscount != null) {
            // Check if the current subtotal still meets the voucher's minimum requirement
            if (subTotal.compareTo(appliedDiscount.getMinOrderValue()) >= 0) {
                BigDecimal calculatedDiscount = subTotal.multiply(appliedDiscount.getDiscountPercent())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                if (calculatedDiscount.compareTo(appliedDiscount.getMaxDiscountAmount()) > 0) {
                    discountAmount = appliedDiscount.getMaxDiscountAmount();
                } else {
                    discountAmount = calculatedDiscount;
                }
            } else {
                // Subtotal dropped below minimum threshold, remove the voucher automatically
                session.removeAttribute("appliedDiscount");
                appliedDiscount = null;
                request.setAttribute("voucherError", "Order value does not meet the minimum requirement for this voucher.");
            }
        }

        BigDecimal subTotalAfterDiscount = subTotal.subtract(discountAmount);
        if (subTotalAfterDiscount.compareTo(BigDecimal.ZERO) < 0) {
            subTotalAfterDiscount = BigDecimal.ZERO;
        }

        // Calculate final taxes and shipping costs
        BigDecimal shippingCost = new BigDecimal("30000"); // Fixed shipping cost
        BigDecimal tax = subTotalAfterDiscount.multiply(new BigDecimal("0.08")); // 8% Tax Rate
        BigDecimal totalAmount = subTotalAfterDiscount.add(shippingCost).add(tax);

        // Fetch User Profile to pre-fill the checkout form
        UserDAO userDAO = new UserDAO();
        UserProfile profile = userDAO.getUserProfile(loggedUser.getId());
        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(loggedUser.getId());
        }

        // Address resolution hierarchy: 1. Session Temp, 2. Cookie, 3. DB Profile
        Cookie[] cookies = request.getCookies();
        String savedAddress = null;
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("savedAddress".equals(c.getName())) {
                    savedAddress = URLDecoder.decode(c.getValue(), "UTF-8");
                    break;
                }
            }
        }
        if (savedAddress != null && !savedAddress.isEmpty()) {
            profile.setAddress(savedAddress);
        }

        // Restore temporarily saved inputs from session if present (e.g. after removing a voucher)
        String tempAddress = (String) session.getAttribute("tempAddress");
        String tempPhone = (String) session.getAttribute("tempPhone");
        if (tempAddress != null) {
            profile.setAddress(tempAddress);
        }
        if (tempPhone != null) {
            profile.setPhone(tempPhone);
        }

        // Read and clear voucher error from session (Flash Attribute pattern)
        String sessionVoucherError = (String) session.getAttribute("voucherError");
        if (sessionVoucherError != null) {
            request.setAttribute("voucherError", sessionVoucherError);
            session.removeAttribute("voucherError");
        }

        // Pass calculated data to JSP
        request.setAttribute("checkoutItems", checkoutItems);
        request.setAttribute("subTotal", subTotal);
        request.setAttribute("discountAmount", discountAmount);
        request.setAttribute("shippingCost", shippingCost);
        request.setAttribute("tax", tax);
        request.setAttribute("totalAmount", totalAmount);
        request.setAttribute("profile", profile);
        request.setAttribute("stockMap", stockMap);
        if (appliedDiscount != null) {
            request.setAttribute("appliedDiscount", appliedDiscount);
        }

        request.getRequestDispatcher("/WEB-INF/cart/checkout.jsp").forward(request, response);
    }

    /**
     * Handles POST requests. Processes order submission, voucher application, and cart updates during checkout.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        User loggedUser = (User) session.getAttribute("user");

        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        try {
            String action = request.getParameter("action");
            // Delegate specific actions to helper methods
            if ("buyNow".equals(action)) {
                handleBuyNow(request, response, session);
                return;
            } else if ("update".equals(action)) {
                handleUpdate(request, response, session);
                return;
            } else if ("delete".equals(action)) {
                handleDelete(request, response, session);
                return;
            } else if ("applyVoucher".equals(action)) {
                handleApplyVoucher(request, response, session);
                return;
            } else if ("removeVoucher".equals(action)) {
                // Save form inputs temporarily before reloading
                String shippingAddress = request.getParameter("shippingAddress");
                String contactPhone = request.getParameter("contactPhone");
                session.setAttribute("tempAddress", shippingAddress);
                session.setAttribute("tempPhone", contactPhone);

                session.removeAttribute("appliedDiscount");
                response.sendRedirect(request.getContextPath() + "/checkout");
                return;
            }

            // Default Action: Process Order Submission
            String shippingAddress = request.getParameter("shippingAddress");
            String contactPhone = request.getParameter("contactPhone");

            @SuppressWarnings("unchecked")
            List<CartItem> checkoutItems = (List<CartItem>) session.getAttribute("checkoutItems");
            if (checkoutItems == null || checkoutItems.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

            // Final Inventory Check: Ensure all items have sufficient stock before processing
            ProductDAO productDAO = new ProductDAO();
            for (CartItem item : checkoutItems) {
                int stock = productDAO.getProductStock(item.getProductId());
                if (item.getQuantity() > stock) {
                    session.setAttribute("voucherError", "Cannot place order. Product '" + item.getProduct().getTitle() + "' has insufficient stock (Available: " + stock + ")");
                    response.sendRedirect(request.getContextPath() + "/checkout");
                    return;
                }
            }

            BigDecimal subTotal = BigDecimal.ZERO;
            for (CartItem item : checkoutItems) {
                BigDecimal itemTotal = item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity()));
                subTotal = subTotal.add(itemTotal);
            }

            // Re-validate and calculate voucher application
            Discount appliedDiscount = (Discount) session.getAttribute("appliedDiscount");
            BigDecimal discountAmount = BigDecimal.ZERO;
            if (appliedDiscount != null && subTotal.compareTo(appliedDiscount.getMinOrderValue()) >= 0) {
                BigDecimal calculatedDiscount = subTotal.multiply(appliedDiscount.getDiscountPercent())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                if (calculatedDiscount.compareTo(appliedDiscount.getMaxDiscountAmount()) > 0) {
                    discountAmount = appliedDiscount.getMaxDiscountAmount();
                } else {
                    discountAmount = calculatedDiscount;
                }
            } else if (appliedDiscount != null) {
                // If voucher became invalid during submission, remove it silently (or redirect to notify user)
                session.removeAttribute("appliedDiscount");
                appliedDiscount = null;
            }

            BigDecimal subTotalAfterDiscount = subTotal.subtract(discountAmount);
            if (subTotalAfterDiscount.compareTo(BigDecimal.ZERO) < 0) {
                subTotalAfterDiscount = BigDecimal.ZERO;
            }

            BigDecimal shippingCost = new BigDecimal("30000");
            BigDecimal tax = subTotalAfterDiscount.multiply(new BigDecimal("0.08"));
            BigDecimal totalAmount = subTotalAfterDiscount.add(shippingCost).add(tax);

            if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                OrderDAO orderDAO = new OrderDAO();
                Order order = new Order();
                order.setUserId(loggedUser.getId());
                order.setShippingAddress(shippingAddress);
                order.setContactPhone(contactPhone);
                order.setTotalAmount(totalAmount);
                if (appliedDiscount != null) {
                    order.setDiscountId(appliedDiscount.getId());
                }

                // Execute a database transaction to insert the order and clear cart items
                boolean success = orderDAO.insertOrder(order, checkoutItems);
                if (success) {
                    // Remember the successful shipping address via a cookie for future use
                    Cookie addressCookie = new Cookie("savedAddress", URLEncoder.encode(shippingAddress, "UTF-8"));
                    addressCookie.setMaxAge(30 * 24 * 60 * 60); // Valid for 30 days
                    addressCookie.setPath(request.getContextPath());
                    response.addCookie(addressCookie);

                    // Clear all session states related to checkout
                    session.removeAttribute("checkoutItems");
                    session.removeAttribute("appliedDiscount");
                    session.removeAttribute("tempAddress");
                    session.removeAttribute("tempPhone");
                    response.sendRedirect(request.getContextPath() + "/orders");
                    return;
                }
            }

            // Fallback for failure or invalid totals
            response.sendRedirect(request.getContextPath() + "/cart?checkout=failed");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred during checkout: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
        }
    }

    /**
     * Validates and applies a discount voucher to the session.
     */
    private void handleApplyVoucher(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        String shippingAddress = request.getParameter("shippingAddress");
        String contactPhone = request.getParameter("contactPhone");
        // Save form inputs so user doesn't lose them when page reloads
        session.setAttribute("tempAddress", shippingAddress);
        session.setAttribute("tempPhone", contactPhone);

        String voucherCode = request.getParameter("voucherCode");
        
        @SuppressWarnings("unchecked")
        List<CartItem> checkoutItems = (List<CartItem>) session.getAttribute("checkoutItems");
        if (checkoutItems == null || checkoutItems.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/checkout");
            return;
        }
        
        BigDecimal subTotal = BigDecimal.ZERO;
        for (CartItem item : checkoutItems) {
            subTotal = subTotal.add(item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())));
        }

        if (voucherCode != null && !voucherCode.trim().isEmpty()) {
            DiscountDAO discountDAO = new DiscountDAO();
            Discount discount = discountDAO.getDiscountByCode(voucherCode.trim());
            
            // Validate the voucher
            if (discount == null) {
                session.setAttribute("voucherError", "Discount code does not exist, has expired, or is inactive.");
            } else if (subTotal.compareTo(discount.getMinOrderValue()) < 0) {
                session.setAttribute("voucherError", "Order value has not reached the minimum requirement (" + discount.getMinOrderValue() + " VNĐ) for this code.");
            } else {
                session.setAttribute("appliedDiscount", discount);
                session.removeAttribute("voucherError");
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/checkout");
    }

    /**
     * Bypasses the cart by creating an immediate checkout session with a single product.
     */
    private void handleBuyNow(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        int productId = Integer.parseInt(request.getParameter("productId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));

        ProductDAO productDAO = new ProductDAO();
        Product product = productDAO.getProductById(productId);

        if (product != null) {
            int stock = productDAO.getProductStock(productId);
            if (quantity > stock) {
                session.setAttribute("voucherError", "Cannot process 'Buy Now'. Requested quantity (" + quantity + ") exceeds available stock (" + stock + ").");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Out of stock");
                return;
            }

            CartItem newItem = new CartItem();
            newItem.setId(0); // 0 signifies it's an ephemeral item, not in the CartItems DB table
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            newItem.setProduct(product);

            List<CartItem> checkoutList = new ArrayList<>();
            checkoutList.add(newItem);
            session.setAttribute("checkoutItems", checkoutList);
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Updates the quantity of a specific item within the checkout session.
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        int productId = Integer.parseInt(request.getParameter("productId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));

        @SuppressWarnings("unchecked")
        List<CartItem> checkoutItems = (List<CartItem>) session.getAttribute("checkoutItems");
        if (checkoutItems != null) {
            for (CartItem item : checkoutItems) {
                if (item.getProductId() == productId) {
                    item.setQuantity(quantity);
                    break;
                }
            }
        }
        response.sendRedirect(request.getContextPath() + "/checkout");
    }

    /**
     * Removes a specific item from the checkout session.
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        int productId = Integer.parseInt(request.getParameter("productId"));

        @SuppressWarnings("unchecked")
        List<CartItem> checkoutItems = (List<CartItem>) session.getAttribute("checkoutItems");
        if (checkoutItems != null) {
            Iterator<CartItem> iterator = checkoutItems.iterator();
            while (iterator.hasNext()) {
                CartItem item = iterator.next();
                if (item.getProductId() == productId) {
                    iterator.remove();
                    break;
                }
            }
        }
        response.sendRedirect(request.getContextPath() + "/checkout");
    }
}