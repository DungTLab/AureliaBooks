<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <h2 class="mb-4">Hỗ Trợ Khách Hàng</h2>
    
    <c:if test="${not empty sessionScope.successMsg}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            ${sessionScope.successMsg}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="successMsg" scope="session" />
    </c:if>
    <c:if test="${not empty sessionScope.errorMsg}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            ${sessionScope.errorMsg}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="errorMsg" scope="session" />
    </c:if>

    <div class="row">
        <!-- Form Gửi Yêu Cầu -->
        <div class="col-md-5 mb-4">
            <div class="card shadow-sm">
                <div class="card-header bg-primary text-white">
                    <h5 class="card-title mb-0">Gửi Yêu Cầu Mới</h5>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/support?action=create" method="POST">
                        <div class="mb-3">
                            <label for="subject" class="form-label fw-bold">Tiêu đề <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="subject" name="subject" placeholder="Nhập tóm tắt vấn đề..." required>
                        </div>
                        <div class="mb-3">
                            <label for="message" class="form-label fw-bold">Nội dung chi tiết <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="message" name="message" rows="5" placeholder="Vui lòng mô tả chi tiết vấn đề bạn đang gặp phải..." required></textarea>
                        </div>
                        <button type="submit" class="btn btn-primary w-100">Gửi Yêu Cầu</button>
                    </form>
                </div>
            </div>
        </div>

        <!-- Lịch sử Yêu Cầu -->
        <div class="col-md-7">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-light">
                    <h5 class="card-title mb-0">Lịch Sử Yêu Cầu Của Bạn</h5>
                </div>
                <div class="card-body p-0">
                    <c:choose>
                        <c:when test="${empty supportRequests}">
                            <div class="p-4 text-center text-muted">
                                Bạn chưa gửi yêu cầu hỗ trợ nào.
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="table-responsive">
                                <table class="table table-hover align-middle mb-0">
                                    <thead class="table-light">
                                        <tr>
                                            <th>Mã</th>
                                            <th>Ngày Tạo</th>
                                            <th>Tiêu Đề / Trạng Thái</th>
                                            <th>Phản Hồi Từ Admin</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="ticket" items="${supportRequests}">
                                            <tr>
                                                <td>#${ticket.id}</td>
                                                <td>
                                                    <small class="text-muted">${ticket.createdAt}</small>
                                                </td>
                                                <td>
                                                    <div class="fw-bold"><c:out value="${ticket.subject}" /></div>
                                                    <c:choose>
                                                        <c:when test="${ticket.status == 'OPEN'}">
                                                            <span class="badge bg-warning text-dark">Chờ Xử Lý</span>
                                                        </c:when>
                                                        <c:when test="${ticket.status == 'PROCESSING'}">
                                                            <span class="badge bg-info text-dark">Đang Xử Lý</span>
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
                                                    <c:choose>
                                                        <c:when test="${not empty ticket.replyMessage}">
                                                            <div class="p-2 bg-light border rounded small">
                                                                <c:out value="${ticket.replyMessage}" />
                                                            </div>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <em class="text-muted small">Đang chờ phản hồi...</em>
                                                        </c:otherwise>
                                                    </c:choose>
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
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
