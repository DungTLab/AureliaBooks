package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.UserDAO;
import com.mycompany.aureliabooks.model.Role;
import com.mycompany.aureliabooks.model.User;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controller for Admin User Management (Role Promotion & Soft Delete).
 * Only accessible by ADMIN role.
 *
 * @author DungLT
 */
@WebServlet(name = "AdminUserController", urlPatterns = {"/admin/users"})
public class AdminUserController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User loggedInUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (loggedInUser == null || !"ADMIN".equals(loggedInUser.getRoleName())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied - Admin Only");
            return;
        }

        String search = request.getParameter("search");
        String roleParam = request.getParameter("roleId");
        int filterRoleId = 0;
        if (roleParam != null && !roleParam.isEmpty()) {
            try {
                filterRoleId = Integer.parseInt(roleParam);
            } catch (NumberFormatException e) {
                filterRoleId = 0;
            }
        }

        int limit = 5;
        int currentPage = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) {
                    currentPage = 1;
                }
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        int totalUsers = userDAO.countUsers(search, filterRoleId);
        int totalPages = (int) Math.ceil((double) totalUsers / limit);
        if (totalPages < 1) {
            totalPages = 1;
        }
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        int offset = (currentPage - 1) * limit;
        List<User> usersList = userDAO.getUsersPaginated(search, filterRoleId, offset, limit);
        List<Role> rolesList = userDAO.getAllRoles();

        request.setAttribute("users", usersList);
        request.setAttribute("roles", rolesList);
        request.setAttribute("search", search);
        request.setAttribute("roleId", filterRoleId);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("successMessage", request.getParameter("success"));
        request.setAttribute("errorMessage", request.getParameter("error"));

        request.getRequestDispatcher("/WEB-INF/user/list-admin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User loggedInUser = (session != null) ? (User) session.getAttribute("user") : null;
        
        if (loggedInUser == null || !"ADMIN".equals(loggedInUser.getRoleName())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied - Admin Only");
            return;
        }

        String action = request.getParameter("action");
        String ctx = request.getContextPath();

        try {
            int userId = Integer.parseInt(request.getParameter("userId"));

            if ("updateRole".equals(action)) {
                int roleId = Integer.parseInt(request.getParameter("roleId"));
                
                // Security constraint: Cannot demote own ADMIN role
                if (userId == loggedInUser.getId() && roleId != 1) {
                    response.sendRedirect(ctx + "/admin/users?error=" + URLEncoder.encode("Bạn không thể tự hạ quyền ADMIN của chính mình!", StandardCharsets.UTF_8));
                    return;
                }

                boolean success = userDAO.updateUserRole(userId, roleId);
                if (success) {
                    response.sendRedirect(ctx + "/admin/users?success=" + URLEncoder.encode("Cập nhật quyền hạn tài khoản thành công.", StandardCharsets.UTF_8));
                } else {
                    response.sendRedirect(ctx + "/admin/users?error=" + URLEncoder.encode("Không thể cập nhật quyền hạn tài khoản.", StandardCharsets.UTF_8));
                }

            } else if ("toggleStatus".equals(action)) {
                boolean active = Boolean.parseBoolean(request.getParameter("active"));

                // Security rule: Cannot lock ADMIN accounts
                if (userDAO.isAdmin(userId)) {
                    response.sendRedirect(ctx + "/admin/users?error=" + URLEncoder.encode("Không thể khóa tài khoản có vai trò ADMIN!", StandardCharsets.UTF_8));
                    return;
                }

                boolean success = userDAO.toggleUserStatus(userId, active);
                String msg = active ? "Kích hoạt tài khoản thành công." : "Khóa tài khoản thành công.";
                if (success) {
                    response.sendRedirect(ctx + "/admin/users?success=" + URLEncoder.encode(msg, StandardCharsets.UTF_8));
                } else {
                    response.sendRedirect(ctx + "/admin/users?error=" + URLEncoder.encode("Không thể cập nhật trạng thái tài khoản.", StandardCharsets.UTF_8));
                }
            } else {
                response.sendRedirect(ctx + "/admin/users");
            }

        } catch (NumberFormatException | NullPointerException e) {
            response.sendRedirect(ctx + "/admin/users?error=" + URLEncoder.encode("Tham số đầu vào không hợp lệ.", StandardCharsets.UTF_8));
        }
    }
}
