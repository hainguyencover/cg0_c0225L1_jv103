package com.example.springgreeting.model;

public class Student {
    private String mssv;
    private String hoTen;
    private double diemTongKet;

    public Student() {
    }

    public Student(String mssv, String hoTen, double diemTongKet) {
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

    public double getDiemTongKet() {
        return diemTongKet;
    }

    public void setMssv(String mssv) {
        this.mssv = mssv;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public void setDiemTongKet(double diemTongKet) {
        this.diemTongKet = diemTongKet;
    }

    public XepLoai getXepLoai() {
        return XepLoai.fromGpa(diemTongKet);
    }
}
