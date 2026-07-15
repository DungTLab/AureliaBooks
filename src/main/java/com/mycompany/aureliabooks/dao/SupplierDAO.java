package com.mycompany.aureliabooks.dao;

import com.mycompany.aureliabooks.model.Supplier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access methods for suppliers.
 */
public class SupplierDAO extends BaseDAO {

    private Supplier mapSupplier(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setId(rs.getInt("Id"));
        supplier.setName(rs.getString("Name"));
        supplier.setContactEmail(rs.getString("ContactEmail"));
        supplier.setContactPhone(rs.getString("ContactPhone"));
        supplier.setAddress(rs.getString("Address"));
        supplier.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return supplier;
    }

    public List<Supplier> getAllSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT Id, Name, ContactEmail, ContactPhone, Address, CreatedAt "
                + "FROM Suppliers ORDER BY Name";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                suppliers.add(mapSupplier(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    public Supplier getSupplierById(int id) {
        String sql = "SELECT Id, Name, ContactEmail, ContactPhone, Address, CreatedAt "
                + "FROM Suppliers WHERE Id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSupplier(rs);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertSupplier(Supplier supplier) {
        String sql = "INSERT INTO Suppliers (Name, ContactEmail, ContactPhone, Address) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setSupplierFields(ps, supplier);
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateSupplier(Supplier supplier) {
        String sql = "UPDATE Suppliers SET Name = ?, ContactEmail = ?, ContactPhone = ?, Address = ? "
                + "WHERE Id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setSupplierFields(ps, supplier);
            ps.setInt(5, supplier.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteSupplier(int id) {
        String sql = "DELETE FROM Suppliers WHERE Id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isReferencedByProducts(int id) {
        String sql = "SELECT 1 FROM Products WHERE SupplierId = ?";

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

    private void setSupplierFields(PreparedStatement ps, Supplier supplier) throws SQLException {
        ps.setString(1, supplier.getName());
        ps.setString(2, supplier.getContactEmail());
        ps.setString(3, supplier.getContactPhone());
        ps.setString(4, supplier.getAddress());
    }
}
