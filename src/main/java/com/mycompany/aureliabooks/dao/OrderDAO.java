package com.mycompany.aureliabooks.dao;

import com.mycompany.aureliabooks.model.CartItem;
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
 * Data Access Object (DAO) for Order persistence and management.
 * Handles all database operations related to Orders, including retrieval,
 * updates, pagination, and reporting.
 * 
 * DESIGN HIGHLIGHTS:
 * - Exception Propagation: Throws SQLException to the Controller layer.
 * - SQL Injection Prevention: Uses Parameterized Queries (PreparedStatement).
 * - ACID Compliance: Uses Manual Commit/Rollback for complex multi-table
 * operations.
 */
public class OrderDAO extends BaseDAO {

    private static final String REPORT_ORDER_STATUSES = "'CONFIRMED','SHIPPING','COMPLETED'";

    /**
     * Utility method to map a database row (ResultSet) to an Order entity.
     * Prevents code duplication across multiple SELECT query methods.
     *
     * @param rs the ResultSet positioned at the current row
     * @return Order object populated with database fields
     * @throws SQLException if a database access error occurs or column is missing
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
     * Retrieves the total count of orders based on dynamic filters.
     * Essential for mathematical calculation of Total Pages in server-side
     * pagination.
     *
     * @param status      the order status filter ("ALL" or specific status)
     * @param searchQuery the keyword to search by Order ID or Contact Phone
     * @return the total number of matching orders
     * @throws SQLException if a database access error occurs
     */
    public int getTotalOrdersCount(String status, String searchQuery) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Orders WHERE 1=1 ");

        if (status != null && !status.equals("ALL")) {
            sql.append(" AND [Status] = ? ");
        }
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append(" AND (CAST(Id AS VARCHAR) = ? OR ContactPhone LIKE ?) ");
        }

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            if (status != null && !status.equals("ALL")) {
                ps.setString(paramIndex++, status);
            }
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                ps.setString(paramIndex++, searchQuery.trim());
                ps.setString(paramIndex++, "%" + searchQuery.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver missing in classpath", e);
        }
        return 0;
    }

    /**
     * Retrieves a specific chunk (page) of orders from the database.
     * Uses SQL Server OFFSET-FETCH mechanism for high-performance pagination.
     *
     * @param status      the order status filter ("ALL" or specific status)
     * @param searchQuery the keyword to search by Order ID or Contact Phone
     * @param offset      the number of rows to skip
     * @param pageSize    the maximum number of rows to return
     * @return a List of Order objects for the requested page
     * @throws SQLException if a database access error occurs
     */
    public List<Order> getOrdersPaged(String status, String searchQuery, int offset, int pageSize) throws SQLException {
        List<Order> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT Id, UserId, DiscountId, TotalAmount, [Status], ShippingAddress, ContactPhone, ProcessedByUserId, ReturnReason, CreatedAt FROM Orders WHERE 1=1 ");

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
     * Helper Method: Restores product inventory quantities when an order is
     * cancelled or returned.
     * Re-adds the quantity of each order item back to the Products table.
     *
     * @param conn    the active database Connection (must be within a transaction)
     * @param orderId the unique identifier of the order
     * @throws SQLException if a database access error occurs
     */
    private void restoreInventory(Connection conn, int orderId) throws SQLException {
        String sqlGetItems = "SELECT ProductId, Quantity FROM OrderItems WHERE OrderId = ?";
        String sqlUpdateProduct = "UPDATE Inventory SET QuantityInStock = QuantityInStock + ? WHERE ProductId = ?";

        try (PreparedStatement psGetItems = conn.prepareStatement(sqlGetItems)) {
            psGetItems.setInt(1, orderId);
            try (ResultSet rsItems = psGetItems.executeQuery()) {
                try (PreparedStatement psUpdateProduct = conn.prepareStatement(sqlUpdateProduct)) {
                    while (rsItems.next()) {
                        int productId = rsItems.getInt("ProductId");
                        int quantity = rsItems.getInt("Quantity");

                        psUpdateProduct.setInt(1, quantity);
                        psUpdateProduct.setInt(2, productId);
                        psUpdateProduct.executeUpdate();
                    }
                }
            }
        }
    }

    /**
     * Updates the status of an order and implements an Audit Trail.
     * Safely wrapped in a Transaction because it may update inventory.
     *
     * @param orderId           the unique identifier of the order to update
     * @param status            the new status value (e.g., "CONFIRMED", "SHIPPED",
     *                          "CANCELLED")
     * @param processedByUserId the ID of the staff member processing this update
     * @return true if the update was successful, false otherwise
     * @throws SQLException if a database error occurs or transaction fails
     */
    public boolean updateOrderStatus(int orderId, String status, Integer processedByUserId) throws SQLException {
        String sqlUpdateStatus = "UPDATE Orders SET [Status] = ?, ProcessedByUserId = ? WHERE Id = ?";

        Connection conn = null;
        try {
            conn = getConnection();
            // Enable manual transaction control
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sqlUpdateStatus)) {
                ps.setString(1, status);
                ps.setObject(2, processedByUserId);
                ps.setInt(3, orderId);

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // Restore inventory if the order is cancelled
            if ("CANCELLED".equals(status)) {
                restoreInventory(conn, orderId);
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new SQLException("Failed to update order status: " + e.getMessage(), e);
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

    /**
     * Retrieves the complete blueprint of a specific order.
     * Fetches the main order record and joins its associated line items
     * (OrderItems) and product names.
     *
     * @param orderId the unique identifier of the order
     * @return the fully populated Order object, or null if not found
     * @throws SQLException if a database access error occurs
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

                            if (order.getItems() == null) {
                                order.setItems(new ArrayList<>());
                            }
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
     * Guarantees Atomicity (All-or-Nothing) across Orders, Products, and
     * StockTransactions tables.
     *
     * @param orderId the unique identifier of the order being returned
     * @param reason  the reason provided by the customer for the return
     * @return true if the return transaction succeeded, false otherwise
     * @throws SQLException if a database error occurs or transaction is aborted
     */
    public boolean requestOrderReturn(int orderId, String reason) throws SQLException {
        String sqlCheckOrder = "SELECT UserId, [Status] FROM Orders WHERE Id = ?";
        String sqlUpdateOrder = "UPDATE Orders SET [Status] = 'RETURNED', ReturnReason = ? WHERE Id = ?";
        String sqlGetItems = "SELECT ProductId, Quantity FROM OrderItems WHERE OrderId = ?";
        String sqlUpdateProduct = "UPDATE Inventory SET QuantityInStock = QuantityInStock + ? WHERE ProductId = ?";
        String sqlInsertStockTrans = "INSERT INTO StockTransactions (ProductId, HandledByUserId, TransactionType, Quantity, TransactionDate) VALUES (?, ?, 'RETURN_IN', ?, CURRENT_TIMESTAMP)";

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            int userId = -1;

            // 1. Validate precondition: Order must be COMPLETED
            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheckOrder)) {
                psCheck.setInt(1, orderId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        String currentStatus = rs.getString("Status");
                        if (!"COMPLETED".equals(currentStatus)) {
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

            // 2. Flag order as RETURNED
            try (PreparedStatement psUpdateOrder = conn.prepareStatement(sqlUpdateOrder)) {
                psUpdateOrder.setString(1, reason);
                psUpdateOrder.setInt(2, orderId);
                psUpdateOrder.executeUpdate();
            }

            // 3. Fetch line items and iterate to restore inventory quantities
            try (PreparedStatement psGetItems = conn.prepareStatement(sqlGetItems)) {
                psGetItems.setInt(1, orderId);
                try (ResultSet rsItems = psGetItems.executeQuery()) {

                    try (PreparedStatement psUpdateInv = conn.prepareStatement(sqlUpdateProduct);
                            PreparedStatement psInsertTrans = conn.prepareStatement(sqlInsertStockTrans)) {

                        while (rsItems.next()) {
                            int productId = rsItems.getInt("ProductId");
                            int quantity = rsItems.getInt("Quantity");

                            // Restore physical stock count (Products table)
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

            // 4. Success -> Persist all changes atomically
            conn.commit();
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new SQLException("Critical Failure: Order Return Transaction aborted and rolled back.", e);
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

    /**
     * Inserts a new order record into the database during Customer Checkout.
     * 
     * @param order the Order object containing all order details
     * @return true if insertion succeeded, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean insertOrder(Order order, List<CartItem> checkoutItems) throws SQLException {
        if (checkoutItems == null || checkoutItems.isEmpty()) {
            return false;
        }

        String insertOrderSql = "INSERT INTO Orders (UserId, DiscountId, TotalAmount, Status, ShippingAddress, ContactPhone) VALUES (?, ?, ?, 'PENDING', ?, ?)";
        String insertOrderItemSql = "INSERT INTO OrderItems (OrderId, ProductId, Quantity, UnitPrice, SubTotal) VALUES (?, ?, ?, ?, ?)";
        String clearCartItemSql = "DELETE FROM CartItems WHERE Id = ?";
        String deductInventorySql = "UPDATE Inventory SET QuantityInStock = QuantityInStock - ? WHERE ProductId = ?";
        String insertStockTransSql = "INSERT INTO StockTransactions (ProductId, HandledByUserId, TransactionType, Quantity, TransactionDate) VALUES (?, ?, 'EXPORT', ?, CURRENT_TIMESTAMP)";

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            int orderId = -1;
            try (PreparedStatement psOrder = conn.prepareStatement(insertOrderSql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setInt(1, order.getUserId());
                if (order.getDiscountId() != null) {
                    psOrder.setInt(2, order.getDiscountId());
                } else {
                    psOrder.setNull(2, java.sql.Types.INTEGER);
                }
                psOrder.setBigDecimal(3, order.getTotalAmount());
                psOrder.setString(4, order.getShippingAddress());
                psOrder.setString(5, order.getContactPhone());
                psOrder.executeUpdate();

                try (ResultSet rs = psOrder.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    }
                }
            }

            if (orderId == -1) {
                conn.rollback();
                return false;
            }

            try (PreparedStatement psItems = conn.prepareStatement(insertOrderItemSql);
                 PreparedStatement psClear = conn.prepareStatement(clearCartItemSql);
                 PreparedStatement psDeduct = conn.prepareStatement(deductInventorySql);
                 PreparedStatement psTrans = conn.prepareStatement(insertStockTransSql)) {
                
                for (CartItem item : checkoutItems) {
                    // Insert into OrderItems
                    psItems.setInt(1, orderId);
                    psItems.setInt(2, item.getProductId());
                    psItems.setInt(3, item.getQuantity());
                    psItems.setBigDecimal(4, item.getProduct().getPrice());
                    psItems.setBigDecimal(5, item.getProduct().getPrice().multiply(new java.math.BigDecimal(item.getQuantity())));
                    psItems.addBatch();

                    // Queue delete from DB Cart if applicable
                    if (item.getId() > 0) {
                        psClear.setInt(1, item.getId());
                        psClear.addBatch();
                    }
                    
                    // Deduct Inventory
                    psDeduct.setInt(1, item.getQuantity());
                    psDeduct.setInt(2, item.getProductId());
                    psDeduct.addBatch();

                    // Log Stock Transaction
                    psTrans.setInt(1, item.getProductId());
                    psTrans.setInt(2, order.getUserId());
                    psTrans.setInt(3, item.getQuantity());
                    psTrans.addBatch();
                }
                psItems.executeBatch();
                psClear.executeBatch();
                psDeduct.executeBatch();
                psTrans.executeBatch();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Returns all orders placed by a specific user, sorted by creation date (newest
     * first).
     * Used for the Customer's Order History page.
     *
     * @param userId the unique identifier of the user
     * @return list of Order objects for the specified user
     * @throws SQLException if a database access error occurs
     */
    public List<Order> getOrdersByUserId(int userId) throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT Id, UserId, DiscountId, TotalAmount, [Status], ShippingAddress, ContactPhone, ProcessedByUserId, ReturnReason, CreatedAt FROM Orders WHERE UserId = ? ORDER BY CreatedAt DESC";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
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
     * Returns every order in the system sorted by creation date (newest first).
     * Typically superseded by getOrdersPaged() to avoid memory overload.
     *
     * @return list of all Order objects in descending order by creation date
     * @throws SQLException if a database access error occurs
     */
    public List<Order> getAllOrders() throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT Id, UserId, DiscountId, TotalAmount, [Status], ShippingAddress, ContactPhone, ProcessedByUserId, ReturnReason, CreatedAt FROM Orders ORDER BY CreatedAt DESC";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver missing", e);
        }
        return list;
    }

    /**
     * Returns all orders with a specific status, sorted by creation date (newest
     * first).
     * Typically superseded by getOrdersPaged() to avoid memory overload.
     *
     * @param status the exact order status to filter by
     * @return list of Order objects matching the given status
     * @throws SQLException if a database access error occurs
     */
    public List<Order> getOrdersByStatus(String status) throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT Id, UserId, DiscountId, TotalAmount, [Status], ShippingAddress, ContactPhone, ProcessedByUserId, ReturnReason, CreatedAt FROM Orders WHERE [Status] = ? ORDER BY CreatedAt DESC";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
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
     * Normalizes the requested report period type for statistical queries.
     * Defaults to "DAY" if the input is invalid or null.
     *
     * @param type the raw input aggregation period (e.g., "MONTH", "QUARTER")
     * @return the normalized period type ("DAY", "MONTH", or "QUARTER")
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
     * Builds a period filter SQL condition for best-selling report queries.
     *
     * @param type  the aggregation period type
     * @param alias the SQL alias for the date column to filter
     * @return the generated SQL condition string with one parameter placeholder
     */
    private String buildBestSellerPeriodFilter(String type, String alias) {
        switch (normalizeReportType(type)) {
            case "MONTH":
                return "CONVERT(varchar(7), " + alias + ", 120) = ?";
            case "QUARTER":
                return "CONCAT(YEAR(" + alias + "), '-Q', DATEPART(QUARTER, " + alias + ")) = ?";
            default:
                return "CONVERT(varchar(10), " + alias + ", 23) = ?";
        }
    }

    /**
     * Retrieves aggregated revenue data grouped by day, month, or quarter.
     * Supports generating trend charts in the Admin Dashboard.
     *
     * @param type the aggregation period ("DAY", "MONTH", "QUARTER")
     * @return Map with period keys mapped to total revenue (e.g., "2026-06" ->
     *         5000.0)
     * @throws SQLException if a database access error occurs
     */
    public Map<String, Double> getRevenueReport(String type) throws SQLException {
        Map<String, Double> revenueData = new LinkedHashMap<>();
        String reportType = normalizeReportType(type);

        String sql;
        if ("MONTH".equals(reportType)) {
            sql = "SELECT CONVERT(varchar(7), CreatedAt, 120) AS PeriodLabel, SUM(TotalAmount) AS Revenue "
                    + "FROM Orders "
                    + "WHERE [Status] IN (" + REPORT_ORDER_STATUSES + ") "
                    + "GROUP BY CONVERT(varchar(7), CreatedAt, 120) "
                    + "ORDER BY PeriodLabel DESC";
        } else if ("QUARTER".equals(reportType)) {
            sql = "SELECT CONCAT(YEAR(CreatedAt), '-Q', DATEPART(QUARTER, CreatedAt)) AS PeriodLabel, "
                    + "SUM(TotalAmount) AS Revenue "
                    + "FROM Orders "
                    + "WHERE [Status] IN (" + REPORT_ORDER_STATUSES + ") "
                    + "GROUP BY YEAR(CreatedAt), DATEPART(QUARTER, CreatedAt) "
                    + "ORDER BY PeriodLabel DESC";
        } else {
            sql = "SELECT CONVERT(varchar(10), CreatedAt, 23) AS PeriodLabel, SUM(TotalAmount) AS Revenue "
                    + "FROM Orders "
                    + "WHERE [Status] IN (" + REPORT_ORDER_STATUSES + ") "
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
     * Retrieves high-level revenue KPIs for the Admin report dashboard.
     *
     * @return Map containing totalRevenue, totalOrders, averageOrderValue, and
     *         totalSoldQuantity
     * @throws SQLException if a database access error occurs
     */
    public Map<String, Object> getRevenueSummary() throws SQLException {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalRevenue", java.math.BigDecimal.ZERO);
        summary.put("totalOrders", 0);
        summary.put("averageOrderValue", java.math.BigDecimal.ZERO);
        summary.put("totalSoldQuantity", 0);

        String revenueSql = "SELECT "
                + "COALESCE(SUM(TotalAmount), 0) AS TotalRevenue, "
                + "COUNT(*) AS TotalOrders, "
                + "COALESCE(AVG(TotalAmount), 0) AS AverageOrderValue "
                + "FROM Orders "
                + "WHERE [Status] IN (" + REPORT_ORDER_STATUSES + ")";

        String soldQuantitySql = "SELECT COALESCE(SUM(oi.Quantity), 0) AS TotalSoldQuantity "
                + "FROM OrderItems oi "
                + "JOIN Orders o ON oi.OrderId = o.Id "
                + "WHERE o.[Status] IN (" + REPORT_ORDER_STATUSES + ")";

        try (Connection conn = getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(revenueSql);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    summary.put("totalRevenue", rs.getBigDecimal("TotalRevenue"));
                    summary.put("totalOrders", rs.getInt("TotalOrders"));
                    summary.put("averageOrderValue", rs.getBigDecimal("AverageOrderValue"));
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(soldQuantitySql);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    summary.put("totalSoldQuantity", rs.getInt("TotalSoldQuantity"));
                }
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver missing", e);
        }

        return summary;
    }

    /**
     * Retrieves best-selling product statistics for a selected day, month, or
     * quarter.
     * Joins Orders, OrderItems, and Products to aggregate total sales volume.
     *
     * @param type   the aggregation period ("DAY", "MONTH", "QUARTER")
     * @param period the selected period label from getRevenueReport()
     * @return List of Maps containing product SKU, title, and total quantity sold
     * @throws SQLException if a database access error occurs
     */
    public List<Map<String, Object>> getBestSellingReport(String type, String period) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        if (period == null || period.trim().isEmpty()) {
            return list;
        }

        String periodFilter = buildBestSellerPeriodFilter(type, "o.CreatedAt");

        String sql = "SELECT TOP 10 p.SKU AS sku, p.Title AS title, SUM(oi.Quantity) AS salesQuantity "
                + "FROM OrderItems oi "
                + "JOIN Orders o ON oi.OrderId = o.Id "
                + "JOIN Products p ON oi.ProductId = p.Id "
                + "WHERE o.[Status] IN (" + REPORT_ORDER_STATUSES + ") "
                + "AND " + periodFilter + " "
                + "GROUP BY p.SKU, p.Title "
                + "ORDER BY salesQuantity DESC";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, period.trim());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("sku", rs.getString("sku"));
                    item.put("title", rs.getString("title"));
                    item.put("salesQuantity", rs.getInt("salesQuantity"));
                    list.add(item);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver missing", e);
        }
        return list;
    }

    public List<Map<String, Object>> getBestSellingReport(String type) throws SQLException {
        return getBestSellingReport(type, null);
    }
}
