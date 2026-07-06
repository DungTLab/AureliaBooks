/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aureliabooks.dao;

import com.mycompany.aureliabooks.model.User;
import com.mycompany.aureliabooks.model.UserProfile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;

/**
 * User DAO class. Created like NetBeans Maven template.
 *
 * @author DungLT
 */
public class UserDAO extends BaseDAO {

    public User checkLogin(String username, String password) {
        String sql = "SELECT u.Id, u.RoleId, r.Name AS RoleName, u.Username, u.PasswordHash, u.Email, u.IsActive "
                + "FROM Users u "
                + "JOIN Roles r ON u.RoleId = r.Id "
                + "WHERE u.Username = ? AND u.IsActive = 1";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("PasswordHash");
                    // Xác thực mật khẩu thông qua BCrypt
                    if (BCrypt.checkpw(password, storedHash)) {
                        User user = new User();
                        user.setId(rs.getInt("Id"));
                        user.setRoleId(rs.getInt("RoleId"));
                        user.setRoleName(rs.getString("RoleName"));
                        user.setUsername(rs.getString("Username"));
                        user.setEmail(rs.getString("Email"));
                        user.setIsActive(rs.getBoolean("IsActive"));
                        return user;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerUser(User user, UserProfile profile) {
        String insertUserSql = "INSERT INTO Users (RoleId, Username, PasswordHash, Email, AuthProvider, IsActive) VALUES (?, ?, ?, ?, ?, 1)";
        String insertProfileSql = "INSERT INTO UserProfiles (UserId, FullName, Phone, Address, AvatarUrl) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement psUser = null;
        PreparedStatement psProfile = null;
        ResultSet rsKeys = null;

        try {
            conn = getConnection();
            // Bắt đầu Transaction
            conn.setAutoCommit(false);

            // 1. Chèn vào bảng Users
            psUser = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS);
            psUser.setInt(1, user.getRoleId());
            psUser.setString(2, user.getUsername());
            psUser.setString(3, user.getPasswordHash());
            psUser.setString(4, user.getEmail());
            psUser.setString(5, user.getAuthProvider() != null ? user.getAuthProvider() : "local");

            int affectedRows = psUser.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();
                return false;
            }

            // Lấy ID tự sinh của User mới chèn
            rsKeys = psUser.getGeneratedKeys();
            int newUserId = 0;
            if (rsKeys.next()) {
                newUserId = rsKeys.getInt(1);
            } else {
                conn.rollback();
                return false;
            }

            // 2. Chèn vào bảng UserProfiles
            psProfile = conn.prepareStatement(insertProfileSql);
            psProfile.setInt(1, newUserId);
            psProfile.setString(2, profile.getFullName());
            psProfile.setString(3, profile.getPhone());
            psProfile.setString(4, profile.getAddress());
            psProfile.setString(5, profile.getAvatarUrl());

            psProfile.executeUpdate();

            // Commit Transaction nếu tất cả thành công
            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Hoàn tác nếu xảy ra lỗi bất kỳ
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            // Đóng tất cả tài nguyên kết nối
            try {
                if (rsKeys != null) {
                    rsKeys.close();
                }
                if (psUser != null) {
                    psUser.close();
                }
                if (psProfile != null) {
                    psProfile.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public UserProfile getUserProfile(int userId) {
        String sql = "SELECT * FROM UserProfiles WHERE UserId = ?"; // Tìm theo UserId
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserProfile profile = new UserProfile();
                    profile.setId(rs.getInt("Id"));
                    profile.setUserId(rs.getInt("UserId"));
                    profile.setFullName(rs.getString("FullName"));
                    profile.setPhone(rs.getString("Phone"));
                    profile.setAddress(rs.getString("Address"));
                    profile.setAvatarUrl(rs.getString("AvatarUrl")); // Tên cột đúng: AvatarUrl
                    return profile;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUserProfile(UserProfile profile) {
        String sql = "UPDATE UserProfiles SET FullName = ?, Phone = ?, [Address] = ?, AvatarUrl = ? WHERE UserId = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, profile.getFullName());
            ps.setString(2, profile.getPhone());
            ps.setString(3, profile.getAddress());
            ps.setString(4, profile.getAvatarUrl());
            ps.setInt(5, profile.getUserId()); // Định danh chỉ bằng UserId

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        // Bước 1: Lấy mật khẩu đã băm cũ từ DB của user này ra
        String selectSql = "SELECT PasswordHash FROM Users WHERE Id = ?";
        String updateSql = "UPDATE Users SET PasswordHash = ? WHERE Id = ?";

        try (Connection conn = getConnection()) {
            String storedHash = null;

            // Truy vấn lấy hash cũ
            try (PreparedStatement psSelect = conn.prepareStatement(selectSql)) {
                psSelect.setInt(1, userId);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        storedHash = rs.getString("PasswordHash");
                    }
                }
            }

            // Bước 2: So khớp mật khẩu cũ thô với hash trong DB
            if (storedHash != null && BCrypt.checkpw(oldPassword, storedHash)) {
                // Bước 3: Nếu khớp, băm mật khẩu mới và cập nhật vào DB
                String newHashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
                try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                    psUpdate.setString(1, newHashedPassword);
                    psUpdate.setInt(2, userId);
                    return psUpdate.executeUpdate() > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Trả về false nếu sai mật khẩu cũ hoặc lỗi kết nối
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT u.Id, u.RoleId, r.Name AS RoleName, u.Username, u.Email, u.IsActive, u.AuthProvider FROM Users u JOIN Roles r ON u.RoleId = r.Id WHERE u.Email = ? AND u.IsActive = 1";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("Id"));
                    user.setRoleId(rs.getInt("RoleId"));
                    user.setRoleName(rs.getString("RoleName"));
                    user.setUsername(rs.getString("Username"));
                    user.setEmail(rs.getString("Email"));
                    user.setIsActive(rs.getBoolean("IsActive"));
                    user.setAuthProvider(rs.getString("AuthProvider"));
                    return user;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
