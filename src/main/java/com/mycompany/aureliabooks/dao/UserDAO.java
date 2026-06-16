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

/**
 * User DAO class.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
public class UserDAO extends BaseDAO {

    public User checkLogin(String username, String password) {
        // Empty skeleton for Sprint 1 (To be implemented by Dev 1)
        return null;
    }

    public boolean registerUser(User user, UserProfile profile) {
        // Empty skeleton for Sprint 1 (To be implemented by Dev 1)
        return false;
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
