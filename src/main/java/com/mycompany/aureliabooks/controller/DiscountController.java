/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.DiscountDAO;
import com.mycompany.aureliabooks.model.Discount;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Admin Discount/Voucher CRUD Controller.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
@WebServlet(name = "DiscountController", urlPatterns = {"/admin/discounts"})
public class DiscountController extends HttpServlet {

    private String validateDiscount(Discount d) {
        if (d.getDiscountPercent().compareTo(BigDecimal.ZERO) <= 0 || d.getDiscountPercent().compareTo(new BigDecimal("20")) > 0) {
            return "Phần trăm giảm phải từ 1 đến 20.";
        }
        if (d.getMinOrderValue().compareTo(new BigDecimal("20000")) < 0) {
            return "Đơn tối thiểu phải từ 20,000 VNĐ trở lên.";
        }
        if (d.getMaxDiscountAmount().compareTo(d.getMinOrderValue()) < 0 || d.getMaxDiscountAmount().compareTo(d.getMinOrderValue().multiply(new BigDecimal("2"))) > 0) {
            return "Giảm tối đa phải nằm trong khoảng từ [Đơn tối thiểu] đến [2 x Đơn tối thiểu].";
        }
        
        long now = System.currentTimeMillis();
        long minFuture = now + 7L * 24 * 60 * 60 * 1000;
        
        if (d.getStartDate().getTime() <= minFuture) {
            return "Thời gian bắt đầu phải cách thời điểm hiện tại ít nhất 7 ngày.";
        }
        
        if (d.getEndDate().getTime() <= d.getStartDate().getTime()) {
            return "Thời gian kết thúc phải lớn hơn thời gian bắt đầu.";
        }
        
        DiscountDAO dao = new DiscountDAO();
        if (dao.isCodeExists(d.getCode(), d.getId() > 0 ? d.getId() : null)) {
            return "Mã voucher này đã tồn tại, vui lòng chọn mã khác.";
        }
        
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("create".equals(action)) {
            request.getRequestDispatcher("/WEB-INF/discount/create.jsp").forward(request, response);
        } else if ("update".equals(action)) {
            String idParam = request.getParameter("id");
            if (idParam == null) {
                response.sendRedirect(request.getContextPath() + "/admin/discounts");
                return;
            }
            int id = Integer.parseInt(idParam);
            DiscountDAO dao = new DiscountDAO();
            Discount d = dao.getDiscountById(id);
            if (d == null) {
                response.sendRedirect(request.getContextPath() + "/admin/discounts");
                return;
            }
            
            long now = System.currentTimeMillis();
            if (now >= d.getStartDate().getTime()) {
                request.setAttribute("errorMessage", "Không thể sửa voucher đã đến hoặc qua thời gian bắt đầu.");
                request.setAttribute("discounts", dao.findAll());
                request.getRequestDispatcher("/WEB-INF/discount/list.jsp").forward(request, response);
                return;
            }
            
            request.setAttribute("discount", d);
            request.getRequestDispatcher("/WEB-INF/discount/update.jsp").forward(request, response);
        } else {
            DiscountDAO dao = new DiscountDAO();
            request.setAttribute("discounts", dao.findAll());
            request.getRequestDispatcher("/WEB-INF/discount/list.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        
        if ("create".equals(action)) {
            Discount d = new Discount();
            d.setCode(request.getParameter("code"));
            d.setIsActive(true);
            
            try {
                d.setDiscountPercent(new BigDecimal(request.getParameter("discountPercent")));
                d.setMaxDiscountAmount(new BigDecimal(request.getParameter("maxDiscountAmount")));
                d.setMinOrderValue(new BigDecimal(request.getParameter("minOrderValue")));
                
                String startDateStr = request.getParameter("startDate");
                String endDateStr = request.getParameter("endDate");
                
                if (startDateStr != null && !startDateStr.isEmpty()) {
                    d.setStartDate(Timestamp.valueOf(LocalDateTime.parse(startDateStr)));
                }
                if (endDateStr != null && !endDateStr.isEmpty()) {
                    d.setEndDate(Timestamp.valueOf(LocalDateTime.parse(endDateStr)));
                }
                
                String error = validateDiscount(d);
                if (error != null) {
                    request.setAttribute("errorMessage", error);
                    request.setAttribute("discount", d);
                    request.getRequestDispatcher("/WEB-INF/discount/create.jsp").forward(request, response);
                    return;
                }
                
                DiscountDAO dao = new DiscountDAO();
                dao.insertDiscount(d);
                response.sendRedirect(request.getContextPath() + "/admin/discounts");
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "Đã xảy ra lỗi khi tạo voucher. Vui lòng kiểm tra lại định dạng dữ liệu.");
                request.setAttribute("discount", d);
                request.getRequestDispatcher("/WEB-INF/discount/create.jsp").forward(request, response);
            }
        } else if ("update".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            DiscountDAO dao = new DiscountDAO();
            Discount originalDiscount = dao.getDiscountById(id);
            if (originalDiscount == null) {
                response.sendRedirect(request.getContextPath() + "/admin/discounts");
                return;
            }
            
            long currentNow = System.currentTimeMillis();
            if (currentNow >= originalDiscount.getStartDate().getTime()) {
                request.setAttribute("errorMessage", "Không thể sửa voucher đã đến hoặc qua thời gian bắt đầu.");
                request.setAttribute("discounts", dao.findAll());
                request.getRequestDispatcher("/WEB-INF/discount/list.jsp").forward(request, response);
                return;
            }
            
            Discount d = new Discount();
            d.setId(id);
            d.setCode(request.getParameter("code"));
            d.setIsActive(true); 
            
            try {
                d.setDiscountPercent(new BigDecimal(request.getParameter("discountPercent")));
                d.setMaxDiscountAmount(new BigDecimal(request.getParameter("maxDiscountAmount")));
                d.setMinOrderValue(new BigDecimal(request.getParameter("minOrderValue")));
                
                String startDateStr = request.getParameter("startDate");
                String endDateStr = request.getParameter("endDate");
                
                if (startDateStr != null && !startDateStr.isEmpty()) {
                    d.setStartDate(Timestamp.valueOf(LocalDateTime.parse(startDateStr)));
                }
                if (endDateStr != null && !endDateStr.isEmpty()) {
                    d.setEndDate(Timestamp.valueOf(LocalDateTime.parse(endDateStr)));
                }
                
                String error = validateDiscount(d);
                if (error != null) {
                    request.setAttribute("errorMessage", error);
                    request.setAttribute("discount", d);
                    request.getRequestDispatcher("/WEB-INF/discount/update.jsp").forward(request, response);
                    return;
                }
                
                dao.updateDiscount(d);
                response.sendRedirect(request.getContextPath() + "/admin/discounts");
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "Đã xảy ra lỗi khi cập nhật voucher. Vui lòng kiểm tra lại định dạng dữ liệu.");
                request.setAttribute("discount", d);
                request.getRequestDispatcher("/WEB-INF/discount/update.jsp").forward(request, response);
            }
        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            DiscountDAO dao = new DiscountDAO();
            Discount d = dao.getDiscountById(id);
            if (d == null) {
                response.sendRedirect(request.getContextPath() + "/admin/discounts");
                return;
            }
            
            long now = System.currentTimeMillis();
            if (now >= d.getStartDate().getTime() && now <= d.getEndDate().getTime()) {
                request.setAttribute("errorMessage", "Không thể xóa voucher đang trong thời gian khả dụng.");
                request.setAttribute("discounts", dao.findAll());
                request.getRequestDispatcher("/WEB-INF/discount/list.jsp").forward(request, response);
                return;
            }
            
            boolean success = dao.deleteDiscount(id);
            if (!success) {
                request.setAttribute("errorMessage", "Không thể xóa voucher này (có thể đã được sử dụng trong đơn hàng).");
                request.setAttribute("discounts", dao.findAll());
                request.getRequestDispatcher("/WEB-INF/discount/list.jsp").forward(request, response);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/admin/discounts");
        }
    }
}
