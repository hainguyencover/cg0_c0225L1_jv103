package com.example.customermanageaspect.controller;

import com.example.customermanageaspect.exception.DuplicateEmailException;
import com.example.customermanageaspect.model.Customer;
import com.example.customermanageaspect.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private ICustomerService customerService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/jpg"
    );
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    // Hiển thị danh sách khách hàng
    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("customers", customerService.findAll());
        return "/customer/index"; // Đổi từ "/index" thành "customer/index"
    }

    // Form tạo mới khách hàng
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "/customer/create";
    }

    // Xử lý tạo mới khách hàng
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Customer customer,
                       BindingResult bindingResult,
                       @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                       Model model) throws IOException {

        // Kiểm tra validation
        if (bindingResult.hasErrors()) {
            return "/customer/create";
        }

        // Xử lý upload file ảnh
        if (avatarFile != null && !avatarFile.isEmpty()) {
            // Validate file
            String validationError = validateImageFile(avatarFile);
            if (validationError != null) {
                model.addAttribute("error", validationError);
                return "/customer/create";
            }

            String filename = storeFile(avatarFile);
            customer.setAvatar(filename);
        }

        try {
            customerService.save(customer);
            return "redirect:/customers";
        } catch (DuplicateEmailException e) {
            // Xóa file đã upload nếu có lỗi
            if (customer.getAvatar() != null) {
                deleteFile(customer.getAvatar());
            }
            model.addAttribute("error", e.getMessage());
            return "/customer/create";
        }
    }

    // Form chỉnh sửa khách hàng
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Customer> customer = customerService.findById(id);
        if (customer.isPresent()) {
            model.addAttribute("customer", customer.get());
            return "/customer/update";
        } else {
            return "redirect:/customers?error=Không+tìm+thấy+khách+hàng";
        }
    }

    // Xử lý cập nhật khách hàng
    @PostMapping("/update")
    public String update(@Valid @ModelAttribute Customer customer,
                         BindingResult bindingResult,
                         @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                         Model model) throws IOException {

        // Kiểm tra validation
        if (bindingResult.hasErrors()) {
            return "/customer/update";
        }

        // Lấy thông tin customer cũ để xóa file avatar cũ (nếu có)
        String oldAvatar = null;
        if (customer.getId() != null) {
            Optional<Customer> existingCustomer = customerService.findById(customer.getId());
            if (existingCustomer.isPresent()) {
                oldAvatar = existingCustomer.get().getAvatar();
            }
        }

        // Xử lý upload file ảnh mới
        if (avatarFile != null && !avatarFile.isEmpty()) {
            // Validate file
            String validationError = validateImageFile(avatarFile);
            if (validationError != null) {
                model.addAttribute("error", validationError);
                return "/customer/update";
            }

            String filename = storeFile(avatarFile);
            customer.setAvatar(filename);

            // Xóa file avatar cũ sau khi upload thành công
            if (oldAvatar != null && !oldAvatar.isEmpty()) {
                deleteFile(oldAvatar);
            }
        } else {
            // Giữ lại avatar cũ nếu không upload file mới
            customer.setAvatar(oldAvatar);
        }

        try {
            customerService.save(customer);
            return "redirect:/customers";
        } catch (DuplicateEmailException e) {
            // Xóa file mới đã upload nếu có lỗi
            if (customer.getAvatar() != null && !customer.getAvatar().equals(oldAvatar)) {
                deleteFile(customer.getAvatar());
                customer.setAvatar(oldAvatar); // Khôi phục avatar cũ
            }
            model.addAttribute("error", e.getMessage());
            return "/customer/update";
        }
    }

    // Xem chi tiết khách hàng
    @GetMapping("/{id}/view")
    public String view(@PathVariable Long id, Model model) {
        Optional<Customer> customer = customerService.findById(id);
        if (customer.isPresent()) {
            model.addAttribute("customer", customer.get());
            return "/customer/view";
        } else {
            return "redirect:/customers?error=Không+tìm+thấy+khách+hàng";
        }
    }

    // Form xác nhận xóa
    @GetMapping("/{id}/delete")
    public String showDeleteForm(@PathVariable Long id, Model model) {
        Optional<Customer> customer = customerService.findById(id);
        if (customer.isPresent()) {
            model.addAttribute("customer", customer.get());
            return "/customer/delete";
        } else {
            return "redirect:/customers?error=Không+tìm+thấy+khách+hàng";
        }
    }

    // Xử lý xóa khách hàng - SỬA LẠI
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            Optional<Customer> customer = customerService.findById(id);
            if (customer.isPresent()) {
                // Xóa file avatar trước khi xóa customer
                if (customer.get().getAvatar() != null) {
                    deleteFile(customer.get().getAvatar());
                }
                customerService.remove(id);
                redirect.addFlashAttribute("success", "Đã xóa khách hàng thành công!");
            } else {
                redirect.addFlashAttribute("error", "Không tìm thấy khách hàng để xóa!");
            }
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Lỗi khi xóa khách hàng: " + e.getMessage());
        }
        return "redirect:/customers";
    }

    // Xử lý exception trùng email
    @ExceptionHandler(DuplicateEmailException.class)
    public ModelAndView handleDuplicateEmail(DuplicateEmailException e) {
        ModelAndView modelAndView = new ModelAndView("inputs-not-acceptable");
        modelAndView.addObject("error", e.getMessage());
        return modelAndView;
    }

    // Phương thức hỗ trợ upload file
    private String storeFile(MultipartFile file) throws IOException {
        Files.createDirectories(Paths.get(uploadDir));
        String originalFilename = file.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        String filename = UUID.randomUUID().toString() + extension;
        Path targetPath = Paths.get(uploadDir).resolve(filename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }

    // Phương thức kiểm tra file ảnh
    private String validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }

        // Kiểm tra kích thước file
        if (file.getSize() > MAX_FILE_SIZE) {
            return "Kích thước file quá lớn. Tối đa 5MB.";
        }

        // Kiểm tra content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            return "Chỉ chấp nhận file ảnh (JPG, PNG, GIF).";
        }

        // Kiểm tra extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = originalFilename.toLowerCase();
            if (!extension.endsWith(".jpg") && !extension.endsWith(".jpeg") &&
                    !extension.endsWith(".png") && !extension.endsWith(".gif")) {
                return "Định dạng file không được hỗ trợ.";
            }
        }

        return null;
    }

    // Phương thức xóa file
    private void deleteFile(String filename) {
        try {
            if (filename != null && !filename.trim().isEmpty()) {
                Path filePath = Paths.get(uploadDir).resolve(filename);
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi xóa file: " + e.getMessage());
            // Không throw exception để không ảnh hưởng đến flow chính
        }
    }
}
