<%@page contentType="text/html" pageEncoding="UTF-8"%>


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

                        <a href="#" class="nav-link d-flex align-items-center gap-2 text-secondary py-2 text-dark">
                            <i class="bi bi-cart fs-5"></i>
                            <span class="small d-none d-xl-inline">Giỏ hàng</span>
                        </a>

<!--                                                <a href="#" class="nav-link d-flex align-items-center gap-2 text-dark fw-bold py-2">
                                                    <i class="bi bi-person fs-5"></i>
                                                    <span class="small d-none d-xl-inline">Tài khoản</span>
                                                </a>-->

                        <div class="text-dark">
                            <a href="<%= request.getContextPath() %>/auth?action=register" class="btn ">Sign up</a>|<a href="<%= request.getContextPath() %>/auth?action=login" class="btn">Login</a>
                        </div>

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