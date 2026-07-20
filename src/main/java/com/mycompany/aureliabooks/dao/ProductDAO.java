/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aureliabooks.dao;

import com.mycompany.aureliabooks.model.Book;
import com.mycompany.aureliabooks.model.Brand;
import com.mycompany.aureliabooks.model.Inventory;
import com.mycompany.aureliabooks.model.Product;
import com.mycompany.aureliabooks.model.Stationery;
import com.mycompany.aureliabooks.model.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Product & Book DAO class.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
public class ProductDAO extends BaseDAO {

    public List<Product> getallActiveProducts(int offset, int limit) {
        List<Product> products = new ArrayList<>();
        if (limit <= 0) {
            return products;
        }

        String sql = "SELECT "
                + "CASE WHEN b.[ProductId] IS NOT NULL THEN 1 ELSE 2 END AS [ProductOrder], "
                + "p.[Id], p.[CategoryId], p.[SupplierId], p.[Title], p.[Description], p.[Price], p.[Sku], p.[Image_URL], p.[IsActive], p.[CreatedAt], "
                + "b.[ProductId] AS [BookProductId], b.[PublisherId], b.[Translator], b.[PublicationYear], b.[NumberOfPages], b.[CoverType], b.[Language], b.[Weight] AS [BookWeight], b.[Dimensions] AS [BookDimensions], "
                + "s.[ProductId] AS [StationeryProductId], s.[BrandId], s.[Origin], s.[Material], s.[Color], s.[Weight] AS [StationeryWeight], s.[Dimensions] AS [StationeryDimensions], s.[Specifications], s.[Warning] "
                + "FROM [dbo].[Products] p "
                + "LEFT JOIN [dbo].[Books] b ON p.[Id] = b.[ProductId] "
                + "LEFT JOIN [dbo].[Stationeries] s ON p.[Id] = s.[ProductId] "
                + "WHERE p.[IsActive] = 1 "
                + "AND (b.[ProductId] IS NOT NULL OR s.[ProductId] IS NOT NULL) "
                + "ORDER BY [ProductOrder], p.[Id] "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Math.max(offset, 0));
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapPagedProduct(rs));
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return products;
    }

    private Product mapPagedProduct(ResultSet rs) throws SQLException {
        if (rs.getObject("BookProductId") != null) {
            Book book = new Book();
            book.setId(rs.getInt("Id"));
            book.setCategoryId(rs.getInt("CategoryId"));
            book.setSupplierId(rs.getInt("SupplierId"));
            if (rs.wasNull()) {
                book.setSupplierId(null);
            }
            book.setTitle(rs.getString("Title"));
            book.setDescription(rs.getString("Description"));
            book.setPrice(rs.getBigDecimal("Price"));
            book.setSku(rs.getString("Sku"));
            book.setImageUrl(rs.getString("Image_URL"));
            book.setIsActive(rs.getBoolean("IsActive"));
            book.setCreatedAt(rs.getTimestamp("CreatedAt"));

            book.setPublisherId(rs.getInt("PublisherId"));
            if (rs.wasNull()) {
                book.setPublisherId(null);
            }
            book.setTranslator(rs.getString("Translator"));
            book.setPublicationYear(rs.getInt("PublicationYear"));
            if (rs.wasNull()) {
                book.setPublicationYear(null);
            }
            book.setNumberOfPages(rs.getInt("NumberOfPages"));
            if (rs.wasNull()) {
                book.setNumberOfPages(null);
            }
            book.setCoverType(rs.getString("CoverType"));
            book.setLanguage(rs.getString("Language"));
            book.setWeight(rs.getBigDecimal("BookWeight"));
            book.setDimensions(rs.getString("BookDimensions"));
            return book;
        }

        Stationery stationery = new Stationery();
        stationery.setId(rs.getInt("Id"));
        stationery.setCategoryId(rs.getInt("CategoryId"));
        stationery.setSupplierId(rs.getInt("SupplierId"));
        if (rs.wasNull()) {
            stationery.setSupplierId(null);
        }
        stationery.setTitle(rs.getString("Title"));
        stationery.setDescription(rs.getString("Description"));
        stationery.setPrice(rs.getBigDecimal("Price"));
        stationery.setSku(rs.getString("Sku"));
        stationery.setImageUrl(rs.getString("Image_URL"));
        stationery.setIsActive(rs.getBoolean("IsActive"));
        stationery.setCreatedAt(rs.getTimestamp("CreatedAt"));

        stationery.setBrandId(rs.getInt("BrandId"));
        if (rs.wasNull()) {
            stationery.setBrandId(null);
        }
        stationery.setOrigin(rs.getString("Origin"));
        stationery.setMaterial(rs.getString("Material"));
        stationery.setColor(rs.getString("Color"));
        stationery.setWeight(rs.getBigDecimal("StationeryWeight"));
        stationery.setDimensions(rs.getString("StationeryDimensions"));
        stationery.setSpecifications(rs.getString("Specifications"));
        stationery.setWarning(rs.getString("Warning"));
        return stationery;
    }

    public int getProductCount() {
        int count = 0;

        // Count Books
        String sqlBooks = "SELECT COUNT(*) FROM [dbo].[Products] p JOIN [dbo].[Books] b ON p.[Id] = b.[ProductId] WHERE p.[IsActive] = 1";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlBooks);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                count += rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Count Stationeries
        String sqlStationeries = "SELECT COUNT(*) FROM [dbo].[Products] p JOIN [dbo].[Stationeries] s ON p.[Id] = s.[ProductId] WHERE p.[IsActive] = 1";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlStationeries);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                count += rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return count;
    }

    public Product getProductById(int id) {
        // 1. Search in Books table first and map properties
        String sqlBook = "SELECT p.*, b.* FROM [dbo].[Products] p JOIN [dbo].[Books] b ON p.[Id] = b.[ProductId] WHERE p.[Id] = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlBook)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Book book = new Book();
                    book.setId(rs.getInt("Id"));
                    book.setCategoryId(rs.getInt("CategoryId"));
                    book.setSupplierId(rs.getInt("SupplierId"));
                    if (rs.wasNull()) {
                        book.setSupplierId(null);
                    }
                    book.setTitle(rs.getString("Title"));
                    book.setDescription(rs.getString("Description"));
                    book.setPrice(rs.getBigDecimal("Price"));
                    book.setSku(rs.getString("Sku"));
                    book.setImageUrl(rs.getString("Image_URL"));
                    book.setIsActive(rs.getBoolean("IsActive"));
                    book.setCreatedAt(rs.getTimestamp("CreatedAt"));

                    book.setPublisherId(rs.getInt("PublisherId"));
                    if (rs.wasNull()) {
                        book.setPublisherId(null);
                    }
                    book.setTranslator(rs.getString("Translator"));
                    book.setPublicationYear(rs.getInt("PublicationYear"));
                    if (rs.wasNull()) {
                        book.setPublicationYear(null);
                    }
                    book.setNumberOfPages(rs.getInt("NumberOfPages"));
                    if (rs.wasNull()) {
                        book.setNumberOfPages(null);
                    }
                    book.setCoverType(rs.getString("CoverType"));
                    book.setLanguage(rs.getString("Language"));
                    book.setWeight(rs.getBigDecimal("Weight"));
                    book.setDimensions(rs.getString("Dimensions"));
                    book.setAuthorIds(getBookAuthorIds(id));
                    return book;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // 2. If not a book, search in Stationeries table and map properties
        String sqlStationery = "SELECT p.*, s.* FROM [dbo].[Products] p JOIN [dbo].[Stationeries] s ON p.[Id] = s.[ProductId] WHERE p.[Id] = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlStationery)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Stationery stationery = new Stationery();
                    stationery.setId(rs.getInt("Id"));
                    stationery.setCategoryId(rs.getInt("CategoryId"));
                    stationery.setSupplierId(rs.getInt("SupplierId"));
                    if (rs.wasNull()) {
                        stationery.setSupplierId(null);
                    }
                    stationery.setTitle(rs.getString("Title"));
                    stationery.setDescription(rs.getString("Description"));
                    stationery.setPrice(rs.getBigDecimal("Price"));
                    stationery.setSku(rs.getString("Sku"));
                    stationery.setImageUrl(rs.getString("Image_URL"));
                    stationery.setIsActive(rs.getBoolean("IsActive"));
                    stationery.setCreatedAt(rs.getTimestamp("CreatedAt"));

                    stationery.setBrandId(rs.getInt("BrandId"));
                    if (rs.wasNull()) {
                        stationery.setBrandId(null);
                    }
                    stationery.setOrigin(rs.getString("Origin"));
                    stationery.setMaterial(rs.getString("Material"));
                    stationery.setColor(rs.getString("Color"));
                    stationery.setWeight(rs.getBigDecimal("Weight"));
                    stationery.setDimensions(rs.getString("Dimensions"));
                    stationery.setSpecifications(rs.getString("Specifications"));
                    stationery.setWarning(rs.getString("Warning"));
                    return stationery;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Integer> getBookAuthorIds(int productId) {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT [AuthorId] FROM [dbo].[Contributor] WHERE [ProductId] = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("AuthorId"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getProductStock(int id) {
        String sql = "SELECT QuantityInStock FROM [dbo].[Inventory] WHERE [ProductId] = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("QuantityInStock");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0; // Default to 0 if not found or error
    }
    
    public HashMap<String, Object> getProductFullInformationById(int id) {
        HashMap<String, Object> map = new HashMap<>();

        // 1. Search in Books table first (JOIN with Publishers, Suppliers, Authors)
        String sqlBook = "SELECT p.*, b.*, pub.[Name] AS PublisherName, sup.[Name] AS SupplierName, "
                + "(SELECT TOP 1 a.[FullName] FROM [dbo].[Contributor] c JOIN [dbo].[Authors] a ON c.[AuthorId] = a.[AuthorId] WHERE c.[ProductId] = p.[Id]) AS AuthorName "
                + "FROM [dbo].[Products] p "
                + "JOIN [dbo].[Books] b ON p.[Id] = b.[ProductId] "
                + "LEFT JOIN [dbo].[Publishers] pub ON b.[PublisherId] = pub.[Id] "
                + "LEFT JOIN [dbo].[Suppliers] sup ON p.[SupplierId] = sup.[Id] "
                + "WHERE p.[Id] = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlBook)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    map.put("productType", "Book");
                    map.put("id", rs.getInt("Id"));
                    map.put("categoryId", rs.getInt("CategoryId"));
                    map.put("supplierId", rs.getObject("SupplierId"));
                    map.put("supplierName", rs.getString("SupplierName"));
                    map.put("title", rs.getString("Title"));
                    map.put("description", rs.getString("Description"));
                    map.put("price", rs.getBigDecimal("Price"));
                    map.put("sku", rs.getString("Sku"));
                    map.put("imageUrl", rs.getString("Image_URL"));
                    map.put("isActive", rs.getBoolean("IsActive"));
                    
                    map.put("publisherId", rs.getObject("PublisherId"));
                    map.put("publisherName", rs.getString("PublisherName"));
                    map.put("authorName", rs.getString("AuthorName"));
                    map.put("translator", rs.getString("Translator"));
                    map.put("publicationYear", rs.getObject("PublicationYear"));
                    map.put("numberOfPages", rs.getObject("NumberOfPages"));
                    map.put("coverType", rs.getString("CoverType"));
                    map.put("language", rs.getString("Language"));
                    map.put("weight", rs.getBigDecimal("Weight"));
                    map.put("dimensions", rs.getString("Dimensions"));
                    map.put("authorIds", getBookAuthorIds(id));
                    return map;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // 2. If not a book, search in Stationeries table (JOIN with Brands, Suppliers)
        String sqlStationery = "SELECT p.*, s.*, br.[Name] AS BrandName, sup.[Name] AS SupplierName "
                + "FROM [dbo].[Products] p "
                + "JOIN [dbo].[Stationeries] s ON p.[Id] = s.[ProductId] "
                + "LEFT JOIN [dbo].[Brands] br ON s.[BrandId] = br.[Id] "
                + "LEFT JOIN [dbo].[Suppliers] sup ON p.[SupplierId] = sup.[Id] "
                + "WHERE p.[Id] = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlStationery)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    map.put("productType", "Stationery");
                    map.put("id", rs.getInt("Id"));
                    map.put("categoryId", rs.getInt("CategoryId"));
                    map.put("supplierId", rs.getObject("SupplierId"));
                    map.put("supplierName", rs.getString("SupplierName"));
                    map.put("title", rs.getString("Title"));
                    map.put("description", rs.getString("Description"));
                    map.put("price", rs.getBigDecimal("Price"));
                    map.put("sku", rs.getString("Sku"));
                    map.put("imageUrl", rs.getString("Image_URL"));
                    map.put("isActive", rs.getBoolean("IsActive"));
                    
                    map.put("brandId", rs.getObject("BrandId"));
                    map.put("brandName", rs.getString("BrandName"));
                    map.put("origin", rs.getString("Origin"));
                    map.put("material", rs.getString("Material"));
                    map.put("color", rs.getString("Color"));
                    map.put("weight", rs.getBigDecimal("Weight"));
                    map.put("dimensions", rs.getString("Dimensions"));
                    map.put("specifications", rs.getString("Specifications"));
                    map.put("warning", rs.getString("Warning"));
                    return map;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Product> searchProducts(String query, int categoryId, int offset, int limit) {
        List<Product> products = new ArrayList<>();
        if (limit <= 0) {
            return products;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("CASE WHEN b.[ProductId] IS NOT NULL THEN 1 ELSE 2 END AS [ProductOrder], ");
        sql.append("p.[Id], p.[CategoryId], p.[SupplierId], p.[Title], p.[Description], p.[Price], p.[Sku], p.[Image_URL], p.[IsActive], p.[CreatedAt], ");
        sql.append("b.[ProductId] AS [BookProductId], b.[PublisherId], b.[Translator], b.[PublicationYear], b.[NumberOfPages], b.[CoverType], b.[Language], b.[Weight] AS [BookWeight], b.[Dimensions] AS [BookDimensions], ");
        sql.append("s.[ProductId] AS [StationeryProductId], s.[BrandId], s.[Origin], s.[Material], s.[Color], s.[Weight] AS [StationeryWeight], s.[Dimensions] AS [StationeryDimensions], s.[Specifications], s.[Warning] ");
        sql.append("FROM [dbo].[Products] p ");
        sql.append("LEFT JOIN [dbo].[Books] b ON p.[Id] = b.[ProductId] ");
        sql.append("LEFT JOIN [dbo].[Stationeries] s ON p.[Id] = s.[ProductId] ");
        sql.append("WHERE p.[IsActive] = 1 ");
        sql.append("AND (b.[ProductId] IS NOT NULL OR s.[ProductId] IS NOT NULL) ");
        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (p.[Title] LIKE ? OR p.[Description] LIKE ?) ");
        }
        if (categoryId > 0) {
            if (categoryId == 1) {
                sql.append("AND b.[Language] = N'Tiếng Việt' ");
            } else if (categoryId == 2) {
                sql.append("AND b.[Language] IS NOT NULL AND b.[Language] <> N'Tiếng Việt' ");
            } else {
                sql.append("AND (p.[CategoryId] = ? OR p.[CategoryId] IN (SELECT Id FROM Categories WHERE ParentId = ?)) ");
            }
        }
        sql.append("ORDER BY [ProductOrder], p.[Id] ");
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (query != null && !query.trim().isEmpty()) {
                String searchPattern = "%" + query.trim() + "%";
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
            }
            if (categoryId > 0 && categoryId != 1 && categoryId != 2) {
                stmt.setInt(paramIndex++, categoryId);
                stmt.setInt(paramIndex++, categoryId);
            }
            stmt.setInt(paramIndex++, Math.max(offset, 0));
            stmt.setInt(paramIndex, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapPagedProduct(rs));
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return products;
    }

    public int countSearchProducts(String query, int categoryId) {
        int count = 0;

        // Count Books matching search criteria
        StringBuilder sqlBooks = new StringBuilder("SELECT COUNT(*) FROM [dbo].[Products] p JOIN [dbo].[Books] b ON p.[Id] = b.[ProductId] WHERE p.[IsActive] = 1 ");
        if (query != null && !query.trim().isEmpty()) {
            sqlBooks.append("AND (p.[Title] LIKE ? OR p.[Description] LIKE ?) ");
        }
        if (categoryId > 0) {
            if (categoryId == 1) {
                sqlBooks.append("AND b.[Language] = N'Tiếng Việt' ");
            } else if (categoryId == 2) {
                sqlBooks.append("AND b.[Language] IS NOT NULL AND b.[Language] <> N'Tiếng Việt' ");
            } else {
                sqlBooks.append("AND (p.[CategoryId] = ? OR p.[CategoryId] IN (SELECT Id FROM Categories WHERE ParentId = ?)) ");
            }
        }
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlBooks.toString())) {
            int paramIndex = 1;
            if (query != null && !query.trim().isEmpty()) {
                String searchPattern = "%" + query.trim() + "%";
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
            }
            if (categoryId > 0 && categoryId != 1 && categoryId != 2) {
                stmt.setInt(paramIndex++, categoryId);
                stmt.setInt(paramIndex++, categoryId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    count += rs.getInt(1);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Count Stationeries matching search criteria
        StringBuilder sqlStationeries = new StringBuilder("SELECT COUNT(*) FROM [dbo].[Products] p JOIN [dbo].[Stationeries] s ON p.[Id] = s.[ProductId] WHERE p.[IsActive] = 1 ");
        if (query != null && !query.trim().isEmpty()) {
            sqlStationeries.append("AND (p.[Title] LIKE ? OR p.[Description] LIKE ?) ");
        }
        if (categoryId > 0) {
            if (categoryId == 1 || categoryId == 2) {
                sqlStationeries.append("AND 1 = 0 ");
            } else {
                sqlStationeries.append("AND (p.[CategoryId] = ? OR p.[CategoryId] IN (SELECT Id FROM Categories WHERE ParentId = ?)) ");
            }
        }
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlStationeries.toString())) {
            int paramIndex = 1;
            if (query != null && !query.trim().isEmpty()) {
                String searchPattern = "%" + query.trim() + "%";
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
            }
            if (categoryId > 0 && categoryId != 1 && categoryId != 2) {
                stmt.setInt(paramIndex++, categoryId);
                stmt.setInt(paramIndex++, categoryId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    count += rs.getInt(1);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return count;
    }

    public boolean insertBook(Book book) {
        String insertProductSQL = "INSERT INTO [dbo].[Products] ([CategoryId], [SupplierId], [Title], [Description], [Price], [Sku], [Image_URL], [IsActive]) " +
                                  "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String insertBookSQL = "INSERT INTO [dbo].[Books] ([ProductId], [PublisherId], [Translator], [PublicationYear], [NumberOfPages], [CoverType], [Language], [Weight], [Dimensions]) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmtProduct = null;
        PreparedStatement stmtBook = null;
        ResultSet generatedKeys = null;

        try {
            conn = this.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            stmtProduct = conn.prepareStatement(insertProductSQL, PreparedStatement.RETURN_GENERATED_KEYS);
            stmtProduct.setInt(1, book.getCategoryId());
            if (book.getSupplierId() != null) {
                stmtProduct.setInt(2, book.getSupplierId());
            } else {
                stmtProduct.setNull(2, java.sql.Types.INTEGER);
            }
            stmtProduct.setString(3, book.getTitle());
            stmtProduct.setString(4, book.getDescription());
            stmtProduct.setBigDecimal(5, book.getPrice());
            stmtProduct.setString(6, book.getSku());
            stmtProduct.setString(7, book.getImageUrl());
            stmtProduct.setBoolean(8, book.isIsActive());

            int affectedRows = stmtProduct.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();
                return false;
            }

            generatedKeys = stmtProduct.getGeneratedKeys();
            if (generatedKeys.next()) {
                int newProductId = generatedKeys.getInt(1);
                book.setId(newProductId);
            } else {
                conn.rollback();
                return false;
            }

            stmtBook = conn.prepareStatement(insertBookSQL);
            stmtBook.setInt(1, book.getId());
            if (book.getPublisherId() != null) {
                stmtBook.setInt(2, book.getPublisherId());
            } else {
                stmtBook.setNull(2, java.sql.Types.INTEGER);
            }
            stmtBook.setString(3, book.getTranslator());
            if (book.getPublicationYear() != null) {
                stmtBook.setInt(4, book.getPublicationYear());
            } else {
                stmtBook.setNull(4, java.sql.Types.INTEGER);
            }
            if (book.getNumberOfPages() != null) {
                stmtBook.setInt(5, book.getNumberOfPages());
            } else {
                stmtBook.setNull(5, java.sql.Types.INTEGER);
            }
            stmtBook.setString(6, book.getCoverType());
            stmtBook.setString(7, book.getLanguage());
            stmtBook.setBigDecimal(8, book.getWeight());
            stmtBook.setString(9, book.getDimensions());

            stmtBook.executeUpdate();

            // Insert Contributor associations
            if (book.getAuthorIds() != null && !book.getAuthorIds().isEmpty()) {
                String insertContributorSQL = "INSERT INTO [dbo].[Contributor] ([ProductId], [AuthorId]) VALUES (?, ?)";
                try (PreparedStatement stmtContrib = conn.prepareStatement(insertContributorSQL)) {
                    for (Integer authorId : book.getAuthorIds()) {
                        stmtContrib.setInt(1, book.getId());
                        stmtContrib.setInt(2, authorId);
                        stmtContrib.addBatch();
                    }
                    stmtContrib.executeBatch();
                }
            }

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (stmtProduct != null) stmtProduct.close();
                if (stmtBook != null) stmtBook.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean insertStationery(Stationery stationery) {
        String insertProductSQL = "INSERT INTO [dbo].[Products] ([CategoryId], [SupplierId], [Title], [Description], [Price], [Sku], [Image_URL], [IsActive]) " +
                                  "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String insertStationerySQL = "INSERT INTO [dbo].[Stationeries] ([ProductId], [BrandId], [Origin], [Material], [Color], [Weight], [Dimensions], [Specifications], [Warning]) " +
                                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmtProduct = null;
        PreparedStatement stmtStationery = null;
        ResultSet generatedKeys = null;

        try {
            conn = this.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            stmtProduct = conn.prepareStatement(insertProductSQL, PreparedStatement.RETURN_GENERATED_KEYS);
            stmtProduct.setInt(1, stationery.getCategoryId());
            if (stationery.getSupplierId() != null) {
                stmtProduct.setInt(2, stationery.getSupplierId());
            } else {
                stmtProduct.setNull(2, java.sql.Types.INTEGER);
            }
            stmtProduct.setString(3, stationery.getTitle());
            stmtProduct.setString(4, stationery.getDescription());
            stmtProduct.setBigDecimal(5, stationery.getPrice());
            stmtProduct.setString(6, stationery.getSku());
            stmtProduct.setString(7, stationery.getImageUrl());
            stmtProduct.setBoolean(8, stationery.isIsActive());

            int affectedRows = stmtProduct.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();
                return false;
            }

            generatedKeys = stmtProduct.getGeneratedKeys();
            if (generatedKeys.next()) {
                int newProductId = generatedKeys.getInt(1);
                stationery.setId(newProductId);
            } else {
                conn.rollback();
                return false;
            }

            stmtStationery = conn.prepareStatement(insertStationerySQL);
            stmtStationery.setInt(1, stationery.getId());
            if (stationery.getBrandId() != null) {
                stmtStationery.setInt(2, stationery.getBrandId());
            } else {
                stmtStationery.setNull(2, java.sql.Types.INTEGER);
            }
            stmtStationery.setString(3, stationery.getOrigin());
            stmtStationery.setString(4, stationery.getMaterial());
            stmtStationery.setString(5, stationery.getColor());
            stmtStationery.setBigDecimal(6, stationery.getWeight());
            stmtStationery.setString(7, stationery.getDimensions());
            stmtStationery.setString(8, stationery.getSpecifications());
            stmtStationery.setString(9, stationery.getWarning());

            stmtStationery.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (stmtProduct != null) stmtProduct.close();
                if (stmtStationery != null) stmtStationery.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean updateBook(Book book) {
        String updateProductSQL = "UPDATE [dbo].[Products] " +
                                  "SET [CategoryId] = ?, [SupplierId] = ?, [Title] = ?, [Description] = ?, [Price] = ?, [Sku] = ?, [Image_URL] = ?, [IsActive] = ? " +
                                  "WHERE [Id] = ?";
        String updateBookSQL = "UPDATE [dbo].[Books] " +
                               "SET [PublisherId] = ?, [Translator] = ?, [PublicationYear] = ?, [NumberOfPages] = ?, [CoverType] = ?, [Language] = ?, [Weight] = ?, [Dimensions] = ? " +
                               "WHERE [ProductId] = ?";

        Connection conn = null;
        PreparedStatement stmtProduct = null;
        PreparedStatement stmtBook = null;

        try {
            conn = this.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            stmtProduct = conn.prepareStatement(updateProductSQL);
            stmtProduct.setInt(1, book.getCategoryId());
            if (book.getSupplierId() != null) {
                stmtProduct.setInt(2, book.getSupplierId());
            } else {
                stmtProduct.setNull(2, java.sql.Types.INTEGER);
            }
            stmtProduct.setString(3, book.getTitle());
            stmtProduct.setString(4, book.getDescription());
            stmtProduct.setBigDecimal(5, book.getPrice());
            stmtProduct.setString(6, book.getSku());
            stmtProduct.setString(7, book.getImageUrl());
            stmtProduct.setBoolean(8, book.isIsActive());
            stmtProduct.setInt(9, book.getId());

            int affectedProduct = stmtProduct.executeUpdate();
            if (affectedProduct == 0) {
                conn.rollback();
                return false;
            }

            stmtBook = conn.prepareStatement(updateBookSQL);
            if (book.getPublisherId() != null) {
                stmtBook.setInt(1, book.getPublisherId());
            } else {
                stmtBook.setNull(1, java.sql.Types.INTEGER);
            }
            stmtBook.setString(2, book.getTranslator());
            if (book.getPublicationYear() != null) {
                stmtBook.setInt(3, book.getPublicationYear());
            } else {
                stmtBook.setNull(3, java.sql.Types.INTEGER);
            }
            if (book.getNumberOfPages() != null) {
                stmtBook.setInt(4, book.getNumberOfPages());
            } else {
                stmtBook.setNull(4, java.sql.Types.INTEGER);
            }
            stmtBook.setString(5, book.getCoverType());
            stmtBook.setString(6, book.getLanguage());
            stmtBook.setBigDecimal(7, book.getWeight());
            stmtBook.setString(8, book.getDimensions());
            stmtBook.setInt(9, book.getId());

            stmtBook.executeUpdate();

            // Delete existing contributor associations
            String deleteContributorSQL = "DELETE FROM [dbo].[Contributor] WHERE [ProductId] = ?";
            try (PreparedStatement stmtDelContrib = conn.prepareStatement(deleteContributorSQL)) {
                stmtDelContrib.setInt(1, book.getId());
                stmtDelContrib.executeUpdate();
            }

            // Insert new contributor associations
            if (book.getAuthorIds() != null && !book.getAuthorIds().isEmpty()) {
                String insertContributorSQL = "INSERT INTO [dbo].[Contributor] ([ProductId], [AuthorId]) VALUES (?, ?)";
                try (PreparedStatement stmtContrib = conn.prepareStatement(insertContributorSQL)) {
                    for (Integer authorId : book.getAuthorIds()) {
                        stmtContrib.setInt(1, book.getId());
                        stmtContrib.setInt(2, authorId);
                        stmtContrib.addBatch();
                    }
                    stmtContrib.executeBatch();
                }
            }

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmtProduct != null) stmtProduct.close();
                if (stmtBook != null) stmtBook.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean updateStationery(Stationery stationery) {
        String updateProductSQL = "UPDATE [dbo].[Products] " +
                                  "SET [CategoryId] = ?, [SupplierId] = ?, [Title] = ?, [Description] = ?, [Price] = ?, [Sku] = ?, [Image_URL] = ?, [IsActive] = ? " +
                                  "WHERE [Id] = ?";
        String updateStationerySQL = "UPDATE [dbo].[Stationeries] " +
                                     "SET [BrandId] = ?, [Origin] = ?, [Material] = ?, [Color] = ?, [Weight] = ?, [Dimensions] = ?, [Specifications] = ?, [Warning] = ? " +
                                     "WHERE [ProductId] = ?";

        Connection conn = null;
        PreparedStatement stmtProduct = null;
        PreparedStatement stmtStationery = null;

        try {
            conn = this.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            stmtProduct = conn.prepareStatement(updateProductSQL);
            stmtProduct.setInt(1, stationery.getCategoryId());
            if (stationery.getSupplierId() != null) {
                stmtProduct.setInt(2, stationery.getSupplierId());
            } else {
                stmtProduct.setNull(2, java.sql.Types.INTEGER);
            }
            stmtProduct.setString(3, stationery.getTitle());
            stmtProduct.setString(4, stationery.getDescription());
            stmtProduct.setBigDecimal(5, stationery.getPrice());
            stmtProduct.setString(6, stationery.getSku());
            stmtProduct.setString(7, stationery.getImageUrl());
            stmtProduct.setBoolean(8, stationery.isIsActive());
            stmtProduct.setInt(9, stationery.getId());

            int affectedProduct = stmtProduct.executeUpdate();
            if (affectedProduct == 0) {
                conn.rollback();
                return false;
            }

            stmtStationery = conn.prepareStatement(updateStationerySQL);
            if (stationery.getBrandId() != null) {
                stmtStationery.setInt(1, stationery.getBrandId());
            } else {
                stmtStationery.setNull(1, java.sql.Types.INTEGER);
            }
            stmtStationery.setString(2, stationery.getOrigin());
            stmtStationery.setString(3, stationery.getMaterial());
            stmtStationery.setString(4, stationery.getColor());
            stmtStationery.setBigDecimal(5, stationery.getWeight());
            stmtStationery.setString(6, stationery.getDimensions());
            stmtStationery.setString(7, stationery.getSpecifications());
            stmtStationery.setString(8, stationery.getWarning());
            stmtStationery.setInt(9, stationery.getId());

            stmtStationery.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmtProduct != null) stmtProduct.close();
                if (stmtStationery != null) stmtStationery.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean deleteProduct(int id) {
        String sql = "UPDATE [dbo].[Products] SET [IsActive] = 0 WHERE [Id] = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns all products (active + inactive) joined with Inventory for admin
     * list management. Sets quantityInStock on each returned Product.
     */
    public List<Product> getAdminProductList(int offset, int limit) {
        List<Product> products = new ArrayList<>();
        if (limit <= 0) return products;

        String sql = "SELECT p.[Id], p.[CategoryId], p.[SupplierId], p.[Title], p.[Description], "
                + "p.[Price], p.[Sku], p.[Image_URL], p.[IsActive], p.[CreatedAt], "
                + "b.[ProductId] AS [BookProductId], b.[PublisherId], b.[Translator], "
                + "b.[PublicationYear], b.[NumberOfPages], b.[CoverType], b.[Language], "
                + "b.[Weight] AS [BookWeight], b.[Dimensions] AS [BookDimensions], "
                + "s.[ProductId] AS [StationeryProductId], s.[BrandId], s.[Origin], "
                + "s.[Material], s.[Color], s.[Weight] AS [StationeryWeight], "
                + "s.[Dimensions] AS [StationeryDimensions], s.[Specifications], s.[Warning], "
                + "ISNULL(inv.[QuantityInStock], 0) AS [QuantityInStock] "
                + "FROM [dbo].[Products] p "
                + "LEFT JOIN [dbo].[Books] b ON p.[Id] = b.[ProductId] "
                + "LEFT JOIN [dbo].[Stationeries] s ON p.[Id] = s.[ProductId] "
                + "LEFT JOIN [dbo].[Inventory] inv ON p.[Id] = inv.[ProductId] "
                + "ORDER BY p.[CreatedAt] DESC, p.[Id] DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Math.max(offset, 0));
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product p = mapPagedProduct(rs);
                    p.setQuantityInStock(rs.getInt("QuantityInStock"));
                    products.add(p);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return products;
    }

    /**
     * Counts total products (active + inactive) for admin pagination.
     */
    public int countAdminProducts() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM [dbo].[Products]";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Returns all publishers for dropdown in create/update forms.
     */
    public List<com.mycompany.aureliabooks.model.Publisher> getAllPublishers() {
        List<com.mycompany.aureliabooks.model.Publisher> publishers = new ArrayList<>();
        String sql = "SELECT [Id], [Name], [Address] FROM [dbo].[Publishers] ORDER BY [Name]";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                com.mycompany.aureliabooks.model.Publisher pub = new com.mycompany.aureliabooks.model.Publisher();
                pub.setId(rs.getInt("Id"));
                pub.setName(rs.getString("Name"));
                pub.setAddress(rs.getString("Address"));
                publishers.add(pub);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return publishers;
    }

    /**
     * Returns all brands for dropdown in create/update forms.
     */
    public List<Brand> getAllBrands() {
        List<Brand> brands = new ArrayList<>();
        String sql = "SELECT [Id], [Name] FROM [dbo].[Brands] ORDER BY [Name]";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Brand brand = new Brand();
                brand.setId(rs.getInt("Id"));
                brand.setName(rs.getString("Name"));
                brands.add(brand);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return brands;
    }

    /**
     * Returns all suppliers for dropdown in create/update forms.
     */
    public List<Supplier> getAllSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT [Id], [Name] FROM [dbo].[Suppliers] ORDER BY [Name]";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Supplier sup = new Supplier();
                sup.setId(rs.getInt("Id"));
                sup.setName(rs.getString("Name"));
                suppliers.add(sup);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return suppliers;
    }


    
    public List<HashMap<String, Object>> getTopSellingBooks(int categoryId, int offset, int limit) {
    List<HashMap<String, Object>> list = new ArrayList<>();
    
    // SQL query joining tables: Products, Books, Publishers, OrderItems and Orders
    // Using OFFSET/FETCH NEXT for pagination
    String sql = "SELECT "
            + "    p.[Id], p.[Title], p.[Price], p.[Image_URL], p.[Description], "
            + "    b.[Language], b.[CoverType], b.[Dimensions], b.[Weight], b.[NumberOfPages], b.[PublicationYear], "
            + "    pub.[Name] AS PublisherName, "
            + "    (SELECT TOP 1 a.[FullName] "
            + "     FROM [dbo].[Contributor] c "
            + "     JOIN [dbo].[Authors] a ON c.[AuthorId] = a.[AuthorId] "
            + "     WHERE c.[ProductId] = p.[Id]) AS AuthorName, "
            + "    COALESCE(SUM(oi.[Quantity]), 0) AS SoldCount "
            + "FROM [dbo].[Products] p "
            + "JOIN [dbo].[Books] b ON p.[Id] = b.[ProductId] "
            + "LEFT JOIN [dbo].[Publishers] pub ON b.[PublisherId] = pub.[Id] "
            + "LEFT JOIN [dbo].[OrderItems] oi ON p.[Id] = oi.[ProductId] "
            + "LEFT JOIN [dbo].[Orders] o ON oi.[OrderId] = o.[Id] AND o.[Status] = 'COMPLETED' AND o.[CreatedAt] >= DATEADD(day, -7, GETDATE()) "
            + "WHERE p.[IsActive] = 1 AND p.[CategoryId] = ? "
            + "GROUP BY p.[Id], p.[Title], p.[Price], p.[Image_URL], p.[Description], b.[Language], b.[CoverType], b.[Dimensions], b.[Weight], b.[NumberOfPages], b.[PublicationYear], pub.[Name] "
            + "ORDER BY SoldCount DESC, p.[Id] DESC "
            + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    try (Connection conn = this.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, categoryId);
        stmt.setInt(2, offset);
        stmt.setInt(3, limit);
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", rs.getInt("Id"));
                map.put("title", rs.getString("Title"));
                map.put("price", rs.getBigDecimal("Price"));
                map.put("imageUrl", rs.getString("Image_URL"));
                map.put("description", rs.getString("Description"));
                map.put("language", rs.getString("Language"));
                map.put("coverType", rs.getString("CoverType"));
                map.put("dimensions", rs.getString("Dimensions"));
                map.put("weight", rs.getBigDecimal("Weight"));
                map.put("numberOfPages", rs.getInt("NumberOfPages"));
                map.put("publicationYear", rs.getInt("PublicationYear"));
                map.put("publisherName", rs.getString("PublisherName"));
                map.put("authorName", rs.getString("AuthorName"));
                map.put("soldCount", rs.getInt("SoldCount"));
                list.add(map);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}

    /**
     * Count total top selling books by category (used for pagination).
     */
    public int countTopSellingBooks(int categoryId) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM [dbo].[Products] p "
                + "JOIN [dbo].[Books] b ON p.[Id] = b.[ProductId] "
                + "WHERE p.[IsActive] = 1 AND p.[CategoryId] = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public List<HashMap<String, Object>> getTopSellingProductsOfMonth(int offset, int limit) {
        List<HashMap<String, Object>> list = new ArrayList<>();
        
        String sql = "SELECT "
                + "    p.[Id], p.[Title], p.[Price], p.[Image_URL], p.[Description], "
                + "    COALESCE(SUM(oi.[Quantity]), 0) AS SoldCount "
                + "FROM [dbo].[Products] p "
                + "LEFT JOIN [dbo].[OrderItems] oi ON p.[Id] = oi.[ProductId] "
                + "LEFT JOIN [dbo].[Orders] o ON oi.[OrderId] = o.[Id] AND o.[Status] = 'COMPLETED' AND o.[CreatedAt] >= DATEADD(month, -1, GETDATE()) "
                + "WHERE p.[IsActive] = 1 "
                + "GROUP BY p.[Id], p.[Title], p.[Price], p.[Image_URL], p.[Description] "
                + "ORDER BY SoldCount DESC, p.[Id] DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, offset);
            stmt.setInt(2, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", rs.getInt("Id"));
                    map.put("title", rs.getString("Title"));
                    map.put("price", rs.getBigDecimal("Price"));
                    map.put("imageUrl", rs.getString("Image_URL"));
                    map.put("description", rs.getString("Description"));
                    map.put("soldCount", rs.getInt("SoldCount"));
                    list.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countTopSellingProductsOfMonth() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM [dbo].[Products] WHERE [IsActive] = 1";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public List<Inventory> getInventoryList() {
        List<Inventory> list = new ArrayList<>();
        String sql = "SELECT p.[Id], p.[Title], p.[Sku], p.[Image_URL], "
                + "ISNULL(inv.[QuantityInStock], 0) AS [QuantityInStock], "
                + "inv.[WarehouseLocation] "
                + "FROM [dbo].[Products] p "
                + "LEFT JOIN [dbo].[Inventory] inv ON p.[Id] = inv.[ProductId] "
                + "ORDER BY p.[Id]";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                com.mycompany.aureliabooks.model.Inventory item = new com.mycompany.aureliabooks.model.Inventory();
                item.setProductId(rs.getInt("Id"));
                item.setProductTitle(rs.getString("Title"));
                item.setSku(rs.getString("Sku"));
                item.setImageUrl(rs.getString("Image_URL"));
                item.setQuantityInStock(rs.getInt("QuantityInStock"));
                item.setWarehouseLocation(rs.getString("WarehouseLocation"));
                list.add(item);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean adjustInventory(int productId, int quantityChange, String warehouseLocation) {
        // Verify current stock level
        String selectSql = "SELECT [QuantityInStock] FROM [dbo].[Inventory] WHERE [ProductId] = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setInt(1, productId);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    // Record exists -> UPDATE
                    int currentStock = rs.getInt("QuantityInStock");
                    int newStock = currentStock + quantityChange;
                    if (newStock < 0) return false;

                    String updateSql = "UPDATE [dbo].[Inventory] SET [QuantityInStock] = ?, "
                            + "[WarehouseLocation] = ?, [LastUpdated] = GETDATE() "
                            + "WHERE [ProductId] = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, newStock);
                        updateStmt.setString(2, warehouseLocation);
                        updateStmt.setInt(3, productId);
                        updateStmt.executeUpdate();
                    }
                } else {
                    // No record exists -> INSERT
                    if (quantityChange < 0) return false;

                    String insertSql = "INSERT INTO [dbo].[Inventory] ([ProductId], [QuantityInStock], "
                            + "[WarehouseLocation], [LastUpdated]) VALUES (?, ?, ?, GETDATE())";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, productId);
                        insertStmt.setInt(2, quantityChange);
                        insertStmt.setString(3, warehouseLocation);
                        insertStmt.executeUpdate();
                    }
                }
            }
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}