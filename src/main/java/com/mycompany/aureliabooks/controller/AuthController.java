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

    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{4,30}$";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$";
    private static final String FULL_NAME_REGEX = "^[\\p{L} ]{2,100}$";
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,50}$";

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
            String rawUsername = request.getParameter("username");
            String rawPassword = request.getParameter("password");
            
            String username = (rawUsername != null) ? rawUsername.trim().toLowerCase() : "";
            String password = (rawPassword != null) ? rawPassword : "";
            
            if (username.isEmpty() || password.isEmpty()) {
                request.setAttribute("error", "Tài khoản và mật khẩu không được để trống!");
                request.getRequestDispatcher("/WEB-INF/auth/login.jsp").forward(request, response);
                return;
            }
            
            if (username.length() < 4 || username.length() > 30) {
                request.setAttribute("error", "Tài khoản không hợp lệ!");
                request.getRequestDispatcher("/WEB-INF/auth/login.jsp").forward(request, response);
                return;
            }

            User loggedInUser = userDAO.checkLogin(username, password);
            if (loggedInUser != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("user", loggedInUser);
                // Redirect to homepage
                response.sendRedirect(request.getContextPath() + "/");
            } else {
                // Login failed -> return to login page with error message
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
            String rawUsername = request.getParameter("username");
            String rawEmail = request.getParameter("email");
            String rawFullName = request.getParameter("fullName");
            String rawPassword = request.getParameter("password");

            String username = (rawUsername != null) ? rawUsername.trim() : "";
            String email = (rawEmail != null) ? rawEmail.trim() : "";
            String fullName = (rawFullName != null) ? rawFullName.trim() : "";
            String password = (rawPassword != null) ? rawPassword.trim() : "";

            if (username.isEmpty() || email.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
                request.setAttribute("error", "Vui lòng nhập đầy đủ các trường thông tin");
                request.getRequestDispatcher("/WEB-INF/auth/register.jsp").forward(request, response);
                return;
            }

            if (!username.matches(USERNAME_REGEX)) {
                request.setAttribute("error", "Tài khoản chỉ gồm chữ cái, số, dấu gạch dưới và từ 4-30 ký tự!");
                request.getRequestDispatcher("/WEB-INF/auth/register.jsp").forward(request, response);
                return;
            }

            if (!email.matches(EMAIL_REGEX)) {
                request.setAttribute("error", "Email không hợp lệ!");
                request.getRequestDispatcher("/WEB-INF/auth/register.jsp").forward(request, response);
                return;
            }

            if (!fullName.matches(FULL_NAME_REGEX)) {
                request.setAttribute("error", "Họ và tên chỉ được chứa chữ cái và khoảng trắng (từ 2 đến 100 ký tự)!");
                request.getRequestDispatcher("/WEB-INF/auth/register.jsp").forward(request, response);
                return;
            }

            if (!password.matches(PASSWORD_REGEX)) {
                request.setAttribute("error", "Mật khẩu phải từ 8 đến 50 ký tự, bao gồm ít nhất 1 chữ hoa, 1 chữ thường và 1 chữ số!");
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
