package com.inwaiders.plames.modules.wallet.domain.commands;

import com.inwaiders.plames.modules.wallet.WalletModule;
import com.inwaiders.plames.modules.wallet.domain.account.CurrencyAccount;
import com.inwaiders.plames.modules.wallet.domain.account.impl.CurrencyAccountBase;
import com.inwaiders.plames.modules.wallet.domain.account.transaction.impl.TransactionImpl;
import com.inwaiders.plames.modules.wallet.domain.currency.impl.CurrencyImpl;
import com.inwaiders.plames.modules.wallet.spring.WalletSpringPortal;

import enterprises.inwaiders.plames.api.command.CommandException;
import enterprises.inwaiders.plames.api.messenger.profile.UserProfile;
import enterprises.inwaiders.plames.api.user.User;
import enterprises.inwaiders.plames.api.utils.DescribedFunctionResult;
import enterprises.inwaiders.plames.domain.messenger.command.MessengerCommand;
import enterprises.inwaiders.plames.system.utils.MessageUtils;

public class WalletTransactionCommand extends MessengerCommand {

	public WalletTransactionCommand() {
	
		this.addAliases("trans", "transaction");
	}
	
//	/wallet /trans private Banana 172,9 gld
	
	@Override
	public void run(UserProfile profile, String... args) throws CommandException {
		
		User user = profile.getUser();
		
		String sourceName = args[0];
		String destinationName = args[1];
		String rawAmount = args[2];
		String currencySign = args[3];
		
		CurrencyImpl currency = CurrencyImpl.parseBySign(currencySign);
			
		if(currency == null) {
			
			throw new CommandException("$wallet.currency.not_found", currencySign);
		}
	
		long amount = 0;
		
			try {
				
				amount = currency.parseAmount(rawAmount);
			}
			catch(NumberFormatException e) {
				
				throw new CommandException("$wallet.command.exception.amount_incorrect", WalletSpringPortal.CONFIG.getDelimetersRow());
			}
		
		CurrencyAccount sourceAccount = CurrencyAccountBase.parseAccount(currency, sourceName, user, true);
			
			if(sourceAccount == null) {
				
				throw new CommandException("$wallet.currency_account.not_found", sourceName);
			}
		
		CurrencyAccount destinationAccount = CurrencyAccountBase.parseAccount(currency, destinationName, user, true);
			
			if(destinationAccount == null) {
				
				throw new CommandException("$wallet.currency_account.not_found", destinationName);
			}
		
		TransactionImpl transaction = TransactionImpl.create();
			transaction.setAmount(amount);
			transaction.setSource(sourceAccount);
			transaction.setDestination(destinationAccount);
			transaction.setCurrency(currency);
			transaction.setSender(user);
			
		transaction.save();
		
		DescribedFunctionResult result = transaction.make();
		
		MessageUtils.send(WalletModule.getSystemProfile(), profile, result.getDescription());
		
		transaction.save();
	}
}
