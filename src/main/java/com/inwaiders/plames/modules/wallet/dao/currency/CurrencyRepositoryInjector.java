package com.inwaiders.plames.modules.wallet.dao.currency;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.modules.wallet.domain.currency.impl.CurrencyImpl;

@Service
public class CurrencyRepositoryInjector {

	@Autowired
	private CurrencyRepository repository;

	@PostConstruct
	private void inject() {
		
		CurrencyImpl.setRepository(repository);
	}
}
