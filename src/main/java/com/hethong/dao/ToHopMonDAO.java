package com.hethong.dao;

import com.hethong.config.HibernateUtil;
import com.hethong.model.ToHopMon;
import org.hibernate.Session;

public class ToHopMonDAO extends GenericDAO<ToHopMon> {

    public ToHopMonDAO() {
        super(ToHopMon.class);
    }

    public ToHopMon findByMaToHop(String maToHop) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM ToHopMon t WHERE t.maToHop = :maToHop", ToHopMon.class)
                    .setParameter("maToHop", maToHop)
                    .uniqueResult();
        }
    }
}
