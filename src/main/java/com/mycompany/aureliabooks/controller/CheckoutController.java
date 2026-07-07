/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.CartDAO;
import com.mycompany.aureliabooks.dao.DiscountDAO;
import com.mycompany.aureliabooks.model.CartItem;
import com.mycompany.aureliabooks.model.Discount;
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
import java.math.RoundingMode;
import java.sql.Timestamp;

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

        String shippingAddress = request.getParameter("shippingAddress");
        String contactPhone = request.getParameter("contactPhone");
        String checkoutAction = request.getParameter("checkoutAction");

        CartDAO cartDAO = new CartDAO();
        List<CartItem> cartItems = cartDAO.findAll(loggedUser.getId());

        BigDecimal cartTotal = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            BigDecimal itemTotal = item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity()));
            cartTotal = cartTotal.add(itemTotal);
        }

        if ("applyVoucher".equals(checkoutAction)) {
            String discountCode = request.getParameter("discountCode");
            String voucherMessage = "";
            BigDecimal discountAmount = BigDecimal.ZERO;
            BigDecimal finalAmount = cartTotal;

            if (discountCode == null || discountCode.trim().isEmpty()) {
                voucherMessage = "Vui lòng nhập mã voucher.";
            } else {
                Discount discount = new DiscountDAO().getDiscountByCode(discountCode);

                if (discount == null || !discount.isIsActive()) {
                    voucherMessage = "Mã voucher không tồn tại hoặc đã bị vô hiệu hóa.";
                } else {
                    Timestamp now = new Timestamp(System.currentTimeMillis());

                    if (discount.getStartDate() != null && now.before(discount.getStartDate())) {
                        voucherMessage = "Mã voucher chưa đến thời gian sử dụng.";
                    } else if (discount.getEndDate() != null && now.after(discount.getEndDate())) {
                        voucherMessage = "Mã voucher đã hết hạn.";
                    } else if (discount.getMinOrderValue() != null
                            && cartTotal.compareTo(discount.getMinOrderValue()) < 0) {
                        voucherMessage = "Đơn hàng chưa đạt giá trị tối thiểu để dùng voucher.";
                    } else {
                        BigDecimal discountPercent = discount.getDiscountPercent() == null
                                ? BigDecimal.ZERO : discount.getDiscountPercent();
                        discountAmount = cartTotal.multiply(discountPercent)
                                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                        if (discount.getMaxDiscountAmount() != null
                                && discountAmount.compareTo(discount.getMaxDiscountAmount()) > 0) {
                            discountAmount = discount.getMaxDiscountAmount();
                        }
                        if (discountAmount.compareTo(cartTotal) > 0) {
                            discountAmount = cartTotal;
                        }

                        finalAmount = cartTotal.subtract(discountAmount);
                        voucherMessage = "Áp dụng voucher thành công.";
                        discountCode = discount.getCode();
                    }
                }
            }

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
            request.setAttribute("defaultPhone", defaultPhone);
            request.setAttribute("defaultAddress", defaultAddress);
            request.setAttribute("discountCode", discountCode);
            request.setAttribute("voucherMessage", voucherMessage);
            request.setAttribute("discountAmount", discountAmount);
            request.setAttribute("finalAmount", finalAmount);
            request.getRequestDispatcher("/WEB-INF/cart/cart.jsp").forward(request, response);
            return;
        }

        if (cartTotal.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal totalAmount = cartTotal;
            Integer discountId = null;
            String discountCode = request.getParameter("discountCode");

            if (discountCode != null && !discountCode.trim().isEmpty()) {
                String voucherMessage = "";
                BigDecimal discountAmount = BigDecimal.ZERO;
                BigDecimal finalAmount = cartTotal;
                Discount discount = new DiscountDAO().getDiscountByCode(discountCode);
                boolean voucherValid = true;

                if (discount == null || !discount.isIsActive()) {
                    voucherMessage = "Mã voucher không tồn tại hoặc đã bị vô hiệu hóa.";
                    voucherValid = false;
                } else {
                    Timestamp now = new Timestamp(System.currentTimeMillis());

                    if (discount.getStartDate() != null && now.before(discount.getStartDate())) {
                        voucherMessage = "Mã voucher chưa đến thời gian sử dụng.";
                        voucherValid = false;
                    } else if (discount.getEndDate() != null && now.after(discount.getEndDate())) {
                        voucherMessage = "Mã voucher đã hết hạn.";
                        voucherValid = false;
                    } else if (discount.getMinOrderValue() != null
                            && cartTotal.compareTo(discount.getMinOrderValue()) < 0) {
                        voucherMessage = "Đơn hàng chưa đạt giá trị tối thiểu để dùng voucher.";
                        voucherValid = false;
                    }
                }

                if (!voucherValid) {
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
                    request.setAttribute("defaultPhone", defaultPhone);
                    request.setAttribute("defaultAddress", defaultAddress);
                    request.setAttribute("discountCode", discountCode);
                    request.setAttribute("voucherMessage", voucherMessage);
                    request.setAttribute("discountAmount", discountAmount);
                    request.setAttribute("finalAmount", finalAmount);
                    request.getRequestDispatcher("/WEB-INF/cart/cart.jsp").forward(request, response);
                    return;
                }

                BigDecimal discountPercent = discount.getDiscountPercent() == null
                        ? BigDecimal.ZERO : discount.getDiscountPercent();
                discountAmount = cartTotal.multiply(discountPercent)
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                if (discount.getMaxDiscountAmount() != null
                        && discountAmount.compareTo(discount.getMaxDiscountAmount()) > 0) {
                    discountAmount = discount.getMaxDiscountAmount();
                }
                if (discountAmount.compareTo(cartTotal) > 0) {
                    discountAmount = cartTotal;
                }

                finalAmount = cartTotal.subtract(discountAmount);
                totalAmount = finalAmount;
                discountId = discount.getId();
            }

            // Attempt to create the order and clear the cart in a single transaction
            boolean success = cartDAO.createOrder(loggedUser.getId(), shippingAddress, contactPhone, totalAmount, discountId);
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
