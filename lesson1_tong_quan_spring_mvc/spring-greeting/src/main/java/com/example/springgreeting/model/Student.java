package com.example.springgreeting.model;

public class Student {
    private String mssv;
    private String hoTen;
    private float diemTongKet;

    public Student() {
    }

    public Student(String mssv, String hoTen, float diemTongKet) {
        this.mssv = mssv;
        this.hoTen = hoTen;
        this.diemTongKet = diemTongKet;
    }

    public String getMssv() {
        return mssv;
    }

    public String getHoTen() {
        return hoTen;
    }

    public float getDiemTongKet() {
        return diemTongKet;
    }

    public void setMssv(String mssv) {
        this.mssv = mssv;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public void setDiemTongKet(float diemTongKet) {
        this.diemTongKet = diemTongKet;
    }
}
