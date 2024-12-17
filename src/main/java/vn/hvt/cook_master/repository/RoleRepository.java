package vn.hvt.cook_master.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hvt.cook_master.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
}