package com.inwaiders.plames.modules.wallet.dao.account;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.inwaiders.plames.modules.wallet.domain.account.impl.CurrencyAccountBase;
import com.inwaiders.plames.modules.wallet.domain.currency.impl.CurrencyImpl;

public interface CurrencyAccountRepository extends JpaRepository<CurrencyAccountBase, Long>{

	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Override
	@Query("SELECT ca FROM CurrencyAccount ca WHERE ca.id = :id AND ca.deleted != true")
	public CurrencyAccountBase getOne(@Param(value = "id") Long id);
	
	@Override
	@Query("SELECT ca FROM CurrencyAccount ca WHERE ca.deleted != true")
	public List<CurrencyAccountBase> findAll();
	
	@Query("SELECT ca FROM CurrencyAccount ca WHERE ca.currency = :cur AND ca.deleted != true")
	public List<CurrencyAccountBase> getByCurrency(@Param(value="cur") CurrencyImpl currency);
	
	@Query("SELECT ca FROM CurrencyAccount ca WHERE ca.currency = :cur AND ca.name = :name AND ca.deleted != true")
	public CurrencyAccountBase getByCurrencyAndName(@Param(value="cur") CurrencyImpl currency, @Param(value="name") String name);
	
	@Override
	@Query("SELECT COUNT(*) FROM CurrencyAccount ca WHERE ca.deleted != true")
	public long count();
}
