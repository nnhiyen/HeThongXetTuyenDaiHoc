package com.hethong.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "thi_sinh")
public class ThiSinh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cccd", unique = true, nullable = false, length = 20)
    private String cccd;

    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "gioi_tinh", length = 10)
    private String gioiTinh;

    @Column(name = "dia_chi", length = 255)
    private String diaChi;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "truong_thpt_tot_nghiep", length = 200)
    private String truongThptTotNghiep;

    @Column(name = "nam_tot_nghiep")
    private Integer namTotNghiep;

    public ThiSinh() {}

    public ThiSinh(String cccd, String hoTen, LocalDate ngaySinh, String gioiTinh,
                   String diaChi, String soDienThoai, String email,
                   String truongThptTotNghiep, Integer namTotNghiep) {
        this.cccd = cccd;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.truongThptTotNghiep = truongThptTotNghiep;
        this.namTotNghiep = namTotNghiep;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTruongThptTotNghiep() { return truongThptTotNghiep; }
    public void setTruongThptTotNghiep(String truongThptTotNghiep) { this.truongThptTotNghiep = truongThptTotNghiep; }

    public Integer getNamTotNghiep() { return namTotNghiep; }
    public void setNamTotNghiep(Integer namTotNghiep) { this.namTotNghiep = namTotNghiep; }

    @Override
    public String toString() {
        return hoTen + " (" + cccd + ")";
    }
}
