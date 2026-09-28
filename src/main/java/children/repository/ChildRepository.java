package children.repository;

import children.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ChildRepository extends JpaRepository<Child, UUID> {
    boolean existsByCode(String code);
}