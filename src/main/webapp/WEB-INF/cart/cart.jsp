<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <h2>Giỏ Hàng Của Bạn</h2>
    <div class="row">
        <!-- Danh sách sản phẩm trong giỏ (Skeleton để Dev 4 triển khai) -->
        <div class="col-lg-8">
            <c:choose>
                <c:when test="${empty sessionScope.cart || empty sessionScope.cart.items}">
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
                            <c:forEach var="item" items="${sessionScope.cart.items}">
                                <tr>
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <img src="${item.product.imageUrl}" class="img-thumbnail me-3" style="width: 50px;" alt="...">
                                            <span>${item.product.title}</span>
                                        </div>
                                    </td>
                                    <td>${item.product.price} VNĐ</td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/cart?action=update" method="POST" style="width: 80px;">
                                            <input type="hidden" name="itemId" value="${item.id}">
                                            <input type="number" class="form-control" name="quantity" value="${item.quantity}" min="1" onchange="this.form.submit()">
                                        </form>
                                    </td>
                                    <td>${item.product.price * item.quantity} VNĐ</td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/cart?action=remove" method="POST">
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
        
        <!-- Cột đặt hàng bên phải (COD Checkout) -->
        <div class="col-lg-4">
            <div class="card shadow-sm mb-4">
                <div class="card-body">
                    <h4 class="card-title mb-4">Thông tin đơn hàng</h4>
                    <ul class="list-group list-group-flush mb-4">
                        <li class="list-group-item d-flex justify-content-between align-items-center">
                            Tổng tiền hàng:
                            <strong>${sessionScope.cartTotal != null ? sessionScope.cartTotal : 0} VNĐ</strong>
                        </li>
                    </ul>
                    
                    <c:if test="${not empty sessionScope.cart && not empty sessionScope.cart.items}">
                        <h5 class="mb-3">Thông tin nhận hàng</h5>
                        <form action="${pageContext.request.contextPath}/checkout" method="POST">
                            <div class="mb-3">
                                <label for="shippingAddress" class="form-label">Địa chỉ giao hàng</label>
                                <textarea class="form-control" id="shippingAddress" name="shippingAddress" required>${sessionScope.userProfile.address}</textarea>
                            </div>
                            <div class="mb-3">
                                <label for="contactPhone" class="form-label">Số điện thoại liên hệ</label>
                                <input type="text" class="form-control" id="contactPhone" name="contactPhone" value="${sessionScope.userProfile.phone}" required>
                            </div>
                            <button type="submit" class="btn btn-primary w-100">Đặt hàng (COD)</button>
                        </form>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
