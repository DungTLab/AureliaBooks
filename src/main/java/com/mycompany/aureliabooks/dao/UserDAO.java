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
import java.util.List;
import java.util.ArrayList;
import com.mycompany.aureliabooks.model.Role;

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
                    // Verify password via BCrypt
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
            // Start Transaction
            conn.setAutoCommit(false);

            // 1. Insert into Users table
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

            // Get auto-generated ID of the newly inserted User
            rsKeys = psUser.getGeneratedKeys();
            int newUserId = 0;
            if (rsKeys.next()) {
                newUserId = rsKeys.getInt(1);
            } else {
                conn.rollback();
                return false;
            }

            // 2. Insert into UserProfiles table
            psProfile = conn.prepareStatement(insertProfileSql);
            psProfile.setInt(1, newUserId);
            psProfile.setString(2, profile.getFullName());
            psProfile.setString(3, profile.getPhone());
            psProfile.setString(4, profile.getAddress());
            psProfile.setString(5, profile.getAvatarUrl());

            psProfile.executeUpdate();

            // Commit Transaction if all steps succeed
            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback if any error occurs
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            // Close all connection resources
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
        String sql = "SELECT * FROM UserProfiles WHERE UserId = ?"; // Find by UserId
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
                    profile.setAvatarUrl(rs.getString("AvatarUrl")); // Exact column name: AvatarUrl
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
            ps.setInt(5, profile.getUserId()); // Identify only by UserId

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        // Step 1: Retrieve old hashed password from DB for this user
        String selectSql = "SELECT PasswordHash FROM Users WHERE Id = ?";
        String updateSql = "UPDATE Users SET PasswordHash = ? WHERE Id = ?";

        try (Connection conn = getConnection()) {
            String storedHash = null;

            // Query to get old hash
            try (PreparedStatement psSelect = conn.prepareStatement(selectSql)) {
                psSelect.setInt(1, userId);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        storedHash = rs.getString("PasswordHash");
                    }
                }
            }

            // Step 2: Match old plain password with hash in DB
            if (storedHash != null && BCrypt.checkpw(oldPassword, storedHash)) {
                // Step 3: If match, hash new password and update in DB
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
        return false; // Return false if old password is incorrect or connection fails
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

    public List<User> getAllUsersWithProfiles() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.Id, u.RoleId, r.Name AS RoleName, u.Username, u.Email, u.IsActive, u.AuthProvider, u.CreatedAt, "
                   + "       p.FullName, p.Phone "
                   + "FROM Users u "
                   + "JOIN Roles r ON u.RoleId = r.Id "
                   + "LEFT JOIN UserProfiles p ON u.Id = p.UserId "
                   + "ORDER BY u.CreatedAt DESC";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("Id"));
                user.setRoleId(rs.getInt("RoleId"));
                user.setRoleName(rs.getString("RoleName"));
                user.setUsername(rs.getString("Username"));
                user.setEmail(rs.getString("Email"));
                user.setIsActive(rs.getBoolean("IsActive"));
                user.setAuthProvider(rs.getString("AuthProvider"));
                user.setCreatedAt(rs.getTimestamp("CreatedAt"));
                user.setFullName(rs.getString("FullName"));
                user.setPhone(rs.getString("Phone"));
                list.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countUsers(String search, int roleId) {
        int count = 0;
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) FROM Users u " +
            "JOIN Roles r ON u.RoleId = r.Id " +
            "LEFT JOIN UserProfiles p ON u.Id = p.UserId WHERE 1=1"
        );
        
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (u.Username LIKE ? OR u.Id = TRY_CAST(? AS INT))");
        }
        if (roleId > 0) {
            sql.append(" AND u.RoleId = ?");
        }

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (search != null && !search.trim().isEmpty()) {
                String searchLike = "%" + search.trim() + "%";
                ps.setString(paramIndex++, searchLike);
                ps.setString(paramIndex++, search.trim());
            }
            if (roleId > 0) {
                ps.setInt(paramIndex++, roleId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public List<User> getUsersPaginated(String search, int roleId, int offset, int limit) {
        List<User> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT u.Id, u.RoleId, r.Name AS RoleName, u.Username, u.Email, u.IsActive, u.AuthProvider, u.CreatedAt, " +
            "       p.FullName, p.Phone " +
            "FROM Users u " +
            "JOIN Roles r ON u.RoleId = r.Id " +
            "LEFT JOIN UserProfiles p ON u.Id = p.UserId " +
            "WHERE 1=1"
        );
        
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (u.Username LIKE ? OR u.Id = TRY_CAST(? AS INT))");
        }
        if (roleId > 0) {
            sql.append(" AND u.RoleId = ?");
        }
        
        sql.append(" ORDER BY u.CreatedAt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (search != null && !search.trim().isEmpty()) {
                String searchLike = "%" + search.trim() + "%";
                ps.setString(paramIndex++, searchLike);
                ps.setString(paramIndex++, search.trim());
            }
            if (roleId > 0) {
                ps.setInt(paramIndex++, roleId);
            }
            ps.setInt(paramIndex++, Math.max(offset, 0));
            ps.setInt(paramIndex++, limit);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("Id"));
                    user.setRoleId(rs.getInt("RoleId"));
                    user.setRoleName(rs.getString("RoleName"));
                    user.setUsername(rs.getString("Username"));
                    user.setEmail(rs.getString("Email"));
                    user.setIsActive(rs.getBoolean("IsActive"));
                    user.setAuthProvider(rs.getString("AuthProvider"));
                    user.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    user.setFullName(rs.getString("FullName"));
                    user.setPhone(rs.getString("Phone"));
                    list.add(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public boolean updateUserRole(int userId, int roleId) {
        String sql = "UPDATE Users SET RoleId = ? WHERE Id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean toggleUserStatus(int userId, boolean isActive) {
        String sql = "UPDATE Users SET IsActive = ? WHERE Id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isActive);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Role> getAllRoles() {
        List<Role> list = new ArrayList<>();
        String sql = "SELECT * FROM Roles ORDER BY Id";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getInt("Id"));
                role.setName(rs.getString("Name"));
                role.setDescription(rs.getString("Description"));
                list.add(role);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean isAdmin(int userId) {
        String sql = "SELECT RoleId FROM Users WHERE Id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("RoleId") == 1; // Role ID 1 is ADMIN
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
