package com.sochka.onlinegamestore.service.impl;

import com.sochka.onlinegamestore.dto.PublisherDTO;
import com.sochka.onlinegamestore.repository.PublisherRepository;
import com.sochka.onlinegamestore.service.PublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublisherServiceImpl implements PublisherService {

    private final PublisherRepository repository;

    @Override
    public List<PublisherDTO> findAll() {
        return repository.findAll().stream()
                .map(p -> PublisherDTO.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .website(p.getWebsite())
                        .supportEmail(p.getSupportEmail())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PublisherDTO createPublisher(String name, String website, String supportEmail) {
        com.sochka.onlinegamestore.domain.Publisher p = com.sochka.onlinegamestore.domain.Publisher.builder()
                .name(name)
                .website(website)
                .supportEmail(supportEmail)
                .build();
        p = repository.save(p);
        return PublisherDTO.builder().id(p.getId()).name(p.getName()).website(p.getWebsite()).supportEmail(p.getSupportEmail()).build();
    }

    @Override
    @Transactional
    public PublisherDTO updatePublisher(java.util.UUID id, String name, String website, String supportEmail) {
        com.sochka.onlinegamestore.domain.Publisher p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found"));
        p.setName(name);
        p.setWebsite(website);
        p.setSupportEmail(supportEmail);
        p = repository.save(p);
        return PublisherDTO.builder().id(p.getId()).name(p.getName()).website(p.getWebsite()).supportEmail(p.getSupportEmail()).build();
    }

    @Override
    @Transactional
    public void deletePublisher(java.util.UUID id) {
        repository.deleteById(id);
    }
}
