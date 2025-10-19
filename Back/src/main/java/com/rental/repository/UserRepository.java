package com.rental.repository;

import com.rental.model.DBUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<DBUser, Integer> {

    Optional<DBUser> findByEmail(String email);
}
