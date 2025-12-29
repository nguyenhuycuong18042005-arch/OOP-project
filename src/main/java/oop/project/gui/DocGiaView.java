package oop.project.gui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import oop.project.model.DocGia;
import oop.project.service.DocGiaService;

import java.time.LocalDate;

public class DocGiaView {
    private DocGiaService docGiaService;

    public DocGiaView(DocGiaService docGiaService) {
        this.docGiaService = docGiaService;
    }

    public Parent getView() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label lblTitle = new Label("Quản lý độc giả");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        TableView<DocGia> table = new TableView<>();

        TableColumn<DocGia, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<DocGia, String> colName = new TableColumn<>("Họ Tên");
        colName.setCellValueFactory(new PropertyValueFactory<>("hoTen"));

        TableColumn<DocGia, String> colPhone = new TableColumn<>("Số Điện Thoại");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));

        TableColumn<DocGia, LocalDate> colExpiry = new TableColumn<>("Ngày Hết Hạn Thẻ");
        colExpiry.setCellValueFactory(new PropertyValueFactory<>("ngayHetHanThe"));

        TableColumn<DocGia, Integer> colBorrowCount = new TableColumn<>("Số Sách Đang Mượn");
        colBorrowCount.setCellValueFactory(new PropertyValueFactory<>("soSachDangMuon"));

//        table.getColumns().addAll(colId, colName, colPhone, colExpiry, colBorrowCount);
        table.getColumns().add(colId);
        table.getColumns().add(colName);
        table.getColumns().add(colPhone);
        table.getColumns().add(colExpiry);
        table.getColumns().add(colBorrowCount);
        // Load data
        refreshTable(table);

        HBox searchBox = new HBox(10);
        searchBox.setPadding(new Insets(5, 0, 5, 0));
        Label lblSearch = new Label("Tìm kiếm:");
        TextField txtSearch = new TextField();
        txtSearch.setPromptText("Nhập ID, tên hoặc số điện thoại...");
        txtSearch.setPrefWidth(300);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                refreshTable(table);
            } else {
                String keyword = newValue.toLowerCase();
                var filtered = docGiaService.getAll().stream()
                        .filter(d -> d.getId().toLowerCase().contains(keyword) ||
                                d.getHoTen().toLowerCase().contains(keyword) ||
                                d.getSoDienThoai().toLowerCase().contains(keyword))
                        .collect(java.util.stream.Collectors.toList());
                table.setItems(FXCollections.observableArrayList(filtered));
            }
        });

        searchBox.getChildren().addAll(lblSearch, txtSearch);

        // Buttons
        HBox buttonBox = new HBox(10);
        Button btnAdd = new Button("Thêm Độc Giả");
        Button btnEdit = new Button("Chỉnh sửa độc giả");
        Button btnDelete = new Button("Xoá độc giả");

        btnAdd.setOnAction(e -> showAddReaderDialog(table));
        btnEdit.setOnAction(e -> {
            DocGia selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showEditReaderDialog(table, selected);
            }
        });
        btnDelete.setOnAction(e -> {
            DocGia selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                docGiaService.delete(selected.getId());
                refreshTable(table);
            }
        });

        buttonBox.getChildren().addAll(btnAdd, btnEdit, btnDelete);

        root.getChildren().addAll(lblTitle, searchBox, table, buttonBox);
        return root;
    }

    private void refreshTable(TableView<DocGia> table) {
        table.setItems(FXCollections.observableArrayList(docGiaService.getAll()));
    }

    private void showAddReaderDialog(TableView<DocGia> table) {
        Dialog<DocGia> dialog = new Dialog<>();
        dialog.setTitle("Thêm độc giả mới");
        dialog.setHeaderText("Nhập thông tin độc giả");

        ButtonType loginButtonType = new ButtonType("Thêm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField id = new TextField();
        id.setPromptText("ID");
        TextField name = new TextField();
        name.setPromptText("Tên");
        TextField phone = new TextField();
        phone.setPromptText("Số điện thoại");

        grid.add(new Label("ID:"), 0, 0);
        grid.add(id, 1, 0);
        grid.add(new Label("Tên:"), 0, 1);
        grid.add(name, 1, 1);
        grid.add(new Label("Số điện thoại:"), 0, 2);
        grid.add(phone, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new DocGia(id.getText(), name.getText(), phone.getText(), LocalDate.now().plusYears(1));
            }
            return null;
        });

        dialog.showAndWait().ifPresent(reader -> {
            docGiaService.add(reader);
            refreshTable(table);
        });
    }

    private void showEditReaderDialog(TableView<DocGia> table, DocGia existing) {
        Dialog<DocGia> dialog = new Dialog<>();
        dialog.setTitle("Chỉnh sửa độc giả");
        dialog.setHeaderText("Chỉnh sửa cho độc giả có ID: " + existing.getId());

        ButtonType btnSave = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSave, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField(existing.getHoTen());
        TextField phone = new TextField(existing.getSoDienThoai());

        grid.add(new Label("Họ tên:"), 0, 1);
        grid.add(name, 1, 1);
        grid.add(new Label("Số điện thoại:"), 0, 2);
        grid.add(phone, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnSave) {
                existing.setHoTen(name.getText());
                existing.setSoDienThoai(phone.getText());
                return existing;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(reader -> {
            docGiaService.update(reader);
            refreshTable(table);
        });
    }
}
