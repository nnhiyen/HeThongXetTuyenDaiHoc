package com.hethong.ui.panels;

import com.hethong.dao.ToHopMonDAO;
import com.hethong.model.ToHopMon;
import com.opencsv.CSVReader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.List;

public class ToHopMonPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private final ToHopMonDAO dao = new ToHopMonDAO();

    private static final String[] COLUMNS = {"ID", "Mã tổ hợp", "Tên tổ hợp", "Danh sách môn"};

    public ToHopMonPanel() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Quản lý tổ hợp môn", SwingConstants.LEFT);
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
            for (ToHopMon t : dao.findAll()) {
                tableModel.addRow(new Object[]{t.getId(), t.getMaToHop(), t.getTenToHop(), t.getDanhSachMon()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel buildForm(JTextField[] fields) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Mã tổ hợp *:", "Tên tổ hợp *:", "Danh sách môn:"};
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
        JTextField[] fields = new JTextField[3];
        JPanel panel = buildForm(fields);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm tổ hợp môn", true);
        dialog.setSize(420, 240);
        dialog.setLocationRelativeTo(this);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu"); JButton btnCancel = new JButton("Hủy");
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER); dialog.add(btnPanel, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            if (fields[0].getText().trim().isEmpty() || fields[1].getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Mã tổ hợp và Tên tổ hợp không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE); return;
            }
            try {
                ToHopMon t = new ToHopMon(fields[0].getText().trim(), fields[1].getText().trim(), fields[2].getText().trim());
                dao.save(t); loadData(); dialog.dispose();
                JOptionPane.showMessageDialog(this, "Thêm tổ hợp môn thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn tổ hợp môn!", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
        Long id = (Long) tableModel.getValueAt(row, 0);
        ToHopMon t;
        try { t = dao.findById(id); } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); return;
        }
        if (t == null) return;

        JTextField[] fields = new JTextField[3];
        JPanel panel = buildForm(fields);
        fields[0].setText(t.getMaToHop()); fields[0].setEditable(false);
        fields[1].setText(t.getTenToHop());
        fields[2].setText(t.getDanhSachMon() != null ? t.getDanhSachMon() : "");

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa tổ hợp môn", true);
        dialog.setSize(420, 240);
        dialog.setLocationRelativeTo(this);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu"); JButton btnCancel = new JButton("Hủy");
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER); dialog.add(btnPanel, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                t.setTenToHop(fields[1].getText().trim());
                t.setDanhSachMon(fields[2].getText().trim());
                dao.update(t); loadData(); dialog.dispose();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.setVisible(true);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn tổ hợp môn!", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
        if (JOptionPane.showConfirmDialog(this, "Xóa tổ hợp môn này?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            ToHopMon t = dao.findById(id);
            if (t != null) { dao.delete(t); loadData(); JOptionPane.showMessageDialog(this, "Xóa thành công!"); }
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
                    ToHopMon t = new ToHopMon(line[0].trim(), line[1].trim(), line.length > 2 ? line[2].trim() : "");
                    dao.save(t); success++;
                } catch (Exception ex) { fail++; }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi đọc file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); return;
        }
        loadData();
        JOptionPane.showMessageDialog(this, "Import hoàn tất: " + success + " thành công, " + fail + " lỗi.");
    }
}
