package oop.project.service;

import com.google.gson.reflect.TypeToken;
import oop.project.model.Sach;
import oop.project.model.SachVatLy;
import oop.project.repository.JsonDataManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SachService implements IGeneralService<Sach> {
    private List<Sach> sachList;
    private List<SachVatLy> sachVatLyList;
    private final JsonDataManager<Sach> sachRepo;
    private final JsonDataManager<SachVatLy> sachVatLyRepo;
    private static final String SACH_FILE = "sach.json";
    private static final String SACH_VAT_LY_FILE = "sach_vat_ly.json";

    public SachService() {
        this.sachRepo = new JsonDataManager<>();
        this.sachVatLyRepo = new JsonDataManager<>();
        loadData();
    }

    private void loadData() {
        Type sachType = new TypeToken<List<Sach>>() {
        }.getType();
        Type sachVatLyType = new TypeToken<List<SachVatLy>>() {
        }.getType();
        this.sachList = sachRepo.loadFromFile(SACH_FILE, sachType);
        this.sachVatLyList = sachVatLyRepo.loadFromFile(SACH_VAT_LY_FILE, sachVatLyType);
    }

    private void saveData() {
        sachRepo.saveToFile(sachList, SACH_FILE);
        // SachVatLy is saved separately when modified
        sachVatLyRepo.saveToFile(sachVatLyList, SACH_VAT_LY_FILE);
    }

    @Override
    public List<Sach> getAll() {
        return sachList;
    }

    @Override
    public void add(Sach t) {
        sachList.add(t);
        saveData();
    }

    @Override
    public Sach findById(String id) {
        // ID for Sach is ISBN
        return sachList.stream()
                .filter(s -> s.getIsbn().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void update(Sach t) {
        for (int i = 0; i < sachList.size(); i++) {
            if (sachList.get(i).getIsbn().equals(t.getIsbn())) {
                sachList.set(i, t);
                saveData();
                return;
            }
        }
    }

    @Override
    public void delete(String id) {
        sachList.removeIf(s -> s.getIsbn().equals(id));
        saveData();
    }

    // --- SachVatLy Management ---

    public void addSachVatLy(SachVatLy svl) {
        sachVatLyList.add(svl);
        saveData();
    }

    public SachVatLy findSachVatLyByMaVach(String maVach) {
        return sachVatLyList.stream()
                .filter(s -> s.getMaVach().equals(maVach))
                .findFirst()
                .orElse(null);
    }

    public List<SachVatLy> getAllSachVatLy() {
        return sachVatLyList;
    }

    public void updateSachVatLy(SachVatLy svl) {
        for (int i = 0; i < sachVatLyList.size(); i++) {
            if (sachVatLyList.get(i).getMaVach().equals(svl.getMaVach())) {
                sachVatLyList.set(i, svl);
                saveData();
                return;
            }
        }
    }

    public void deleteSachVatLy(String maVach) {
        sachVatLyList.removeIf(s -> s.getMaVach().equals(maVach));
        saveData();
    }

    public List<Sach> searchByTitle(String keyword) {
        List<Sach> result = new ArrayList<>();
        for (Sach s : sachList) {
            if (s.getTieuDe().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(s);
            }
        }
        return result;
    }

    public List<Sach> searchByIsbn(String isbn) {
        List<Sach> result = new ArrayList<>();
        for (Sach s : sachList) {
            if (s.getIsbn().toLowerCase().contains(isbn.toLowerCase())) {
                result.add(s);
            }
        }
        return result;
    }

    public List<Sach> searchByAuthor(String author) {
        List<Sach> result = new ArrayList<>();
        for (Sach s : sachList) {
            if (s.getTacGia().toLowerCase().contains(author.toLowerCase())) {
                result.add(s);
            }
        }
        return result;
    }
}
