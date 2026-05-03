package com.hethong.dao;

import com.hethong.config.HibernateUtil;
import com.hethong.model.Nganh;
import com.hethong.model.NganhToHop;
import com.hethong.model.ToHopMon;
import org.hibernate.Session;

import java.util.List;

public class NganhToHopDAO extends GenericDAO<NganhToHop> {

    public NganhToHopDAO() {
        super(NganhToHop.class);
    }

    public List<NganhToHop> findByNganh(Nganh nganh) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM NganhToHop n WHERE n.nganh = :nganh", NganhToHop.class)
                    .setParameter("nganh", nganh)
                    .list();
        }
    }

    public List<NganhToHop> findByToHopMon(ToHopMon toHopMon) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM NganhToHop n WHERE n.toHopMon = :toHopMon", NganhToHop.class)
                    .setParameter("toHopMon", toHopMon)
                    .list();
        }
    }

    public NganhToHop findByNganhAndToHopMon(Nganh nganh, ToHopMon toHopMon) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM NganhToHop n WHERE n.nganh = :nganh AND n.toHopMon = :toHopMon", NganhToHop.class)
                    .setParameter("nganh", nganh)
                    .setParameter("toHopMon", toHopMon)
                    .uniqueResult();
        }
    }
}
