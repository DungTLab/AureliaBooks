<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %> <!-- JSTL formatting tags -->
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <h2 class="mb-4">Danh Sách Đơn Hàng (Admin)</h2>

    <!-- Order status filters; the active filter is highlighted in the UI. -->
    <div class="mb-3">
        <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-sm ${selectedStatus == 'ALL' ? 'btn-dark' : 'btn-secondary'}">Tất cả</a>
        <a href="${pageContext.request.contextPath}/admin/orders?status=PENDING" class="btn btn-sm ${selectedStatus == 'PENDING' ? 'btn-info text-white' : 'btn-outline-info'}">Chờ xác nhận</a>
        <a href="${pageContext.request.contextPath}/admin/orders?status=SHIPPING" class="btn btn-sm ${selectedStatus == 'SHIPPING' ? 'btn-primary' : 'btn-outline-primary'}">Đang giao</a>
        <a href="${pageContext.request.contextPath}/admin/orders?status=COMPLETED" class="btn btn-sm ${selectedStatus == 'COMPLETED' ? 'btn-success' : 'btn-outline-success'}">Hoàn thành</a>
        <a href="${pageContext.request.contextPath}/admin/orders?status=CANCELLED" class="btn btn-sm ${selectedStatus == 'CANCELLED' ? 'btn-danger' : 'btn-outline-danger'}">Đã Hủy</a>
    </div>

    <table class="table table-bordered table-hover align-middle">
        <thead class="table-dark">
            <tr>
                <th>Mã Đơn</th>
                <th>Ngày Tạo</th>
                <th>Mã KH</th>
                <th>Tổng Tiền</th>
                <th>Trạng Thái</th>
                <th style="min-width: 220px;">Thao Tác</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="order" items="${orderList}">
                <tr>
                    <td><strong>#${order.id}</strong></td>
                    <!-- Display the order date in local format. -->
                    <td><fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                    <td>KH-${order.userId}</td>
                    <!-- Display the total amount in VND. -->
                    <td class="text-danger fw-bold">
                        <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="VNĐ" maxFractionDigits="0"/>
                    </td>
                    <td>
                        <span class="badge ${order.status == 'COMPLETED' ? 'bg-success' : (order.status == 'CANCELLED' ? 'bg-danger' : (order.status == 'RETURNED' ? 'bg-warning' : 'bg-info'))}">
                            ${order.status}
                        </span>
                    </td>
                    <td>
                        <a href="${pageContext.request.contextPath}/orders?action=detail&id=${order.id}" class="btn btn-sm btn-outline-secondary">Chi tiết</a>

                        <!-- Admin-only order status actions. -->
                        <c:if test="${order.status == 'PENDING'}">
                            <!-- Mark the order as shipping. -->
                            <form action="${pageContext.request.contextPath}/admin/orders?action=updateStatus" method="POST" style="display:inline-block;">
                                <input type="hidden" name="orderId" value="${order.id}">
                                <input type="hidden" name="newStatus" value="SHIPPING">
                                <input type="hidden" name="filterStatus" value="${selectedStatus}">
                                <input type="hidden" name="page" value="${currentPage}">
                                <button type="submit" class="btn btn-sm btn-primary ms-1">Giao hàng</button>
                            </form>
                            <!-- Cancel the order. -->
                            <form action="${pageContext.request.contextPath}/admin/orders?action=updateStatus" method="POST" style="display:inline-block;">
                                <input type="hidden" name="orderId" value="${order.id}">
                                <input type="hidden" name="newStatus" value="CANCELLED">
                                <input type="hidden" name="filterStatus" value="${selectedStatus}">
                                <input type="hidden" name="page" value="${currentPage}">
                                <button type="submit" class="btn btn-sm btn-danger ms-1" onclick="return confirm('Bạn chắc chắn muốn hủy đơn này?')">Hủy</button>
                            </form>
                        </c:if>

                        <c:if test="${order.status == 'SHIPPING'}">
                            <!-- Mark the order as completed. -->
                            <form action="${pageContext.request.contextPath}/admin/orders?action=updateStatus" method="POST" style="display:inline-block;">
                                <input type="hidden" name="orderId" value="${order.id}">
                                <input type="hidden" name="newStatus" value="COMPLETED">
                                <input type="hidden" name="filterStatus" value="${selectedStatus}">
                                <input type="hidden" name="page" value="${currentPage}">
                                <button type="submit" class="btn btn-sm btn-success ms-1">Hoàn thành</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            <!-- Empty state when no orders match the selected filter. -->
            <c:if test="${empty orderList}">
                <tr><td colspan="6" class="text-center text-muted py-4">Chưa có dữ liệu đơn hàng phù hợp.</td></tr>
            </c:if>
        </tbody>
    </table>

    <!-- Pagination is shown only when there is more than one page. -->
    <c:if test="${totalPages > 1}">
        <nav aria-label="Page navigation" class="mt-4">
            <ul class="pagination justify-content-center">
                <!-- Nút Về Trước (Previous) -->
                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                    <a class="page-link" href="?status=${selectedStatus}&page=${currentPage - 1}" aria-label="Previous">
                        <span aria-hidden="true">&laquo;</span>
                    </a>
                </li>

                <!-- Các số trang -->
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                        <a class="page-link" href="?status=${selectedStatus}&page=${i}">${i}</a>
                    </li>
                </c:forEach>

                <!-- Nút Sang Sau (Next) -->
                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                    <a class="page-link" href="?status=${selectedStatus}&page=${currentPage + 1}" aria-label="Next">
                        <span aria-hidden="true">&raquo;</span>
                    </a>
                </li>
            </ul>
        </nav>
    </c:if>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />