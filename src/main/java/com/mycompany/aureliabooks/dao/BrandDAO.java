package com.mycompany.aureliabooks.dao;

import com.mycompany.aureliabooks.model.Brand;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Brand DAO class.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
public class BrandDAO extends BaseDAO {

    private Brand mapBrand(ResultSet rs) throws SQLException {
        Brand brand = new Brand();
        brand.setId(rs.getInt("Id"));
        brand.setName(rs.getString("Name"));
        brand.setOriginCountry(rs.getString("OriginCountry"));
        brand.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return brand;
    }

    public List<Brand> getAllBrands() {
        List<Brand> brands = new ArrayList<>();
        String sql = "SELECT Id, Name, OriginCountry, CreatedAt FROM Brands ORDER BY Name";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                brands.add(mapBrand(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return brands;
    }

    public Brand getBrandById(int id) {
        String sql = "SELECT Id, Name, OriginCountry, CreatedAt FROM Brands WHERE Id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBrand(rs);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertBrand(Brand brand) {
        String sql = "INSERT INTO Brands (Name, OriginCountry) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, brand.getName());
            ps.setString(2, brand.getOriginCountry());

            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateBrand(Brand brand) {
        String sql = "UPDATE Brands SET Name = ?, OriginCountry = ? WHERE Id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, brand.getName());
            ps.setString(2, brand.getOriginCountry());
            ps.setInt(3, brand.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteBrand(int id) {
        String sql = "DELETE FROM Brands WHERE Id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isReferencedByStationery(int id) {
        String sql = "SELECT 1 FROM Stationeries WHERE BrandId = ?";

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
