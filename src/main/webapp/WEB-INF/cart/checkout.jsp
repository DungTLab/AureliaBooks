<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Thanh Toán Đơn Hàng</h2>
        <a href="${pageContext.request.contextPath}/cart" class="btn btn-secondary">Quay lại giỏ hàng</a>
    </div>
    
    <c:if test="${not empty requestScope.checkoutError}">
        <div class="alert alert-danger py-2">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>${requestScope.checkoutError}
        </div>
    </c:if>

    <!-- Main Checkout Form (using HTML5 form attribute for inputs outside this tag) -->
    <form id="checkoutForm" action="${pageContext.request.contextPath}/checkout" method="POST"></form>

    <div class="row">
        <!-- Cột thông tin giao hàng và danh sách hàng hóa -->
        <div class="col-lg-8">
            <!-- Card Thông tin nhận hàng -->
            <div class="card shadow-sm mb-4">
                <div class="card-header bg-white">
                    <h5 class="card-title mb-0">1. Thông tin giao hàng</h5>
                </div>
                <div class="card-body">
                    <div class="mb-3">
                        <label for="shippingAddress" class="form-label">Địa chỉ giao hàng <span class="text-danger">*</span></label>
                        <textarea class="form-control" id="shippingAddress" name="shippingAddress" form="checkoutForm" rows="3" minlength="5" maxlength="255" required>${requestScope.profile.address}</textarea>
                    </div>
                    <div class="mb-3">
                        <label for="contactPhone" class="form-label">Số điện thoại liên hệ <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="contactPhone" name="contactPhone" form="checkoutForm" value="${requestScope.profile.phone}" pattern="^0[0-9]{9}$" minlength="10" maxlength="10" inputmode="numeric" title="Vui lòng nhập số điện thoại gồm 10 chữ số và bắt đầu bằng số 0" required>
                    </div>
                </div>
            </div>

            <!-- Card Danh sách sản phẩm -->
            <div class="card shadow-sm mb-4">
                <div class="card-header bg-white">
                    <h5 class="card-title mb-0">2. Sản phẩm trong đơn hàng</h5>
                </div>
                <div class="card-body">
                    <table class="table align-middle mb-0">
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
                            <c:forEach var="item" items="${requestScope.checkoutItems}">
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
                                        <form action="${pageContext.request.contextPath}/checkout?action=update" method="POST" style="width: 80px;">
                                            <input type="hidden" name="productId" value="${item.productId}">
                                            <input type="number" class="form-control ${item.quantity > stock ? 'is-invalid' : ''}" name="quantity" value="${item.quantity}" min="1" onchange="this.form.submit()">
                                        </form>
                                    </td>
                                    <td><strong><fmt:formatNumber value="${item.product.price * item.quantity}" type="number" groupingUsed="true" maxFractionDigits="0"/> VNĐ</strong></td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/checkout?action=delete" method="POST">
                                            <input type="hidden" name="productId" value="${item.productId}">
                                            <button type="submit" class="btn btn-sm btn-danger">Xóa</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Cột thanh toán và tổng tiền -->
        <div class="col-lg-4">
            <!-- Card Mã giảm giá -->
            <div class="card shadow-sm mb-4">
                <div class="card-header bg-white">
                    <h5 class="card-title mb-0">Mã giảm giá</h5>
                </div>
                <div class="card-body">
                    <c:if test="${not empty voucherError}">
                        <div class="alert alert-danger p-2 mb-3"><small>${voucherError}</small></div>
                        <c:remove var="voucherError" scope="session" />
                    </c:if>
                    
                    <c:choose>
                        <c:when test="${not empty appliedDiscount}">
                            <div class="alert alert-success d-flex justify-content-between align-items-center mb-0 p-2">
                                <div>
                                    <strong>${appliedDiscount.code}</strong><br>
                                    <small>Đã giảm: -<fmt:formatNumber value="${discountAmount}" type="number" groupingUsed="true" maxFractionDigits="0"/> VNĐ</small>
                                </div>
                                <button type="submit" form="checkoutForm" formaction="${pageContext.request.contextPath}/checkout?action=removeVoucher" formnovalidate class="btn btn-sm btn-outline-danger">Hủy</button>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="d-flex">
                                <input type="text" class="form-control me-2" id="voucherCode" name="voucherCode" form="checkoutForm" placeholder="Nhập mã voucher">
                                <button type="submit" form="checkoutForm" formaction="${pageContext.request.contextPath}/checkout?action=applyVoucher" formnovalidate class="btn btn-outline-primary">Áp dụng</button>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="card shadow-sm sticky-top" style="top: 20px;">
                <div class="card-header bg-white">
                    <h5 class="card-title mb-0">3. Chi tiết thanh toán</h5>
                </div>
                <div class="card-body">
                    <ul class="list-group list-group-flush mb-4">
                        <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                            Tổng tiền hàng:
                            <span><fmt:formatNumber value="${requestScope.subTotal}" type="number" groupingUsed="true" maxFractionDigits="0"/> VNĐ</span>
                        </li>
                        <c:if test="${not empty appliedDiscount}">
                        <li class="list-group-item d-flex justify-content-between align-items-center px-0 text-success">
                            Giảm giá (${appliedDiscount.code}):
                            <span>-<fmt:formatNumber value="${requestScope.discountAmount}" type="number" groupingUsed="true" maxFractionDigits="0"/> VNĐ</span>
                        </li>
                        </c:if>
                        <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                            Phí giao hàng:
                            <span><fmt:formatNumber value="${requestScope.shippingCost}" type="number" groupingUsed="true" maxFractionDigits="0"/> VNĐ</span>
                        </li>
                        <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                            Thuế VAT (8%):
                            <span><fmt:formatNumber value="${requestScope.tax}" type="number" groupingUsed="true" maxFractionDigits="0"/> VNĐ</span>
                        </li>
                        <li class="list-group-item d-flex justify-content-between align-items-center px-0 fw-bold fs-5">
                            Tổng cộng:
                            <span class="text-danger"><fmt:formatNumber value="${requestScope.totalAmount}" type="number" groupingUsed="true" maxFractionDigits="0"/> VNĐ</span>
                        </li>
                    </ul>

                    <c:choose>
                        <c:when test="${canCheckout}">
                            <button type="submit" form="checkoutForm" class="btn btn-danger w-100 py-2 fs-5">Xác nhận Đặt Hàng (COD)</button>
                        </c:when>
                        <c:otherwise>
                            <button class="btn btn-secondary w-100 py-2 fs-5" disabled>Vui lòng cập nhật số lượng</button>
                            <small class="text-danger mt-2 d-block text-center">Một số mặt hàng vượt quá tồn kho!</small>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/includes/footer.jsp" />
