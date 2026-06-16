/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aureliabooks.dao;

import com.mycompany.aureliabooks.model.Book;
import com.mycompany.aureliabooks.model.Product;
import java.util.List;

/**
 * Product & Book DAO class.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
public class ProductDAO extends BaseDAO {

    public List<Product> getAllActiveProducts(int offset, int limit) {
        // Empty skeleton for Sprint 1 (To be implemented by Dev 3)
        return null;
    }

    public int getProductCount() {
        // Empty skeleton for Sprint 1 (To be implemented by Dev 3)
        return 0;
    }

    public Product getProductById(int id) {
        // Empty skeleton for Sprint 1 (To be implemented by Dev 3)
        return null;
    }

    public List<Product> searchProducts(String query, int categoryId, int offset, int limit) {
        // Empty skeleton for Sprint 1 (To be implemented by Dev 3)
        return null;
    }

    public int countSearchProducts(String query, int categoryId) {
        // Empty skeleton for Sprint 1 (To be implemented by Dev 3)
        return 0;
    }

    public boolean insertBook(Book book) {
        // Transaction based insert for TPT (Drafted in guides)
        return false;
    }

    public boolean updateBook(Book book) {
        // Empty skeleton for Sprint 1 (To be implemented by Dev 3)
        return false;
    }

    public boolean deleteProduct(int id) {
        // Empty skeleton for Sprint 1 (To be implemented by Dev 3)
        return false;
    }
}
