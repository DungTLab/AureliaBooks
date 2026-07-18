<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5" style="max-width: 600px;">
    <div class="mb-4">
        <a href="${pageContext.request.contextPath}/admin/authors" class="btn btn-link p-0 text-decoration-none">
            <i class="bi bi-arrow-left me-1"></i>Quay lại danh sách
        </a>
    </div>

    <div class="card shadow-sm">
        <div class="card-header bg-success text-white py-3">
            <h5 class="card-title mb-0">Thêm Tác Giả Mới</h5>
        </div>
        <div class="card-body p-4">
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger" role="alert">
                    ${errorMessage}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/admin/authors?action=create" method="POST">
                <div class="mb-3">
                    <label for="fullName" class="form-label fw-bold">Họ tên <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="fullName" name="fullName" value="${author.fullName}" maxlength="255" pattern="^[A-Za-z0-9À-ỹ\s\-&()]{2,255}$" title="Họ tên chỉ được chứa chữ cái, số, khoảng trắng và các ký tự - & ( ) từ 2 đến 255 ký tự" required>
                </div>
                
                <div class="mb-3">
                    <label for="biography" class="form-label fw-bold">Tiểu sử</label>
                    <textarea class="form-control" id="biography" name="biography" rows="5" maxlength="2000">${author.biography}</textarea>
                </div>

                <div class="d-grid mt-4">
                    <button type="submit" class="btn btn-success py-2">
                        <i class="bi bi-save me-1"></i>Lưu Tác Giả
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
