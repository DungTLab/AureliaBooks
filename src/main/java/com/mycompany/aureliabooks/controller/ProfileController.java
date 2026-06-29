/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.UserDAO;
import com.mycompany.aureliabooks.model.User;
import com.mycompany.aureliabooks.model.UserProfile;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * User Profile & Password Controller. Created like NetBeans Maven template.
 *
 * @author DungLT
 */
@WebServlet(name = "ProfileController", urlPatterns = {"/profile"})
public class ProfileController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. Kiểm tra đăng nhập
        HttpSession session = request.getSession(false);
        User loggedInUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (loggedInUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        // --- Đọc & Xóa THÔNG BÁO TỪ SESSION ---
        if (session.getAttribute("profileSuccess") != null) {
            request.setAttribute("profileSuccess", session.getAttribute("profileSuccess"));
            session.removeAttribute("profileSuccess");
        }
        if (session.getAttribute("profileError") != null) {
            request.setAttribute("profileError", session.getAttribute("profileError"));
            session.removeAttribute("profileError");
        }
        if (session.getAttribute("passwordSuccess") != null) {
            request.setAttribute("passwordSuccess", session.getAttribute("passwordSuccess"));
            session.removeAttribute("passwordSuccess");
        }
        if (session.getAttribute("passwordError") != null) {
            request.setAttribute("passwordError", session.getAttribute("passwordError"));
            session.removeAttribute("passwordError");
        }
        // -------------------------------------------------------------

        // 2. Lấy thông tin UserProfile mới nhất từ DB
        UserProfile profile = userDAO.getUserProfile(loggedInUser.getId());
        session.setAttribute("userProfile", profile);

        // 3. Hiển thị trang profile.jsp
        request.getRequestDispatcher("/WEB-INF/user/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("updateInfo".equals(action)) {
            handleUpdateInfo(request, response);
        } else if ("changePassword".equals(action)) {
            handleChangePassword(request, response);
        }
    }

    private void handleUpdateInfo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User loggedInUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (loggedInUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        String fullName = request.getParameter("fullName").trim();
        String phone = request.getParameter("phone").trim();
        String address = request.getParameter("address").trim();

        if (fullName.isEmpty()) {
            session.setAttribute("profileError", "Họ và tên không được để trống!");
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        UserProfile newProfile = userDAO.getUserProfile(loggedInUser.getId());
        if (newProfile == null) {
            newProfile = new UserProfile();
            newProfile.setUserId(loggedInUser.getId());
        }
        newProfile.setFullName(fullName);
        newProfile.setPhone(phone);
        newProfile.setAddress(address);

        boolean isSuccess = userDAO.updateUserProfile(newProfile);

        if (isSuccess) {
            session.setAttribute("profileSuccess", "Cập nhật thông tin cá nhân thành công!");
        } else {
            session.setAttribute("profileError", "Cập nhật thất bại. Vui lòng thử lại!");
        }

        response.sendRedirect(request.getContextPath() + "/profile");
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User loggedInUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (loggedInUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");

        if (oldPassword.isEmpty() || newPassword.isEmpty()) {
            session.setAttribute("passwordError", "Vui lòng nhập đầy đủ mật khẩu cũ và mới!");
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        if (newPassword.length() < 6) {
            session.setAttribute("passwordError", "Mật khẩu mới phải từ 6 ký tự trở lên!");
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        boolean isSuccess = userDAO.changePassword(loggedInUser.getId(), oldPassword, newPassword);

        if (isSuccess) {
            session.setAttribute("passwordSuccess", "Đổi mật khẩu thành công!");
        } else {
            session.setAttribute("passwordError", "Đổi mật khẩu thất bại. Mật khẩu cũ không chính xác!");
        }

        response.sendRedirect(request.getContextPath() + "/profile");
    }
}
