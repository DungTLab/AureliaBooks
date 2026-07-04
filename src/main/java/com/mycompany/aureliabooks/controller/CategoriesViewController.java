package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.CategoryDAO;
import com.mycompany.aureliabooks.model.Category;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controller to display all categories hierarchically on the client side.
 * Mapped to "/categories".
 * 
 * @author DungLT
 */
@WebServlet(name = "CategoriesViewController", urlPatterns = {"/categories"})
public class CategoriesViewController extends HttpServlet {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Category> allCategories = categoryDAO.getAllCategories();
        
        List<Category> parents = new ArrayList<>();
        Map<Integer, List<Category>> childrenMap = new HashMap<>();

        for (Category c : allCategories) {
            if (c.getParentId() == null) {
                parents.add(c);
            } else {
                childrenMap.computeIfAbsent(c.getParentId(), k -> new ArrayList<>()).add(c);
            }
        }

        request.setAttribute("parentCategories", parents);
        request.setAttribute("childrenMap", childrenMap);

        request.getRequestDispatcher("/WEB-INF/product/categories.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
