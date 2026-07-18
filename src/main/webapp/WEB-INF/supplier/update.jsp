<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5" style="max-width: 600px;">
    <div class="mb-4">
        <a href="${pageContext.request.contextPath}/admin/suppliers" class="btn btn-link p-0 text-decoration-none">
            <i class="bi bi-arrow-left me-1"></i>Quay lại danh sách
        </a>
    </div>

    <div class="card shadow-sm">
        <div class="card-header bg-primary text-white py-3">
            <h5 class="card-title mb-0">Cập Nhật Nhà Cung Cấp</h5>
        </div>
        <div class="card-body p-4">
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger" role="alert">
                    ${errorMessage}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/admin/suppliers?action=update" method="POST">
                <input type="hidden" name="id" value="${supplier.id}">

                <div class="mb-3">
                    <label for="name" class="form-label fw-bold">Tên nhà cung cấp <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="name" name="name" value="<c:out value='${supplier.name}' />" minlength="2" maxlength="255" required>
                </div>
                
                <div class="mb-3">
                    <label for="contactEmail" class="form-label fw-bold">Email liên hệ</label>
                    <input type="email" class="form-control" id="contactEmail" name="contactEmail" value="<c:out value='${supplier.contactEmail}' />" maxlength="100">
                </div>

                <div class="mb-3">
                    <label for="contactPhone" class="form-label fw-bold">Số điện thoại</label>
                    <input type="tel" class="form-control" id="contactPhone" name="contactPhone" value="<c:out value='${supplier.contactPhone}' />" minlength="7" maxlength="20" pattern="[0-9+() .-]+" title="Chỉ nhập chữ số và các ký tự + ( ) khoảng trắng . -">
                </div>

                <div class="mb-3">
                    <label for="address" class="form-label fw-bold">Địa chỉ</label>
                    <textarea class="form-control" id="address" name="address" rows="3" maxlength="1000"><c:out value="${supplier.address}" /></textarea>
                </div>

                <div class="d-grid mt-4">
                    <button type="submit" class="btn btn-primary py-2">
                        <i class="bi bi-save me-1"></i>Lưu Thay Đổi
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
