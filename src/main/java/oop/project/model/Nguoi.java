package oop.project.model;

public abstract class Nguoi {
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
