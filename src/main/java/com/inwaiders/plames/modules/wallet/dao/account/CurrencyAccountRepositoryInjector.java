package com.inwaiders.plames.modules.wallet.dao.account;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.modules.wallet.domain.account.impl.CurrencyAccountBase;

@Service
public class CurrencyAccountRepositoryInjector {

	@Autowired
	private CurrencyAccountRepository repository;

	@PostConstruct
	private void inject() {
		
		CurrencyAccountBase.setRepository(repository);
	}
}