package com.hethong.dao;

import com.hethong.config.HibernateUtil;
import com.hethong.model.ThiSinh;
import org.hibernate.Session;

import java.util.List;

public class ThiSinhDAO extends GenericDAO<ThiSinh> {

    public ThiSinhDAO() {
        super(ThiSinh.class);
    }

    public ThiSinh findByCccd(String cccd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM ThiSinh t WHERE t.cccd = :cccd", ThiSinh.class)
                    .setParameter("cccd", cccd)
                    .uniqueResult();
        }
    }

    public List<ThiSinh> searchByNameOrCccd(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String pattern = "%" + keyword + "%";
            return session.createQuery(
                    "FROM ThiSinh t WHERE t.hoTen LIKE :kw OR t.cccd LIKE :kw", ThiSinh.class)
                    .setParameter("kw", pattern)
                    .list();
        }
    }

    public List<ThiSinh> findPaginated(int page, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM ThiSinh t ORDER BY t.id", ThiSinh.class)
                    .setFirstResult((page - 1) * pageSize)
                    .setMaxResults(pageSize)
                    .list();
        }
    }

    public long countAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT COUNT(t) FROM ThiSinh t", Long.class)
                    .uniqueResult();
        }
    }
}
