/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.ProductDAO;
import com.mycompany.aureliabooks.model.Inventory;
import com.mycompany.aureliabooks.model.Product;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Admin Inventory Management Controller.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
@WebServlet(name = "AdminInventoryController", urlPatterns = {"/admin/inventory"})
public class AdminInventoryController extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String view = request.getParameter("view");

        if (view == null || view.equals("list")) {
            // Load danh sách toàn bộ inventory để hiển thị
            request.setAttribute("inventoryList", productDAO.getInventoryList());
            request.getRequestDispatcher("/WEB-INF/inventory/list.jsp").forward(request, response);

        } else if (view.equals("adjust")) {
            // Load thông tin sản phẩm và tồn kho hiện tại để hiển thị form điều chỉnh
            String productIdParam = request.getParameter("productId");
            if (productIdParam == null || productIdParam.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Thiếu mã sản phẩm.");
                request.setAttribute("inventoryList", productDAO.getInventoryList());
                request.getRequestDispatcher("/WEB-INF/inventory/list.jsp").forward(request, response);
                return;
            }

            try {
                int productId = Integer.parseInt(productIdParam);
                Product product = productDAO.getProductById(productId);
                if (product == null) {
                    request.setAttribute("errorMessage", "Không tìm thấy sản phẩm có ID = " + productId + ".");
                    request.setAttribute("inventoryList", productDAO.getInventoryList());
                    request.getRequestDispatcher("/WEB-INF/inventory/list.jsp").forward(request, response);
                    return;
                }
                // Lấy currentStock và warehouseLocation từ getInventoryList
                int currentStock = 0;
                String warehouseLocation = "";
                for (Inventory inv : productDAO.getInventoryList()) {
                    if (inv.getProductId() == productId) {
                        currentStock = inv.getQuantityInStock();
                        warehouseLocation = inv.getWarehouseLocation();
                        break;
                    }
                }
                request.setAttribute("product", product);
                request.setAttribute("currentStock", currentStock);
                request.setAttribute("warehouseLocation", warehouseLocation);
                request.getRequestDispatcher("/WEB-INF/inventory/adjust.jsp").forward(request, response);

            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Mã sản phẩm không hợp lệ.");
                request.setAttribute("inventoryList", productDAO.getInventoryList());
                request.getRequestDispatcher("/WEB-INF/inventory/list.jsp").forward(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String productIdParam = request.getParameter("productId");
        String quantityChangeParam = request.getParameter("quantityChange");
        String warehouseLocation = request.getParameter("warehouseLocation");

        // Validate productId
        if (productIdParam == null || productIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Thiếu mã sản phẩm.");
            request.setAttribute("inventoryList", productDAO.getInventoryList());
            request.getRequestDispatcher("/WEB-INF/inventory/list.jsp").forward(request, response);
            return;
        }

        // Validate quantityChange
        if (quantityChangeParam == null || quantityChangeParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập số lượng thay đổi.");
            // Load lại product để hiển thị form
            try {
                int productId = Integer.parseInt(productIdParam);
                Product product = productDAO.getProductById(productId);
                int currentStockVal = 0;
                String wlVal = "";
                for (Inventory inv : productDAO.getInventoryList()) {
                    if (inv.getProductId() == productId) {
                        currentStockVal = inv.getQuantityInStock();
                        wlVal = inv.getWarehouseLocation();
                        break;
                    }
                }
                request.setAttribute("product", product);
                request.setAttribute("currentStock", currentStockVal);
                request.setAttribute("warehouseLocation", wlVal);
            } catch (NumberFormatException ex) { /* bỏ qua */ }
            request.getRequestDispatcher("/WEB-INF/inventory/adjust.jsp").forward(request, response);
            return;
        }

        try {
            int productId = Integer.parseInt(productIdParam);
            int quantityChange = Integer.parseInt(quantityChangeParam.trim());

            if (quantityChange == 0) {
                request.setAttribute("errorMessage", "Số lượng thay đổi không được bằng 0.");
                Product product = productDAO.getProductById(productId);
                int currentStockVal2 = 0;
                String wlVal2 = "";
                for (Inventory inv : productDAO.getInventoryList()) {
                    if (inv.getProductId() == productId) {
                        currentStockVal2 = inv.getQuantityInStock();
                        wlVal2 = inv.getWarehouseLocation();
                        break;
                    }
                }
                request.setAttribute("product", product);
                request.setAttribute("currentStock", currentStockVal2);
                request.setAttribute("warehouseLocation", wlVal2);
                request.getRequestDispatcher("/WEB-INF/inventory/adjust.jsp").forward(request, response);
                return;
            }

            boolean ok = productDAO.adjustInventory(productId, quantityChange, warehouseLocation);

            if (ok) {
                request.setAttribute("successMessage", "Điều chỉnh tồn kho thành công!");
            } else {
                request.setAttribute("errorMessage", "Điều chỉnh thất bại. Số lượng tồn kho không được âm.");
            }
            request.setAttribute("inventoryList", productDAO.getInventoryList());
            request.getRequestDispatcher("/WEB-INF/inventory/list.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Mã sản phẩm hoặc số lượng không hợp lệ.");
            request.setAttribute("inventoryList", productDAO.getInventoryList());
            request.getRequestDispatcher("/WEB-INF/inventory/list.jsp").forward(request, response);
        }
    }
}
