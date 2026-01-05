package oop.project.gui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import oop.project.model.PhieuMuon;
import oop.project.model.SachVatLy;
import oop.project.service.DocGiaService;
import oop.project.service.MuonTraService;
import oop.project.service.SachService;

import java.time.LocalDate;
import java.util.List;

public class MuonTraView {
    private MuonTraService muonTraService;
    private SachService sachService;
    private DocGiaService docGiaService;

    public MuonTraView(MuonTraService muonTraService, SachService sachService, DocGiaService docGiaService) {
        this.muonTraService = muonTraService;
        this.sachService = sachService;
        this.docGiaService = docGiaService;
    }

    public Parent getView() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label lblTitle = new Label("Quản lý Mượn Trả");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        TableView<PhieuMuon> table = new TableView<>();

        TableColumn<PhieuMuon, String> colId = new TableColumn<>("Mã Phiếu Mượn");
        colId.setCellValueFactory(new PropertyValueFactory<>("maPhieu"));

        TableColumn<PhieuMuon, String> colReader = new TableColumn<>("Mã Độc Giả");
        colReader.setCellValueFactory(new PropertyValueFactory<>("idDocGia"));

        TableColumn<PhieuMuon, LocalDate> colDate = new TableColumn<>("Ngày Mượn");
        colDate.setCellValueFactory(new PropertyValueFactory<>("ngayMuon"));

        TableColumn<PhieuMuon, LocalDate> colDue = new TableColumn<>("Hạn Trả");
        colDue.setCellValueFactory(new PropertyValueFactory<>("hanTra"));

        TableColumn<PhieuMuon, LocalDate> colReturn = new TableColumn<>("Ngày Trả");
        colReturn.setCellValueFactory(new PropertyValueFactory<>("ngayTraThucTe"));

//        table.getColumns().addAll(colId, colReader, colDate, colDue, colReturn);
        table.getColumns().add(colId);
        table.getColumns().add(colReader);
        table.getColumns().add(colDate);
        table.getColumns().add(colDue);
        table.getColumns().add(colReturn);

        refreshTable(table);

        HBox searchBox = new HBox(10);
        searchBox.setPadding(new Insets(5, 0, 5, 0));
        Label lblSearch = new Label("Tìm kiếm:");
        TextField txtSearch = new TextField();
        txtSearch.setPromptText("Nhập mã phiếu mượn hoặc mã độc giả...");
        txtSearch.setPrefWidth(300);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                refreshTable(table);
            } else {
                String keyword = newValue.toLowerCase();
                var filtered = muonTraService.getAll().stream()
                        .filter(pm -> pm.getMaPhieu().toLowerCase().contains(keyword) ||
                                pm.getIdDocGia().toLowerCase().contains(keyword))
                        .collect(java.util.stream.Collectors.toList());
                table.setItems(FXCollections.observableArrayList(filtered));
            }
        });

        searchBox.getChildren().addAll(lblSearch, txtSearch);

        HBox buttonBox = new HBox(10);
        Button btnBorrow = new Button("Tạo phiếu mượn");
        Button btnReturn = new Button("Trả sách");
        Button btnDelete = new Button("Xoá phiếu mượn");

        btnBorrow.setOnAction(e -> showBorrowDialog(table));
        btnReturn.setOnAction(e -> {
            PhieuMuon selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (selected.getNgayTraThucTe() != null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Phiếu mượn này đã được trả.");
                    alert.show();
                } else {
                    String res = muonTraService.traSach(selected.getMaPhieu());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, res);
                    alert.showAndWait();
                    refreshTable(table);
                    table.refresh(); // Force UI update
                }
            }
        });
        btnDelete.setOnAction(e -> {
            PhieuMuon selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Xác nhận xoá phiếu mượn này? (Lưu ý: xoá phiếu mượn sẽ không tự động trả sách)", ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(resp -> {
                    if (resp == ButtonType.YES) {
                        muonTraService.delete(selected.getMaPhieu());
                        refreshTable(table);
                    }
                });
            }
        });

        buttonBox.getChildren().addAll(btnBorrow, btnReturn, btnDelete);

        root.getChildren().addAll(lblTitle, searchBox, table, buttonBox);
        return root;
    }

    private void refreshTable(TableView<PhieuMuon> table) {
        table.setItems(FXCollections.observableArrayList(muonTraService.getAll()));
    }

    private void showBorrowDialog(TableView<PhieuMuon> table) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Tạo phiếu mượn sách");
        dialog.setHeaderText("Chọn độc giả và sách vật lý");

        ButtonType type = new ButtonType("Mượn", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(type, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        // Reader Selection
        ComboBox<oop.project.model.DocGia> cmbReader = new ComboBox<>();
        cmbReader.setItems(FXCollections.observableArrayList(docGiaService.getAll()));
        cmbReader.setPromptText("Chọn độc giả");
        // Custom rendering for Reader ComboBox
        cmbReader.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(oop.project.model.DocGia item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getId() + " - " + item.getHoTen());
            }
        });
        cmbReader.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(oop.project.model.DocGia item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getId() + " - " + item.getHoTen());
            }
        });

        // Available Books Selection
        TableView<oop.project.model.SachVatLy> tblAvailable = new TableView<>();
        tblAvailable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblAvailable.setPrefHeight(200);

        TableColumn<oop.project.model.SachVatLy, String> colBarcode = new TableColumn<>("Mã Vạch");
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("maVach"));

        TableColumn<oop.project.model.SachVatLy, String> colTitle = new TableColumn<>("Tiêu Đề");
        colTitle.setCellValueFactory(cellData -> {
            oop.project.model.Sach s = sachService.findById(cellData.getValue().getIsbn());
            return new javafx.beans.property.SimpleStringProperty(s != null ? s.getTieuDe() : "Unknown");
        });

        tblAvailable.getColumns().addAll(colBarcode, colTitle);

        // Load only available books
        List<SachVatLy> availableBooks = sachService.getAllSachVatLy().stream()
                .filter(s -> oop.project.model.SachVatLy.TRANG_THAI_SAN_SANG.equals(s.getTrangThai()))
                .collect(java.util.stream.Collectors.toList());
        tblAvailable.setItems(FXCollections.observableArrayList(availableBooks));

        grid.add(new Label("Độc giả:"), 0, 0);
        grid.add(cmbReader, 1, 0);
        grid.add(new Label("Chọn sách (Ctrl/Shift để chọn nhiều sách cùng lúc):"), 0, 1);
        grid.add(tblAvailable, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == type) {
                oop.project.model.DocGia selectedReader = cmbReader.getSelectionModel().getSelectedItem();
                if (selectedReader == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Chưa chọn độc giả.");
                    alert.show();
                    return false;
                }

                List<oop.project.model.SachVatLy> selectedBooks = tblAvailable.getSelectionModel()
                        .getSelectedItems();
                if (selectedBooks.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Chọn ít nhất 1 quyển sách.");
                    alert.show();
                    return false;
                }

                List<String> barcodes = selectedBooks.stream()
                        .map(oop.project.model.SachVatLy::getMaVach)
                        .collect(java.util.stream.Collectors.toList());

                String result = muonTraService.muonSach(selectedReader.getId(), barcodes);

                Alert alert = new Alert(Alert.AlertType.INFORMATION, result);
                alert.showAndWait();
                return true;
            }
            return false;
        });

        dialog.showAndWait().ifPresent(success -> {
            if (success)
                refreshTable(table);
        });
    }
}
