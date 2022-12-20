package com.shopme.admin.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.shopme.common.entity.User;

public interface UserRepository extends PagingAndSortingRepository<User, Integer> {
	
	@Query("SELECT u FROM User u WHERE u.email = :email")
	public User getUserByEmail(@Param("email") String email);
	
	public Long countById(Integer Id);
	
	@Query("SELECT u FROM User u WHERE u.firstName Like %?1% or u.lastName Like %?1% or u.email Like %?1%")
	public Page<User> findAll(String keyword, Pageable pageable);
	
	
	@Query("UPDATE User u SET u.enabled  = ?2 WHERE u.id = ?1")
	@Modifying
	public void updateEnableStatus(Integer Id, boolean enabled);
	
}
