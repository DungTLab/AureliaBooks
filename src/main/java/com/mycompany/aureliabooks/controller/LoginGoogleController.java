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
 * LoginGoogleController - Xử lý callback OAuth từ Google.
 * @author DungLT
 */
@WebServlet(name = "LoginGoogleController", urlPatterns = {"/login-google"})
public class LoginGoogleController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Bước A: Đọc mã code hoặc mã error từ Google gửi về qua URL
        String code = request.getParameter("code");
        String error = request.getParameter("error");

        // Gặp lỗi (Ví dụ: Người dùng nhấn nút Hủy từ chối cấp quyền trên màn hình Google)
        if (error != null && !error.isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "Đăng nhập bằng Google bị từ chối hoặc xảy ra lỗi.");
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        // TÌNH HUỐNG 1: Người dùng vừa click nút Google (Chưa có Code) -> Chuyển hướng họ sang Google đăng nhập
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

        // TÌNH HUỐNG 2: Đã có Authorization Code gửi về từ Google
        try {
            // 1. Gọi hàm tiện ích đổi code lấy Access Token
            String accessToken = GoogleUtils.getToken(code);

            // 2. Gửi Access Token đi lấy thông tin Profile người dùng
            GooglePojo googlePojo = GoogleUtils.getUserInfo(accessToken);

            if (googlePojo == null || googlePojo.getEmail() == null) {
                throw new Exception("Không thể lấy thông tin email từ tài khoản Google của bạn.");
            }

            // 3. Truy vấn xem Email này đã tồn tại trong CSDL của ứng dụng chưa
            User user = userDAO.getUserByEmail(googlePojo.getEmail());

            if (user == null) {
                // CASE 2.1: Tài khoản Google này đăng nhập lần đầu -> Tự động đăng ký
                user = new User();
                user.setRoleId(3); // 3 ứng với vai trò CUSTOMER
                user.setUsername(googlePojo.getEmail()); // Đặt username bằng chính Email
                user.setEmail(googlePojo.getEmail());
                user.setAuthProvider("google"); // Đánh dấu loại tài khoản Google
                
                // Mật khẩu local của acc Google sẽ được sinh ngẫu nhiên UUID để đảm bảo an toàn bảo mật
                String randomPassword = UUID.randomUUID().toString();
                user.setPasswordHash(BCrypt.hashpw(randomPassword, BCrypt.gensalt()));

                UserProfile profile = new UserProfile();
                profile.setFullName(googlePojo.getName() != null ? googlePojo.getName() : "Google User");
                profile.setAvatarUrl(googlePojo.getPicture()); // Lưu link ảnh đại diện Google cung cấp

                // Thực hiện lưu tài khoản vào DB
                boolean success = userDAO.registerUser(user, profile);
                if (success) {
                    // Lấy lại User từ DB sau khi lưu thành công để có đầy đủ thông tin Id tự sinh
                    user = userDAO.getUserByEmail(googlePojo.getEmail());
                } else {
                    throw new Exception("Không thể tự động khởi tạo tài khoản mới từ Google.");
                }
            }

            // CASE 2.2: Đã có tài khoản (hoặc vừa đăng ký xong) -> Tiến hành đăng nhập
            HttpSession session = request.getSession();
            session.setAttribute("user", user); // Lưu User vào session để SecurityFilter nhận diện

            // Điều hướng về trang chủ
            response.sendRedirect(request.getContextPath() + "/home");

        } catch (Exception e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("error", "Lỗi xác thực Google: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
