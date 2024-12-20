package vn.hvt.cook_master.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hvt.cook_master.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Integer> {
}
