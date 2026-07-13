/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aureliabooks.dao;

import com.mycompany.aureliabooks.model.CartItem;
import com.mycompany.aureliabooks.model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

/**
 * Data Access Object (DAO) for managing shopping cart operations.
 * Handles database interactions related to Cart, CartItems, and creating Orders.
 * 
 * @author ADMIN
 */
public class CartDAO extends BaseDAO {

    /**
     * Retrieves all cart items for a specific user.
     * Joins CartItems, Carts, and Products tables to return complete item details.
     * 
     * @param userId The ID of the user whose cart items are to be fetched.
     * @return A list of CartItem objects containing product details.
     */
    public List<CartItem> findAll(int userId) {
        List<CartItem> list = new ArrayList<>();
        String sql = "SELECT ci.Id, ci.CartId, ci.ProductId, ci.Quantity, ci.AddedAt, "
                + "p.Title, p.Price, p.Image_URL, p.Sku "
                + "FROM CartItems ci "
                + "JOIN Carts c ON ci.CartId = c.Id "
                + "JOIN Products p ON ci.ProductId = p.Id "
                + "WHERE c.UserId = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setId(rs.getInt("Id"));
                    item.setCartId(rs.getInt("CartId"));
                    item.setProductId(rs.getInt("ProductId"));
                    item.setQuantity(rs.getInt("Quantity"));
                    item.setAddedAt(rs.getTimestamp("AddedAt"));

                    Product product = new Product();
                    product.setId(rs.getInt("ProductId"));
                    product.setTitle(rs.getString("Title"));
                    product.setPrice(rs.getBigDecimal("Price"));
                    product.setImageUrl(rs.getString("Image_URL"));
                    product.setSku(rs.getString("Sku"));

                    item.setProduct(product);
                    list.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateQuantity(int itemId, int quantity, int userId) {
        String sql = "UPDATE CartItems SET Quantity = ? WHERE Id = ? AND CartId = (SELECT Id FROM Carts WHERE UserId = ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, itemId);
            ps.setInt(3, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCartIdByUserId(int userId) {
        String sqlSelect = "SELECT Id FROM Carts WHERE UserId = ?";
        String sqlInsert = "INSERT INTO Carts (UserId, CreatedAt) VALUES (?, CURRENT_TIMESTAMP)";
        
        try (Connection conn = getConnection()) {
            // Check if cart exists
            try (PreparedStatement psSelect = conn.prepareStatement(sqlSelect)) {
                psSelect.setInt(1, userId);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("Id");
                    }
                }
            }
            
            // Cart does not exist, insert a new one dynamically
            try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                psInsert.setInt(1, userId);
                int affectedRows = psInsert.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet rsKeys = psInsert.getGeneratedKeys()) {
                        if (rsKeys.next()) {
                            return rsKeys.getInt(1);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Adds a product to the user's cart or updates its quantity if it already exists.
     * 
     * @param cartId    The ID of the cart to add the item to.
     * @param productId The ID of the product being added.
     * @param quantity  The quantity of the product to add.
     */
    public void addItem(int cartId, int productId, int quantity) {
        String checkSql = "SELECT Id, Quantity FROM CartItems WHERE CartId = ? AND ProductId = ?";
        try (Connection conn = getConnection(); PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
            psCheck.setInt(1, cartId);
            psCheck.setInt(2, productId);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next()) {
                    int existingId = rs.getInt("Id");
                    int newQuantity = rs.getInt("Quantity") + quantity;
                    // Note: internal addition from addItem can use a simplified internal update
                    String sqlUpdate = "UPDATE CartItems SET Quantity = ? WHERE Id = ?";
                    try (PreparedStatement psUp = conn.prepareStatement(sqlUpdate)) {
                        psUp.setInt(1, newQuantity);
                        psUp.setInt(2, existingId);
                        psUp.executeUpdate();
                    }
                } else {
                    String insertSql = "INSERT INTO CartItems (CartId, ProductId, Quantity) VALUES (?, ?, ?)";
                    try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                        psInsert.setInt(1, cartId);
                        psInsert.setInt(2, productId);
                        psInsert.setInt(3, quantity);
                        psInsert.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteItem(int itemId, int userId) {
        String sql = "DELETE FROM CartItems WHERE Id = ? AND CartId = (SELECT Id FROM Carts WHERE UserId = ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getUserInfo(int userId) {
        String sql = "SELECT Phone, Address FROM UserProfiles WHERE UserId = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String phone = rs.getString("Phone");
                    String address = rs.getString("Address");
                    // Tránh trường hợp null
                    phone = (phone != null) ? phone : "";
                    address = (address != null) ? address : "";
                    return phone + "/" + address;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/";
    }
}
