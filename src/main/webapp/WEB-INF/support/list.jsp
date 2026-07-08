<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <h2 class="mb-4">Quản Lý Yêu Cầu Hỗ Trợ (Admin)</h2>

    <c:if test="${not empty sessionScope.successMsg}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            ${sessionScope.successMsg}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="successMsg" scope="session" />
    </c:if>

    <div class="card shadow-sm mb-4">
        <div class="card-header bg-white py-3">
            <h5 class="m-0 font-weight-bold text-primary">Danh Sách Ticket Từ Khách Hàng</h5>
        </div>
        <div class="card-body">
            <c:choose>
                <c:when test="${empty adminSupportRequests}">
                    <div class="alert alert-info">Hiện tại không có yêu cầu hỗ trợ nào cần xử lý.</div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-bordered table-hover align-middle">
                            <thead class="table-light">
                                <tr>
                                    <th>Mã Ticket</th>
                                    <th>Người Gửi (User ID)</th>
                                    <th>Tiêu Đề</th>
                                    <th>Ngày Gửi</th>
                                    <th>Trạng Thái</th>
                                    <th>Thao Tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="ticket" items="${adminSupportRequests}">
                                    <tr>
                                        <td>#${ticket.id}</td>
                                        <td>${ticket.userId}</td>
                                        <td><c:out value="${ticket.subject}" /></td>
                                        <td>${ticket.createdAt}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${ticket.status == 'OPEN'}">
                                                    <span class="badge bg-warning text-dark">Chờ Xử Lý</span>
                                                </c:when>
                                                <c:when test="${ticket.status == 'PROCESSING'}">
                                                    <span class="badge bg-info text-dark">Đang Phản Hồi</span>
                                                </c:when>
                                                <c:when test="${ticket.status == 'RESOLVED'}">
                                                    <span class="badge bg-success">Đã Giải Quyết</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary">${ticket.status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/admin/support?action=reply&id=${ticket.id}" class="btn btn-sm btn-primary">
                                                Xem & Phản hồi
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
