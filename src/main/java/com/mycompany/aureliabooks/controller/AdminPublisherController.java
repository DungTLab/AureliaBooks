/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.PublisherDAO;
import com.mycompany.aureliabooks.model.Publisher;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Admin Publisher CRUD Controller. Created like NetBeans Maven template.
 *
 * @author DungLT
 */
@WebServlet(name = "AdminPublisherController", urlPatterns = {"/admin/publishers"})
public class AdminPublisherController extends HttpServlet {

    private final PublisherDAO publisherDAO = new PublisherDAO();

    private String validatePublisher(String name, String address) {
        if (name == null || name.trim().isEmpty()) {
            return "Tên NXB không được để trống.";
        }
        String trimmedName = name.trim();
        if (!trimmedName.matches("^[\\p{L}0-9 \\-&()]+$")) {
            return "Tên NXB chỉ được chứa chữ cái, số, khoảng trắng và các ký tự - & ( ).";
        }
        if (trimmedName.length() < 2 || trimmedName.length() > 255) {
            return "Tên NXB phải có độ dài từ 2 đến 255 ký tự.";
        }

        if (address != null && !address.trim().isEmpty()) {
            String trimmedAddress = address.trim();
            if (!trimmedAddress.matches("^[\\p{L}0-9 \\-&(),]+$")) {
                return "Địa Chỉ chỉ được chứa chữ cái, số, khoảng trắng và các ký tự - & ( ).,";
            }
            if (trimmedAddress.length() < 2 || trimmedAddress.length() > 500) {
                return "Địa chỉ phải có độ dài từ 2 đến 500 ký tự.";
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
            String view = request.getParameter("view");
            String ctx = request.getContextPath();

            if ("create".equals(view)) {
                request.getRequestDispatcher("/WEB-INF/publisher/create.jsp").forward(request, response);

            } else if ("update".equals(view)) {
                Integer id = parseNullableInteger(request.getParameter("id"));
                if (id == null) {
                    response.sendRedirect(ctx + "/admin/publishers?error=" + encodeParam("ID không hợp lệ."));
                    return;
                }

                Publisher publisher = publisherDAO.getPublisherById(id);
                if (publisher == null) {
                    response.sendRedirect(ctx + "/admin/publishers?error=" + encodeParam("NXB không tồn tại."));
                    return;
                }
                request.setAttribute("publisher", publisher);
                request.getRequestDispatcher("/WEB-INF/publisher/update.jsp").forward(request, response);

            } else if ("delete".equals(view)) {
                Integer id = parseNullableInteger(request.getParameter("id"));
                if (id == null) {
                    response.sendRedirect(ctx + "/admin/publishers?error=" + encodeParam("ID không hợp lệ."));
                    return;
                }

                Publisher publisher = publisherDAO.getPublisherById(id);
                if (publisher == null) {
                    response.sendRedirect(ctx + "/admin/publishers?error=" + encodeParam("NXB không tồn tại."));
                    return;
                }

                boolean isReferenced = publisherDAO.isReferencedByBooks(id);
                if (isReferenced) {
                    request.setAttribute("usageWarning", "NXB này đang được liên kết với một hoặc nhiều sách. Không thể xóa.");
                }
                request.setAttribute("isReferenced", isReferenced);
                request.setAttribute("publisher", publisher);
                request.getRequestDispatcher("/WEB-INF/publisher/delete.jsp").forward(request, response);

            } else {
                List<Publisher> publishers = publisherDAO.getAllPublisher();
                request.setAttribute("publishers", publishers);
                request.setAttribute("successMessage", request.getParameter("success"));
                request.setAttribute("errorMessage", request.getParameter("error"));
                request.getRequestDispatcher("/WEB-INF/publisher/list.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi tải thông tin NXB: " + e.getMessage());
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
                String address = request.getParameter("address");

                String errorMessage = validatePublisher(name, address);
                if (errorMessage != null) {
                    Publisher publisher = new Publisher();
                    publisher.setName(name == null ? "" : name.trim());
                    publisher.setAddress(address == null ? "" : address.trim());

                    request.setAttribute("errorMessage", errorMessage);
                    request.setAttribute("publisher", publisher);
                    request.getRequestDispatcher("/WEB-INF/publisher/create.jsp").forward(request, response);
                    return;
                }

                Publisher publisher = new Publisher();
                publisher.setName(name.trim());
                publisher.setAddress(address == null || address.trim().isEmpty() ? null : address.trim());

                boolean success = publisherDAO.insertPublisher(publisher);
                if (success) {
                    response.sendRedirect(ctx + "/admin/publishers?success=" + encodeParam("Thêm NXB thành công."));
                } else {
                    request.setAttribute("errorMessage", "Không thể thêm NXB.");
                    request.setAttribute("publisher", publisher);
                    request.getRequestDispatcher("/WEB-INF/publisher/create.jsp").forward(request, response);
                }

            } else if ("update".equals(action)) {
                Integer id = parseNullableInteger(request.getParameter("id"));
                if (id == null) {
                    response.sendRedirect(ctx + "/admin/publishers?error=" + encodeParam("ID không hợp lệ."));
                    return;
                }

                Publisher existing = publisherDAO.getPublisherById(id);
                if (existing == null) {
                    response.sendRedirect(ctx + "/admin/publishers?error=" + encodeParam("NXB không tồn tại."));
                    return;
                }

                String name = request.getParameter("name");
                String address = request.getParameter("address");

                String errorMessage = validatePublisher(name, address);
                if (errorMessage != null) {
                    Publisher publisher = new Publisher();
                    publisher.setId(id);
                    publisher.setName(name == null ? "" : name.trim());
                    publisher.setAddress(address == null ? "" : address.trim());

                    request.setAttribute("errorMessage", errorMessage);
                    request.setAttribute("publisher", publisher);
                    request.getRequestDispatcher("/WEB-INF/publisher/update.jsp").forward(request, response);
                    return;
                }

                Publisher publisher = new Publisher();
                publisher.setId(id);
                publisher.setName(name.trim());
                publisher.setAddress(address == null || address.trim().isEmpty() ? null : address.trim());

                boolean success = publisherDAO.updatePublisher(publisher);
                if (success) {
                    response.sendRedirect(ctx + "/admin/publishers?success=" + encodeParam("Cập nhật NXB thành công."));
                } else {
                    request.setAttribute("errorMessage", "Không thể cập nhật NXB.");
                    request.setAttribute("publisher", publisher);
                    request.getRequestDispatcher("/WEB-INF/publisher/update.jsp").forward(request, response);
                }

            } else if ("delete".equals(action)) {
                Integer id = parseNullableInteger(request.getParameter("id"));
                if (id == null) {
                    response.sendRedirect(ctx + "/admin/publishers?error=" + encodeParam("ID không hợp lệ."));
                    return;
                }

                if (publisherDAO.isReferencedByBooks(id)) {
                    response.sendRedirect(ctx + "/admin/publishers?error=" + encodeParam("NXB đang có sản phẩm liên kết, không thể xóa."));
                    return;
                }

                boolean success = publisherDAO.deletePublisher(id);
                if (success) {
                    response.sendRedirect(ctx + "/admin/publishers?success=" + encodeParam("Xóa NXB thành công."));
                } else {
                    response.sendRedirect(ctx + "/admin/publishers?error=" + encodeParam("Không thể xóa NXB."));
                }

            } else {
                response.sendRedirect(ctx + "/admin/publishers");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi xử lý NXB: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
        }
    }
}
