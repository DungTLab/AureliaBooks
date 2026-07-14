<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Quản Lý Nhà Xuất Bản (Admin)</h2>
        <a href="${pageContext.request.contextPath}/admin/publishers?view=create" class="btn btn-success">
            <i class="bi bi-plus-lg me-1"></i>Thêm Nhà Xuất Bản Mới
        </a>
    </div>

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            ${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            ${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <div class="card shadow-sm">
        <div class="card-body p-0">
            <table class="table table-striped table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th style="width: 10%;">Mã ID</th>
                        <th style="width: 30%;">Tên Nhà Xuất Bản</th>
                        <th style="width: 40%;">Địa Chỉ</th>
                        <th style="width: 20%;" class="text-end">Thao Tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="pub" items="${publishers}">
                        <tr>
                            <td>${pub.id}</td>
                            <td><strong>${pub.name}</strong></td>
                            <td>${pub.address}</td>
                            <td class="text-end">
                                <a href="${pageContext.request.contextPath}/admin/publishers?view=update&id=${pub.id}" class="btn btn-sm btn-outline-primary">
                                    <i class="bi bi-pencil me-1"></i>Sửa
                                </a>
                                <a href="${pageContext.request.contextPath}/admin/publishers?view=delete&id=${pub.id}" class="btn btn-sm btn-outline-danger ms-1">
                                    <i class="bi bi-trash me-1"></i>Xóa
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty publishers}">
                        <tr>
                            <td colspan="4" class="text-center py-4 text-muted">
                                Không tìm thấy nhà xuất bản nào trong hệ thống.
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
