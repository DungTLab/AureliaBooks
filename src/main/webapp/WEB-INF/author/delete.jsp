<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5" style="max-width: 500px;">
    <div class="card shadow-sm border-danger">
        <div class="card-header bg-danger text-white py-3">
            <h5 class="card-title mb-0">Xác Nhận Xóa Tác Giả</h5>
        </div>
        <div class="card-body p-4 text-center">
            <i class="bi bi-exclamation-triangle-fill text-danger fs-1 mb-3"></i>
            
            <p class="fs-5">
                Bạn có chắc chắn muốn xóa tác giả <strong class="text-danger">${author.fullName}</strong> khỏi hệ thống?
            </p>

            <c:if test="${not empty usageWarning}">
                <div class="alert alert-warning text-start" role="alert">
                    <i class="bi bi-info-circle-fill me-2"></i>${usageWarning}
                </div>
            </c:if>

            <p class="text-muted small">Hành động này không thể hoàn tác nếu được thực hiện.</p>

            <form action="${pageContext.request.contextPath}/admin/authors?action=delete" method="POST" class="mt-4">
                <input type="hidden" name="id" value="${author.authorId}">
                
                <div class="d-flex justify-content-center gap-3">
                    <a href="${pageContext.request.contextPath}/admin/authors" class="btn btn-secondary px-4 py-2">Hủy Bỏ</a>
                    <button type="submit" class="btn btn-danger px-4 py-2" ${isReferenced ? 'disabled' : ''}>
                        Đồng Ý Xóa
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
