<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>AureliaBooks</title>
        <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/assets/images/Logo/sub-logo-ver2.png"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/font/font-icon-bs/bootstrap-icons.min.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    </head>
    <body>

        <%-- Navigation Bar --%>
        <nav class="navbar navbar-expand bg-white border-bottom py-2 shadow-sm flex-column">
            <div class="container flex-column flex-lg-row gap-1 gap-lg-0"> 

                <!-- ROW 1 (Mobile only): Logo centered -->
                <div class="d-flex d-lg-none w-100 justify-content-center mb-2">
                    <a href="${pageContext.request.contextPath}/" class="navbar-brand mx-auto">
                        <img src="${pageContext.request.contextPath}/assets/images/Logo/main-logo.png" alt="AureliaBooks" style="height: 38px;"/>
                    </a>
                </div>

                <!-- MAIN NAVBAR CONTENT ROW -->
                <div class="d-flex w-100 align-items-center justify-content-between gap-2 gap-lg-4">

                    <!-- Desktop Logo (Visible only on lg and above) -->
                    <div class="d-none d-lg-flex align-items-center order-1">
                        <a href="${pageContext.request.contextPath}/" class="navbar-brand me-0">
                            <img src="${pageContext.request.contextPath}/assets/images/Logo/main-logo.png" alt="AureliaBooks" style="height: 50px;"/>
                        </a>
                    </div>

                    <!-- Category Dropdown (Visible on both) -->
                    <div class="dropdown position-static order-1">
                        <a class="nav-link text-secondary fs-4 p-1 p-md-2" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false" title="Categories">
                            <i class="bi bi-grid-3x3-gap-fill text-dark"></i> 
                        </a>

                        <ul class="dropdown-menu w-100 p-3 shadow-sm">
                            <li>
                                <div class="row g-2 text-center">
                                    <div class="col-4 col-lg-4 border-end-lg">
                                        <a class="dropdown-item fw-bold text-dark rounded py-2" href="${pageContext.request.contextPath}/products?categoryId=1">Sách trong nước</a>
                                    </div>
                                    <div class="col-4 col-lg-4 border-end-lg">
                                        <a class="dropdown-item fw-bold text-dark rounded py-2" href="${pageContext.request.contextPath}/products?categoryId=2">Sách nước ngoài</a>
                                    </div>
                                    <div class="col-4 col-lg-4">
                                        <a class="dropdown-item fw-bold text-dark rounded py-2" href="${pageContext.request.contextPath}/products?categoryId=3">VPP - Dụng cụ học sinh</a>
                                    </div>
                                </div>
                            </li>
                        </ul>
                    </div>

                    <!-- Search Bar (Visible on both, flex-grow-1) -->
                    <form class="d-flex order-2 flex-grow-1 mx-1 mx-lg-2" role="search" action="${pageContext.request.contextPath}/products" method="get" style="max-width: 550px;">
                        <div class="input-group">
                            <input class="form-control border-end-0 py-2 fs-7 fs-md-6" type="search" name="query" value="${param.query}" placeholder="Tìm kiếm sách..." aria-label="Search"/>
                            <button class="btn btn-danger px-3 px-md-4" type="submit">
                                <i class="bi bi-search"></i>
                            </button>
                        </div>
                    </form>

                    <!-- Desktop Right-side Menu (Visible on lg and above) -->
                    <div class="d-none d-lg-flex order-3 align-items-center gap-4">
                        <a href="#" class="nav-link d-flex align-items-center gap-2 text-secondary position-relative py-2 text-dark">
                            <span class="badge bg-danger p-1 rounded-circle position-absolute" style="top: 2px; left: 15px; width: 6px; height: 6px;"></span>
                            <i class="bi bi-bell fs-5"></i>
                            <span class="small d-none d-xl-inline">Thông báo</span>
                        </a>

                        <a href="<%= request.getContextPath()%>/cart?action=view" class="nav-link d-flex align-items-center gap-2 text-secondary py-2 text-dark">
                            <i class="bi bi-cart fs-5"></i>
                            <span class="small d-none d-xl-inline">Giỏ hàng</span>
                        </a>

                        <c:choose>
                            <c:when test="${not empty sessionScope.user}">
                                <div class="dropdown">
                                    <a class="nav-link dropdown-toggle d-flex align-items-center gap-2 text-dark fw-bold py-2" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                        <i class="bi bi-person-circle fs-5"></i>
                                        <span class="small d-none d-xl-inline">${sessionScope.user.username}</span>
                                    </a>
                                    <ul class="dropdown-menu dropdown-menu-end shadow-sm">
                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile"><i class="bi bi-person me-2"></i>Hồ sơ cá nhân</a></li>
                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/orders"><i class="bi bi-receipt me-2"></i>Đơn hàng của tôi</a></li>

                                        <c:if test="${sessionScope.user.roleName eq 'ADMIN' or sessionScope.user.roleName eq 'EMPLOYEE'}">
                                            <li>
                                                <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/categories">
                                                    <i class="bi bi-list-ul me-2"></i>Quản lý Danh mục
                                                </a>
                                            </li>
                                            <li>
                                                <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/discounts">
                                                    <i class="bi bi-ticket-perforated me-2"></i>Quản lý Voucher
                                                </a>
                                            </li>
                                            <c:if test="${sessionScope.user.roleName eq 'ADMIN'}">
                                                <li>
                                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/reports">
                                                        <i class="bi bi-bar-chart-line me-2"></i>Báo cáo thống kê
                                                    </a>
                                                </li>
                                            </c:if>
                                            <li><hr class="dropdown-divider"></li>
                                            <li>
                                                <a class="dropdown-item text-danger fw-bold" href="${pageContext.request.contextPath}/admin/orders">
                                                    <i class="bi bi-speedometer2 me-2"></i>
                                                    <c:choose>
                                                        <c:when test="${sessionScope.user.roleName eq 'ADMIN'}">Quản trị hệ thống</c:when>
                                                        <c:otherwise>Trang Nhân viên</c:otherwise>
                                                    </c:choose>
                                                </a>
                                            </li>
                                        </c:if>

                                        <li><hr class="dropdown-divider"></li>
                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/auth?action=logout"><i class="bi bi-box-arrow-right me-2"></i>Đăng xuất</a></li>
                                    </ul>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/auth?action=login" class="nav-link d-flex align-items-center gap-2 text-dark fw-bold py-2">
                                    <i class="bi bi-person fs-5"></i>
                                    <span class="small d-none d-xl-inline">Đăng nhập</span>
                                </a>
                            </c:otherwise>
                        </c:choose>

                        <div class="dropdown">
                            <button class="btn btn-sm btn-outline-secondary dropdown-toggle py-1" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                                VI
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end text-dark">
                                <li><a class="dropdown-item" href="">EN</a></li>
                                <li><a class="dropdown-item" href="#">VI</a></li>
                            </ul>
                        </div>
                    </div>

                    <!-- Mobile Right-side Menu (Visible only below lg, styled like Fahasa with Cart and Profile Dropdown) -->
                    <div class="d-flex d-lg-none align-items-center gap-1 order-3">
                        <!-- Cart Link -->
                        <a href="<%= request.getContextPath()%>/cart?action=view" class="nav-link text-dark p-2" title="Cart">
                            <i class="bi bi-cart fs-4"></i>
                        </a>

                        <!-- User Profile Dropdown Toggle -->
                        <div class="dropdown">
                            <a class="nav-link text-dark p-2 dropdown-toggle-split" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false" title="Menu">
                                <i class="bi bi-person fs-4"></i>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end shadow-sm py-2" style="min-width: 200px;">
                                <c:choose>
                                    <c:when test="${not empty sessionScope.user}">
                                        <li class="dropdown-header border-bottom pb-2 mb-1">Tài khoản: ${sessionScope.user.username}</li>
                                        <li><a class="dropdown-item py-2" href="${pageContext.request.contextPath}/profile"><i class="bi bi-person me-2 text-secondary"></i>Hồ sơ cá nhân</a></li>
                                        <li><a class="dropdown-item py-2" href="${pageContext.request.contextPath}/orders"><i class="bi bi-receipt me-2 text-secondary"></i>Đơn hàng của tôi</a></li>

                                        <c:if test="${sessionScope.user.roleName eq 'ADMIN' or sessionScope.user.roleName eq 'EMPLOYEE'}">
                                            <li>
                                                <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/admin/categories">
                                                    <i class="bi bi-list-ul me-2 text-secondary"></i>Quản lý Danh mục
                                                </a>
                                            </li>
                                            <li>
                                                <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/admin/discounts">
                                                    <i class="bi bi-ticket-perforated me-2 text-secondary"></i>Quản lý Voucher
                                                </a>
                                            </li>
                                            <c:if test="${sessionScope.user.roleName eq 'ADMIN'}">
                                                <li>
                                                    <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/admin/reports">
                                                        <i class="bi bi-bar-chart-line me-2 text-secondary"></i>Báo cáo thống kê
                                                    </a>
                                                </li>
                                            </c:if>
                                            <li>
                                                <a class="dropdown-item text-danger fw-bold py-2" href="${pageContext.request.contextPath}/admin/orders">
                                                    <i class="bi bi-speedometer2 me-2"></i>
                                                    <c:choose>
                                                        <c:when test="${sessionScope.user.roleName eq 'ADMIN'}">Quản trị hệ thống</c:when>
                                                        <c:otherwise>Trang Nhân viên</c:otherwise>
                                                    </c:choose>
                                                </a>
                                            </li>
                                        </c:if>
                                        <li><hr class="dropdown-divider my-1"></li>
                                        <li><a class="dropdown-item py-2" href="${pageContext.request.contextPath}/auth?action=logout"><i class="bi bi-box-arrow-right me-2 text-secondary"></i>Đăng xuất</a></li>
                                        </c:when>
                                        <c:otherwise>
                                        <li><a class="dropdown-item py-2 fw-bold" href="${pageContext.request.contextPath}/auth?action=login"><i class="bi bi-box-arrow-in-right me-2 text-secondary"></i>Đăng nhập</a></li>
                                        </c:otherwise>
                                    </c:choose>
                                <li><hr class="dropdown-divider my-1"></li>
                                <li class="dropdown-header">Ngôn ngữ</li>
                                <li><a class="dropdown-item py-1" href="#">VI - Tiếng Việt</a></li>
                                <li><a class="dropdown-item py-1" href="#">EN - English</a></li>
                            </ul>
                        </div>
                    </div>

                </div>
            </div>
        </nav>