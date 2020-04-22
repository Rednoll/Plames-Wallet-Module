package com.inwaiders.plames.modules.wallet.domain.wallet.handlers;

import java.util.List;

import com.inwaiders.plames.modules.wallet.domain.account.CurrencyAccount;
import com.inwaiders.plames.modules.wallet.domain.account.PrivateCurrencyAccount;
import com.inwaiders.plames.modules.wallet.domain.currency.Currency;
import com.inwaiders.plames.modules.wallet.domain.events.CreateCurrencyEvent;
import com.inwaiders.plames.modules.wallet.domain.wallet.impl.WalletImpl;

import enterprises.inwaiders.plames.api.event.PlamesHandler;
import enterprises.inwaiders.plames.api.user.User;

public class CreateCurrencyWalletHandler implements PlamesHandler<CreateCurrencyEvent>{

	@Override
	public void run(CreateCurrencyEvent event) {
	
		Currency currency = event.getCurrency();

		List<WalletImpl> wallets = WalletImpl.getAll();
		
		for(WalletImpl wallet : wallets) {
			
			User owner = wallet.getOwner();
			
			PrivateCurrencyAccount account = (PrivateCurrencyAccount) CurrencyAccount.create(currency, owner.getNickname(), "private");
				account.setOwner(owner);
				
			account.save();
				
			wallet.addPrivateAccount(account);
		
			wallet.save();
		}
	}
}
