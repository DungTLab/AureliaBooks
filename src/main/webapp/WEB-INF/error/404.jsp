<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- Include the application's shared header -->
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5 text-center">
    <div class="row justify-content-center align-items-center" style="min-height: 60vh;">
        <div class="col-md-8 col-lg-6">
            <div class="card shadow-lg border-0 rounded-4">
                <div class="card-body p-5">
                    <!-- 404 error warning icon -->
                    <div class="mb-4 text-warning">
                        <h1 class="display-1 fw-bold">404</h1>
                        <h2 class="h3 mt-3">Không Tìm Thấy Trang</h2>
                    </div>
                    
                    <p class="text-muted lead mb-4">
                        Xin lỗi, trang bạn đang tìm kiếm không tồn tại hoặc đã bị di dời.
                    </p>
                    
                    <!-- Block displaying detailed error cause (passed from the Controller or Container) -->
                    <c:if test="${not empty requestScope['jakarta.servlet.error.request_uri']}">
                         <div class="alert alert-secondary text-start border-secondary shadow-sm" role="alert">
                             <i class="fw-bold">Đường dẫn lỗi:</i> <br/>
                             <c:out value="${requestScope['jakarta.servlet.error.request_uri']}" />
                         </div>
                    </c:if>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-warning text-start border-warning shadow-sm" role="alert">
                            <i class="fw-bold">Chi tiết sự cố:</i> <br/>
                            <c:out value="${errorMessage}" />
                        </div>
                    </c:if>

                    <hr class="my-4 text-muted">

                    <!-- Navigation buttons -->
                    <div class="d-grid gap-2 d-sm-flex justify-content-sm-center">
                        <button type="button" class="btn btn-outline-secondary btn-lg px-4 gap-3" onclick="history.back()">
                            Quay Lại
                        </button>
                        <a href="${pageContext.request.contextPath}/" class="btn btn-primary btn-lg px-4">
                            Về Trang Chủ
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Include the application's shared footer -->
<jsp:include page="/WEB-INF/includes/footer.jsp" />
