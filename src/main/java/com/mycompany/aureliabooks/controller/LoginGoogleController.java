package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.UserDAO;
import com.mycompany.aureliabooks.model.GooglePojo;
import com.mycompany.aureliabooks.model.User;
import com.mycompany.aureliabooks.model.UserProfile;
import com.mycompany.aureliabooks.util.GoogleUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

/**
 * LoginGoogleController - Handles OAuth callback from Google.
 * @author DungLT
 */
@WebServlet(name = "LoginGoogleController", urlPatterns = {"/login-google"})
public class LoginGoogleController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Step A: Read code or error parameters sent from Google via URL
        String code = request.getParameter("code");
        String error = request.getParameter("error");

        // Handle error (e.g. user cancelled or denied access on Google consent screen)
        if (error != null && !error.isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "Đăng nhập bằng Google bị từ chối hoặc xảy ra lỗi.");
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        // CASE 1: User just clicked Google sign-in button (No code yet) -> Redirect them to Google login screen
        if (code == null || code.isEmpty()) {
            String googleAuthUrl = "https://accounts.google.com/o/oauth2/auth"
                    + "?client_id=" + GoogleUtils.CLIENT_ID
                    + "&redirect_uri=" + GoogleUtils.REDIRECT_URI
                    + "&response_type=code"
                    + "&scope=email%20profile%20openid"
                    + "&prompt=select_account";
            response.sendRedirect(googleAuthUrl);
            return;
        }

        // CASE 2: Authorization Code received from Google
        try {
            // 1. Exchange code for Access Token
            String accessToken = GoogleUtils.getToken(code);

            // 2. Fetch Google User Profile using Access Token
            GooglePojo googlePojo = GoogleUtils.getUserInfo(accessToken);

            if (googlePojo == null || googlePojo.getEmail() == null) {
                throw new Exception("Không thể lấy thông tin email từ tài khoản Google của bạn.");
            }

            // 3. Query if this email already exists in our database
            User user = userDAO.getUserByEmail(googlePojo.getEmail());

            if (user == null) {
                // CASE 2.1: First time login with this Google account -> Register automatically
                user = new User();
                user.setRoleId(3); // 3 maps to the CUSTOMER role
                user.setUsername(googlePojo.getEmail()); // Set username as the Email address
                user.setEmail(googlePojo.getEmail());
                user.setAuthProvider("google"); // Mark auth provider as Google
                
                // Generate a random local password for safety/security purposes
                String randomPassword = UUID.randomUUID().toString();
                user.setPasswordHash(BCrypt.hashpw(randomPassword, BCrypt.gensalt()));

                UserProfile profile = new UserProfile();
                profile.setFullName(googlePojo.getName() != null ? googlePojo.getName() : "Google User");
                profile.setAvatarUrl(googlePojo.getPicture()); // Save Google picture url

                // Persist user and profile into the database
                boolean success = userDAO.registerUser(user, profile);
                if (success) {
                    // Retrieve newly created User to populate the auto-generated Id
                    user = userDAO.getUserByEmail(googlePojo.getEmail());
                } else {
                    throw new Exception("Unable to automatically create a new account from Google.");
                }
            }

            // CASE 2.2: User exists (or just registered) -> Proceed with login
            HttpSession session = request.getSession();
            session.setAttribute("user", user); // Store User in session for SecurityFilter checks

            // Redirect to home page
            response.sendRedirect(request.getContextPath() + "/home");

        } catch (Exception e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("error", "Google authentication error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
