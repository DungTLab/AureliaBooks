<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <h2 class="mb-4">Lịch Sử Mua Hàng</h2>
    
    <c:if test="${not empty sessionScope.successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            ${sessionScope.successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="successMessage" scope="session" />
    </c:if>

    <c:if test="${not empty sessionScope.errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            ${sessionScope.errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="errorMessage" scope="session" />
    </c:if>
    
    <!-- Lịch sử đơn (Skeleton để Dev 4 triển khai) -->
    <c:choose>
        <c:when test="${empty orders}">
            <div class="alert alert-info">Bạn chưa đặt đơn hàng nào. <a href="${pageContext.request.contextPath}/products">Mua sắm ngay</a></div>
        </c:when>
        <c:otherwise>
            <table class="table table-bordered table-striped align-middle">
                <thead>
                    <tr>
                        <th>Mã Đơn Hàng</th>
                        <th>Ngày Đặt</th>
                        <th>Địa Chỉ Giao Hàng</th>
                        <th>Tổng Tiền</th>
                        <th>Trạng Thái</th>
                        <th>Thao Tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="order" items="${orders}">
                        <tr style="cursor: pointer;" onclick="if(event.target.tagName !== 'BUTTON' && event.target.tagName !== 'A' && event.target.closest('.modal') == null) { window.location.href='${pageContext.request.contextPath}/orders?action=detail&id=${order.id}'; }">
                            <td>#${order.id}</td>
                            <td>${order.createdAt}</td>
                            <td>${order.shippingAddress}</td>
                            <td>${order.totalAmount} VNĐ</td>
                            <td>
                                <span class="badge ${order.status == 'COMPLETED' ? 'bg-success' : (order.status == 'CANCELLED' ? 'bg-danger' : (order.status == 'RETURNED' ? 'bg-warning' : 'bg-info'))}">
                                    ${order.status}
                                </span>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/orders?action=detail&id=${order.id}" class="btn btn-sm btn-outline-primary">Chi tiết</a>
                                
                                <!-- Hỗ trợ trả hàng (Chỉ hiện nút Trả hàng khi trạng thái đơn là COMPLETED) -->
                                <c:if test="${order.status == 'COMPLETED'}">
                                    <button class="btn btn-sm btn-danger ms-2" data-bs-toggle="modal" data-bs-target="#returnModal${order.id}">Yêu cầu trả hàng</button>
                                    
                                    <!-- Modal Yêu cầu Trả hàng -->
                                    <div class="modal fade" id="returnModal${order.id}" tabindex="-1" aria-hidden="true" onclick="event.stopPropagation();">
                                        <div class="modal-dialog">
                                            <div class="modal-content">
                                                <form action="${pageContext.request.contextPath}/orders?action=return" method="POST">
                                                    <input type="hidden" name="orderId" value="${order.id}">
                                                    <div class="modal-header">
                                                        <h5 class="modal-title">Yêu cầu Trả Hàng - Đơn #${order.id}</h5>
                                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                    </div>
                                                    <div class="modal-body">
                                                        <div class="mb-3">
                                                            <label for="returnReason" class="form-label">Lý do trả hàng</label>
                                                            <textarea class="form-control" name="returnReason" rows="3" minlength="10" maxlength="500" title="Lý do trả hàng từ 10 đến 500 ký tự" required placeholder="Vui lòng nêu rõ lý do trả hàng để chúng tôi duyệt..."></textarea>
                                                        </div>
                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                                                        <button type="submit" class="btn btn-danger">Gửi Yêu Cầu Trả Hàng</button>
                                                    </div>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
