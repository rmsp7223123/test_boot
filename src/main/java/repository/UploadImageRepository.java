package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import domain.entity.UploadImage;

@Repository
public interface UploadImageRepository extends JpaRepository<UploadImage, Long> {
}
