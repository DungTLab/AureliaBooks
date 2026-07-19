<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
        <jsp:include page="/WEB-INF/includes/header.jsp" />

        <div class="container my-5">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2>Quản Lý Sản Phẩm</h2>
                <div>
                    <a href="${pageContext.request.contextPath}/admin/products?view=create&type=book"
                        class="btn btn-success me-2">Thêm Sách</a>
                    <a href="${pageContext.request.contextPath}/admin/products?view=create&type=stationery"
                        class="btn btn-success">Thêm VPP</a>
                </div>
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

            <!-- Bảng danh sách sản phẩm -->
            <table class="table table-bordered table-striped align-middle">
                <thead>
                    <tr>
                        <th>Mã SP</th>
                        <th>Hình ảnh</th>
                        <th>Tên sản phẩm</th>
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
                                <c:choose>
                                    <c:when test="${not empty book.imageUrl && book.imageUrl.contains('/')}">
                                        <img src="${pageContext.request.contextPath}/uploads/${book.imageUrl}" class="img-thumbnail" style="width: 50px; height: 50px; object-fit: contain;" alt="${book.title}">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="${pageContext.request.contextPath}/assets/images/book-image/${book.imageUrl}" class="img-thumbnail" style="width: 50px; height: 50px; object-fit: contain;" alt="${book.title}">
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <strong>${book.title}</strong> <br>
                                <small class="text-muted">SKU: ${book.sku}</small>
                            </td>
                            <td>${book.categoryId}</td>
                            <td class="fw-semibold text-danger"><fmt:formatNumber value="${book.price}" type="number" groupingUsed="true" maxFractionDigits="0"/> đ</td>
                            <td>
                                <!-- Hiển thị tồn kho với màu sắc phù hợp -->
                                <span class="badge ${book.quantityInStock > 5 ? 'bg-success' : (book.quantityInStock > 0 ? 'bg-warning text-dark' : 'bg-danger')}">
                                    ${book.quantityInStock}
                                </span>
                            </td>
                            <td>
                                <span class="badge ${book.isActive ? 'bg-success' : 'bg-danger'}">
                                    ${book.isActive ? 'Đang bán' : 'Ngừng bán'}
                                </span>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/admin/products?view=update&productId=${book.id}"
                                    class="btn btn-sm btn-primary">Sửa</a>
                                <a href="${pageContext.request.contextPath}/admin/products?view=delete&productId=${book.id}"
                                    class="btn btn-sm btn-danger ms-1">Xóa</a>
                                <a href="${pageContext.request.contextPath}/admin/inventory?view=adjust&productId=${book.id}"
                                    class="btn btn-sm btn-outline-secondary ms-1" title="Điều chỉnh kho">
                                    <i class="bi bi-boxes"></i> Kho
                                </a>
                            </td>
                        </tr>

                    </c:forEach>
                </tbody>
            </table>

            <!-- Phân trang -->
            <c:if test="${totalPages > 1}">
                <nav aria-label="Product pagination" class="d-flex justify-content-center mt-4">
                    <ul class="pagination">
                        <!-- Nút Trước -->
                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/admin/products?page=${currentPage - 1}" aria-label="Previous">
                                <span aria-hidden="true">&laquo;</span>
                            </a>
                        </li>

                        <!-- Số trang -->
                        <c:forEach var="i" begin="1" end="${totalPages}">
                            <li class="page-item ${i == currentPage ? 'active' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/admin/products?page=${i}">${i}</a>
                            </li>
                        </c:forEach>

                        <!-- Nút Sau -->
                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/admin/products?page=${currentPage + 1}" aria-label="Next">
                                <span aria-hidden="true">&raquo;</span>
                            </a>
                        </li>
                    </ul>
                </nav>
            </c:if>

        </div>

        <jsp:include page="/WEB-INF/includes/footer.jsp" />