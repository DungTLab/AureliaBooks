<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Quản Lý Danh Mục (Admin)</h2>
        <a href="${pageContext.request.contextPath}/admin/categories?action=create" class="btn btn-success">Thêm danh mục mới</a>
    </div>

    <!-- Danh sách danh mục (Skeleton để Dev 2 triển khai) -->
    <table class="table table-bordered table-striped">
        <thead>
            <tr>
                <th>ID</th>
                <th>Tên Danh Mục</th>
                <th>Danh Mục Cha ID</th>
                <th>Thao tác</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="cat" items="${categories}">
                <tr>
                    <td>${cat.id}</td>
                    <td>${cat.name}</td>
                    <td>${cat.parentId}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/admin/categories?action=update&id=${cat.id}" class="btn btn-sm btn-primary">Sửa</a>
                        <form action="${pageContext.request.contextPath}/admin/categories?action=delete&id=${cat.id}" method="POST" style="display:inline-block;">
                            <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Bạn chắc chắn muốn xóa?')">Xóa</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
