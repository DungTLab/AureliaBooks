package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.AuthorDAO;
import com.mycompany.aureliabooks.model.Author;
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
 * Admin Author CRUD Controller.
 * Created like NetBeans Maven template.
 * @author DungLT
 */
@WebServlet(name = "AdminAuthorController", urlPatterns = {"/admin/authors"})
public class AdminAuthorController extends HttpServlet {

    private final AuthorDAO authorDAO = new AuthorDAO();

    private String validateAuthor(String fullName, String biography) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "Họ tên tác giả không được để trống.";
        }
        String trimmedName = fullName.trim();
        if (!trimmedName.matches("^[\\p{L}0-9 \\-&()]+$")) {
            return "Họ tên tác giả chỉ được chứa chữ cái, số, khoảng trắng và các ký tự - & ( ).";
        }
        if (trimmedName.length() < 2 || trimmedName.length() > 255) {
            return "Họ tên tác giả phải có độ dài từ 2 đến 255 ký tự.";
        }

        if (biography != null && !biography.trim().isEmpty()) {
            if (biography.trim().length() > 2000) {
                return "Tiểu sử tác giả không được vượt quá 2000 ký tự.";
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
                request.getRequestDispatcher("/WEB-INF/author/create.jsp").forward(request, response);

            } else if ("update".equals(action)) {
                Integer id = parseNullableInteger(request.getParameter("id"));
                if (id == null) {
                    response.sendRedirect(ctx + "/admin/authors?error=" + encodeParam("ID không hợp lệ."));
                    return;
                }

                Author author = authorDAO.getAuthorById(id);
                if (author == null) {
                    response.sendRedirect(ctx + "/admin/authors?error=" + encodeParam("Tác giả không tồn tại."));
                    return;
                }
                request.setAttribute("author", author);
                request.getRequestDispatcher("/WEB-INF/author/update.jsp").forward(request, response);

            } else if ("delete".equals(action)) {
                Integer id = parseNullableInteger(request.getParameter("id"));
                if (id == null) {
                    response.sendRedirect(ctx + "/admin/authors?error=" + encodeParam("ID không hợp lệ."));
                    return;
                }

                Author author = authorDAO.getAuthorById(id);
                if (author == null) {
                    response.sendRedirect(ctx + "/admin/authors?error=" + encodeParam("Tác giả không tồn tại."));
                    return;
                }

                boolean isReferenced = authorDAO.isReferencedByBook(id);
                if (isReferenced) {
                    request.setAttribute("usageWarning", "Tác giả này đang được liên kết với một hoặc nhiều sách. Không thể xóa.");
                }
                request.setAttribute("isReferenced", isReferenced);
                request.setAttribute("author", author);
                request.getRequestDispatcher("/WEB-INF/author/delete.jsp").forward(request, response);

            } else {
                List<Author> authors = authorDAO.getAllAuthors();
                request.setAttribute("authors", authors);
                request.setAttribute("successMessage", request.getParameter("success"));
                request.setAttribute("errorMessage", request.getParameter("error"));
                request.getRequestDispatcher("/WEB-INF/author/list.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi tải thông tin tác giả: " + e.getMessage());
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
                String fullName = request.getParameter("fullName");
                String biography = request.getParameter("biography");

                String errorMessage = validateAuthor(fullName, biography);
                if (errorMessage != null) {
                    Author author = new Author();
                    author.setFullName(fullName == null ? "" : fullName.trim());
                    author.setBiography(biography == null ? "" : biography.trim());

                    request.setAttribute("errorMessage", errorMessage);
                    request.setAttribute("author", author);
                    request.getRequestDispatcher("/WEB-INF/author/create.jsp").forward(request, response);
                    return;
                }

                Author author = new Author();
                author.setFullName(fullName.trim());
                author.setBiography(biography == null || biography.trim().isEmpty() ? null : biography.trim());

                boolean success = authorDAO.insertAuthor(author);
                if (success) {
                    response.sendRedirect(ctx + "/admin/authors?success=" + encodeParam("Thêm tác giả thành công."));
                } else {
                    request.setAttribute("errorMessage", "Không thể thêm tác giả.");
                    request.setAttribute("author", author);
                    request.getRequestDispatcher("/WEB-INF/author/create.jsp").forward(request, response);
                }

            } else if ("update".equals(action)) {
                Integer id = parseNullableInteger(request.getParameter("id"));
                if (id == null) {
                    response.sendRedirect(ctx + "/admin/authors?error=" + encodeParam("ID không hợp lệ."));
                    return;
                }

                Author existing = authorDAO.getAuthorById(id);
                if (existing == null) {
                    response.sendRedirect(ctx + "/admin/authors?error=" + encodeParam("Tác giả không tồn tại."));
                    return;
                }

                String fullName = request.getParameter("fullName");
                String biography = request.getParameter("biography");

                String errorMessage = validateAuthor(fullName, biography);
                if (errorMessage != null) {
                    Author author = new Author();
                    author.setAuthorId(id);
                    author.setFullName(fullName == null ? "" : fullName.trim());
                    author.setBiography(biography == null ? "" : biography.trim());

                    request.setAttribute("errorMessage", errorMessage);
                    request.setAttribute("author", author);
                    request.getRequestDispatcher("/WEB-INF/author/update.jsp").forward(request, response);
                    return;
                }

                Author author = new Author();
                author.setAuthorId(id);
                author.setFullName(fullName.trim());
                author.setBiography(biography == null || biography.trim().isEmpty() ? null : biography.trim());

                boolean success = authorDAO.updateAuthor(author);
                if (success) {
                    response.sendRedirect(ctx + "/admin/authors?success=" + encodeParam("Cập nhật tác giả thành công."));
                } else {
                    request.setAttribute("errorMessage", "Không thể cập nhật tác giả.");
                    request.setAttribute("author", author);
                    request.getRequestDispatcher("/WEB-INF/author/update.jsp").forward(request, response);
                }

            } else if ("delete".equals(action)) {
                Integer id = parseNullableInteger(request.getParameter("id"));
                if (id == null) {
                    response.sendRedirect(ctx + "/admin/authors?error=" + encodeParam("ID không hợp lệ."));
                    return;
                }

                if (authorDAO.isReferencedByBook(id)) {
                    response.sendRedirect(ctx + "/admin/authors?error=" + encodeParam("Tác giả đang có sách liên kết, không thể xóa."));
                    return;
                }

                boolean success = authorDAO.deleteAuthor(id);
                if (success) {
                    response.sendRedirect(ctx + "/admin/authors?success=" + encodeParam("Xóa tác giả thành công."));
                } else {
                    response.sendRedirect(ctx + "/admin/authors?error=" + encodeParam("Không thể xóa tác giả."));
                }

            } else {
                response.sendRedirect(ctx + "/admin/authors");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi xử lý tác giả: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
        }
    }
}
