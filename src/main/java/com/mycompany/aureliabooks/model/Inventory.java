/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aureliabooks.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Inventory stock model.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
public class Inventory implements Serializable {
    private int productId;
    private int quantityInStock;
    private String warehouseLocation;
    private Timestamp lastUpdated;

    public Inventory() {
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public String getWarehouseLocation() {
        return warehouseLocation;
    }

    public void setWarehouseLocation(String warehouseLocation) {
        this.warehouseLocation = warehouseLocation;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // -------------------------------------------------------------------------
    // Transient fields - populated by JOIN queries (không có cột trong DB)
    // -------------------------------------------------------------------------
    private String productTitle;
    private String sku;

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
