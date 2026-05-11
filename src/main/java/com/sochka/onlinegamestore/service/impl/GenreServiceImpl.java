package com.sochka.onlinegamestore.service.impl;

import com.sochka.onlinegamestore.dto.GenreDTO;
import com.sochka.onlinegamestore.repository.GenreRepository;
import com.sochka.onlinegamestore.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenreServiceImpl implements GenreService {

    private final GenreRepository repository;

    @Override
    public List<GenreDTO> findAll() {
        return repository.findAll().stream()
                .map(g -> GenreDTO.builder()
                        .id(g.getId())
                        .name(g.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GenreDTO createGenre(String name) {
        if (name != null && repository.existsByNameIgnoreCase(name.trim())) {
            throw new IllegalArgumentException("Duplicate Prevention: A genre with identity '" + name.trim() + "' already exists.");
        }
        com.sochka.onlinegamestore.domain.Genre g = com.sochka.onlinegamestore.domain.Genre.builder()
                .name(name.trim())
                .build();
        g = repository.save(g);
        return GenreDTO.builder().id(g.getId()).name(g.getName()).build();
    }

    @Override
    @Transactional
    public GenreDTO updateGenre(UUID id, String name) {
        com.sochka.onlinegamestore.domain.Genre g = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found"));
        
        String trimmed = name != null ? name.trim() : "";
        if (!trimmed.isEmpty() && !trimmed.equalsIgnoreCase(g.getName())) {
            if (repository.existsByNameIgnoreCase(trimmed)) {
                throw new IllegalArgumentException("Merge Conflict: The specified genre title is currently claimed by an alternate entity.");
            }
            g.setName(trimmed);
        }
        g = repository.save(g);
        return GenreDTO.builder().id(g.getId()).name(g.getName()).build();
    }

    @Override
    @Transactional
    public void deleteGenre(UUID id) {
        repository.deleteById(id);
    }
}
