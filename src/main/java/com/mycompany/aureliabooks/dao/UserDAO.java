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
        // Empty skeleton for Sprint 1 (To be implemented by Dev 1)
        return null;
    }

    public boolean updateUserProfile(UserProfile profile) {
        // Empty skeleton for Sprint 1 (To be implemented by Dev 1)
        return false;
    }

    public boolean changePassword(int userId, String oldPasswordHash, String newPasswordHash) {
        // Empty skeleton for Sprint 1 (To be implemented by Dev 1)
        return false;
    }
}
