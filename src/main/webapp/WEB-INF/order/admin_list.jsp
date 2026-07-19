<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %> 

<jsp:include page="/WEB-INF/includes/header.jsp" />

<c:set var="safeSearch"><c:out value="${searchQuery}"/></c:set>
<c:set var="safeStatus"><c:out value="${selectedStatus}"/></c:set>

    <div class="container my-5">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Danh Sách Đơn Hàng</h2>
        </div>

        <div class="d-flex justify-content-between mb-3 flex-wrap gap-2">
            <div>
                <a href="${pageContext.request.contextPath}/admin/orders?status=ALL&search=${safeSearch}" class="btn btn-sm ${selectedStatus == 'ALL' ? 'btn-dark' : 'btn-secondary'}">Tất cả</a>
            <a href="${pageContext.request.contextPath}/admin/orders?status=PENDING&search=${safeSearch}" class="btn btn-sm ${selectedStatus == 'PENDING' ? 'btn-info text-white' : 'btn-outline-info'}">Chờ xác nhận</a>
            <a href="${pageContext.request.contextPath}/admin/orders?status=CONFIRMED&search=${safeSearch}" class="btn btn-sm ${selectedStatus == 'CONFIRMED' ? 'btn-secondary text-white' : 'btn-outline-secondary'}">Đã xác nhận</a>
            <a href="${pageContext.request.contextPath}/admin/orders?status=SHIPPING&search=${safeSearch}" class="btn btn-sm ${selectedStatus == 'SHIPPING' ? 'btn-primary' : 'btn-outline-primary'}">Đang giao</a>
            <a href="${pageContext.request.contextPath}/admin/orders?status=COMPLETED&search=${safeSearch}" class="btn btn-sm ${selectedStatus == 'COMPLETED' ? 'btn-success' : 'btn-outline-success'}">Hoàn thành</a>
            <a href="${pageContext.request.contextPath}/admin/orders?status=CANCELLED&search=${safeSearch}" class="btn btn-sm ${selectedStatus == 'CANCELLED' ? 'btn-danger' : 'btn-outline-danger'}">Đã Hủy</a>
            <a href="${pageContext.request.contextPath}/admin/orders?status=RETURNED&search=${safeSearch}" class="btn btn-sm ${selectedStatus == 'RETURNED' ? 'btn-warning text-dark' : 'btn-outline-warning'}">Đã Trả Hàng</a>
            <a href="${pageContext.request.contextPath}/admin/orders?status=RETURN_REQUESTED&search=${safeSearch}" class="btn btn-sm ${selectedStatus == 'RETURN_REQUESTED' ? 'btn-info text-dark' : 'btn-outline-info'}">Yêu cầu trả hàng</a>
            <a href="${pageContext.request.contextPath}/admin/orders?status=RETURN_REJECTED&search=${safeSearch}" class="btn btn-sm ${selectedStatus == 'RETURN_REJECTED' ? 'btn-danger' : 'btn-outline-danger'}">Từ chối trả</a>
        </div>

        <form action="${pageContext.request.contextPath}/admin/orders" method="GET" class="d-flex">
            <input type="hidden" name="status" value="${safeStatus}">
            <input type="text" name="search" class="form-control form-control-sm me-2" placeholder="Tìm Mã đơn, Số ĐT..." value="${safeSearch}">
            <button type="submit" class="btn btn-sm btn-outline-dark px-3">Tìm</button>

            <c:if test="${not empty searchQuery}">
                <a href="${pageContext.request.contextPath}/admin/orders?status=${safeStatus}" class="btn btn-sm btn-link text-danger text-decoration-none text-nowrap">Xóa tìm kiếm</a>
            </c:if>
        </form>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger"><c:out value="${errorMessage}"/></div>
    </c:if>
    <c:if test="${not empty sessionScope.successMsg}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <c:out value="${sessionScope.successMsg}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="successMsg" scope="session" />
    </c:if>
    <c:if test="${not empty sessionScope.errorMsg}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <c:out value="${sessionScope.errorMsg}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="errorMsg" scope="session" />
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
                        <td><strong>#<c:out value="${order.id}" /></strong></td>
                        <td><fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                        <td>KH-<c:out value="${order.userId}"/></td>
                        <td class="text-danger fw-bold">
                            <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="VNĐ" maxFractionDigits="0"/>
                        </td>
                        <td>
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
                            
                            <%-- Show return reason inline --%>
                            <c:if test="${(order.status == 'RETURNED' or order.status == 'RETURN_REQUESTED' or order.status == 'RETURN_REJECTED') and not empty order.returnReason}">
                                <div class="text-muted small mt-1" style="max-width:180px; white-space:normal;">
                                    <i class="bi bi-chat-left-text me-1"></i><strong>Lý do khách:</strong> <c:out value="${order.returnReason}"/>
                                </div>
                            </c:if>
                            <%-- Show admin reject note --%>
                            <c:if test="${order.status == 'RETURN_REJECTED' and not empty order.returnAdminNote}">
                                <div class="text-danger small mt-1" style="max-width:180px; white-space:normal;">
                                    <i class="bi bi-x-circle me-1"></i><strong>Lý do từ chối:</strong> <c:out value="${order.returnAdminNote}"/>
                                </div>
                            </c:if>
                        </td>

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

                        <td>
                            <a href="${pageContext.request.contextPath}/admin/orders?action=detail&orderId=${order.id}" class="btn btn-sm btn-outline-secondary">Chi tiết</a>

                            <c:if test="${order.status == 'PENDING'}">
                                <form action="${pageContext.request.contextPath}/admin/orders?action=updateStatus" method="POST" style="display:inline-block;">
                                    <input type="hidden" name="orderId" value="${order.id}">
                                    <input type="hidden" name="newStatus" value="CONFIRMED">
                                    <input type="hidden" name="filterStatus" value="${safeStatus}">
                                    <input type="hidden" name="search" value="${safeSearch}">
                                    <input type="hidden" name="page" value="${currentPage}">
                                    <button type="submit" class="btn btn-sm btn-secondary text-white ms-1">Xác nhận</button>
                                </form>
                                <form action="${pageContext.request.contextPath}/admin/orders?action=updateStatus" method="POST" style="display:inline-block;">
                                    <input type="hidden" name="orderId" value="${order.id}">
                                    <input type="hidden" name="newStatus" value="CANCELLED">
                                    <input type="hidden" name="filterStatus" value="${safeStatus}">
                                    <input type="hidden" name="search" value="${safeSearch}">
                                    <input type="hidden" name="page" value="${currentPage}">
                                    <button type="submit" class="btn btn-sm btn-danger ms-1" onclick="return confirm('Bạn chắc chắn muốn HỦY đơn hàng #${order.id} này?')">Hủy</button>
                                </form>
                            </c:if>

                            <c:if test="${order.status == 'CONFIRMED'}">
                                <form action="${pageContext.request.contextPath}/admin/orders?action=updateStatus" method="POST" style="display:inline-block;">
                                    <input type="hidden" name="orderId" value="${order.id}">
                                    <input type="hidden" name="newStatus" value="SHIPPING">
                                    <input type="hidden" name="filterStatus" value="${safeStatus}">
                                    <input type="hidden" name="search" value="${safeSearch}">
                                    <input type="hidden" name="page" value="${currentPage}">
                                    <button type="submit" class="btn btn-sm btn-primary ms-1">Giao hàng</button>
                                </form>
                                <form action="${pageContext.request.contextPath}/admin/orders?action=updateStatus" method="POST" style="display:inline-block;">
                                    <input type="hidden" name="orderId" value="${order.id}">
                                    <input type="hidden" name="newStatus" value="CANCELLED">
                                    <input type="hidden" name="filterStatus" value="${safeStatus}">
                                    <input type="hidden" name="search" value="${safeSearch}">
                                    <input type="hidden" name="page" value="${currentPage}">
                                    <button type="submit" class="btn btn-sm btn-danger ms-1" onclick="return confirm('Bạn chắc chắn muốn HỦY đơn hàng #${order.id} này?')">Hủy</button>
                                </form>
                            </c:if>

                            <c:if test="${order.status == 'SHIPPING'}">
                                <form action="${pageContext.request.contextPath}/admin/orders?action=updateStatus" method="POST" style="display:inline-block;">
                                    <input type="hidden" name="orderId" value="${order.id}">
                                    <input type="hidden" name="newStatus" value="COMPLETED">
                                    <input type="hidden" name="filterStatus" value="${safeStatus}">
                                    <input type="hidden" name="search" value="${safeSearch}">
                                    <input type="hidden" name="page" value="${currentPage}">
                                    <button type="submit" class="btn btn-sm btn-success ms-1">Hoàn thành</button>
                                </form>
                            </c:if>

                            <c:if test="${order.status == 'RETURN_REQUESTED'}">
                                <form action="${pageContext.request.contextPath}/admin/orders?action=approveReturn" method="POST" style="display:inline-block;" onsubmit="return confirm('Bạn chắc chắn muốn DUYỆT yêu cầu trả hàng cho đơn hàng #${order.id} này? Hành động này sẽ hoàn lại tồn kho.')">
                                    <input type="hidden" name="orderId" value="${order.id}">
                                    <input type="hidden" name="filterStatus" value="${safeStatus}">
                                    <input type="hidden" name="search" value="${safeSearch}">
                                    <input type="hidden" name="page" value="${currentPage}">
                                    <button type="submit" class="btn btn-sm btn-success ms-1">Duyệt trả</button>
                                </form>
                                <button class="btn btn-sm btn-danger ms-1" data-bs-toggle="modal" data-bs-target="#rejectModal${order.id}">Từ chối</button>

                                <!-- Modal Từ chối Trả hàng -->
                                <div class="modal fade" id="rejectModal${order.id}" tabindex="-1" aria-hidden="true" onclick="event.stopPropagation();">
                                    <div class="modal-dialog">
                                        <div class="modal-content text-start">
                                            <form action="${pageContext.request.contextPath}/admin/orders?action=rejectReturn" method="POST">
                                                <input type="hidden" name="orderId" value="${order.id}">
                                                <input type="hidden" name="filterStatus" value="${safeStatus}">
                                                <input type="hidden" name="search" value="${safeSearch}">
                                                <input type="hidden" name="page" value="${currentPage}">
                                                <div class="modal-header">
                                                    <h5 class="modal-title text-dark">Từ Chối Trả Hàng - Đơn #${order.id}</h5>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                </div>
                                                <div class="modal-body">
                                                    <div class="mb-3">
                                                        <label for="rejectReason" class="form-label text-dark">Lý do từ chối</label>
                                                        <textarea class="form-control" name="rejectReason" rows="3" minlength="10" maxlength="500" required placeholder="Vui lòng nhập lý do từ chối trả hàng (tối thiểu 10 ký tự)..."></textarea>
                                                    </div>
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                                                    <button type="submit" class="btn btn-danger">Xác nhận Từ chối</button>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>

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

    <c:if test="${totalPages > 1}">
        <nav aria-label="Page navigation" class="mt-4">
            <ul class="pagination justify-content-center">

                <!-- First & Previous Page Links -->
                <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                    <a class="page-link" href="?status=${safeStatus}&search=${safeSearch}&page=1" title="First Page">&laquo;&laquo;</a>
                </li>
                <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                    <a class="page-link" href="?status=${safeStatus}&search=${safeSearch}&page=${currentPage - 1}" title="Previous Page">&laquo;</a>
                </li>

                <!-- Left Ellipsis (show if startPage > 1) -->
                <c:if test="${startPage > 1}">
                    <li class="page-item disabled"><span class="page-link">...</span></li>
                    </c:if>

                <!-- Sliding Window Loop (render pages from startPage to endPage) -->
                <c:forEach begin="${startPage}" end="${endPage}" var="i">
                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                        <a class="page-link" href="?status=${safeStatus}&search=${safeSearch}&page=${i}">
                            <c:out value="${i}"/>
                        </a>
                    </li>
                </c:forEach>

                <!-- Right Ellipsis (show if endPage < totalPages) -->
                <c:if test="${endPage < totalPages}">
                    <li class="page-item disabled"><span class="page-link">...</span></li>
                    </c:if>

                <!-- Next & Last Page Links -->
                <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                    <a class="page-link" href="?status=${safeStatus}&search=${safeSearch}&page=${currentPage + 1}" title="Next Page">&raquo;</a>
                </li>
                <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                    <a class="page-link" href="?status=${safeStatus}&search=${safeSearch}&page=${totalPages}" title="Last Page">&raquo;&raquo;</a>
                </li>

            </ul>
        </nav>
    </c:if>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />