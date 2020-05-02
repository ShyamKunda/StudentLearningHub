package inquerro.repository;

import inquerro.model.ImageModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepositoy extends JpaRepository<ImageModel, Long> {

    Optional<ImageModel> findByName(String name);
}
