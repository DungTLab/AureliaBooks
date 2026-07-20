package com.mycompany.aureliabooks.controller;

import com.mycompany.aureliabooks.dao.AuthorDAO;
import com.mycompany.aureliabooks.dao.CategoryDAO;
import com.mycompany.aureliabooks.dao.ProductDAO;
import com.mycompany.aureliabooks.model.Author;
import com.mycompany.aureliabooks.model.Book;
import com.mycompany.aureliabooks.model.Product;
import com.mycompany.aureliabooks.model.Stationery;
import com.mycompany.aureliabooks.util.UploadUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
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
 *
 * URL pattern: /admin/products?view={list|create|update|delete}
 *
 * @author DungLT
 */
@WebServlet(name = "AdminProductController", urlPatterns = {"/admin/products"})
@MultipartConfig // Required to parse multipart file uploads (enctype="multipart/form-data")
public class AdminProductController extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final AuthorDAO authorDAO = new AuthorDAO();

    private Integer parseIntegerValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal parseBigDecimalValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean isValidPrice(BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) >= 0;
    }

    private boolean isValidPositiveInteger(Integer value) {
        return value != null && value > 0;
    }

    private boolean isValidPublicationYear(Integer value) {
        int currentYear = java.time.Year.now().getValue();
        return value != null && value >= 1900 && value <= currentYear;
    }

    private void setProductFormAttributes(HttpServletRequest request, String productType) {
        request.setAttribute("categories", categoryDAO.getAllCategories());
        if ("book".equals(productType)) {
            request.setAttribute("publishers", productDAO.getAllPublishers());
            request.setAttribute("suppliers", productDAO.getAllSuppliers());
            request.setAttribute("authors", authorDAO.getAllAuthors());
        } else {
            request.setAttribute("brands", productDAO.getAllBrands());
            request.setAttribute("suppliers", productDAO.getAllSuppliers());
        }
    }

    // Helper method to load product list and forward to list.jsp
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
    // doGet - Display CRUD views
    // =========================================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String view = request.getParameter("view");
        // Default to displaying the product list page
        if (view == null || view.trim().isEmpty()) {
            view = "list";
        }

        switch (view) {
            // ------------------------------------------------------------------
            // LIST: Display all products
            // ------------------------------------------------------------------
            case "list": {
                showList(request, response);
                break;
            }

            // ------------------------------------------------------------------
            // CREATE: Display product creation form
            // ------------------------------------------------------------------
            case "create": {
                String type = request.getParameter("type");
                if (type == null || type.trim().isEmpty()) {
                    type = "book";
                }
                request.setAttribute("productType", type);
                
                setProductFormAttributes(request, type);
                request.getRequestDispatcher("/WEB-INF/product/create.jsp").forward(request, response);
                break;
            }

            // ------------------------------------------------------------------
            // UPDATE: Display product edit form, pre-load existing product data
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
                    Product product = productDAO.getProductById(productId);
                    if (product == null) {
                        request.setAttribute("errorMessage", "Không tìm thấy sản phẩm có ID = " + productId + ".");
                        showList(request, response);
                        return;
                    }
                    
                    String productType = "book";
                    if (product instanceof Book) {
                        productType = "book";
                    } else if (product instanceof Stationery) {
                        productType = "stationery";
                    }
                    
                    request.setAttribute("productType", productType);
                    request.setAttribute("product", product);
                    setProductFormAttributes(request, productType);
                    request.getRequestDispatcher("/WEB-INF/product/update.jsp").forward(request, response);
                } catch (NumberFormatException e) {
                    request.setAttribute("errorMessage", "ID sản phẩm không hợp lệ.");
                    showList(request, response);
                }
                break;
            }

            // ------------------------------------------------------------------
            // DELETE: Display product deletion confirmation page
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

            // Default back to list view if no matches found
            default:
                showList(request, response);
                break;
        }
    }

    // =========================================================================
    // doPost - Handle form submissions (create / update / delete product)
    // =========================================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set encoding to handle UTF-8 request data
        request.setCharacterEncoding("UTF-8");

        String view = request.getParameter("view");

        // Check view parameter to decide action
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
    // Process creation of a new product
    // =========================================================================
    private void handleCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String productType = request.getParameter("productType");
            if (productType == null) productType = "book";

            String title       = request.getParameter("title");
            String categoryStr = request.getParameter("categoryId");
            String priceStr    = request.getParameter("price");
            String sku         = request.getParameter("sku");
            String description = request.getParameter("description");

            if (title == null || title.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Tiêu đề không được để trống!");
                request.setAttribute("productType", productType);
                setProductFormAttributes(request, productType);
                request.getRequestDispatcher("/WEB-INF/product/create.jsp").forward(request, response);
                return;
            }

            // Enforce alphanumeric formatting constraints for SKU
            if (sku == null || sku.trim().isEmpty() || !sku.matches("^[a-zA-Z0-9_\\-]+$")) {
                request.setAttribute("errorMessage", "SKU không hợp lệ (không được để trống, chỉ chứa chữ, số, gạch nối).");
                request.setAttribute("productType", productType);
                setProductFormAttributes(request, productType);
                request.getRequestDispatcher("/WEB-INF/product/create.jsp").forward(request, response);
                return;
            }

            Part imagePart = request.getPart("image");
            if (imagePart == null || imagePart.getSize() == 0 || imagePart.getContentType() == null || !imagePart.getContentType().startsWith("image/")) {
                request.setAttribute("errorMessage", "Vui lòng tải lên một tệp hình ảnh hợp lệ (VD: .jpg, .png)!");
                request.setAttribute("productType", productType);
                setProductFormAttributes(request, productType);
                request.getRequestDispatcher("/WEB-INF/product/create.jsp").forward(request, response);
                return;
            }
            String imageUrl = UploadUtils.saveUploadedFile(imagePart, getServletContext(), "book-image");
            if (imageUrl == null) {
                imageUrl = "default.jpg";
            }

            Integer categoryId = parseIntegerValue(categoryStr);
            BigDecimal price = parseBigDecimalValue(priceStr);

            if (categoryId == null || !isValidPrice(price)) {
                request.setAttribute("errorMessage", "Danh mục hoặc giá tiền không hợp lệ.");
                request.setAttribute("productType", productType);
                setProductFormAttributes(request, productType);
                request.getRequestDispatcher("/WEB-INF/product/create.jsp").forward(request, response);
                return;
            }

            boolean ok = false;

            if ("book".equals(productType)) {
                Book newBook = new Book();
                newBook.setTitle(title.trim());
                newBook.setDescription(description);
                newBook.setSku(sku);
                newBook.setImageUrl(imageUrl);
                newBook.setIsActive(true);
                newBook.setCategoryId(categoryId);
                newBook.setPrice(price);

                Integer publisherId = parseIntegerValue(request.getParameter("publisherId"));
                newBook.setPublisherId(publisherId);
                newBook.setTranslator(request.getParameter("translator"));

                Integer supplierId = parseIntegerValue(request.getParameter("supplierId"));
                newBook.setSupplierId(supplierId);

                String[] authorIdsParam = request.getParameterValues("authorIds");
                List<Integer> authorIds = new ArrayList<>();
                if (authorIdsParam != null) {
                    for (String idStr : authorIdsParam) {
                        try {
                            authorIds.add(Integer.parseInt(idStr));
                        } catch (NumberFormatException e) {
                            // ignore
                        }
                    }
                }
                newBook.setAuthorIds(authorIds);
                
                // Validate Book-specific numerical constraints
                Integer pubYear = parseIntegerValue(request.getParameter("publicationYear"));
                String pubYearRaw = request.getParameter("publicationYear");
                if (pubYearRaw != null && !pubYearRaw.trim().isEmpty() && !isValidPublicationYear(pubYear)) {
                    throw new IllegalArgumentException("Năm xuất bản không hợp lệ.");
                }
                newBook.setPublicationYear(pubYear);
                
                Integer numPages = parseIntegerValue(request.getParameter("numberOfPages"));
                String numPagesRaw = request.getParameter("numberOfPages");
                if (numPagesRaw != null && !numPagesRaw.trim().isEmpty() && !isValidPositiveInteger(numPages)) {
                    throw new IllegalArgumentException("Số trang phải là số dương.");
                }
                newBook.setNumberOfPages(numPages);
                
                newBook.setCoverType(request.getParameter("coverType"));
                newBook.setLanguage(request.getParameter("language"));

                ok = productDAO.insertBook(newBook);
            } else {
                Stationery stat = new Stationery();
                stat.setTitle(title.trim());
                stat.setDescription(description);
                stat.setSku(sku);
                stat.setImageUrl(imageUrl);
                stat.setIsActive(true);
                stat.setCategoryId(categoryId);
                stat.setPrice(price);

                stat.setBrandId(parseIntegerValue(request.getParameter("brandId")));
                stat.setSupplierId(parseIntegerValue(request.getParameter("supplierId")));
                stat.setOrigin(request.getParameter("origin"));
                stat.setMaterial(request.getParameter("material"));
                stat.setColor(request.getParameter("color"));
                
                // Validate Stationery-specific numerical constraints
                BigDecimal weight = parseBigDecimalValue(request.getParameter("weight"));
                String weightRaw = request.getParameter("weight");
                if (weightRaw != null && !weightRaw.trim().isEmpty() && (weight == null || weight.compareTo(BigDecimal.ZERO) < 0)) {
                    throw new IllegalArgumentException("Trọng lượng không hợp lệ.");
                }
                stat.setWeight(weight);
                
                stat.setDimensions(request.getParameter("dimensions"));
                stat.setSpecifications(request.getParameter("specifications"));
                stat.setWarning(request.getParameter("warning"));

                ok = productDAO.insertStationery(stat);
            }

            if (ok) {
                request.setAttribute("successMessage", "Thêm sản phẩm \"" + title.trim() + "\" thành công!");
                showList(request, response);
            } else {
                request.setAttribute("errorMessage", "Thêm sản phẩm thất bại, vui lòng thử lại.");
                request.setAttribute("productType", productType);
                setProductFormAttributes(request, productType);
                request.getRequestDispatcher("/WEB-INF/product/create.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            
            String pType = request.getParameter("productType") != null ? request.getParameter("productType") : "book";
            request.setAttribute("productType", pType);
            setProductFormAttributes(request, pType);
            request.getRequestDispatcher("/WEB-INF/product/create.jsp").forward(request, response);
        }
    }

    // =========================================================================
    // Process updating an existing product
    // =========================================================================
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String productIdParam = request.getParameter("productId");
            if (productIdParam == null || productIdParam.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Thiếu ID sản phẩm khi cập nhật.");
                showList(request, response);
                return;
            }
            int productId = Integer.parseInt(productIdParam);

            Product existing = productDAO.getProductById(productId);
            if (existing == null) {
                request.setAttribute("errorMessage", "Không tìm thấy sản phẩm để cập nhật.");
                showList(request, response);
                return;
            }

            String productType = request.getParameter("productType");
            if (productType == null) productType = (existing instanceof Book) ? "book" : "stationery";

            String title       = request.getParameter("title");
            String categoryStr = request.getParameter("categoryId");
            String priceStr    = request.getParameter("price");
            String sku         = request.getParameter("sku");
            String description = request.getParameter("description");

            if (title == null || title.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Tiêu đề không được để trống!");
                request.setAttribute("productType", productType);
                request.setAttribute("product", existing);
                setProductFormAttributes(request, productType);
                request.getRequestDispatcher("/WEB-INF/product/update.jsp").forward(request, response);
                return;
            }

            // Enforce alphanumeric formatting constraints for SKU
            if (sku == null || sku.trim().isEmpty() || !sku.matches("^[a-zA-Z0-9_\\-]+$")) {
                request.setAttribute("errorMessage", "SKU không hợp lệ (không được để trống, chỉ chứa chữ, số, gạch nối).");
                request.setAttribute("productType", productType);
                request.setAttribute("product", existing);
                setProductFormAttributes(request, productType);
                request.getRequestDispatcher("/WEB-INF/product/update.jsp").forward(request, response);
                return;
            }

            Part imagePart = request.getPart("image");
            String imageUrl = UploadUtils.saveUploadedFile(imagePart, getServletContext(), "book-image");
            if (imageUrl == null) {
                imageUrl = existing.getImageUrl();
            }

            Integer categoryId = parseIntegerValue(categoryStr);
            BigDecimal price = parseBigDecimalValue(priceStr);

            if (categoryId == null || !isValidPrice(price)) {
                request.setAttribute("errorMessage", "Danh mục hoặc giá tiền không hợp lệ.");
                request.setAttribute("productType", productType);
                request.setAttribute("product", existing);
                setProductFormAttributes(request, productType);
                request.getRequestDispatcher("/WEB-INF/product/update.jsp").forward(request, response);
                return;
            }

            boolean ok = false;

            if ("book".equals(productType)) {
                Book updatedBook = new Book();
                updatedBook.setId(productId);
                updatedBook.setTitle(title.trim());
                updatedBook.setDescription(description);
                updatedBook.setSku(sku);
                updatedBook.setImageUrl(imageUrl);
                updatedBook.setIsActive(existing.isIsActive());
                updatedBook.setCategoryId(categoryId);
                updatedBook.setPrice(price);

                updatedBook.setPublisherId(parseIntegerValue(request.getParameter("publisherId")));
                updatedBook.setTranslator(request.getParameter("translator"));

                Integer supplierId = parseIntegerValue(request.getParameter("supplierId"));
                updatedBook.setSupplierId(supplierId);

                String[] authorIdsParam = request.getParameterValues("authorIds");
                List<Integer> authorIds = new ArrayList<>();
                if (authorIdsParam != null) {
                    for (String idStr : authorIdsParam) {
                        try {
                            authorIds.add(Integer.parseInt(idStr));
                        } catch (NumberFormatException e) {
                            // ignore
                        }
                    }
                }
                updatedBook.setAuthorIds(authorIds);
                
                // Validate Book-specific numerical constraints
                Integer pubYear = parseIntegerValue(request.getParameter("publicationYear"));
                String pubYearRaw = request.getParameter("publicationYear");
                if (pubYearRaw != null && !pubYearRaw.trim().isEmpty() && !isValidPublicationYear(pubYear)) {
                    throw new IllegalArgumentException("Năm xuất bản không hợp lệ.");
                }
                updatedBook.setPublicationYear(pubYear);
                
                Integer numPages = parseIntegerValue(request.getParameter("numberOfPages"));
                String numPagesRaw = request.getParameter("numberOfPages");
                if (numPagesRaw != null && !numPagesRaw.trim().isEmpty() && !isValidPositiveInteger(numPages)) {
                    throw new IllegalArgumentException("Số trang phải là số dương.");
                }
                updatedBook.setNumberOfPages(numPages);
                
                updatedBook.setCoverType(request.getParameter("coverType"));
                updatedBook.setLanguage(request.getParameter("language"));

                ok = productDAO.updateBook(updatedBook);
            } else {
                Stationery stat = new Stationery();
                stat.setId(productId);
                stat.setTitle(title.trim());
                stat.setDescription(description);
                stat.setSku(sku);
                stat.setImageUrl(imageUrl);
                stat.setIsActive(existing.isIsActive());
                stat.setCategoryId(categoryId);
                stat.setPrice(price);

                stat.setBrandId(parseIntegerValue(request.getParameter("brandId")));
                stat.setSupplierId(parseIntegerValue(request.getParameter("supplierId")));
                stat.setOrigin(request.getParameter("origin"));
                stat.setMaterial(request.getParameter("material"));
                stat.setColor(request.getParameter("color"));
                
                // Validate Stationery-specific numerical constraints
                BigDecimal weight = parseBigDecimalValue(request.getParameter("weight"));
                String weightRaw = request.getParameter("weight");
                if (weightRaw != null && !weightRaw.trim().isEmpty() && (weight == null || weight.compareTo(BigDecimal.ZERO) < 0)) {
                    throw new IllegalArgumentException("Trọng lượng không hợp lệ.");
                }
                stat.setWeight(weight);
                
                stat.setDimensions(request.getParameter("dimensions"));
                stat.setSpecifications(request.getParameter("specifications"));
                stat.setWarning(request.getParameter("warning"));

                ok = productDAO.updateStationery(stat);
            }

            if (ok) {
                request.setAttribute("successMessage", "Cập nhật sản phẩm \"" + title.trim() + "\" thành công!");
                showList(request, response);
            } else {
                request.setAttribute("errorMessage", "Cập nhật thất bại, vui lòng thử lại.");
                request.setAttribute("productType", productType);
                request.setAttribute("product", existing);
                setProductFormAttributes(request, productType);
                request.getRequestDispatcher("/WEB-INF/product/update.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            
            String pType = request.getParameter("productType") != null ? request.getParameter("productType") : "book";
            request.setAttribute("productType", pType);
            try {
                int pid = Integer.parseInt(request.getParameter("productId"));
                request.setAttribute("product", productDAO.getProductById(pid));
            } catch(Exception ignored) {}
            setProductFormAttributes(request, pType);
            request.getRequestDispatcher("/WEB-INF/product/update.jsp").forward(request, response);
        }
    }

    // =========================================================================
    // Process product deletion (soft delete - sets isActive = 0)
    // =========================================================================
    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Step 1: Get productId from hidden input
            String productIdParam = request.getParameter("productId");
            if (productIdParam == null || productIdParam.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Thiếu ID sản phẩm khi xóa.");
                showList(request, response);
                return;
            }
            int productId = Integer.parseInt(productIdParam);

            // Step 2: Check if the product exists
            Product existing = productDAO.getProductById(productId);
            if (existing == null) {
                request.setAttribute("errorMessage", "Không tìm thấy sản phẩm để xóa.");
                showList(request, response);
                return;
            }

            // Step 3: Call DAO to soft-delete (sets isActive = 0, does not remove from DB)
            boolean ok = productDAO.deleteProduct(productId);

            // Step 4: Set success message and redirect back to the product list
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
