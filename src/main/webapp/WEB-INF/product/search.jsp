<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />


<div class="bg-white py-4">
    <div class="container">
        <div class="border-top pt-3 mb-4">
            <div class="d-flex justify-content-between align-items-end gap-3 flex-wrap">
                <div>
                    <h2 class="h4 mb-1">Kết quả tìm kiếm</h2>
                    <c:choose>
                        <c:when test="${not empty query}">
                            <div class="text-secondary">Từ khóa: <strong>${query}</strong></div>
                        </c:when>
                        <c:otherwise>
                            <div class="text-secondary">Tất cả sản phẩm</div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="text-secondary small">${totalProducts} sản phẩm</div>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty products}">
                <div class="alert alert-info mb-0">
                    Không tìm thấy sản phẩm phù hợp.
                </div>
            </c:when>
            <c:otherwise>
                <div class="row row-cols-5 g-4 gy-5">
                    <c:forEach var="product" items="${products}">
                        <div class="col">
                            <a href="${pageContext.request.contextPath}/products?action=detail&id=${product.id}" class="card h-100 text-decoration-none text-dark border-0 shadow-sm">
                                <div class="ratio ratio-1x1">
                                    <img src="${pageContext.request.contextPath}/uploads/${product.imageUrl}" class="card-img-top img-fluid object-fit-contain p-3" alt="${product.title}">
                                </div>
                                <div class="card-body">
                                    <h5 class="card-title fs-6 fw-normal mb-2">${product.title}</h5>
                                    <div class="text-danger fw-bold fs-5">
                                        <fmt:formatNumber value="${product.price}" type="number" groupingUsed="true"/> đ
                                    </div>
                                </div>
                            </a>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>

        <c:if test="${totalProducts > 0}">
            <nav aria-label="Page navigation" class="d-flex justify-content-center py-4 mt-3">
                <ul class="pagination mb-0">
                    <c:url var="prevPageUrl" value="/products">
                        <c:param name="query" value="${query}" />
                        <c:param name="categoryId" value="${categoryId}" />
                        <c:param name="page" value="${currentPage - 1}" />
                    </c:url>
                    <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                        <a class="page-link" href="${prevPageUrl}" aria-label="Previous">
                            <span aria-hidden="true">&laquo;</span>
                        </a>
                    </li>

                    <c:choose>
                        <c:when test="${totalPages <= 7}">
                            <c:forEach var="i" begin="1" end="${totalPages}">
                                <c:url var="pageUrl" value="/products">
                                    <c:param name="query" value="${query}" />
                                    <c:param name="categoryId" value="${categoryId}" />
                                    <c:param name="page" value="${i}" />
                                </c:url>
                                <li class="page-item ${i == currentPage ? 'active' : ''}">
                                    <a class="page-link" href="${pageUrl}">${i}</a>
                                </li>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <c:url var="firstPageUrl" value="/products">
                                <c:param name="query" value="${query}" />
                                <c:param name="categoryId" value="${categoryId}" />
                                <c:param name="page" value="1" />
                            </c:url>
                            <li class="page-item ${currentPage == 1 ? 'active' : ''}">
                                <a class="page-link" href="${firstPageUrl}">1</a>
                            </li>

                            <c:set var="startPage" value="${currentPage - 2}" />
                            <c:set var="endPage" value="${currentPage + 2}" />
                            <c:if test="${startPage < 2}">
                                <c:set var="startPage" value="2" />
                                <c:set var="endPage" value="5" />
                            </c:if>
                            <c:if test="${endPage > totalPages - 1}">
                                <c:set var="endPage" value="${totalPages - 1}" />
                                <c:set var="startPage" value="${totalPages - 4}" />
                            </c:if>

                            <c:if test="${startPage > 2}">
                                <li class="page-item disabled"><span class="page-link">...</span></li>
                            </c:if>

                            <c:forEach var="i" begin="${startPage}" end="${endPage}">
                                <c:url var="pageUrl" value="/products">
                                    <c:param name="query" value="${query}" />
                                    <c:param name="categoryId" value="${categoryId}" />
                                    <c:param name="page" value="${i}" />
                                </c:url>
                                <li class="page-item ${i == currentPage ? 'active' : ''}">
                                    <a class="page-link" href="${pageUrl}">${i}</a>
                                </li>
                            </c:forEach>

                            <c:if test="${endPage < totalPages - 1}">
                                <li class="page-item disabled"><span class="page-link">...</span></li>
                            </c:if>

                            <c:url var="lastPageUrl" value="/products">
                                <c:param name="query" value="${query}" />
                                <c:param name="categoryId" value="${categoryId}" />
                                <c:param name="page" value="${totalPages}" />
                            </c:url>
                            <li class="page-item ${currentPage == totalPages ? 'active' : ''}">
                                <a class="page-link" href="${lastPageUrl}">${totalPages}</a>
                            </li>
                        </c:otherwise>
                    </c:choose>

                    <c:url var="nextPageUrl" value="/products">
                        <c:param name="query" value="${query}" />
                        <c:param name="categoryId" value="${categoryId}" />
                        <c:param name="page" value="${currentPage + 1}" />
                    </c:url>
                    <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                        <a class="page-link" href="${nextPageUrl}" aria-label="Next">
                            <span aria-hidden="true">&raquo;</span>
                        </a>
                    </li>
                </ul>
            </nav>
        </c:if>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
