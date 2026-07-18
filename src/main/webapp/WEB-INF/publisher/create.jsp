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
        <div class="card-header bg-success text-white py-3">
            <h5 class="card-title mb-0">Thêm Nhà Xuất Bản Mới</h5>
        </div>
        <div class="card-body p-4">
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger" role="alert">
                    ${errorMessage}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/admin/publishers?action=create" method="POST">
                <div class="mb-3">
                    <label for="name" class="form-label fw-bold">Tên nhà xuất bản <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="name" name="name" value="${publisher.name}" maxlength="255" pattern="^[A-Za-z0-9À-ỹ\s\-&()]{2,255}$" title="Tên nhà xuất bản chỉ được chứa chữ cái, số, khoảng trắng và các ký tự - & ( ) từ 2 đến 255 ký tự" required>
                </div>
                
                <div class="mb-3">
                    <label for="address" class="form-label fw-bold">Địa chỉ</label>
                    <input type="text" class="form-control" id="address" name="address" value="${publisher.address}" maxlength="500">
                </div>

                <div class="d-grid mt-4">
                    <button type="submit" class="btn btn-success py-2">
                        <i class="bi bi-save me-1"></i>Lưu Nhà Xuất Bản
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
