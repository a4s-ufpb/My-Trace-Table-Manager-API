package com.ufpb.br.apps4society.my_trace_table_manager.repository;

import com.ufpb.br.apps4society.my_trace_table_manager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    UserDetails findByEmail(String email);
    Page<User> findByNameStartsWithIgnoreCase(String name, Pageable pageable);

    @Query("SELECT u FROM tb_user u WHERE u.role = 'PROFESSOR'")
    Page<User> findAllProfessors(Pageable pageable);

    Optional<User> findUserByEmail(String email);
}
