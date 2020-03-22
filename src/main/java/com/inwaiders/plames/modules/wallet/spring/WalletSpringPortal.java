package com.inwaiders.plames.modules.wallet.spring;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.modules.wallet.WalletConfiguration;

@Service
public class WalletSpringPortal {

	@Autowired
	private WalletConfiguration mainConfig;
	
	public static WalletConfiguration CONFIG = null;

	@PostConstruct
	private void statize() {
		
		CONFIG = mainConfig;
	}
}
