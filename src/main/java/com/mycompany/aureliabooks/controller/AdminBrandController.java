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
 * Admin Brand CRUD Controller.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
@WebServlet(name = "AdminBrandController", urlPatterns = {"/admin/brands"})
public class AdminBrandController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String view = request.getParameter("view");
        if (view == null || view.equals("list")) {
            request.getRequestDispatcher("/WEB-INF/brand/list.jsp").forward(request, response);
        } else if (view.equals("create")) {
            request.getRequestDispatcher("/WEB-INF/brand/create.jsp").forward(request, response);
        } else if (view.equals("update")) {
            request.getRequestDispatcher("/WEB-INF/brand/update.jsp").forward(request, response);
        } else if (view.equals("delete")) {
            request.getRequestDispatcher("/WEB-INF/brand/delete.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Action processing logic for create, update, delete will be placed here
    }
}
