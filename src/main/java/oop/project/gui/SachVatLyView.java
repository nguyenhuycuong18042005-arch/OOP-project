package oop.project.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import oop.project.model.Sach;
import oop.project.model.SachVatLy;
import oop.project.service.SachService;

public class SachVatLyView {
    private SachService sachService;

    public SachVatLyView(SachService sachService) {
        this.sachService = sachService;
    }

    public Parent getView(){
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label lblTitle = new Label("Bản sao vất lý");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        TableView<SachVatLy> table = new TableView<>();

        TableColumn<SachVatLy, String> colBarcode = new TableColumn<>("Mã Vạch");
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("maVach"));

        TableColumn<SachVatLy, String> colIsbn = new TableColumn<>("ISBN");
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        TableColumn<SachVatLy, String> colTitle = new TableColumn<>("Tiêu Đề");
        colTitle.setCellValueFactory(cellData -> {
            Sach s = sachService.findById(cellData.getValue().getIsbn());
            return new SimpleStringProperty(s != null ? s.getTieuDe() : "Unknown");
        });

        TableColumn<SachVatLy, String> colShelf = new TableColumn<>("Vị Trí Kệ");
        colShelf.setCellValueFactory(new PropertyValueFactory<>("viTriKe"));

        TableColumn<SachVatLy, String> colStatus = new TableColumn<>("Trạng Thái");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("trangThai"));

//        table.getColumns().addAll(colBarcode, colIsbn, colTitle, colShelf, colStatus);
        table.getColumns().add(colBarcode);
        table.getColumns().add(colIsbn);
        table.getColumns().add(colTitle);
        table.getColumns().add(colShelf);
        table.getColumns().add(colStatus);

        refreshTable(table);

        HBox searchBox = new HBox(10);
        searchBox.setPadding(new Insets(5, 0, 5, 0));
        Label lblSearch = new Label("Tìm kiếm:");
        TextField txtSearch = new TextField();
        txtSearch.setPromptText("Nhập mã vạch, ISBN, tiêu đề hoặc vị trí kệ...");
        txtSearch.setPrefWidth(350);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                refreshTable(table);
            } else {
                String keyword = newValue.toLowerCase();
                var filtered = sachService.getAllSachVatLy().stream()
                        .filter(sv -> {
                            Sach s = sachService.findById(sv.getIsbn());
                            String title = (s != null ? s.getTieuDe() : "").toLowerCase();
                            return sv.getMaVach().toLowerCase().contains(keyword) ||
                                    sv.getIsbn().toLowerCase().contains(keyword) ||
                                    sv.getViTriKe().toLowerCase().contains(keyword) ||
                                    title.contains(keyword);
                        })
                        .collect(java.util.stream.Collectors.toList());
                table.setItems(FXCollections.observableArrayList(filtered));
            }
        });

        searchBox.getChildren().addAll(lblSearch, txtSearch);

        HBox buttonBox = new HBox(10);
        Button btnAdd = new Button("Thêm bản sao");
        Button btnEdit = new Button("Chỉnh sửa bản sao");
        Button btnDelete = new Button("Xoá bản sao");

        btnAdd.setOnAction(e -> showAddCopyDialog(table));
        btnEdit.setOnAction(e -> {
            SachVatLy selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showEditCopyDialog(table, selected);
            }
        });
        btnDelete.setOnAction(e -> {
            SachVatLy selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                sachService.deleteSachVatLy(selected.getMaVach());
                refreshTable(table);
            }
        });

        buttonBox.getChildren().addAll(btnAdd, btnEdit, btnDelete);

        root.getChildren().addAll(lblTitle, searchBox, table, buttonBox);
        return root;
    }

    private void refreshTable(TableView<SachVatLy> table) {
        table.setItems(FXCollections.observableArrayList(sachService.getAllSachVatLy()));
    }

    private void showAddCopyDialog(TableView<SachVatLy> table) {
        Dialog<SachVatLy> dialog = new Dialog<>();
        dialog.setTitle("Thêm bản sao vật lý");
        dialog.setHeaderText("Nhập chi tiết");

        ButtonType addBtn = new ButtonType("Thêm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField barcode = new TextField();
        barcode.setPromptText("Mã vạch (Độc nhất)");
        TextField isbn = new TextField();
        isbn.setPromptText("ISBN");
        TextField shelf = new TextField();
        shelf.setPromptText("Vị trí kệ");

        grid.add(new Label("Mã vạch:"), 0, 0);
        grid.add(barcode, 1, 0);
        grid.add(new Label("ISBN:"), 0, 1);
        grid.add(isbn, 1, 1);
        grid.add(new Label("Vị trí kệ:"), 0, 2);
        grid.add(shelf, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addBtn) {
                // Verify ISBN exists
                if (sachService.findById(isbn.getText()) == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Không tìm thấy ISBN, hãy thêm đầu sách trước.");
                    alert.show();
                    return null;
                }
                return new SachVatLy(barcode.getText(), isbn.getText(), shelf.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(copy -> {
            sachService.addSachVatLy(copy);
            refreshTable(table);
        });
    }

    private void showEditCopyDialog(TableView<SachVatLy> table, SachVatLy existing) {
        Dialog<SachVatLy> dialog = new Dialog<>();
        dialog.setTitle("Chỉnh sửa bản sao");
        dialog.setHeaderText("Chỉnh sửa chi tiết bản sao");

        ButtonType btnSave = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSave, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField shelf = new TextField(existing.getViTriKe());

        grid.add(new Label("Vị trí kệ:"), 0, 1);
        grid.add(shelf, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnSave) {
                existing.setViTriKe(shelf.getText());
                return existing;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(copy -> {
            sachService.updateSachVatLy(copy);
            refreshTable(table);
        });
    }
}
