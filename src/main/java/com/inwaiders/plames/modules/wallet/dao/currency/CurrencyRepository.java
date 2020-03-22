package com.inwaiders.plames.modules.wallet.dao.currency;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inwaiders.plames.modules.wallet.domain.currency.impl.CurrencyImpl;

@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyImpl, Long>{

	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Override
	@Query("SELECT c FROM Currency c WHERE c.id = :id AND c.deleted != true")
	public CurrencyImpl getOne(@Param(value = "id") Long id);
	
	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Query("SELECT c FROM Currency c WHERE c.tag = :tag AND c.deleted != true")
	public CurrencyImpl getByTag(@Param(value = "tag") String tag);

	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Query("SELECT c FROM Currency c WHERE c.name = :name AND c.deleted != true")
	public CurrencyImpl getByName(@Param(value = "name") String name);
	
	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Query("SELECT c FROM Currency c WHERE c.code = :code AND c.deleted != true")
	public CurrencyImpl getByCode(@Param(value = "code") byte code);
	
	@Override
	@Query("SELECT c FROM Currency c WHERE c.deleted != true")
	public List<CurrencyImpl> findAll();
	
	@Override
	@Query("SELECT COUNT(*) FROM Currency c WHERE c.deleted != true")
	public long count();
}
