<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Quản Lý Danh Mục (Admin)</h2>
        <a href="${pageContext.request.contextPath}/admin/categories?action=create" class="btn btn-success">
            <i class="bi bi-plus-lg me-1"></i>Thêm Danh Mục Mới
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
                        <th style="width: 10%;">ID</th>
                        <th style="width: 40%;">Tên Danh Mục</th>
                        <th style="width: 30%;">Danh Mục Cha</th>
                        <th style="width: 20%;" class="text-end text-nowrap">Thao Tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="cat" items="${categories}">
                        <tr>
                            <td>${cat.id}</td>
                            <td>
                                <strong>${cat.name}</strong>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${empty cat.parentId}">
                                        <span class="badge bg-secondary">Danh mục cấp 1</span>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="parentName" value="" />
                                        <c:forEach var="parent" items="${categories}">
                                            <c:if test="${parent.id == cat.parentId}">
                                                <c:set var="parentName" value="${parent.name}" />
                                            </c:if>
                                        </c:forEach>

                                        <c:choose>
                                            <c:when test="${not empty parentName}">
                                                ${parentName}
                                            </c:when>
                                            <c:otherwise>
                                                ID: ${cat.parentId}
                                            </c:otherwise>
                                        </c:choose>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-end text-nowrap">
                                <a href="${pageContext.request.contextPath}/admin/categories?action=update&id=${cat.id}" class="btn btn-sm btn-outline-primary">
                                    <i class="bi bi-pencil me-1"></i>Sửa
                                </a>

                                <form action="${pageContext.request.contextPath}/admin/categories?action=delete&id=${cat.id}" method="POST" class="d-inline">
                                    <button type="submit" class="btn btn-sm btn-outline-danger ms-1" onclick="return confirm('Bạn có chắc muốn xóa danh mục này?');">
                                        <i class="bi bi-trash me-1"></i>Xóa
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty categories}">
                        <tr>
                            <td colspan="4" class="text-center py-4 text-muted">
                                Không tìm thấy danh mục nào trong hệ thống.
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
