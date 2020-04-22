package com.inwaiders.plames.modules.wallet.domain.currency.handlers;

import java.util.List;

import com.inwaiders.plames.modules.wallet.domain.account.CurrencyAccount;
import com.inwaiders.plames.modules.wallet.domain.account.PrivateCurrencyAccount;
import com.inwaiders.plames.modules.wallet.domain.currency.Currency;
import com.inwaiders.plames.modules.wallet.domain.currency.impl.CurrencyImpl;
import com.inwaiders.plames.modules.wallet.domain.events.CreateWalletEvent;
import com.inwaiders.plames.modules.wallet.domain.wallet.Wallet;

import enterprises.inwaiders.plames.api.event.PlamesHandler;
import enterprises.inwaiders.plames.api.user.User;

public class CreateWalletCurrencyHandler implements PlamesHandler<CreateWalletEvent>{

	@Override
	public void run(CreateWalletEvent event) {
		
		Wallet wallet = event.getWallet();
		User user = wallet.getOwner();
		
		List<CurrencyImpl> currencies = CurrencyImpl.getAll();
	
		for(Currency currency : currencies) {
			
			PrivateCurrencyAccount account = (PrivateCurrencyAccount) CurrencyAccount.create(currency, user.getNickname(), "private");
				account.setOwner(user);
			
			wallet.addPrivateAccount(account);
		}
	}
}