package oop.project.model;

public abstract class Nguoi {
// Lớp trừu tượng đại diện cho một người trong hệ thống thư viện
// Dùng làm lớp cha cho DocGia, ThuThu,...
    protected String id;
    protected String hoTen;
    protected String soDienThoai;

    public Nguoi() {}

    public Nguoi(String id, String hoTen, String soDienThoai) {
        this.id = id;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public abstract String layVaiTro();
}
