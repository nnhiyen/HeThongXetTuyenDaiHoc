package com.hethong.model;

import jakarta.persistence.*;

@Entity
@Table(name = "diem_thi_sinh")
public class DiemThiSinh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "thi_sinh_id")
    private ThiSinh thiSinh;

    @Column(name = "loai_diem", length = 20)
    private String loaiDiem;

    @Column(name = "mon", length = 100)
    private String mon;

    @Column(name = "diem")
    private Double diem;

    @Column(name = "nam")
    private Integer nam;

    public DiemThiSinh() {}

    public DiemThiSinh(ThiSinh thiSinh, String loaiDiem, String mon, Double diem, Integer nam) {
        this.thiSinh = thiSinh;
        this.loaiDiem = loaiDiem;
        this.mon = mon;
        this.diem = diem;
        this.nam = nam;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ThiSinh getThiSinh() { return thiSinh; }
    public void setThiSinh(ThiSinh thiSinh) { this.thiSinh = thiSinh; }

    public String getLoaiDiem() { return loaiDiem; }
    public void setLoaiDiem(String loaiDiem) { this.loaiDiem = loaiDiem; }

    public String getMon() { return mon; }
    public void setMon(String mon) { this.mon = mon; }

    public Double getDiem() { return diem; }
    public void setDiem(Double diem) { this.diem = diem; }

    public Integer getNam() { return nam; }
    public void setNam(Integer nam) { this.nam = nam; }

    @Override
    public String toString() {
        return "DiemThiSinh{thiSinh=" + (thiSinh != null ? thiSinh.getHoTen() : "null")
                + ", loaiDiem='" + loaiDiem + "', mon='" + mon + "', diem=" + diem + "}";
    }
}
