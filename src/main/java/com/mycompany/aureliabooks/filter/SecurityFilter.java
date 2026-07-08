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
        
        // 1. Allow static assets and authentication paths without checks
        if (requestURI.contains("/assets/") || requestURI.endsWith("login.jsp") 
            || requestURI.endsWith("register.jsp") || requestURI.contains("/auth")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Get logged in User info
        User loggedInUser = (session != null) ? (User) session.getAttribute("user") : null;

        // 3. Block Admin and Employee accounts from purchasing (cart, checkout, personal orders)
        if (requestURI.contains("/cart") || requestURI.contains("/checkout") || requestURI.equals(httpRequest.getContextPath() + "/orders")) {
            if (loggedInUser != null && ("ADMIN".equals(loggedInUser.getRoleName()) || "EMPLOYEE".equals(loggedInUser.getRoleName()))) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied - Admin/Staff cannot place orders.");
                return;
            }
        }

        // 4. Role-based authorization for Admin and Employee sections
        if (requestURI.contains("/admin/")) {
            if (loggedInUser == null) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/auth?action=login");
                return;
            }
            
            String role = loggedInUser.getRoleName();
            
            // Admin-only sections: reports, user management, discounts
            if (requestURI.contains("/admin/reports") 
                || requestURI.contains("/admin/users") 
                || requestURI.contains("/admin/discounts")) {
                if (!"ADMIN".equals(role)) {
                    httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied - Admin Only");
                    return;
                }
            } 
            // Shared sections for Admin and Employee: orders, categories, products, support tickets
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
