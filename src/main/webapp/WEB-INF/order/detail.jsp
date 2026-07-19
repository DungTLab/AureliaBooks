<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Chi Tiết Đơn Hàng #${order.id}</h2>
        <a href="javascript:history.back()" class="btn btn-secondary">Quay lại</a>
    </div>

    <div class="row">
        <!-- Thông tin giao nhận hàng -->
        <div class="col-md-4">
            <div class="card shadow-sm mb-4">
                <div class="card-body">
                    <h5 class="card-title">Thông tin giao hàng</h5>
                    <hr>
                    <p><strong>Ngày đặt hàng:</strong> ${order.createdAt}</p>
                    <p><strong>Địa chỉ nhận hàng:</strong> ${order.shippingAddress}</p>
                    <p><strong>Số điện thoại:</strong> ${order.contactPhone}</p>
                    <p><strong>Trạng thái:</strong> 
                        <c:choose>
                            <c:when test="${order.status == 'COMPLETED'}">
                                <span class="badge bg-success">COMPLETED</span>
                            </c:when>
                            <c:when test="${order.status == 'CANCELLED'}">
                                <span class="badge bg-danger">CANCELLED</span>
                            </c:when>
                            <c:when test="${order.status == 'RETURN_REQUESTED'}">
                                <span class="badge bg-info text-dark">RETURN_REQUESTED</span>
                            </c:when>
                            <c:when test="${order.status == 'RETURNED'}">
                                <span class="badge bg-warning text-dark">RETURNED</span>
                            </c:when>
                            <c:when test="${order.status == 'RETURN_REJECTED'}">
                                <span class="badge bg-secondary">RETURN_REJECTED</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-info text-dark">${order.status}</span>
                            </c:otherwise>
                        </c:choose>
                    </p>
                    <c:if test="${(order.status == 'RETURNED' || order.status == 'RETURN_REQUESTED' || order.status == 'RETURN_REJECTED') && not empty order.returnReason}">
                        <p class="text-warning"><strong>Lý do trả hàng:</strong> <c:out value="${order.returnReason}"/></p>
                    </c:if>
                    <c:if test="${order.status == 'RETURN_REJECTED' && not empty order.returnAdminNote}">
                        <p class="text-danger"><strong>Lý do từ chối của admin:</strong> <c:out value="${order.returnAdminNote}"/></p>
                    </c:if>
                    
                    <c:if test="${order.status == 'PENDING' || order.status == 'SHIPPING'}">
                        <div class="mt-3">
                            <form action="${pageContext.request.contextPath}/orders?action=cancel" method="POST" onsubmit="return confirm('Bạn có chắc chắn muốn hủy đơn hàng này không?');">
                                <input type="hidden" name="orderId" value="${order.id}">
                                <button type="submit" class="btn btn-outline-danger w-100">Hủy đơn hàng này</button>
                            </form>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>

        <!-- Chi tiết các món hàng đã mua -->
        <div class="col-md-8">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">Sản phẩm đã đặt</h5>
                    <hr>
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Sản phẩm</th>
                                <th>Đơn giá</th>
                                <th>Số lượng</th>
                                <th>Thành tiền</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${order.items}">
                                <tr>
                                    <td>${item.product.title}</td>
                                    <td><fmt:formatNumber value="${item.unitPrice}" type="number" groupingUsed="true" maxFractionDigits="0"/> VNĐ</td>
                                    <td>${item.quantity}</td>
                                    <td><fmt:formatNumber value="${item.subTotal}" type="number" groupingUsed="true" maxFractionDigits="0"/> VNĐ</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                        <tfoot>
                            <tr>
                                <td colspan="3" class="text-end"><strong>Tổng cộng:</strong></td>
                                <td><strong><fmt:formatNumber value="${order.totalAmount}" type="number" groupingUsed="true" maxFractionDigits="0"/> VNĐ</strong></td>
                            </tr>
                        </tfoot>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
