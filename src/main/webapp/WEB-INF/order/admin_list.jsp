<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <h2 class="mb-4">Danh Sách Đơn Hàng (Admin)</h2>
    
    <!-- Bộ lọc trạng thái đơn -->
    <div class="mb-3">
        <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-sm btn-secondary">Tất cả</a>
        <a href="${pageContext.request.contextPath}/admin/orders?status=PENDING" class="btn btn-sm btn-info">Chờ xác nhận</a>
        <a href="${pageContext.request.contextPath}/admin/orders?status=SHIPPING" class="btn btn-sm btn-primary">Đang giao</a>
        <a href="${pageContext.request.contextPath}/admin/orders?status=COMPLETED" class="btn btn-sm btn-success">Hoàn thành</a>
        <a href="${pageContext.request.contextPath}/admin/orders?status=RETURNED" class="btn btn-sm btn-warning">Đã trả hàng</a>
    </div>

    <!-- Danh sách đơn admin (Skeleton để Dev 5 triển khai) -->
    <table class="table table-bordered table-striped align-middle">
        <thead>
            <tr>
                <th>Mã Đơn</th>
                <th>Ngày Tạo</th>
                <th>Khách Hàng ID</th>
                <th>Tổng Tiền</th>
                <th>Trạng Thái Hiện Tại</th>
                <th>Lý do trả (nếu có)</th>
                <th>Thao Tác</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="order" items="${orderList}">
                <tr>
                    <td>#${order.id}</td>
                    <td>${order.createdAt}</td>
                    <td>${order.userId}</td>
                    <td>${order.totalAmount} VNĐ</td>
                    <td>
                        <span class="badge ${order.status == 'COMPLETED' ? 'bg-success' : (order.status == 'CANCELLED' ? 'bg-danger' : (order.status == 'RETURNED' ? 'bg-warning' : 'bg-info'))}">
                            ${order.status}
                        </span>
                    </td>
                    <td><c:out value="${order.returnReason}" /></td>
                    <td>
                        <a href="${pageContext.request.contextPath}/orders?action=detail&id=${order.id}" class="btn btn-sm btn-outline-secondary">Xem chi tiết</a>
                        
                        <!-- Cập nhật Trạng thái đơn (Dành cho Admin duyệt từ PENDING -> COMPLETED) -->
                        <c:if test="${order.status == 'PENDING'}">
                            <form action="${pageContext.request.contextPath}/admin/orders?action=updateStatus" method="POST" style="display:inline-block;">
                                <input type="hidden" name="orderId" value="${order.id}">
                                <input type="hidden" name="newStatus" value="COMPLETED">
                                <button type="submit" class="btn btn-sm btn-success ms-2">Hoàn thành đơn</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
