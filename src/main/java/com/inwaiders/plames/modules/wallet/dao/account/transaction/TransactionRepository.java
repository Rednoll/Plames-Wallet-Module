package com.inwaiders.plames.modules.wallet.dao.account.transaction;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inwaiders.plames.modules.wallet.domain.account.transaction.impl.TransactionImpl;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionImpl, Long>{

	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Override
	@Query("SELECT t FROM Transaction t WHERE t.id = :id AND t.deleted != true")
	public TransactionImpl getOne(@Param(value = "id") Long id);
	
	@Override
	@Query("SELECT t FROM Transaction t WHERE t.deleted != true")
	public List<TransactionImpl> findAll();
	
	@Override
	@Query("SELECT COUNT(*) FROM Transaction t WHERE t.deleted != true")
	public long count();
}
