/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aureliabooks.dao;

import com.mycompany.aureliabooks.model.Order;
import com.mycompany.aureliabooks.model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Order & Returns & Statistics DAO class. Created like NetBeans Maven template.
 *
 * @author DungLT
 */
public class OrderDAO extends BaseDAO {

    // Helper method for reusing
    private Order mapResultSetToOrder(ResultSet rs) throws Exception {
        Order order = new Order();
        order.setId(rs.getInt("Id"));
        order.setUserId(rs.getInt("UserId"));
        order.setDiscountId((Integer) rs.getObject("DiscountId"));
        order.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        order.setStatus(rs.getString("Status"));
        order.setShippingAddress(rs.getString("ShippingAddress"));
        order.setContactPhone(rs.getString("ContactPhone"));
        order.setProcessedByUserId((Integer) rs.getInt("ProcessedByUserId"));
        order.setReturnReason(rs.getString("ReturnReason"));
        order.setCreatedAt(rs.getTimestamp("CreatedAt"));

        return order;
    }

    public boolean insertOrder(Order order) {
        // Empty skeleton for Sprint 1 (To be implemented by Dev 4)
        return false;
    }

    public List<Order> getOrdersByUserId(int userId) {
        // Empty skeleton for Sprint 1 (To be implemented by Dev 4)
        return null;
    }

    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT Id, UserId, TotalAmount, [Status], ShippingAddress, ContactPhone, ProcessedByUserId, ReturnReason, CreatedAt from Orders ORDER BY CreatedAt DESC";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Order> getOrdersByStatus(String status) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT Id, UserId, TotalAmount, [Status], ShippingAddress, ContactPhone, ProcessedByUserId, ReturnReason, CreatedAt from Orders WHERE [Status] = ? ORDER BY CreatedAt DESC";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    list.add(mapResultSetToOrder(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public Order getOrderById(int orderId) {
        // Empty skeleton for Sprint 1 (To be implemented by Dev 4/5)
        return null;
    }

    public boolean updateOrderStatus(int orderId, String status, Integer processedByUserId) {
        String sql = "UPDATE Orders SET [Status] = ?, ProcessedByUserId = ? WHERE Id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setObject(2, processedByUserId);
            ps.setInt(3, orderId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean requestOrderReturn(int orderId, String reason) {
        // Transaction to set status to RETURNED and increase stock (To be implemented by Dev 4)
        return false;
    }

    public Map<String, Double> getRevenueReport(String type) {
        // Statistical query (GROUP BY) for daily, monthly, quarterly reports (To be implemented by Dev 2)
        return null;
    }

    public List<Map<String, Object>> getBestSellingReport(String type) {
        // Statistical query for best-selling books (To be implemented by Dev 2)
        return null;
    }
}
