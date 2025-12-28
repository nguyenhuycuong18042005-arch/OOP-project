package oop.project.util;

import oop.project.model.DocGia;
import oop.project.model.PhieuMuon;
import oop.project.model.Sach;
import oop.project.model.SachVatLy;
import oop.project.service.DocGiaService;
import oop.project.service.MuonTraService;
import oop.project.service.SachService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataInitializer {
    private final SachService sachService;
    private final DocGiaService docGiaService;
    private final MuonTraService muonTraService;

    public DataInitializer(SachService sachService, DocGiaService docGiaService, MuonTraService muonTraService) {
        this.sachService = sachService;
        this.docGiaService = docGiaService;
        this.muonTraService = muonTraService;
    }

    public void generate() {
        System.out.println("Dang sinh du lieu thu...");

        // 1. Sinh 10 Doc Gia
        List<String> docGiaIds = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            String id = String.format("DG%03d", i);
            docGiaIds.add(id);
            if (docGiaService.findById(id) == null) {
                docGiaService.add(new DocGia(id, "Doc Gia " + i, "090000000" + i, LocalDate.now().plusYears(1)));
            }
        }
        System.out.println("- Da tao 10 Doc Gia.");

        // 2. Sinh 5 Dau Sach, moi dau sach 5 ban sao
        List<String> maVachList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String isbn = String.format("ISBN%03d", i);
            if (sachService.findById(isbn) == null) {
                sachService.add(new Sach(isbn, "Sach Thu " + i, "Tac Gia " + i, "The Loai " + i, 2020 + i));
            }
            for (int j = 1; j <= 5; j++) {
                String mv = isbn + "-CP" + j;
                maVachList.add(mv);
                if (sachService.findSachVatLyByMaVach(mv) == null) {
                    sachService.addSachVatLy(new SachVatLy(mv, isbn, "Ke " + i));
                }
            }
        }
        System.out.println("- Da tao 5 Dau Sach va 25 Ban Sao.");

        // 3. Sinh du lieu Muon Sach
        // Reset sach statuses first just in case to avoid conflicts during generation
        for (SachVatLy s : sachService.getAllSachVatLy()) {
            s.setTrangThai(SachVatLy.TRANG_THAI_SAN_SANG);
            sachService.updateSachVatLy(s);
        }

        int bookIndex = 0;

        // Group 1: 2 Readers - 0 books (Index 0, 1 -> DG001, DG002) - Do nothing

        // Group 2: 2 Readers - 1 book (Index 2, 3 -> DG003, DG004)
        for (int i = 2; i <= 3; i++) {
            List<String> borrow = new ArrayList<>();
            borrow.add(maVachList.get(bookIndex++));
            muonTraService.muonSach(docGiaIds.get(i), borrow);
        }

        // Group 3: 2 Readers - 2 books (Index 4, 5 -> DG005, DG006)
        for (int i = 4; i <= 5; i++) {
            List<String> borrow = new ArrayList<>();
            borrow.add(maVachList.get(bookIndex++));
            borrow.add(maVachList.get(bookIndex++));
            muonTraService.muonSach(docGiaIds.get(i), borrow);
        }

        // Group 4: 2 Readers - 3 books (Index 6, 7 -> DG007, DG008)
        for (int i = 6; i <= 7; i++) {
            List<String> borrow = new ArrayList<>();
            borrow.add(maVachList.get(bookIndex++));
            borrow.add(maVachList.get(bookIndex++));
            borrow.add(maVachList.get(bookIndex++));
            muonTraService.muonSach(docGiaIds.get(i), borrow);
        }

        // Group 5: 2 Readers - 3 books OVERDUE (Index 8, 9 -> DG009, DG010)
        for (int i = 8; i <= 9; i++) {
            String dgId = docGiaIds.get(i);
            List<String> borrow = new ArrayList<>();
            borrow.add(maVachList.get(bookIndex++));
            borrow.add(maVachList.get(bookIndex++));
            borrow.add(maVachList.get(bookIndex++));


            muonTraService.muonSach(dgId, borrow);

            List<PhieuMuon> activeLoans = muonTraService.getCurrentLoans(dgId);
            for (PhieuMuon pm : activeLoans) {

                pm.setNgayMuon(LocalDate.now().minusDays(14));
                pm.setHanTra(LocalDate.now().minusDays(7));
                muonTraService.update(pm);
            }
        }
    }
}
