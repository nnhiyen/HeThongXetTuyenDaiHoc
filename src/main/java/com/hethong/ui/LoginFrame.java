package com.hethong.ui;

import com.hethong.dao.NguoiDungDAO;
import com.hethong.model.NguoiDung;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblStatus;
    private final NguoiDungDAO nguoiDungDAO = new NguoiDungDAO();

    public LoginFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Hệ Thống Xét Tuyển Đại Học - Đăng Nhập");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Title
        JLabel lblTitle = new JLabel("Đăng Nhập Hệ Thống", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtUsername = new JTextField(20);
        formPanel.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        txtPassword = new JPasswordField(20);
        formPanel.add(txtPassword, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        btnLogin = new JButton("Đăng Nhập");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(120, 35));

        lblStatus = new JLabel("", SwingConstants.CENTER);
        lblStatus.setForeground(Color.RED);

        bottomPanel.add(btnLogin, BorderLayout.CENTER);
        bottomPanel.add(lblStatus, BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Action listeners
        btnLogin.addActionListener(e -> performLogin());
        txtPassword.addActionListener(e -> performLogin());
        txtUsername.addActionListener(e -> txtPassword.requestFocus());

        getRootPane().setDefaultButton(btnLogin);
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        btnLogin.setEnabled(false);
        lblStatus.setText("Đang xử lý...");
        lblStatus.setForeground(Color.BLUE);

        SwingWorker<NguoiDung, Void> worker = new SwingWorker<>() {
            @Override
            protected NguoiDung doInBackground() {
                try {
                    List<NguoiDung> all = nguoiDungDAO.findAll();
                    if (all.isEmpty()) {
                        // Fallback hardcoded credentials when DB is empty
                        if ("admin".equals(username) && "admin123".equals(password)) {
                            NguoiDung fallback = new NguoiDung();
                            fallback.setTenDangNhap("admin");
                            fallback.setHoTen("Administrator");
                            fallback.setQuyen("ADMIN");
                            fallback.setTrangThai(true);
                            return fallback;
                        }
                        return null;
                    }
                    return nguoiDungDAO.findByTenDangNhapAndMatKhau(username, password);
                } catch (Exception ex) {
                    // DB not available: use hardcoded fallback
                    if ("admin".equals(username) && "admin123".equals(password)) {
                        NguoiDung fallback = new NguoiDung();
                        fallback.setTenDangNhap("admin");
                        fallback.setHoTen("Administrator");
                        fallback.setQuyen("ADMIN");
                        fallback.setTrangThai(true);
                        return fallback;
                    }
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    NguoiDung user = get();
                    if (user != null) {
                        MainFrame mainFrame = new MainFrame(user);
                        mainFrame.setVisible(true);
                        dispose();
                    } else {
                        lblStatus.setForeground(Color.RED);
                        lblStatus.setText("Tên đăng nhập hoặc mật khẩu không đúng!");
                        txtPassword.setText("");
                        btnLogin.setEnabled(true);
                    }
                } catch (Exception ex) {
                    lblStatus.setForeground(Color.RED);
                    lblStatus.setText("Lỗi kết nối: " + ex.getMessage());
                    btnLogin.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
}
