package com.hethong.dao;

import com.hethong.config.HibernateUtil;
import com.hethong.model.DiemCong;
import com.hethong.model.ThiSinh;
import org.hibernate.Session;

import java.util.List;

public class DiemCongDAO extends GenericDAO<DiemCong> {

    public DiemCongDAO() {
        super(DiemCong.class);
    }

    public List<DiemCong> findByThiSinh(ThiSinh thiSinh) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM DiemCong d WHERE d.thiSinh = :thiSinh", DiemCong.class)
                    .setParameter("thiSinh", thiSinh)
                    .list();
        }
    }
}
