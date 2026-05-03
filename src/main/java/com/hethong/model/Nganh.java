package com.hethong.model;

import jakarta.persistence.*;

@Entity
@Table(name = "nganh")
public class Nganh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_nganh", unique = true, nullable = false, length = 20)
    private String maNganh;

    @Column(name = "ten_nganh", nullable = false, length = 200)
    private String tenNganh;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "chi_tieu_tuyen")
    private Integer chiTieuTuyen;

    @Column(name = "diem_san_loc")
    private Double diemSanLoc;

    public Nganh() {}

    public Nganh(String maNganh, String tenNganh, String moTa, Integer chiTieuTuyen, Double diemSanLoc) {
        this.maNganh = maNganh;
        this.tenNganh = tenNganh;
        this.moTa = moTa;
        this.chiTieuTuyen = chiTieuTuyen;
        this.diemSanLoc = diemSanLoc;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMaNganh() { return maNganh; }
    public void setMaNganh(String maNganh) { this.maNganh = maNganh; }

    public String getTenNganh() { return tenNganh; }
    public void setTenNganh(String tenNganh) { this.tenNganh = tenNganh; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public Integer getChiTieuTuyen() { return chiTieuTuyen; }
    public void setChiTieuTuyen(Integer chiTieuTuyen) { this.chiTieuTuyen = chiTieuTuyen; }

    public Double getDiemSanLoc() { return diemSanLoc; }
    public void setDiemSanLoc(Double diemSanLoc) { this.diemSanLoc = diemSanLoc; }

    @Override
    public String toString() {
        return tenNganh + " (" + maNganh + ")";
    }
}
