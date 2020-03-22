package com.inwaiders.plames.modules.wallet.domain.commands;

import com.inwaiders.plames.api.command.CommandException;
import com.inwaiders.plames.api.messenger.profile.UserProfile;
import com.inwaiders.plames.api.user.User;
import com.inwaiders.plames.domain.messenger.command.MessengerCommand;
import com.inwaiders.plames.modules.wallet.WalletModule;
import com.inwaiders.plames.modules.wallet.domain.account.impl.CurrencyAccountImpl;
import com.inwaiders.plames.modules.wallet.domain.currency.impl.CurrencyImpl;
import com.inwaiders.plames.system.utils.MessageUtils;

public class WalletCreateCommand extends MessengerCommand {

	public WalletCreateCommand() {
	
		this.addAliases("create");
	}
	
	@Override
	public void run(UserProfile profile, String... args) throws CommandException {
	
		User user = profile.getUser();
		
		String accountName = args[0];
		String currencySign = args[1];
		
		CurrencyImpl currency = CurrencyImpl.parseBySign(currencySign);
	
		if(currency == null) {
			
			throw new CommandException("$wallet.currency.not_found", currencySign);
		}
		
		if(CurrencyAccountImpl.getByCurrencyAndName(currency, accountName) != null) {
			
			throw new CommandException("$wallet.command.create.account_exists", accountName, currency.getName());
		}
		
		CurrencyAccountImpl newAccount = CurrencyAccountImpl.create(currency, accountName);
			newAccount.getOwners().add(user);
	
		newAccount.save();
		
		MessageUtils.send(WalletModule.getSystemProfile(), profile, "$wallet.command.create.success", accountName, currency.getTag());
	}
}
