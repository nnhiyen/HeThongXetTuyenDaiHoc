package com.hethong.dao;

import com.hethong.config.HibernateUtil;
import com.hethong.model.DiemThiSinh;
import com.hethong.model.ThiSinh;
import org.hibernate.Session;

import java.util.List;

public class DiemThiSinhDAO extends GenericDAO<DiemThiSinh> {

    public DiemThiSinhDAO() {
        super(DiemThiSinh.class);
    }

    public List<DiemThiSinh> findByThiSinh(ThiSinh thiSinh) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM DiemThiSinh d WHERE d.thiSinh = :thiSinh", DiemThiSinh.class)
                    .setParameter("thiSinh", thiSinh)
                    .list();
        }
    }

    public List<DiemThiSinh> findByLoaiDiem(String loaiDiem) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM DiemThiSinh d WHERE d.loaiDiem = :loaiDiem", DiemThiSinh.class)
                    .setParameter("loaiDiem", loaiDiem)
                    .list();
        }
    }

    public List<DiemThiSinh> findByThiSinhAndLoaiDiem(ThiSinh thiSinh, String loaiDiem) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM DiemThiSinh d WHERE d.thiSinh = :thiSinh AND d.loaiDiem = :loaiDiem",
                    DiemThiSinh.class)
                    .setParameter("thiSinh", thiSinh)
                    .setParameter("loaiDiem", loaiDiem)
                    .list();
        }
    }
}
