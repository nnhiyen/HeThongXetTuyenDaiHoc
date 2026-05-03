package com.hethong.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bang_quy_doi")
public class BangQuyDoi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loai", length = 100)
    private String loai;

    @Column(name = "gia_tri")
    private Double giaTri;

    @Column(name = "diem_quy_doi")
    private Double diemQuyDoi;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    public BangQuyDoi() {}

    public BangQuyDoi(String loai, Double giaTri, Double diemQuyDoi, String moTa) {
        this.loai = loai;
        this.giaTri = giaTri;
        this.diemQuyDoi = diemQuyDoi;
        this.moTa = moTa;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLoai() { return loai; }
    public void setLoai(String loai) { this.loai = loai; }

    public Double getGiaTri() { return giaTri; }
    public void setGiaTri(Double giaTri) { this.giaTri = giaTri; }

    public Double getDiemQuyDoi() { return diemQuyDoi; }
    public void setDiemQuyDoi(Double diemQuyDoi) { this.diemQuyDoi = diemQuyDoi; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    @Override
    public String toString() {
        return "BangQuyDoi{loai='" + loai + "', giaTri=" + giaTri + ", diemQuyDoi=" + diemQuyDoi + "}";
    }
}
