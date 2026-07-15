package com.mycompany.aureliabooks.dao;

import com.mycompany.aureliabooks.model.Author;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author DAO class.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
public class AuthorDAO extends BaseDAO {

    private Author mapAuthor(ResultSet rs) throws SQLException {
        Author author = new Author();
        author.setAuthorId(rs.getInt("AuthorId"));
        author.setFullName(rs.getString("FullName"));
        author.setBiography(rs.getString("Biography"));
        author.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return author;
    }

    public List<Author> getAllAuthors() {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT AuthorId, FullName, Biography, CreatedAt FROM Authors ORDER BY FullName";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                authors.add(mapAuthor(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return authors;
    }

    public Author getAuthorById(int authorId) {
        String sql = "SELECT AuthorId, FullName, Biography, CreatedAt FROM Authors WHERE AuthorId = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, authorId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAuthor(rs);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertAuthor(Author author) {
        String sql = "INSERT INTO Authors (FullName, Biography) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, author.getFullName());
            ps.setString(2, author.getBiography());

            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateAuthor(Author author) {
        String sql = "UPDATE Authors SET FullName = ?, Biography = ? WHERE AuthorId = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, author.getFullName());
            ps.setString(2, author.getBiography());
            ps.setInt(3, author.getAuthorId());

            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAuthor(int authorId) {
        String sql = "DELETE FROM Authors WHERE AuthorId = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, authorId);
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isReferencedByBook(int authorId) {
        String sql = "SELECT 1 FROM Books WHERE AuthorId = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, authorId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
