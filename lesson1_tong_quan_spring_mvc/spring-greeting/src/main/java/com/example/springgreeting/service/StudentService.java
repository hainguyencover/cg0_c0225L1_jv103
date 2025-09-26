package com.example.springgreeting.service;

import com.example.springgreeting.model.Student;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService implements IStudentService {

    private final Map<String, Student> students = new HashMap<>();

    @PostConstruct
    public void initData() {
        students.put("SV001", new Student("SV001", "Nguyen Van A", 9.2));
        students.put("SV002", new Student("SV002", "Tran Thi B", 8.5));
        students.put("SV003", new Student("SV003", "Le Van C", 7.8));
        students.put("SV004", new Student("SV004", "Pham Thi D", 6.3));
        students.put("SV005", new Student("SV005", "Hoang Van E", 4.9));
        students.put("SV006", new Student("SV006", "Do Thi F", 7.1));
        students.put("SV007", new Student("SV007", "Nguyen Van G", 8.9));
        students.put("SV008", new Student("SV008", "Tran Van H", 5.5));
        students.put("SV009", new Student("SV009", "Le Thi I", 9.8));
        students.put("SV010", new Student("SV010", "Pham Van J", 6.7));
        students.put("SV011", new Student("SV011", "Nguyen Van K", 7.4));
        students.put("SV012", new Student("SV012", "Tran Thi L", 8.1));
        students.put("SV013", new Student("SV013", "Le Van M", 5.9));
        students.put("SV014", new Student("SV014", "Pham Thi N", 9.0));
        students.put("SV015", new Student("SV015", "Hoang Van O", 4.5));
        students.put("SV016", new Student("SV016", "Do Thi P", 6.8));
        students.put("SV017", new Student("SV017", "Nguyen Van Q", 8.2));
        students.put("SV018", new Student("SV018", "Tran Van R", 7.6));
        students.put("SV019", new Student("SV019", "Le Thi S", 5.2));
        students.put("SV020", new Student("SV020", "Pham Van T", 9.5));
    }

    @Override
    public List<Student> findAll(String q, String sort, String dir, int page, int size) {
        // 1. filter theo q
        List<Student> list = students.values().stream()
                .filter(s -> q == null || q.isEmpty()
                        || s.getMssv().toLowerCase().contains(q.toLowerCase())
                        || s.getHoTen().toLowerCase().contains(q.toLowerCase()))
                .collect(Collectors.toList());

        // 2. sort
        Comparator<Student> cmp;
        switch (sort) {
            case "name":
                cmp = Comparator.comparing(Student::getHoTen, String.CASE_INSENSITIVE_ORDER);
                break;
            case "gpa":
                cmp = Comparator.comparingDouble(Student::getDiemTongKet);
                break;
            default:
                cmp = Comparator.comparing(Student::getMssv);
        }
        if ("desc".equalsIgnoreCase(dir)) {
            cmp = cmp.reversed();
        }
        list.sort(cmp);

        // 3. pagination
        int total = list.size();
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);
        if (fromIndex > total) {
            return Collections.emptyList();
        }
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public long count(String q) {
        if (q == null || q.trim().isEmpty()) return students.size();
        String kw = q.trim().toLowerCase();
        return (int) students.values().stream()
                .filter(s -> s.getMssv().toLowerCase().contains(kw) || s.getHoTen().toLowerCase().contains(kw))
                .count();
    }

    @Override
    public Student findById(String id) {
        return students.get(id);
    }

    @Override
    public void create(Student s) {
        students.put(s.getMssv(), s);
    }

    @Override
    public void update(Student s) {
        students.put(s.getMssv(), s);
    }

    @Override
    public void delete(String mssv) {
        students.remove(mssv);
    }

    @Override
    public boolean existsById(String mssv) {
        return students.containsKey(mssv);
    }
}
