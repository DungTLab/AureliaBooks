
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file=".././includes/header.jsp" %>



<div class="container container-fluid mt-3 mb-3">

    <div class="row g-4">
        <div class="col-lg-5 ">

            <div class="mb-2">
                <c:choose>
                    <c:when test="${not empty product.imageUrl && product.imageUrl.contains('/')}">
                        <img class="img-fluid" src="${pageContext.request.contextPath}/uploads/${product.imageUrl}" 
                             alt="${product.title}" style="width: 100%; height: 400px; object-fit: contain;" />
                    </c:when>
                    <c:otherwise>
                        <img class="img-fluid" src="${pageContext.request.contextPath}/assets/images/book-image/${product.imageUrl}" 
                             alt="${product.title}" style="width: 100%; height: 400px; object-fit: contain;" />
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="d-flex gap-2">
                <c:choose>
                    <c:when test="${sessionScope.user.roleName eq 'ADMIN' or sessionScope.user.roleName eq 'EMPLOYEE'}">
                        <button type="button" class="btn btn-secondary w-100" disabled><i class="bi bi-info-circle me-1"></i>Tài khoản Quản trị không được mua hàng</button>
                    </c:when>
                    <c:otherwise>
                        <button type="button" class="btn btn-outline-danger w-50" onclick="addToCart(false)">Thêm vào giỏ hàng</button>
                        <button type="button" class="btn btn-danger w-50" onclick="addToCart(true)">Mua ngay</button>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <c:choose>
            <c:when test="${product.productType == 'Book'}">   
                <div class="col-lg-7 ">
                    <h4 class="fw-bold mb-4">${product.title}</h4>
                    <div class="row">
                        <div class="col-md-6 w-50">
                            <p>Nhà cung cấp: ${product.supplierName}</p>
                        </div>

                        <div class="col-md-6 w-50">
                            <p>Tác giả: ${product.authorName}</p>
                        </div>

                    </div>

                    <div class="row">
                        <div class="col-md-6 w-50">
                            <p>Nhà xuất bản: ${product.publisherName}</p>
                        </div>

                        <div class="col-md-6 w-50">
                            <p>Hình thức bìa: ${product.coverType}</p>
                        </div>
                    </div>

                    <div class="row align-items-center mb-4">
                        <div class="col-3 text-secondary" style="font-size: 14px;">Số lượng:</div>
                        <div class="col-9">
                            <div class="input-group d-inline-flex" style="width: 140px;">
                                <button class="btn border text-secondary bg-light px-2" type="button" onclick="decreaseQuantity()"><i class="bi bi-dash"></i></button>
                                <input type="text" id="quantityInput" class="form-control text-center fw-bold border-start-0 border-end-0 bg-white px-1" value="1" oninput="this.value = this.value.replace(/[^0-9]/g, '');" onblur="validateQuantity(this)">
                                <button class="btn border text-secondary bg-light px-2" type="button" onclick="increaseQuantity()"><i class="bi bi-plus"></i></button>
                            </div>
                            <span class="text-secondary ms-3" style="font-size: 14px;">(Kho còn: ${product.stock})</span>
                        </div>
                    </div>



                    <h5 class="fw-bold mb-4" style="font-size: 18px;">Thông tin chi tiết</h5>
                    <table class="table table-borderless mb-0">
                        <tbody>
                            <tr>
                                <td class="text-secondary" style="width: 30%; font-size: 14px;">Mã hàng</td>
                                <td class="fw-medium text-dark" style="font-size: 14px;">${product.sku}</td>
                            </tr>
                            <tr>
                                <td class="text-secondary" style="width: 30%; font-size: 14px;">Độ tuổi</td>
                                <td class="fw-medium text-dark" style="font-size: 14px;">15+</td>
                            </tr>
                            <tr>
                                <td class="text-secondary" style="width: 30%; font-size: 14px;">Tên Nhà Cung Cấp</td>
                                <td class="fw-medium text-dark" style="font-size: 14px;"><a href="#" class="text-decoration-none">${product.supplierName}</a></td>
                            </tr>
                            <tr>
                                <td class="text-secondary" style="width: 30%; font-size: 14px;">Tác giả</td>
                                <td class="fw-medium text-dark" style="font-size: 14px;">${product.authorName}</td>
                            </tr>
                            <tr>
                                <td class="text-secondary" style="width: 30%; font-size: 14px;">Người Dịch</td>
                                <td class="fw-medium text-dark" style="font-size: 14px;">${product.translator}</td>
                            </tr>
                            <tr>
                                <td class="text-secondary" style="width: 30%; font-size: 14px;">NXB</td>
                                <td class="fw-medium text-dark" style="font-size: 14px;">${product.publisherName}</td>
                            </tr>
                            <tr>
                                <td class="text-secondary" style="width: 30%; font-size: 14px;">Năm XB</td>
                                <td class="fw-medium text-dark" style="font-size: 14px;">${product.publicationYear}</td>
                            </tr>
                        </tbody>
                    </table>

                    <!-- Khối 4: Mô tả sản phẩm -->

                    <h5 class="fw-bold mb-3" style="font-size: 18px;">Mô tả sản phẩm</h5>
                    <p class="fw-bold text-dark mb-2">${product.title}</p>
                    <div class="text-dark" style="font-size: 14px; text-align: justify; line-height: 1.7;">
                        ${product.description}
                    </div>

                </div>
            </c:when>

            <c:when test="${product.productType =='Stationery'}">   
                <div class="col-lg-7 ">
                    <h4 class="fw-bold mb-4">${product.title}</h4>
                    <div class="row">
                        <div class="col-md-6 w-50">
                            <p>Thương hiệu: ${product.brandName}</p>
                        </div>

                        <div class="col-md-6 w-50">
                            <p>Xuất xứ: ${product.origin}</p>
                        </div>

                    </div>

                    <div class="row align-items-center mb-4">
                        <div class="col-3 text-secondary" style="font-size: 14px;">Số lượng:</div>
                        <div class="col-9">
                            <div class="input-group d-inline-flex" style="width: 140px;">
                                <button class="btn border text-secondary bg-light px-2" type="button" onclick="decreaseQuantity()"><i class="bi bi-dash"></i></button>
                                <input type="text" id="quantityInput" class="form-control text-center fw-bold border-start-0 border-end-0 bg-white px-1" value="1" oninput="this.value = this.value.replace(/[^0-9]/g, '');" onblur="validateQuantity(this)">
                                <button class="btn border text-secondary bg-light px-2" type="button" onclick="increaseQuantity()"><i class="bi bi-plus"></i></button>
                            </div>
                            <span class="text-secondary ms-3" style="font-size: 14px;">(Kho còn: ${product.stock})</span>
                        </div>
                    </div>



                    <h5 class="fw-bold mb-4" style="font-size: 18px;">Thông tin chi tiết</h5>
                    <table class="table table-borderless mb-0">
                        <tbody>
                            <tr>
                                <td class="text-secondary" style="width: 30%; font-size: 14px;">Mã hàng</td>
                                <td class="fw-medium text-dark" style="font-size: 14px;">${product.sku}</td>
                            </tr>
                            <tr>
                                <td class="text-secondary" style="width: 30%; font-size: 14px;">Thương Hiệu</td>
                                <td class="fw-medium text-dark" style="font-size: 14px;">${product.brandName}</td>
                            </tr>
                            <tr>
                                <td class="text-secondary" style="width: 30%; font-size: 14px;">Tên Nhà Cung Cấp</td>
                                <td class="fw-medium text-dark" style="font-size: 14px;"><a href="#" class="text-decoration-none">${product.supplierName}</a></td>
                            </tr>
                            <tr>
                                <td class="text-secondary" style="width: 30%; font-size: 14px;">Màu sắc</td>
                                <td class="fw-medium text-dark" style="font-size: 14px;">${product.color}</td>
                            </tr>
                            <tr>
                                <td class="text-secondary" style="width: 30%; font-size: 14px;">Chất liệu</td>
                                <td class="fw-medium text-dark" style="font-size: 14px;">${product.material}</td>
                            </tr>
                            <tr>
                                <td class="text-secondary" style="width: 30%; font-size: 14px;">Trọng lượng (gr)</td>
                                <td class="fw-medium text-dark" style="font-size: 14px;">${product.weight}</td>
                            </tr>
                            <tr>
                                <td class="text-secondary" style="width: 30%; font-size: 14px;">Kích Thước</td>
                                <td class="fw-medium text-dark" style="font-size: 14px;">${product.dimensions}</td>
                            </tr>

                            <tr>
                                <td class="text-secondary" style="width: 30%; font-size: 14px;">Phân loại</td>
                                <td class="fw-medium text-dark" style="font-size: 14px;">${product.specifications}</td>
                            </tr>
                            <c:if test="${product.warning!=null}">
                                <tr>
                                    <td class="text-secondary" style="width: 30%; font-size: 14px;">Cảnh Báo</td>
                                    <td class="fw-medium text-danger" style="font-size: 14px;">${product.warning}</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>

                    <!-- Khối 4: Mô tả sản phẩm -->

                    <h5 class="fw-bold mb-3" style="font-size: 18px;">Mô tả sản phẩm</h5>
                    <p class="fw-bold text-dark mb-2">${product.title}</p>
                    <div class="text-dark" style="font-size: 14px; text-align: justify; line-height: 1.7;">
                        ${product.description}
                    </div>

                </div>

            </div>
        </div>
        </c:when>
    </c:choose>
</div>
</div>



<script>
    var maxStock = ${product.stock != null ? product.stock : 1};
    
    function validateQuantity(input) {
        var val = parseInt(input.value);
        if (isNaN(val) || val < 1) {
            input.value = 1;
        } else if (val > maxStock) {
            input.value = maxStock;
        }
    }

    function decreaseQuantity() {
        var input = document.getElementById('quantityInput');
        var val = parseInt(input.value);
        if (val > 1) {
            input.value = val - 1;
        }
        validateQuantity(input);
    }
    
    function increaseQuantity() {
        var input = document.getElementById('quantityInput');
        var val = parseInt(input.value);
        if (val < maxStock) {
            input.value = val + 1;
        }
        validateQuantity(input);
    }
    function addToCart(redirect) {
        var productId = "${product.id}";
        var quantity = document.getElementById('quantityInput').value;
        addToCartGlobal(productId, quantity, redirect);
    }
</script>

<%@include file=".././includes/footer.jsp" %>
