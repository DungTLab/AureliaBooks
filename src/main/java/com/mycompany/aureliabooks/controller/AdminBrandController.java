/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.BrandDAO;
import com.mycompany.aureliabooks.model.Brand;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Admin Brand CRUD Controller.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
@WebServlet(name = "AdminBrandController", urlPatterns = {"/admin/brands"})
public class AdminBrandController extends HttpServlet {

    private final BrandDAO brandDAO = new BrandDAO();

    private String validateBrand(String name, String originCountry) {
        if (name == null || name.trim().isEmpty()) {
            return "Tên thương hiệu không được để trống.";
        }
        String trimmedName = name.trim();
        if (!trimmedName.matches("^[\\p{L}0-9 \\-&()]+$")) {
            return "Tên thương hiệu chỉ được chứa chữ cái, số, khoảng trắng và các ký tự - & ( ).";
        }
        if (trimmedName.length() < 2 || trimmedName.length() > 255) {
            return "Tên thương hiệu phải có độ dài từ 2 đến 255 ký tự.";
        }

        if (originCountry != null && !originCountry.trim().isEmpty()) {
            String trimmedCountry = originCountry.trim();
            if (!trimmedCountry.matches("^[\\p{L}0-9 \\-&()]+$")) {
                return "Quốc gia xuất xứ chỉ được chứa chữ cái, số, khoảng trắng và các ký tự - & ( ).";
            }
            if (trimmedCountry.length() < 2 || trimmedCountry.length() > 100) {
                return "Quốc gia xuất xứ phải có độ dài từ 2 đến 100 ký tự.";
            }
        }
        return null;
    }

    private Integer parseNullableInteger(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String encodeParam(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            String ctx = request.getContextPath();

            if ("create".equals(action)) {
                request.getRequestDispatcher("/WEB-INF/brand/create.jsp").forward(request, response);

            } else if ("update".equals(action)) {
                Integer id = parseNullableInteger(request.getParameter("id"));
                if (id == null) {
                    response.sendRedirect(ctx + "/admin/brands?error=" + encodeParam("ID không hợp lệ."));
                    return;
                }

                Brand brand = brandDAO.getBrandById(id);
                if (brand == null) {
                    response.sendRedirect(ctx + "/admin/brands?error=" + encodeParam("Thương hiệu không tồn tại."));
                    return;
                }
                request.setAttribute("brand", brand);
                request.getRequestDispatcher("/WEB-INF/brand/update.jsp").forward(request, response);

            } else if ("delete".equals(action)) {
                Integer id = parseNullableInteger(request.getParameter("id"));
                if (id == null) {
                    response.sendRedirect(ctx + "/admin/brands?error=" + encodeParam("ID không hợp lệ."));
                    return;
                }

                Brand brand = brandDAO.getBrandById(id);
                if (brand == null) {
                    response.sendRedirect(ctx + "/admin/brands?error=" + encodeParam("Thương hiệu không tồn tại."));
                    return;
                }

                boolean isReferenced = brandDAO.isReferencedByStationery(id);
                if (isReferenced) {
                    request.setAttribute("usageWarning", "Thương hiệu này đang được liên kết với một hoặc nhiều văn phòng phẩm. Không thể xóa.");
                }
                request.setAttribute("isReferenced", isReferenced);
                request.setAttribute("brand", brand);
                request.getRequestDispatcher("/WEB-INF/brand/delete.jsp").forward(request, response);

            } else {
                List<Brand> brands = brandDAO.getAllBrands();
                request.setAttribute("brands", brands);
                request.setAttribute("successMessage", request.getParameter("success"));
                request.setAttribute("errorMessage", request.getParameter("error"));
                request.getRequestDispatcher("/WEB-INF/brand/list.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi tải thông tin thương hiệu: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            String ctx = request.getContextPath();

            if ("create".equals(action)) {
                String name = request.getParameter("name");
                String originCountry = request.getParameter("originCountry");

                String errorMessage = validateBrand(name, originCountry);
                if (errorMessage != null) {
                    Brand brand = new Brand();
                    brand.setName(name == null ? "" : name.trim());
                    brand.setOriginCountry(originCountry == null ? "" : originCountry.trim());

                    request.setAttribute("errorMessage", errorMessage);
                    request.setAttribute("brand", brand);
                    request.getRequestDispatcher("/WEB-INF/brand/create.jsp").forward(request, response);
                    return;
                }

                Brand brand = new Brand();
                brand.setName(name.trim());
                brand.setOriginCountry(originCountry == null || originCountry.trim().isEmpty() ? null : originCountry.trim());

                boolean success = brandDAO.insertBrand(brand);
                if (success) {
                    response.sendRedirect(ctx + "/admin/brands?success=" + encodeParam("Thêm thương hiệu thành công."));
                } else {
                    request.setAttribute("errorMessage", "Không thể thêm thương hiệu.");
                    request.setAttribute("brand", brand);
                    request.getRequestDispatcher("/WEB-INF/brand/create.jsp").forward(request, response);
                }

            } else if ("update".equals(action)) {
                Integer id = parseNullableInteger(request.getParameter("id"));
                if (id == null) {
                    response.sendRedirect(ctx + "/admin/brands?error=" + encodeParam("ID không hợp lệ."));
                    return;
                }

                Brand existing = brandDAO.getBrandById(id);
                if (existing == null) {
                    response.sendRedirect(ctx + "/admin/brands?error=" + encodeParam("Thương hiệu không tồn tại."));
                    return;
                }

                String name = request.getParameter("name");
                String originCountry = request.getParameter("originCountry");

                String errorMessage = validateBrand(name, originCountry);
                if (errorMessage != null) {
                    Brand brand = new Brand();
                    brand.setId(id);
                    brand.setName(name == null ? "" : name.trim());
                    brand.setOriginCountry(originCountry == null ? "" : originCountry.trim());

                    request.setAttribute("errorMessage", errorMessage);
                    request.setAttribute("brand", brand);
                    request.getRequestDispatcher("/WEB-INF/brand/update.jsp").forward(request, response);
                    return;
                }

                Brand brand = new Brand();
                brand.setId(id);
                brand.setName(name.trim());
                brand.setOriginCountry(originCountry == null || originCountry.trim().isEmpty() ? null : originCountry.trim());

                boolean success = brandDAO.updateBrand(brand);
                if (success) {
                    response.sendRedirect(ctx + "/admin/brands?success=" + encodeParam("Cập nhật thương hiệu thành công."));
                } else {
                    request.setAttribute("errorMessage", "Không thể cập nhật thương hiệu.");
                    request.setAttribute("brand", brand);
                    request.getRequestDispatcher("/WEB-INF/brand/update.jsp").forward(request, response);
                }

            } else if ("delete".equals(action)) {
                Integer id = parseNullableInteger(request.getParameter("id"));
                if (id == null) {
                    response.sendRedirect(ctx + "/admin/brands?error=" + encodeParam("ID không hợp lệ."));
                    return;
                }

                if (brandDAO.isReferencedByStationery(id)) {
                    response.sendRedirect(ctx + "/admin/brands?error=" + encodeParam("Thương hiệu đang có sản phẩm liên kết, không thể xóa."));
                    return;
                }

                boolean success = brandDAO.deleteBrand(id);
                if (success) {
                    response.sendRedirect(ctx + "/admin/brands?success=" + encodeParam("Xóa thương hiệu thành công."));
                } else {
                    response.sendRedirect(ctx + "/admin/brands?error=" + encodeParam("Không thể xóa thương hiệu."));
                }

            } else {
                response.sendRedirect(ctx + "/admin/brands");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi xử lý thương hiệu: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
        }
    }
}
