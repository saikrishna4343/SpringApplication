package com.moblileApplication.app.ws.UserRepository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.moblileApplication.app.ws.Entity.UserEntity;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {
	UserEntity findUserByEmail(String email);
	UserEntity findUserByUserId(String userId);
	
	@Transactional
	@Modifying
	@Query("Delete from users u where u.userId=:userId")
	void deleteByUserId(String userId);
}
