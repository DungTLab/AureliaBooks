/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.CategoryDAO;
import com.mycompany.aureliabooks.model.Category;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Admin Category CRUD Controller. Created like NetBeans Maven template.
 *
 * @author DungLT
 */
@WebServlet(name = "CategoryController", urlPatterns = {"/admin/categories"})
public class CategoryController extends HttpServlet {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("create".equals(action)) {
            request.setAttribute("parentCategories", categoryDAO.getAllCategories());
            request.getRequestDispatcher("/WEB-INF/category/create.jsp").forward(request, response);

        } else if ("update".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));

            request.setAttribute("category", categoryDAO.getCategoryById(id));
            request.setAttribute("parentCategories", categoryDAO.getAllCategories());

            request.getRequestDispatcher("/WEB-INF/category/update.jsp").forward(request, response);

        } else {
            request.setAttribute("categories", categoryDAO.getAllCategories());
            request.setAttribute("successMessage", request.getParameter("success"));
            request.setAttribute("errorMessage", request.getParameter("error"));

            request.getRequestDispatcher("/WEB-INF/category/list.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("create".equals(action)) {
            // Xử lý thêm mới danh mục
            String name = request.getParameter("name");
            String parentIdRaw = request.getParameter("parentId");

            Category category = new Category();
            category.setName(name.trim());

            if (parentIdRaw == null || parentIdRaw.isEmpty()) {
                category.setParentId(null);
            } else {
                category.setParentId(Integer.parseInt(parentIdRaw));
            }

            boolean sucess = categoryDAO.insertCategory(category);

            if (sucess) {
                response.sendRedirect(request.getContextPath() + "/admin/categories");
            } else {
                request.setAttribute("errorMessage", "Không thể thêm danh mục.");
                request.setAttribute("parentCategories", categoryDAO.getAllCategories());
                request.getRequestDispatcher("/WEB-INF/category/create.jsp").forward(request, response);
            }
//            createCategory(request, response);
        } else if ("update".equals(action)) {
            // Xử lý cập nhật danh mục
            int id = Integer.parseInt(request.getParameter("id"));
            String name = request.getParameter("name");
            String parentIdRaw = request.getParameter("parentId");

            Category category = new Category();
            category.setId(id);
            category.setName(name.trim());

            if (parentIdRaw == null || parentIdRaw.isEmpty()) {
                category.setParentId(null);
            } else {
                category.setParentId(Integer.parseInt(parentIdRaw));
            }

            boolean success = categoryDAO.updateCategory(category);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/categories");
            } else {
                request.setAttribute("errorMessage", "Không thể cập nhật danh mục.");
                request.setAttribute("category", category);
                request.setAttribute("parentCategories", categoryDAO.getAllCategories());
                request.getRequestDispatcher("/WEB-INF/category/update.jsp").forward(request, response);
            }
//            updateCategory(request, response);
        } else if ("delete".equals(action)) {
            // Xử lý xóa danh mục
            int id = Integer.parseInt(request.getParameter("id"));

            boolean success = categoryDAO.deleteCategory(id);

            response.sendRedirect(request.getContextPath() + "/admin/categories");
//            deleteCategory(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/categories");
        }
    }
}
