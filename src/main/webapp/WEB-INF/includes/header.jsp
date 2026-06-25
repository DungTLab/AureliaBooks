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
        <nav class="navbar navbar-expand-lg bg-white border-bottom py-2 shadow-sm">
            <div class="container"> 
                <div class="d-flex w-100 align-items-center justify-content-between gap-4">

                    <div class="d-flex align-items-center">
                        <a href="${pageContext.request.contextPath}/" class="navbar-brand me-0">
                            <img src="${pageContext.request.contextPath}/assets/images/Logo/main-logo.png" alt="AureliaBooks" class="img-logo" style="height: 50px;"/>
                        </a>
                    </div>

                    <div class="dropdown position-static">
                        <a class="nav-link text-secondary fs-4 p-2" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false" title="Categories">
                            <i class="bi bi-grid-3x3-gap-fill text-dark"></i> 
                        </a>

                        <ul class="dropdown-menu w-100 p-3 shadow-sm">
                            <li>
                                <div class="d-flex align-items-center justify-content-start gap-4">
                                    <a class="dropdown-item fw-bold text-dark rounded py-2 px-3 border-end" href="#">Sách trong nước</a>
                                    <a class="dropdown-item fw-bold text-dark rounded py-2 px-3 border-end" href="#">Sách nước ngoài</a>
                                    <a class="dropdown-item fw-bold text-dark rounded py-2 px-3 border-end" href="#">VPP - Dụng cụ học sinh</a>
                                    <a class="dropdown-item fw-bold text-dark rounded py-2 px-3" href="#">Đồ chơi</a>
                                </div>
                            </li>
                        </ul>
                    </div>

                    <form class="d-flex flex-grow-1 mx-2" role="search" style="max-width: 550px;">
                        <div class="input-group">
                            <input class="form-control border-end-0 py-2" type="search" placeholder="Tìm kiếm sách..." aria-label="Search"/>
                            <button class="btn btn-danger px-4" type="submit">
                                <i class="bi bi-search"></i>
                            </button>
                        </div>
                    </form>

                    <div class="d-none d-lg-flex align-items-center gap-4">

                        <a href="#" class="nav-link d-flex align-items-center gap-2 text-secondary position-relative py-2 text-dark">
                            <span class="badge bg-danger p-1 rounded-circle position-absolute" style="top: 2px; left: 15px; width: 6px; height: 6px;"></span>
                            <i class="bi bi-bell fs-5"></i>
                            <span class="small d-none d-xl-inline">Thông báo</span>
                        </a>

                        <a href="<%= request.getContextPath() %>/cart?action=view" class="nav-link d-flex align-items-center gap-2 text-secondary py-2 text-dark">
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

                                        <%-- Nếu là ADMIN thì hiển thị thêm nút truy cập trang Quản trị --%>
                                        <c:if test="${sessionScope.user.roleName eq 'ADMIN'}">
                                            <li><hr class="dropdown-divider"></li>
                                            <li><a class="dropdown-item text-danger fw-bold" href="${pageContext.request.contextPath}/admin/orders"><i class="bi bi-speedometer2 me-2"></i>Quản trị hệ thống</a></li>
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

                </div>
            </div>
        </nav>