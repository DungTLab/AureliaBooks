<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="mb-1">Quản lý danh mục</h2>
            <p class="text-muted mb-0">Danh sách danh mục sách trong hệ thống.</p>
        </div>
        <a href="${pageContext.request.contextPath}/admin/categories?action=create" class="btn btn-success">
            Thêm danh mục
        </a>
    </div>

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success">${successMessage}</div>
    </c:if>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">${errorMessage}</div>
    </c:if>

    <div class="table-responsive">
        <table class="table table-bordered table-striped align-middle">
            <thead class="table-light">
                <tr>
                    <th style="width: 80px;">ID</th>
                    <th>Tên danh mục</th>
                    <th>Danh mục cha</th>
                    <th style="width: 180px;">Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty categories}">
                        <tr>
                            <td colspan="4" class="text-center text-muted py-4">
                                Chưa có danh mục nào.
                            </td>
                        </tr>
                    </c:when>

                    <c:otherwise>
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
                                <td>
                                    <a
                                        href="${pageContext.request.contextPath}/admin/categories?action=update&id=${cat.id}"
                                        class="btn btn-sm btn-primary">
                                        Sửa
                                    </a>

                                    <form
                                        action="${pageContext.request.contextPath}/admin/categories?action=delete&id=${cat.id}"
                                        method="POST"
                                        class="d-inline">
                                        <button
                                            type="submit"
                                            class="btn btn-sm btn-danger"
                                            onclick="return confirm('Bạn có chắc muốn xóa danh mục này?');">
                                            Xóa
                                        </button>
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

<jsp:include page="/WEB-INF/includes/footer.jsp" />
