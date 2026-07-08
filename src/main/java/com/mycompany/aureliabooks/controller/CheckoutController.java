/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.CartDAO;
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
import java.util.ArrayList;
import java.util.Iterator;
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
 */
@WebServlet(name = "CheckoutController", urlPatterns = {"/checkout"})
public class CheckoutController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loggedUser = (User) session.getAttribute("user");

        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        String action = request.getParameter("action");
        if ("prepareCart".equals(action)) {
            CartDAO cartDAO = new CartDAO();
            List<CartItem> cartItems = cartDAO.findAll(loggedUser.getId());
            session.setAttribute("checkoutItems", cartItems);
            response.sendRedirect(request.getContextPath() + "/checkout");
            return;
        }

        List<CartItem> checkoutItems = (List<CartItem>) session.getAttribute("checkoutItems");
        if (checkoutItems == null || checkoutItems.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        ProductDAO productDAO = new ProductDAO();
        BigDecimal subTotal = BigDecimal.ZERO;
        java.util.Map<Integer, Integer> stockMap = new java.util.HashMap<>();

        for (CartItem item : checkoutItems) {
            BigDecimal itemTotal = item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity()));
            subTotal = subTotal.add(itemTotal);
            stockMap.put(item.getProductId(), productDAO.getProductStock(item.getProductId()));
        }

        // Voucher logic
        Discount appliedDiscount = (Discount) session.getAttribute("appliedDiscount");
        BigDecimal discountAmount = BigDecimal.ZERO;
        
        if (appliedDiscount != null) {
            if (subTotal.compareTo(appliedDiscount.getMinOrderValue()) >= 0) {
                BigDecimal calculatedDiscount = subTotal.multiply(appliedDiscount.getDiscountPercent())
                        .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
                if (calculatedDiscount.compareTo(appliedDiscount.getMaxDiscountAmount()) > 0) {
                    discountAmount = appliedDiscount.getMaxDiscountAmount();
                } else {
                    discountAmount = calculatedDiscount;
                }
            } else {
                // Order value dropped below minimum, remove voucher
                session.removeAttribute("appliedDiscount");
                appliedDiscount = null;
                request.setAttribute("voucherError", "Đơn hàng không đủ điều kiện tối thiểu để áp dụng mã giảm giá này.");
            }
        }

        BigDecimal subTotalAfterDiscount = subTotal.subtract(discountAmount);
        if (subTotalAfterDiscount.compareTo(BigDecimal.ZERO) < 0) {
            subTotalAfterDiscount = BigDecimal.ZERO;
        }

        BigDecimal shippingCost = new BigDecimal("30000");
        BigDecimal tax = subTotalAfterDiscount.multiply(new BigDecimal("0.08"));
        BigDecimal totalAmount = subTotalAfterDiscount.add(shippingCost).add(tax);

        UserDAO userDAO = new UserDAO();
        UserProfile profile = userDAO.getUserProfile(loggedUser.getId());

        // Read and clear voucher error from session (Flash Attribute)
        String sessionVoucherError = (String) session.getAttribute("voucherError");
        if (sessionVoucherError != null) {
            request.setAttribute("voucherError", sessionVoucherError);
            session.removeAttribute("voucherError");
        }

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
                session.removeAttribute("appliedDiscount");
                response.sendRedirect(request.getContextPath() + "/checkout");
                return;
            }

            // Process Submit Order
            String shippingAddress = request.getParameter("shippingAddress");
            String contactPhone = request.getParameter("contactPhone");

            List<CartItem> checkoutItems = (List<CartItem>) session.getAttribute("checkoutItems");
            if (checkoutItems == null || checkoutItems.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

            // Validate inventory availability before inserting the order
            ProductDAO productDAO = new ProductDAO();
            for (CartItem item : checkoutItems) {
                int stock = productDAO.getProductStock(item.getProductId());
                if (item.getQuantity() > stock) {
                    session.setAttribute("voucherError", "Không thể đặt hàng. Sản phẩm '" + item.getProduct().getTitle() + "' không đủ hàng trong kho (Hiện có: " + stock + ")");
                    response.sendRedirect(request.getContextPath() + "/checkout");
                    return;
                }
            }

            BigDecimal subTotal = BigDecimal.ZERO;
            for (CartItem item : checkoutItems) {
                BigDecimal itemTotal = item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity()));
                subTotal = subTotal.add(itemTotal);
            }

            // Voucher logic during submit
            Discount appliedDiscount = (Discount) session.getAttribute("appliedDiscount");
            BigDecimal discountAmount = BigDecimal.ZERO;
            if (appliedDiscount != null && subTotal.compareTo(appliedDiscount.getMinOrderValue()) >= 0) {
                BigDecimal calculatedDiscount = subTotal.multiply(appliedDiscount.getDiscountPercent())
                        .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
                if (calculatedDiscount.compareTo(appliedDiscount.getMaxDiscountAmount()) > 0) {
                    discountAmount = appliedDiscount.getMaxDiscountAmount();
                } else {
                    discountAmount = calculatedDiscount;
                }
            } else if (appliedDiscount != null) {
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

                // Thực hiện lưu đơn hàng và xóa các item tương ứng trong DB Cart
                boolean success = orderDAO.insertOrder(order, checkoutItems);
                if (success) {
                    session.removeAttribute("checkoutItems");
                    session.removeAttribute("appliedDiscount");
                    response.sendRedirect(request.getContextPath() + "/orders?action=view");
                    return;
                }
            }

            // Nếu không thành công hoặc totalAmount <= 0
            response.sendRedirect(request.getContextPath() + "/cart?checkout=failed");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình thanh toán: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
        }
    }

    private void handleApplyVoucher(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        String voucherCode = request.getParameter("voucherCode");
        
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
            com.mycompany.aureliabooks.dao.DiscountDAO discountDAO = new com.mycompany.aureliabooks.dao.DiscountDAO();
            Discount discount = discountDAO.getDiscountByCode(voucherCode.trim());
            
            if (discount == null) {
                session.setAttribute("voucherError", "Mã giảm giá không tồn tại, hết hạn hoặc không hoạt động.");
            } else if (subTotal.compareTo(discount.getMinOrderValue()) < 0) {
                session.setAttribute("voucherError", "Đơn hàng chưa đạt giá trị tối thiểu (" + discount.getMinOrderValue() + " VNĐ) để sử dụng mã này.");
            } else {
                session.setAttribute("appliedDiscount", discount);
                session.removeAttribute("voucherError");
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/checkout");
    }

    private void handleBuyNow(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        int productId = Integer.parseInt(request.getParameter("productId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));

        ProductDAO productDAO = new ProductDAO();
        Product product = productDAO.getProductById(productId);

        if (product != null) {
            CartItem newItem = new CartItem();
            newItem.setId(0); // 0 means it's not in the DB CartItems table yet
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            newItem.setProduct(product);

            List<CartItem> checkoutList = new ArrayList<>();
            checkoutList.add(newItem);
            session.setAttribute("checkoutItems", checkoutList);
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        int productId = Integer.parseInt(request.getParameter("productId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));

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

    private void handleDelete(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        int productId = Integer.parseInt(request.getParameter("productId"));

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