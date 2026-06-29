package com.mycompany.aureliabooks.dao;

import com.mycompany.aureliabooks.model.Order;
import com.mycompany.aureliabooks.model.OrderItem;
import com.mycompany.aureliabooks.model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object (DAO) for Order persistence.
 * 
 * DESIGN HIGHLIGHTS:
 * - Exception Propagation: Throws SQLException to Controller instead of swallowing errors.
 * - SQL Injection Prevention: Uses Parameterized Queries (PreparedStatement) exclusively.
 * - ACID Compliance: Uses Manual Commit/Rollback for complex multi-table operations.
 */
public class OrderDAO extends BaseDAO {

    /**
     * Utility method to map a database row to an Order object.
     * Prevents code duplication across multiple SELECT methods.
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
     * Retrieves the total count of orders based on filters.
     * Essential for mathematical calculation of Total Pages in pagination.
     */
    public int getTotalOrdersCount(String status, String searchQuery) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Orders WHERE 1=1 ");
        
        // Dynamically append SQL conditions based on active filters
        if (status != null && !status.equals("ALL")) {
            sql.append(" AND [Status] = ? ");
        }
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append(" AND (CAST(Id AS VARCHAR) = ? OR ContactPhone LIKE ?) ");
        }

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            // Bind parameters sequentially to prevent SQL Injection
            if (status != null && !status.equals("ALL")) {
                ps.setString(paramIndex++, status);
            }
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                ps.setString(paramIndex++, searchQuery.trim());
                ps.setString(paramIndex++, "%" + searchQuery.trim() + "%"); // Partial match for phone
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver missing in classpath", e);
        }
        return 0;
    }

    /**
     * Retrieves a specific chunk (page) of orders from the database.
     * Uses SQL Server OFFSET-FETCH mechanism for high-performance server-side pagination.
     */
    public List<Order> getOrdersPaged(String status, String searchQuery, int offset, int pageSize) throws SQLException {
        List<Order> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT Id, UserId, DiscountId, TotalAmount, [Status], ShippingAddress, ContactPhone, ProcessedByUserId, ReturnReason, CreatedAt FROM Orders WHERE 1=1 ");

        if (status != null && !status.equals("ALL")) {
            sql.append(" AND [Status] = ? ");
        }
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append(" AND (CAST(Id AS VARCHAR) = ? OR ContactPhone LIKE ?) ");
        }
        sql.append(" ORDER BY CreatedAt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (status != null && !status.equals("ALL")) {
                ps.setString(paramIndex++, status);
            }
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                ps.setString(paramIndex++, searchQuery.trim());
                ps.setString(paramIndex++, "%" + searchQuery.trim() + "%");
            }
            // Bind pagination boundary parameters
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToOrder(rs));
                }
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver missing", e);
        }
        return list;
    }

    /**
     * Updates order status and implements Audit Trail by recording which Admin executed the action.
     */
    public boolean updateOrderStatus(int orderId, String status, int processedByUserId) throws SQLException {
        String sql = "UPDATE Orders SET [Status] = ?, ProcessedByUserId = ? WHERE Id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, processedByUserId); // Tracks accountability
            ps.setInt(3, orderId);
            return ps.executeUpdate() > 0;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver missing", e);
        }
    }

    /**
     * Retrieves complete order blueprint including line items and joined product names.
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

            // Only fetch items if the parent order successfully exists
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
            throw new SQLException("Database driver missing", e);
        }
        return order;
    }

    /**
     * Executes a strictly isolated Database Transaction to handle Order Returns.
     * Guarantees Atomicity (All-or-Nothing) across Orders, Inventory, and StockTransactions tables.
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
            
            // 1. Initiate Transaction - Disable auto-commit
            conn.setAutoCommit(false);

            int userId = -1;
            
            // 2. Validate precondition: Order must be COMPLETED
            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheckOrder)) {
                psCheck.setInt(1, orderId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        String currentStatus = rs.getString("Status");
                        if (!"COMPLETED".equals(currentStatus)) {
                            conn.rollback(); // Precondition failed -> Abort
                            return false;
                        }
                        userId = rs.getInt("UserId");
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 3. Flag order as RETURNED
            try (PreparedStatement psUpdateOrder = conn.prepareStatement(sqlUpdateOrder)) {
                psUpdateOrder.setString(1, reason);
                psUpdateOrder.setInt(2, orderId);
                psUpdateOrder.executeUpdate();
            }

            // 4. Fetch line items and iterate to restore inventory quantities
            try (PreparedStatement psGetItems = conn.prepareStatement(sqlGetItems)) {
                psGetItems.setInt(1, orderId);
                try (ResultSet rsItems = psGetItems.executeQuery()) {
                    
                    try (PreparedStatement psUpdateInv = conn.prepareStatement(sqlUpdateInventory);
                         PreparedStatement psInsertTrans = conn.prepareStatement(sqlInsertStockTrans)) {
                        
                        while (rsItems.next()) {
                            int productId = rsItems.getInt("ProductId");
                            int quantity = rsItems.getInt("Quantity");

                            // Restore physical stock count
                            psUpdateInv.setInt(1, quantity);
                            psUpdateInv.setInt(2, productId);
                            psUpdateInv.executeUpdate();

                            // Maintain historical audit log (Stock Transaction)
                            psInsertTrans.setInt(1, productId);
                            psInsertTrans.setInt(2, userId); 
                            psInsertTrans.setInt(3, quantity);
                            psInsertTrans.executeUpdate();
                        }
                    }
                }
            }

            // 5. Success -> Persist all changes atomically
            conn.commit();
            return true;

        } catch (Exception e) {
            // Rollback mechanism: Reverts DB to its pristine state before the failure occurred
            if (conn != null) {
                try {
                    conn.rollback(); 
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new SQLException("Critical Failure: Order Return Transaction aborted and rolled back.", e);
        } finally {
            // Resource cleanup: Restore connection properties and release back to pool
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

    /**
     * Returns aggregated revenue data for the requested time period.
     * Used for financial reporting and business analytics.
     *
     * @param type the aggregation period - "DAY", "MONTH", or "QUARTER"
     * @return Map with period keys (e.g., "2026-01") mapped to total revenue as Double,
     *         or null if not yet implemented
     */
    private String normalizeReportType(String type) {
        if (type == null) {
            return "DAY";
        }
        String normalized = type.trim().toUpperCase();
        if (!normalized.equals("MONTH") && !normalized.equals("QUARTER")) {
            return "DAY";
        }
        return normalized;
    }

    /**
     * Build a date filter condition for best-selling report queries
     * based on the selected report type.
     */
    private String buildBestSellerDateFilter(String type, String alias) {
        switch (normalizeReportType(type)) {
            case "MONTH":
                return "YEAR(" + alias + ") = YEAR(GETDATE()) AND MONTH(" + alias + ") = MONTH(GETDATE())";
            case "QUARTER":
                return "YEAR(" + alias + ") = YEAR(GETDATE()) AND DATEPART(QUARTER, " + alias + ") = DATEPART(QUARTER, GETDATE())";
            default:
                return "CAST(" + alias + " AS DATE) = CAST(GETDATE() AS DATE)";
        }
    }

    /**
     * Retrieve aggregated revenue data grouped by day, month, or quarter.
     * Refactored to throw SQLException to Controller for proper 500 error handling.
     */
    public Map<String, Double> getRevenueReport(String type) throws SQLException {
        Map<String, Double> revenueData = new LinkedHashMap<>();
        String reportType = normalizeReportType(type);

        String sql;
        if ("MONTH".equals(reportType)) {
            sql = "SELECT CONVERT(varchar(7), CreatedAt, 120) AS PeriodLabel, SUM(TotalAmount) AS Revenue "
                + "FROM Orders "
                + "WHERE [Status] IN ('CONFIRMED','SHIPPED','DELIVERED','COMPLETED') "
                + "GROUP BY CONVERT(varchar(7), CreatedAt, 120) "
                + "ORDER BY PeriodLabel DESC";
        } else if ("QUARTER".equals(reportType)) {
            sql = "SELECT CONCAT(YEAR(CreatedAt), '-Q', DATEPART(QUARTER, CreatedAt)) AS PeriodLabel, "
                + "SUM(TotalAmount) AS Revenue "
                + "FROM Orders "
                + "WHERE [Status] IN ('CONFIRMED','SHIPPED','DELIVERED','COMPLETED') "
                + "GROUP BY YEAR(CreatedAt), DATEPART(QUARTER, CreatedAt) "
                + "ORDER BY PeriodLabel DESC";
        } else {
            sql = "SELECT CONVERT(varchar(10), CreatedAt, 23) AS PeriodLabel, SUM(TotalAmount) AS Revenue "
                + "FROM Orders "
                + "WHERE [Status] IN ('CONFIRMED','SHIPPED','DELIVERED','COMPLETED') "
                + "GROUP BY CONVERT(varchar(10), CreatedAt, 23) "
                + "ORDER BY PeriodLabel DESC";
        }

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                revenueData.put(rs.getString("PeriodLabel"), rs.getDouble("Revenue"));
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver missing", e);
        }

        return revenueData;
    }

    /**
     * Retrieve best-selling product report data for the selected time period.
     * Refactored to throw SQLException to Controller for proper 500 error handling.
     */
    public List<Map<String, Object>> getBestSellingReport(String type) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String dateFilter = buildBestSellerDateFilter(type, "o.CreatedAt");

        String sql = "SELECT TOP 10 p.SKU AS sku, p.Title AS title, SUM(oi.Quantity) AS salesQuantity "
                   + "FROM OrderItems oi "
                   + "JOIN Orders o ON oi.OrderId = o.Id "
                   + "JOIN Products p ON oi.ProductId = p.Id "
                   + "WHERE o.[Status] IN ('CONFIRMED','SHIPPED','DELIVERED','COMPLETED') "
                   + "AND " + dateFilter + " "
                   + "GROUP BY p.SKU, p.Title "
                   + "ORDER BY salesQuantity DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("sku", rs.getString("sku"));
                item.put("title", rs.getString("title"));
                item.put("salesQuantity", rs.getInt("salesQuantity"));
                list.add(item);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver missing", e);
        }
        return list;
    }
}