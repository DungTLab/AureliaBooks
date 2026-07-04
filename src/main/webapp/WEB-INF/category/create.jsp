<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <h2>Thêm Danh Mục Mới</h2>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">${errorMessage}</div>
    </c:if>
    <div class="row">
        <div class="col-md-6">
            <form action="${pageContext.request.contextPath}/admin/categories?action=create" method="POST">
                <div class="mb-3">
                    <label for="name" class="form-label">Tên danh mục</label>
                    <input type="text" class="form-control" id="name" name="name"
                           value="${category.name}" required>
                </div>
                <div class="mb-3">
                    <label for="parentId" class="form-label">Danh mục cha</label>
                    <select class="form-select" id="parentId" name="parentId">
                        <option value="">Không có (Danh mục cấp 1)</option>
                        <c:forEach var="cat" items="${parentCategories}">
                            <option value="${cat.id}"
                                    ${category.parentId == cat.id ? 'selected' : ''}>
                                ${cat.name}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <button type="submit" class="btn btn-success">Lưu danh mục</button>
                <a href="${pageContext.request.contextPath}/admin/categories"
                   class="btn btn-secondary">Quay lại</a>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
