<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <h2>Xác Nhận Xóa Sách</h2>
    <div class="card border-danger mb-3" style="max-width: 30rem;">
        <div class="card-header bg-danger text-white">Cảnh báo xóa dữ liệu</div>
        <div class="card-body">
            <h5 class="card-title">Bạn có chắc chắn muốn xóa cuốn sách sau?</h5>
            <p class="card-text">
                <strong>Tên sách:</strong> ${book.title} <br>
                <strong>Mã SKU:</strong> ${book.sku} <br>
                <strong>Giá:</strong> ${book.price} VNĐ
            </p>
            <form action="${pageContext.request.contextPath}/admin/products?view=delete" method="POST">
                <input type="hidden" name="productId" value="${book.id}">
                <button type="submit" class="btn btn-danger">Xác nhận xóa vĩnh viễn</button>
                <a href="${pageContext.request.contextPath}/admin/products?view=list" class="btn btn-secondary">Hủy bỏ</a>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
