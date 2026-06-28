<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %> 

<!-- Shared Header inclusion -->
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Danh Sách Đơn Hàng (Admin)</h2>
    </div>

    <!-- UI SECTION: Dynamic Filtering & Keyword Searching -->
    <div class="d-flex justify-content-between mb-3 flex-wrap gap-2">
        <!-- Status Filters. The active status button is visually highlighted -->
        <div>
            <a href="${pageContext.request.contextPath}/admin/orders?status=ALL&search=${searchQuery}" class="btn btn-sm ${selectedStatus == 'ALL' ? 'btn-dark' : 'btn-secondary'}">Tất cả</a>
            <a href="${pageContext.request.contextPath}/admin/orders?status=PENDING&search=${searchQuery}" class="btn btn-sm ${selectedStatus == 'PENDING' ? 'btn-info text-white' : 'btn-outline-info'}">Chờ xác nhận</a>
            <a href="${pageContext.request.contextPath}/admin/orders?status=SHIPPING&search=${searchQuery}" class="btn btn-sm ${selectedStatus == 'SHIPPING' ? 'btn-primary' : 'btn-outline-primary'}">Đang giao</a>
            <a href="${pageContext.request.contextPath}/admin/orders?status=COMPLETED&search=${searchQuery}" class="btn btn-sm ${selectedStatus == 'COMPLETED' ? 'btn-success' : 'btn-outline-success'}">Hoàn thành</a>
            <a href="${pageContext.request.contextPath}/admin/orders?status=CANCELLED&search=${searchQuery}" class="btn btn-sm ${selectedStatus == 'CANCELLED' ? 'btn-danger' : 'btn-outline-danger'}">Đã Hủy</a>
        </div>

        <!-- Search Form. Performs a GET request to construct a shareable URL -->
        <form action="${pageContext.request.contextPath}/admin/orders" method="GET" class="d-flex">
            <!-- Hidden input guarantees the current status filter is preserved while searching -->
            <input type="hidden" name="status" value="<c:out value='${selectedStatus}'/>">
            
            <!-- Safe output applied using c:out to prevent XSS injection in the search box -->
            <input type="text" name="search" class="form-control form-control-sm me-2" placeholder="Tìm Mã đơn, Số điện thoại..." value="<c:out value='${searchQuery}'/>">
            <button type="submit" class="btn btn-sm btn-outline-dark px-3">Tìm</button>
            
            <c:if test="${not empty searchQuery}">
                <a href="${pageContext.request.contextPath}/admin/orders?status=${selectedStatus}" class="btn btn-sm btn-link text-danger text-decoration-none text-nowrap">Xóa bộ lọc</a>
            </c:if>
        </form>
    </div>

    <!-- Global Error Message Display Area -->
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger"><c:out value="${errorMessage}"/></div>
    </c:if>

    <!-- UI SECTION: Data Grid -->
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
                        <!-- Output encoding (<c:out>) applied globally to neutralize XSS payloads -->
                        <td><strong>#<c:out value="${order.id}" /></strong></td>
                        
                        <!-- Date formatting utilizing standard JSTL fmt tags -->
                        <td><fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                        
                        <td>KH-<c:out value="${order.userId}"/></td>
                        
                        <td class="text-danger fw-bold">
                            <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="VNĐ" maxFractionDigits="0"/>
                        </td>
                        
                        <td>
                            <span class="badge ${order.status == 'COMPLETED' ? 'bg-success' : (order.status == 'CANCELLED' ? 'bg-danger' : (order.status == 'RETURNED' ? 'bg-warning text-dark' : 'bg-info text-dark'))}">
                                <c:out value="${order.status}" />
                            </span>
                        </td>
                        
                        <!-- Audit Trail Column: Displays accountability (Which Admin processed this) -->
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

                        <!-- Action Controls: Enclosed in isolated POST forms to mutate application state securely -->
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/orders?action=detail&orderId=${order.id}" class="btn btn-sm btn-outline-secondary">Chi tiết</a>

                            <c:if test="${order.status == 'PENDING'}">
                                <!-- Form payload explicitly passes current view state (search, page) to enable seamless redirects after mutation -->
                                <form action="${pageContext.request.contextPath}/admin/orders?action=updateStatus" method="POST" style="display:inline-block;">
                                    <input type="hidden" name="orderId" value="<c:out value='${order.id}'/>">
                                    <input type="hidden" name="newStatus" value="SHIPPING">
                                    <input type="hidden" name="filterStatus" value="<c:out value='${selectedStatus}'/>">
                                    <input type="hidden" name="search" value="<c:out value='${searchQuery}'/>">
                                    <input type="hidden" name="page" value="<c:out value='${currentPage}'/>">
                                    <button type="submit" class="btn btn-sm btn-primary ms-1">Giao hàng</button>
                                </form>
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
                
                <!-- Empty State Representation -->
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

    <!-- UI SECTION: Pagination Controls -->
    <!-- Dynamically generates navigation links appending both search query and status filter to preserve state -->
    <c:if test="${totalPages > 1}">
        <nav aria-label="Page navigation" class="mt-4">
            <ul class="pagination justify-content-center">
                <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                    <a class="page-link" href="?status=${selectedStatus}&search=${searchQuery}&page=${currentPage - 1}" aria-label="Previous">
                        <span aria-hidden="true">&laquo;</span>
                    </a>
                </li>

                <c:forEach begin="1" end="${totalPages}" var="i">
                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                        <a class="page-link" href="?status=${selectedStatus}&search=${searchQuery}&page=${i}"><c:out value="${i}"/></a>
                    </li>
                </c:forEach>

                <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                    <a class="page-link" href="?status=${selectedStatus}&search=${searchQuery}&page=${currentPage + 1}" aria-label="Next">
                        <span aria-hidden="true">&raquo;</span>
                    </a>
                </li>
            </ul>
        </nav>
    </c:if>
</div>

<!-- Shared Footer inclusion -->
<jsp:include page="/WEB-INF/includes/footer.jsp" />