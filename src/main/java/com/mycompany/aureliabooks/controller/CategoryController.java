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

    /**
     * Validate the category name: - must not be empty - must contain only
     * letters and spaces - length must be between 2 and 100 characters
     */
    private String validateCategoryName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Tên danh mục không được để trống.";
        }
        String trimmed = name.trim();
        if (!trimmed.matches("^[\\p{L}0-9 \\-&()]+$")) {
            return "Tên danh mục chỉ được chứa chữ cái, dấu cách, chứa số hoặc ký tự đặc biệt.";
        }
        if (trimmed.length() < 2 || trimmed.length() > 100) {
            return "Tên danh mục phải có độ dài từ 2 đến 100 ký tự.";
        }
        return null;
    }

    /**
     * Parse a nullable integer from a string input. Returns null for empty or
     * invalid values.
     */
    private Integer parseNullableInteger(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            String ctx = request.getContextPath();
            if ("create".equals(action)) {
                request.setAttribute("parentCategories", categoryDAO.getAllCategories());
                request.getRequestDispatcher("/WEB-INF/category/create.jsp").forward(request, response);

            } else if ("update".equals(action)) {
                Integer id;
                try {
                    id = Integer.parseInt(request.getParameter("id"));
                } catch (NumberFormatException | NullPointerException e) {
                    request.setAttribute("errorMessage", "ID không hợp lệ.");
                    request.setAttribute("categories", categoryDAO.getAllCategories());
                    request.getRequestDispatcher("/WEB-INF/category/list.jsp").forward(request, response);
                    return;
                }

                // Kiem tra danh muc co ton tai hay ko
                Category category = categoryDAO.getCategoryById(id);
                if (category == null) {
                    request.setAttribute("errorMessage", "Danh mục không tồn tại.");
                    request.setAttribute("categories", categoryDAO.getAllCategories());
                    request.getRequestDispatcher("/WEB-INF/category/list.jsp").forward(request, response);
                    return;
                }
                request.setAttribute("category", category);
                request.setAttribute("parentCategories", categoryDAO.getAllCategories());

                request.getRequestDispatcher("/WEB-INF/category/update.jsp").forward(request, response);

            } else {
                request.setAttribute("categories", categoryDAO.getAllCategories());
                request.setAttribute("successMessage", request.getParameter("success"));
                request.setAttribute("errorMessage", request.getParameter("error"));

                request.getRequestDispatcher("/WEB-INF/category/list.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi tải thông tin danh mục: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            String ctx = request.getContextPath();

            if ("create".equals(action)) {
                String name = request.getParameter("name");
                String parentIdRaw = request.getParameter("parentId");

                String errorMessage = validateCategoryName(name);
                Integer parentId = parseNullableInteger(parentIdRaw);
                if (parentIdRaw != null && !parentIdRaw.isEmpty() && parentId == null) {
                    errorMessage = "Danh mục cha không hợp lệ.";
                }

                if (errorMessage != null) {
                    Category category = new Category();
                    category.setName(name == null ? "" : name.trim());
                    category.setParentId(parentId);

                    request.setAttribute("errorMessage", errorMessage);
                    request.setAttribute("category", category);
                    request.setAttribute("parentCategories", categoryDAO.getAllCategories());
                    request.getRequestDispatcher("/WEB-INF/category/create.jsp").forward(request, response);
                    return;
                }

                Category category = new Category();
                category.setName(name.trim());
                category.setParentId(parentId);

                boolean success = categoryDAO.insertCategory(category);

                if (success) {
                    request.setAttribute("successMessage", "Thêm danh mục thành công.");
                    request.setAttribute("categories", categoryDAO.getAllCategories());
                    request.getRequestDispatcher("/WEB-INF/category/list.jsp").forward(request, response);
                } else {
                    request.setAttribute("errorMessage", "Không thể thêm danh mục.");
                    request.setAttribute("parentCategories", categoryDAO.getAllCategories());
                    request.getRequestDispatcher("/WEB-INF/category/create.jsp").forward(request, response);
                }
            } else if ("update".equals(action)) {
                // Check 
                Integer id;
                try {
                    id = Integer.parseInt(request.getParameter("id"));

                } catch (NumberFormatException | NullPointerException e) {
                    request.setAttribute("errorMessage", "ID không hợp lệ.");
                    request.setAttribute("categories", categoryDAO.getAllCategories());
                    request.getRequestDispatcher("/WEB-INF/category/list.jsp").forward(request, response);
                    return;
                }

                // Check category have already existed
                Category existing = categoryDAO.getCategoryById(id);
                System.out.println("CAtegory: " + existing);
                if (existing == null) {
                    request.setAttribute("errorMessage", "Danh mục không tồn tại.");
                    request.setAttribute("categories", categoryDAO.getAllCategories());
                    request.getRequestDispatcher("/WEB-INF/category/list.jsp").forward(request, response);
                    return;
                }
                String name = request.getParameter("name");
                String parentIdRaw = request.getParameter("parentId");

                String errorMessage = validateCategoryName(name);
                Integer parentId = parseNullableInteger(parentIdRaw);
                if (parentIdRaw != null && !parentIdRaw.isEmpty() && parentId == null) {
                    errorMessage = "Danh mục cha không hợp lệ.";
                }
                if (parentId != null && parentId == id) {
                    errorMessage = "Danh mục cha không thể là chính danh mục này.";
                }

                Category category = new Category();
                category.setId(id);
                category.setName(name == null ? "" : name.trim());
                category.setParentId(parentId);

                if (errorMessage != null) {
                    request.setAttribute("errorMessage", errorMessage);
                    request.setAttribute("category", category);
                    request.setAttribute("parentCategories", categoryDAO.getAllCategories());
                    request.getRequestDispatcher("/WEB-INF/category/update.jsp").forward(request, response);
                    return;
                }

                boolean success = categoryDAO.updateCategory(category);

                if (success) {
                    request.setAttribute("successMessage", "Cập nhật danh mục thành công.");
                    request.setAttribute("categories", categoryDAO.getAllCategories());
                    request.getRequestDispatcher("/WEB-INF/category/list.jsp").forward(request, response);
                } else {
                    request.setAttribute("errorMessage", "Không thể cập nhật danh mục.");
                    request.setAttribute("category", category);
                    request.setAttribute("parentCategories", categoryDAO.getAllCategories());
                    request.getRequestDispatcher("/WEB-INF/category/update.jsp").forward(request, response);
                }
            } else if ("delete".equals(action)) {
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    if (categoryDAO.hasChildCategory(id)) {
                        request.setAttribute("errorMessage", "Danh mục đang có danh mục con, không thể xóa.");
                        request.setAttribute("categories", categoryDAO.getAllCategories());
                        request.getRequestDispatcher("/WEB-INF/category/list.jsp").forward(request, response);
                        return;
                    }

                    if (categoryDAO.hasProducts(id)) {
                        request.setAttribute("errorMessage", "Danh mục đang có sản phẩm, không thể xóa.");
                        request.setAttribute("categories", categoryDAO.getAllCategories());
                        request.getRequestDispatcher("/WEB-INF/category/list.jsp").forward(request, response);
                        return;
                    }
                    boolean success = categoryDAO.deleteCategory(id);

                    if (success) {
                        request.setAttribute("successMessage", "Xóa danh mục thành công.");
                    } else {
                        request.setAttribute("errorMessage", "Không thể xóa danh mục.");
                    }
                    request.setAttribute("categories", categoryDAO.getAllCategories());
                    request.getRequestDispatcher("/WEB-INF/category/list.jsp").forward(request, response);
                } catch (NumberFormatException e) {
                    request.setAttribute("errorMessage", "ID không hợp lệ.");
                    request.setAttribute("categories", categoryDAO.getAllCategories());
                    request.getRequestDispatcher("/WEB-INF/category/list.jsp").forward(request, response);
                }
            } else {
                response.sendRedirect(ctx + "/admin/categories");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi xử lý danh mục: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
        }
    }
}
