<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Quản Lý Mã Giảm Giá (Vouchers)</h2>
        <a href="${pageContext.request.contextPath}/admin/discounts?action=create" class="btn btn-success">
            <i class="bi bi-plus-lg me-1"></i>Thêm Mã Giảm Giá Mới
        </a>
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
                        <th style="width: 5%;">ID</th>
                        <th style="width: 15%;">Mã Voucher</th>
                        <th style="width: 15%;">Phần Trăm Giảm</th>
                        <th style="width: 15%;">Giảm Tối Đa</th>
                        <th style="width: 15%;">Đơn Tối Thiểu</th>
                        <th style="width: 15%;">Thời Hạn</th>
                        <th style="width: 10%;">Trạng Thái</th>
                        <th style="width: 10%;" class="text-end text-nowrap">Thao Tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty discounts}">
                            <tr>
                                <td colspan="8" class="text-center py-4 text-muted">
                                    Không tìm thấy mã giảm giá nào trong hệ thống.
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="discount" items="${discounts}">
                                <tr>
                                    <td>${discount.id}</td>
                                    <td><span class="badge bg-secondary fs-6">${discount.code}</span></td>
                                    <td>${discount.discountPercent}%</td>
                                    <td>${discount.maxDiscountAmount} VNĐ</td>
                                    <td>${discount.minOrderValue} VNĐ</td>
                                    <td>
                                        <small>Từ: ${discount.startDate}</small><br>
                                        <small>Đến: ${discount.endDate}</small>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${discount.isActive}">
                                                <span class="badge bg-success">Hoạt động</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger">Khóa</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-end text-nowrap">
                                        <a href="${pageContext.request.contextPath}/admin/discounts?action=update&id=${discount.id}" class="btn btn-sm btn-outline-primary">
                                            <i class="bi bi-pencil me-1"></i>Sửa
                                        </a>
                                        <form action="${pageContext.request.contextPath}/admin/discounts" method="POST" class="d-inline" onsubmit="return confirm('Bạn có chắc chắn muốn xóa mã giảm giá này không?');">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="id" value="${discount.id}">
                                            <button type="submit" class="btn btn-sm btn-outline-danger ms-1">
                                                <i class="bi bi-trash me-1"></i>Xóa
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
