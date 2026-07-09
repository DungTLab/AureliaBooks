package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.CategoryDAO;
import com.mycompany.aureliabooks.dao.ProductDAO;
import com.mycompany.aureliabooks.model.Book;
import com.mycompany.aureliabooks.model.Product;
import com.mycompany.aureliabooks.util.UploadUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

/**
 * Admin Product CRUD Controller.
 * Chỉ sử dụng request.setAttribute() và request.getRequestDispatcher().forward()
 * theo đúng mức độ cơ bản của sinh viên học môn PRJ301.
 *
 * URL pattern: /admin/products?view={list|create|update|delete}
 *
 * @author DungLT
 */
@WebServlet(name = "AdminProductController", urlPatterns = {"/admin/products"})
@MultipartConfig // Cần có annotation này để đọc được file upload (enctype="multipart/form-data")
public class AdminProductController extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    // Hàm phụ trợ để tải danh sách sách và chuyển hướng sang list.jsp
    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int limit = 10;
        int currentPage = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        int totalProducts = productDAO.countAdminProducts();
        int totalPages = (int) Math.ceil((double) totalProducts / limit);
        if (totalPages < 1) totalPages = 1;
        if (currentPage > totalPages) currentPage = totalPages;
        int offset = (currentPage - 1) * limit;

        List<Product> listBooks = productDAO.getAdminProductList(offset, limit);

        request.setAttribute("listBooks", listBooks);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("/WEB-INF/product/list.jsp").forward(request, response);
    }

    // =========================================================================
    // doGet - Hiển thị các trang CRUD
    // =========================================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String view = request.getParameter("view");
        // Mặc định hiển thị trang danh sách sách
        if (view == null || view.trim().isEmpty()) {
            view = "list";
        }

        switch (view) {
            // ------------------------------------------------------------------
            // LIST: hiển thị danh sách tất cả sản phẩm
            // ------------------------------------------------------------------
            case "list": {
                showList(request, response);
                break;
            }

            // ------------------------------------------------------------------
            // CREATE: hiển thị form thêm sách mới
            // ------------------------------------------------------------------
            case "create": {
                request.setAttribute("categories", categoryDAO.getAllCategories());
                request.setAttribute("publishers", productDAO.getAllPublishers());
                request.getRequestDispatcher("/WEB-INF/product/create.jsp").forward(request, response);
                break;
            }

            // ------------------------------------------------------------------
            // UPDATE: hiển thị form sửa sách, load data sách lên trước
            // ------------------------------------------------------------------
            case "update": {
                String productIdParam = request.getParameter("productId");
                if (productIdParam == null || productIdParam.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Thiếu ID sản phẩm.");
                    showList(request, response);
                    return;
                }
                try {
                    int productId = Integer.parseInt(productIdParam);
                    Product book = productDAO.getProductById(productId);
                    if (book == null) {
                        request.setAttribute("errorMessage", "Không tìm thấy sản phẩm có ID = " + productId + ".");
                        showList(request, response);
                        return;
                    }
                    request.setAttribute("book", book);
                    request.setAttribute("categories", categoryDAO.getAllCategories());
                    request.setAttribute("publishers", productDAO.getAllPublishers());
                    request.getRequestDispatcher("/WEB-INF/product/update.jsp").forward(request, response);
                } catch (NumberFormatException e) {
                    request.setAttribute("errorMessage", "ID sản phẩm không hợp lệ.");
                    showList(request, response);
                }
                break;
            }

            // ------------------------------------------------------------------
            // DELETE: hiển thị trang xác nhận xóa
            // ------------------------------------------------------------------
            case "delete": {
                String productIdParam = request.getParameter("productId");
                if (productIdParam == null || productIdParam.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Thiếu ID sản phẩm.");
                    showList(request, response);
                    return;
                }
                try {
                    int productId = Integer.parseInt(productIdParam);
                    Product book = productDAO.getProductById(productId);
                    if (book == null) {
                        request.setAttribute("errorMessage", "Không tìm thấy sản phẩm có ID = " + productId + ".");
                        showList(request, response);
                        return;
                    }
                    request.setAttribute("book", book);
                    request.getRequestDispatcher("/WEB-INF/product/delete.jsp").forward(request, response);
                } catch (NumberFormatException e) {
                    request.setAttribute("errorMessage", "ID sản phẩm không hợp lệ.");
                    showList(request, response);
                }
                break;
            }

            // Không khớp view nào thì về list
            default:
                showList(request, response);
                break;
        }
    }

    // =========================================================================
    // doPost - Xử lý form submit (thêm / sửa / xóa sản phẩm)
    // =========================================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Đặt encoding để đọc tiếng Việt không bị lỗi
        request.setCharacterEncoding("UTF-8");

        String view = request.getParameter("view");

        // Dựa vào tham số ?view= để biết đang làm gì
        if ("create".equals(view)) {
            handleCreate(request, response);

        } else if ("update".equals(view)) {
            handleUpdate(request, response);

        } else if ("delete".equals(view)) {
            handleDelete(request, response);

        } else {
            showList(request, response);
        }
    }

    // =========================================================================
    // Xử lý THÊM sách mới
    // =========================================================================
    private void handleCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Bước 1: Đọc các trường từ form
            String title       = request.getParameter("title");
            String categoryStr = request.getParameter("categoryId");
            String priceStr    = request.getParameter("price");
            String sku         = request.getParameter("sku");
            String description = request.getParameter("description");

            // Các trường riêng của sách
            String publisherStr    = request.getParameter("publisherId");
            String translator      = request.getParameter("translator");
            String yearStr         = request.getParameter("publicationYear");
            String pagesStr        = request.getParameter("numberOfPages");
            String coverType       = request.getParameter("coverType");
            String language        = request.getParameter("language");

            // Bước 2: Kiểm tra dữ liệu cơ bản (validate đơn giản)
            if (title == null || title.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Tiêu đề sách không được để trống!");
                request.setAttribute("categories", categoryDAO.getAllCategories());
                request.setAttribute("publishers", productDAO.getAllPublishers());
                request.getRequestDispatcher("/WEB-INF/product/create.jsp").forward(request, response);
                return;
            }

            // Bước 3: Xử lý upload ảnh
            Part imagePart = request.getPart("image");
            String imageUrl = UploadUtils.saveUploadedFile(imagePart, getServletContext(), "book-image");
            if (imageUrl == null) {
                imageUrl = "default-book.jpg";
            }

            // Bước 4: Tạo object Book và set các giá trị
            Book newBook = new Book();
            newBook.setTitle(title.trim());
            newBook.setDescription(description);
            newBook.setSku(sku);
            newBook.setImageUrl(imageUrl);
            newBook.setIsActive(true);

            newBook.setCategoryId(Integer.parseInt(categoryStr));
            newBook.setPrice(new BigDecimal(priceStr));

            if (publisherStr != null && !publisherStr.trim().isEmpty()) {
                newBook.setPublisherId(Integer.parseInt(publisherStr));
            }
            if (translator != null && !translator.trim().isEmpty()) {
                newBook.setTranslator(translator.trim());
            }
            if (yearStr != null && !yearStr.trim().isEmpty()) {
                newBook.setPublicationYear(Integer.parseInt(yearStr));
            }
            if (pagesStr != null && !pagesStr.trim().isEmpty()) {
                newBook.setNumberOfPages(Integer.parseInt(pagesStr));
            }
            if (coverType != null && !coverType.trim().isEmpty()) {
                newBook.setCoverType(coverType.trim());
            }
            if (language != null && !language.trim().isEmpty()) {
                newBook.setLanguage(language.trim());
            }

            // Bước 5: Gọi DAO để lưu vào database
            boolean ok = productDAO.insertBook(newBook);

            // Bước 6: Đặt thông báo kết quả và hiển thị danh sách sách mới
            if (ok) {
                request.setAttribute("successMessage", "Thêm sách \"" + title.trim() + "\" thành công!");
            } else {
                request.setAttribute("errorMessage", "Thêm sách thất bại, vui lòng thử lại.");
            }
            showList(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Dữ liệu số không hợp lệ, vui lòng kiểm tra lại.");
            request.setAttribute("categories", categoryDAO.getAllCategories());
            request.setAttribute("publishers", productDAO.getAllPublishers());
            request.getRequestDispatcher("/WEB-INF/product/create.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            request.setAttribute("categories", categoryDAO.getAllCategories());
            request.setAttribute("publishers", productDAO.getAllPublishers());
            request.getRequestDispatcher("/WEB-INF/product/create.jsp").forward(request, response);
        }
    }

    // =========================================================================
    // Xử lý SỬA sách
    // =========================================================================
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Bước 1: Đọc productId từ hidden input
            String productIdParam = request.getParameter("productId");
            if (productIdParam == null || productIdParam.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Thiếu ID sản phẩm khi cập nhật.");
                showList(request, response);
                return;
            }
            int productId = Integer.parseInt(productIdParam);

            // Bước 2: Lấy sách cũ từ DB để kiểm tra còn tồn tại không
            Product existing = productDAO.getProductById(productId);
            if (existing == null) {
                request.setAttribute("errorMessage", "Không tìm thấy sản phẩm để cập nhật.");
                showList(request, response);
                return;
            }

            // Bước 3: Đọc các trường mới từ form
            String title       = request.getParameter("title");
            String categoryStr = request.getParameter("categoryId");
            String priceStr    = request.getParameter("price");
            String sku         = request.getParameter("sku");
            String description = request.getParameter("description");

            String publisherStr    = request.getParameter("publisherId");
            String translator      = request.getParameter("translator");
            String yearStr         = request.getParameter("publicationYear");
            String pagesStr        = request.getParameter("numberOfPages");
            String coverType       = request.getParameter("coverType");
            String language        = request.getParameter("language");

            // Bước 4: Validate cơ bản
            if (title == null || title.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Tiêu đề sách không được để trống!");
                request.setAttribute("book", existing);
                request.setAttribute("categories", categoryDAO.getAllCategories());
                request.setAttribute("publishers", productDAO.getAllPublishers());
                request.getRequestDispatcher("/WEB-INF/product/update.jsp").forward(request, response);
                return;
            }

            // Bước 5: Xử lý ảnh
            Part imagePart = request.getPart("image");
            String imageUrl = UploadUtils.saveUploadedFile(imagePart, getServletContext(), "book-image");
            if (imageUrl == null) {
                imageUrl = existing.getImageUrl(); // Giữ ảnh cũ
            }

            // Bước 6: Gán dữ liệu mới vào Book object
            Book updatedBook = new Book();
            updatedBook.setId(productId);
            updatedBook.setTitle(title.trim());
            updatedBook.setDescription(description);
            updatedBook.setSku(sku);
            updatedBook.setImageUrl(imageUrl);
            updatedBook.setIsActive(existing.isIsActive());
            updatedBook.setCategoryId(Integer.parseInt(categoryStr));
            updatedBook.setPrice(new BigDecimal(priceStr));

            if (publisherStr != null && !publisherStr.trim().isEmpty()) {
                updatedBook.setPublisherId(Integer.parseInt(publisherStr));
            }
            if (translator != null && !translator.trim().isEmpty()) {
                updatedBook.setTranslator(translator.trim());
            }
            if (yearStr != null && !yearStr.trim().isEmpty()) {
                updatedBook.setPublicationYear(Integer.parseInt(yearStr));
            }
            if (pagesStr != null && !pagesStr.trim().isEmpty()) {
                updatedBook.setNumberOfPages(Integer.parseInt(pagesStr));
            }
            if (coverType != null && !coverType.trim().isEmpty()) {
                updatedBook.setCoverType(coverType.trim());
            }
            if (language != null && !language.trim().isEmpty()) {
                updatedBook.setLanguage(language.trim());
            }

            // Bước 7: Lưu xuống database
            boolean ok = productDAO.updateBook(updatedBook);

            // Bước 8: Đặt thông báo kết quả và hiển thị danh sách sách mới
            if (ok) {
                request.setAttribute("successMessage", "Cập nhật sách \"" + title.trim() + "\" thành công!");
            } else {
                request.setAttribute("errorMessage", "Cập nhật thất bại, vui lòng thử lại.");
            }
            showList(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Dữ liệu số không hợp lệ khi cập nhật.");
            showList(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            showList(request, response);
        }
    }

    // =========================================================================
    // Xử lý XÓA sách (soft delete - chỉ set isActive = 0)
    // =========================================================================
    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Bước 1: Lấy productId từ hidden input
            String productIdParam = request.getParameter("productId");
            if (productIdParam == null || productIdParam.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Thiếu ID sản phẩm khi xóa.");
                showList(request, response);
                return;
            }
            int productId = Integer.parseInt(productIdParam);

            // Bước 2: Kiểm tra sản phẩm còn tồn tại không
            Product existing = productDAO.getProductById(productId);
            if (existing == null) {
                request.setAttribute("errorMessage", "Không tìm thấy sản phẩm để xóa.");
                showList(request, response);
                return;
            }

            // Bước 3: Gọi DAO xóa mềm (đặt isActive = 0, không xóa khỏi DB)
            boolean ok = productDAO.deleteProduct(productId);

            // Bước 4: Đặt thông báo kết quả và hiển thị danh sách sách mới
            if (ok) {
                request.setAttribute("successMessage", "Đã ngừng bán sách \"" + existing.getTitle() + "\" thành công!");
            } else {
                request.setAttribute("errorMessage", "Xóa thất bại, vui lòng thử lại.");
            }
            showList(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "ID sản phẩm không hợp lệ.");
            showList(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            showList(request, response);
        }
    }
}
