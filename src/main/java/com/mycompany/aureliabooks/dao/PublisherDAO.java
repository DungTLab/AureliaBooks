package com.mycompany.aureliabooks.dao;

import com.mycompany.aureliabooks.model.Publisher;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Publisher DAO class.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
public class PublisherDAO extends BaseDAO {

    private Publisher mapPublisher(ResultSet rs) throws SQLException {
        Publisher publisher = new Publisher();
        publisher.setId(rs.getInt("Id"));
        publisher.setName(rs.getString("Name"));
        publisher.setAddress(rs.getString("Address"));
        publisher.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return publisher;
    }

    public List<Publisher> getAllPublishers() {
        List<Publisher> publishers = new ArrayList<>();
        String sql = "SELECT Id, Name, Address, CreatedAt FROM Publishers ORDER BY Name";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                publishers.add(mapPublisher(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return publishers;
    }

    public Publisher getPublisherById(int id) {
        String sql = "SELECT Id, Name, Address, CreatedAt FROM Publishers WHERE Id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPublisher(rs);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertPublisher(Publisher publisher) {
        String sql = "INSERT INTO Publishers (Name, Address) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, publisher.getName());
            ps.setString(2, publisher.getAddress());

            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePublisher(Publisher publisher) {
        String sql = "UPDATE Publishers SET Name = ?, Address = ? WHERE Id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, publisher.getName());
            ps.setString(2, publisher.getAddress());
            ps.setInt(3, publisher.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deletePublisher(int id) {
        String sql = "DELETE FROM Publishers WHERE Id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isReferencedByBook(int id) {
        String sql = "SELECT 1 FROM Books WHERE PublisherId = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
