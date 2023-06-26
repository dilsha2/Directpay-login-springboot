package lk.directpay.Login.repositories;

import lk.directpay.Login.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Role findByName(String name);
}
