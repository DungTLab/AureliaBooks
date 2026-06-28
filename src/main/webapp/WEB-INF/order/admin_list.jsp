<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %> 
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Danh Sách Đơn Hàng (Admin)</h2>
    </div>

    <!-- Giao diện Lọc (Filter) và Tìm kiếm (Search) -->
    <div class="d-flex justify-content-between mb-3 flex-wrap gap-2">
        <!-- Bộ lọc Status (Bên trái) -->
        <div>
            <a href="${pageContext.request.contextPath}/admin/orders?status=ALL&search=${searchQuery}" class="btn btn-sm ${selectedStatus == 'ALL' ? 'btn-dark' : 'btn-secondary'}">Tất cả</a>
            <a href="${pageContext.request.contextPath}/admin/orders?status=PENDING&search=${searchQuery}" class="btn btn-sm ${selectedStatus == 'PENDING' ? 'btn-info text-white' : 'btn-outline-info'}">Chờ xác nhận</a>
            <a href="${pageContext.request.contextPath}/admin/orders?status=SHIPPING&search=${searchQuery}" class="btn btn-sm ${selectedStatus == 'SHIPPING' ? 'btn-primary' : 'btn-outline-primary'}">Đang giao</a>
            <a href="${pageContext.request.contextPath}/admin/orders?status=COMPLETED&search=${searchQuery}" class="btn btn-sm ${selectedStatus == 'COMPLETED' ? 'btn-success' : 'btn-outline-success'}">Hoàn thành</a>
            <a href="${pageContext.request.contextPath}/admin/orders?status=CANCELLED&search=${searchQuery}" class="btn btn-sm ${selectedStatus == 'CANCELLED' ? 'btn-danger' : 'btn-outline-danger'}">Đã Hủy</a>
        </div>

        <!-- Ô Tìm kiếm (Bên phải) -->
        <form action="${pageContext.request.contextPath}/admin/orders" method="GET" class="d-flex">
            <input type="hidden" name="status" value="<c:out value='${selectedStatus}'/>">
            <input type="text" name="search" class="form-control form-control-sm me-2" placeholder="Tìm Mã đơn, Số điện thoại..." value="<c:out value='${searchQuery}'/>">
            <button type="submit" class="btn btn-sm btn-outline-dark px-3">Tìm</button>
            <c:if test="${not empty searchQuery}">
                <a href="${pageContext.request.contextPath}/admin/orders?status=${selectedStatus}" class="btn btn-sm btn-link text-danger text-decoration-none text-nowrap">Xóa bộ lọc</a>
            </c:if>
        </form>
    </div>

    <!-- Thông báo lỗi (nếu có) -->
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger"><c:out value="${errorMessage}"/></div>
    </c:if>

    <div class="table-responsive">
        <table class="table table-bordered table-hover align-middle shadow-sm">
            <thead class="table-dark text-nowrap">
                <tr>
                    <th>Mã Đơn</th>
                    <th>Ngày Tạo</th>
                    <th>Mã KH</th>
                    <th>Tổng Tiền</th>
                    <th>Trạng Thái</th>
                    <th>Người Duyệt</th>
                    <th style="min-width: 240px;">Thao Tác</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="order" items="${orderList}">
                    <tr>
                        <!-- Mã Đơn -->
                        <td><strong>#<c:out value="${order.id}" /></strong></td>
                        
                        <!-- Ngày tạo (Format local) -->
                        <td><fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                        
                        <!-- Mã KH -->
                        <td>KH-<c:out value="${order.userId}"/></td>
                        
                        <!-- Tổng Tiền -->
                        <td class="text-danger fw-bold">
                            <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="VNĐ" maxFractionDigits="0"/>
                        </td>
                        
                        <!-- Trạng Thái -->
                        <td>
                            <span class="badge ${order.status == 'COMPLETED' ? 'bg-success' : (order.status == 'CANCELLED' ? 'bg-danger' : (order.status == 'RETURNED' ? 'bg-warning text-dark' : 'bg-info text-dark'))}">
                                <c:out value="${order.status}" />
                            </span>
                        </td>
                        
                        <!-- Người Duyệt -->
                        <td>
                            <c:choose>
                                <c:when test="${not empty order.processedByUserId}">
                                    <span class="badge bg-secondary">Admin #<c:out value="${order.processedByUserId}" /></span>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted small">Chưa duyệt</span>
                                </c:otherwise>
                            </c:choose>
                        </td>

                        <!-- Thao Tác -->
                        <td>
                            <!-- Nút Xem chi tiết -->
                            <a href="${pageContext.request.contextPath}/admin/orders?action=detail&orderId=${order.id}" class="btn btn-sm btn-outline-secondary">Chi tiết</a>

                            <!-- Các nút cập nhật trạng thái dành riêng cho Admin -->
                            <c:if test="${order.status == 'PENDING'}">
                                <!-- Chuyển sang Đang giao -->
                                <form action="${pageContext.request.contextPath}/admin/orders?action=updateStatus" method="POST" style="display:inline-block;">
                                    <input type="hidden" name="orderId" value="<c:out value='${order.id}'/>">
                                    <input type="hidden" name="newStatus" value="SHIPPING">
                                    <input type="hidden" name="filterStatus" value="<c:out value='${selectedStatus}'/>">
                                    <input type="hidden" name="search" value="<c:out value='${searchQuery}'/>">
                                    <input type="hidden" name="page" value="<c:out value='${currentPage}'/>">
                                    <button type="submit" class="btn btn-sm btn-primary ms-1">Giao hàng</button>
                                </form>
                                <!-- Nút Hủy đơn -->
                                <form action="${pageContext.request.contextPath}/admin/orders?action=updateStatus" method="POST" style="display:inline-block;">
                                    <input type="hidden" name="orderId" value="<c:out value='${order.id}'/>">
                                    <input type="hidden" name="newStatus" value="CANCELLED">
                                    <input type="hidden" name="filterStatus" value="<c:out value='${selectedStatus}'/>">
                                    <input type="hidden" name="search" value="<c:out value='${searchQuery}'/>">
                                    <input type="hidden" name="page" value="<c:out value='${currentPage}'/>">
                                    <button type="submit" class="btn btn-sm btn-danger ms-1" onclick="return confirm('Bạn chắc chắn muốn HỦY đơn hàng #${order.id} này?')">Hủy</button>
                                </form>
                            </c:if>

                            <c:if test="${order.status == 'SHIPPING'}">
                                <!-- Chuyển sang Hoàn thành -->
                                <form action="${pageContext.request.contextPath}/admin/orders?action=updateStatus" method="POST" style="display:inline-block;">
                                    <input type="hidden" name="orderId" value="<c:out value='${order.id}'/>">
                                    <input type="hidden" name="newStatus" value="COMPLETED">
                                    <input type="hidden" name="filterStatus" value="<c:out value='${selectedStatus}'/>">
                                    <input type="hidden" name="search" value="<c:out value='${searchQuery}'/>">
                                    <input type="hidden" name="page" value="<c:out value='${currentPage}'/>">
                                    <button type="submit" class="btn btn-sm btn-success ms-1">Hoàn thành</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                
                <!-- Khi danh sách trống -->
                <c:if test="${empty orderList}">
                    <tr>
                        <td colspan="7" class="text-center text-muted py-5">
                            <i class="fs-4 mb-2 d-block">📦</i>
                            Chưa có dữ liệu đơn hàng phù hợp với tìm kiếm của bạn.
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>

    <!-- Phân trang (Pagination) -->
    <c:if test="${totalPages > 1}">
        <nav aria-label="Page navigation" class="mt-4">
            <ul class="pagination justify-content-center">
                <!-- Nút Previous -->
                <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                    <a class="page-link" href="?status=${selectedStatus}&search=${searchQuery}&page=${currentPage - 1}" aria-label="Previous">
                        <span aria-hidden="true">&laquo;</span>
                    </a>
                </li>

                <!-- Số trang -->
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                        <a class="page-link" href="?status=${selectedStatus}&search=${searchQuery}&page=${i}"><c:out value="${i}"/></a>
                    </li>
                </c:forEach>

                <!-- Nút Next -->
                <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                    <a class="page-link" href="?status=${selectedStatus}&search=${searchQuery}&page=${currentPage + 1}" aria-label="Next">
                        <span aria-hidden="true">&raquo;</span>
                    </a>
                </li>
            </ul>
        </nav>
    </c:if>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />