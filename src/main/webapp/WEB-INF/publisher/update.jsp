<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5" style="max-width: 600px;">
    <div class="mb-4">
        <a href="${pageContext.request.contextPath}/admin/publishers" class="btn btn-link p-0 text-decoration-none">
            <i class="bi bi-arrow-left me-1"></i>Quay lại danh sách
        </a>
    </div>

    <div class="card shadow-sm">
        <div class="card-header bg-primary text-white py-3">
            <h5 class="card-title mb-0">Cập Nhật Nhà Xuất Bản</h5>
        </div>
        <div class="card-body p-4">
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger" role="alert">
                    ${errorMessage}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/admin/publishers?view=update" method="POST">
                <input type="hidden" name="id" value="${publisher.id}">
                <input type="hidden" name="action" value="update">
                <div class="mb-3">
                    <label for="name" class="form-label fw-bold">Tên nhà xuất bản <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="name" name="name" value="${publisher.name}" required>
                </div>
                
                <div class="mb-3">
                    <label for="address" class="form-label fw-bold">Địa chỉ</label>
                    <input type="text" class="form-control" id="address" name="address" value="${publisher.address}">
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
