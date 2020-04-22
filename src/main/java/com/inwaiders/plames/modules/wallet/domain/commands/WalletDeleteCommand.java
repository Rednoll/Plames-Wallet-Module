package com.inwaiders.plames.modules.wallet.domain.commands;

import com.inwaiders.plames.modules.wallet.WalletModule;
import com.inwaiders.plames.modules.wallet.domain.account.impl.CurrencyAccountBase;
import com.inwaiders.plames.modules.wallet.domain.currency.impl.CurrencyImpl;

import enterprises.inwaiders.plames.api.command.CommandException;
import enterprises.inwaiders.plames.api.messenger.profile.UserProfile;
import enterprises.inwaiders.plames.domain.messenger.command.MessengerCommand;
import enterprises.inwaiders.plames.system.utils.MessageUtils;

public class WalletDeleteCommand extends MessengerCommand {

	public WalletDeleteCommand() {

		this.addAliases("delete");
	}
	
	@Override
	public void run(UserProfile profile, String... args) throws CommandException {
	
		String accountName = args[0];
		String currencySign = args[1];
		
		CurrencyImpl currency = CurrencyImpl.parseBySign(currencySign);
		
		if(currency == null) {
			
			throw new CommandException("$wallet.currency.not_found", currencySign);
		}
		
		CurrencyAccountBase account = CurrencyAccountBase.getByCurrencyAndName(currency, accountName, false);
	
		if(account == null) {
			
			throw new CommandException("$wallet.currency_account.not_found", accountName);
		}
		
		account.delete();
		
		MessageUtils.send(WalletModule.getSystemProfile(), profile, "$wallet.command.delete.success", accountName);
	}
}
