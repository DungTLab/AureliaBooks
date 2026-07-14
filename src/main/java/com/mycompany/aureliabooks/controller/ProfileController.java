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

    private static final String FULL_NAME_REGEX = "^[\\p{L} ]{2,100}$";
    private static final String PHONE_REGEX = "^0[0-9]{9}$";
    private static final String ADDRESS_REGEX = "^[\\p{L}\\p{N}\\s,\\.\\-/]{5,255}$";
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,50}$";

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

        try {
            // 2. Lấy thông tin UserProfile mới nhất từ DB
            UserProfile profile = userDAO.getUserProfile(loggedInUser.getId());
            session.setAttribute("userProfile", profile);

            // 3. Hiển thị trang profile.jsp
            request.getRequestDispatcher("/WEB-INF/user/profile.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi tải thông tin hồ sơ: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
        }
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

        try {
            String rawFullName = request.getParameter("fullName");
            String rawPhone = request.getParameter("phone");
            String rawAddress = request.getParameter("address");

            String fullName = (rawFullName != null) ? rawFullName.trim() : "";
            String phone = (rawPhone != null) ? rawPhone.trim() : "";
            String address = (rawAddress != null) ? rawAddress.trim() : "";

            if (fullName.isEmpty()) {
                session.setAttribute("profileError", "Họ và tên không được để trống!");
                response.sendRedirect(request.getContextPath() + "/profile");
                return;
            }

            if (!fullName.matches(FULL_NAME_REGEX)) {
                session.setAttribute("profileError", "Họ và tên chỉ được chứa chữ cái và khoảng trắng (từ 2 đến 100 ký tự)!");
                response.sendRedirect(request.getContextPath() + "/profile");
                return;
            }

            if (!phone.isEmpty() && !phone.matches(PHONE_REGEX)) {
                session.setAttribute("profileError", "Số điện thoại không hợp lệ! Vui lòng nhập đúng 10 chữ số bắt đầu bằng số 0.");
                response.sendRedirect(request.getContextPath() + "/profile");
                return;
            }

            if (!address.isEmpty() && !address.matches(ADDRESS_REGEX)) {
                session.setAttribute("profileError", "Địa chỉ không hợp lệ! Chỉ cho phép chữ, số, khoảng trắng và các ký tự , . - / (từ 5 đến 255 ký tự).");
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
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi cập nhật hồ sơ: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
        }
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User loggedInUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (loggedInUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        try {
            String rawOldPassword = request.getParameter("oldPassword");
            String rawNewPassword = request.getParameter("newPassword");

            String oldPassword = (rawOldPassword != null) ? rawOldPassword : "";
            String newPassword = (rawNewPassword != null) ? rawNewPassword : "";

            if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                session.setAttribute("passwordError", "Vui lòng nhập đầy đủ mật khẩu cũ và mới!");
                response.sendRedirect(request.getContextPath() + "/profile");
                return;
            }

            if (!newPassword.matches(PASSWORD_REGEX)) {
                session.setAttribute("passwordError", "Mật khẩu mới phải từ 8 đến 50 ký tự, bao gồm ít nhất 1 chữ hoa, 1 chữ thường và 1 chữ số!");
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
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi đổi mật khẩu: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
        }
    }
}
