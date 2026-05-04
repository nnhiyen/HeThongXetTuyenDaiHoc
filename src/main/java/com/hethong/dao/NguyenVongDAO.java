package com.hethong.dao;

import com.hethong.config.HibernateUtil;
import com.hethong.model.Nganh;
import com.hethong.model.NguyenVong;
import com.hethong.model.ThiSinh;
import org.hibernate.Session;

import java.util.List;

public class NguyenVongDAO extends GenericDAO<NguyenVong> {

    public NguyenVongDAO() {
        super(NguyenVong.class);
    }

    public List<NguyenVong> findByThiSinh(ThiSinh thiSinh) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM NguyenVong n WHERE n.thiSinh = :thiSinh ORDER BY n.thuTu", NguyenVong.class)
                    .setParameter("thiSinh", thiSinh)
                    .list();
        }
    }

    public List<NguyenVong> findByNganh(Nganh nganh) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM NguyenVong n WHERE n.nganh = :nganh", NguyenVong.class)
                    .setParameter("nganh", nganh)
                    .list();
        }
    }

    public List<NguyenVong> findByTrangThai(String trangThai) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM NguyenVong n WHERE n.trangThai = :trangThai", NguyenVong.class)
                    .setParameter("trangThai", trangThai)
                    .list();
        }
    }
}
