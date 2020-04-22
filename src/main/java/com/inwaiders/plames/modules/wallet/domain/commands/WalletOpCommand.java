package com.inwaiders.plames.modules.wallet.domain.commands;

import com.inwaiders.plames.modules.wallet.WalletModule;
import com.inwaiders.plames.modules.wallet.domain.account.impl.CurrencyAccountBase;
import com.inwaiders.plames.modules.wallet.domain.currency.impl.CurrencyImpl;

import enterprises.inwaiders.plames.api.command.CommandException;
import enterprises.inwaiders.plames.api.messenger.profile.UserProfile;
import enterprises.inwaiders.plames.api.user.User;
import enterprises.inwaiders.plames.api.utils.DescribedFunctionResult;
import enterprises.inwaiders.plames.domain.messenger.command.MessengerCommand;
import enterprises.inwaiders.plames.domain.user.impl.UserImpl;
import enterprises.inwaiders.plames.system.utils.MessageUtils;

public class WalletOpCommand extends MessengerCommand {

	public WalletOpCommand() {
		
		this.addAliases("op");
	}
	
	@Override
	public void run(UserProfile profile, String... args) throws CommandException {
	
		User suspectAdmin = profile.getUser();
		
		if(args.length != 3) {
			
			throw new CommandException("$wallet.command.op.invalid_args");
		}
		
		String userName = args[0];
		String accountName = args[1];
		String currencySign = args[2];
		
		User user = UserImpl.getByNickname(userName);
	
		CurrencyImpl currency = CurrencyImpl.parseBySign(currencySign);
		
		if(currency == null) {
			
			throw new CommandException("$wallet.currency.not_found", currencySign);
		}
		
		CurrencyAccountBase account = CurrencyAccountBase.getByCurrencyAndName(currency, accountName, false);
	
		if(account == null) {
			
			throw new CommandException("$wallet.currency_account.not_found", accountName);
		}
		
		DescribedFunctionResult result = account.opUser(suspectAdmin, user);
	
		MessageUtils.send(WalletModule.getSystemProfile(), profile, result.getDescription());
	}
}
