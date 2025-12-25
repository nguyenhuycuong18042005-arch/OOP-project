package oop.project.service;

import java.util.List;

public interface IGeneralService<T> {
    List<T> getAll();// lấy thông tin từ database

    void add(T t);// thêm

    T findById(String id);// tim kiếm theo id

    void update(T t);// cập nhật

    void delete(String id); // xoa
}
