package oop.project.model;
import java.time.LocalDate;

public class DocGia extends Nguoi {
    private LocalDate ngayHetHanThe;
    private int soSachDangMuon;
    // private List<PhieuMuon> danhSachPhieuMuon;
    // Số lượng sách tối đa một độc giả được mượn cùng lúc
    public static final int SO_SACH_DANG_MUON_MAX = 3;
    public DocGia() {
        super();
    }

    public DocGia(String id, String hoTen, String soDienThoai, LocalDate ngayHetHanThe) {
        super(id, hoTen, soDienThoai);
        this.ngayHetHanThe = ngayHetHanThe;
        this.soSachDangMuon = 0;
    }

    public LocalDate getNgayHetHanThe() {
        return ngayHetHanThe;
    }

    public void setNgayHetHanThe(LocalDate ngayHetHanThe) {
        this.ngayHetHanThe = ngayHetHanThe;
    }

    public int getSoSachDangMuon() {
        return soSachDangMuon;
    }

    public void setSoSachDangMuon(int soSachDangMuon) {
        this.soSachDangMuon = soSachDangMuon;
    }

    // Kiểm tra thẻ độc giả còn hiệu lực hay không
    public boolean kiemTraHanThe() {
        if (ngayHetHanThe == null)
            return false;
        return ngayHetHanThe.isAfter(LocalDate.now());
    }

    @Override
    public String layVaiTro() {
        return "Doc gia";
    }
}
