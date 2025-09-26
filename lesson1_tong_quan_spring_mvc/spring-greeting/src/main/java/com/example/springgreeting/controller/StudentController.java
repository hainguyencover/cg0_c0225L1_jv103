package com.example.springgreeting.controller;

import com.example.springgreeting.model.Student;
import com.example.springgreeting.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private IStudentService studentService;

    @GetMapping
    public ModelAndView list(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        List<Student> students = studentService.findAll(q, sort, dir, page, size);
        long total = studentService.count(q);
        int totalPages = (int) Math.ceil((double) total / size);

        ModelAndView mav = new ModelAndView("students/list");
        mav.addObject("students", students);
        mav.addObject("q", q);
        mav.addObject("sort", sort);
        mav.addObject("dir", dir);
        mav.addObject("page", page);
        mav.addObject("size", size);
        mav.addObject("totalPages", totalPages);
        mav.addObject("total", total);
        return mav;
    }

    @GetMapping("/{id}")
    public String detail(
            @PathVariable("id") String id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Student s = studentService.findById(id); // gọi service để tìm sinh viên theo MSSV

        if (s == null) {
            redirectAttributes.addFlashAttribute("message",
                    "Không tìm thấy sinh viên có MSSV = " + id);
            return "redirect:/students";
        }

        model.addAttribute("student", s);

        // Chỉ cần trả về "students/student-detail" (KHÔNG có .jsp, KHÔNG redirect)
        return "students/student-detail";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        // Truyền object trống để binding với form JSP
        model.addAttribute("studentForm", new Student());
        return "students/add";
    }

    @PostMapping("/add")
    public String doAdd(
            @ModelAttribute("studentForm") Student studentForm,
            BindingResult binding,
            RedirectAttributes ra,
            Model model) {

        boolean hasError = false;

        // Kiểm tra MSSV
        if (studentForm.getMssv() == null || studentForm.getMssv().trim().isEmpty()) {
            binding.rejectValue("mssv", "mssv.empty", "MSSV không được để trống");
            hasError = true;
        } else if (studentForm.getMssv().length() < 3 || studentForm.getMssv().length() > 20) {
            binding.rejectValue("mssv", "mssv.length", "MSSV phải từ 3–20 ký tự");
            hasError = true;
        } else if (studentService.existsById(studentForm.getMssv())) {
            binding.rejectValue("mssv", "error.mssv", "MSSV đã tồn tại");
            hasError = true;
        }

        // Kiểm tra Họ tên
        if (studentForm.getHoTen() == null || studentForm.getHoTen().trim().isEmpty()) {
            binding.rejectValue("hoTen", "hoTen.empty", "Họ tên không được để trống");
            hasError = true;
        } else {
            studentForm.setHoTen(studentForm.getHoTen().trim());
        }

        // Kiểm tra GPA
        if (studentForm.getDiemTongKet() < 0.0 || studentForm.getDiemTongKet() > 10.0) {
            binding.rejectValue("diemTongKet", "gpa.invalid", "Điểm tổng kết phải từ 0.0 đến 10.0");
            hasError = true;
        }

        // Nếu có lỗi → trả lại form
        if (hasError) {
            model.addAttribute("studentForm", studentForm);
            return "students/add";
        }

        studentService.create(studentForm);
        ra.addFlashAttribute("message", "Thêm sinh viên thành công!");
        return "redirect:/students";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") String id,
                               Model model,
                               RedirectAttributes ra) {
        Student s = studentService.findById(id);
        if (s == null) {
            ra.addFlashAttribute("message", "Không tìm thấy sinh viên có MSSV = " + id);
            return "redirect:/students";
        }

        model.addAttribute("studentForm", s);
        return "students/edit";
    }

    @PostMapping("/{id}/edit")
    public String doEdit(@PathVariable("id") String id,
                         @ModelAttribute("studentForm") Student studentForm,
                         BindingResult binding,
                         RedirectAttributes ra,
                         Model model) {

        if (!studentService.existsById(id)) {
            ra.addFlashAttribute("message", "Không tìm thấy sinh viên có MSSV = " + id);
            return "redirect:/students";
        }

        // validate thủ công
        boolean hasErrors = false;

        if (studentForm.getHoTen() == null || studentForm.getHoTen().trim().isEmpty()) {
            binding.rejectValue("hoTen", "error.hoTen", "Họ tên không được để trống");
            hasErrors = true;
        } else {
            studentForm.setHoTen(studentForm.getHoTen().trim());
        }

        if (studentForm.getDiemTongKet() < 0.0 || studentForm.getDiemTongKet() > 10.0) {
            binding.rejectValue("diemTongKet", "error.diemTongKet", "GPA phải nằm trong khoảng 0.0 - 10.0");
            hasErrors = true;
        }

        if (hasErrors) {
            model.addAttribute("studentForm", studentForm);
            return "students/edit";
        }

        // update dữ liệu
        Student existing = studentService.findById(id);
        existing.setHoTen(studentForm.getHoTen());
        existing.setDiemTongKet(studentForm.getDiemTongKet());
        studentService.update(existing);

        ra.addFlashAttribute("message", "Cập nhật sinh viên thành công!");
        return "redirect:/students";
    }

    @PostMapping("/{id}/delete")
    public String doDelete(@PathVariable("id") String id,
                           RedirectAttributes ra) {
        if (!studentService.existsById(id)) {
            ra.addFlashAttribute("message", "Không tìm thấy sinh viên có MSSV = " + id);
            return "redirect:/students";
        }

        studentService.delete(id);
        ra.addFlashAttribute("message", "Xóa sinh viên thành công!");
        return "redirect:/students";
    }
}
