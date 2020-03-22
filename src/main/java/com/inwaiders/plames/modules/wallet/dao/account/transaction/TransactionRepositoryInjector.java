package com.inwaiders.plames.modules.wallet.dao.account.transaction;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.modules.wallet.domain.account.transaction.impl.TransactionImpl;

@Service
public class TransactionRepositoryInjector {

	@Autowired
	private TransactionRepository repository;

	@PostConstruct
	private void inject() {
		
		TransactionImpl.setRepository(repository);
	}
}
