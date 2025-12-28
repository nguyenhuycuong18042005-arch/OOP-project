package oop.project.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import oop.project.model.ThuThu;
import oop.project.service.ThuThuService;

import javax.swing.text.PasswordView;


public class DangNhapView {
    private final Stage stage;
    private final ThuThuService thuThuService;

    public DangNhapView(Stage stage){
        this.stage = stage;
        this.thuThuService = new ThuThuService();
    }

    public Parent getView(){
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Hệ thống quản lý thư viện");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        Label labelUser = new Label("Tài khoản");
        TextField textUser = new TextField();

        Label labelPass = new Label("Mật khẩu");
        PasswordField textPass = new PasswordField();

        grid.add(labelUser, 0, 0);
        grid.add(textUser, 1, 0);
        grid.add(labelPass, 0, 1);
        grid.add(textPass, 1, 1);

        Button btnLogin = new Button("Đăng nhập");
        Label lblMessage = new Label();
        lblMessage.setStyle("-fx-text-fill: red;");

        btnLogin.setOnAction(e -> {
            String id = textUser.getText();
            String pw = textPass.getText();
            ThuThu user = thuThuService.login(id, pw);
            if (user != null) {
                lblMessage.setText("Đăng nhập thành công!");

                MainView mainView = new MainView(stage, user);
                Scene scene = new Scene(mainView.getView(), 800, 600);
                stage.setScene(scene);
                stage.centerOnScreen();
            } else {
                lblMessage.setText("Tài khoản hoặc mật khẩu không đúng.");
            }
        });
        root.getChildren().addAll(title, grid, btnLogin, lblMessage);
        return root;
    }
}
