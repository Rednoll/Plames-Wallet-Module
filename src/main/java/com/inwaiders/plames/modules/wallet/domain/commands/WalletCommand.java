package com.inwaiders.plames.modules.wallet.domain.commands;

import java.util.Collection;

import com.inwaiders.plames.modules.wallet.WalletModule;
import com.inwaiders.plames.modules.wallet.domain.account.CurrencyAccount;
import com.inwaiders.plames.modules.wallet.domain.account.impl.CurrencyAccountBase;
import com.inwaiders.plames.modules.wallet.domain.currency.Currency;
import com.inwaiders.plames.modules.wallet.domain.currency.impl.CurrencyImpl;
import com.inwaiders.plames.modules.wallet.domain.wallet.Wallet;
import com.inwaiders.plames.modules.wallet.domain.wallet.impl.WalletImpl;

import enterprises.inwaiders.plames.api.command.CommandException;
import enterprises.inwaiders.plames.api.messenger.profile.UserProfile;
import enterprises.inwaiders.plames.api.user.User;
import enterprises.inwaiders.plames.domain.messenger.command.MessengerCommand;
import enterprises.inwaiders.plames.system.utils.MessageUtils;

public class WalletCommand extends MessengerCommand {

	public WalletCommand() {
	
		this.addAliases("wallet", "w");
		
		this.addChildCommand(new WalletTransactionCommand());
		this.addChildCommand(new WalletCreateCommand());
		this.addChildCommand(new WalletDeleteCommand());
		this.addChildCommand(new WalletOpCommand());
		this.addChildCommand(new WalletDeopCommand());
		this.addChildCommand(new WalletBuyCommand());
	}
	
	@Override
	public void run(UserProfile profile, String... args) throws CommandException {
	
		User user = profile.getUser();
		
		if(args.length == 0) {
		
			Wallet wallet = WalletImpl.getByOwner(user);
		
			if(wallet != null) {
				
				StringBuilder builder = new StringBuilder();
				
				builder.append(user.getNickname()+"'s wallet:\n");
				
				Collection<CurrencyAccount> accounts = wallet.getPrivateAccounts();
			
				for(CurrencyAccount account : accounts) {
					
					Currency currency = account.getCurrency();
			
					String name = currency.getName();
					String tag = currency.getTag();
					
					String amount = account.getDisplayBalance();
				
					builder.append(name+": "+amount+" "+tag+"\n");
				}
				
				MessageUtils.send(WalletModule.getSystemProfile(), profile, builder.toString().trim());
			}
			else {
				
				//TODO: REPORT
			}
		}
		
		if(args.length == 2) {
			
			String accountName = args[0];
			String currencySign = args[1];
		
			CurrencyImpl currency = CurrencyImpl.parseBySign(currencySign);
		
			if(currency == null) {
				
				throw new CommandException("Currency "+currencySign+" not found!");
			}
			
			CurrencyAccountBase account = CurrencyAccountBase.getByCurrencyAndName(currency, accountName);
			
			if(account == null) {
				
				throw new CommandException("Currency account "+accountName+" not found!");
			}
	
			String tag = currency.getTag();		
			String amount = account.getDisplayBalance();
					
			MessageUtils.send(WalletModule.getSystemProfile(), profile, accountName+": "+amount+" "+tag);
		}
	}
}
