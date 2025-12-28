package oop.project.gui;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import oop.project.service.DocGiaService;
import oop.project.service.MuonTraService;
import oop.project.service.SachService;

import java.util.List;

public class BaoCaoView {
    private MuonTraService muonTraService;
    private SachService sachService;
    private DocGiaService docGiaService;
    private TextArea txtReport;

    public BaoCaoView(MuonTraService muonTraService, SachService sachService, DocGiaService docGiaService) {
        this.muonTraService = muonTraService;
        this.sachService = sachService;
        this.docGiaService = docGiaService;
    }

    public Parent getView() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label lblTitle = new Label("Báo cáo - Thống kê");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        txtReport = new TextArea();
        txtReport.setEditable(false);
        txtReport.setPrefHeight(400);

        // Initial load
        refresh();

        root.getChildren().addAll(lblTitle, txtReport);
        return root;
    }

    public void refresh() {
        if (txtReport == null)
            return;

        StringBuilder sb = new StringBuilder();

        // General Stats
        long totalBooks = sachService.getAllSachVatLy().size();
        long borrowed = sachService.getAllSachVatLy().stream()
                .filter(s -> "DANG_MUON".equals(s.getTrangThai()))
                .count();
        sb.append("--- Thống kê hệ thống ---\n");
        sb.append("Tổng sách vật lý đang có: ").append(totalBooks).append("\n");
        sb.append("Đang mượn: ").append(borrowed).append("\n");
        sb.append("Sẵn có: ").append(totalBooks - borrowed).append("\n\n");

        // Top Borrowed Books
        sb.append("--- Sách được mượn nhiều nhất (Top 5) ---\n");
        List<String> topBooks = muonTraService.getTopBorrowedBooks(5);
        if (topBooks.isEmpty()) {
            sb.append("(No data)\n");
        } else {
            topBooks.forEach(s -> sb.append(s).append("\n"));
        }
        sb.append("\n");

        // Most Active Readers
        sb.append("--- Độc giả tích cực nhất (Top 5) ---\n");
        List<String> topReaders = muonTraService.getMostActiveReaders(5);
        if (topReaders.isEmpty()) {
            sb.append("(No data)\n");
        } else {
            topReaders.forEach(s -> sb.append(s).append("\n"));
        }

        txtReport.setText(sb.toString());
    }
}
