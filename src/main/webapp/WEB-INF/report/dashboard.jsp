<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/includes/header.jsp" />

<div class="container my-5">
    <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 mb-4">
        <div>
            <h2 class="mb-1">Báo cáo doanh thu</h2>
            <p class="text-muted mb-0">Theo dõi doanh thu từ các đơn đã xác nhận, đang giao hoặc hoàn thành.</p>
        </div>
        <form action="${pageContext.request.contextPath}/admin/reports" method="GET" class="d-flex gap-2">
            <select class="form-select" id="type" name="type" aria-label="Chọn kiểu báo cáo">
                <option value="DAY" ${reportType == 'DAY' ? 'selected' : ''}>Theo ngày</option>
                <option value="MONTH" ${reportType == 'MONTH' ? 'selected' : ''}>Theo tháng</option>
                <option value="QUARTER" ${reportType == 'QUARTER' ? 'selected' : ''}>Theo quý</option>
            </select>
            <button type="submit" class="btn btn-danger text-nowrap">
                <i class="bi bi-funnel me-1"></i>Xem
            </button>
        </form>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert">
            ${error}
        </div>
    </c:if>

    <div class="row g-3 mb-4">
        <div class="col-12 col-md-6 col-xl-3">
            <div class="card shadow-sm h-100">
                <div class="card-body">
                    <div class="text-muted small mb-2">Tổng doanh thu</div>
                    <div class="h4 mb-0 text-danger">
                        <fmt:formatNumber value="${revenueSummary.totalRevenue}" type="number" maxFractionDigits="0" /> VNĐ
                    </div>
                </div>
            </div>
        </div>
        <div class="col-12 col-md-6 col-xl-3">
            <div class="card shadow-sm h-100">
                <div class="card-body">
                    <div class="text-muted small mb-2">Đơn tính doanh thu</div>
                    <div class="h4 mb-0">
                        <fmt:formatNumber value="${revenueSummary.totalOrders}" type="number" maxFractionDigits="0" />
                    </div>
                </div>
            </div>
        </div>
        <div class="col-12 col-md-6 col-xl-3">
            <div class="card shadow-sm h-100">
                <div class="card-body">
                    <div class="text-muted small mb-2">Giá trị trung bình</div>
                    <div class="h4 mb-0">
                        <fmt:formatNumber value="${revenueSummary.averageOrderValue}" type="number" maxFractionDigits="0" /> VNĐ
                    </div>
                </div>
            </div>
        </div>
        <div class="col-12 col-md-6 col-xl-3">
            <div class="card shadow-sm h-100">
                <div class="card-body">
                    <div class="text-muted small mb-2">Số lượng đã bán</div>
                    <div class="h4 mb-0">
                        <fmt:formatNumber value="${revenueSummary.totalSoldQuantity}" type="number" maxFractionDigits="0" />
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row g-4">
        <div class="col-12 col-xl-7">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-white fw-bold">
                    <i class="bi bi-cash-coin me-2 text-danger"></i>Doanh thu theo kỳ
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle">
                            <thead class="table-light">
                                <tr>
                                    <th>Thời gian</th>
                                    <th class="text-end">Doanh thu</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty revenueData}">
                                        <tr>
                                            <td colspan="2" class="text-center text-muted py-4">
                                                Chưa có dữ liệu doanh thu.
                                            </td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="rev" items="${revenueData}">
                                            <c:url var="periodUrl" value="/admin/reports">
                                                <c:param name="type" value="${reportType}" />
                                                <c:param name="period" value="${rev.key}" />
                                            </c:url>
                                            <tr class="${selectedPeriod eq rev.key ? 'table-danger' : ''}">
                                                <td>
                                                    <a href="${periodUrl}" class="${selectedPeriod eq rev.key ? 'fw-bold text-danger' : 'text-danger text-decoration-none'}">
                                                        ${rev.key}
                                                    </a>
                                                </td>
                                                <td class="text-end fw-semibold">
                                                    <fmt:formatNumber value="${rev.value}" type="number" maxFractionDigits="0" /> VNĐ
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-12 col-xl-5">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-white fw-bold d-flex justify-content-between align-items-center gap-2">
                    <span><i class="bi bi-trophy me-2 text-danger"></i>Sản phẩm bán chạy</span>
                    <c:if test="${not empty selectedPeriod}">
                        <span class="badge text-bg-danger">${selectedPeriod}</span>
                    </c:if>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle">
                            <thead class="table-light">
                                <tr>
                                    <th>SKU</th>
                                    <th>Sản phẩm</th>
                                    <th class="text-end">Đã bán</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty bestSellingData}">
                                        <tr>
                                            <td colspan="3" class="text-center text-muted py-4">
                                                Chưa có sản phẩm bán chạy trong kỳ này.
                                            </td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="item" items="${bestSellingData}">
                                            <tr>
                                                <td>${item.sku}</td>
                                                <td>${item.title}</td>
                                                <td class="text-end fw-semibold">${item.salesQuantity}</td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
