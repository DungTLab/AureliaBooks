<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5" style="max-width: 600px;">
    <div class="mb-4">
        <a href="${pageContext.request.contextPath}/admin/brands" class="btn btn-link p-0 text-decoration-none">
            <i class="bi bi-arrow-left me-1"></i>Quay lại danh sách
        </a>
    </div>

    <div class="card shadow-sm">
        <div class="card-header bg-primary text-white py-3">
            <h5 class="card-title mb-0">Cập Nhật Thương Hiệu</h5>
        </div>
        <div class="card-body p-4">
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger" role="alert">
                    ${errorMessage}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/admin/brands?view=update" method="POST">
                <input type="hidden" name="id" value="${brand.id}">

                <div class="mb-3">
                    <label for="name" class="form-label fw-bold">Tên thương hiệu <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="name" name="name" value="${brand.name}" required>
                </div>
                
                <div class="mb-3">
                    <label for="originCountry" class="form-label fw-bold">Quốc gia xuất xứ</label>
                    <input type="text" class="form-control" id="originCountry" name="originCountry" value="${brand.originCountry}">
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
