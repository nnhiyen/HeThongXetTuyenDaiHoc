package com.hethong.ui.panels;

import com.hethong.dao.BangQuyDoiDAO;
import com.hethong.model.BangQuyDoi;
import com.opencsv.CSVReader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.List;

public class BangQuyDoiPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private final BangQuyDoiDAO dao = new BangQuyDoiDAO();
    private JTextField txtSearch;

    private static final String[] COLUMNS = {"ID", "Loại", "Giá trị", "Điểm quy đổi", "Mô tả"};

    public BangQuyDoiPanel() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Bảng quy đổi", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(title, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        txtSearch = new JTextField(25);
        JButton btnSearch = new JButton("Tìm kiếm");
        JButton btnClear = new JButton("Xóa bộ lọc");
        searchPanel.add(new JLabel("Tìm kiếm (Loại/Mô tả):"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnClear);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

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
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); loadData(); });
        btnImportCsv.addActionListener(e -> importCsv());
        btnSearch.addActionListener(e -> performSearch());
        btnClear.addActionListener(e -> { txtSearch.setText(""); loadData(); });
        txtSearch.addActionListener(e -> performSearch());
    }

    public void loadData() {
        tableModel.setRowCount(0);
        try {
            for (BangQuyDoi b : dao.findAll()) {
                tableModel.addRow(new Object[]{b.getId(), b.getLoai(), b.getGiaTri(), b.getDiemQuyDoi(), b.getMoTa()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch() {
        String kw = txtSearch.getText().trim();
        if (kw.isEmpty()) { loadData(); return; }
        tableModel.setRowCount(0);
        try {
            for (BangQuyDoi b : dao.searchByLoaiOrMoTa(kw)) {
                tableModel.addRow(new Object[]{b.getId(), b.getLoai(), b.getGiaTri(), b.getDiemQuyDoi(), b.getMoTa()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel buildForm(JTextField[] fields) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Loại *:", "Giá trị:", "Điểm quy đổi:", "Mô tả:"};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 1.0;
            fields[i] = new JTextField(22);
            panel.add(fields[i], gbc);
        }
        return panel;
    }

    private void showAddDialog() {
        JTextField[] fields = new JTextField[4];
        JPanel panel = buildForm(fields);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm bảng quy đổi", true);
        dialog.setSize(420, 280);
        dialog.setLocationRelativeTo(this);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu"); JButton btnCancel = new JButton("Hủy");
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER); dialog.add(btnPanel, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            if (fields[0].getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Loại không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE); return;
            }
            try {
                BangQuyDoi b = new BangQuyDoi();
                b.setLoai(fields[0].getText().trim());
                b.setGiaTri(fields[1].getText().trim().isEmpty() ? null : Double.parseDouble(fields[1].getText().trim()));
                b.setDiemQuyDoi(fields[2].getText().trim().isEmpty() ? null : Double.parseDouble(fields[2].getText().trim()));
                b.setMoTa(fields[3].getText().trim());
                dao.save(b); loadData(); dialog.dispose();
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
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
        BangQuyDoi b;
        try { b = dao.findById(id); } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); return;
        }
        if (b == null) return;

        JTextField[] fields = new JTextField[4];
        JPanel panel = buildForm(fields);
        fields[0].setText(b.getLoai() != null ? b.getLoai() : "");
        fields[1].setText(b.getGiaTri() != null ? b.getGiaTri().toString() : "");
        fields[2].setText(b.getDiemQuyDoi() != null ? b.getDiemQuyDoi().toString() : "");
        fields[3].setText(b.getMoTa() != null ? b.getMoTa() : "");

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa bảng quy đổi", true);
        dialog.setSize(420, 280);
        dialog.setLocationRelativeTo(this);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu"); JButton btnCancel = new JButton("Hủy");
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER); dialog.add(btnPanel, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                b.setLoai(fields[0].getText().trim());
                b.setGiaTri(fields[1].getText().trim().isEmpty() ? null : Double.parseDouble(fields[1].getText().trim()));
                b.setDiemQuyDoi(fields[2].getText().trim().isEmpty() ? null : Double.parseDouble(fields[2].getText().trim()));
                b.setMoTa(fields[3].getText().trim());
                dao.update(b); loadData(); dialog.dispose();
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
        if (JOptionPane.showConfirmDialog(this, "Xóa bản ghi này?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            BangQuyDoi b = dao.findById(id);
            if (b != null) { dao.delete(b); loadData(); JOptionPane.showMessageDialog(this, "Xóa thành công!"); }
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
                if (line.length < 1) { fail++; continue; }
                try {
                    BangQuyDoi b = new BangQuyDoi();
                    b.setLoai(line[0].trim());
                    if (line.length > 1 && !line[1].trim().isEmpty()) b.setGiaTri(Double.parseDouble(line[1].trim()));
                    if (line.length > 2 && !line[2].trim().isEmpty()) b.setDiemQuyDoi(Double.parseDouble(line[2].trim()));
                    if (line.length > 3) b.setMoTa(line[3].trim());
                    dao.save(b); success++;
                } catch (Exception ex) { fail++; }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi đọc file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); return;
        }
        loadData();
        JOptionPane.showMessageDialog(this, "Import hoàn tất: " + success + " thành công, " + fail + " lỗi.");
    }
}
