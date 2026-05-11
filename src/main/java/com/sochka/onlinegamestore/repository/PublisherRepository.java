package com.sochka.onlinegamestore.repository;

import com.sochka.onlinegamestore.domain.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, UUID> {
    List<Publisher> findByNameContainingIgnoreCase(String namePart);
}
