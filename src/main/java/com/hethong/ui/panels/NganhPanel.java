package com.hethong.ui.panels;

import com.hethong.dao.NganhDAO;
import com.hethong.model.Nganh;
import com.opencsv.CSVReader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.List;

public class NganhPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private final NganhDAO dao = new NganhDAO();

    private static final String[] COLUMNS = {"ID", "Mã ngành", "Tên ngành", "Mô tả", "Chỉ tiêu", "Điểm sàn lọc"};

    public NganhPanel() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Quản lý ngành", SwingConstants.LEFT);
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
            for (Nganh n : dao.findAll()) {
                tableModel.addRow(new Object[]{n.getId(), n.getMaNganh(), n.getTenNganh(),
                        n.getMoTa(), n.getChiTieuTuyen(), n.getDiemSanLoc()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JDialog buildDialog(String title, JTextField[] fields) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Mã ngành *:", "Tên ngành *:", "Mô tả:", "Chỉ tiêu tuyển:", "Điểm sàn lọc:"};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 1.0;
            fields[i] = i == 2 ? new JTextField(25) : new JTextField(20);
            panel.add(fields[i], gbc);
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setSize(420, 310);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        return dialog;
    }

    private void showAddDialog() {
        JTextField[] fields = new JTextField[5];
        JDialog dialog = buildDialog("Thêm ngành", fields);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu"); JButton btnCancel = new JButton("Hủy");
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            if (fields[0].getText().trim().isEmpty() || fields[1].getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Mã ngành và Tên ngành không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE); return;
            }
            try {
                Nganh n = new Nganh();
                n.setMaNganh(fields[0].getText().trim());
                n.setTenNganh(fields[1].getText().trim());
                n.setMoTa(fields[2].getText().trim());
                n.setChiTieuTuyen(fields[3].getText().trim().isEmpty() ? null : Integer.parseInt(fields[3].getText().trim()));
                n.setDiemSanLoc(fields[4].getText().trim().isEmpty() ? null : Double.parseDouble(fields[4].getText().trim()));
                dao.save(n); loadData(); dialog.dispose();
                JOptionPane.showMessageDialog(this, "Thêm ngành thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn ngành!", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
        Long id = (Long) tableModel.getValueAt(row, 0);
        Nganh n;
        try { n = dao.findById(id); } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); return;
        }
        if (n == null) return;

        JTextField[] fields = new JTextField[5];
        JDialog dialog = buildDialog("Sửa ngành", fields);
        fields[0].setText(n.getMaNganh()); fields[0].setEditable(false);
        fields[1].setText(n.getTenNganh());
        fields[2].setText(n.getMoTa() != null ? n.getMoTa() : "");
        fields[3].setText(n.getChiTieuTuyen() != null ? n.getChiTieuTuyen().toString() : "");
        fields[4].setText(n.getDiemSanLoc() != null ? n.getDiemSanLoc().toString() : "");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu"); JButton btnCancel = new JButton("Hủy");
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                n.setTenNganh(fields[1].getText().trim());
                n.setMoTa(fields[2].getText().trim());
                n.setChiTieuTuyen(fields[3].getText().trim().isEmpty() ? null : Integer.parseInt(fields[3].getText().trim()));
                n.setDiemSanLoc(fields[4].getText().trim().isEmpty() ? null : Double.parseDouble(fields[4].getText().trim()));
                dao.update(n); loadData(); dialog.dispose();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.setVisible(true);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn ngành!", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
        if (JOptionPane.showConfirmDialog(this, "Xóa ngành này?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            Nganh n = dao.findById(id);
            if (n != null) { dao.delete(n); loadData(); JOptionPane.showMessageDialog(this, "Xóa thành công!"); }
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
                if (line.length < 2) { fail++; continue; }
                try {
                    Nganh n = new Nganh();
                    n.setMaNganh(line[0].trim());
                    n.setTenNganh(line[1].trim());
                    if (line.length > 2) n.setMoTa(line[2].trim());
                    if (line.length > 3 && !line[3].trim().isEmpty()) n.setChiTieuTuyen(Integer.parseInt(line[3].trim()));
                    if (line.length > 4 && !line[4].trim().isEmpty()) n.setDiemSanLoc(Double.parseDouble(line[4].trim()));
                    dao.save(n); success++;
                } catch (Exception ex) { fail++; }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi đọc file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); return;
        }
        loadData();
        JOptionPane.showMessageDialog(this, "Import hoàn tất: " + success + " thành công, " + fail + " lỗi.");
    }
}
