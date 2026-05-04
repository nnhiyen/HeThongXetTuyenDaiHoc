package com.hethong.model;

import jakarta.persistence.*;

@Entity
@Table(name = "nganh_to_hop")
public class NganhToHop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nganh_id")
    private Nganh nganh;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_hop_mon_id")
    private ToHopMon toHopMon;

    public NganhToHop() {}

    public NganhToHop(Nganh nganh, ToHopMon toHopMon) {
        this.nganh = nganh;
        this.toHopMon = toHopMon;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Nganh getNganh() { return nganh; }
    public void setNganh(Nganh nganh) { this.nganh = nganh; }

    public ToHopMon getToHopMon() { return toHopMon; }
    public void setToHopMon(ToHopMon toHopMon) { this.toHopMon = toHopMon; }

    @Override
    public String toString() {
        return "NganhToHop{nganh=" + (nganh != null ? nganh.getTenNganh() : "null")
                + ", toHopMon=" + (toHopMon != null ? toHopMon.getTenToHop() : "null") + "}";
    }
}
