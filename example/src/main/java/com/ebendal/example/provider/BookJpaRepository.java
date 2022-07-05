package com.ebendal.example.provider;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface BookJpaRepository extends JpaRepository<Book, UUID> {
}
