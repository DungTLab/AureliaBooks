<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="bg-light py-4">
    <div class="container bg-white p-4 p-md-5 rounded shadow-sm">
        <h1 class="text-danger fw-bold border-bottom pb-3 mb-4"><i class="bi bi-grid-3x3-gap me-2"></i>Tất cả danh mục sản phẩm</h1>
        <p class="text-secondary mb-5">Khám phá kho sách phong phú và văn phòng phẩm chất lượng tại AureliaBooks.</p>

        <div class="row g-4">
            <c:forEach var="parent" items="${parentCategories}">
                <div class="col-md-4">
                    <div class="card h-100 border-0 shadow-sm">
                        <div class="card-header bg-danger text-white py-3">
                            <h4 class="card-title mb-0 fw-bold fs-5">
                                <c:choose>
                                    <c:when test="${parent.id == 1}">
                                        <i class="bi bi-book me-2"></i>Sách Trong Nước
                                    </c:when>
                                    <c:when test="${parent.id == 2}">
                                        <i class="bi bi-translate me-2"></i>Sách Nước Ngoài
                                    </c:when>
                                    <c:when test="${parent.id == 3}">
                                        <i class="bi bi-pencil-palette me-2"></i>VPP - Dụng Cụ Học Sinh
                                    </c:when>
                                    <c:otherwise>
                                        <i class="bi bi-tag me-2"></i>${parent.name}
                                    </c:otherwise>
                                </c:choose>
                            </h4>
                        </div>
                        <div class="card-body py-4">
                            <!-- Link to see all items in parent category -->
                            <a href="${pageContext.request.contextPath}/products?categoryId=${parent.id}" class="btn btn-sm btn-outline-danger w-100 fw-bold mb-4">
                                Xem tất cả sản phẩm
                            </a>

                            <!-- List of Subcategories -->
                            <c:set var="children" value="${childrenMap[parent.id]}" />
                            <c:choose>
                                <c:when test="${not empty children}">
                                    <div class="list-group list-group-flush">
                                        <c:forEach var="child" items="${children}">
                                            <a href="${pageContext.request.contextPath}/products?categoryId=${child.id}" class="list-group-item list-group-item-action border-0 px-0 d-flex justify-content-between align-items-center">
                                                <span><i class="bi bi-chevron-right text-danger me-2 small"></i>${child.name}</span>
                                                <span class="badge bg-light text-secondary rounded-circle border">»</span>
                                            </a>
                                        </c:forEach>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <p class="text-muted small mb-0">Không có danh mục con.</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
