# 📚 AureliaBooks — Online Book & Stationery E-Commerce Platform

> **Đồ Án Môn Học:** PRJ301 — Java Web Application Development  
> **Kiến trúc:** Model-View-Controller (MVC) | Vertical Slice Architecture  
> **Công nghệ:** Java Servlet / JSP (Jakarta EE 10), SQL Server, Maven, Bootstrap 5

---

## 📖 1. Giới Thiệu Dự Án

**AureliaBooks** là hệ thống thương mại điện tử chuyên kinh doanh các sản phẩm Tri thức (Sách trong nước, Sách ngoại văn) và Văn phòng phẩm (Dụng cụ học tập, Thiết bị văn phòng). System giải quyết nhu cầu mua sắm trực tuyến tiện lợi cho khách hàng và cung cấp bộ công cụ quản lý toàn diện (Kho hàng, Đơn hàng, Mã giảm giá, Thống kê doanh thu, Chăm sóc khách hàng) cho chủ cửa hàng và quản trị viên.

### 🌟 Điểm Nổi Bật Kỹ Thuật
- **Đăng Nhập Google OAuth 2.0:** Tích hợp API đăng nhập nhanh không cần mật khẩu.
- **Bảo Mật Mật Khẩu BCrypt:** Mã hóa mật khẩu một chiều an toàn với muối ngẫu nhiên.
- **Giao Dịch Hoàn Kho An Toàn (SQL Transaction):** Xử lý quy trình trả hàng và hoàn số lượng kho trong 1 giao dịch nguyên tố (all-or-nothing), đảm bảo toàn vẹn dữ liệu.
- **Quản Lý Tác Giả Đa Chiều (Contributor n:n):** Hỗ trợ liên kết 1 cuốn sách với nhiều tác giả thông qua bảng trung gian `Contributor`.
- **Áp Dụng Mã Giảm Giá (Voucher Engine):** Tự động kiểm tra thời hạn, giá trị đơn tối thiểu và tính toán mức giảm tối đa.
- **Phân Trang & Tìm Kiếm Tối Ưu:** Tìm kiếm sản phẩm toàn diện và phân trang hiệu năng cao với `OFFSET/FETCH`.

---

## 👥 2. Phân Công Nhiệm Vụ Thành Viên (Team Contributions)

| # | Thành viên | Vai trò | Chức năng đảm nhận (Vertical Slice) |
|---|---|---|---|
| 1 | **Lê Tiến Dũng** | **Team Lead** (Dev 1) | • Khung hệ thống (BaseDAO, Security Filter)<br>• Đăng ký, Đăng nhập truyền thống & **Google OAuth 2.0**<br>• Hồ sơ cá nhân (Profile) & Đổi mật khẩu<br>• CRUD Thương hiệu (Brands) |
| 2 | **Huỳnh Nhật Duy** | Dev 2 | • Quản lý Danh mục (Categories CRUD)<br>• Quản lý Sản phẩm Admin (Form động Sách & VPP)<br>• Tích hợp Tác giả - Sách liên kết nhiều-nhiều (Contributor)<br>• Quản lý Kho hàng (Inventory Dashboard & Lịch sử nhập/xuất)<br>• Tìm kiếm, Lọc & Phân trang phía Khách hàng |
| 3 | **Nguyễn Phú Trọng** | Dev 3 | • Quản lý Giỏ hàng (Cart)<br>• Đặt hàng COD (Checkout)<br>• Xử lý tính toán & áp dụng Mã giảm giá (Voucher) lúc Checkout<br>• CRUD Nhà cung cấp (Suppliers)<br>• Báo cáo & Thống kê doanh thu, Top sản phẩm bán chạy |
| 4 | **Nguyễn Trần Đức Anh** | Dev 4 | • Quản lý Đơn hàng cá nhân & Gửi yêu cầu Trả hàng (Customer)<br>• Quản lý & Duyệt Đơn hàng (Admin)<br>• Duyệt Trả hàng & Tự động hoàn kho an toàn với Transaction<br>• CRUD Mã giảm giá (Vouchers/Discounts) |
| 5 | **Trần Huỳnh Giác** | Dev 5 | • Hệ thống Yêu cầu hỗ trợ (Support Tickets - Khách gửi & Admin phản hồi)<br>• CRUD Tác giả (Authors)<br>• CRUD Nhà xuất bản (Publishers) |

---

## 🛠️ 3. Công Nghệ Sử Dụng (Tech Stack)

- **Backend:** Java 17, Jakarta EE 10 (Servlet 6.0, JSP 3.1), JSTL.
- **Database:** Microsoft SQL Server.
- **Build Tool & Dependency Management:** Apache Maven.
- **Frontend & UI:** HTML5, CSS3, JavaScript, Bootstrap 5, Bootstrap Icons.
- **Security & Libraries:**
  - `jBCrypt` (Mã hóa mật khẩu)
  - `Gson` (Xử lý JSON API Google)
  - `Java HTTP Client` (Gọi RESTful API Google OAuth 2.0)

---

## 🔑 4. Các Chức Năng Chính (Features)

### 👤 Khách Vãng Lai & Khách Hàng (Guest & Customer)
- **Xem & Tìm Kiếm:** Tìm kiếm sách theo từ khóa, lọc theo danh mục, phân trang sản phẩm.
- **Giỏ Hàng:** Thêm, điều chỉnh số lượng, xóa sản phẩm khỏi giỏ.
- **Tài Khoản:** Đăng ký, Đăng nhập (Mật khẩu / Google OAuth 2.0), Đổi mật khẩu, Cập nhật thông tin cá nhân.
- **Thanh Toán:** Đặt hàng nhận tiền mặt (COD), áp dụng mã giảm giá hợp lệ.
- **Quản Lý Đơn Hàng:** Xem lịch sử mua hàng, chi tiết đơn, gửi yêu cầu trả/hủy đơn.
- **Hỗ Trợ:** Gửi ticket yêu cầu trợ giúp đến ban quản trị.

### 🛡️ Quản Trị Viên & Nhân Viên (Admin & Staff)
- **Quản Lý Sản Phẩm:** CRUD Sách (chọn NXB, NCC, nhiều Tác giả) và Văn phòng phẩm (chọn Thương hiệu, NCC, xuất xứ).
- **Quản Lý Kho Hàng:** Đơn kho, cập nhật số lượng tồn kho (Nhập/Xuất), theo dõi nhật ký giao dịch.
- **Quản Lý Đơn Hàng:** Duyệt đơn (`PENDING` $\rightarrow$ `SHIPPED` $\rightarrow$ `DELIVERED`), phê duyệt yêu cầu trả hàng & tự động cộng hoàn kho.
- **Quản Lý Mã Giảm Giá:** Tạo/sửa/xóa Voucher (mã code, phần trăm giảm, thời hạn, giới hạn đơn).
- **Quản Lý Danh Mục Master Data:** CRUD Tác giả, NXB, Thương hiệu, Nhà cung cấp, Danh mục sản phẩm.
- **Hỗ Trợ Khách Hàng:** Tiếp nhận và phản hồi Ticket hỗ trợ của người dùng.
- **Thống Kê Doanh Thu:** Biểu đồ doanh thu theo thời gian và thống kê mặt hàng bán chạy.
- **Quản Lý Tài Khoản:** Quản lý danh sách người dùng, cấp quyền, khóa/mở tài khoản.

---

## 📂 5. Cấu Trúc Thư Mục Dự Án (Project Structure)

```text
AureliaBooks/
├── src/
│   ├── main/
│   │   ├── java/com/mycompany/aureliabooks/
│   │   │   ├── context/        # Cấu hình kết nối CSDL (DBContext, setup.sql)
│   │   │   ├── controller/     # Java Servlets xử lý request (23 Controllers)
│   │   │   ├── dao/            # Data Access Objects (12 DAOs)
│   │   │   ├── filter/         # Security Filters phân quyền truy cập
│   │   │   ├── model/          # Entities/POJOs (Book, Product, Order, User...)
│   │   │   └── util/           # Class tiện ích (GoogleUtils, UploadUtils, PasswordUtils)
│   │   ├── resources/          # Resource đính kèm (oauth.properties, META-INF)
│   │   └── webapp/
│   │       ├── assets/         # CSS, JS, Bootstrap Icons, Images
│   │       ├── WEB-INF/
│   │       │   ├── includes/   # Header, Footer navigation bar
│   │       │   └── [modules]/  # Thư mục chứa giao diện JSP (product, order, cart...)
│   │       └── uploads/        # Thư mục lưu hình ảnh sản phẩm tải lên
├── pom.xml                     # Cấu hình Maven Dependencies
└── README.md                   # Tài liệu hướng dẫn dự án
```

---

## 🚀 6. Hướng Dẫn Cài Đặt & Khởi Chạy (Setup & Run Guide)

### 📋 Yêu Cầu Tiền Đề
- **JDK:** Java 17 trở lên.
- **IDE:** Apache NetBeans 15+, Eclipse, hoặc VS Code.
- **Web Server:** Apache Tomcat 10.1+ (hỗ trợ Jakarta EE 10).
- **Database:** Microsoft SQL Server.
- **Build Tool:** Apache Maven.

### 🗄️ Bước 1: Cấu Hình Cơ Sở Dữ Liệu
1. Mở **SQL Server Management Studio (SSMS)**.
2. Mở file SQL khởi tạo tại đường dẫn: `src/main/java/com.mycompany.aureliabooks.context/setup.sql`.
3. Chạy script để khởi tạo Database `AureliaBooksDB` và các bảng cùng dữ liệu mẫu.
4. Chỉnh sửa thông tin đăng nhập SQL Server (User, Password, Port) trong file [`DBContext.java`](file:///D:/FPTU_document/SE_4/PRJ301/PRJ_asignment/AureliaBooks/src/main/java/com/mycompany/aureliabooks/context/DBContext.java) nếu cần:
   ```java
   private final String userID = "sa";
   private final String password = "123";
   ```

### 🔑 Bước 2: Cấu Hình Google OAuth 2.0 (Tùy chọn)
Tạo file `src/main/resources/oauth.properties` với thông tin Client ID và Client Secret từ Google Cloud Console:
```properties
google.client_id=YOUR_GOOGLE_CLIENT_ID.apps.googleusercontent.com
google.client_secret=YOUR_GOOGLE_CLIENT_SECRET
```

### 💻 Bước 3: Biên Dịch & Chạy Dự Án
1. Mở dự án trong NetBeans / IDE của bạn.
2. Chạy lệnh Maven build:
   ```bash
   mvn clean install
   ```
3. Deploy dự án lên Apache Tomcat server và truy cập tại đường dẫn:
   `http://localhost:8080/AureliaBooks/`

---

## 🔒 7. Bảo Mật & An Toàn Thông Tin

- File chứa thông tin nhạy cảm `oauth.properties` được đưa vào `.gitignore` để tránh rò rỉ dữ liệu lên Repository công khai.
- Mật khẩu người dùng được băm qua BCrypt trước khi lưu trữ vào CSDL.
- Bộ lọc `SecurityFilter` kiểm soát chặt chẽ quyền hạn truy cập theo vai trò (`GUEST`, `CUSTOMER`, `EMPLOYEE`, `ADMIN`).

---

## 📄 8. Giấy Phép & Bản Quyền

Dự án được phát triển phục vụ mục đích học tập môn **PRJ301 - Java Web Application Development** tại Đại học FPT.  
© 2026 Nhóm Phát Triển AureliaBooks. All Rights Reserved.