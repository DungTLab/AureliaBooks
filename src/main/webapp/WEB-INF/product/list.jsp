<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Quản Lý Kho Sách (Admin - TPT)</h2>
        <a href="${pageContext.request.contextPath}/admin/products?view=create" class="btn btn-success">Thêm Sách Mới</a>
    </div>

    <!-- Thông báo kết quả -->
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <!-- Bảng danh sách sách phía Admin (Skeleton để Dev 3 liên kết CSDL) -->
    <table class="table table-bordered table-striped align-middle">
        <thead>
            <tr>
                <th>Mã SP</th>
                <th>Hình ảnh</th>
                <th>Tiêu đề sách</th>
                <th>Danh mục</th>
                <th>Giá bán</th>
                <th>Số lượng tồn</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="book" items="${listBooks}">
                <tr>
                    <td>${book.id}</td>
                    <td>
                        <img src="${pageContext.request.contextPath}/uploads/${book.imageUrl}"
                             onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/assets/images/book-image/${book.imageUrl}'"
                             class="img-thumbnail" style="width: 50px;" alt="...">
                    </td>
                    <td>
                        <strong>${book.title}</strong> <br>
                        <small class="text-muted">SKU: ${book.sku}</small>
                    </td>
                    <td>${book.categoryId}</td>
                    <td>${book.price} VNĐ</td>
                    <td>
                        <!-- Đọc tồn kho từ bảng Inventory tương ứng -->
                        <span class="badge bg-secondary">${book.quantityInStock != null ? book.quantityInStock : 0}</span>
                    </td>
                    <td>
                        <span class="badge ${book.isActive ? 'bg-success' : 'bg-danger'}">
                            ${book.isActive ? 'Đang bán' : 'Ngừng bán'}
                        </span>
                    </td>
                    <td>
                        <a href="${pageContext.request.contextPath}/admin/products?view=update&productId=${book.id}" class="btn btn-sm btn-primary">Sửa</a>
                        <a href="${pageContext.request.contextPath}/admin/products?view=delete&productId=${book.id}" class="btn btn-sm btn-danger ms-1">Xóa</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />