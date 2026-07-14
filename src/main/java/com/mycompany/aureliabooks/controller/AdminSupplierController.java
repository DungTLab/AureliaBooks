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
 * Admin Supplier CRUD Controller.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
@WebServlet(name = "AdminSupplierController", urlPatterns = {"/admin/suppliers"})
public class AdminSupplierController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String view = request.getParameter("view");
        if (view == null || view.equals("list")) {
            request.getRequestDispatcher("/WEB-INF/supplier/list.jsp").forward(request, response);
        } else if (view.equals("create")) {
            request.getRequestDispatcher("/WEB-INF/supplier/create.jsp").forward(request, response);
        } else if (view.equals("update")) {
            request.getRequestDispatcher("/WEB-INF/supplier/update.jsp").forward(request, response);
        } else if (view.equals("delete")) {
            request.getRequestDispatcher("/WEB-INF/supplier/delete.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Action processing logic for create, update, delete will be placed here
    }
}
