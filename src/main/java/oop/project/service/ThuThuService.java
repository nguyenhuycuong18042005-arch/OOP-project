package oop.project.service;
import com.google.gson.reflect.TypeToken;
import com.project.oop.lms.model.ThuThu;
import com.project.oop.lms.repository.JsonDataManager;

import java.lang.reflect.Type;
import java.util.List;

public class ThuThuService {//thủ thư không phải đối tượng bị quản lý nên không kế thừa interface
    private List<ThuThu> thuThuList;
    private final JsonDataManager<ThuThu> repo;//biến để tải thông tin từ file
    private static final String FILE_PATH = "thu_thu.json";//đường dẫn file

    public ThuThuService() {
        this.repo = new JsonDataManager<>();
        loadData();
        if (thuThuList.isEmpty()) {
            ThuThu admin = new ThuThu("admin", "Quan Tri Vien", "0000000000", "admin123");
            thuThuList.add(admin);
            saveData();
        }
    }

    private void loadData() {//lấy thông tin thủ thư từ file
        Type type = new TypeToken<List<ThuThu>>() {
        }.getType();
        this.thuThuList = repo.loadFromFile(FILE_PATH, type);
    }

    private void saveData() {//lưu thông tin thủ thư vào file
        repo.saveToFile(thuThuList, FILE_PATH);
    }

    public ThuThu login(String id, String password) {//kiểm tra thông tin đăng nhập
        return thuThuList.stream()
                .filter(tt -> tt.getId().equals(id) && tt.getMatKhau().equals(password))
                .findFirst()
                .orElse(null);
    }
}
