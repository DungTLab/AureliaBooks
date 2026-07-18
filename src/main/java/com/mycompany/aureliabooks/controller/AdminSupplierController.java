package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.SupplierDAO;
import com.mycompany.aureliabooks.model.Supplier;
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
 * Admin Supplier CRUD Controller.
 */
@WebServlet(name = "AdminSupplierController", urlPatterns = {"/admin/suppliers"})
public class AdminSupplierController extends HttpServlet {

    private static final String LIST_URL = "/admin/suppliers";
    private final SupplierDAO supplierDAO = new SupplierDAO();

    private String validateSupplier(String name, String email, String phone, String address) {
        if (name == null || name.trim().isEmpty()) {
            return "Tên nhà cung cấp không được để trống.";
        }

        String trimmedName = name.trim();
        if (trimmedName.length() < 2 || trimmedName.length() > 255) {
            return "Tên nhà cung cấp phải có độ dài từ 2 đến 255 ký tự.";
        }
        if (!trimmedName.matches("^[\\p{L}0-9 \\-&().]+$")) {
            return "Tên nhà cung cấp chỉ được chứa chữ cái, số, khoảng trắng và các ký tự - & ( ) .";
        }

        if (email != null && !email.trim().isEmpty()) {
            String trimmedEmail = email.trim();
            if (trimmedEmail.length() > 100) {
                return "Email liên hệ không được vượt quá 100 ký tự.";
            }
            if (!trimmedEmail.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return "Email liên hệ không đúng định dạng.";
            }
        }

        if (phone != null && !phone.trim().isEmpty()) {
            String trimmedPhone = phone.trim();
            if (trimmedPhone.length() < 7 || trimmedPhone.length() > 20) {
                return "Số điện thoại phải có độ dài từ 7 đến 20 ký tự.";
            }
            if (!trimmedPhone.matches("^[0-9+() .-]+$")) {
                return "Số điện thoại chỉ được chứa chữ số và các ký tự + ( ) khoảng trắng . -";
            }
        }

        if (address != null && address.trim().length() > 1000) {
            return "Địa chỉ không được vượt quá 1000 ký tự.";
        }
        return null;
    }

    private Integer parseId(String raw) {
        try {
            int id = Integer.parseInt(raw);
            return id > 0 ? id : null;
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    private String encodeParam(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String nullableValue(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private Supplier buildSupplier(Integer id, String name, String email, String phone, String address) {
        Supplier supplier = new Supplier();
        if (id != null) {
            supplier.setId(id);
        }
        supplier.setName(name == null ? "" : name.trim());
        supplier.setContactEmail(nullableValue(email));
        supplier.setContactPhone(nullableValue(phone));
        supplier.setAddress(nullableValue(address));
        return supplier;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            String ctx = request.getContextPath();

            if ("create".equals(action)) {
                request.getRequestDispatcher("/WEB-INF/supplier/create.jsp").forward(request, response);
                return;
            }

            if ("update".equals(action) || "delete".equals(action)) {
                Integer id = parseId(request.getParameter("id"));
                if (id == null) {
                    response.sendRedirect(ctx + LIST_URL + "?error=" + encodeParam("ID không hợp lệ."));
                    return;
                }

                Supplier supplier = supplierDAO.getSupplierById(id);
                if (supplier == null) {
                    response.sendRedirect(ctx + LIST_URL + "?error=" + encodeParam("Nhà cung cấp không tồn tại."));
                    return;
                }

                request.setAttribute("supplier", supplier);
                if ("delete".equals(action)) {
                    boolean isReferenced = supplierDAO.isReferencedByProducts(id);
                    request.setAttribute("isReferenced", isReferenced);
                    if (isReferenced) {
                        request.setAttribute("usageWarning",
                                "Nhà cung cấp này đang được liên kết với một hoặc nhiều sản phẩm. Không thể xóa.");
                    }
                    request.getRequestDispatcher("/WEB-INF/supplier/delete.jsp").forward(request, response);
                } else {
                    request.getRequestDispatcher("/WEB-INF/supplier/update.jsp").forward(request, response);
                }
                return;
            }

            List<Supplier> suppliers = supplierDAO.getAllSuppliers();
            request.setAttribute("suppliers", suppliers);
            request.setAttribute("successMessage", request.getParameter("success"));
            request.setAttribute("errorMessage", request.getParameter("error"));
            request.getRequestDispatcher("/WEB-INF/supplier/list.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi tải thông tin nhà cung cấp: " + e.getMessage());
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
                saveNewSupplier(request, response, ctx);
            } else if ("update".equals(action)) {
                updateSupplier(request, response, ctx);
            } else if ("delete".equals(action)) {
                deleteSupplier(request, response, ctx);
            } else {
                response.sendRedirect(ctx + LIST_URL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi xử lý nhà cung cấp: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
        }
    }

    private void saveNewSupplier(HttpServletRequest request, HttpServletResponse response, String ctx)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("contactEmail");
        String phone = request.getParameter("contactPhone");
        String address = request.getParameter("address");
        Supplier supplier = buildSupplier(null, name, email, phone, address);

        String errorMessage = validateSupplier(name, email, phone, address);
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            request.setAttribute("supplier", supplier);
            request.getRequestDispatcher("/WEB-INF/supplier/create.jsp").forward(request, response);
            return;
        }

        if (supplierDAO.insertSupplier(supplier)) {
            response.sendRedirect(ctx + LIST_URL + "?success=" + encodeParam("Thêm nhà cung cấp thành công."));
        } else {
            request.setAttribute("errorMessage", "Không thể thêm nhà cung cấp.");
            request.setAttribute("supplier", supplier);
            request.getRequestDispatcher("/WEB-INF/supplier/create.jsp").forward(request, response);
        }
    }

    private void updateSupplier(HttpServletRequest request, HttpServletResponse response, String ctx)
            throws ServletException, IOException {
        Integer id = parseId(request.getParameter("id"));
        if (id == null) {
            response.sendRedirect(ctx + LIST_URL + "?error=" + encodeParam("ID không hợp lệ."));
            return;
        }
        if (supplierDAO.getSupplierById(id) == null) {
            response.sendRedirect(ctx + LIST_URL + "?error=" + encodeParam("Nhà cung cấp không tồn tại."));
            return;
        }

        String name = request.getParameter("name");
        String email = request.getParameter("contactEmail");
        String phone = request.getParameter("contactPhone");
        String address = request.getParameter("address");
        Supplier supplier = buildSupplier(id, name, email, phone, address);

        String errorMessage = validateSupplier(name, email, phone, address);
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            request.setAttribute("supplier", supplier);
            request.getRequestDispatcher("/WEB-INF/supplier/update.jsp").forward(request, response);
            return;
        }

        if (supplierDAO.updateSupplier(supplier)) {
            response.sendRedirect(ctx + LIST_URL + "?success=" + encodeParam("Cập nhật nhà cung cấp thành công."));
        } else {
            request.setAttribute("errorMessage", "Không thể cập nhật nhà cung cấp.");
            request.setAttribute("supplier", supplier);
            request.getRequestDispatcher("/WEB-INF/supplier/update.jsp").forward(request, response);
        }
    }

    private void deleteSupplier(HttpServletRequest request, HttpServletResponse response, String ctx)
            throws IOException {
        Integer id = parseId(request.getParameter("id"));
        if (id == null) {
            response.sendRedirect(ctx + LIST_URL + "?error=" + encodeParam("ID không hợp lệ."));
            return;
        }
        if (supplierDAO.getSupplierById(id) == null) {
            response.sendRedirect(ctx + LIST_URL + "?error=" + encodeParam("Nhà cung cấp không tồn tại."));
            return;
        }
        if (supplierDAO.isReferencedByProducts(id)) {
            response.sendRedirect(ctx + LIST_URL + "?error="
                    + encodeParam("Nhà cung cấp đang có sản phẩm liên kết, không thể xóa."));
            return;
        }

        if (supplierDAO.deleteSupplier(id)) {
            response.sendRedirect(ctx + LIST_URL + "?success=" + encodeParam("Xóa nhà cung cấp thành công."));
        } else {
            response.sendRedirect(ctx + LIST_URL + "?error=" + encodeParam("Không thể xóa nhà cung cấp."));
        }
    }
}
