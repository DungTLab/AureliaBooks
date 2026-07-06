<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h3 class="card-title text-center mb-4">Đăng Nhập</h3>
                    <c:if test="${not empty requestScope.error}">
                        <div class="alert alert-danger py-2" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i>${requestScope.error}
                        </div>
                    </c:if>
                    <c:if test="${not empty requestScope.success}">
                        <div class="alert alert-success py-2" role="alert">
                            <i class="bi bi-check-circle-fill me-2"></i>${requestScope.success}
                        </div>
                    </c:if>
                    <!-- Form đăng nhập rỗng (Skeleton để Dev 1 triển khai) -->
                    <form action="${pageContext.request.contextPath}/auth?action=login" method="POST">
                        <div class="mb-3">
                            <label for="username" class="form-label">Tài khoản</label>
                            <input type="text" class="form-control" id="username" name="username" required>
                        </div>
                        <div class="mb-3">
                            <label for="password" class="form-label">Mật khẩu</label>
                            <input type="password" class="form-control" id="password" name="password" required>
                        </div>
                        <button type="submit" class="btn btn-primary w-100">Đăng Nhập</button>
                    </form>
                    
                    <div class="text-center my-3 text-secondary">Hoặc</div>
                    <a href="${pageContext.request.contextPath}/login-google" class="btn btn-outline-danger w-100">
                        <i class="bi bi-google me-2"></i> Đăng nhập bằng Google
                    </a>
                    
                    <div class="text-center mt-3">
                        Chưa có tài khoản? <a href="${pageContext.request.contextPath}/auth?action=register">Đăng ký ngay</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
