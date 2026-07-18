<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Phản Hồi Yêu Cầu Hỗ Trợ #${supportRequest.id}</h2>
        <a href="${pageContext.request.contextPath}/admin/support" class="btn btn-outline-secondary">Quay lại danh sách</a>
    </div>

    <c:if test="${not empty sessionScope.errorMsg}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            ${sessionScope.errorMsg}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="errorMsg" scope="session" />
    </c:if>

    <div class="row">
        <!-- Thông tin Ticket -->
        <div class="col-md-6 mb-4">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-light">
                    <h5 class="card-title mb-0">Nội Dung Yêu Cầu</h5>
                </div>
                <div class="card-body">
                    <p><strong>Tiêu đề:</strong> <c:out value="${supportRequest.subject}" /></p>
                    <p><strong>Người gửi (User ID):</strong> ${supportRequest.userId}</p>
                    <p><strong>Ngày gửi:</strong> ${supportRequest.createdAt}</p>
                    <p><strong>Trạng thái hiện tại:</strong> 
                        <span class="badge bg-secondary">${supportRequest.status}</span>
                    </p>
                    <hr>
                    <p class="mb-1"><strong>Nội dung chi tiết:</strong></p>
                    <div class="p-3 bg-light border rounded">
                        <c:out value="${supportRequest.message}" />
                    </div>
                </div>
            </div>
        </div>

        <!-- Form Phản hồi -->
        <div class="col-md-6 mb-4">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-primary text-white">
                    <h5 class="card-title mb-0">Phản Hồi Cho Khách Hàng</h5>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/admin/support?action=reply" method="POST">
                        <input type="hidden" name="id" value="${supportRequest.id}" />
                        
                        <div class="mb-3">
                            <label for="replyMessage" class="form-label fw-bold">Nội dung phản hồi <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="replyMessage" name="replyMessage" rows="6" placeholder="Nhập câu trả lời hoặc hướng dẫn giải quyết..." minlength="10" maxlength="2000" title="Nội dung phản hồi từ 10 đến 2000 ký tự" required><c:out value="${supportRequest.replyMessage}" /></textarea>
                        </div>
                        
                        <div class="mb-4">
                            <label for="status" class="form-label fw-bold">Cập nhật Trạng thái</label>
                            <select class="form-select" id="status" name="status">
                                <option value="OPEN" ${supportRequest.status == 'OPEN' ? 'selected' : ''}>OPEN - Chờ Xử Lý</option>
                                <option value="PROCESSING" ${supportRequest.status == 'PROCESSING' ? 'selected' : ''}>PROCESSING - Đang Xử Lý</option>
                                <option value="RESOLVED" ${supportRequest.status == 'RESOLVED' ? 'selected' : ''}>RESOLVED - Đã Giải Quyết</option>
                            </select>
                        </div>
                        
                        <button type="submit" class="btn btn-success w-100">Gửi Phản Hồi & Cập Nhật</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
