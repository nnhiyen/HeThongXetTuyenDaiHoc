package com.hethong.dao;

import com.hethong.config.HibernateUtil;
import com.hethong.model.BangQuyDoi;
import org.hibernate.Session;

import java.util.List;

public class BangQuyDoiDAO extends GenericDAO<BangQuyDoi> {

    public BangQuyDoiDAO() {
        super(BangQuyDoi.class);
    }

    public List<BangQuyDoi> findByLoai(String loai) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM BangQuyDoi b WHERE b.loai = :loai", BangQuyDoi.class)
                    .setParameter("loai", loai)
                    .list();
        }
    }

    public List<BangQuyDoi> searchByLoaiOrMoTa(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String pattern = "%" + keyword + "%";
            return session.createQuery(
                    "FROM BangQuyDoi b WHERE b.loai LIKE :kw OR b.moTa LIKE :kw", BangQuyDoi.class)
                    .setParameter("kw", pattern)
                    .list();
        }
    }
}
