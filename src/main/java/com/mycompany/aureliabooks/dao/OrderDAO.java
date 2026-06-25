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
     * Maps the current {@link ResultSet} row to an {@link Order} entity. Keeps
     * row-to-object conversion in one place so the query methods stay small.
     *
     * @param rs the ResultSet positioned at the current row
     * @return Order object with all fields populated from the ResultSet
     * @throws Exception if database column access fails
     */
    private Order mapResultSetToOrder(ResultSet rs) throws Exception {
        Order order = new Order();
        // Map all database columns to Order entity fields
        order.setId(rs.getInt("Id"));
        order.setUserId(rs.getInt("UserId"));
        // DiscountId is nullable, so cast to Integer wrapper
        order.setDiscountId((Integer) rs.getObject("DiscountId"));
        order.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        order.setStatus(rs.getString("Status"));
        order.setShippingAddress(rs.getString("ShippingAddress"));
        order.setContactPhone(rs.getString("ContactPhone"));
        // ProcessedByUserId is populated when admin processes the order
        order.setProcessedByUserId((Integer) rs.getInt("ProcessedByUserId"));
        order.setReturnReason(rs.getString("ReturnReason"));
        order.setCreatedAt(rs.getTimestamp("CreatedAt"));

        return order;
    }

    /**
     * Inserts a new order record into the Orders table.
     * 
     * @param order the Order object containing all order details (UserId, TotalAmount, Status, etc.)
     * @return true if insertion succeeded, false otherwise
     * 
     * TODO: Implement when checkout flow is integrated. Should:
     *   - INSERT new record with initial status (typically "PENDING")
     *   - Extract order items and insert into OrderItems table
     *   - Apply any active discounts if applicable
     *   - Return generated orderId for confirmation
     */
    public boolean insertOrder(Order order) {
        return false;
    }

    /**
     * Returns all orders placed by a specific user, sorted by creation date (newest first).
     * Used for displaying user's order history.
     *
     * @param userId the unique identifier of the user
     * @return list of Order objects for the specified user, or null if not implemented
     * 
     * TODO: Implement when user account history view is connected. Should:
     *   - Query Orders table WHERE UserId = ?
     *   - Include related OrderItems and Product details if needed
     *   - Sort by CreatedAt DESC to show most recent orders first
     */
    public List<Order> getOrdersByUserId(int userId) {
        return null;
    }

    /**
     * Returns every order in the system sorted by creation date (newest first).
     * Typically used for admin dashboard to view all orders.
     *
     * @return list of all Order objects in descending order by creation date
     */
    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        // SQL: Retrieve all orders sorted by creation date (newest first)
        // Uses [Status] with brackets because "Status" is a SQL reserved keyword
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
     * Returns all orders with a specific status, sorted by creation date (newest first).
     * Used for filtering orders by stage (e.g., PENDING, CONFIRMED, SHIPPED, DELIVERED).
     *
     * @param status the order status to filter by (e.g., "PENDING", "CONFIRMED", "SHIPPED")
     * @return list of Order objects matching the given status
     */
    public List<Order> getOrdersByStatus(String status) {
        List<Order> list = new ArrayList<>();
        // SQL: Filter orders by exact status match, sorted by newest first
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
     * Used for displaying detailed order information.
     *
     * @param orderId the unique identifier of the order
     * @return Order object if found, null otherwise
     * 
     * TODO: Implement to load full order details. Should:
     *   - Query Orders table WHERE Id = ?
     *   - Retrieve associated OrderItems and related Product data
     *   - Include discount information if applicable
     *   - Include staff member info who processed the order (if any)
     */
    public Order getOrderById(int orderId) {
        return null;
    }

    /**
     * Updates the status of an order and records which staff member processed it.
     * Used when admin changes order state (e.g., PENDING → CONFIRMED → SHIPPED).
     *
     * @param orderId the unique identifier of the order to update
     * @param status the new status value (e.g., "CONFIRMED", "SHIPPED", "DELIVERED")
     * @param processedByUserId the ID of the staff member making this change (may be null)
     * @return true if update succeeded (1+ rows affected), false otherwise
     */
    public boolean updateOrderStatus(int orderId, String status, Integer processedByUserId) {
        // SQL: Update status and record which admin processed this order
        String sql = "UPDATE Orders SET [Status] = ?, ProcessedByUserId = ? WHERE Id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            // Parameter 1: New status
            ps.setString(1, status);
            // Parameter 2: Admin/staff user ID (nullable for automated status changes)
            ps.setObject(2, processedByUserId);
            // Parameter 3: Order ID to identify which order to update
            ps.setInt(3, orderId);

            // Returns true if at least one row was affected
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Creates a return request for an order, allowing customers to request refunds.
     * Records the reason for the return.
     *
     * @param orderId the unique identifier of the order being returned
     * @param reason the customer's reason for requesting the return
     * @return true if return request was created successfully, false otherwise
     * 
     * TODO: Implement as a transactional operation that:
     *   - Creates a return record with orderId, reason, and current timestamp
     *   - Updates order status to "RETURN_REQUESTED"
     *   - Restores inventory (increase product quantities for items in this order)
     *   - Potentially holds refund processing for admin approval
     *   - Use transaction to ensure consistency if any step fails
     */
    public boolean requestOrderReturn(int orderId, String reason) {
        return false;
    }

    /**
     * Returns aggregated revenue data for the requested time period.
     * Used for financial reporting and business analytics.
     *
     * @param type the aggregation period - "DAY", "MONTH", or "QUARTER"
     * @return Map with period keys (e.g., "2026-01") mapped to total revenue as Double,
     *         or null if not yet implemented
     * 
     * TODO: Implement to aggregate revenue by specified period:
     *   - For type="DAY": Group by DATE, return revenue per day
     *   - For type="MONTH": Group by YEAR-MONTH, return revenue per month
     *   - For type="QUARTER": Group by YEAR-QUARTER, return revenue per quarter
     *   - Only include orders with confirmed/completed status
     *   - Apply any discounts to get accurate net revenue
     */
    public Map<String, Double> getRevenueReport(String type) {
        return null;
    }

    /**
     * Returns best-selling product statistics for the requested time period.
     * Each map contains product info and sales quantity/revenue metrics.
     *
     * @param type the aggregation period - "DAY", "MONTH", or "QUARTER"
     * @return List of Maps containing product ID, name, quantity sold, and total revenue,
     *         ordered by sales volume (highest first), or null if not implemented
     * 
     * TODO: Implement to find top-selling products:
     *   - Join Orders → OrderItems → Products to get sales data
     *   - Group by product and aggregate quantities sold
     *   - Filter by date range based on type parameter
     *   - Only include completed/delivered orders
     *   - Sort by quantity descending to show best sellers first
     *   - Return top 10-20 products for the report
     */
    public List<Map<String, Object>> getBestSellingReport(String type) {
        return null;
    }

    /**
     * Returns the total count of orders (optionally filtered by status).
     * Used for pagination calculations to determine total number of pages.
     *
     * @param status the order status filter ("ALL" for no filter, or specific status like "PENDING")
     * @return total number of orders matching the criteria
     */
    public int getTotalOrdersCount(String status) {
        // SQL: Count all orders, optionally filtered by status
        String sql = "SELECT COUNT(*) FROM Orders";

        // If status is specified and not "ALL", add WHERE clause to filter
        if (status != null && !status.equals("ALL")) {
            sql += " WHERE [Status] = ?";
        }

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            // Only bind parameter if filtering by status
            if (status != null && !status.equals("ALL")) {
                ps.setString(1, status);
            }

            try (ResultSet rs = ps.executeQuery()) {
                // COUNT(*) always returns one row with the count value
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Returns a paginated list of orders, optionally filtered by status.
     * Used for admin dashboard pagination to display orders in chunks.
     *
     * @param status the order status filter ("ALL" for no filter, or specific status like "PENDING")
     * @param offset the number of rows to skip (0-based index for first page)
     * @param pageSize the maximum number of orders to return per page
     * @return List of Order objects for the requested page
     */
    public List<Order> getOrdersPaged(String status, int offset, int pageSize) {
        List<Order> list = new ArrayList<>();

        // SQL: Retrieve orders with pagination support using OFFSET/FETCH
        String sql = "SELECT Id, UserId, DiscountId, TotalAmount, [Status], ShippingAddress, ContactPhone, ProcessedByUserId, ReturnReason, CreatedAt \n"
                + "FROM Orders ";

        // Only add WHERE clause if filtering by specific status (NOT "ALL")
        if (status != null && !status.equals("ALL")) {
            sql += "WHERE [Status] = ? ";
        }

        // SQL: Sort by newest first, then apply pagination (OFFSET/FETCH for SQL Server)
        sql += "ORDER BY CreatedAt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql);) {
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
