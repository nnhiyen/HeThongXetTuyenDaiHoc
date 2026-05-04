package com.hethong.model;

import jakarta.persistence.*;

@Entity
@Table(name = "diem_cong")
public class DiemCong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "thi_sinh_id")
    private ThiSinh thiSinh;

    @Column(name = "loai_uu_tien", length = 100)
    private String loaiUuTien;

    @Column(name = "gia_tri")
    private Double giaTri;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    public DiemCong() {}

    public DiemCong(ThiSinh thiSinh, String loaiUuTien, Double giaTri, String moTa) {
        this.thiSinh = thiSinh;
        this.loaiUuTien = loaiUuTien;
        this.giaTri = giaTri;
        this.moTa = moTa;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ThiSinh getThiSinh() { return thiSinh; }
    public void setThiSinh(ThiSinh thiSinh) { this.thiSinh = thiSinh; }

    public String getLoaiUuTien() { return loaiUuTien; }
    public void setLoaiUuTien(String loaiUuTien) { this.loaiUuTien = loaiUuTien; }

    public Double getGiaTri() { return giaTri; }
    public void setGiaTri(Double giaTri) { this.giaTri = giaTri; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    @Override
    public String toString() {
        return "DiemCong{thiSinh=" + (thiSinh != null ? thiSinh.getHoTen() : "null")
                + ", loaiUuTien='" + loaiUuTien + "', giaTri=" + giaTri + "}";
    }
}
