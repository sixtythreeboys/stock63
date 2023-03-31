package com.sixtythree.stock63.domestic.kospi.repository;

import com.sixtythree.stock63.domestic.kospi.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {

}
