package com.inwaiders.plames.modules.wallet.dao.wallet;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.modules.wallet.domain.wallet.impl.WalletImpl;

@Service
public class WalletRepositoryInjector {

	@Autowired
	private WalletRepository repository;

	@PostConstruct
	private void injector() {
		
		WalletImpl.setRepository(repository);
	}
}
