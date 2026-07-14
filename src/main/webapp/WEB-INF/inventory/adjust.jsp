<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5" style="max-width: 600px;">
    <div class="mb-4">
        <a href="${pageContext.request.contextPath}/admin/inventory" class="btn btn-link p-0 text-decoration-none">
            <i class="bi bi-arrow-left me-1"></i>Quay lại danh sách kho
        </a>
    </div>

    <div class="card shadow-sm">
        <div class="card-header bg-primary text-white py-3">
            <h5 class="card-title mb-0">Điều Chỉnh Tồn Kho Sản Phẩm</h5>
        </div>
        <div class="card-body p-4">
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger" role="alert">
                    ${errorMessage}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/admin/inventory?view=adjust" method="POST">
                <input type="hidden" name="productId" value="${product.id}">

                <div class="mb-3">
                    <label class="form-label fw-bold text-muted">Tên sản phẩm</label>
                    <input type="text" class="form-control bg-light" value="${product.title}" readonly>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-bold text-muted">SKU</label>
                    <input type="text" class="form-control bg-light" value="${product.sku}" readonly>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-bold text-muted">Số lượng tồn hiện tại</label>
                    <input type="text" class="form-control bg-light fw-bold text-success" value="${currentStock != null ? currentStock : 0}" readonly>
                </div>
                
                <div class="mb-3">
                    <label for="quantityChange" class="form-label fw-bold">Số lượng thay đổi <span class="text-danger">*</span></label>
                    <input type="number" class="form-control" id="quantityChange" name="quantityChange" required>
                    <div class="form-text text-muted">Nhập số dương để **nhập thêm hàng**, số âm để **xuất/trả hàng** (Ví dụ: 10 hoặc -5).</div>
                </div>

                <div class="mb-3">
                    <label for="warehouseLocation" class="form-label fw-bold">Vị trí kho hàng</label>
                    <input type="text" class="form-control" id="warehouseLocation" name="warehouseLocation" value="${warehouseLocation}">
                    <div class="form-text text-muted">Khu vực lưu trữ (Ví dụ: Kệ A-1, Khu B...).</div>
                </div>

                <div class="d-grid mt-4">
                    <button type="submit" class="btn btn-primary py-2">
                        <i class="bi bi-check-lg me-1"></i>Xác Nhận Thay Đổi
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
