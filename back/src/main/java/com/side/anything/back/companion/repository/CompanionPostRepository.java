package com.side.anything.back.companion.repository;

import com.side.anything.back.companion.entity.CompanionPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface CompanionPostRepository extends JpaRepository<CompanionPost, Long> {

    @Modifying
    @Query("DELETE FROM CompanionPost cp WHERE cp.id = :id")
    void deleteById(@NonNull @Param("id") Long id);
}
