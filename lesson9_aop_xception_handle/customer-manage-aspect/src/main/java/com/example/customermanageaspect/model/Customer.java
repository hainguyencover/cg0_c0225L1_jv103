package com.example.customermanageaspect.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "customers", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên không được để trống")
    @Size(min = 2, max = 100, message = "Tên phải từ 2 đến 100 ký tự")
    private String name;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    @Column(nullable = false, unique = true)
    private String email;

    @Pattern(regexp = "^(|\\+?[0-9\\s\\-\\(\\)]{10,})$", message = "Số điện thoại không hợp lệ")
    @Size(max = 20, message = "Số điện thoại không quá 20 ký tự")
    private String phone;

    private String avatar;

    // Constructors
    public Customer() {
    }

    public Customer(String name, String email, String phone, String avatar) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.avatar = avatar;
    }

    // Getters and Setters (giữ nguyên)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
