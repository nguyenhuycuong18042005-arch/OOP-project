package com.project.oop.lms.service;

import com.google.gson.reflect.TypeToken;
import com.project.oop.lms.model.DocGia;
import com.project.oop.lms.model.PhieuMuon;
import com.project.oop.lms.model.SachVatLy;
import com.project.oop.lms.repository.JsonDataManager;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class MuonTraService implements IGeneralService<PhieuMuon> {
    private List<PhieuMuon> phieuMuonList;
    private final JsonDataManager<PhieuMuon> repo;
    private static final String FILE_PATH = "phieu_muon.json";

    // Dependencies
    private final SachService sachService;
    private final DocGiaService docGiaService;

    public MuonTraService(SachService sachService, DocGiaService docGiaService) {
        this.repo = new JsonDataManager<>();
        this.sachService = sachService;
        this.docGiaService = docGiaService;
        loadData();
    }

    private void loadData() {
        Type type = new TypeToken<List<PhieuMuon>>() {
        }.getType();
        this.phieuMuonList = repo.loadFromFile(FILE_PATH, type);
    }

    private void saveData() {
        repo.saveToFile(phieuMuonList, FILE_PATH);
    }

    @Override
    public List<PhieuMuon> getAll() {
        return phieuMuonList;
    }

    @Override
    public void add(PhieuMuon t) {
        phieuMuonList.add(t);
        saveData();
    }

    @Override
    public PhieuMuon findById(String id) {
        return phieuMuonList.stream()
                .filter(p -> p.getMaPhieu().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void update(PhieuMuon t) {
        for (int i = 0; i < phieuMuonList.size(); i++) {
            if (phieuMuonList.get(i).getMaPhieu().equals(t.getMaPhieu())) {
                phieuMuonList.set(i, t);
                saveData();
                return;
            }
        }
    }

    @Override
    public void delete(String id) {
        phieuMuonList.removeIf(p -> p.getMaPhieu().equals(id));
        saveData();
    }

    public String muonSach(String docGiaId, List<String> danhSachMaVach) {
        if (danhSachMaVach == null || danhSachMaVach.isEmpty())
            return "Danh sach ma vach trong.";

        // 1. Kiểm tra DocGia
        DocGia docGia = docGiaService.findById(docGiaId);
        if (docGia == null)
            return "Doc gia khong ton tai.";
        if (!docGia.kiemTraHanThe())
            return "The doc gia da het han.";

        // Check Limit Max 3
        if (docGia.getSoSachDangMuon() + danhSachMaVach.size() > DocGia.SO_SACH_DANG_MUON_MAX) {
            return "Khong the muon them. Tong so sach muon se vuot qua " + DocGia.SO_SACH_DANG_MUON_MAX
                    + " (Hien tai: " + docGia.getSoSachDangMuon()
                    + ", Muon them: " + danhSachMaVach.size() + ")";
        }

        // 2. Kiểm tra Sach
        List<SachVatLy> booksToBorrow = new java.util.ArrayList<>();
        for (String maVach : danhSachMaVach) {
            SachVatLy sach = sachService.findSachVatLyByMaVach(maVach.trim());
            if (sach == null)
                return "Ma vach sach " + maVach + " khong ton tai.";
            if (!SachVatLy.TRANG_THAI_SAN_SANG.equals(sach.getTrangThai())) {
                return "Sach " + maVach + " khong o trang thai san sang (" + sach.getTrangThai() + ").";
            }
            booksToBorrow.add(sach);
        }

        // 3. Tạo PhieuMuon
        PhieuMuon phieu = new PhieuMuon();
        phieu.setMaPhieu(UUID.randomUUID().toString().substring(0, 8));
        phieu.setIdDocGia(docGiaId);
        phieu.setSachVatLy(booksToBorrow);
        phieu.setNgayMuon(LocalDate.now());
        phieu.setHanTra(LocalDate.now().plusDays(7)); // 7 days loan

        // 4. Cập nhật trạng thái
        add(phieu);

        for (SachVatLy s : booksToBorrow) {
            s.setTrangThai(SachVatLy.TRANG_THAI_DANG_MUON);
            sachService.updateSachVatLy(s);
        }

        docGia.setSoSachDangMuon(docGia.getSoSachDangMuon() + booksToBorrow.size());
        docGiaService.update(docGia);

        return "Muon sach thanh cong. Ma phieu: " + phieu.getMaPhieu();
    }

    // trả sách
    public String traSach(String maPhieu) {
        PhieuMuon phieu = findById(maPhieu);
        if (phieu == null)
            return "Khong tim thay phieu muon.";
        if (phieu.getNgayTraThucTe() != null)
            return "Phieu nay da tra sach roi.";

        // cập nhật Phieu
        phieu.setNgayTraThucTe(LocalDate.now());
        phieu.tinhTienPhat();
        update(phieu);

        // cập nhật Sach
        if (phieu.getSachVatLy() != null) {
            for (SachVatLy sInPhieu : phieu.getSachVatLy()) {
                SachVatLy currentSach = sachService.findSachVatLyByMaVach(sInPhieu.getMaVach());
                if (currentSach != null) {
                    currentSach.setTrangThai(SachVatLy.TRANG_THAI_SAN_SANG);
                    sachService.updateSachVatLy(currentSach);
                }
            }
        }

        // cập nhật DocGia
        DocGia docGia = docGiaService.findById(phieu.getIdDocGia());
        if (docGia != null) {
            int borrowedCount = (phieu.getSachVatLy() != null) ? phieu.getSachVatLy().size() : 0;
            docGia.setSoSachDangMuon(Math.max(0, docGia.getSoSachDangMuon() - borrowedCount));
            docGiaService.update(docGia);
        }

        return "Tra sach thanh cong. Tien phat: " + phieu.getTienPhat();
    }

    public List<PhieuMuon> getHistory(String docGiaId) {
        return phieuMuonList.stream()
                .filter(p -> p.getIdDocGia().equals(docGiaId))
                .collect(java.util.stream.Collectors.toList());
    }

    public List<PhieuMuon> getCurrentLoans(String docGiaId) {
        return phieuMuonList.stream()
                .filter(p -> p.getIdDocGia().equals(docGiaId) && p.getNgayTraThucTe() == null)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<String> getTopBorrowedBooks(int limit) {
        java.util.Map<String, Integer> bookCounts = new java.util.HashMap<>();
        for (PhieuMuon pm : phieuMuonList) {
            if (pm.getSachVatLy() != null) {
                for (SachVatLy svl : pm.getSachVatLy()) {
                    String isbn = svl.getIsbn();
                    bookCounts.put(isbn, bookCounts.getOrDefault(isbn, 0) + 1);
                }
            }
        }

        return bookCounts.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // DESC
                .limit(limit)
                .map(entry -> {
                    com.project.oop.lms.model.Sach s = sachService.findById(entry.getKey());
                    String title = (s != null) ? s.getTieuDe() : "Unknown ISBN: " + entry.getKey();
                    return title + " (Muon: " + entry.getValue() + " luot)";
                })
                .collect(java.util.stream.Collectors.toList());
    }

    public List<String> getMostActiveReaders(int limit) {
        java.util.Map<String, Integer> readerCounts = new java.util.HashMap<>();
        for (PhieuMuon pm : phieuMuonList) {
            if (pm.getSachVatLy() != null && !pm.getSachVatLy().isEmpty()) {
                String did = pm.getIdDocGia();
                int count = pm.getSachVatLy().size();
                readerCounts.put(did, readerCounts.getOrDefault(did, 0) + count);
            }
        }

        return readerCounts.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // DESC
                .limit(limit)
                .map(entry -> {
                    DocGia dg = docGiaService.findById(entry.getKey());
                    String name = (dg != null) ? dg.getHoTen() : "Unknown ID: " + entry.getKey();
                    return name + " (Tong sach muon: " + entry.getValue() + ")";
                })
                .collect(java.util.stream.Collectors.toList());
    }
}
