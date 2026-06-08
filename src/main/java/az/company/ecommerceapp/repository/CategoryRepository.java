package az.company.ecommerceapp.repository;

import az.company.ecommerceapp.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByActiveTrueOrderByDisplayOrderAscNameAsc();

    Optional<Category> findBySlugAndActiveTrue(String slug);
}
