package com.example.springsettingemail.service;

import com.example.springsettingemail.model.Settings;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {
    private Settings currentSettings;

    public SettingsService() {
        // Khởi tạo cấu hình mặc định
        currentSettings = new Settings("Vietnamese", 25, true, "Cheers,\nYour Name");
    }

    /**
     * Lấy thông tin cấu hình hiện tại.
     * @return đối tượng Settings.
     */
    public Settings getSettings() {
        return this.currentSettings;
    }

    /**
     * Cập nhật thông tin cấu hình.
     * @param newSettings đối tượng Settings chứa thông tin mới.
     */
    public void updateSettings(Settings newSettings) {
        this.currentSettings = newSettings;
        System.out.println("Settings updated via Spring Service: " + newSettings);
    }
}
