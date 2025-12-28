package oop.project.service;
import com.google.gson.reflect.TypeToken;
import com.project.oop.lms.model.DocGia;
import com.project.oop.lms.repository.JsonDataManager;

import java.lang.reflect.Type;
import java.util.List;

public class DocGiaService implements IGeneralService<DocGia> {
    private List<DocGia> docGiaList;
    private final JsonDataManager<DocGia> repo;
    private static final String FILE_PATH = "doc_gia.json";

    public DocGiaService() {
        this.repo = new JsonDataManager<>();
        loadData();
    }

    private void loadData() {
        Type type = new TypeToken<List<DocGia>>() {
        }.getType();
        this.docGiaList = repo.loadFromFile(FILE_PATH, type);
    }

    private void saveData() {
        repo.saveToFile(docGiaList, FILE_PATH);
    }

    // override lại các phương thức của interface
    @Override
    public List<DocGia> getAll() {
        return docGiaList;
    }

    @Override
    public void add(DocGia t) {
        docGiaList.add(t);
        saveData();
    }

    @Override
    public DocGia findById(String id) {
        return docGiaList.stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void update(DocGia t) {
        for (int i = 0; i < docGiaList.size(); i++) {
            if (docGiaList.get(i).getId().equals(t.getId())) {
                docGiaList.set(i, t);
                saveData();
                return;
            }
        }
    }

    @Override
    public void delete(String id) {
        docGiaList.removeIf(d -> d.getId().equals(id));
        saveData();
    }
}
