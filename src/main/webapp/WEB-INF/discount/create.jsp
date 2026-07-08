<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card shadow-sm">
                <div class="card-header bg-white d-flex justify-content-between align-items-center">
                    <h4 class="mb-0">Thêm Mới Mã Giảm Giá</h4>
                    <a href="${pageContext.request.contextPath}/admin/discounts" class="btn btn-sm btn-outline-secondary">Quay lại</a>
                </div>
                <div class="card-body">
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger p-2 mb-4">${errorMessage}</div>
                    </c:if>

                    <div class="alert alert-warning p-2 mb-4">
                        <strong>Lưu ý:</strong> Mã giảm giá mới phải được lên lịch trước (Thời gian bắt đầu phải lớn hơn thời gian hiện tại) và không vượt quá 7 ngày tới.
                    </div>

                    <form action="${pageContext.request.contextPath}/admin/discounts?action=create" method="POST">
                        <div class="mb-3">
                            <label class="form-label fw-bold">Mã Voucher (Code) <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="code" required placeholder="VD: SUMMER2026" value="${discount.code}">
                        </div>
                        
                        <div class="row">
                            <div class="col-md-4 mb-3">
                                <label class="form-label fw-bold">Phần trăm giảm (%) <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" name="discountPercent" required min="1" max="20" step="0.01" value="${discount.discountPercent}">
                                <small class="text-muted">Từ 1% đến 20%</small>
                            </div>
                            <div class="col-md-4 mb-3">
                                <label class="form-label fw-bold">Giảm tối đa (VNĐ) <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" name="maxDiscountAmount" required min="20000" step="1000" value="${discount.maxDiscountAmount}">
                                <small class="text-muted">Từ Đơn tối thiểu đến 2x Đơn tối thiểu</small>
                            </div>
                            <div class="col-md-4 mb-3">
                                <label class="form-label fw-bold">Đơn tối thiểu (VNĐ) <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" name="minOrderValue" required min="20000" step="1000" value="${discount.minOrderValue}">
                                <small class="text-muted">Tối thiểu 20,000 VNĐ</small>
                            </div>
                        </div>

                        <c:if test="${not empty discount.startDate}">
                            <fmt:formatDate value="${discount.startDate}" pattern="yyyy-MM-dd'T'HH:mm" var="formattedStartDate" />
                        </c:if>
                        <c:if test="${not empty discount.endDate}">
                            <fmt:formatDate value="${discount.endDate}" pattern="yyyy-MM-dd'T'HH:mm" var="formattedEndDate" />
                        </c:if>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label fw-bold">Thời gian bắt đầu <span class="text-danger">*</span></label>
                                <input type="datetime-local" class="form-control" name="startDate" required value="${formattedStartDate}">
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label fw-bold">Thời gian kết thúc <span class="text-danger">*</span></label>
                                <input type="datetime-local" class="form-control" name="endDate" required value="${formattedEndDate}">
                            </div>
                        </div>

                        <div class="d-grid mt-4">
                            <button type="submit" class="btn btn-primary">Lưu Mã Giảm Giá</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
