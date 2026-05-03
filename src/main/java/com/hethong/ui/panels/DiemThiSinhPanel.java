package com.hethong.ui.panels;

import com.hethong.dao.DiemThiSinhDAO;
import com.hethong.dao.ThiSinhDAO;
import com.hethong.model.DiemThiSinh;
import com.hethong.model.ThiSinh;
import com.opencsv.CSVReader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class DiemThiSinhPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private final DiemThiSinhDAO dao = new DiemThiSinhDAO();
    private final ThiSinhDAO thiSinhDAO = new ThiSinhDAO();
    private JComboBox<String> cboFilter;

    private static final String[] COLUMNS = {"ID", "CCCD Thí sinh", "Họ tên", "Loại điểm", "Môn", "Điểm", "Năm"};
    private static final String[] LOAI_DIEM = {"Tất cả", "THPT", "VSAT", "DGNL"};

    public DiemThiSinhPanel() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Quản lý điểm thí sinh", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(title, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        cboFilter = new JComboBox<>(LOAI_DIEM);
        JButton btnFilter = new JButton("Lọc");
        JButton btnStats = new JButton("Thống kê");
        filterPanel.add(new JLabel("Lọc theo loại điểm:"));
        filterPanel.add(cboFilter);
        filterPanel.add(btnFilter);
        filterPanel.add(btnStats);
        topPanel.add(filterPanel, BorderLayout.CENTER);
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
        btnRefresh.addActionListener(e -> { cboFilter.setSelectedIndex(0); loadData(); });
        btnImportCsv.addActionListener(e -> importCsv());
        btnFilter.addActionListener(e -> loadData());
        btnStats.addActionListener(e -> showStats());
    }

    public void loadData() {
        tableModel.setRowCount(0);
        try {
            String filter = (String) cboFilter.getSelectedItem();
            List<DiemThiSinh> list;
            if (filter == null || filter.equals("Tất cả")) {
                list = dao.findAll();
            } else {
                list = dao.findByLoaiDiem(filter);
            }
            for (DiemThiSinh d : list) {
                String cccd = d.getThiSinh() != null ? d.getThiSinh().getCccd() : "";
                String hoTen = d.getThiSinh() != null ? d.getThiSinh().getHoTen() : "";
                tableModel.addRow(new Object[]{d.getId(), cccd, hoTen, d.getLoaiDiem(), d.getMon(), d.getDiem(), d.getNam()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showStats() {
        try {
            List<DiemThiSinh> all = dao.findAll();
            Map<String, DoubleSummaryStatistics> stats = all.stream()
                    .filter(d -> d.getLoaiDiem() != null && d.getDiem() != null)
                    .collect(Collectors.groupingBy(DiemThiSinh::getLoaiDiem,
                            Collectors.summarizingDouble(d -> d.getDiem())));

            StringBuilder sb = new StringBuilder("Thống kê điểm trung bình theo loại:\n\n");
            stats.forEach((loai, s) -> sb.append(String.format("%-10s: TB=%.2f, Min=%.2f, Max=%.2f, Số lượng=%d%n",
                    loai, s.getAverage(), s.getMin(), s.getMax(), s.getCount())));

            JTextArea ta = new JTextArea(sb.toString());
            ta.setEditable(false);
            ta.setFont(new Font("Monospaced", Font.PLAIN, 13));
            JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Thống kê điểm", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi thống kê: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddDialog() {
        List<ThiSinh> thiSinhList;
        try { thiSinhList = thiSinhDAO.findAll(); } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách thí sinh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); return;
        }

        JComboBox<ThiSinh> cboThiSinh = new JComboBox<>(thiSinhList.toArray(new ThiSinh[0]));
        JComboBox<String> cboLoaiDiem = new JComboBox<>(new String[]{"THPT", "VSAT", "DGNL"});
        JTextField txtMon = new JTextField(20);
        JTextField txtDiem = new JTextField(10);
        JTextField txtNam = new JTextField(10);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addRow(panel, gbc, 0, "Thí sinh *:", cboThiSinh);
        addRow(panel, gbc, 1, "Loại điểm *:", cboLoaiDiem);
        addRow(panel, gbc, 2, "Môn *:", txtMon);
        addRow(panel, gbc, 3, "Điểm *:", txtDiem);
        addRow(panel, gbc, 4, "Năm:", txtNam);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm điểm thí sinh", true);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu"); JButton btnCancel = new JButton("Hủy");
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER); dialog.add(btnPanel, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            if (txtMon.getText().trim().isEmpty() || txtDiem.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Môn và Điểm không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE); return;
            }
            try {
                DiemThiSinh d = new DiemThiSinh();
                d.setThiSinh((ThiSinh) cboThiSinh.getSelectedItem());
                d.setLoaiDiem((String) cboLoaiDiem.getSelectedItem());
                d.setMon(txtMon.getText().trim());
                d.setDiem(Double.parseDouble(txtDiem.getText().trim()));
                d.setNam(txtNam.getText().trim().isEmpty() ? null : Integer.parseInt(txtNam.getText().trim()));
                dao.save(d); loadData(); dialog.dispose();
                JOptionPane.showMessageDialog(this, "Thêm điểm thành công!");
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
        DiemThiSinh d;
        try { d = dao.findById(id); } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); return;
        }
        if (d == null) return;

        List<ThiSinh> thiSinhList;
        try { thiSinhList = thiSinhDAO.findAll(); } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); return;
        }

        JComboBox<ThiSinh> cboThiSinh = new JComboBox<>(thiSinhList.toArray(new ThiSinh[0]));
        if (d.getThiSinh() != null) cboThiSinh.setSelectedItem(thiSinhList.stream().filter(ts -> ts.getId().equals(d.getThiSinh().getId())).findFirst().orElse(null));
        JComboBox<String> cboLoaiDiem = new JComboBox<>(new String[]{"THPT", "VSAT", "DGNL"});
        cboLoaiDiem.setSelectedItem(d.getLoaiDiem());
        JTextField txtMon = new JTextField(d.getMon() != null ? d.getMon() : "", 20);
        JTextField txtDiem = new JTextField(d.getDiem() != null ? d.getDiem().toString() : "", 10);
        JTextField txtNam = new JTextField(d.getNam() != null ? d.getNam().toString() : "", 10);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addRow(panel, gbc, 0, "Thí sinh:", cboThiSinh);
        addRow(panel, gbc, 1, "Loại điểm:", cboLoaiDiem);
        addRow(panel, gbc, 2, "Môn:", txtMon);
        addRow(panel, gbc, 3, "Điểm:", txtDiem);
        addRow(panel, gbc, 4, "Năm:", txtNam);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa điểm thí sinh", true);
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
                d.setThiSinh((ThiSinh) cboThiSinh.getSelectedItem());
                d.setLoaiDiem((String) cboLoaiDiem.getSelectedItem());
                d.setMon(txtMon.getText().trim());
                d.setDiem(Double.parseDouble(txtDiem.getText().trim()));
                d.setNam(txtNam.getText().trim().isEmpty() ? null : Integer.parseInt(txtNam.getText().trim()));
                dao.update(d); loadData(); dialog.dispose();
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
        if (JOptionPane.showConfirmDialog(this, "Xóa điểm này?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            DiemThiSinh d = dao.findById(id);
            if (d != null) { dao.delete(d); loadData(); JOptionPane.showMessageDialog(this, "Xóa thành công!"); }
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
                if (line.length < 4) { fail++; continue; }
                try {
                    ThiSinh ts = thiSinhDAO.findByCccd(line[0].trim());
                    if (ts == null) { fail++; continue; }
                    DiemThiSinh d = new DiemThiSinh();
                    d.setThiSinh(ts);
                    d.setLoaiDiem(line[1].trim());
                    d.setMon(line[2].trim());
                    d.setDiem(Double.parseDouble(line[3].trim()));
                    if (line.length > 4 && !line[4].trim().isEmpty()) d.setNam(Integer.parseInt(line[4].trim()));
                    dao.save(d); success++;
                } catch (Exception ex) { fail++; }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi đọc file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); return;
        }
        loadData();
        JOptionPane.showMessageDialog(this, "Import hoàn tất: " + success + " thành công, " + fail + " lỗi.");
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, Component comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(comp, gbc);
    }
}
