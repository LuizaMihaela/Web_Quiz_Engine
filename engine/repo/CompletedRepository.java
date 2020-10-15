package engine.repo;

import engine.model.Completed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompletedRepository extends JpaRepository<Completed, String> {
    Page<Completed> findByAuthor(String author, Pageable pageable);
}
