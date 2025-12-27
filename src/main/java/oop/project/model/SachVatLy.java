package oop.project.model;

public class SachVatLy {
    private String maVach; // Unique ID
    private String isbn; // liên kết với sách
    private String trangThai; // "SAN_SANG", "DANG_MUON", "BAO_TRI"
    private String viTriKe;

    public static final String TRANG_THAI_SAN_SANG = "SAN_SANG";
    public static final String TRANG_THAI_DANG_MUON = "DANG_MUON";
    public static final String TRANG_THAI_BAO_TRI = "BAO_TRI";

    public SachVatLy() {
    }

    public SachVatLy(String maVach, String isbn, String viTriKe) {
        this.maVach = maVach;
        this.isbn = isbn;
        this.viTriKe = viTriKe;
        this.trangThai = TRANG_THAI_SAN_SANG;
    }

    public String getMaVach() {
        return maVach;
    }

    public void setMaVach(String maVach) {
        this.maVach = maVach;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getViTriKe() {
        return viTriKe;
    }

    public void setViTriKe(String viTriKe) {
        this.viTriKe = viTriKe;
    }
}
