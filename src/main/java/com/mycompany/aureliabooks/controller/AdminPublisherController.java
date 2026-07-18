package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.PublisherDAO;
import com.mycompany.aureliabooks.model.Publisher;
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
 * Admin Publisher CRUD Controller.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
@WebServlet(name = "AdminPublisherController", urlPatterns = {"/admin/publishers"})
public class AdminPublisherController extends HttpServlet {

    private final PublisherDAO publisherDAO = new PublisherDAO();

    private String validatePublisher(String name, String address) {
        if (name == null || name.trim().isEmpty()) {
            return "Tên nhà xuất bản không được để trống.";
        }
        String trimmedName = name.trim();
        if (!trimmedName.matches("^[\\p{L}0-9 \\-&()]+$")) {
            return "Tên nhà xuất bản chỉ được chứa chữ cái, số, khoảng trắng và các ký tự - & ( ).";
        }
        if (trimmedName.length() < 2 || trimmedName.length() > 255) {
            return "Tên nhà xuất bản phải có độ dài từ 2 đến 255 ký tự.";
        }

        if (address != null && !address.trim().isEmpty()) {
            if (address.trim().length() > 500) {
                return "Địa chỉ không được vượt quá 500 ký tự.";
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
                request.getRequestDispatcher("/WEB-INF/publisher/create.jsp").forward(request, response);

            } else if ("update".equals(action)) {
                Integer id = parseNullableInteger(request.getParameter("id"));
                if (id == null) {
                    response.sendRedirect(ctx + "/admin/publishers?error=" + encodeParam("ID không hợp lệ."));
                    return;
                }

                Publisher publisher = publisherDAO.getPublisherById(id);
                if (publisher == null) {
                    response.sendRedirect(ctx + "/admin/publishers?error=" + encodeParam("Nhà xuất bản không tồn tại."));
                    return;
                }
                request.setAttribute("publisher", publisher);
                request.getRequestDispatcher("/WEB-INF/publisher/update.jsp").forward(request, response);

            } else if ("delete".equals(action)) {
                Integer id = parseNullableInteger(request.getParameter("id"));
                if (id == null) {
                    response.sendRedirect(ctx + "/admin/publishers?error=" + encodeParam("ID không hợp lệ."));
                    return;
                }

                Publisher publisher = publisherDAO.getPublisherById(id);
                if (publisher == null) {
                    response.sendRedirect(ctx + "/admin/publishers?error=" + encodeParam("Nhà xuất bản không tồn tại."));
                    return;
                }

                boolean isReferenced = publisherDAO.isReferencedByBook(id);
                if (isReferenced) {
                    request.setAttribute("usageWarning", "Nhà xuất bản này đang được liên kết với một hoặc nhiều sách. Không thể xóa.");
                }
                request.setAttribute("isReferenced", isReferenced);
                request.setAttribute("publisher", publisher);
                request.getRequestDispatcher("/WEB-INF/publisher/delete.jsp").forward(request, response);

            } else {
                List<Publisher> publishers = publisherDAO.getAllPublishers();
                request.setAttribute("publishers", publishers);
                request.setAttribute("successMessage", request.getParameter("success"));
                request.setAttribute("errorMessage", request.getParameter("error"));
                request.getRequestDispatcher("/WEB-INF/publisher/list.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi tải thông tin nhà xuất bản: " + e.getMessage());
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
                    response.sendRedirect(ctx + "/admin/publishers?success=" + encodeParam("Thêm nhà xuất bản thành công."));
                } else {
                    request.setAttribute("errorMessage", "Không thể thêm nhà xuất bản.");
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
                    response.sendRedirect(ctx + "/admin/publishers?error=" + encodeParam("Nhà xuất bản không tồn tại."));
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
                    response.sendRedirect(ctx + "/admin/publishers?success=" + encodeParam("Cập nhật nhà xuất bản thành công."));
                } else {
                    request.setAttribute("errorMessage", "Không thể cập nhật nhà xuất bản.");
                    request.setAttribute("publisher", publisher);
                    request.getRequestDispatcher("/WEB-INF/publisher/update.jsp").forward(request, response);
                }

            } else if ("delete".equals(action)) {
                Integer id = parseNullableInteger(request.getParameter("id"));
                if (id == null) {
                    response.sendRedirect(ctx + "/admin/publishers?error=" + encodeParam("ID không hợp lệ."));
                    return;
                }

                if (publisherDAO.isReferencedByBook(id)) {
                    response.sendRedirect(ctx + "/admin/publishers?error=" + encodeParam("Nhà xuất bản đang có sách liên kết, không thể xóa."));
                    return;
                }

                boolean success = publisherDAO.deletePublisher(id);
                if (success) {
                    response.sendRedirect(ctx + "/admin/publishers?success=" + encodeParam("Xóa nhà xuất bản thành công."));
                } else {
                    response.sendRedirect(ctx + "/admin/publishers?error=" + encodeParam("Không thể xóa nhà xuất bản."));
                }

            } else {
                response.sendRedirect(ctx + "/admin/publishers");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi xử lý nhà xuất bản: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
        }
    }
}
