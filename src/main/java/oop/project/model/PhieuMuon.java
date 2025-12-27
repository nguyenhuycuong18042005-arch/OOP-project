package oop.project.model;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class PhieuMuon {
    private String maPhieu;
    private String idDocGia;
    private List<SachVatLy> sachVatLy;
    private LocalDate ngayMuon;
    private LocalDate hanTra;
    private LocalDate ngayTraThucTe;
    private double tienPhat;

    public PhieuMuon() {
    }

    public PhieuMuon(String maPhieu, String idDocGia, List<SachVatLy> sachVatLy, LocalDate ngayMuon, LocalDate hanTra) {
        this.maPhieu = maPhieu;
        this.idDocGia = idDocGia;
        this.sachVatLy = sachVatLy;
        this.ngayMuon = ngayMuon;
        this.hanTra = hanTra;
        this.tienPhat = 0.0;
    }

    public String getMaPhieu() {
        return maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu;
    }

    public String getIdDocGia() {
        return idDocGia;
    }

    public void setIdDocGia(String idDocGia) {
        this.idDocGia = idDocGia;
    }

    public List<SachVatLy> getSachVatLy() {
        return sachVatLy;
    }

    public void setSachVatLy(List<SachVatLy> sachVatLy) {
        this.sachVatLy = sachVatLy;
    }

    public LocalDate getNgayMuon() {
        return ngayMuon;
    }

    public void setNgayMuon(LocalDate ngayMuon) {
        this.ngayMuon = ngayMuon;
    }

    public LocalDate getHanTra() {
        return hanTra;
    }

    public void setHanTra(LocalDate hanTra) {
        this.hanTra = hanTra;
    }

    public LocalDate getNgayTraThucTe() {
        return ngayTraThucTe;
    }

    public void setNgayTraThucTe(LocalDate ngayTraThucTe) {
        this.ngayTraThucTe = ngayTraThucTe;
    }

    public double getTienPhat() {
        return tienPhat;
    }

    public void setTienPhat(double tienPhat) {
        this.tienPhat = tienPhat;
    }

    public boolean isQuaHan() {
        if (ngayTraThucTe != null) {
            return ngayTraThucTe.isAfter(hanTra);
        }
        return LocalDate.now().isAfter(hanTra);
    }

    public void tinhTienPhat() {
        if (isQuaHan() && ngayTraThucTe != null) {
            long daysOver = ChronoUnit.DAYS.between(hanTra, ngayTraThucTe);
            // Example fine: 5000 VND per day
            this.tienPhat = daysOver * 5000;
        } else {
            this.tienPhat = 0;
        }
    }

    public double getEstimatedFine() {
        if (ngayTraThucTe == null && LocalDate.now().isAfter(hanTra)) {
            long daysOver = ChronoUnit.DAYS.between(hanTra, LocalDate.now());
            return daysOver * 5000;
        }
        return 0;
    }
}
