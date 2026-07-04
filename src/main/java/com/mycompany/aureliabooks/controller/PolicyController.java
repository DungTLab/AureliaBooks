package com.mycompany.aureliabooks.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controller serving static policy pages (Return Policy, Privacy Policy, Terms of Service).
 * Mapped to "/policy".
 * 
 * @author DungLT
 */
@WebServlet(name = "PolicyController", urlPatterns = {"/policy"})
public class PolicyController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String type = request.getParameter("type");

        if ("return".equals(type)) {
            request.getRequestDispatcher("/WEB-INF/policy/return.jsp").forward(request, response);
        } else if ("privacy".equals(type)) {
            request.getRequestDispatcher("/WEB-INF/policy/privacy.jsp").forward(request, response);
        } else if ("terms".equals(type)) {
            request.getRequestDispatcher("/WEB-INF/policy/terms.jsp").forward(request, response);
        } else {
            // Default back to homepage if type parameter is invalid or missing
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
