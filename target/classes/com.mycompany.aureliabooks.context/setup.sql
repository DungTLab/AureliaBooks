/*******************************************************************************
   Drop database if it exists
********************************************************************************/
IF EXISTS (SELECT name FROM master.dbo.sysdatabases WHERE name = N'AureliaBooks')
BEGIN
	ALTER DATABASE [AureliaBooks] SET OFFLINE WITH ROLLBACK IMMEDIATE;
	ALTER DATABASE [AureliaBooks] SET ONLINE;
	DROP DATABASE [AureliaBooks];
END
GO

/*******************************************************************************
   Create database
********************************************************************************/
CREATE DATABASE [AureliaBooks];
GO

USE [AureliaBooks];
GO

-- =========================================================================
-- KỊCH BẢN KHỞI TẠO CƠ SỞ DỮ LIỆU DỰ ÁN "AURELIA BOOKS" (SQL Server)
-- Phù hợp với môn PRJ301 và cấu trúc skeleton trong Java Web project
-- =========================================================================

-- XÓA BẢNG NẾU ĐÃ TỒN TẠI (Theo thứ tự ràng buộc từ khóa ngoại cấp thấp đến cao)
IF OBJECT_ID('dbo.SupportRequests', 'U') IS NOT NULL DROP TABLE dbo.SupportRequests;
IF OBJECT_ID('dbo.OrderItems', 'U') IS NOT NULL DROP TABLE dbo.OrderItems;
IF OBJECT_ID('dbo.Orders', 'U') IS NOT NULL DROP TABLE dbo.Orders;
IF OBJECT_ID('dbo.CartItems', 'U') IS NOT NULL DROP TABLE dbo.CartItems;
IF OBJECT_ID('dbo.Carts', 'U') IS NOT NULL DROP TABLE dbo.Carts;
IF OBJECT_ID('dbo.StockTransactions', 'U') IS NOT NULL DROP TABLE dbo.StockTransactions;
IF OBJECT_ID('dbo.Inventory', 'U') IS NOT NULL DROP TABLE dbo.Inventory;
IF OBJECT_ID('dbo.UserProfiles', 'U') IS NOT NULL DROP TABLE dbo.UserProfiles;
IF OBJECT_ID('dbo.Users', 'U') IS NOT NULL DROP TABLE dbo.Users;
IF OBJECT_ID('dbo.Roles', 'U') IS NOT NULL DROP TABLE dbo.Roles;
IF OBJECT_ID('dbo.Contributor', 'U') IS NOT NULL DROP TABLE dbo.Contributor;
IF OBJECT_ID('dbo.Authors', 'U') IS NOT NULL DROP TABLE dbo.Authors;
IF OBJECT_ID('dbo.Books', 'U') IS NOT NULL DROP TABLE dbo.Books;
IF OBJECT_ID('dbo.Stationeries', 'U') IS NOT NULL DROP TABLE dbo.Stationeries;
IF OBJECT_ID('dbo.Products', 'U') IS NOT NULL DROP TABLE dbo.Products;
IF OBJECT_ID('dbo.Brands', 'U') IS NOT NULL DROP TABLE dbo.Brands;
IF OBJECT_ID('dbo.Publishers', 'U') IS NOT NULL DROP TABLE dbo.Publishers;
IF OBJECT_ID('dbo.Suppliers', 'U') IS NOT NULL DROP TABLE dbo.Suppliers;
IF OBJECT_ID('dbo.Categories', 'U') IS NOT NULL DROP TABLE dbo.Categories;
IF OBJECT_ID('dbo.Discounts', 'U') IS NOT NULL DROP TABLE dbo.Discounts;
GO

-- =========================================================================
-- GIAI ĐOẠN 1: TẠO CÁC BẢNG ĐỘC LẬP (LEVEL 0)
-- =========================================================================

-- 1. Bảng Vai trò (Roles)
CREATE TABLE Roles (
    Id INT PRIMARY KEY IDENTITY(1,1),
    Name NVARCHAR(50) NOT NULL UNIQUE,
    Description NVARCHAR(255) NULL
);

-- 2. Bảng Danh mục Sản phẩm (Categories)
CREATE TABLE Categories (
    Id INT PRIMARY KEY IDENTITY(1,1),
    ParentId INT NULL FOREIGN KEY REFERENCES Categories(Id),
    Name NVARCHAR(100) NOT NULL
);

-- 3. Bảng Nhà cung cấp (Suppliers)
CREATE TABLE Suppliers (
    Id INT PRIMARY KEY IDENTITY(1,1),
    Name NVARCHAR(255) NOT NULL,
    ContactEmail NVARCHAR(100) NULL,
    ContactPhone NVARCHAR(20) NULL,
    Address NVARCHAR(MAX) NULL
);

-- 4. Bảng Nhà xuất bản (Publishers)
CREATE TABLE Publishers (
    Id INT PRIMARY KEY IDENTITY(1,1),
    Name NVARCHAR(255) NOT NULL,
    Address NVARCHAR(500) NULL,
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- 5. Bảng Thương hiệu Văn phòng phẩm (Brands)
CREATE TABLE Brands (
    Id INT PRIMARY KEY IDENTITY(1,1),
    Name NVARCHAR(255) NOT NULL,
    OriginCountry NVARCHAR(100) NULL,
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- 6. Bảng Tác giả (Authors)
CREATE TABLE Authors (
    AuthorId INT PRIMARY KEY IDENTITY(1,1),
    FullName NVARCHAR(255) NOT NULL,
    Biography NVARCHAR(MAX) NULL,
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- 7. Bảng Mã giảm giá (Discounts)
CREATE TABLE Discounts (
    Id INT PRIMARY KEY IDENTITY(1,1),
    Code NVARCHAR(50) NOT NULL UNIQUE,
    DiscountPercent DECIMAL(5,2) NOT NULL CHECK (DiscountPercent >= 0 AND DiscountPercent <= 100),
    MaxDiscountAmount DECIMAL(18,2) NOT NULL CHECK (MaxDiscountAmount >= 0),
    MinOrderValue DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (MinOrderValue >= 0),
    StartDate DATETIME NOT NULL,
    EndDate DATETIME NOT NULL,
    IsActive BIT DEFAULT 1,
    CONSTRAINT CK_Dates CHECK (EndDate > StartDate)
);

-- =========================================================================
-- GIAI ĐOẠN 2: TẠO CÁC BẢNG SẢN PHẨM (TPT INHERITANCE)
-- =========================================================================

-- 8. Bảng Sản phẩm chung (Products) - BẢNG CHA CỐT LÕI
CREATE TABLE Products (
    Id INT PRIMARY KEY IDENTITY(1,1),
    CategoryId INT NOT NULL FOREIGN KEY REFERENCES Categories(Id),
    SupplierId INT NULL FOREIGN KEY REFERENCES Suppliers(Id),
    Title NVARCHAR(255) NOT NULL,
    Description NVARCHAR(MAX) NULL,
    Price DECIMAL(18,2) NOT NULL CHECK (Price >= 0),
    Sku NVARCHAR(50) UNIQUE NULL,
    Image_URL NVARCHAR(500) NULL,
    IsActive BIT DEFAULT 1,
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- 9. Bảng Sách (Books) - BẢNG CON (Quan hệ 1-1 với Products)
CREATE TABLE Books (
    ProductId INT PRIMARY KEY FOREIGN KEY REFERENCES Products(Id) ON DELETE CASCADE,
    PublisherId INT NULL FOREIGN KEY REFERENCES Publishers(Id),
    Translator NVARCHAR(255) NULL,
    PublicationYear INT NULL,
    NumberOfPages INT NULL,
    CoverType NVARCHAR(50) NULL,
    Language NVARCHAR(100) DEFAULT N'Tiếng Việt',
    Weight DECIMAL(10,2) NULL,
    Dimensions NVARCHAR(100) NULL
);

-- 10. Bảng Văn phòng phẩm (Stationeries) - BẢNG CON (Quan hệ 1-1 với Products)
CREATE TABLE Stationeries (
    ProductId INT PRIMARY KEY FOREIGN KEY REFERENCES Products(Id) ON DELETE CASCADE,
    BrandId INT NULL FOREIGN KEY REFERENCES Brands(Id),
    Origin NVARCHAR(100) NULL,
    Material NVARCHAR(255) NULL,
    Color NVARCHAR(100) NULL,
    Weight DECIMAL(10,2) NULL,
    Dimensions NVARCHAR(100) NULL,
    Specifications NVARCHAR(MAX) NULL,
    Warning NVARCHAR(500) NULL
);

-- 11. Bảng Trung gian Tác giả - Sách (Contributor - Quan hệ n-n)
CREATE TABLE Contributor (
    ProductId INT FOREIGN KEY REFERENCES Products(Id) ON DELETE CASCADE,
    AuthorId INT FOREIGN KEY REFERENCES Authors(AuthorId) ON DELETE CASCADE,
    PRIMARY KEY (ProductId, AuthorId)
);

-- =========================================================================
-- GIAI ĐOẠN 3: TẠO CÁC BẢNG NGƯỜI DÙNG (USERS & SECURITY)
-- =========================================================================

-- 12. Bảng Tài khoản (Users)
CREATE TABLE Users (
    Id INT PRIMARY KEY IDENTITY(1,1),
    RoleId INT NOT NULL FOREIGN KEY REFERENCES Roles(Id),
    Username NVARCHAR(100) NOT NULL UNIQUE,
    PasswordHash NVARCHAR(255) NOT NULL,
    Email NVARCHAR(100) NOT NULL UNIQUE,
    AuthProvider NVARCHAR(50) DEFAULT 'local', -- 'local' hoặc 'google'
    CreatedAt DATETIME DEFAULT GETDATE(),
    IsActive BIT DEFAULT 1
);

-- 13. Bảng Hồ sơ chi tiết (UserProfiles - Quan hệ 1-1 với Users)
CREATE TABLE UserProfiles (
    Id INT PRIMARY KEY IDENTITY(1,1),
    UserId INT NOT NULL UNIQUE FOREIGN KEY REFERENCES Users(Id) ON DELETE CASCADE,
    FullName NVARCHAR(100) NOT NULL,
    Phone NVARCHAR(20) NULL,
    Address NVARCHAR(MAX) NULL,
    AvatarUrl NVARCHAR(255) NULL
);

-- =========================================================================
-- GIAI ĐOẠN 4: QUẢN LÝ KHO HÀNG (INVENTORY)
-- =========================================================================

-- 14. Bảng Tồn kho (Inventory)
CREATE TABLE Inventory (
    ProductId INT PRIMARY KEY FOREIGN KEY REFERENCES Products(Id) ON DELETE CASCADE,
    QuantityInStock INT NOT NULL DEFAULT 0 CHECK (QuantityInStock >= 0),
    WarehouseLocation NVARCHAR(100) NULL,
    LastUpdated DATETIME DEFAULT GETDATE()
);

-- 15. Bảng Giao dịch Nhập/Xuất kho (StockTransactions)
CREATE TABLE StockTransactions (
    Id INT PRIMARY KEY IDENTITY(1,1),
    ProductId INT NOT NULL FOREIGN KEY REFERENCES Products(Id),
    HandledByUserId INT NOT NULL FOREIGN KEY REFERENCES Users(Id),
    SupplierId INT NULL FOREIGN KEY REFERENCES Suppliers(Id),
    TransactionType NVARCHAR(20) NOT NULL CHECK (TransactionType IN ('IMPORT', 'EXPORT', 'RETURN_IN')),
    Quantity INT NOT NULL CHECK (Quantity > 0),
    TransactionDate DATETIME DEFAULT GETDATE()
);

-- =========================================================================
-- GIAI ĐOẠN 5: GIỎ HÀNG & ĐƠN HÀNG (CARTS, ORDERS & TRANSACTIONS)
-- =========================================================================

-- 16. Bảng Giỏ hàng (Carts)
CREATE TABLE Carts (
    Id INT PRIMARY KEY IDENTITY(1,1),
    UserId INT NULL FOREIGN KEY REFERENCES Users(Id) ON DELETE CASCADE,
    SessionId NVARCHAR(255) NULL, -- Phục vụ cho khách vãng lai (Guest) mua hàng không cần đăng nhập
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- 17. Bảng Chi tiết Giỏ hàng (CartItems)
CREATE TABLE CartItems (
    Id INT PRIMARY KEY IDENTITY(1,1),
    CartId INT NOT NULL FOREIGN KEY REFERENCES Carts(Id) ON DELETE CASCADE,
    ProductId INT NOT NULL FOREIGN KEY REFERENCES Products(Id) ON DELETE CASCADE,
    Quantity INT NOT NULL CHECK (Quantity > 0),
    AddedAt DATETIME DEFAULT GETDATE()
);

-- 18. Bảng Đơn hàng (Orders)
CREATE TABLE Orders (
    Id INT PRIMARY KEY IDENTITY(1,1),
    UserId INT NOT NULL FOREIGN KEY REFERENCES Users(Id),
    DiscountId INT NULL FOREIGN KEY REFERENCES Discounts(Id),
    TotalAmount DECIMAL(18,2) NOT NULL CHECK (TotalAmount >= 0),
    Status NVARCHAR(50) NOT NULL DEFAULT 'PENDING' 
        CHECK (Status IN ('PENDING', 'CONFIRMED', 'SHIPPING', 'COMPLETED', 'CANCELLED', 'RETURNED')),
    ShippingAddress NVARCHAR(MAX) NOT NULL,
    ContactPhone NVARCHAR(20) NOT NULL,
    ProcessedByUserId INT NULL FOREIGN KEY REFERENCES Users(Id), -- Lưu vết nhân viên xử lý
    ReturnReason NVARCHAR(MAX) NULL, -- Lý do trả hàng nếu trạng thái là RETURNED
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- 19. Bảng Chi tiết Đơn hàng (OrderItems)
CREATE TABLE OrderItems (
    Id INT PRIMARY KEY IDENTITY(1,1),
    OrderId INT NOT NULL FOREIGN KEY REFERENCES Orders(Id) ON DELETE CASCADE,
    ProductId INT NOT NULL FOREIGN KEY REFERENCES Products(Id),
    Quantity INT NOT NULL CHECK (Quantity > 0),
    UnitPrice DECIMAL(18,2) NOT NULL CHECK (UnitPrice >= 0),
    SubTotal DECIMAL(18,2) NOT NULL CHECK (SubTotal >= 0)
);

-- 20. Bảng Yêu cầu hỗ trợ (SupportRequests)
CREATE TABLE SupportRequests (
    Id INT PRIMARY KEY IDENTITY(1,1),
    UserId INT NOT NULL FOREIGN KEY REFERENCES Users(Id) ON DELETE CASCADE,
    HandledByUserId INT NULL FOREIGN KEY REFERENCES Users(Id),
    Subject NVARCHAR(255) NOT NULL,
    Message NVARCHAR(MAX) NOT NULL,
    ReplyMessage NVARCHAR(MAX) NULL,
    Status NVARCHAR(50) NOT NULL DEFAULT 'OPEN' CHECK (Status IN ('OPEN', 'PROCESSING', 'RESOLVED')),
    CreatedAt DATETIME DEFAULT GETDATE()
);
GO

-- =========================================================================
-- GIAI ĐOẠN 6: TẠO CÁC CHỈ MỤC (INDEX) HỖ TRỢ TRUY VẤN & BÁO CÁO NHANH
-- =========================================================================

-- Hỗ trợ xem danh sách sản phẩm theo Category (Trang chủ/Tìm kiếm)
CREATE INDEX IX_Products_CategoryId ON Products(CategoryId);

-- Hỗ trợ tìm kiếm nhanh theo tiêu đề sách
CREATE INDEX IX_Products_Title ON Products(Title);

-- Hỗ trợ truy vấn thống kê doanh thu theo ngày & trạng thái
CREATE INDEX IX_Orders_CreatedAt_Status ON Orders(CreatedAt, Status);
GO

-- =========================================================================
-- GIAI ĐOẠN 7: DỮ LIỆU MẪU CHUẨN (MOCK DATA FOR TESTING)
-- =========================================================================

-- 1. Chèn dữ liệu Roles
INSERT INTO Roles (Name, Description) VALUES 
('ADMIN', N'Quản trị viên toàn bộ hệ thống'),
('EMPLOYEE', N'Nhân viên bán hàng và hỗ trợ khách hàng'),
('CUSTOMER', N'Khách hàng mua hàng trực tuyến');
GO

-- 2. Chèn dữ liệu Categories
INSERT INTO Categories (ParentId, Name) VALUES 
(NULL, N'Sách Tiếng Việt'),       -- ID: 1
(NULL, N'Sách Ngoại Văn'),        -- ID: 2
(NULL, N'Dụng Cụ Học Tập');       -- ID: 3

INSERT INTO Categories (ParentId, Name) VALUES 
(1, N'Văn Học Trong Nước'),       -- ID: 4
(1, N'Sách Kỹ Năng - Sống Đẹp'),   -- ID: 5
(2, N'Fiction & Literature'),     -- ID: 6
(3, N'Bút - Viết');               -- ID: 7
GO

-- 3. Chèn dữ liệu Suppliers
INSERT INTO Suppliers (Name, ContactEmail, ContactPhone, Address) VALUES 
(N'Công Ty Sách Fahasa', 'contact@fahasa.com', '02838225446', N'Nguyễn Huệ, Quận 1, TP. HCM'),
(N'Nhà Sách Phương Nam', 'info@phuongnam.com', '19006656', N'Quận Hải Châu, Đà Nẵng'),
(N'Thiên Long Group', 'sales@thienlong.com', '02837505555', N'Bình Tân, TP. HCM');
GO

-- 4. Chèn dữ liệu Publishers
INSERT INTO Publishers (Name, Address) VALUES 
(N'NXB Trẻ', N'Quận 3, TP. Hồ Chí Minh'),
(N'NXB Kim Đồng', N'Quận Hai Bà Trưng, Hà Nội'),
(N'HarperCollins', N'New York, USA');
GO

-- 5. Chèn dữ liệu Brands
INSERT INTO Brands (Name, OriginCountry) VALUES 
(N'Thiên Long', N'Việt Nam'),
(N'Pentel', N'Nhật Bản'),
(N'Faber-Castell', N'Đức');
GO

-- 6. Chèn dữ liệu Authors
INSERT INTO Authors (FullName, Biography) VALUES 
(N'Nguyễn Nhật Ánh', N'Nhà văn nổi tiếng của Việt Nam với các tác phẩm dành cho tuổi trẻ.'),
(N'Dale Carnegie', N'Tác giả người Mỹ nổi tiếng với cuốn sách Đắc Nhân Tâm.'),
(N'Haruki Murakami', N'Nhà văn Nhật Bản nổi tiếng thế giới.');
GO

-- 7. Chèn dữ liệu Discounts
INSERT INTO Discounts (Code, DiscountPercent, MaxDiscountAmount, MinOrderValue, StartDate, EndDate, IsActive) VALUES 
('HE2026', 10.00, 50000.00, 200000.00, '2026-06-01', '2026-08-31', 1),
('WELCOME', 20.00, 30000.00, 0.00, '2026-01-01', '2026-12-31', 1);
GO

-- 8. Chèn dữ liệu Products (Base)
-- Tổng 5 sản phẩm (3 sách, 2 dụng cụ học tập)
SET IDENTITY_INSERT Products ON;
INSERT INTO Products (Id, CategoryId, SupplierId, Title, Description, Price, Sku, Image_URL, IsActive) VALUES 
(1, 4, 1, N'Mắt Biếc', N'Tác phẩm truyện dài tiêu biểu của nhà văn Nguyễn Nhật Ánh.', 110000.00, 'B-MB-001', 'images/mat-biec.jpg', 1),
(2, 5, 1, N'Đắc Nhân Tâm', N'Cuốn sách kỹ năng sống bán chạy nhất mọi thời đại.', 86000.00, 'B-DNT-002', 'images/dac-nhan-tam.jpg', 1),
(3, 6, 2, N'Norwegian Wood', N'Tác phẩm Rừng Na Uy bằng tiếng Anh của Haruki Murakami.', 250000.00, 'B-NW-003', 'images/norwegian-wood.jpg', 1),
(4, 7, 3, N'Bút Bi Thiên Long FO-024', N'Bút bi viết êm, mực đều, thích hợp cho học sinh.', 4500.00, 'S-TL-004', 'images/but-tl.jpg', 1),
(5, 7, 3, N'Bút Chì Kim Pentel AX105', N'Bút chì kim ngòi 0.5mm nhập khẩu Nhật Bản.', 18000.00, 'S-PT-005', 'images/but-chi-pentel.jpg', 1);
SET IDENTITY_INSERT Products OFF;
GO

-- 9. Chèn dữ liệu Books (Con)
INSERT INTO Books (ProductId, PublisherId, Translator, PublicationYear, NumberOfPages, CoverType, Language, Weight, Dimensions) VALUES 
(1, 1, NULL, 2019, 290, N'Bìa mềm', N'Tiếng Việt', 250.00, '13 x 20 cm'),
(2, 1, N'Nguyễn Văn A', 2021, 320, N'Bìa mềm', N'Tiếng Việt', 300.00, '14 x 20.5 cm'),
(3, 3, N'Jay Rubin', 2015, 380, N'Bìa cứng', N'Tiếng Anh', 400.00, '15 x 23 cm');
GO

-- 10. Chèn dữ liệu Stationeries (Con)
INSERT INTO Stationeries (ProductId, BrandId, Origin, Material, Color, Weight, Dimensions, Specifications, Warning) VALUES 
(4, 1, N'Việt Nam', N'Nhựa', N'Xanh', 10.00, '14 cm', N'Hộp 20 cây', N'Tránh xa tầm tay trẻ em dưới 3 tuổi'),
(5, 2, N'Nhật Bản', N'Nhựa và kim loại', N'Đen', 15.00, '14.5 cm', N'Ngòi chì 0.5mm', N'Không ấn ngòi quá mạnh');
GO

-- 11. Chèn dữ liệu Contributor
INSERT INTO Contributor (ProductId, AuthorId) VALUES 
(1, 1), -- Mắt Biếc của Nguyễn Nhật Ánh
(2, 2), -- Đắc Nhân Tâm của Dale Carnegie
(3, 3); -- Rừng Na Uy của Haruki Murakami
GO

-- 12. Chèn dữ liệu Users (Mật khẩu mẫu ở đây tương trưng, khi code sinh viên sẽ dùng BCrypt băm)
INSERT INTO Users (RoleId, Username, PasswordHash, Email, AuthProvider, IsActive) VALUES 
(1, 'admin', 'admin123_hashed', 'admin@minifahasa.com', 'local', 1),
(2, 'employee1', 'emp123_hashed', 'emp1@minifahasa.com', 'local', 1),
(3, 'customer1', 'cust123_hashed', 'customer1@gmail.com', 'local', 1),
(3, 'customer2', 'cust456_hashed', 'customer2@gmail.com', 'google', 1);
GO

-- 13. Chèn dữ liệu UserProfiles
INSERT INTO UserProfiles (UserId, FullName, Phone, Address, AvatarUrl) VALUES 
(1, N'Trần Văn Quản Trị', '0901234567', N'123 Nguyễn Văn Cừ, Quận 5, TP. HCM', 'avatars/admin.jpg'),
(2, N'Nguyễn Thị Nhân Viên', '0907654321', N'456 Lê Lợi, Quận 1, TP. HCM', 'avatars/emp1.jpg'),
(3, N'Lê Minh Khách Hàng 1', '0911112222', N'789 Cách Mạng Tháng Tám, Tân Bình, TP. HCM', 'avatars/cust1.jpg'),
(4, N'Phạm Hoàng Khách Hàng 2', '0933334444', N'101 Điện Biên Phủ, Bình Thạnh, TP. HCM', 'avatars/cust2.jpg');
GO

-- 14. Chèn dữ liệu Inventory (Số lượng tồn ban đầu)
INSERT INTO Inventory (ProductId, QuantityInStock, WarehouseLocation, LastUpdated) VALUES 
(1, 100, N'Kệ A-01', GETDATE()),
(2, 150, N'Kệ B-02', GETDATE()),
(3, 30, N'Kệ Ngoại Văn C-01', GETDATE()),
(4, 1000, N'Hộp Phụ Phẩm E-05', GETDATE()),
(5, 200, N'Kệ Dụng Cụ D-02', GETDATE());
GO

-- 15. Chèn dữ liệu Orders & OrderItems (Thao tác thống kê mẫu)
-- Đơn hàng 1: Hoàn thành (Dùng để chạy thử thống kê và nút Trả Hàng)
INSERT INTO Orders (UserId, DiscountId, TotalAmount, Status, ShippingAddress, ContactPhone, ProcessedByUserId, CreatedAt) VALUES 
(3, NULL, 196000.00, 'COMPLETED', N'789 Cách Mạng Tháng Tám, Tân Bình, TP. HCM', '0911112222', 2, '2026-06-10 14:30:00');

INSERT INTO OrderItems (OrderId, ProductId, Quantity, UnitPrice, SubTotal) VALUES 
(1, 1, 1, 110000.00, 110000.00), -- 1 cuốn Mắt Biếc
(1, 2, 1, 86000.00, 86000.00);    -- 1 cuốn Đắc Nhân Tâm

-- Đơn hàng 2: Đang chờ xử lý
INSERT INTO Orders (UserId, DiscountId, TotalAmount, Status, ShippingAddress, ContactPhone, ProcessedByUserId, CreatedAt) VALUES 
(4, 1, 225000.00, 'PENDING', N'101 Điện Biên Phủ, Bình Thạnh, TP. HCM', '0933334444', NULL, '2026-06-15 09:15:00');

INSERT INTO OrderItems (OrderId, ProductId, Quantity, UnitPrice, SubTotal) VALUES 
(2, 3, 1, 250000.00, 250000.00); -- 1 cuốn Rừng Na Uy (Có áp mã discount)
GO
