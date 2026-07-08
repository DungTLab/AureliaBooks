/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aureliabooks.dao;

import com.mycompany.aureliabooks.model.Discount;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Discount/Voucher DAO class.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
public class DiscountDAO extends BaseDAO {

    public List<Discount> findAll() {
        List<Discount> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM Discounts ORDER BY Id DESC";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                Discount d = new Discount();
                d.setId(rs.getInt("Id"));
                d.setCode(rs.getString("Code"));
                d.setDiscountPercent(rs.getBigDecimal("DiscountPercent"));
                d.setMaxDiscountAmount(rs.getBigDecimal("MaxDiscountAmount"));
                d.setMinOrderValue(rs.getBigDecimal("MinOrderValue"));
                d.setStartDate(rs.getTimestamp("StartDate"));
                d.setEndDate(rs.getTimestamp("EndDate"));
                d.setIsActive(rs.getBoolean("IsActive"));
                list.add(d);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Discount getDiscountById(int id) {
        String sql = "SELECT * FROM Discounts WHERE Id = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Discount d = new Discount();
                    d.setId(rs.getInt("Id"));
                    d.setCode(rs.getString("Code"));
                    d.setDiscountPercent(rs.getBigDecimal("DiscountPercent"));
                    d.setMaxDiscountAmount(rs.getBigDecimal("MaxDiscountAmount"));
                    d.setMinOrderValue(rs.getBigDecimal("MinOrderValue"));
                    d.setStartDate(rs.getTimestamp("StartDate"));
                    d.setEndDate(rs.getTimestamp("EndDate"));
                    d.setIsActive(rs.getBoolean("IsActive"));
                    return d;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Discount getDiscountByCode(String code) {
        String sql = "SELECT * FROM Discounts WHERE Code = ? AND IsActive = 1 AND GETDATE() BETWEEN StartDate AND EndDate";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, code);
            try (java.sql.ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Discount d = new Discount();
                    d.setId(rs.getInt("Id"));
                    d.setCode(rs.getString("Code"));
                    d.setDiscountPercent(rs.getBigDecimal("DiscountPercent"));
                    d.setMaxDiscountAmount(rs.getBigDecimal("MaxDiscountAmount"));
                    d.setMinOrderValue(rs.getBigDecimal("MinOrderValue"));
                    d.setStartDate(rs.getTimestamp("StartDate"));
                    d.setEndDate(rs.getTimestamp("EndDate"));
                    d.setIsActive(rs.getBoolean("IsActive"));
                    return d;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertDiscount(Discount discount) {
        String sql = "INSERT INTO Discounts (Code, DiscountPercent, MaxDiscountAmount, MinOrderValue, StartDate, EndDate, IsActive) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, discount.getCode());
            statement.setBigDecimal(2, discount.getDiscountPercent());
            statement.setBigDecimal(3, discount.getMaxDiscountAmount());
            statement.setBigDecimal(4, discount.getMinOrderValue());
            statement.setTimestamp(5, discount.getStartDate());
            statement.setTimestamp(6, discount.getEndDate());
            statement.setBoolean(7, discount.isIsActive());
            return statement.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateDiscount(Discount discount) {
        String sql = "UPDATE Discounts SET Code=?, DiscountPercent=?, MaxDiscountAmount=?, MinOrderValue=?, StartDate=?, EndDate=?, IsActive=? WHERE Id=?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, discount.getCode());
            statement.setBigDecimal(2, discount.getDiscountPercent());
            statement.setBigDecimal(3, discount.getMaxDiscountAmount());
            statement.setBigDecimal(4, discount.getMinOrderValue());
            statement.setTimestamp(5, discount.getStartDate());
            statement.setTimestamp(6, discount.getEndDate());
            statement.setBoolean(7, discount.isIsActive());
            statement.setInt(8, discount.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteDiscount(int id) {
        String sql = "DELETE FROM Discounts WHERE Id = ?";
        try (java.sql.Connection conn = getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (java.sql.SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
