package oop.project.gui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import oop.project.model.Sach;
import oop.project.service.SachService;


public class SachView {
        private SachService sachService;

        public SachView(SachService sachService) {
            this.sachService = sachService;
        }

        public Parent getView() {
            VBox root = new VBox(10);
            root.setPadding(new Insets(10));

            Label lblTitle = new Label("Quản lý sách");
            lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            TableView<Sach> table = new TableView<>();

            TableColumn<Sach, String> colIsbn = new TableColumn<>("ISBN");
            colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));

            TableColumn<Sach, String> colTitle = new TableColumn<>("Tiêu Đề");
            colTitle.setCellValueFactory(new PropertyValueFactory<>("tieuDe"));

            TableColumn<Sach, String> colAuthor = new TableColumn<>("Tác Giả");
            colAuthor.setCellValueFactory(new PropertyValueFactory<>("tacGia"));

            TableColumn<Sach, Integer> colYear = new TableColumn<>("Năm Xuất Bản");
            colYear.setCellValueFactory(new PropertyValueFactory<>("namXuatBan"));

//            table.getColumns().addAll(colIsbn, colTitle, colAuthor, colYear);
            table.getColumns().add(colIsbn);
            table.getColumns().add(colTitle);
            table.getColumns().add(colAuthor);
            table.getColumns().add(colYear);


            refreshTable(table);

            HBox searchBox = new HBox(10);
            searchBox.setPadding(new Insets(5, 0, 5, 0));
            Label lblSearch = new Label("Tìm kiếm:");
            TextField txtSearch = new TextField();
            txtSearch.setPromptText("Nhập tiêu đề, tác giả hoặc ISBN...");
            txtSearch.setPrefWidth(300);

            txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    refreshTable(table);
                } else {
                    String keyword = newValue.toLowerCase();
                    var filtered = sachService.getAll().stream()
                            .filter(s -> s.getTieuDe().toLowerCase().contains(keyword) ||
                                    s.getTacGia().toLowerCase().contains(keyword) ||
                                    s.getIsbn().toLowerCase().contains(keyword))
                            .collect(java.util.stream.Collectors.toList());
                    table.setItems(FXCollections.observableArrayList(filtered));
                }
            });

            searchBox.getChildren().addAll(lblSearch, txtSearch);

            HBox buttonBox = new HBox(10);
            Button btnAdd = new Button("Thêm sách");
            Button btnEdit = new Button("Sửa sách");
            Button btnDelete = new Button("Xoá sách");
            Button btnCopies = new Button("Quản lý bản sao sách");

            btnAdd.setOnAction(e -> showAddBookDialog(table));
            btnEdit.setOnAction(e -> {
                Sach selected = table.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    showEditBookDialog(table, selected);
                }
            });
            btnDelete.setOnAction(e -> {
                Sach selected = table.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    sachService.delete(selected.getIsbn());
                    refreshTable(table);
                }
            });
            btnCopies.setOnAction(e -> {
                Sach selected = table.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    showCopiesDialog(selected);
                }
            });

            buttonBox.getChildren().addAll(btnAdd, btnEdit, btnDelete, btnCopies);

            root.getChildren().addAll(lblTitle, searchBox, table, buttonBox);
            return root;
        }

        private void refreshTable(TableView<Sach> table) {
            table.setItems(FXCollections.observableArrayList(sachService.getAll()));
        }

        private void showAddBookDialog(TableView<Sach> table) {
            Dialog<Sach> dialog = new Dialog<>();
            dialog.setTitle("Thêm sách mới");
            dialog.setHeaderText("Nhập thông tin sách");

            ButtonType loginButtonType = new ButtonType("Thêm", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField isbn = new TextField();
            isbn.setPromptText("ISBN");
            TextField title = new TextField();
            title.setPromptText("Tiêu đề");
            TextField author = new TextField();
            author.setPromptText("Tác giả");
            TextField genre = new TextField();
            genre.setPromptText("Thể loại");
            TextField year = new TextField();
            year.setPromptText("Năm xuất bản");

            grid.add(new Label("ISBN:"), 0, 0);
            grid.add(isbn, 1, 0);
            grid.add(new Label("Tiêu đề:"), 0, 1);
            grid.add(title, 1, 1);
            grid.add(new Label("Tác giả:"), 0, 2);
            grid.add(author, 1, 2);
            grid.add(new Label("Thể loại:"), 0, 3);
            grid.add(genre, 1, 3);
            grid.add(new Label("Năm xuất bản:"), 0, 4);
            grid.add(year, 1, 4);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == loginButtonType) {
                    try {
                        return new Sach(isbn.getText(), title.getText(), author.getText(), genre.getText(),
                                Integer.parseInt(year.getText()));
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
                return null;
            });

            dialog.showAndWait().ifPresent(sach -> {
                sachService.add(sach);
                refreshTable(table);
            });
        }

        private void showEditBookDialog(TableView<Sach> table, Sach existing) {
            Dialog<Sach> dialog = new Dialog<>();
            dialog.setTitle("Chỉnh sửa sách");
            dialog.setHeaderText("Chỉnh sửa chi tiết sách");

            ButtonType btnSave = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(btnSave, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField title = new TextField(existing.getTieuDe());
            TextField author = new TextField(existing.getTacGia());
            TextField genre = new TextField(existing.getTheLoai());
            TextField year = new TextField(String.valueOf(existing.getNamXuatBan()));

            grid.add(new Label("Tiêu đề:"), 0, 1);
            grid.add(title, 1, 1);
            grid.add(new Label("Tác giả:"), 0, 2);
            grid.add(author, 1, 2);
            grid.add(new Label("Thể loại:"), 0, 3);
            grid.add(genre, 1, 3);
            grid.add(new Label("Năm xuất bản:"), 0, 4);
            grid.add(year, 1, 4);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == btnSave) {
                    try {
                        existing.setTieuDe(title.getText());
                        existing.setTacGia(author.getText());
                        existing.setTheLoai(genre.getText());
                        existing.setNamXuatBan(Integer.parseInt(year.getText()));
                        return existing;
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
                return null;
            });

            dialog.showAndWait().ifPresent(sach -> {
                sachService.update(sach);
                refreshTable(table);
            });
        }

        private void showCopiesDialog(Sach book) {
            Stage stage = new Stage();
            stage.setTitle("Quản lý bản sao: " + book.getTieuDe());

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(10));

            TableView<oop.project.model.SachVatLy> table = new TableView<>();
            TableColumn<oop.project.model.SachVatLy, String> colBarcode = new TableColumn<>("Mã Vạch");
            colBarcode.setCellValueFactory(new PropertyValueFactory<>("maVach"));
            TableColumn<oop.project.model.SachVatLy, String> colShelf = new TableColumn<>("Vị Trí Kệ Sách");
            colShelf.setCellValueFactory(new PropertyValueFactory<>("viTriKe"));
            TableColumn<oop.project.model.SachVatLy, String> colStatus = new TableColumn<>("Trạng thái");
            colStatus.setCellValueFactory(new PropertyValueFactory<>("trangThai"));

//            table.getColumns().addAll(colBarcode, colShelf, colStatus);
            table.getColumns().add(colBarcode);
            table.getColumns().add(colShelf);
            table.getColumns().add(colStatus);

            Runnable refresh = () -> {
                table.setItems(FXCollections.observableArrayList(
                        sachService.getAllSachVatLy().stream()
                                .filter(s -> s.getIsbn().equals(book.getIsbn()))
                                .collect(java.util.stream.Collectors.toList())));
            };
            refresh.run();

            HBox controls = new HBox(10);
            TextField txtBarcode = new TextField();
            txtBarcode.setPromptText("Mã vạch mới");
            TextField txtShelf = new TextField();
            txtShelf.setPromptText("Vị trí kệ");
            Button btnAdd = new Button("Thêm bản sao");
            Button btnDel = new Button("Xoá mục đã chọn");

            btnAdd.setOnAction(e -> {
                if (!txtBarcode.getText().isEmpty()) {
                    sachService.addSachVatLy(new oop.project.model.SachVatLy(txtBarcode.getText(), book.getIsbn(),
                            txtShelf.getText()));
                    txtBarcode.clear();
                    refresh.run();
                }
            });

            btnDel.setOnAction(e -> {
                oop.project.model.SachVatLy selected = table.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    sachService.deleteSachVatLy(selected.getMaVach());
                    refresh.run();
                }
            });

            controls.getChildren().addAll(txtBarcode, txtShelf, btnAdd, btnDel);
            layout.getChildren().addAll(new Label("Bản sao của " + book.getTieuDe()), table, controls);

            Scene scene = new Scene(layout, 500, 400);
            stage.setScene(scene);
            stage.show();
        }

}
