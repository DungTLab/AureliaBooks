/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Filter.java to edit this template
 */
package com.mycompany.aureliabooks.filter;

import com.mycompany.aureliabooks.model.User;
import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Authorization and Authentication Security Filter.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
@WebFilter("/*")
public class SecurityFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String requestURI = httpRequest.getRequestURI();
        
        // 1. Cho phép các static assets đi qua không cần check
        if (requestURI.contains("/assets/") || requestURI.endsWith("login.jsp") 
            || requestURI.endsWith("register.jsp") || requestURI.contains("/auth")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Lấy thông tin User
        User loggedInUser = (session != null) ? (User) session.getAttribute("user") : null;

        // 3. Phân quyền Admin & Employee (Nhân viên)
        if (requestURI.contains("/admin/")) {
            if (loggedInUser == null) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/auth?action=login");
                return;
            }
            
            String role = loggedInUser.getRoleName();
            
            // Các phân hệ chỉ dành riêng cho ADMIN: Báo cáo thống kê, Quản lý tài khoản, Quản lý Voucher
            if (requestURI.contains("/admin/reports") 
                || requestURI.contains("/admin/users") 
                || requestURI.contains("/admin/discounts")) {
                if (!"ADMIN".equals(role)) {
                    httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied - Admin Only");
                    return;
                }
            } 
            // Các phân hệ dùng chung cho ADMIN và EMPLOYEE: Đơn hàng, Thể loại, Sản phẩm, Hỗ trợ (Tickets)
            else {
                if (!"ADMIN".equals(role) && !"EMPLOYEE".equals(role)) {
                    httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
