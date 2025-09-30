package com.example.springsettingemail.controller;

import com.example.springsettingemail.model.Settings;
import com.example.springsettingemail.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/settings")
public class SettingsController {

    private final SettingsService settingsService;

    // Sử dụng Constructor Injection để inject SettingsService.
    // Đây là cách được khuyến khích trong Spring.
    @Autowired
    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    /**
     * Xử lý GET request.
     *
     * @param model đối tượng để truyền dữ liệu tới view.
     * @return tên của view template (settings.html).
     */
    @GetMapping
    public String showSettingsPage(Model model) {
        // Chuẩn bị dữ liệu cho view
        List<String> languages = Arrays.asList("English", "Vietnamese", "Japanese", "Chinese");
        List<Integer> pageSizes = Arrays.asList(5, 10, 15, 25, 50, 100);

        // Thêm các thuộc tính vào model để view có thể truy cập
        model.addAttribute("settings", settingsService.getSettings());
        model.addAttribute("languages", languages);
        model.addAttribute("pageSizes", pageSizes);

        // Trả về tên của file view (không có đuôi .html)
        return "settings";
    }

    /**
     * Xử lý POST request.
     *
     * @param settings Spring sẽ tự động binding dữ liệu từ form vào đối tượng Settings này.
     * @return redirect về trang settings.
     */
    @PostMapping
    public String updateSettings(@ModelAttribute Settings settings) {
        // Spring đã tự động tạo và set giá trị cho đối tượng settings từ form.
        // Chỉ cần gọi service để cập nhật.
        settingsService.updateSettings(settings);

        // Redirect về trang /settings bằng GET request.
        return "redirect:/settings";
    }
}
