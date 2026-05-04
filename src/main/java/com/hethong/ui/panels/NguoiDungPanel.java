package com.hethong.ui.panels;

import com.hethong.dao.NguoiDungDAO;
import com.hethong.model.NguoiDung;
import com.hethong.util.PasswordUtil;
import com.opencsv.CSVReader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.util.List;

public class NguoiDungPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private final NguoiDungDAO dao = new NguoiDungDAO();

    private static final String[] COLUMNS = {"ID", "Tên đăng nhập", "Họ tên", "Email", "Quyền", "Trạng thái", "Ngày tạo"};

    public NguoiDungPanel() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Quản lý người dùng", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Button panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnRefresh = new JButton("Làm mới");
        JButton btnChangePass = new JButton("Đổi mật khẩu");
        JButton btnImportCsv = new JButton("Import CSV");

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);
        btnPanel.add(btnChangePass);
        btnPanel.add(btnImportCsv);
        add(btnPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadData());
        btnChangePass.addActionListener(e -> showChangePasswordDialog());
        btnImportCsv.addActionListener(e -> importCsv());
    }

    public void loadData() {
        tableModel.setRowCount(0);
        try {
            List<NguoiDung> list = dao.findAll();
            for (NguoiDung u : list) {
                tableModel.addRow(new Object[]{
                        u.getId(), u.getTenDangNhap(), u.getHoTen(), u.getEmail(),
                        u.getQuyen(), u.isTrangThai() ? "Hoạt động" : "Khóa",
                        u.getNgayTao() != null ? u.getNgayTao().toString() : ""
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm người dùng", true);
        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtTenDangNhap = new JTextField(20);
        JPasswordField txtMatKhau = new JPasswordField(20);
        JTextField txtHoTen = new JTextField(20);
        JTextField txtEmail = new JTextField(20);
        JComboBox<String> cboQuyen = new JComboBox<>(new String[]{"USER", "ADMIN"});
        JCheckBox chkTrangThai = new JCheckBox("Hoạt động", true);

        addFormRow(panel, gbc, 0, "Tên đăng nhập *:", txtTenDangNhap);
        addFormRow(panel, gbc, 1, "Mật khẩu *:", txtMatKhau);
        addFormRow(panel, gbc, 2, "Họ tên:", txtHoTen);
        addFormRow(panel, gbc, 3, "Email:", txtEmail);
        addFormRow(panel, gbc, 4, "Quyền:", cboQuyen);
        addFormRow(panel, gbc, 5, "Trạng thái:", chkTrangThai);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            String tenDN = txtTenDangNhap.getText().trim();
            String matKhau = new String(txtMatKhau.getPassword());
            if (tenDN.isEmpty() || matKhau.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Tên đăng nhập và mật khẩu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                NguoiDung u = new NguoiDung();
                u.setTenDangNhap(tenDN);
                u.setMatKhau(PasswordUtil.hash(matKhau));
                u.setHoTen(txtHoTen.getText().trim());
                u.setEmail(txtEmail.getText().trim());
                u.setQuyen((String) cboQuyen.getSelectedItem());
                u.setTrangThai(chkTrangThai.isSelected());
                u.setNgayTao(LocalDate.now());
                dao.save(u);
                loadData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Thêm người dùng thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn người dùng cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = (Long) tableModel.getValueAt(row, 0);
        NguoiDung u;
        try {
            u = dao.findById(id);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (u == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa người dùng", true);
        dialog.setSize(420, 340);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTenDangNhap = new JLabel(u.getTenDangNhap());
        lblTenDangNhap.setFont(lblTenDangNhap.getFont().deriveFont(Font.BOLD));
        JTextField txtHoTen = new JTextField(u.getHoTen() != null ? u.getHoTen() : "", 20);
        JTextField txtEmail = new JTextField(u.getEmail() != null ? u.getEmail() : "", 20);
        JComboBox<String> cboQuyen = new JComboBox<>(new String[]{"USER", "ADMIN"});
        cboQuyen.setSelectedItem(u.getQuyen());
        JCheckBox chkTrangThai = new JCheckBox("Hoạt động", u.isTrangThai());

        addFormRow(panel, gbc, 0, "Tên đăng nhập:", lblTenDangNhap);
        addFormRow(panel, gbc, 1, "Họ tên:", txtHoTen);
        addFormRow(panel, gbc, 2, "Email:", txtEmail);
        addFormRow(panel, gbc, 3, "Quyền:", cboQuyen);
        addFormRow(panel, gbc, 4, "Trạng thái:", chkTrangThai);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                u.setHoTen(txtHoTen.getText().trim());
                u.setEmail(txtEmail.getText().trim());
                u.setQuyen((String) cboQuyen.getSelectedItem());
                u.setTrangThai(chkTrangThai.isSelected());
                dao.update(u);
                loadData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.setVisible(true);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn người dùng cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa người dùng này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            NguoiDung u = dao.findById(id);
            if (u != null) {
                dao.delete(u);
                loadData();
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi xóa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showChangePasswordDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn người dùng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = (Long) tableModel.getValueAt(row, 0);
        NguoiDung u;
        try {
            u = dao.findById(id);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (u == null) return;

        JPasswordField newPass = new JPasswordField(20);
        int result = JOptionPane.showConfirmDialog(this, new Object[]{"Mật khẩu mới:", newPass},
                "Đổi mật khẩu - " + u.getTenDangNhap(), JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String pass = new String(newPass.getPassword());
            if (!pass.isEmpty()) {
                try {
                    u.setMatKhau(PasswordUtil.hash(pass));
                    dao.update(u);
                    JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void importCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = chooser.getSelectedFile();
        int success = 0, fail = 0;
        try (CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String[] line;
            boolean first = true;
            while ((line = reader.readNext()) != null) {
                if (first) { first = false; continue; } // skip header
                if (line.length < 4) { fail++; continue; }
                try {
                    NguoiDung u = new NguoiDung();
                    u.setTenDangNhap(line[0].trim());
                    String rawPass = line[1].trim();
                    u.setMatKhau(rawPass.isEmpty() ? PasswordUtil.hash("changeme") : PasswordUtil.hash(rawPass));
                    u.setHoTen(line[2].trim());
                    u.setEmail(line.length > 3 ? line[3].trim() : "");
                    u.setQuyen(line.length > 4 ? line[4].trim() : "USER");
                    u.setTrangThai(true);
                    u.setNgayTao(LocalDate.now());
                    dao.save(u);
                    success++;
                } catch (Exception ex) {
                    fail++;
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi đọc file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        loadData();
        JOptionPane.showMessageDialog(this, "Import hoàn tất: " + success + " thành công, " + fail + " lỗi.");
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, Component comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(comp, gbc);
    }
}
