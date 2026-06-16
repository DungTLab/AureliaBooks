<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <div class="row">
        <div class="col-md-4">
            <div class="card shadow-sm mb-4">
                <div class="card-body text-center">
                    <img src="${sessionScope.userProfile.avatarUrl != null ? sessionScope.userProfile.avatarUrl : 'assets/images/default-avatar.png'}" class="rounded-circle img-fluid" style="width: 150px;" alt="Avatar">
                    <h5 class="my-3">${sessionScope.userProfile.fullName}</h5>
                    <p class="text-muted mb-1">Vai trò: ${sessionScope.user.roleName}</p>
                </div>
            </div>
        </div>
        <div class="col-md-8">
            <div class="card shadow-sm mb-4">
                <div class="card-body">
                    <h4 class="mb-4">Thông tin cá nhân</h4>
                    <!-- Form thay đổi thông tin (Skeleton để Dev 1 triển khai) -->
                    <form action="${pageContext.request.contextPath}/profile?action=updateInfo" method="POST">
                        <div class="mb-3">
                            <label for="fullName" class="form-label">Họ và Tên</label>
                            <input type="text" class="form-control" id="fullName" name="fullName" value="${sessionScope.userProfile.fullName}">
                        </div>
                        <div class="mb-3">
                            <label for="phone" class="form-label">Số điện thoại</label>
                            <input type="text" class="form-control" id="phone" name="phone" value="${sessionScope.userProfile.phone}">
                        </div>
                        <div class="mb-3">
                            <label for="address" class="form-label">Địa chỉ</label>
                            <textarea class="form-control" id="address" name="address">${sessionScope.userProfile.address}</textarea>
                        </div>
                        <button type="submit" class="btn btn-primary">Cập nhật thông tin</button>
                    </form>
                    
                    <hr class="my-4">
                    
                    <h4 class="mb-4">Đổi mật khẩu</h4>
                    <!-- Form đổi mật khẩu (Skeleton để Dev 1 triển khai) -->
                    <form action="${pageContext.request.contextPath}/profile?action=changePassword" method="POST">
                        <div class="mb-3">
                            <label for="oldPassword" class="form-label">Mật khẩu cũ</label>
                            <input type="password" class="form-control" id="oldPassword" name="oldPassword" required>
                        </div>
                        <div class="mb-3">
                            <label for="newPassword" class="form-label">Mật khẩu mới</label>
                            <input type="password" class="form-control" id="newPassword" name="newPassword" required>
                        </div>
                        <button type="submit" class="btn btn-warning">Đổi mật khẩu</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
