package com.inwaiders.plames.modules.wallet.domain.commands;

import com.inwaiders.plames.api.command.CommandException;
import com.inwaiders.plames.api.locale.PlamesLocale;
import com.inwaiders.plames.api.messenger.profile.UserProfile;
import com.inwaiders.plames.api.user.User;
import com.inwaiders.plames.api.utils.DescribedFunctionResult;
import com.inwaiders.plames.domain.messenger.command.MessengerCommand;
import com.inwaiders.plames.modules.paygate.domain.billing.gateway.PaymentGateway;
import com.inwaiders.plames.modules.wallet.WalletModule;
import com.inwaiders.plames.modules.wallet.domain.account.CurrencyAccount;
import com.inwaiders.plames.modules.wallet.domain.account.impl.CurrencyAccountImpl;
import com.inwaiders.plames.modules.wallet.domain.bill.CurrencyBill;
import com.inwaiders.plames.modules.wallet.domain.currency.MicrotransactionCurrency;
import com.inwaiders.plames.modules.wallet.domain.currency.impl.CurrencyImpl;
import com.inwaiders.plames.modules.wallet.spring.WalletSpringPortal;
import com.inwaiders.plames.system.utils.MessageUtils;

public class WalletBuyCommand extends MessengerCommand {

	// /wallet /buy test_account_0 15 gld
	
	public WalletBuyCommand() {
		
		this.addAliases("buy");
	}
	
	@Override
	public void run(UserProfile profile, String... args) throws CommandException {
		
		User user = profile.getUser();
		PlamesLocale userLocale = profile.getUser().getLocale();
		
		String accountName = null;
		String rawAmount = null;
		String currencySign = null;
	
		if(args.length == 3) {
			
			accountName = args[0];
			rawAmount = args[1];
			currencySign = args[2];
		}
		else if(args.length == 2) {
			
			accountName = "private";
			rawAmount = args[0];
			currencySign = args[1];
		}
		
		CurrencyImpl currency = CurrencyImpl.parseBySign(currencySign);
		MicrotransactionCurrency mcCurrency = null;
				
			if(currency == null) {
				
				throw new CommandException("$wallet.currency.not_found", currencySign);
			}
		
			if(currency instanceof MicrotransactionCurrency) {
				
				mcCurrency = (MicrotransactionCurrency) currency;
			}
			else {
				
				throw new CommandException("$wallet.command.buy.not_for_sale", currencySign);
			}
			
		long currencyAmount = 0;
			
			try {
				
				currencyAmount = currency.parseAmount(rawAmount);
			}
			catch(NumberFormatException e) {
				
				throw new CommandException("$wallet.command.buy.amount_incorrect", WalletSpringPortal.CONFIG.getDelimetersRow());
			}
				
		CurrencyAccount account = CurrencyAccountImpl.parseAccount(currency, accountName, user, true);
	
			if(account == null) {
				
				throw new CommandException("$wallet.currency_account.not_found", accountName);
			}
		
		int realAmount = mcCurrency.convertToReal(currencyAmount);
			
		CurrencyBill bill = CurrencyBill.create(user, realAmount);
			bill.setDescription(userLocale.getMessage("$wallet.currency_bill.description", currency.getName(), currency.getDisplayAmount(currencyAmount), currency.getTag()));
			bill.setAccount(account);
			bill.setCurrencyAmount(currencyAmount);	
			bill.setProfile(profile);
			
		bill.save();
			
		PaymentGateway paygate = PaymentGateway.getDefault();
		
		if(paygate != null) {
			
			DescribedFunctionResult result = paygate.processBill(bill);
			
			MessageUtils.send(WalletModule.getSystemProfile(), profile, result.getDescription());
		}
		else {
		
			throw new CommandException("$wallet.paygate.not_found");
		}
	}
}
