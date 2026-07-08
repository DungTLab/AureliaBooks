<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Quản lý Mã Giảm Giá (Vouchers)</h2>
        <a href="${pageContext.request.contextPath}/admin/discounts?action=create" class="btn btn-primary">Thêm Mã Giảm Giá Mới</a>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger p-2 mb-4">${errorMessage}</div>
    </c:if>

    <div class="table-responsive">
        <table class="table table-bordered table-hover align-middle shadow-sm">
            <thead class="table-light">
                <tr>
                    <th>ID</th>
                    <th>Mã Voucher</th>
                    <th>Phần trăm giảm</th>
                    <th>Giảm tối đa</th>
                    <th>Đơn tối thiểu</th>
                    <th>Thời hạn</th>
                    <th>Trạng thái</th>
                    <th>Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty discounts}">
                        <tr><td colspan="8" class="text-center">Chưa có mã giảm giá nào.</td></tr>
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
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/discounts?action=update&id=${discount.id}" class="btn btn-sm btn-outline-primary">Sửa</a>
                                    <form action="${pageContext.request.contextPath}/admin/discounts" method="POST" class="d-inline" onsubmit="return confirm('Bạn có chắc chắn muốn xóa mã giảm giá này không?');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="${discount.id}">
                                        <button type="submit" class="btn btn-sm btn-outline-danger">Xóa</button>
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

<jsp:include page="/WEB-INF/includes/footer.jsp" />
