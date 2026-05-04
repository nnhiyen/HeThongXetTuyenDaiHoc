package com.hethong.dao;

import com.hethong.config.HibernateUtil;
import com.hethong.model.NguoiDung;
import org.hibernate.Session;

import java.util.List;

public class NguoiDungDAO extends GenericDAO<NguoiDung> {

    public NguoiDungDAO() {
        super(NguoiDung.class);
    }

    public NguoiDung findByTenDangNhap(String tenDangNhap) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM NguoiDung n WHERE n.tenDangNhap = :tenDangNhap", NguoiDung.class)
                    .setParameter("tenDangNhap", tenDangNhap)
                    .uniqueResult();
        }
    }

    public NguoiDung findByTenDangNhapAndMatKhau(String tenDangNhap, String matKhau) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM NguoiDung n WHERE n.tenDangNhap = :tenDangNhap AND n.matKhau = :matKhau AND n.trangThai = true",
                    NguoiDung.class)
                    .setParameter("tenDangNhap", tenDangNhap)
                    .setParameter("matKhau", matKhau)
                    .uniqueResult();
        }
    }

    public List<NguoiDung> findByQuyen(String quyen) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM NguoiDung n WHERE n.quyen = :quyen", NguoiDung.class)
                    .setParameter("quyen", quyen)
                    .list();
        }
    }
}
