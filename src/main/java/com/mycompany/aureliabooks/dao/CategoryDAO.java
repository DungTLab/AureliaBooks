/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aureliabooks.dao;

import com.mycompany.aureliabooks.model.Category;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Category DAO class.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
public class CategoryDAO extends BaseDAO {
    
    // Helper method to reusing
    private Category mapCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getInt("Id"));
        
        int parentId = rs.getInt("ParentId");
        
        if(rs.wasNull()){
            category.setParentId(null);
        } else {
            category.setParentId(parentId);
        }
        category.setName(rs.getString("Name"));
        return category;
    }
    
    private void setNullableParentId(PreparedStatement ps, int index, Integer parentId)
            throws SQLException {
        if (parentId == null) {
            ps.setNull(index, java.sql.Types.INTEGER);
        } else {
            ps.setInt(index, parentId);
        }
    }
    
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT Id, ParentId, Name FROM Categories ORDER BY ParentId, Name";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categories.add(mapCategory(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return categories;
    }

    public Category getCategoryById(int id) {
        // Empty skeleton for Sprint 1 (To be implemented by Dev 2)
        String sql = "SELECT Id, ParentId, Name FROM Categories WHERE Id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCategory(rs);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertCategory(Category category) {
        String sql = "INSERT INTO Categories (ParentId, Name) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setNullableParentId(ps, 1, category.getParentId());
            ps.setString(2, category.getName());

            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateCategory(Category category) {
        String sql = "UPDATE Categories SET ParentId = ?, Name = ? WHERE Id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setNullableParentId(ps, 1, category.getParentId());
            ps.setString(2, category.getName());
            ps.setInt(3, category.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteCategory(int id) {
        String sql = "DELETE FROM Categories WHERE Id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    //Kiem tra co danh muc con hay ko
    public boolean hasChildCategory(int id){
        String sql = "SELECT 1 FROM Categories WHERE Id = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()){
                return rs.next();
            } 
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
    //kiem tra co san pham hay ko
    public boolean hasProducts(int id) {
    String sql = "SELECT 1 FROM Products WHERE CategoryId = ?";

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
