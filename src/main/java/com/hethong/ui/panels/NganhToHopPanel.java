package com.hethong.ui.panels;

import com.hethong.dao.NganhDAO;
import com.hethong.dao.NganhToHopDAO;
import com.hethong.dao.ToHopMonDAO;
import com.hethong.model.Nganh;
import com.hethong.model.NganhToHop;
import com.hethong.model.ToHopMon;
import com.opencsv.CSVReader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.List;

public class NganhToHopPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private final NganhToHopDAO dao = new NganhToHopDAO();
    private final NganhDAO nganhDAO = new NganhDAO();
    private final ToHopMonDAO toHopMonDAO = new ToHopMonDAO();

    private static final String[] COLUMNS = {"ID", "Ngành", "Tổ hợp môn"};

    public NganhToHopPanel() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Ngành - Tổ hợp môn", SwingConstants.LEFT);
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
            for (NganhToHop nth : dao.findAll()) {
                String nganhName = nth.getNganh() != null ? nth.getNganh().toString() : "";
                String toHopName = nth.getToHopMon() != null ? nth.getToHopMon().toString() : "";
                tableModel.addRow(new Object[]{nth.getId(), nganhName, toHopName});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddDialog() {
        List<Nganh> nganhList;
        List<ToHopMon> toHopList;
        try {
            nganhList = nganhDAO.findAll();
            toHopList = toHopMonDAO.findAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); return;
        }

        JComboBox<Nganh> cboNganh = new JComboBox<>(nganhList.toArray(new Nganh[0]));
        JComboBox<ToHopMon> cboToHop = new JComboBox<>(toHopList.toArray(new ToHopMon[0]));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Ngành:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; panel.add(cboNganh, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; panel.add(new JLabel("Tổ hợp môn:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; panel.add(cboToHop, gbc);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm Ngành - Tổ hợp môn", true);
        dialog.setSize(420, 200);
        dialog.setLocationRelativeTo(this);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu"); JButton btnCancel = new JButton("Hủy");
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER); dialog.add(btnPanel, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            Nganh nganh = (Nganh) cboNganh.getSelectedItem();
            ToHopMon toHop = (ToHopMon) cboToHop.getSelectedItem();
            if (nganh == null || toHop == null) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn đầy đủ!", "Lỗi", JOptionPane.ERROR_MESSAGE); return;
            }
            try {
                NganhToHop nth = new NganhToHop(nganh, toHop);
                dao.save(nth); loadData(); dialog.dispose();
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
        NganhToHop nth;
        try { nth = dao.findById(id); } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); return;
        }
        if (nth == null) return;

        List<Nganh> nganhList;
        List<ToHopMon> toHopList;
        try {
            nganhList = nganhDAO.findAll();
            toHopList = toHopMonDAO.findAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); return;
        }

        JComboBox<Nganh> cboNganh = new JComboBox<>(nganhList.toArray(new Nganh[0]));
        JComboBox<ToHopMon> cboToHop = new JComboBox<>(toHopList.toArray(new ToHopMon[0]));
        if (nth.getNganh() != null) cboNganh.setSelectedItem(nganhList.stream().filter(n -> n.getId().equals(nth.getNganh().getId())).findFirst().orElse(null));
        if (nth.getToHopMon() != null) cboToHop.setSelectedItem(toHopList.stream().filter(t -> t.getId().equals(nth.getToHopMon().getId())).findFirst().orElse(null));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Ngành:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; panel.add(cboNganh, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; panel.add(new JLabel("Tổ hợp môn:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; panel.add(cboToHop, gbc);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa Ngành - Tổ hợp môn", true);
        dialog.setSize(420, 200);
        dialog.setLocationRelativeTo(this);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu"); JButton btnCancel = new JButton("Hủy");
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER); dialog.add(btnPanel, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                nth.setNganh((Nganh) cboNganh.getSelectedItem());
                nth.setToHopMon((ToHopMon) cboToHop.getSelectedItem());
                dao.update(nth); loadData(); dialog.dispose();
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
        if (JOptionPane.showConfirmDialog(this, "Xóa liên kết này?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            NganhToHop nth = dao.findById(id);
            if (nth != null) { dao.delete(nth); loadData(); JOptionPane.showMessageDialog(this, "Xóa thành công!"); }
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
                    Nganh nganh = nganhDAO.findByMaNganh(line[0].trim());
                    ToHopMon toHop = toHopMonDAO.findByMaToHop(line[1].trim());
                    if (nganh == null || toHop == null) { fail++; continue; }
                    NganhToHop nth = new NganhToHop(nganh, toHop);
                    dao.save(nth); success++;
                } catch (Exception ex) { fail++; }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi đọc file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); return;
        }
        loadData();
        JOptionPane.showMessageDialog(this, "Import hoàn tất: " + success + " thành công, " + fail + " lỗi.");
    }
}
