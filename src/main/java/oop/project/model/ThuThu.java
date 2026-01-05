package oop.project.model;
public class ThuThu extends Nguoi {
    private String matKhau; // Mật khẩu dùng cho việc đăng nhập hệ thống của thủ thư
    

    public ThuThu() {
        super();
    }

    public ThuThu(String id, String hoTen, String soDienThoai, String matKhau) {
        super(id, hoTen, soDienThoai);
        this.matKhau = matKhau;
        
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    @Override
    public String layVaiTro() {
        return "Thu thu";
    }
}
