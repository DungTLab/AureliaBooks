<%@page contentType="text/html" pageEncoding="UTF-8"%>





<footer class="bg-dark text-light pt-5 pb-4">
    <div class="container text-center text-md-start">
        <div class="row text-center text-md-start">

            <!-- Column 1: About & Slogan -->
            <div class="col-md-3 col-lg-3 col-xl-3 mx-auto mt-3">
                <h5 class="text-uppercase mb-4 font-weight-bold text-warning">AureliaBooks</h5>
                <p>Khơi nguồn tri thức, mở lối thành công. Chúng tôi mang đến không gian sách đa dạng, chất lượng giúp bạn khám phá thế giới và phát triển bản thân mỗi ngày.</p>
            </div>

            <!-- Column 2: Book Categories -->
            <div class="col-md-2 col-lg-2 col-xl-2 mx-auto mt-3">
                <h5 class="text-uppercase mb-4 font-weight-bold text-warning">Danh mục sách</h5>
                <p><a href="${pageContext.request.contextPath}/products?categoryId=4" class="text-light text-decoration-none">Sách Văn Học</a></p>
                <p><a href="${pageContext.request.contextPath}/products?categoryId=5" class="text-light text-decoration-none">Kinh Tế - Kỹ Năng</a></p>
                <p><a href="${pageContext.request.contextPath}/products?categoryId=6" class="text-light text-decoration-none">Sách Thiếu Nhi</a></p>
                <p><a href="${pageContext.request.contextPath}/products?categoryId=7" class="text-light text-decoration-none">Truyện Tranh - Manga</a></p>
                <p><a href="${pageContext.request.contextPath}/categories" class="text-warning fw-bold text-decoration-none small"><i class="bi bi-chevron-right"></i> Xem tất cả danh mục</a></p>
            </div>

             <!-- Column 3: Quick Links / Policies -->
             <div class="col-md-3 col-lg-2 col-xl-2 mx-auto mt-3">
                 <h5 class="text-uppercase mb-4 font-weight-bold text-warning">Chính sách</h5>
                 <p><a href="${pageContext.request.contextPath}/" class="text-light text-decoration-none">Trang chủ</a></p>
                 <p><a href="${pageContext.request.contextPath}/policy?type=return" class="text-light text-decoration-none">Chính sách đổi trả</a></p>
                 <p><a href="${pageContext.request.contextPath}/policy?type=privacy" class="text-light text-decoration-none">Chính sách bảo mật</a></p>
                 <p><a href="${pageContext.request.contextPath}/policy?type=terms" class="text-light text-decoration-none">Điều khoản dịch vụ</a></p>
             </div>

            <!-- Column 4: Contact Info -->
            <div class="col-md-4 col-lg-3 col-xl-3 mx-auto mt-3">
                <h5 class="text-uppercase mb-4 font-weight-bold text-warning">Liên hệ</h5>
                <p><i class="bi bi-geo-alt-fill me-2"></i>600, Nguyễn Văn Cừ nối dài, TP.Cần Thơ</p>
                <p><i class="bi bi-envelope-fill me-2"></i>Dungletiense@gmail.com</p>
                <p><i class="bi bi-telephone-fill me-2"></i> +84 349 747 317</p>
                <p><i class="bi bi-clock-fill me-2"></i> Giờ mở cửa: 8:00 - 22:00</p>
            </div>

        </div>

        <hr class="mb-4">

        <!-- Dòng bản quyền và MXH -->
        <div class="row align-items-center">
            <div class="col-md-7 col-lg-8 text-center text-md-start">
                <p class="mb-0">© 2026 Toàn bộ bản quyền thuộc về: 
                    <a href="${pageContext.request.contextPath}/" class="text-warning text-decoration-none"><strong>AureliaBooks</strong></a>
                </p>
            </div>

            <div class="col-md-5 col-lg-4 text-center text-md-end mt-3 mt-md-0">
                <ul class="list-unstyled list-inline mb-0">
                    <li class="list-inline-item">
                        <a href="#" class="btn-sm text-light fs-4 me-3"><i class="bi bi-facebook"></i></a>
                    </li>
                    <li class="list-inline-item">
                        <a href="#" class="btn-sm text-light fs-4 me-3"><i class="bi bi-instagram"></i></a>
                    </li>
                    <li class="list-inline-item">
                        <a href="#" class="btn-sm text-light fs-4 me-3"><i class="bi bi-tiktok"></i></a>
                    </li>
                    <li class="list-inline-item">
                        <a href="#" class="btn-sm text-light fs-4"><i class="bi bi-shopee"></i></a>
                    </li>
                </ul>
            </div>
        </div>

    </div>
</footer>
<script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.js"></script>

<!-- Global Toast Notification Container -->
<div class="toast-container position-fixed bottom-0 end-0 p-3" style="z-index: 1100;">
    <div id="cartToast" class="toast align-items-center text-white bg-success border-0" role="alert" aria-live="assertive" aria-atomic="true" data-bs-delay="3000">
        <div class="d-flex">
            <div class="toast-body">
                <i class="bi bi-check-circle-fill me-2"></i> Đã thêm sản phẩm vào giỏ hàng thành công!
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    </div>
</div>

<script>
    function addToCartGlobal(productId, quantity, redirect) {
        var contextPath = "${pageContext.request.contextPath}";
        var url = "";
        if (redirect) {
            url = contextPath + "/checkout?action=buyNow&productId=" + productId + "&quantity=" + quantity;
        } else {
            url = contextPath + "/cart?action=add&productId=" + productId + "&quantity=" + quantity;
        }
        
        fetch(url, {
            method: 'POST'
        }).then(response => {
            if (response.redirected && response.url.includes("login")) {
                window.location.href = response.url;
            } else {
                if (redirect) {
                    window.location.href = contextPath + "/checkout";
                } else {
                    var toastEl = document.getElementById('cartToast');
                    var toast = new bootstrap.Toast(toastEl);
                    toast.show();
                }
            }
        }).catch(error => {
            console.error('Error:', error);
            alert("Có lỗi xảy ra khi thêm vào giỏ hàng!");
        });
    }
</script>
</body>
</html>
