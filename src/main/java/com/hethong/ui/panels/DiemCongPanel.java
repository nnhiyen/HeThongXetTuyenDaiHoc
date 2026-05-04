package com.hethong.ui.panels;

import com.hethong.dao.DiemCongDAO;
import com.hethong.dao.ThiSinhDAO;
import com.hethong.model.DiemCong;
import com.hethong.model.ThiSinh;
import com.opencsv.CSVReader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.List;

public class DiemCongPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private final DiemCongDAO dao = new DiemCongDAO();
    private final ThiSinhDAO thiSinhDAO = new ThiSinhDAO();

    private static final String[] COLUMNS = {"ID", "CCCD Thí sinh", "Họ tên", "Loại ưu tiên", "Giá trị", "Mô tả"};

    public DiemCongPanel() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Quản lý điểm cộng", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnRefresh = new JButton("Làm mới");
        JButton btnImportCsv = new JButton("Import CSV");
        btnPanel.add(btnAdd); btnPanel.add(btnEdit); btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh); btnPanel.add(btnImportCsv);
        add(btnPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadData());
        btnImportCsv.addActionListener(e -> importCsv());
    }

    public void loadData() {
        tableModel.setRowCount(0);
        try {
            for (DiemCong dc : dao.findAll()) {
                String cccd = dc.getThiSinh() != null ? dc.getThiSinh().getCccd() : "";
                String hoTen = dc.getThiSinh() != null ? dc.getThiSinh().getHoTen() : "";
                tableModel.addRow(new Object[]{dc.getId(), cccd, hoTen, dc.getLoaiUuTien(), dc.getGiaTri(), dc.getMoTa()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddDialog() {
        List<ThiSinh> thiSinhList;
        try { thiSinhList = thiSinhDAO.findAll(); } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách thí sinh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); return;
        }

        JComboBox<ThiSinh> cboThiSinh = new JComboBox<>(thiSinhList.toArray(new ThiSinh[0]));
        JTextField txtLoaiUuTien = new JTextField(20);
        JTextField txtGiaTri = new JTextField(10);
        JTextArea txtMoTa = new JTextArea(3, 20);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; panel.add(new JLabel("Thí sinh *:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; panel.add(cboThiSinh, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; panel.add(new JLabel("Loại ưu tiên *:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; panel.add(txtLoaiUuTien, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; panel.add(new JLabel("Giá trị *:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; panel.add(txtGiaTri, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; panel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; panel.add(new JScrollPane(txtMoTa), gbc);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm điểm cộng", true);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu"); JButton btnCancel = new JButton("Hủy");
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER); dialog.add(btnPanel, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            if (txtLoaiUuTien.getText().trim().isEmpty() || txtGiaTri.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Loại ưu tiên và Giá trị không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE); return;
            }
            try {
                DiemCong dc = new DiemCong();
                dc.setThiSinh((ThiSinh) cboThiSinh.getSelectedItem());
                dc.setLoaiUuTien(txtLoaiUuTien.getText().trim());
                dc.setGiaTri(Double.parseDouble(txtGiaTri.getText().trim()));
                dc.setMoTa(txtMoTa.getText().trim());
                dao.save(dc); loadData(); dialog.dispose();
                JOptionPane.showMessageDialog(this, "Thêm điểm cộng thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng!", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
        Long id = (Long) tableModel.getValueAt(row, 0);
        DiemCong dc;
        try { dc = dao.findById(id); } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); return;
        }
        if (dc == null) return;

        List<ThiSinh> thiSinhList;
        try { thiSinhList = thiSinhDAO.findAll(); } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); return;
        }

        JComboBox<ThiSinh> cboThiSinh = new JComboBox<>(thiSinhList.toArray(new ThiSinh[0]));
        if (dc.getThiSinh() != null) cboThiSinh.setSelectedItem(thiSinhList.stream().filter(ts -> ts.getId().equals(dc.getThiSinh().getId())).findFirst().orElse(null));
        JTextField txtLoaiUuTien = new JTextField(dc.getLoaiUuTien() != null ? dc.getLoaiUuTien() : "", 20);
        JTextField txtGiaTri = new JTextField(dc.getGiaTri() != null ? dc.getGiaTri().toString() : "", 10);
        JTextArea txtMoTa = new JTextArea(dc.getMoTa() != null ? dc.getMoTa() : "", 3, 20);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; panel.add(new JLabel("Thí sinh:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; panel.add(cboThiSinh, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; panel.add(new JLabel("Loại ưu tiên:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; panel.add(txtLoaiUuTien, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; panel.add(new JLabel("Giá trị:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; panel.add(txtGiaTri, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; panel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; panel.add(new JScrollPane(txtMoTa), gbc);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa điểm cộng", true);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu"); JButton btnCancel = new JButton("Hủy");
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER); dialog.add(btnPanel, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                dc.setThiSinh((ThiSinh) cboThiSinh.getSelectedItem());
                dc.setLoaiUuTien(txtLoaiUuTien.getText().trim());
                dc.setGiaTri(Double.parseDouble(txtGiaTri.getText().trim()));
                dc.setMoTa(txtMoTa.getText().trim());
                dao.update(dc); loadData(); dialog.dispose();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.setVisible(true);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng!", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
        if (JOptionPane.showConfirmDialog(this, "Xóa điểm cộng này?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            DiemCong dc = dao.findById(id);
            if (dc != null) { dao.delete(dc); loadData(); JOptionPane.showMessageDialog(this, "Xóa thành công!"); }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi xóa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void importCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = chooser.getSelectedFile();
        int success = 0, fail = 0;
        try (CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String[] line; boolean first = true;
            while ((line = reader.readNext()) != null) {
                if (first) { first = false; continue; }
                if (line.length < 3) { fail++; continue; }
                try {
                    ThiSinh ts = thiSinhDAO.findByCccd(line[0].trim());
                    if (ts == null) { fail++; continue; }
                    DiemCong dc = new DiemCong();
                    dc.setThiSinh(ts);
                    dc.setLoaiUuTien(line[1].trim());
                    dc.setGiaTri(Double.parseDouble(line[2].trim()));
                    if (line.length > 3) dc.setMoTa(line[3].trim());
                    dao.save(dc); success++;
                } catch (Exception ex) { fail++; }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi đọc file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); return;
        }
        loadData();
        JOptionPane.showMessageDialog(this, "Import hoàn tất: " + success + " thành công, " + fail + " lỗi.");
    }
}
