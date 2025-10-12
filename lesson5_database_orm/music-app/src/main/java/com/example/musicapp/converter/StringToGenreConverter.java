package com.example.musicapp.converter;

import com.example.musicapp.model.Genre;
import com.example.musicapp.service.IGenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class StringToGenreConverter implements Converter<String, Genre> {

    @Autowired
    private IGenreService genreService;

    @Override
    public Genre convert(String source) {
        try {
            Long id = Long.parseLong(source);
            return genreService.findById(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
