package vn.hvt.cook_master.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hvt.cook_master.entity.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like, Integer> {
}