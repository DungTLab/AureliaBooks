<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <h2>Cập Nhật Sách (Admin)</h2>
    <!-- Form cập nhật sách (Skeleton để Dev 3 triển khai theo luồng TPT Transaction) -->
    <form action="${pageContext.request.contextPath}/product?view=update" method="POST" enctype="multipart/form-data">
        <input type="hidden" name="productId" value="${book.id}">
        <div class="row">
            <div class="col-md-6">
                <h4 class="mb-3">Thông tin sản phẩm chung</h4>
                <div class="mb-3">
                    <label for="title" class="form-label">Tiêu đề sách</label>
                    <input type="text" class="form-control" id="title" name="title" value="${book.title}" required>
                </div>
                <div class="mb-3">
                    <label for="categoryId" class="form-label">Danh mục</label>
                    <select class="form-select" id="categoryId" name="categoryId" required>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat.id}" ${cat.id == book.categoryId ? 'selected' : ''}>${cat.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="mb-3">
                    <label for="price" class="form-label">Giá tiền</label>
                    <input type="number" step="0.01" class="form-control" id="price" name="price" value="${book.price}" required>
                </div>
                <div class="mb-3">
                    <label for="sku" class="form-label">Mã SKU</label>
                    <input type="text" class="form-control" id="sku" name="sku" value="${book.sku}">
                </div>
                <div class="mb-3">
                    <label for="image" class="form-label">Hình ảnh minh họa</label>
                    <input type="file" class="form-control" id="image" name="image">
                    <p class="text-muted mt-1">Ảnh hiện tại: ${book.imageUrl}</p>
                </div>
                <div class="mb-3">
                    <label for="description" class="form-label">Mô tả sản phẩm</label>
                    <textarea class="form-control" id="description" name="description" rows="3">${book.description}</textarea>
                </div>
            </div>
            
            <div class="col-md-6">
                <h4 class="mb-3">Thông số chi tiết Sách</h4>
                <div class="mb-3">
                    <label for="publisherId" class="form-label">Nhà xuất bản</label>
                    <select class="form-select" id="publisherId" name="publisherId">
                        <c:forEach var="pub" items="${publishers}">
                            <option value="${pub.id}" ${pub.id == book.publisherId ? 'selected' : ''}>${pub.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="mb-3">
                    <label for="translator" class="form-label">Dịch giả</label>
                    <input type="text" class="form-control" id="translator" name="translator" value="${book.translator}">
                </div>
                <div class="mb-3">
                    <label for="publicationYear" class="form-label">Năm xuất bản</label>
                    <input type="number" class="form-control" id="publicationYear" name="publicationYear" value="${book.publicationYear}">
                </div>
                <div class="mb-3">
                    <label for="numberOfPages" class="form-label">Số trang</label>
                    <input type="number" class="form-control" id="numberOfPages" name="numberOfPages" value="${book.numberOfPages}">
                </div>
                <div class="mb-3">
                    <label for="coverType" class="form-label">Loại bìa</label>
                    <input type="text" class="form-control" id="coverType" name="coverType" value="${book.coverType}">
                </div>
                <div class="mb-3">
                    <label for="language" class="form-label">Ngôn ngữ</label>
                    <input type="text" class="form-control" id="language" name="language" value="${book.language}">
                </div>
            </div>
        </div>
        <button type="submit" class="btn btn-primary mt-4">Cập nhật</button>
        <a href="${pageContext.request.contextPath}/product?view=list" class="btn btn-secondary mt-4">Quay lại</a>
    </form>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
