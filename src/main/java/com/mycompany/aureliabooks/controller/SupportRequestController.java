/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.aureliabooks.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Customer Support Request Controller for both clients and admins.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
@WebServlet(name = "SupportRequestController", urlPatterns = {"/support", "/admin/support"})
public class SupportRequestController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (uri.contains("/admin/")) {
            // Admin actions
            String action = request.getParameter("action");
            if ("reply".equals(action)) {
                request.getRequestDispatcher("/WEB-INF/support/reply.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/WEB-INF/support/list.jsp").forward(request, response);
            }
        } else {
            // Customer actions
            String action = request.getParameter("action");
            if ("create".equals(action)) {
                request.getRequestDispatcher("/WEB-INF/support/create.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/WEB-INF/support/create.jsp").forward(request, response); // fallback
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (uri.contains("/admin/")) {
            // Admin replies to support request ticket
        } else {
            // Customer submits support request ticket
        }
    }
}
