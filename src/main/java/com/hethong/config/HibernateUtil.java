package com.hethong.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            // Load base config (dialect, ddl-auto, mappings, etc.) from hibernate.cfg.xml,
            // then override connection settings from environment variables so that
            // credentials are never required to be hard-coded in a committed file.
            String dbHost = getEnv("DB_HOST", "localhost");
            String dbPort = getEnv("DB_PORT", "3306");
            String dbName = getEnv("DB_NAME", "hethong_xet_tuyen");
            String dbUser = getEnv("DB_USER", "root");
            String dbPass = getEnv("DB_PASS", "root");

            String url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName
                    + "?serverTimezone=UTC&allowPublicKeyRetrieval=true";

            Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
            configuration.setProperty("hibernate.connection.url", url);
            configuration.setProperty("hibernate.connection.username", dbUser);
            configuration.setProperty("hibernate.connection.password", dbPass);

            sessionFactory = configuration.buildSessionFactory();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}

