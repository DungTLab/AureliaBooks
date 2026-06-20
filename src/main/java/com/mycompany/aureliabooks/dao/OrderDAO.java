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
 * DAO for order persistence, status management, returns, and reporting queries.
 */
public class OrderDAO extends BaseDAO {

    /**
     * Maps the current {@link ResultSet} row to an {@link Order} entity.
     * Keeps row-to-object conversion in one place so the query methods stay small.
     */
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

    /**
     * Inserts a new order record.
     */
    public boolean insertOrder(Order order) {
        // TODO: Implement order persistence when the checkout flow is wired up.
        return false;
    }

    /**
     * Returns all orders placed by a specific user.
     */
    public List<Order> getOrdersByUserId(int userId) {
        // TODO: Filter orders by user once the account history view is connected.
        return null;
    }

    /**
     * Returns every order sorted by newest first.
     */
    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT Id, UserId, DiscountId, TotalAmount, [Status], ShippingAddress, ContactPhone, ProcessedByUserId, ReturnReason, CreatedAt from Orders ORDER BY CreatedAt DESC";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Returns orders that match the given status, sorted by newest first.
     */
    public List<Order> getOrdersByStatus(String status) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT Id, UserId, DiscountId, TotalAmount, [Status], ShippingAddress, ContactPhone, ProcessedByUserId, ReturnReason, CreatedAt from Orders WHERE [Status] = ? ORDER BY CreatedAt DESC";

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

    /**
     * Returns a single order by its primary key.
     */
    public Order getOrderById(int orderId) {
        // TODO: Load the full order detail view from the database.
        return null;
    }

    /**
     * Updates the status of an order and records the staff member who processed it.
     */
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

    /**
     * Creates a return request for an order.
     */
    public boolean requestOrderReturn(int orderId, String reason) {
        // TODO: Wrap the return workflow in a transaction and restore inventory.
        return false;
    }

    /**
     * Returns aggregated revenue data for the requested period.
     */
    public Map<String, Double> getRevenueReport(String type) {
        // TODO: Aggregate revenue by day, month, or quarter as required by the report.
        return null;
    }

    /**
     * Returns best-selling product statistics for the requested period.
     */
    public List<Map<String, Object>> getBestSellingReport(String type) {
        // TODO: Aggregate best-selling products for the requested reporting period.
        return null;
    }
}
