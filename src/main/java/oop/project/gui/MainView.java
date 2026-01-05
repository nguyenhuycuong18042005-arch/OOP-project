package oop.project.gui;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.Node.*;

import oop.project.model.ThuThu;
import oop.project.service.DocGiaService;
import oop.project.service.MuonTraService;
import oop.project.service.SachService;

public class MainView {
    private Stage stage = new Stage();
    private ThuThu currentUser;

    public MainView(Stage stage, ThuThu user) {
        this.stage = stage;
        this.currentUser = user;
    }

    public Parent getView(){
        BorderPane root = new BorderPane();

        Label header = new Label("Chào mừng " + currentUser.getHoTen());
        header.setStyle("-fx-font-size: 16px; -fx-padding: 10; -fx-background-color: #eee;");
        root.setTop(header);

        // Khởi tạo service dùng chung trong hệ thống
        SachService sachService = new SachService();
        DocGiaService docGiaService = new DocGiaService();
        MuonTraService muonTraService = new MuonTraService(sachService, docGiaService);

        if (sachService.getAll().isEmpty() && docGiaService.getAll().isEmpty()) {
            System.out.println("Không tìm thấy dữ liệu. Đang khởi tạo dữ liệu mẫu...");
            new oop.project.util.DataInitializer(sachService, docGiaService, muonTraService).generate();
            System.out.println("Dữ liệu mẫu đã được khởi tạo thành công!\n");
        }

        // Tạo tab, mỗi tab là 1 chức năng
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab tabSach = new Tab("Sách");
        tabSach.setContent(new SachView(sachService).getView());

        Tab tabSachHu = new Tab("Bản sao vật lý");
        tabSachHu.setContent(new SachVatLyView(sachService).getView());

        Tab tabDocGia = new Tab("Độc Giả");
        tabDocGia.setContent(new DocGiaView(docGiaService).getView());

        Tab tabMuonTra = new Tab("Mượn trả");
        tabMuonTra.setContent(new MuonTraView(muonTraService, sachService, docGiaService).getView());

        Tab tabThongKe = new Tab("Báo cáo - Thống kê");
        BaoCaoView reportView = new BaoCaoView(muonTraService, sachService, docGiaService);
        tabThongKe.setContent(reportView.getView());

        tabPane.getTabs().addAll(tabSach, tabSachHu, tabDocGia, tabMuonTra, tabThongKe);

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            // refresh lại báo cáo để cập nhật số liệu mới
            if (newTab == tabThongKe) {
                reportView.refresh();
            }
        });

        root.setCenter(tabPane);

        return root;
    }

}
