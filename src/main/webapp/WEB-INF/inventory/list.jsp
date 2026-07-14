<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Quản Lý Tồn Kho (Admin - Inventory)</h2>
    </div>

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            ${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            ${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <div class="card shadow-sm">
        <div class="card-body p-0">
            <table class="table table-striped table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th style="width: 10%;">Mã SP</th>
                        <th style="width: 35%;">Tên Sản Phẩm / Sách</th>
                        <th style="width: 15%;">SKU</th>
                        <th style="width: 15%;">Số Lượng Tồn</th>
                        <th style="width: 15%;">Vị Trí Kho</th>
                        <th style="width: 10%;" class="text-end">Thao Tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${inventoryList}">
                        <tr>
                            <td>${item.productId}</td>
                            <td>
                                <strong>${item.productTitle}</strong>
                            </td>
                            <td><code class="text-secondary">${item.sku}</code></td>
                            <td>
                                <span class="badge ${item.quantityInStock > 5 ? 'bg-success' : (item.quantityInStock > 0 ? 'bg-warning text-dark' : 'bg-danger')}">
                                    ${item.quantityInStock}
                                </span>
                            </td>
                            <td>
                                <span class="text-muted small">
                                    <i class="bi bi-geo-alt me-1"></i>${item.warehouseLocation != null ? item.warehouseLocation : 'Chưa nhập kho'}
                                </span>
                            </td>
                            <td class="text-end">
                                <a href="${pageContext.request.contextPath}/admin/inventory?view=adjust&productId=${item.productId}" class="btn btn-sm btn-primary">
                                    <i class="bi bi-arrow-left-right me-1"></i>Điều chỉnh
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty inventoryList}">
                        <tr>
                            <td colspan="6" class="text-center py-4 text-muted">
                                Không tìm thấy sản phẩm nào trong kho.
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
