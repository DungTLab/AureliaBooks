<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <div class="row">
        <div class="col-md-4">
            <div class="card shadow-sm mb-4">
                <div class="card-body text-center">
                    <c:choose>
                        <c:when test="${not empty sessionScope.userProfile.avatarUrl}">
                            <c:choose>
                                <c:when test="${sessionScope.userProfile.avatarUrl.startsWith('http')}">
                                    <c:set var="avatarSrc" value="${sessionScope.userProfile.avatarUrl}" />
                                </c:when>
                                <c:otherwise>
                                    <c:set var="avatarSrc" value="${pageContext.request.contextPath}/uploads/${sessionScope.userProfile.avatarUrl}" />
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <c:set var="avatarSrc" value="https://cdn-icons-png.flaticon.com/512/149/149071.png" />
                        </c:otherwise>
                    </c:choose>
                    <img src="${avatarSrc}" class="rounded-circle img-fluid mb-3" style="width: 150px; height: 150px; object-fit: cover;" alt="Avatar">
                    
                    <form action="${pageContext.request.contextPath}/profile?action=updateAvatar" method="POST" enctype="multipart/form-data" class="mb-3">
                        <div class="input-group input-group-sm mb-2">
                            <input type="file" class="form-control" name="avatar" accept="image/*" required>
                        </div>
                        <button type="submit" class="btn btn-sm btn-outline-primary w-100">Thay ảnh đại diện</button>
                    </form>

                    <h5 class="my-2">${sessionScope.userProfile.fullName}</h5>
                    <p class="text-muted mb-1">Vai trò: ${sessionScope.user.roleName}</p>
                </div>
            </div>
        </div>
        <div class="col-md-8">
            <div class="card shadow-sm mb-4">
                <div class="card-body">
                    <h4 class="mb-4">Thông tin cá nhân</h4>

                    <c:if test="${not empty requestScope.profileError}">
                        <div class="alert alert-danger py-2" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i>${requestScope.profileError}
                        </div>
                    </c:if>
                    <c:if test="${not empty requestScope.profileSuccess}">
                        <div class="alert alert-success py-2" role="alert">
                            <i class="bi bi-check-circle-fill me-2"></i>${requestScope.profileSuccess}
                        </div>
                    </c:if>

                    <!-- Form thay đổi thông tin (Skeleton để Dev 1 triển khai) -->
                    <form action="${pageContext.request.contextPath}/profile?action=updateInfo" method="POST">
                        <div class="mb-3">
                            <label for="fullName" class="form-label">Họ và Tên</label>
                            <input type="text" class="form-control" id="fullName" name="fullName" value="${sessionScope.userProfile.fullName}" required pattern="^[A-Za-zÀ-ỹ\s]{2,100}$" title="Họ và tên chỉ được chứa chữ cái và khoảng trắng, độ dài từ 2 đến 100 ký tự">
                        </div>
                        <div class="mb-3">
                            <label for="phone" class="form-label">Số điện thoại</label>
                            <input type="text" class="form-control" id="phone" name="phone" value="${sessionScope.userProfile.phone}" pattern="^0[0-9]{9}$" title="Số điện thoại phải bắt đầu bằng số 0 và bao gồm đúng 10 chữ số">
                        </div>
                        <div class="mb-3">
                            <label for="address" class="form-label">Địa chỉ</label>
                            <textarea class="form-control" id="address" name="address">${sessionScope.userProfile.address}</textarea>
                        </div>
                        <button type="submit" class="btn btn-primary">Cập nhật thông tin</button>
                    </form>

                    <hr class="my-4">

                    <h4 class="mb-4">Đổi mật khẩu</h4>
                    
                    <c:if test="${not empty requestScope.passwordError}">
                        <div class="alert alert-danger py-2" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i>${requestScope.passwordError}
                        </div>
                    </c:if>
                    <c:if test="${not empty requestScope.passwordSuccess}">
                        <div class="alert alert-success py-2" role="alert">
                            <i class="bi bi-check-circle-fill me-2"></i>${requestScope.passwordSuccess}
                        </div>
                    </c:if>
                    <!-- Form đổi mật khẩu (Skeleton để Dev 1 triển khai) -->
                    <form action="${pageContext.request.contextPath}/profile?action=changePassword" method="POST">
                        <div class="mb-3">
                            <label for="oldPassword" class="form-label">Mật khẩu cũ</label>
                            <input type="password" class="form-control" id="oldPassword" name="oldPassword" required>
                        </div>
                        <div class="mb-3">
                            <label for="newPassword" class="form-label">Mật khẩu mới</label>
                            <input type="password" class="form-control" id="newPassword" name="newPassword" required pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,50}$" title="Mật khẩu phải từ 8 đến 50 ký tự, bao gồm ít nhất 1 chữ hoa, 1 chữ thường và 1 chữ số">
                        </div>
                        <button type="submit" class="btn btn-warning">Đổi mật khẩu</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
