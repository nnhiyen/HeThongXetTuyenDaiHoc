package com.hethong.model;

import jakarta.persistence.*;

@Entity
@Table(name = "nguyen_vong")
public class NguyenVong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "thi_sinh_id")
    private ThiSinh thiSinh;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nganh_id")
    private Nganh nganh;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_hop_mon_id")
    private ToHopMon toHopMon;

    @Column(name = "thu_tu")
    private Integer thuTu;

    @Column(name = "diem_xet_tuyen")
    private Double diemXetTuyen;

    @Column(name = "trang_thai", length = 20)
    private String trangThai = "CHO_XET";

    public NguyenVong() {}

    public NguyenVong(ThiSinh thiSinh, Nganh nganh, ToHopMon toHopMon,
                      Integer thuTu, Double diemXetTuyen, String trangThai) {
        this.thiSinh = thiSinh;
        this.nganh = nganh;
        this.toHopMon = toHopMon;
        this.thuTu = thuTu;
        this.diemXetTuyen = diemXetTuyen;
        this.trangThai = trangThai;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ThiSinh getThiSinh() { return thiSinh; }
    public void setThiSinh(ThiSinh thiSinh) { this.thiSinh = thiSinh; }

    public Nganh getNganh() { return nganh; }
    public void setNganh(Nganh nganh) { this.nganh = nganh; }

    public ToHopMon getToHopMon() { return toHopMon; }
    public void setToHopMon(ToHopMon toHopMon) { this.toHopMon = toHopMon; }

    public Integer getThuTu() { return thuTu; }
    public void setThuTu(Integer thuTu) { this.thuTu = thuTu; }

    public Double getDiemXetTuyen() { return diemXetTuyen; }
    public void setDiemXetTuyen(Double diemXetTuyen) { this.diemXetTuyen = diemXetTuyen; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    @Override
    public String toString() {
        return "NguyenVong{thiSinh=" + (thiSinh != null ? thiSinh.getHoTen() : "null")
                + ", nganh=" + (nganh != null ? nganh.getTenNganh() : "null")
                + ", trangThai='" + trangThai + "'}";
    }
}
