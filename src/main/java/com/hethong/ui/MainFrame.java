package com.hethong.ui;

import com.hethong.model.NguoiDung;
import com.hethong.ui.panels.*;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final NguoiDung currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public MainFrame(NguoiDung currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }

    private void initComponents() {
        setTitle("Hệ Thống Xét Tuyển Đại Học");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(41, 128, 185));
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JLabel lblTitle = new JLabel("HỆ THỐNG XÉT TUYỂN ĐẠI HỌC");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);
        String displayName = currentUser.getHoTen() != null ? currentUser.getHoTen() : currentUser.getTenDangNhap();
        JLabel lblUser = new JLabel("Xin chào: " + displayName + " (" + currentUser.getQuyen() + ")");
        lblUser.setForeground(Color.WHITE);
        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        userPanel.add(lblUser);
        userPanel.add(btnLogout);

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(userPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Left navigation panel
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(52, 73, 94));
        leftPanel.setPreferredSize(new Dimension(200, 0));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        // Content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Add panels and nav buttons
        addNavAndPanel(leftPanel, "Quản lý người dùng", "NguoiDung",
                "ADMIN".equals(currentUser.getQuyen()) ? new NguoiDungPanel() : new JPanel());
        addNavAndPanel(leftPanel, "Quản lý thí sinh", "ThiSinh", new ThiSinhPanel());
        addNavAndPanel(leftPanel, "Quản lý ngành", "Nganh", new NganhPanel());
        addNavAndPanel(leftPanel, "Quản lý tổ hợp môn", "ToHopMon", new ToHopMonPanel());
        addNavAndPanel(leftPanel, "Ngành - Tổ hợp môn", "NganhToHop", new NganhToHopPanel());
        addNavAndPanel(leftPanel, "Quản lý điểm", "DiemThiSinh", new DiemThiSinhPanel());
        addNavAndPanel(leftPanel, "Điểm cộng", "DiemCong", new DiemCongPanel());
        addNavAndPanel(leftPanel, "Nguyện vọng", "NguyenVong", new NguyenVongPanel());
        addNavAndPanel(leftPanel, "Bảng quy đổi", "BangQuyDoi", new BangQuyDoiPanel());

        leftPanel.add(Box.createVerticalGlue());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, contentPanel);
        splitPane.setDividerLocation(200);
        splitPane.setDividerSize(3);
        add(splitPane, BorderLayout.CENTER);

        // Show first available panel
        cardLayout.show(contentPanel, "ThiSinh");
    }

    private void addNavAndPanel(JPanel navPanel, String label, String cardName, JComponent panel) {
        contentPanel.add(panel, cardName);

        JButton btn = new JButton(label);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(52, 73, 94));
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(41, 128, 185));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(52, 73, 94));
            }
        });

        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        navPanel.add(btn);
        navPanel.add(Box.createRigidArea(new Dimension(0, 2)));
    }
}
