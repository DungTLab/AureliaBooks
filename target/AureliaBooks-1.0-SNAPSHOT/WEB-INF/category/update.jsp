<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <h2>Cập Nhật Danh Mục</h2>
    <div class="row">
        <div class="col-md-6">
            <form action="${pageContext.request.contextPath}/admin/categories?action=update" method="POST">
                <input type="hidden" name="id" value="${category.id}">
                <div class="mb-3">
                    <label for="name" class="form-label">Tên danh mục</label>
                    <input type="text" class="form-control" id="name" name="name" value="${category.name}" required>
                </div>
                <div class="mb-3">
                    <label for="parentId" class="form-label">Danh mục cha</label>
                    <select class="form-select" id="parentId" name="parentId">
                        <option value="">Không có (Danh mục cấp 1)</option>
                        <c:forEach var="cat" items="${parentCategories}">
                            <c:if test="${cat.id != category.id}">
                                <option value="${cat.id}" ${cat.id == category.parentId ? 'selected' : ''}>${cat.name}</option>
                            </c:if>
                        </c:forEach>
                    </select>
                </div>
                <button type="submit" class="btn btn-primary">Cập nhật</button>
                <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-secondary">Quay lại</a>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
