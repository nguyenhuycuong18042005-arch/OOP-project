package oop.project.model;

public class SachVatLy {
    private String maVach; // Unique ID cho từng sách vật lý 
    private String isbn; // ISBN dùng để liên kết bản sách vật lý với thông tin đầu sách
    private String trangThai; // "SAN_SANG", "DANG_MUON", "BAO_TRI"
    private String viTriKe;

    // Các trạng thái hợp lệ của bản sách vật lý
    public static final String TRANG_THAI_SAN_SANG = "SAN_SANG";
    public static final String TRANG_THAI_DANG_MUON = "DANG_MUON";
    public static final String TRANG_THAI_BAO_TRI = "BAO_TRI";

    public SachVatLy() {
    }

    // Khi tạo mới bản sách, mặc định trạng thái là sẵn sàng cho mượn
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
