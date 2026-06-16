/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aureliabooks.dao;

import com.mycompany.aureliabooks.model.Order;
import com.mycompany.aureliabooks.model.Product;
import java.util.List;
import java.util.Map;

/**
 * Order & Returns & Statistics DAO class.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
public class OrderDAO extends BaseDAO {

    public boolean insertOrder(Order order) {
        // Empty skeleton for Sprint 1 (To be implemented by Dev 4)
        return false;
    }

    public List<Order> getOrdersByUserId(int userId) {
        // Empty skeleton for Sprint 1 (To be implemented by Dev 4)
        return null;
    }

    public List<Order> getAllOrders() {
        // Empty skeleton for Sprint 1 (To be implemented by Dev 5)
        return null;
    }

    public Order getOrderById(int orderId) {
        // Empty skeleton for Sprint 1 (To be implemented by Dev 4/5)
        return null;
    }

    public boolean updateOrderStatus(int orderId, String status, Integer processedByUserId) {
        // Empty skeleton for Sprint 1 (To be implemented by Dev 5)
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
