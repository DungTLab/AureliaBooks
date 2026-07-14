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
 * Admin Inventory Management Controller.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
@WebServlet(name = "AdminInventoryController", urlPatterns = {"/admin/inventory"})
public class AdminInventoryController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String view = request.getParameter("view");
        if (view == null || view.equals("list")) {
            request.getRequestDispatcher("/WEB-INF/inventory/list.jsp").forward(request, response);
        } else if (view.equals("adjust")) {
            request.getRequestDispatcher("/WEB-INF/inventory/adjust.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Inventory adjustment POST handler goes here
    }
}
