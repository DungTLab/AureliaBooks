/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aureliabooks.dao;

import com.mycompany.aureliabooks.model.SupportRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object (DA0) for Support Request persistence and management.
 * Handles all database operations related to customer support tickets,
 * including retrieval, updates, and creation
 *
 * DESIGN HIGHLIGHTS: - Exception Propagation: Throes SQLException to the
 * Controller layer. - SQL Injection Prevention: User Parameterized Queries
 * (PreparedStatement). - Code Reusability: Utilizes an centralized ResultSet
 * mapping utility.
 */
public class SupportRequestDAO extends BaseDAO {

    // Enterprise Standard: Centralized logger for debugging
    private static final Logger LOGGER = Logger.getLogger(SupportRequestDAO.class.getName());

    /**
     * Utility method to map a database row (ResultSet) to a SupportRequest
     * entity. Prevent code duplication across multiple SELECT query methods.
     *
     * @param rs the ResultSet positioned at the current row
     * @return SupportRequest object populated with database fields
     * @throws SQL Exception if a database access error occurs or column is
     * missing
     */
    private SupportRequest mapResultSetToSupportRequest(ResultSet rs) throws SQLException {
        SupportRequest sq = new SupportRequest();
        sq.setId(rs.getInt("Id"));
        sq.setUserId(rs.getInt("UserId"));

        // Carefully handle nullable integer relationship
        sq.setHandledByUserId((Integer) rs.getObject("HandledByUserId"));

        sq.setSubject(rs.getString("Subject"));
        sq.setMessage(rs.getString("Message"));
        sq.setReplyMessage(rs.getString("ReplyMessage"));
        sq.setStatus(rs.getString("Status"));
        sq.setCreatedAt(rs.getTimestamp("CreatedAt"));

        return sq;
    }

    /**
     * Insert a new support request into the database. The default statys for a
     * newly created request is 'OPEN'.
     *
     * @param request the SupportRequest object containing ticket details
     * @return true if the insertion is successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean insertSupportRequest(SupportRequest request) throws SQLException {
        String sql = "INSERT INTO SupportRequests (UserId, Subject, Message, Status) VALUES (?, ?, ?, 'OPEN')";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            // Map model properties to SQL parameters
            ps.setInt(1, request.getUserId());
            ps.setString(2, request.getSubject());
            ps.setString(3, request.getMessage());

            // Execute the update and verify rows affected
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Database driver not found while inserting support request", e
            );
            throw new SQLException("Database drive missing in classpath", e);
        }
    }

    /**
     * Retrieves all support requests associated with a spectific user. The list
     * is sorted by creation data in descending order (newest first).
     *
     * @param userId the ID of the user whose tickets are being retrieved
     * @return a List of SupportRequest objects for the requested user
     * @throws SQLException if a database access error occurs
     */
    public List<SupportRequest> getSupportRequestsByUserId(int userId) throws SQLException {
        List<SupportRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM SupportRequests WHERE UserId = ? ORDER BY CreatedAt DESC";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                // Iterate through the result set and map data to objects
                while (rs.next()) {
                    list.add(mapResultSetToSupportRequest(rs));
                }
            }

        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Database driver not found while retrieving user support requests", e
            );
            throw new SQLException("Database drive missing in classpath", e);
        }

        return list;
    }

    /**
     * Retriveves all support request in the system.
     *
     * @return a list of all SupportRequest objects
     * @throws SQLException if a database access error occurs
     */
    public List<SupportRequest> getAllSupportRequests() {
        // Empty skeleton for Sprint 3 (To be implemented by Dev 2)
        return null;
    }

    /**
     * Retrieves a specific support request by its unique ID.
     *
     * @param id the unique indentifier of the support request
     * @return the SupportRequest object if found, null otherwise
     * @throws SQLException if a database access error occurs
     */
    public SupportRequest getSupportRequestById(int id) throws SQLException {
        String sql = "SELECT * FROM SupportRequests WHERE Id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                // Return the maped object if a record is found
                if (rs.next()) {
                    return mapResultSetToSupportRequest(rs);
                }
            }

        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Database driver not found while retrieving support request by ID", e);
            throw new SQLException("Database driver missing in classpath", e);
        }

        return null;
    }

    /**
     * Updates a support request with a reply message and assigns the handler.
     *
     * @param id the unique identifier of the support request
     * @param replyMessage the response message from the admin/staff
     * @param handledByUserId the ID of the admin/staff handling the request
     * @return true if the reply was successfully update, false otherwise
     * @throws SQLException if an database error occurs or transaction fails
     */
    public boolean replySupportRequest(int id, String replyMessage, int handledByUserId) {
        // Empty skeleton for Sprint 3 (To be implemented by Dev 2)
        return false;
    }
}
