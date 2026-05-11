package com.sochka.onlinegamestore.service;

import com.sochka.onlinegamestore.dto.PublisherDTO;
import java.util.List;

public interface PublisherService {
    List<PublisherDTO> findAll();
    PublisherDTO createPublisher(String name, String website, String supportEmail);
    PublisherDTO updatePublisher(java.util.UUID id, String name, String website, String supportEmail);
    void deletePublisher(java.util.UUID id);
}
