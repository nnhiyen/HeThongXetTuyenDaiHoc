CREATE DATABASE IF NOT EXISTS hethong_xet_tuyen CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hethong_xet_tuyen;

CREATE TABLE IF NOT EXISTS nguoi_dung (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ten_dang_nhap VARCHAR(50) UNIQUE NOT NULL,
    mat_khau VARCHAR(255) NOT NULL,
    ho_ten VARCHAR(100),
    email VARCHAR(100),
    quyen VARCHAR(20) DEFAULT 'USER',
    trang_thai BOOLEAN DEFAULT TRUE,
    ngay_tao DATE
);

CREATE TABLE IF NOT EXISTS thi_sinh (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cccd VARCHAR(20) UNIQUE NOT NULL,
    ho_ten VARCHAR(100) NOT NULL,
    ngay_sinh DATE,
    gioi_tinh VARCHAR(10),
    dia_chi VARCHAR(255),
    so_dien_thoai VARCHAR(20),
    email VARCHAR(100),
    truong_thpt_tot_nghiep VARCHAR(200),
    nam_tot_nghiep INT
);

CREATE TABLE IF NOT EXISTS nganh (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ma_nganh VARCHAR(20) UNIQUE NOT NULL,
    ten_nganh VARCHAR(200) NOT NULL,
    mo_ta TEXT,
    chi_tieu_tuyen INT,
    diem_san_loc DOUBLE
);

CREATE TABLE IF NOT EXISTS to_hop_mon (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ma_to_hop VARCHAR(20) UNIQUE NOT NULL,
    ten_to_hop VARCHAR(100) NOT NULL,
    danh_sach_mon VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS nganh_to_hop (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nganh_id BIGINT,
    to_hop_mon_id BIGINT,
    FOREIGN KEY (nganh_id) REFERENCES nganh(id),
    FOREIGN KEY (to_hop_mon_id) REFERENCES to_hop_mon(id)
);

CREATE TABLE IF NOT EXISTS diem_thi_sinh (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    thi_sinh_id BIGINT,
    loai_diem VARCHAR(20),
    mon VARCHAR(100),
    diem DOUBLE,
    nam INT,
    FOREIGN KEY (thi_sinh_id) REFERENCES thi_sinh(id)
);

CREATE TABLE IF NOT EXISTS diem_cong (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    thi_sinh_id BIGINT,
    loai_uu_tien VARCHAR(100),
    gia_tri DOUBLE,
    mo_ta TEXT,
    FOREIGN KEY (thi_sinh_id) REFERENCES thi_sinh(id)
);

CREATE TABLE IF NOT EXISTS nguyen_vong (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    thi_sinh_id BIGINT,
    nganh_id BIGINT,
    to_hop_mon_id BIGINT,
    thu_tu INT,
    diem_xet_tuyen DOUBLE,
    trang_thai VARCHAR(20) DEFAULT 'CHO_XET',
    FOREIGN KEY (thi_sinh_id) REFERENCES thi_sinh(id),
    FOREIGN KEY (nganh_id) REFERENCES nganh(id),
    FOREIGN KEY (to_hop_mon_id) REFERENCES to_hop_mon(id)
);

CREATE TABLE IF NOT EXISTS bang_quy_doi (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    loai VARCHAR(100),
    gia_tri DOUBLE,
    diem_quy_doi DOUBLE,
    mo_ta TEXT
);

-- Sample data
INSERT INTO nguoi_dung (ten_dang_nhap, mat_khau, ho_ten, email, quyen, trang_thai, ngay_tao) VALUES
('admin', 'admin123', 'Administrator', 'admin@example.com', 'ADMIN', TRUE, CURDATE()),
('user1', 'user123', 'Nguyễn Văn A', 'user1@example.com', 'USER', TRUE, CURDATE());

INSERT INTO nganh (ma_nganh, ten_nganh, mo_ta, chi_tieu_tuyen, diem_san_loc) VALUES
('CNTT', 'Công nghệ thông tin', 'Ngành CNTT chất lượng cao', 200, 20.0),
('KT', 'Kế toán', 'Ngành kế toán tài chính', 150, 18.0),
('QT', 'Quản trị kinh doanh', 'Ngành quản trị kinh doanh', 180, 17.5);

INSERT INTO to_hop_mon (ma_to_hop, ten_to_hop, danh_sach_mon) VALUES
('A00', 'Toán - Lý - Hóa', 'Toán,Vật lý,Hóa học'),
('A01', 'Toán - Lý - Anh', 'Toán,Vật lý,Tiếng Anh'),
('D01', 'Toán - Văn - Anh', 'Toán,Ngữ văn,Tiếng Anh');

INSERT INTO thi_sinh (cccd, ho_ten, ngay_sinh, gioi_tinh, dia_chi, so_dien_thoai, email, truong_thpt_tot_nghiep, nam_tot_nghiep) VALUES
('001234567890', 'Nguyễn Thị Bích', '2005-03-15', 'Nữ', 'Hà Nội', '0912345678', 'bich@email.com', 'THPT Chu Văn An', 2023),
('001234567891', 'Trần Văn Cường', '2005-07-22', 'Nam', 'Hà Nội', '0923456789', 'cuong@email.com', 'THPT Kim Liên', 2023);
