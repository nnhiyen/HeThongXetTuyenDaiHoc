package com.hethong.ui.panels;

import com.hethong.dao.NganhDAO;
import com.hethong.dao.NguyenVongDAO;
import com.hethong.dao.ThiSinhDAO;
import com.hethong.dao.ToHopMonDAO;
import com.hethong.model.Nganh;
import com.hethong.model.NguyenVong;
import com.hethong.model.ThiSinh;
import com.hethong.model.ToHopMon;
import com.opencsv.CSVReader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.List;

public class NguyenVongPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private final NguyenVongDAO dao = new NguyenVongDAO();
    private final ThiSinhDAO thiSinhDAO = new ThiSinhDAO();
    private final NganhDAO nganhDAO = new NganhDAO();
    private final ToHopMonDAO toHopMonDAO = new ToHopMonDAO();
    private JComboBox<String> cboStatusFilter;

    private static final String[] COLUMNS = {"ID", "CCCD Thí sinh", "Họ tên", "Ngành", "Tổ hợp môn", "Thứ tự", "Điểm xét tuyển", "Trạng thái"};
    private static final String[] TRANG_THAI = {"Tất cả", "CHO_XET", "TRUNG_TUYEN", "KHONG_TRUNG"};

    public NguyenVongPanel() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Quản lý nguyện vọng", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(title, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        cboStatusFilter = new JComboBox<>(TRANG_THAI);
        JButton btnFilter = new JButton("Lọc");
        filterPanel.add(new JLabel("Lọc theo trạng thái:"));
        filterPanel.add(cboStatusFilter);
        filterPanel.add(btnFilter);
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
        btnRefresh.addActionListener(e -> { cboStatusFilter.setSelectedIndex(0); loadData(); });
        btnImportCsv.addActionListener(e -> importCsv());
        btnFilter.addActionListener(e -> loadData());
    }

    public void loadData() {
        tableModel.setRowCount(0);
        try {
            String filter = (String) cboStatusFilter.getSelectedItem();
            List<NguyenVong> list;
            if (filter == null || filter.equals("Tất cả")) {
                list = dao.findAll();
            } else {
                list = dao.findByTrangThai(filter);
            }
            for (NguyenVong nv : list) {
                String cccd = nv.getThiSinh() != null ? nv.getThiSinh().getCccd() : "";
                String hoTen = nv.getThiSinh() != null ? nv.getThiSinh().getHoTen() : "";
                String nganh = nv.getNganh() != null ? nv.getNganh().toString() : "";
                String toHop = nv.getToHopMon() != null ? nv.getToHopMon().toString() : "";
                tableModel.addRow(new Object[]{nv.getId(), cccd, hoTen, nganh, toHop, nv.getThuTu(), nv.getDiemXetTuyen(), nv.getTrangThai()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel buildForm(JComboBox<ThiSinh> cboThiSinh, JComboBox<Nganh> cboNganh,
                              JComboBox<ToHopMon> cboToHop, JTextField txtThuTu,
                              JTextField txtDiemXetTuyen, JComboBox<String> cboTrangThai) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addRow(panel, gbc, 0, "Thí sinh *:", cboThiSinh);
        addRow(panel, gbc, 1, "Ngành *:", cboNganh);
        addRow(panel, gbc, 2, "Tổ hợp môn *:", cboToHop);
        addRow(panel, gbc, 3, "Thứ tự:", txtThuTu);
        addRow(panel, gbc, 4, "Điểm xét tuyển:", txtDiemXetTuyen);
        addRow(panel, gbc, 5, "Trạng thái:", cboTrangThai);
        return panel;
    }

    private void showAddDialog() {
        List<ThiSinh> thiSinhList;
        List<Nganh> nganhList;
        List<ToHopMon> toHopList;
        try {
            thiSinhList = thiSinhDAO.findAll();
            nganhList = nganhDAO.findAll();
            toHopList = toHopMonDAO.findAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); return;
        }

        JComboBox<ThiSinh> cboThiSinh = new JComboBox<>(thiSinhList.toArray(new ThiSinh[0]));
        JComboBox<Nganh> cboNganh = new JComboBox<>(nganhList.toArray(new Nganh[0]));
        JComboBox<ToHopMon> cboToHop = new JComboBox<>(toHopList.toArray(new ToHopMon[0]));
        JTextField txtThuTu = new JTextField(5);
        JTextField txtDiemXetTuyen = new JTextField(10);
        JComboBox<String> cboTrangThai = new JComboBox<>(new String[]{"CHO_XET", "TRUNG_TUYEN", "KHONG_TRUNG"});

        JPanel panel = buildForm(cboThiSinh, cboNganh, cboToHop, txtThuTu, txtDiemXetTuyen, cboTrangThai);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm nguyện vọng", true);
        dialog.setSize(450, 380);
        dialog.setLocationRelativeTo(this);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu"); JButton btnCancel = new JButton("Hủy");
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER); dialog.add(btnPanel, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                NguyenVong nv = new NguyenVong();
                nv.setThiSinh((ThiSinh) cboThiSinh.getSelectedItem());
                nv.setNganh((Nganh) cboNganh.getSelectedItem());
                nv.setToHopMon((ToHopMon) cboToHop.getSelectedItem());
                nv.setThuTu(txtThuTu.getText().trim().isEmpty() ? null : Integer.parseInt(txtThuTu.getText().trim()));
                nv.setDiemXetTuyen(txtDiemXetTuyen.getText().trim().isEmpty() ? null : Double.parseDouble(txtDiemXetTuyen.getText().trim()));
                nv.setTrangThai((String) cboTrangThai.getSelectedItem());
                dao.save(nv); loadData(); dialog.dispose();
                JOptionPane.showMessageDialog(this, "Thêm nguyện vọng thành công!");
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
        NguyenVong nv;
        try { nv = dao.findById(id); } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); return;
        }
        if (nv == null) return;

        List<ThiSinh> thiSinhList;
        List<Nganh> nganhList;
        List<ToHopMon> toHopList;
        try {
            thiSinhList = thiSinhDAO.findAll();
            nganhList = nganhDAO.findAll();
            toHopList = toHopMonDAO.findAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); return;
        }

        JComboBox<ThiSinh> cboThiSinh = new JComboBox<>(thiSinhList.toArray(new ThiSinh[0]));
        JComboBox<Nganh> cboNganh = new JComboBox<>(nganhList.toArray(new Nganh[0]));
        JComboBox<ToHopMon> cboToHop = new JComboBox<>(toHopList.toArray(new ToHopMon[0]));
        if (nv.getThiSinh() != null) cboThiSinh.setSelectedItem(thiSinhList.stream().filter(ts -> ts.getId().equals(nv.getThiSinh().getId())).findFirst().orElse(null));
        if (nv.getNganh() != null) cboNganh.setSelectedItem(nganhList.stream().filter(n -> n.getId().equals(nv.getNganh().getId())).findFirst().orElse(null));
        if (nv.getToHopMon() != null) cboToHop.setSelectedItem(toHopList.stream().filter(t -> t.getId().equals(nv.getToHopMon().getId())).findFirst().orElse(null));
        JTextField txtThuTu = new JTextField(nv.getThuTu() != null ? nv.getThuTu().toString() : "", 5);
        JTextField txtDiemXetTuyen = new JTextField(nv.getDiemXetTuyen() != null ? nv.getDiemXetTuyen().toString() : "", 10);
        JComboBox<String> cboTrangThai = new JComboBox<>(new String[]{"CHO_XET", "TRUNG_TUYEN", "KHONG_TRUNG"});
        cboTrangThai.setSelectedItem(nv.getTrangThai());

        JPanel panel = buildForm(cboThiSinh, cboNganh, cboToHop, txtThuTu, txtDiemXetTuyen, cboTrangThai);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa nguyện vọng", true);
        dialog.setSize(450, 380);
        dialog.setLocationRelativeTo(this);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu"); JButton btnCancel = new JButton("Hủy");
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER); dialog.add(btnPanel, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                nv.setThiSinh((ThiSinh) cboThiSinh.getSelectedItem());
                nv.setNganh((Nganh) cboNganh.getSelectedItem());
                nv.setToHopMon((ToHopMon) cboToHop.getSelectedItem());
                nv.setThuTu(txtThuTu.getText().trim().isEmpty() ? null : Integer.parseInt(txtThuTu.getText().trim()));
                nv.setDiemXetTuyen(txtDiemXetTuyen.getText().trim().isEmpty() ? null : Double.parseDouble(txtDiemXetTuyen.getText().trim()));
                nv.setTrangThai((String) cboTrangThai.getSelectedItem());
                dao.update(nv); loadData(); dialog.dispose();
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
        if (JOptionPane.showConfirmDialog(this, "Xóa nguyện vọng này?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            NguyenVong nv = dao.findById(id);
            if (nv != null) { dao.delete(nv); loadData(); JOptionPane.showMessageDialog(this, "Xóa thành công!"); }
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
                    Nganh nganh = nganhDAO.findByMaNganh(line[1].trim());
                    ToHopMon toHop = toHopMonDAO.findByMaToHop(line[2].trim());
                    if (ts == null || nganh == null || toHop == null) { fail++; continue; }
                    NguyenVong nv = new NguyenVong();
                    nv.setThiSinh(ts);
                    nv.setNganh(nganh);
                    nv.setToHopMon(toHop);
                    if (line.length > 3 && !line[3].trim().isEmpty()) nv.setThuTu(Integer.parseInt(line[3].trim()));
                    if (line.length > 4 && !line[4].trim().isEmpty()) nv.setDiemXetTuyen(Double.parseDouble(line[4].trim()));
                    nv.setTrangThai(line.length > 5 && !line[5].trim().isEmpty() ? line[5].trim() : "CHO_XET");
                    dao.save(nv); success++;
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
