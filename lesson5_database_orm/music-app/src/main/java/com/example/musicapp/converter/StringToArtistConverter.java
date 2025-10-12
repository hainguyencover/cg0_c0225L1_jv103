package com.example.musicapp.converter;

import com.example.musicapp.model.Artist;
import com.example.musicapp.service.IArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class StringToArtistConverter implements Converter<String, Artist> {

    @Autowired
    private IArtistService artistService;

    @Override
    public Artist convert(String source) {
        // source chính là chuỗi ID được gửi từ form
        try {
            Long id = Long.parseLong(source);
            // Dùng service để tìm Artist từ ID và trả về
            return artistService.findById(id);
        } catch (NumberFormatException e) {
            // Xử lý trường hợp source không phải là số (nếu có)
            return null;
        }
    }
}
