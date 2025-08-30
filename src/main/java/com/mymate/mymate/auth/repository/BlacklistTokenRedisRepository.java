package com.mymate.mymate.auth.repository;


import com.mymate.mymate.auth.entity.BlacklistTokenRedisEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistTokenRedisRepository extends CrudRepository<BlacklistTokenRedisEntity, String> {
} 