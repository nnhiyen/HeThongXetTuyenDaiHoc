package com.hethong.model;

import jakarta.persistence.*;

@Entity
@Table(name = "to_hop_mon")
public class ToHopMon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_to_hop", unique = true, nullable = false, length = 20)
    private String maToHop;

    @Column(name = "ten_to_hop", nullable = false, length = 100)
    private String tenToHop;

    @Column(name = "danh_sach_mon", length = 255)
    private String danhSachMon;

    public ToHopMon() {}

    public ToHopMon(String maToHop, String tenToHop, String danhSachMon) {
        this.maToHop = maToHop;
        this.tenToHop = tenToHop;
        this.danhSachMon = danhSachMon;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMaToHop() { return maToHop; }
    public void setMaToHop(String maToHop) { this.maToHop = maToHop; }

    public String getTenToHop() { return tenToHop; }
    public void setTenToHop(String tenToHop) { this.tenToHop = tenToHop; }

    public String getDanhSachMon() { return danhSachMon; }
    public void setDanhSachMon(String danhSachMon) { this.danhSachMon = danhSachMon; }

    @Override
    public String toString() {
        return tenToHop + " (" + maToHop + ")";
    }
}
