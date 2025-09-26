package com.example.springgreeting.model;

public enum XepLoai {
    XUAT_SAC("Xuất sắc"),
    GIOI("Giỏi"),
    KHA("Khá"),
    TRUNG_BINH("Trung bình"),
    YEU("Yếu"),
    KEM("Kém");

    private final String label;

    XepLoai(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static XepLoai fromGpa(double gpa) {
        if (gpa >= 9.0) return XepLoai.XUAT_SAC;
        if (gpa >= 8.0) return XepLoai.GIOI;
        if (gpa >= 6.5) return XepLoai.KHA;
        if (gpa >= 5.0) return XepLoai.TRUNG_BINH;
        if (gpa >= 4.0) return XepLoai.YEU;
        return XepLoai.KEM;
    }
}
