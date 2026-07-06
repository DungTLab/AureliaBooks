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
import org.mindrot.jbcrypt.BCrypt;

/**
 * Authentication Controller for Login, Register, Logout. Created like NetBeans
 * Maven template.
 *
 * @author DungLT
 */
@WebServlet(name = "AuthController", urlPatterns = {"/auth"})
public class AuthController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            // Hủy session và logout
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/");
        } else if ("register".equals(action)) {
            request.getRequestDispatcher("/WEB-INF/auth/register.jsp").forward(request, response);
        } else if ("login".equals(action)) {
            request.getRequestDispatcher("/WEB-INF/auth/login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("login".equals(action)) {
            handlelogin(request, response);
            // Xử lý đăng nhập
        } else if ("register".equals(action)) {
            handleRegister(request, response);
            // Xử lý đăng ký tài khoản
        }

    }

    private void handlelogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String username = request.getParameter("username").trim();
            String password = request.getParameter("password");
            if (username.isEmpty() || password.isEmpty()) {
                request.setAttribute("error", "Tài khoản và mật khẩu không được để trống!");
                request.getRequestDispatcher("/WEB-INF/auth/login.jsp").forward(request, response);
                return;
            }

            User loggedInUser = userDAO.checkLogin(username, password);
            if (loggedInUser != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("user", loggedInUser);
                // Chuyển hướng về trang chủ
                response.sendRedirect(request.getContextPath() + "/");
            } else {
                // Đăng nhập thất bại -> quay lại trang login kèm thông báo lỗi
                request.setAttribute("error", "Tài khoản hoặc mật khẩu không chính xác!");
                request.getRequestDispatcher("/WEB-INF/auth/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi hệ thống trong quá trình đăng nhập: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String username = request.getParameter("username").trim();
            String email = request.getParameter("email").trim();
            String fullName = request.getParameter("fullName").trim();
            String password = request.getParameter("password").trim();

            if (username.isEmpty() || email.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
                request.setAttribute("error", "Vui lòng nhập đầy đủ các trường thông tin");
                request.getRequestDispatcher("/WEB-INF/auth/register.jsp").forward(request, response);
                return;
            }

            if (password.length() < 6) {
                request.setAttribute("error", "Mật khẩu phải có độ dài từ 6 ký tự trở lên!");
                request.getRequestDispatcher("/WEB-INF/auth/register.jsp").forward(request, response);
                return;
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setRoleId(3);
            user.setPasswordHash(hashedPassword);
            user.setAuthProvider("local");

            UserProfile profile = new UserProfile();
            profile.setFullName(fullName);

            boolean isSuccess = userDAO.registerUser(user, profile);

            if (isSuccess) {
                request.setAttribute("success", "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
                request.getRequestDispatcher("/WEB-INF/auth/login.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Đăng ký thất bại! Tài khoản hoặc Email có thể đã tồn tại.");
                request.getRequestDispatcher("/WEB-INF/auth/register.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi hệ thống trong quá trình đăng ký: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
        }
    }
}
