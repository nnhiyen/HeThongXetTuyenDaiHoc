package com.hethong.ui.panels;

import com.hethong.dao.ThiSinhDAO;
import com.hethong.model.ThiSinh;
import com.opencsv.CSVReader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ThiSinhPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private final ThiSinhDAO dao = new ThiSinhDAO();

    private JTextField txtSearch;
    private JLabel lblPage;
    private int currentPage = 1;
    private static final int PAGE_SIZE = 20;
    private boolean searchMode = false;

    private static final String[] COLUMNS = {"ID", "CCCD", "Họ tên", "Ngày sinh", "Giới tính", "Địa chỉ", "SĐT", "Email", "Trường THPT", "Năm TN"};

    public ThiSinhPanel() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Quản lý thí sinh", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 16));

        // Search bar
        JPanel topPanel = new JPanel(new BorderLayout(5, 0));
        topPanel.add(title, BorderLayout.NORTH);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        txtSearch = new JTextField(25);
        JButton btnSearch = new JButton("Tìm kiếm");
        JButton btnClear = new JButton("Xóa bộ lọc");
        searchPanel.add(new JLabel("Tìm kiếm (CCCD/Tên):"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnClear);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom panel: buttons + pagination
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnRefresh = new JButton("Làm mới");
        JButton btnImportCsv = new JButton("Import CSV");
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);
        btnPanel.add(btnImportCsv);

        JPanel pagePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        JButton btnPrev = new JButton("◀ Trước");
        lblPage = new JLabel("Trang 1");
        JButton btnNext = new JButton("Tiếp ▶");
        pagePanel.add(btnPrev);
        pagePanel.add(lblPage);
        pagePanel.add(btnNext);

        bottomPanel.add(btnPanel, BorderLayout.WEST);
        bottomPanel.add(pagePanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> { searchMode = false; txtSearch.setText(""); currentPage = 1; loadData(); });
        btnImportCsv.addActionListener(e -> importCsv());
        btnSearch.addActionListener(e -> performSearch());
        btnClear.addActionListener(e -> { searchMode = false; txtSearch.setText(""); currentPage = 1; loadData(); });
        txtSearch.addActionListener(e -> performSearch());
        btnPrev.addActionListener(e -> { if (currentPage > 1) { currentPage--; loadData(); } });
        btnNext.addActionListener(e -> { currentPage++; loadData(); });
    }

    public void loadData() {
        tableModel.setRowCount(0);
        try {
            List<ThiSinh> list;
            if (searchMode) {
                list = dao.searchByNameOrCccd(txtSearch.getText().trim());
                lblPage.setText("Kết quả: " + list.size());
            } else {
                long total = dao.countAll();
                int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);
                if (totalPages == 0) totalPages = 1;
                if (currentPage > totalPages) currentPage = totalPages;
                list = dao.findPaginated(currentPage, PAGE_SIZE);
                lblPage.setText("Trang " + currentPage + "/" + totalPages + " (Tổng: " + total + ")");
            }
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (ThiSinh ts : list) {
                tableModel.addRow(new Object[]{
                        ts.getId(), ts.getCccd(), ts.getHoTen(),
                        ts.getNgaySinh() != null ? ts.getNgaySinh().format(fmt) : "",
                        ts.getGioiTinh(), ts.getDiaChi(), ts.getSoDienThoai(),
                        ts.getEmail(), ts.getTruongThptTotNghiep(),
                        ts.getNamTotNghiep() != null ? ts.getNamTotNghiep() : ""
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch() {
        String kw = txtSearch.getText().trim();
        if (kw.isEmpty()) { searchMode = false; currentPage = 1; loadData(); return; }
        searchMode = true;
        loadData();
    }

    private JPanel buildThiSinhForm(JTextField[] fields) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"CCCD *:", "Họ tên *:", "Ngày sinh (yyyy-MM-dd):", "Giới tính:", "Địa chỉ:", "SĐT:", "Email:", "Trường THPT:", "Năm tốt nghiệp:"};
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
        JTextField[] fields = new JTextField[9];
        JPanel panel = buildThiSinhForm(fields);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm thí sinh", true);
        dialog.setSize(450, 430);
        dialog.setLocationRelativeTo(this);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");
        btnPanel.add(btnSave); btnPanel.add(btnCancel);

        dialog.setLayout(new BorderLayout());
        dialog.add(new JScrollPane(panel), BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            if (fields[0].getText().trim().isEmpty() || fields[1].getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "CCCD và Họ tên không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                ThiSinh ts = new ThiSinh();
                ts.setCccd(fields[0].getText().trim());
                ts.setHoTen(fields[1].getText().trim());
                if (!fields[2].getText().trim().isEmpty()) {
                    try {
                        ts.setNgaySinh(LocalDate.parse(fields[2].getText().trim()));
                    } catch (DateTimeParseException dtpe) {
                        JOptionPane.showMessageDialog(dialog, "Ngày sinh không hợp lệ. Vui lòng nhập theo định dạng yyyy-MM-dd (ví dụ: 2005-03-15)", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                ts.setGioiTinh(fields[3].getText().trim());
                ts.setDiaChi(fields[4].getText().trim());
                ts.setSoDienThoai(fields[5].getText().trim());
                ts.setEmail(fields[6].getText().trim());
                ts.setTruongThptTotNghiep(fields[7].getText().trim());
                if (!fields[8].getText().trim().isEmpty()) {
                    try {
                        ts.setNamTotNghiep(Integer.parseInt(fields[8].getText().trim()));
                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(dialog, "Năm tốt nghiệp không hợp lệ. Vui lòng nhập một số nguyên (ví dụ: 2023)", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                dao.save(ts);
                loadData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Thêm thí sinh thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn thí sinh!", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
        Long id = (Long) tableModel.getValueAt(row, 0);
        ThiSinh ts;
        try { ts = dao.findById(id); } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); return;
        }
        if (ts == null) return;

        JTextField[] fields = new JTextField[9];
        JPanel panel = buildThiSinhForm(fields);
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        fields[0].setText(ts.getCccd()); fields[0].setEditable(false);
        fields[1].setText(ts.getHoTen());
        fields[2].setText(ts.getNgaySinh() != null ? ts.getNgaySinh().format(fmt) : "");
        fields[3].setText(ts.getGioiTinh() != null ? ts.getGioiTinh() : "");
        fields[4].setText(ts.getDiaChi() != null ? ts.getDiaChi() : "");
        fields[5].setText(ts.getSoDienThoai() != null ? ts.getSoDienThoai() : "");
        fields[6].setText(ts.getEmail() != null ? ts.getEmail() : "");
        fields[7].setText(ts.getTruongThptTotNghiep() != null ? ts.getTruongThptTotNghiep() : "");
        fields[8].setText(ts.getNamTotNghiep() != null ? ts.getNamTotNghiep().toString() : "");

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa thí sinh", true);
        dialog.setSize(450, 430);
        dialog.setLocationRelativeTo(this);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");
        btnPanel.add(btnSave); btnPanel.add(btnCancel);

        dialog.setLayout(new BorderLayout());
        dialog.add(new JScrollPane(panel), BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                ts.setHoTen(fields[1].getText().trim());
                if (!fields[2].getText().trim().isEmpty()) {
                    try {
                        ts.setNgaySinh(LocalDate.parse(fields[2].getText().trim()));
                    } catch (DateTimeParseException dtpe) {
                        JOptionPane.showMessageDialog(dialog, "Ngày sinh không hợp lệ. Vui lòng nhập theo định dạng yyyy-MM-dd (ví dụ: 2005-03-15)", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    ts.setNgaySinh(null);
                }
                ts.setGioiTinh(fields[3].getText().trim());
                ts.setDiaChi(fields[4].getText().trim());
                ts.setSoDienThoai(fields[5].getText().trim());
                ts.setEmail(fields[6].getText().trim());
                ts.setTruongThptTotNghiep(fields[7].getText().trim());
                if (!fields[8].getText().trim().isEmpty()) {
                    try {
                        ts.setNamTotNghiep(Integer.parseInt(fields[8].getText().trim()));
                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(dialog, "Năm tốt nghiệp không hợp lệ. Vui lòng nhập một số nguyên (ví dụ: 2023)", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    ts.setNamTotNghiep(null);
                }
                dao.update(ts);
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
        if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn thí sinh!", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa thí sinh này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            ThiSinh ts = dao.findById(id);
            if (ts != null) { dao.delete(ts); loadData(); JOptionPane.showMessageDialog(this, "Xóa thành công!"); }
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
                    ThiSinh ts = new ThiSinh();
                    ts.setCccd(line[0].trim());
                    ts.setHoTen(line[1].trim());
                    if (line.length > 2 && !line[2].trim().isEmpty()) ts.setNgaySinh(LocalDate.parse(line[2].trim()));
                    if (line.length > 3) ts.setGioiTinh(line[3].trim());
                    if (line.length > 4) ts.setDiaChi(line[4].trim());
                    if (line.length > 5) ts.setSoDienThoai(line[5].trim());
                    if (line.length > 6) ts.setEmail(line[6].trim());
                    if (line.length > 7) ts.setTruongThptTotNghiep(line[7].trim());
                    if (line.length > 8 && !line[8].trim().isEmpty()) ts.setNamTotNghiep(Integer.parseInt(line[8].trim()));
                    dao.save(ts);
                    success++;
                } catch (Exception ex) { fail++; }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi đọc file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        loadData();
        JOptionPane.showMessageDialog(this, "Import hoàn tất: " + success + " thành công, " + fail + " lỗi.");
    }
}
