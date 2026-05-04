package com.hethong.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "nguoi_dung")
public class NguoiDung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_dang_nhap", unique = true, nullable = false, length = 50)
    private String tenDangNhap;

    @Column(name = "mat_khau", length = 255)
    private String matKhau;

    @Column(name = "ho_ten", length = 100)
    private String hoTen;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "quyen", length = 20)
    private String quyen = "USER";

    @Column(name = "trang_thai")
    private boolean trangThai = true;

    @Column(name = "ngay_tao")
    private LocalDate ngayTao;

    public NguoiDung() {}

    public NguoiDung(String tenDangNhap, String matKhau, String hoTen, String email,
                     String quyen, boolean trangThai, LocalDate ngayTao) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.hoTen = hoTen;
        this.email = email;
        this.quyen = quyen;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getQuyen() { return quyen; }
    public void setQuyen(String quyen) { this.quyen = quyen; }

    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }

    public LocalDate getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDate ngayTao) { this.ngayTao = ngayTao; }

    @Override
    public String toString() {
        return "NguoiDung{id=" + id + ", tenDangNhap='" + tenDangNhap + "', hoTen='" + hoTen + "', quyen='" + quyen + "'}";
    }
}
