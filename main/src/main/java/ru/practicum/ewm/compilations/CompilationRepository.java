package ru.practicum.ewm.compilations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.compilations.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    Page<Compilation> findAll(Pageable page);

    Page<Compilation> findByPinnedIs(boolean pinned, Pageable pageable);
}
