package com.zzpj.repository;

import com.zzpj.domain.Link;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {
    Optional<Link> findOneByHash(String hash);
}
