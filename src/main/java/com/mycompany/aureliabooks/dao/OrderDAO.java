package com.mycompany.aureliabooks.dao;

import com.mycompany.aureliabooks.model.Order;
import com.mycompany.aureliabooks.model.OrderItem;
import com.mycompany.aureliabooks.model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling Order persistence.
 * Responsible for CRUD operations and complex transactional workflows like Order Returns.
 */
public class OrderDAO extends BaseDAO {

    /**
     * Maps a ResultSet row to an Order entity.
     */
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("Id"));
        order.setUserId(rs.getInt("UserId"));
        order.setDiscountId((Integer) rs.getObject("DiscountId"));
        order.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        order.setStatus(rs.getString("Status"));
        order.setShippingAddress(rs.getString("ShippingAddress"));
        order.setContactPhone(rs.getString("ContactPhone"));
        order.setProcessedByUserId((Integer) rs.getObject("ProcessedByUserId"));
        order.setReturnReason(rs.getString("ReturnReason"));
        order.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return order;
    }

    /**
     * Retrieves the total count of orders for pagination calculations.
     * Throws SQLException to the caller layer for proper error handling.
     */
    public int getTotalOrdersCount(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Orders";
        if (status != null && !status.equals("ALL")) {
            sql += " WHERE [Status] = ?";
        }

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (status != null && !status.equals("ALL")) {
                ps.setString(1, status);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found", e);
        }
        return 0;
    }

    /**
     * Fetches a paginated list of orders, optionally filtered by status.
     * Utilizes OFFSET/FETCH for efficient server-side pagination.
     */
    public List<Order> getOrdersPaged(String status, int offset, int pageSize) throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT Id, UserId, DiscountId, TotalAmount, [Status], ShippingAddress, ContactPhone, ProcessedByUserId, ReturnReason, CreatedAt "
                   + "FROM Orders ";

        if (status != null && !status.equals("ALL")) {
            sql += "WHERE [Status] = ? ";
        }
        sql += "ORDER BY CreatedAt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            if (status != null && !status.equals("ALL")) {
                ps.setString(paramIndex++, status);
            }
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToOrder(rs));
                }
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found", e);
        }
        return list;
    }

    /**
     * Updates the status of an order and logs the admin user who processed it.
     */
    public boolean updateOrderStatus(int orderId, String status, int processedByUserId) throws SQLException {
        String sql = "UPDATE Orders SET [Status] = ?, ProcessedByUserId = ? WHERE Id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, processedByUserId);
            ps.setInt(3, orderId);
            return ps.executeUpdate() > 0;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found", e);
        }
    }

    /**
     * Retrieves full order details including associated OrderItems and Product titles.
     * Performs a JOIN operation to fetch item properties efficiently.
     */
    public Order getOrderById(int orderId) throws SQLException {
        Order order = null;
        String sqlOrder = "SELECT Id, UserId, DiscountId, TotalAmount, [Status], ShippingAddress, ContactPhone, ProcessedByUserId, ReturnReason, CreatedAt FROM Orders WHERE Id = ?";
        String sqlItems = "SELECT oi.Id, oi.OrderId, oi.ProductId, oi.Quantity, oi.UnitPrice, oi.SubTotal, p.Title " +
                          "FROM OrderItems oi JOIN Products p ON oi.ProductId = p.Id WHERE oi.OrderId = ?";

        try (Connection conn = getConnection()) {
            try (PreparedStatement psOrder = conn.prepareStatement(sqlOrder)) {
                psOrder.setInt(1, orderId);
                try (ResultSet rsOrder = psOrder.executeQuery()) {
                    if (rsOrder.next()) {
                        order = mapResultSetToOrder(rsOrder);
                    }
                }
            }

            if (order != null) {
                try (PreparedStatement psItems = conn.prepareStatement(sqlItems)) {
                    psItems.setInt(1, orderId);
                    try (ResultSet rsItems = psItems.executeQuery()) {
                        while (rsItems.next()) {
                            OrderItem item = new OrderItem();
                            item.setId(rsItems.getInt("Id"));
                            item.setOrderId(rsItems.getInt("OrderId"));
                            item.setProductId(rsItems.getInt("ProductId"));
                            item.setQuantity(rsItems.getInt("Quantity"));
                            item.setUnitPrice(rsItems.getBigDecimal("UnitPrice"));
                            item.setSubTotal(rsItems.getBigDecimal("SubTotal"));

                            Product product = new Product();
                            product.setId(rsItems.getInt("ProductId"));
                            product.setTitle(rsItems.getString("Title"));
                            item.setProduct(product);

                            order.getItems().add(item);
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found", e);
        }
        return order;
    }

    /**
     * Executes a complex database transaction to process an order return.
     * 1. Validates the order is in COMPLETED state.
     * 2. Updates order status to RETURNED.
     * 3. Restores product quantities to Inventory.
     * 4. Logs the transaction into StockTransactions.
     * Guaranteed Atomicity via JDBC connection transaction management.
     */
    public boolean requestOrderReturn(int orderId, String reason) throws SQLException {
        String sqlCheckOrder = "SELECT UserId, [Status] FROM Orders WHERE Id = ?";
        String sqlUpdateOrder = "UPDATE Orders SET [Status] = 'RETURNED', ReturnReason = ? WHERE Id = ?";
        String sqlGetItems = "SELECT ProductId, Quantity FROM OrderItems WHERE OrderId = ?";
        String sqlUpdateInventory = "UPDATE Inventory SET QuantityInStock = QuantityInStock + ?, LastUpdated = CURRENT_TIMESTAMP WHERE ProductId = ?";
        String sqlInsertStockTrans = "INSERT INTO StockTransactions (ProductId, HandledByUserId, TransactionType, Quantity, TransactionDate) VALUES (?, ?, 'RETURN', ?, CURRENT_TIMESTAMP)";

        Connection conn = null;
        try {
            conn = getConnection();
            // Disable auto-commit to begin transaction block
            conn.setAutoCommit(false);

            int userId = -1;
            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheckOrder)) {
                psCheck.setInt(1, orderId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        String currentStatus = rs.getString("Status");
                        if (!"COMPLETED".equals(currentStatus)) {
                            // Reject returns for non-completed orders to maintain business logic consistency
                            conn.rollback();
                            return false;
                        }
                        userId = rs.getInt("UserId");
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement psUpdateOrder = conn.prepareStatement(sqlUpdateOrder)) {
                psUpdateOrder.setString(1, reason);
                psUpdateOrder.setInt(2, orderId);
                psUpdateOrder.executeUpdate();
            }

            try (PreparedStatement psGetItems = conn.prepareStatement(sqlGetItems)) {
                psGetItems.setInt(1, orderId);
                try (ResultSet rsItems = psGetItems.executeQuery()) {
                    
                    try (PreparedStatement psUpdateInv = conn.prepareStatement(sqlUpdateInventory);
                         PreparedStatement psInsertTrans = conn.prepareStatement(sqlInsertStockTrans)) {
                        
                        while (rsItems.next()) {
                            int productId = rsItems.getInt("ProductId");
                            int quantity = rsItems.getInt("Quantity");

                            // Restore stock logic
                            psUpdateInv.setInt(1, quantity);
                            psUpdateInv.setInt(2, productId);
                            psUpdateInv.executeUpdate();

                            // Maintain audit trail
                            psInsertTrans.setInt(1, productId);
                            psInsertTrans.setInt(2, userId); 
                            psInsertTrans.setInt(3, quantity);
                            psInsertTrans.executeUpdate();
                        }
                    }
                }
            }

            // Commit the entire transaction atomically
            conn.commit();
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Undo all changes in case of failure
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new SQLException("Transaction failed during order return processing.", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}