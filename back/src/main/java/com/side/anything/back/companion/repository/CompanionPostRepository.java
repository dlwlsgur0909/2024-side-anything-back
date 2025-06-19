package com.side.anything.back.companion.repository;

import com.side.anything.back.companion.entity.CompanionPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface CompanionPostRepository extends JpaRepository<CompanionPost, Long> {

    @Query(
            value = """
                    SELECT cp FROM CompanionPost cp
                    WHERE
                        cp.title LIKE %:keyword%
                        OR cp.location LIKE  %:keyword%
                    ORDER BY
                        cp.id DESC
                    """,
            countQuery = """
                        SELECT COUNT(cp) FROM CompanionPost cp
                        WHERE
                            cp.title LIKE %:keyword%
                            OR cp.location LIKE %:keyword%
                        """
    )
    Page<CompanionPost> findPagedList(@Param("keyword") String keyword,
                                      Pageable pageable);

    @Query("SELECT cp FROM CompanionPost cp JOIN FETCH cp.member m WHERE cp.id = :id")
    Optional<CompanionPost> findDetailById(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM CompanionPost cp WHERE cp.id = :id")
    void deleteById(@NonNull @Param("id") Long id);

}
