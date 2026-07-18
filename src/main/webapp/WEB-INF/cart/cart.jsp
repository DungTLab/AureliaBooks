<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-2">
        <h2>Giỏ Hàng Của Bạn</h2>
    </div>
    
    <c:if test="${not empty sessionScope.cartSuccessMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i><c:out value="${sessionScope.cartSuccessMessage}" />
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="cartSuccessMessage" scope="session" />
    </c:if>
    <c:if test="${not empty sessionScope.cartWarningMessage}">
        <div class="alert alert-warning alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i><c:out value="${sessionScope.cartWarningMessage}" />
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="cartWarningMessage" scope="session" />
    </c:if>
    <c:if test="${not empty sessionScope.cartErrorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-octagon-fill me-2"></i><c:out value="${sessionScope.cartErrorMessage}" />
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="cartErrorMessage" scope="session" />
    </c:if>
    
    <div class="justify-content-between  mb-4">
        <a class="btn btn-outline-danger" href="${pageContext.request.contextPath}/orders?action=view">Đơn hàng</a>
    </div>
    
    <div class="row">
        <!-- Danh sách sản phẩm trong giỏ (Skeleton để Dev 4 triển khai) -->
        <div class="col-lg-8">
            <c:choose>
                <c:when test="${empty requestScope.cartItems}">
                    <div class="alert alert-info">Giỏ hàng của bạn đang trống. <a href="${pageContext.request.contextPath}/products">Tiếp tục mua sắm</a></div>
                </c:when>
                <c:otherwise>
                    <table class="table table-striped align-middle">
                        <thead>
                            <tr>
                                <th>Sản phẩm</th>
                                <th>Giá</th>
                                <th>Số lượng</th>
                                <th>Thành tiền</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:set var="canCheckout" value="true" />
                            <c:forEach var="item" items="${requestScope.cartItems}">
                                <c:set var="stock" value="${requestScope.stockMap[item.productId]}" />
                                <c:if test="${item.quantity > stock}">
                                    <c:set var="canCheckout" value="false" />
                                </c:if>
                                <tr class="${item.quantity > stock ? 'table-danger' : ''}">
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <c:choose>
                                                <c:when test="${not empty item.product.imageUrl && item.product.imageUrl.contains('/')}">
                                                    <img src="${pageContext.request.contextPath}/uploads/${item.product.imageUrl}" class="img-thumbnail me-3" style="width: 50px; height: 50px; object-fit: contain;" alt="${item.product.title}">
                                                </c:when>
                                                <c:otherwise>
                                                    <img src="${pageContext.request.contextPath}/assets/images/book-image/${item.product.imageUrl}" class="img-thumbnail me-3" style="width: 50px; height: 50px; object-fit: contain;" alt="${item.product.title}">
                                                </c:otherwise>
                                            </c:choose>
                                            <span>
                                                ${item.product.title}
                                                <c:if test="${item.quantity > stock}">
                                                    <br><small class="text-danger">Tồn kho chỉ còn ${stock}</small>
                                                </c:if>
                                            </span>
                                        </div>
                                    </td>
                                    <td><fmt:formatNumber value="${item.product.price}" type="number" groupingUsed="true" maxFractionDigits="0"/> VNĐ</td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/cart?action=update" method="POST" style="width: 100px;">
                                            <input type="hidden" name="itemId" value="${item.id}">
                                            <input type="number" class="form-control ${item.quantity > stock ? 'is-invalid' : ''}" name="quantity" value="${item.quantity}" min="1" onchange="this.form.submit()">
                                        </form>
                                    </td>
                                    <td><fmt:formatNumber value="${item.product.price * item.quantity}" type="number" groupingUsed="true" maxFractionDigits="0"/> VNĐ</td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/cart?action=delete" method="POST">
                                            <input type="hidden" name="itemId" value="${item.id}">
                                            <button type="submit" class="btn btn-sm btn-danger">Xóa</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
        
        <!-- Cột đặt hàng bên phải -->
        <div class="col-lg-4">
            <div class="card shadow-sm mb-4">
                <div class="card-body">
                    <h4 class="card-title mb-4">Thông tin giỏ hàng</h4>
                    <ul class="list-group list-group-flush mb-4">
                        <li class="list-group-item d-flex justify-content-between align-items-center">
                            Tổng tiền hàng:
                            <strong><fmt:formatNumber value="${requestScope.cartTotal != null ? requestScope.cartTotal : 0}" type="number" groupingUsed="true" maxFractionDigits="0"/> VNĐ</strong>
                        </li>
                    </ul>
                    
                    <c:if test="${not empty requestScope.cartItems}">
                        <c:choose>
                            <c:when test="${canCheckout}">
                                <a href="${pageContext.request.contextPath}/checkout?action=prepareCart" class="btn btn-primary w-100">Tiến hành đặt hàng</a>
                            </c:when>
                            <c:otherwise>
                                <button class="btn btn-secondary w-100" disabled>Vui lòng cập nhật số lượng</button>
                                <small class="text-danger mt-2 d-block text-center">Một số mặt hàng vượt quá tồn kho!</small>
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
