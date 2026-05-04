package com.hethong.dao;

import com.hethong.config.HibernateUtil;
import com.hethong.model.Nganh;
import org.hibernate.Session;

public class NganhDAO extends GenericDAO<Nganh> {

    public NganhDAO() {
        super(Nganh.class);
    }

    public Nganh findByMaNganh(String maNganh) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Nganh n WHERE n.maNganh = :maNganh", Nganh.class)
                    .setParameter("maNganh", maNganh)
                    .uniqueResult();
        }
    }
}
