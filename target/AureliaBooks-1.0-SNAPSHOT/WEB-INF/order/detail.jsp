<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
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
                        <span class="badge ${order.status == 'COMPLETED' ? 'bg-success' : (order.status == 'CANCELLED' ? 'bg-danger' : (order.status == 'RETURNED' ? 'bg-warning' : 'bg-info'))}">
                            ${order.status}
                        </span>
                    </p>
                    <c:if test="${order.status == 'RETURNED'}">
                        <p class="text-danger"><strong>Lý do trả hàng:</strong> ${order.returnReason}</p>
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
                                    <td>${item.unitPrice} VNĐ</td>
                                    <td>${item.quantity}</td>
                                    <td>${item.subTotal} VNĐ</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                        <tfoot>
                            <tr>
                                <td colspan="3" class="text-end"><strong>Tổng cộng:</strong></td>
                                <td><strong>${order.totalAmount} VNĐ</strong></td>
                            </tr>
                        </tfoot>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
