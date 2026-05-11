package com.sochka.onlinegamestore.service;

import com.sochka.onlinegamestore.dto.GenreDTO;
import java.util.List;
import java.util.UUID;

public interface GenreService {
    List<GenreDTO> findAll();
    GenreDTO createGenre(String name);
    GenreDTO updateGenre(UUID id, String name);
    void deleteGenre(UUID id);
}
