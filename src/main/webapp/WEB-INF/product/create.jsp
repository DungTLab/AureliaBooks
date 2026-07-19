<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <h2>Thêm Mới ${productType == 'stationery' ? 'Văn Phòng Phẩm' : 'Sách'} (Admin)</h2>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger" role="alert">
            ${errorMessage}
        </div>
    </c:if>

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success" role="alert">
            ${successMessage}
        </div>
    </c:if>

    <!-- Form thêm mới sách (Skeleton để Dev 3 triển khai theo luồng TPT Transaction) -->
    <form action="${pageContext.request.contextPath}/admin/products?view=create" method="POST" enctype="multipart/form-data">
        <input type="hidden" name="productType" value="${productType}">
        <div class="row">
            <div class="col-md-6">
                <h4 class="mb-3">Thông tin sản phẩm chung</h4>
                <div class="mb-3">
                    <label for="title" class="form-label">Tiêu đề sách</label>
                    <input type="text" class="form-control" id="title" name="title" value="${param.title}" required>
                </div>
                <div class="mb-3">
                    <label for="categoryId" class="form-label">Danh mục</label>
                    <select class="form-select" id="categoryId" name="categoryId" required>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat.id}">${cat.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="mb-3">
                    <label for="price" class="form-label">Giá tiền</label>
                    <input type="number" step="1000" min="1000" class="form-control" id="price" name="price" value="${param.price}" required>
                </div>
                <div class="mb-3">
                    <label for="sku" class="form-label">Mã SKU</label>
                    <input type="text" class="form-control" id="sku" name="sku" value="${param.sku}">
                </div>
                <div class="mb-3">
                    <label for="image" class="form-label">Hình ảnh minh họa</label>
                    <input type="file" class="form-control" id="image" name="image">
                </div>
                <div class="mb-3">
                    <label for="description" class="form-label">Mô tả sản phẩm</label>
                    <textarea class="form-control" id="description" name="description" rows="3">${param.description}</textarea>
                </div>
            </div>
            
            <div class="col-md-6">
                <c:choose>
                    <c:when test="${productType == 'book'}">
                        <h4 class="mb-3">Thông số chi tiết Sách</h4>
                        <div class="mb-3">
                            <label for="publisherId" class="form-label">Nhà xuất bản</label>
                            <select class="form-select" id="publisherId" name="publisherId">
                                <c:forEach var="pub" items="${publishers}">
                                    <option value="${pub.id}">${pub.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="translator" class="form-label">Dịch giả</label>
                            <input type="text" class="form-control" id="translator" name="translator" value="${param.translator}">
                        </div>
                        <div class="mb-3">
                            <label for="publicationYear" class="form-label">Năm xuất bản</label>
                            <input type="number" class="form-control" id="publicationYear" name="publicationYear" value="${param.publicationYear}">
                        </div>
                        <div class="mb-3">
                            <label for="numberOfPages" class="form-label">Số trang</label>
                            <input type="number" class="form-control" id="numberOfPages" name="numberOfPages" value="${param.numberOfPages}">
                        </div>
                        <div class="mb-3">
                            <label for="coverType" class="form-label">Loại bìa</label>
                            <input type="text" class="form-control" id="coverType" name="coverType" value="${param.coverType}">
                        </div>
                        <div class="mb-3">
                            <label for="language" class="form-label">Ngôn ngữ</label>
                            <input type="text" class="form-control" id="language" name="language" value="${not empty param.language ? param.language : 'Tiếng Việt'}">
                        </div>
                    </c:when>
                    <c:when test="${productType == 'stationery'}">
                        <h4 class="mb-3">Thông số chi tiết Văn phòng phẩm</h4>
                        <div class="mb-3">
                            <label for="brandId" class="form-label">Thương hiệu</label>
                            <select class="form-select" id="brandId" name="brandId">
                                <option value="">-- Chọn Thương hiệu --</option>
                                <c:forEach var="brand" items="${brands}">
                                    <option value="${brand.id}">${brand.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="supplierId" class="form-label">Nhà cung cấp</label>
                            <select class="form-select" id="supplierId" name="supplierId">
                                <option value="">-- Chọn Nhà cung cấp --</option>
                                <c:forEach var="supplier" items="${suppliers}">
                                    <option value="${supplier.id}">${supplier.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="origin" class="form-label">Xuất xứ</label>
                            <input type="text" class="form-control" id="origin" name="origin" value="${param.origin}">
                        </div>
                        <div class="mb-3">
                            <label for="material" class="form-label">Chất liệu</label>
                            <input type="text" class="form-control" id="material" name="material" value="${param.material}">
                        </div>
                        <div class="mb-3">
                            <label for="color" class="form-label">Màu sắc</label>
                            <input type="text" class="form-control" id="color" name="color" value="${param.color}">
                        </div>
                        <div class="mb-3">
                            <label for="weight" class="form-label">Trọng lượng (g)</label>
                            <input type="number" step="0.01" class="form-control" id="weight" name="weight" value="${param.weight}">
                        </div>
                        <div class="mb-3">
                            <label for="dimensions" class="form-label">Kích thước</label>
                            <input type="text" class="form-control" id="dimensions" name="dimensions" value="${param.dimensions}">
                        </div>
                        <div class="mb-3">
                            <label for="specifications" class="form-label">Thông số kỹ thuật</label>
                            <textarea class="form-control" id="specifications" name="specifications" rows="2">${param.specifications}</textarea>
                        </div>
                        <div class="mb-3">
                            <label for="warning" class="form-label">Cảnh báo (nếu có)</label>
                            <input type="text" class="form-control" id="warning" name="warning" value="${param.warning}">
                        </div>
                    </c:when>
                </c:choose>
            </div>
        </div>
        <button type="submit" class="btn btn-success mt-4">Thêm mới</button>
        <a href="${pageContext.request.contextPath}/admin/products?view=list" class="btn btn-secondary mt-4">Quay lại</a>
    </form>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
