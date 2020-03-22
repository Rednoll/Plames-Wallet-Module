package com.inwaiders.plames.modules.wallet.domain.wallet.handlers;

import java.util.List;

import com.inwaiders.plames.api.event.PlamesHandler;
import com.inwaiders.plames.api.user.User;
import com.inwaiders.plames.modules.wallet.domain.account.CurrencyAccount;
import com.inwaiders.plames.modules.wallet.domain.currency.Currency;
import com.inwaiders.plames.modules.wallet.domain.events.CreateCurrencyEvent;
import com.inwaiders.plames.modules.wallet.domain.wallet.impl.WalletImpl;

public class CreateCurrencyWalletHandler implements PlamesHandler<CreateCurrencyEvent>{

	@Override
	public void run(CreateCurrencyEvent event) {
	
		Currency currency = event.getCurrency();

		List<WalletImpl> wallets = WalletImpl.getAll();
		
		for(WalletImpl wallet : wallets) {
			
			User owner = wallet.getOwner();
			
			CurrencyAccount account = CurrencyAccount.create(currency, owner.getNickname());
				account.getOwners().add(owner);
				
			wallet.addPrivateAccount(account);
		}
	}
}
