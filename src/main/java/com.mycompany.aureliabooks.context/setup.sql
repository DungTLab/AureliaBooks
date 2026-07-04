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
-- Đã chuẩn hóa đặt tên trong ngoặc vuông [] chống đụng độ Keyword
-- =========================================================================

-- XÓA BẢNG NẾU ĐÃ TỒN TẠI (Theo thứ tự ràng buộc từ khóa ngoại từ thấp đến cao)
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
CREATE TABLE [dbo].[Roles] (
    [Id] INT PRIMARY KEY IDENTITY(1,1),
    [Name] NVARCHAR(50) NOT NULL UNIQUE,
    [Description] NVARCHAR(255) NULL
);

-- 2. Bảng Danh mục Sản phẩm (Categories)
CREATE TABLE [dbo].[Categories] (
    [Id] INT PRIMARY KEY IDENTITY(1,1),
    [ParentId] INT NULL FOREIGN KEY REFERENCES [dbo].[Categories]([Id]),
    [Name] NVARCHAR(100) NOT NULL
);

-- 3. Bảng Nhà cung cấp (Suppliers)
CREATE TABLE [dbo].[Suppliers] (
    [Id] INT PRIMARY KEY IDENTITY(1,1),
    [Name] NVARCHAR(255) NOT NULL,
    [ContactEmail] NVARCHAR(100) NULL,
    [ContactPhone] NVARCHAR(20) NULL,
    [Address] NVARCHAR(MAX) NULL,
    [CreatedAt] DATETIME DEFAULT GETDATE()
);

-- 4. Bảng Nhà xuất bản (Publishers)
CREATE TABLE [dbo].[Publishers] (
    [Id] INT PRIMARY KEY IDENTITY(1,1),
    [Name] NVARCHAR(255) NOT NULL,
    [Address] NVARCHAR(500) NULL,
    [CreatedAt] DATETIME DEFAULT GETDATE()
);

-- 5. Bảng Thương hiệu Văn phòng phẩm (Brands)
CREATE TABLE [dbo].[Brands] (
    [Id] INT PRIMARY KEY IDENTITY(1,1),
    [Name] NVARCHAR(255) NOT NULL,
    [OriginCountry] NVARCHAR(100) NULL,
    [CreatedAt] DATETIME DEFAULT GETDATE()
);

-- 6. Bảng Tác giả (Authors)
CREATE TABLE [dbo].[Authors] (
    [AuthorId] INT PRIMARY KEY IDENTITY(1,1),
    [FullName] NVARCHAR(255) NOT NULL,
    [Biography] NVARCHAR(MAX) NULL,
    [CreatedAt] DATETIME DEFAULT GETDATE()
);

-- 7. Bảng Mã giảm giá (Discounts)
CREATE TABLE [dbo].[Discounts] (
    [Id] INT PRIMARY KEY IDENTITY(1,1),
    [Code] NVARCHAR(50) NOT NULL UNIQUE,
    [DiscountPercent] DECIMAL(5,2) NOT NULL CHECK ([DiscountPercent] >= 0 AND [DiscountPercent] <= 100),
    [MaxDiscountAmount] DECIMAL(18,2) NOT NULL CHECK ([MaxDiscountAmount] >= 0),
    [MinOrderValue] DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK ([MinOrderValue] >= 0),
    [StartDate] DATETIME NOT NULL,
    [EndDate] DATETIME NOT NULL,
    [IsActive] BIT DEFAULT 1,
    CONSTRAINT [CK_Dates] CHECK ([EndDate] > [StartDate])
);

-- =========================================================================
-- GIAI ĐOẠN 2: TẠO CÁC BẢNG SẢN PHẨM (TPT INHERITANCE)
-- =========================================================================

-- 8. Bảng Sản phẩm chung (Products) - BẢNG CHA CỐT LÕI
CREATE TABLE [dbo].[Products] (
    [Id] INT PRIMARY KEY IDENTITY(1,1),
    [CategoryId] INT NOT NULL FOREIGN KEY REFERENCES [dbo].[Categories]([Id]),
    [SupplierId] INT NULL FOREIGN KEY REFERENCES [dbo].[Suppliers]([Id]),
    [Title] NVARCHAR(255) NOT NULL,
    [Description] NVARCHAR(MAX) NULL,
    [Price] DECIMAL(18,2) NOT NULL CHECK ([Price] >= 0),
    [Sku] NVARCHAR(50) UNIQUE NULL,
    [Image_URL] NVARCHAR(500) NULL,
    [IsActive] BIT DEFAULT 1,
    [CreatedAt] DATETIME DEFAULT GETDATE()
);

-- 9. Bảng Sách (Books) - BẢNG CON (Quan hệ 1-1 với Products)
CREATE TABLE [dbo].[Books] (
    [ProductId] INT PRIMARY KEY FOREIGN KEY REFERENCES [dbo].[Products]([Id]) ON DELETE CASCADE,
    [PublisherId] INT NULL FOREIGN KEY REFERENCES [dbo].[Publishers]([Id]),
    [Translator] NVARCHAR(255) NULL,
    [PublicationYear] INT NULL,
    [NumberOfPages] INT NULL,
    [CoverType] NVARCHAR(50) NULL,
    [Language] NVARCHAR(100) DEFAULT N'Tiếng Việt',
    [Weight] DECIMAL(10,2) NULL,
    [Dimensions] NVARCHAR(100) NULL
);

-- 10. Bảng Văn phòng phẩm (Stationeries) - BẢNG CON (Quan hệ 1-1 với Products)
CREATE TABLE [dbo].[Stationeries] (
    [ProductId] INT PRIMARY KEY FOREIGN KEY REFERENCES [dbo].[Products]([Id]) ON DELETE CASCADE,
    [BrandId] INT NULL FOREIGN KEY REFERENCES [dbo].[Brands]([Id]),
    [Origin] NVARCHAR(100) NULL,
    [Material] NVARCHAR(255) NULL,
    [Color] NVARCHAR(100) NULL,
    [Weight] DECIMAL(10,2) NULL,
    [Dimensions] NVARCHAR(100) NULL,
    [Specifications] NVARCHAR(MAX) NULL,
    [Warning] NVARCHAR(500) NULL
);

-- 11. Bảng Trung gian Tác giả - Sách (Contributor - Quan hệ n-n)
CREATE TABLE [dbo].[Contributor] (
    [ProductId] INT FOREIGN KEY REFERENCES [dbo].[Products]([Id]) ON DELETE CASCADE,
    [AuthorId] INT FOREIGN KEY REFERENCES [dbo].[Authors]([AuthorId]) ON DELETE CASCADE,
    PRIMARY KEY ([ProductId], [AuthorId])
);

-- =========================================================================
-- GIAI ĐOẠN 3: TẠO CÁC BẢNG NGƯỜI DÙNG (USERS & SECURITY)
-- =========================================================================

-- 12. Bảng Tài khoản (Users)
CREATE TABLE [dbo].[Users] (
    [Id] INT PRIMARY KEY IDENTITY(1,1),
    [RoleId] INT NOT NULL FOREIGN KEY REFERENCES [dbo].[Roles]([Id]),
    [Username] NVARCHAR(100) NOT NULL UNIQUE,
    [PasswordHash] NVARCHAR(255) NOT NULL,
    [Email] NVARCHAR(100) NOT NULL UNIQUE,
    [AuthProvider] NVARCHAR(50) DEFAULT 'local',
    [CreatedAt] DATETIME DEFAULT GETDATE(),
    [IsActive] BIT DEFAULT 1
);

-- 13. Bảng Hồ sơ chi tiết (UserProfiles - Quan hệ 1-1 với Users)
CREATE TABLE [dbo].[UserProfiles] (
    [Id] INT PRIMARY KEY IDENTITY(1,1),
    [UserId] INT NOT NULL UNIQUE FOREIGN KEY REFERENCES [dbo].[Users]([Id]) ON DELETE CASCADE,
    [FullName] NVARCHAR(100) NOT NULL,
    [Phone] NVARCHAR(20) NULL,
    [Address] NVARCHAR(MAX) NULL,
    [AvatarUrl] NVARCHAR(255) NULL
);

-- =========================================================================
-- GIAI ĐOẠN 4: QUẢN LÝ KHO HÀNG (INVENTORY)
-- =========================================================================

-- 14. Bảng Tồn kho (Inventory)
CREATE TABLE [dbo].[Inventory] (
    [ProductId] INT PRIMARY KEY FOREIGN KEY REFERENCES [dbo].[Products]([Id]) ON DELETE CASCADE,
    [QuantityInStock] INT NOT NULL DEFAULT 0 CHECK ([QuantityInStock] >= 0),
    [WarehouseLocation] NVARCHAR(100) NULL,
    [LastUpdated] DATETIME DEFAULT GETDATE()
);

-- 15. Bảng Giao dịch Nhập/Xuất kho (StockTransactions)
CREATE TABLE [dbo].[StockTransactions] (
    [Id] INT PRIMARY KEY IDENTITY(1,1),
    [ProductId] INT NOT NULL FOREIGN KEY REFERENCES [dbo].[Products]([Id]),
    [HandledByUserId] INT NOT NULL FOREIGN KEY REFERENCES [dbo].[Users]([Id]),
    [SupplierId] INT NULL FOREIGN KEY REFERENCES [dbo].[Suppliers]([Id]),
    [TransactionType] NVARCHAR(20) NOT NULL CHECK ([TransactionType] IN ('IMPORT', 'EXPORT', 'RETURN_IN')),
    [Quantity] INT NOT NULL CHECK ([Quantity] > 0),
    [TransactionDate] DATETIME DEFAULT GETDATE()
);

-- =========================================================================
-- GIAI ĐOẠN 5: GIỎ HÀNG & ĐƠN HÀNG (CARTS, ORDERS & TRANSACTIONS)
-- =========================================================================

-- 16. Bảng Giỏ hàng (Carts)
CREATE TABLE [dbo].[Carts] (
    [Id] INT PRIMARY KEY IDENTITY(1,1),
    [UserId] INT NULL FOREIGN KEY REFERENCES [dbo].[Users]([Id]) ON DELETE CASCADE,
    [SessionId] NVARCHAR(255) NULL,
    [CreatedAt] DATETIME DEFAULT GETDATE()
);

-- 17. Bảng Chi tiết Giỏ hàng (CartItems)
CREATE TABLE [dbo].[CartItems] (
    [Id] INT PRIMARY KEY IDENTITY(1,1),
    [CartId] INT NOT NULL FOREIGN KEY REFERENCES [dbo].[Carts]([Id]) ON DELETE CASCADE,
    [ProductId] INT NOT NULL FOREIGN KEY REFERENCES [dbo].[Products]([Id]) ON DELETE CASCADE,
    [Quantity] INT NOT NULL CHECK ([Quantity] > 0),
    [AddedAt] DATETIME DEFAULT GETDATE()
);

-- 18. Bảng Đơn hàng (Orders)
CREATE TABLE [dbo].[Orders] (
    [Id] INT PRIMARY KEY IDENTITY(1,1),
    [UserId] INT NOT NULL FOREIGN KEY REFERENCES [dbo].[Users]([Id]),
    [DiscountId] INT NULL FOREIGN KEY REFERENCES [dbo].[Discounts]([Id]),
    [TotalAmount] DECIMAL(18,2) NOT NULL CHECK ([TotalAmount] >= 0),
    [Status] NVARCHAR(50) NOT NULL DEFAULT 'PENDING' 
        CHECK ([Status] IN ('PENDING', 'CONFIRMED', 'SHIPPING', 'COMPLETED', 'CANCELLED', 'RETURNED')),
    [ShippingAddress] NVARCHAR(MAX) NOT NULL,
    [ContactPhone] NVARCHAR(20) NOT NULL,
    [ProcessedByUserId] INT NULL FOREIGN KEY REFERENCES [dbo].[Users]([Id]),
    [ReturnReason] NVARCHAR(MAX) NULL,
    [CreatedAt] DATETIME DEFAULT GETDATE()
);

-- 19. Bảng Chi tiết Đơn hàng (OrderItems)
CREATE TABLE [dbo].[OrderItems] (
    [Id] INT PRIMARY KEY IDENTITY(1,1),
    [OrderId] INT NOT NULL FOREIGN KEY REFERENCES [dbo].[Orders]([Id]) ON DELETE CASCADE,
    [ProductId] INT NOT NULL FOREIGN KEY REFERENCES [dbo].[Products]([Id]),
    [Quantity] INT NOT NULL CHECK ([Quantity] > 0),
    [UnitPrice] DECIMAL(18,2) NOT NULL CHECK ([UnitPrice] >= 0),
    [SubTotal] DECIMAL(18,2) NOT NULL CHECK ([SubTotal] >= 0)
);

-- 20. Bảng Yêu cầu hỗ trợ (SupportRequests)
CREATE TABLE [dbo].[SupportRequests] (
    [Id] INT PRIMARY KEY IDENTITY(1,1),
    [UserId] INT NOT NULL FOREIGN KEY REFERENCES [dbo].[Users]([Id]) ON DELETE CASCADE,
    [HandledByUserId] INT NULL FOREIGN KEY REFERENCES [dbo].[Users]([Id]),
    [Subject] NVARCHAR(255) NOT NULL,
    [Message] NVARCHAR(MAX) NOT NULL,
    [ReplyMessage] NVARCHAR(MAX) NULL,
    [Status] NVARCHAR(50) NOT NULL DEFAULT 'OPEN' CHECK ([Status] IN ('OPEN', 'PROCESSING', 'RESOLVED')),
    [CreatedAt] DATETIME DEFAULT GETDATE()
);
GO

-- =========================================================================
-- GIAI ĐOẠN 6: TẠO CÁC CHỈ MỤC (INDEX) HỖ TRỢ TRUY VẤN & BÁO CÁO NHANH
-- =========================================================================

-- Hỗ trợ xem danh sách sản phẩm theo Category (Trang chủ/Tìm kiếm)
CREATE INDEX IX_Products_CategoryId ON [dbo].[Products]([CategoryId]);

-- Hỗ trợ tìm kiếm nhanh theo tiêu đề sách
CREATE INDEX IX_Products_Title ON [dbo].[Products]([Title]);

-- Hỗ trợ truy vấn thống kê doanh thu theo ngày & trạng thái
CREATE INDEX IX_Orders_CreatedAt_Status ON [dbo].[Orders]([CreatedAt], [Status]);
GO

-- =========================================================================
-- GIAI ĐOẠN 7: DỮ LIỆU MẪU CHUẨN (MOCK DATA FOR TESTING)
-- =========================================================================

-- 1. Chèn dữ liệu Roles
INSERT INTO [dbo].[Roles] ([Name], [Description]) VALUES 
('ADMIN', N'Quản trị viên toàn bộ hệ thống'),
('EMPLOYEE', N'Nhân viên bán hàng và hỗ trợ khách hàng'),
('CUSTOMER', N'Khách hàng mua hàng trực tuyến');
GO

-- 2. Chèn dữ liệu Categories
INSERT INTO [dbo].[Categories] ([ParentId], [Name]) VALUES 
(NULL, N'Sách Tiếng Việt'),       -- ID: 1
(NULL, N'Sách Ngoại Văn'),        -- ID: 2
(NULL, N'Dụng Cụ Học Tập');       -- ID: 3

INSERT INTO [dbo].[Categories] ([ParentId], [Name]) VALUES 
(1, N'Văn Học Trong Nước'),       -- ID: 4
(1, N'Sách Kỹ Năng - Sống Đẹp'),   -- ID: 5
(2, N'Fiction & Literature'),     -- ID: 6
(3, N'Bút - Viết');               -- ID: 7
GO


-- 3. Chèn dữ liệu Suppliers
INSERT INTO [dbo].[Suppliers] ([Name], [ContactEmail], [ContactPhone], [Address]) VALUES 
(N'Công Ty Sách Fahasa', 'contact@fahasa.com', '02838225446', N'Nguyễn Huệ, Quận 1, TP. HCM'),
(N'Nhà Sách Phương Nam', 'info@phuongnam.com', '19006656', N'Quận Hải Châu, Đà Nẵng'),
(N'Thiên Long Group', 'sales@thienlong.com', '02837505555', N'Bình Tân, TP. HCM');
GO

-- 4. Chèn dữ liệu Publishers
INSERT INTO [dbo].[Publishers] ([Name], [Address]) VALUES 
(N'NXB Trẻ', N'Quận 3, TP. Hồ Chí Minh'),
(N'NXB Kim Đồng', N'Quận Hai Bà Trưng, Hà Nội'),
(N'HarperCollins', N'New York, USA');
GO

-- 5. Chèn dữ liệu Brands
INSERT INTO [dbo].[Brands] ([Name], [OriginCountry]) VALUES 
(N'Thiên Long', N'Việt Nam'),
(N'Pentel', N'Nhật Bản'),
(N'Faber-Castell', N'Đức');
GO

-- 6. Chèn dữ liệu Authors
INSERT INTO [dbo].[Authors] ([FullName], [Biography]) VALUES 
(N'Nguyễn Nhật Ánh', N'Nhà văn nổi tiếng của Việt Nam với các tác phẩm dành cho tuổi trẻ.'),
(N'Dale Carnegie', N'Tác giả người Mỹ nổi tiếng với cuốn sách Đắc Nhân Tâm.'),
(N'Haruki Murakami', N'Nhà văn Nhật Bản nổi tiếng thế giới.'),
(N'Vũ Trọng Phụng', N'Nhà văn, nhà báo nổi tiếng của Việt Nam vào đầu thế kỷ 20, ông nổi tiếng với lối văn tả thực châm biếm sâu cay.'),
(N'Rosie Nguyễn', N'Tác giả tự do, người viết sách chuyên về phong cách sống và phát triển bản thân của giới trẻ Việt Nam.'),
(N'Paulo Coelho', N'Tiểu thuyết gia người Brazil nổi tiếng trên toàn thế giới, tác giả của tiểu thuyết kinh điển Nhà Giả Kim.'),
(N'J.K. Rowling', N'Nữ nhà văn nổi tiếng người Anh, tác giả của bộ sách huyền thoại Harry Potter.'),
(N'Harper Lee', N'Nữ nhà văn người Mỹ nổi tiếng thế giới qua tác phẩm kinh điển Giết con chim nhại.'),
(N'Robin Sharma', N'Một trong những chuyên gia hàng đầu thế giới về kỹ năng lãnh đạo và phát triển bản thân, tác giả cuốn sách Đời ngắn đừng ngủ dài.');
GO

-- 7. Chèn dữ liệu Discounts
INSERT INTO [dbo].[Discounts] ([Code], [DiscountPercent], [MaxDiscountAmount], [MinOrderValue], [StartDate], [EndDate], [IsActive]) VALUES 
('HE2026', 10.00, 50000.00, 200000.00, '2026-06-01', '2026-08-31', 1),
('WELCOME', 20.00, 30000.00, 0.00, '2026-01-01', '2026-12-31', 1);
GO

-- 8. Chèn dữ liệu Products (Base)
SET IDENTITY_INSERT [dbo].[Products] ON;
INSERT INTO [dbo].[Products] ([Id], [CategoryId], [SupplierId], [Title], [Description], [Price], [Sku], [Image_URL], [IsActive]) VALUES 
(1, 4, 1, N'Mắt Biếc', N'Tác phẩm truyện dài tiêu biểu của nhà văn Nguyễn Nhật Ánh.', 110000.00, 'B-MB-001', 'images/mat-biec.jpg', 1),
(2, 5, 1, N'Đắc Nhân Tâm', N'Cuốn sách kỹ năng sống bán chạy nhất mọi thời đại.', 86000.00, 'B-DNT-002', 'images/dac-nhan-tam.jpg', 1),
(3, 6, 2, N'Norwegian Wood', N'Tác phẩm Rừng Na Uy bằng tiếng Anh của Haruki Murakami.', 250000.00, 'B-NW-003', 'images/norwegian-wood.jpg', 1),
(4, 7, 3, N'Bút Bi Thiên Long FO-024', N'Bút bi viết êm, mực đều, thích hợp cho học sinh.', 4500.00, 'S-TL-004', 'images/but-tl.jpg', 1),
(5, 7, 3, N'Bút Chì Kim Pentel AX105', N'Bút chì kim ngòi 0.5mm nhập khẩu Nhật Bản.', 18000.00, 'S-PT-005', 'images/but-chi-pentel.jpg', 1),
(6, 4, 1, N'Số Đỏ', N'Tác phẩm văn học hiện thực xuất sắc châm biếm xã hội của nhà văn Vũ Trọng Phụng.', 65000.00, 'B-SD-006', 'images/so-do.jpg', 1),
(7, 5, 1, N'Tuổi Trẻ Đáng Giá Bao Nhiêu', N'Cuốn sách truyền cảm hứng và định hướng phong cách sống cho giới trẻ Việt Nam của tác giả Rosie Nguyễn.', 75000.00, 'B-TTD-007', 'images/tuoi-tre.jpg', 1),
(8, 5, 2, N'Nhà Giả Kim', N'Tiểu thuyết huyền thoại về hành trình đi tìm kho báu của cậu bé chăn cừu Santiago, tác phẩm dịch tiếng Việt.', 89000.00, 'B-NGK-008', 'images/nha-gia-kim.jpg', 1),
(9, 4, 1, N'Cho Tôi Xin Một Vé Đi Tuổi Thơ', N'Truyện dài nổi tiếng của Nguyễn Nhật Ánh, đưa người đọc trở về thời niên thiếu hồn nhiên, tươi đẹp.', 85000.00, 'B-VT-009', 'images/ve-tuoi-tho.jpg', 1),
(10, 5, 1, N'Đời Ngắn Đừng Ngủ Dài', N'Tập hợp những lời khuyên, bài học cuộc sống sâu sắc và thực tế từ Robin Sharma giúp phát huy tiềm năng cá nhân.', 70000.00, 'B-DND-010', 'images/doi-ngan.jpg', 1),
(11, 6, 2, N'The Alchemist (English Edition)', N'The masterpiece novel by Paulo Coelho in original English translation.', 180000.00, 'B-AL-011', 'images/the-alchemist.jpg', 1),
(12, 6, 2, N'Harry Potter and the Philosopher''s Stone', N'The first book in J.K. Rowling''s legendary wizarding series in English.', 295000.00, 'B-HP1-012', 'images/harry-potter-1.jpg', 1),
(13, 6, 2, N'To Kill a Mockingbird', N'Harper Lee''s Pulitzer Prize-winning classic novel of warmth and humor.', 220000.00, 'B-TKM-013', 'images/to-kill-mockingbird.jpg', 1),
(14, 7, 3, N'Sổ Tay Kẻ Sọc Faber-Castell A5', N'Sổ tay ghi chép kẻ sọc ngang thương hiệu Faber-Castell chất lượng cao.', 25000.00, 'S-FC-014', 'images/so-tay-fc.jpg', 1),
(15, 7, 3, N'Bút Màu Sáp Faber-Castell 12 Màu', N'Bút màu sáp trơn mượt, an toàn, không chứa chất độc hại cho trẻ em.', 42000.00, 'S-FC-015', 'images/sap-mau-fc.jpg', 1),
(16, 7, 3, N'Gôm Tẩy Faber-Castell Dust-Free', N'Gôm tẩy bút chì cao cấp, tẩy sạch bụi và không làm rách giấy.', 12000.00, 'S-FC-016', 'images/gom-fc.jpg', 1);
SET IDENTITY_INSERT [dbo].[Products] OFF;
GO

-- 9. Chèn dữ liệu Books (Con)
INSERT INTO [dbo].[Books] ([ProductId], [PublisherId], [Translator], [PublicationYear], [NumberOfPages], [CoverType], [Language], [Weight], [Dimensions]) VALUES 
(1, 1, NULL, 2019, 290, N'Bìa mềm', N'Tiếng Việt', 250.00, '13 x 20 cm'),
(2, 1, N'Nguyễn Văn A', 2021, 320, N'Bìa mềm', N'Tiếng Việt', 300.00, '14 x 20.5 cm'),
(3, 3, N'Jay Rubin', 2015, 380, N'Bìa cứng', N'Tiếng Anh', 400.00, '15 x 23 cm'),
(6, 1, NULL, 2020, 250, N'Bìa mềm', N'Tiếng Việt', 200.00, '13 x 19 cm'),
(7, 1, NULL, 2021, 280, N'Bìa mềm', N'Tiếng Việt', 240.00, '14 x 20 cm'),
(8, 1, N'Lê Chu Cầu', 2020, 220, N'Bìa mềm', N'Tiếng Việt', 180.00, '13 x 20.5 cm'),
(9, 1, NULL, 2018, 210, N'Bìa mềm', N'Tiếng Việt', 190.00, '13 x 20 cm'),
(10, 1, N'Phạm Anh Tuấn', 2019, 260, N'Bìa mềm', N'Tiếng Việt', 210.00, '13.5 x 20.5 cm'),
(11, 3, NULL, 2014, 208, N'Bìa mềm', N'Tiếng Anh', 170.00, '13.5 x 20.3 cm'),
(12, 3, NULL, 2017, 352, N'Bìa mềm', N'Tiếng Anh', 320.00, '13 x 20 cm'),
(13, 3, NULL, 2015, 376, N'Bìa mềm', N'Tiếng Anh', 310.00, '14 x 21 cm');
GO

-- 10. Chèn dữ liệu Stationeries (Con)
INSERT INTO [dbo].[Stationeries] ([ProductId], [BrandId], [Origin], [Material], [Color], [Weight], [Dimensions], [Specifications], [Warning]) VALUES 
(4, 1, N'Việt Nam', N'Nhựa', N'Xanh', 10.00, '14 cm', N'Hộp 20 cây', N'Tránh xa tầm tay trẻ em dưới 3 tuổi'),
(5, 2, N'Nhật Bản', N'Nhựa và kim loại', N'Đen', 15.00, '14.5 cm', N'Ngòi chì 0.5mm', N'Không ấn ngòi quá mạnh'),
(14, 3, N'Đức', N'Giấy và da', N'Nâu', 120.00, '15 x 21 cm', N'120 trang, 80gsm', N'Tránh tiếp xúc nguồn nước'),
(15, 3, N'Đức', N'Sáp màu', N'Đa sắc', 80.00, '12 cm', N'Hộp 12 cây', N'Không thích hợp trẻ dưới 3 tuổi'),
(16, 3, N'Đức', N'Cao su tổng hợp', N'Xanh dương', 25.00, '4 x 2 cm', N'Sản phẩm đơn chiếc', N'Không được nuốt');
GO

-- 11. Chèn dữ liệu Contributor
INSERT INTO [dbo].[Contributor] ([ProductId], [AuthorId]) VALUES 
(1, 1), -- Mắt Biếc của Nguyễn Nhật Ánh
(2, 2), -- Đắc Nhân Tâm của Dale Carnegie
(3, 3), -- Rừng Na Uy của Haruki Murakami
(6, 4), -- Số Đỏ - Vũ Trọng Phụng
(7, 5), -- Tuổi Trẻ Đáng Giá Bao Nhiêu - Rosie Nguyễn
(8, 6), -- Nhà Giả Kim - Paulo Coelho
(9, 1), -- Cho Tôi Xin Một Vé Đi Tuổi Thơ - Nguyễn Nhật Ánh
(10, 9), -- Đời Ngắn Đừng Ngủ Dài - Robin Sharma
(11, 6), -- The Alchemist - Paulo Coelho
(12, 7), -- Harry Potter 1 - J.K. Rowling
(13, 8); -- To Kill a Mockingbird - Harper Lee
GO

-- 12. Chèn dữ liệu Users (Mật khẩu mẫu ở đây tương trưng, khi code sinh viên sẽ dùng BCrypt băm)
INSERT INTO [dbo].[Users] ([RoleId], [Username], [PasswordHash], [Email], [AuthProvider], [IsActive]) VALUES 
(1, 'admin', '$2a$10$oQWb3bZ8vcAbTlWumFtMNeNoMGAa3AyHZnuTutYco4BZMD8mBIz02', 'admin@minifahasa.com', 'local', 1),
(2, 'dunglt', '$2a$10$4cKNr/Wh2GRVb/c5CKZhC.wwexcJP0w6Xi4CdLE.ky7mgW6IMTn0.', 'emp1@minifahasa.com', 'local', 1),
(2, 'giacth', '$2a$10$yo7zXNy/77M3vRXSudLD1.CSL4U8JEVIXLH4ccKnTjX5QlKHygscG', 'emp2@minifahasa.com', 'local', 1),
(2, 'duyhn', '$2a$10$JO1dBx0rh8YDRr3MbDuSAO5j5D.iAEyupOlKY8sAWAilvmNoC40Qa', 'emp3@minifahasa.com', 'local', 1),
(2, 'anhntd', '$2a$10$ibaL/gWTaAsrvmZDNKOFe.BzwxItTdVYp0VZhBSrv/6qR./JRTycS', 'emp4@minifahasa.com', 'local', 1),
(2, 'trongnp', '$2a$10$Bw8wJmP2mAtjYYrEdfDYkuTNc4vkdZwb45KKCvfrUNOh9KWzWsCIi', 'emp5@minifahasa.com', 'local', 1),
(3, 'customer1', 'cust123_hashed', 'customer1@gmail.com', 'local', 1),
(3, 'customer2', 'cust456_hashed', 'customer2@gmail.com', 'google', 1);
GO

-- 13. Chèn dữ liệu UserProfiles
INSERT INTO [dbo].[UserProfiles] ([UserId], [FullName], [Phone], [Address], [AvatarUrl]) VALUES 
(1, N'Trần Văn Quản Trị', '0901234567', N'123 Nguyễn Văn Cừ, Quận 5, TP. HCM', 'avatars/admin.jpg'),
(2, N'Lê Tiến Dũng', '0907654321', N'456 Lê Lợi, Quận 1, TP. HCM', 'avatars/emp1.jpg'),
(3, N'Trần Huỳnh Giác', '0907654322', N'456 Lê Lợi, Quận 1, TP. HCM', 'avatars/emp2.jpg'),
(4, N'Nguyễn Nhật Duy', '0907654323', N'456 Lê Lợi, Quận 1, TP. HCM', 'avatars/emp3.jpg'),
(5, N'Nguyễn Trần Đức Anh', '0907654324', N'456 Lê Lợi, Quận 1, TP. HCM', 'avatars/emp4.jpg'),
(6, N'Nguyễn Phú Trọng', '0907654325', N'456 Lê Lợi, Quận 1, TP. HCM', 'avatars/emp5.jpg'),
(7, N'Lê Minh Khách Hàng 1', '0911112222', N'789 Cách Mạng Tháng Tám, Tân Bình, TP. HCM', 'avatars/cust1.jpg'),
(8, N'Phạm Hoàng Khách Hàng 2', '0933334444', N'101 Điện Biên Phủ, Bình Thạnh, TP. HCM', 'avatars/cust2.jpg');
GO

-- 14. Chèn dữ liệu Inventory (Số lượng tồn ban đầu)
INSERT INTO [dbo].[Inventory] ([ProductId], [QuantityInStock], [WarehouseLocation], [LastUpdated]) VALUES 
(1, 100, N'Kệ A-01', GETDATE()),
(2, 150, N'Kệ B-02', GETDATE()),
(3, 30, N'Kệ Ngoại Văn C-01', GETDATE()),
(4, 1000, N'Hộp Phụ Phẩm E-05', GETDATE()),
(5, 200, N'Kệ Dụng Cụ D-02', GETDATE()),
(6, 80, N'Kệ A-03', GETDATE()),
(7, 120, N'Kệ B-04', GETDATE()),
(8, 90, N'Kệ B-05', GETDATE()),
(9, 110, N'Kệ A-02', GETDATE()),
(10, 150, N'Kệ B-01', GETDATE()),
(11, 40, N'Kệ Ngoại Văn C-02', GETDATE()),
(12, 25, N'Kệ Ngoại Văn C-03', GETDATE()),
(13, 15, N'Kệ Ngoại Văn C-04', GETDATE()),
(14, 300, N'Kệ Dụng Cụ D-03', GETDATE()),
(15, 180, N'Kệ Dụng Cụ D-04', GETDATE()),
(16, 500, N'Kệ Dụng Cụ D-05', GETDATE());
GO

-- 15. Chèn dữ liệu Orders & OrderItems (Thao tác thống kê mẫu)
-- Đơn hàng 1: Hoàn thành (Dùng để chạy thử thống kê và nút Trả Hàng)
INSERT INTO [dbo].[Orders] ([UserId], [DiscountId], [TotalAmount], [Status], [ShippingAddress], [ContactPhone], [ProcessedByUserId], [CreatedAt]) VALUES 
(7, NULL, 196000.00, 'COMPLETED', N'789 Cách Mạng Tháng Tám, Tân Bình, TP. HCM', '0911112222', 2, '2026-06-10 14:30:00');

INSERT INTO [dbo].[OrderItems] ([OrderId], [ProductId], [Quantity], [UnitPrice], [SubTotal]) VALUES 
(1, 1, 1, 110000.00, 110000.00), -- 1 cuốn Mắt Biếc
(1, 2, 1, 86000.00, 86000.00);    -- 1 cuốn Đắc Nhân Tâm

-- Đơn hàng 2: Đang chờ xử lý
INSERT INTO [dbo].[Orders] ([UserId], [DiscountId], [TotalAmount], [Status], [ShippingAddress], [ContactPhone], [ProcessedByUserId], [CreatedAt]) VALUES 
(8, 1, 225000.00, 'PENDING', N'101 Điện Biên Phủ, Bình Thạnh, TP. HCM', '0933334444', NULL, '2026-06-15 09:15:00');

INSERT INTO [dbo].[OrderItems] ([OrderId], [ProductId], [Quantity], [UnitPrice], [SubTotal]) VALUES 
(2, 3, 1, 250000.00, 250000.00); -- 1 cuốn Rừng Na Uy (Có áp mã discount)
GO
