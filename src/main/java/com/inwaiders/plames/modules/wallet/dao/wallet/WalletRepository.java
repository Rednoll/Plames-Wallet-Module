package com.inwaiders.plames.modules.wallet.dao.wallet;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inwaiders.plames.api.user.User;
import com.inwaiders.plames.modules.wallet.domain.wallet.impl.WalletImpl;

@Repository
public interface WalletRepository extends JpaRepository<WalletImpl, Long>{

	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Override
	@Query("SELECT w FROM Wallet w WHERE w.id = :id AND w.deleted != true")
	public WalletImpl getOne(@Param(value = "id") Long id);
	
	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Query("SELECT w FROM Wallet w WHERE w.owner = :user AND w.deleted != true")
	public WalletImpl getByOwner(@Param(value = "user") User user);
	
	@Override
	@Query("SELECT w FROM Wallet w WHERE w.deleted != true")
	public List<WalletImpl> findAll();
	
	@Override
	@Query("SELECT COUNT(*) FROM Wallet w WHERE w.deleted != true")
	public long count();
}
