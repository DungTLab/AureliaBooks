<%-- 
    Document   : index
    Created on : May 27, 2026, 11:05:46 AM
    Author     : DungLT
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="./WEB-INF/includes/header.jsp" %>

<%-- Body --%>
<div class="bg-body-secondary pb-3">


    <%-- View all product --%>
    <div class="container mt-3">


        <div class=" container d-flex rounded-3 justify-content-between align-items-center pt-3 pb-3 bg-light">
            <label class="h2 mb-0"><i class="bi bi-graph-up-arrow btn btn-danger disabled"></i> Xu Hướng Mua Sắm</label>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-danger fw-bold shadow-sm"><i class="bi bi-collection-fill me-1"></i> Xem tất cả sản phẩm</a>
        </div>

        <div class="container rounded-3 bg-white pt-3">
            <%-- Danh sách sản phẩm: mỗi dòng 1 sản phẩm --%>
            <div class="row g-3 pb-3">
                <c:forEach var="product" items="${listTopSaleProducts}">
                    <div class="col-12">
                        <div class="card border-2">
                            <div class="row g-0">
                                <div class="col-md-3">
                                    <c:choose>
                                        <c:when test="${not empty product.imageUrl && product.imageUrl.contains('/')}">
                                            <img src="${pageContext.request.contextPath}/uploads/${product.imageUrl}" class="img-fluid rounded-start object-fit-contain w-100" alt="${product.title}" style="height: 220px;">
                                        </c:when>
                                        <c:otherwise>
                                            <img src="${pageContext.request.contextPath}/assets/images/book-image/${product.imageUrl}" class="img-fluid rounded-start object-fit-contain w-100" alt="${product.title}" style="height: 220px;">
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="col-md-9">
                                    <div class="card-body d-flex flex-column h-100">
                                        <a class="card-title h4 mb-3 text-decoration-none" href="${pageContext.request.contextPath}/products?action=detail&id=${product.id}">${product.title}</a>
                                        <p class="card-text text-secondary mb-4">${product.description}</p>
                                        <div class="mt-auto d-flex justify-content-between align-items-center">
                                            <span class="text-danger fw-bold h5 mb-0">
                                                <fmt:formatNumber value="${product.price}" type="number" groupingUsed="true"/>đ
                                            </span>
                                            <c:choose>
                                                <c:when test="${sessionScope.user.roleName eq 'ADMIN' or sessionScope.user.roleName eq 'EMPLOYEE'}">
                                                    <span class="badge bg-secondary py-2 px-3"><i class="bi bi-info-circle me-1"></i>Tài khoản Quản trị</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <button type="button" onclick="addToCartGlobal(${product.id}, 1, false)" class="btn btn-primary px-4"> <i class="bi bi-cart"></i> Add to cart</button>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <%-- Phân trang --%>
            <c:if test="${totalPages > 1}">
                <nav aria-label="Page navigation" class="d-flex justify-content-center pb-3 pt-2">
                    <ul class="pagination mb-0">

                        <%-- Trang đầu và trang trước --%>
                        <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                            <a class="page-link" href="?page=1" title="Trang đầu">&laquo;&laquo;</a>
                        </li>
                        <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                            <a class="page-link" href="?page=${currentPage - 1}" title="Trang trước">&laquo;</a>
                        </li>

                        <%-- Dấu ba chấm bên trái --%>
                        <c:if test="${startPage > 1}">
                            <li class="page-item disabled"><span class="page-link">...</span></li>
                        </c:if>

                        <%-- Cửa sổ số trang --%>
                        <c:forEach begin="${startPage}" end="${endPage}" var="i">
                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                <a class="page-link" href="?page=${i}">
                                    <c:out value="${i}"/>
                                </a>
                            </li>
                        </c:forEach>

                        <%-- Dấu ba chấm bên phải --%>
                        <c:if test="${endPage < totalPages}">
                            <li class="page-item disabled"><span class="page-link">...</span></li>
                        </c:if>

                        <%-- Trang sau và trang cuối --%>
                        <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="?page=${currentPage + 1}" title="Trang sau">&raquo;</a>
                        </li>
                        <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="?page=${totalPages}" title="Trang cuối">&raquo;&raquo;</a>
                        </li>
                    </ul>
                </nav>
            </c:if>
        </div>


    </div>

</div>


</div>
<%@include file="./WEB-INF/includes/footer.jsp" %>
