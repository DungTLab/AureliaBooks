<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <h2 class="mb-4">Báo Cáo Thống Kê Doanh Thu & Sản Phẩm</h2>
    
    <!-- Bộ lọc báo cáo -->
    <div class="card mb-4 shadow-sm">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/admin/reports" method="GET" class="row g-3 align-items-center">
                <div class="col-auto">
                    <label for="type" class="col-form-label">Chọn kiểu báo cáo:</label>
                </div>
                <div class="col-auto">
                    <select class="form-select" id="type" name="type">
                        <option value="DAY" ${param.type == 'DAY' ? 'selected' : ''}>Theo Ngày</option>
                        <option value="MONTH" ${param.type == 'MONTH' ? 'selected' : ''}>Theo Tháng</option>
                        <option value="QUARTER" ${param.type == 'QUARTER' ? 'selected' : ''}>Theo Quý</option>
                    </select>
                </div>
                <div class="col-auto">
                    <button type="submit" class="btn btn-primary">Xem báo cáo</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Bảng hiển thị số liệu thống kê (Skeleton để Dev 2 triển khai) -->
    <div class="row">
        <div class="col-md-6 mb-4">
            <div class="card shadow-sm h-100">
                <div class="card-body">
                    <h5 class="card-title">Báo cáo tổng doanh thu</h5>
                    <hr>
                    <table class="table table-bordered">
                        <thead>
                            <tr>
                                <th>Thời gian</th>
                                <th>Tổng doanh thu (VNĐ)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="rev" items="${revenueData}">
                                <tr>
                                    <td>${rev.key}</td>
                                    <td>${rev.value} VNĐ</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="col-md-6 mb-4">
            <div class="card shadow-sm h-100">
                <div class="card-body">
                    <h5 class="card-title">Thống kê sản phẩm bán chạy</h5>
                    <hr>
                    <table class="table table-bordered">
                        <thead>
                            <tr>
                                <th>Mã SKU</th>
                                <th>Tiêu đề sách</th>
                                <th>Số lượng đã bán</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${bestSellingData}">
                                <tr>
                                    <td>${item.sku}</td>
                                    <td>${item.title}</td>
                                    <td>${item.salesQuantity}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
