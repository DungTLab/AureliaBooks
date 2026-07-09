<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="mb-1"><i class="bi bi-people-fill text-danger me-2"></i>Quản lý tài khoản người dùng</h2>
            <p class="text-muted mb-0">Cấp quyền, quản lý vai trò và đóng/mở tài khoản nhân viên & khách hàng.</p>
        </div>
    </div>

    <!-- Bộ lọc & Tìm kiếm -->
    <div class="card shadow-sm border-0 mb-4 bg-light">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/admin/users" method="GET" class="row g-3 align-items-end">
                <div class="col-md-5">
                    <label for="search" class="form-label fw-bold text-secondary small">Tìm kiếm tài khoản (Username / ID)</label>
                    <div class="input-group">
                        <span class="input-group-text bg-white border-end-0 text-muted"><i class="bi bi-search"></i></span>
                        <input type="text" class="form-control border-start-0 ps-0" id="search" name="search" placeholder="Nhập tên tài khoản hoặc ID cần tìm..." value="${fn:escapeXml(search)}" />
                    </div>
                </div>
                <div class="col-md-4">
                    <label for="roleId" class="form-label fw-bold text-secondary small">Lọc theo vai trò</label>
                    <select class="form-select" id="roleId" name="roleId">
                        <option value="0">Tất cả vai trò</option>
                        <c:forEach var="r" items="${roles}">
                            <option value="${r.id}" ${roleId == r.id ? 'selected' : ''}>${r.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-3 d-flex gap-2">
                    <button type="submit" class="btn btn-danger w-100"><i class="bi bi-funnel-fill me-1"></i> Lọc</button>
                    <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline-secondary w-100"><i class="bi bi-arrow-counterclockwise me-1"></i> Xóa bộ lọc</a>
                </div>
            </form>
        </div>
    </div>

    <!-- Thông báo kết quả -->
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <div class="card shadow-sm border-0">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th class="ps-4" style="width: 70px;">ID</th>
                            <th>Tài khoản</th>
                            <th>Họ và tên</th>
                            <th>Liên hệ / Xác thực</th>
                            <th>Ngày tạo</th>
                            <th>Vai trò</th>
                            <th class="text-center" style="width: 120px;">Trạng thái</th>
                            <th class="pe-4 text-center" style="width: 250px;">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty users}">
                                <tr>
                                    <td colspan="8" class="text-center text-muted py-5">
                                        <i class="bi bi-people fs-1 d-block mb-2"></i>
                                        Không tìm thấy người dùng nào phù hợp.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="u" items="${users}">
                                    <tr>
                                        <td class="ps-4 text-secondary fw-semibold">${u.id}</td>
                                        <td>
                                            <div class="d-flex align-items-center">
                                                <div class="avatar-circle-sm bg-danger text-white rounded-circle d-flex align-items-center justify-content-center me-2" style="width: 32px; height: 32px; font-size: 14px; font-weight: bold;">
                                                    ${u.username.substring(0, 1).toUpperCase()}
                                                </div>
                                                <div>
                                                    <span class="fw-bold text-dark d-block">${u.username}</span>
                                                    <span class="badge bg-light text-secondary border fs-8">${u.authProvider}</span>
                                                </div>
                                            </div>
                                        </td>
                                        <td>
                                            <span class="text-dark fw-medium">${not empty u.fullName ? u.fullName : '<span class="text-muted small">Chưa cập nhật</span>'}</span>
                                        </td>
                                        <td>
                                            <span class="small d-block text-secondary"><i class="bi bi-envelope me-1"></i>${u.email}</span>
                                            <c:if test="${not empty u.phone}">
                                                <span class="small d-block text-secondary"><i class="bi bi-telephone me-1"></i>${u.phone}</span>
                                            </c:if>
                                        </td>
                                        <td>
                                            <span class="small text-secondary">
                                                <fmt:formatDate value="${u.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                            </span>
                                        </td>
                                        <td>
                                            <!-- Form cập nhật quyền hạn -->
                                            <form action="${pageContext.request.contextPath}/admin/users?action=updateRole" method="POST" class="d-flex align-items-center gap-1">
                                                <input type="hidden" name="userId" value="${u.id}" />
                                                <select name="roleId" class="form-select form-select-sm" style="max-width: 140px;" ${u.roleName eq 'ADMIN' and u.id == sessionScope.user.id ? 'disabled' : ''}>
                                                    <c:forEach var="r" items="${roles}">
                                                        <option value="${r.id}" ${u.roleId == r.id ? 'selected' : ''}>${r.name}</option>
                                                    </c:forEach>
                                                </select>
                                                <button type="submit" class="btn btn-sm btn-outline-primary py-1 px-2" ${u.roleName eq 'ADMIN' and u.id == sessionScope.user.id ? 'disabled' : ''}>
                                                    Lưu
                                                </button>
                                            </form>
                                        </td>
                                        <td class="text-center">
                                            <c:choose>
                                                <c:when test="${u.isActive}">
                                                    <span class="badge bg-success-subtle text-success border border-success-subtle px-2 py-1 rounded-pill">Hoạt động</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-danger-subtle text-danger border border-danger-subtle px-2 py-1 rounded-pill">Đã khóa</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="pe-4 text-center">
                                            <!-- Form khóa/mở khóa mềm tài khoản -->
                                            <form action="${pageContext.request.contextPath}/admin/users?action=toggleStatus" method="POST" class="d-inline">
                                                <input type="hidden" name="userId" value="${u.id}" />
                                                <input type="hidden" name="active" value="${not u.isActive}" />
                                                <c:choose>
                                                    <c:when test="${u.roleName eq 'ADMIN'}">
                                                        <button type="button" class="btn btn-sm btn-secondary py-1 px-3" disabled title="Không thể khóa tài khoản có quyền Admin">
                                                            <i class="bi bi-lock-fill me-1"></i> Khóa
                                                        </button>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:choose>
                                                            <c:when test="${u.isActive}">
                                                                <button type="submit" class="btn btn-sm btn-danger py-1 px-3" onclick="return confirm('Bạn có chắc chắn muốn khóa tài khoản [${u.username}]?');">
                                                                    <i class="bi bi-lock me-1"></i> Khóa
                                                                </button>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <button type="submit" class="btn btn-sm btn-success py-1 px-3" onclick="return confirm('Bạn có chắc chắn muốn mở khóa tài khoản [${u.username}]?');">
                                                                    <i class="bi bi-unlock me-1"></i> Mở
                                                                </button>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:otherwise>
                                                </c:choose>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Phân trang (Pagination) -->
    <c:if test="${totalPages > 1}">
        <nav aria-label="Page navigation" class="mt-4">
            <ul class="pagination justify-content-center shadow-sm">
                <!-- Nút trang trước -->
                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                    <a class="page-link text-danger" href="${pageContext.request.contextPath}/admin/users?page=${currentPage - 1}&search=${fn:escapeXml(search)}&roleId=${roleId}" aria-label="Previous">
                        <span aria-hidden="true">&laquo; Trước</span>
                    </a>
                </li>

                <!-- Các nút số trang -->
                <c:forEach var="i" begin="1" end="${totalPages}">
                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                        <a class="page-link ${currentPage == i ? 'bg-danger border-danger text-white' : 'text-danger'}" href="${pageContext.request.contextPath}/admin/users?page=${i}&search=${fn:escapeXml(search)}&roleId=${roleId}">${i}</a>
                    </li>
                </c:forEach>

                <!-- Nút trang sau -->
                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                    <a class="page-link text-danger" href="${pageContext.request.contextPath}/admin/users?page=${currentPage + 1}&search=${fn:escapeXml(search)}&roleId=${roleId}" aria-label="Next">
                        <span aria-hidden="true">Sau &raquo;</span>
                    </a>
                </li>
            </ul>
        </nav>
    </c:if>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
