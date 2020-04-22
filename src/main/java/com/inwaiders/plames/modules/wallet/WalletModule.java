package com.inwaiders.plames.modules.wallet;

import java.util.List;

import org.jboss.logging.Logger;

import com.inwaiders.plames.modules.wallet.domain.account.CurrencyAccount;
import com.inwaiders.plames.modules.wallet.domain.account.CurrencyAccountHlRepository;
import com.inwaiders.plames.modules.wallet.domain.account.PrivateCurrencyAccount;
import com.inwaiders.plames.modules.wallet.domain.account.impl.CurrencyAccountBase;
import com.inwaiders.plames.modules.wallet.domain.account.transaction.impl.TransactionImpl;
import com.inwaiders.plames.modules.wallet.domain.account.transaction.strategy.impl.SpringTransactionStrategy;
import com.inwaiders.plames.modules.wallet.domain.commands.WalletCommand;
import com.inwaiders.plames.modules.wallet.domain.currency.CurrencyHlRepository;
import com.inwaiders.plames.modules.wallet.domain.currency.handlers.CreateWalletCurrencyHandler;
import com.inwaiders.plames.modules.wallet.domain.currency.impl.CurrencyImpl;
import com.inwaiders.plames.modules.wallet.domain.events.CreateCurrencyEvent;
import com.inwaiders.plames.modules.wallet.domain.events.CreateWalletEvent;
import com.inwaiders.plames.modules.wallet.domain.wallet.Wallet;
import com.inwaiders.plames.modules.wallet.domain.wallet.WalletHlRepository;
import com.inwaiders.plames.modules.wallet.domain.wallet.handlers.CreateCurrencyWalletHandler;
import com.inwaiders.plames.modules.wallet.domain.wallet.handlers.CreateUserWalletHandler;
import com.inwaiders.plames.modules.wallet.domain.wallet.impl.WalletImpl;

import enterprises.inwaiders.plames.api.command.CommandRegistry;
import enterprises.inwaiders.plames.api.event.EventEngine;
import enterprises.inwaiders.plames.api.event.EventStage;
import enterprises.inwaiders.plames.api.user.User;
import enterprises.inwaiders.plames.domain.messenger.profile.impl.SystemProfile;
import enterprises.inwaiders.plames.domain.module.impl.ModuleBase;
import enterprises.inwaiders.plames.domain.user.events.UserCreateEvent;
import enterprises.inwaiders.plames.domain.user.impl.UserImpl;
import enterprises.inwaiders.plames.modules.webcontroller.domain.module.BaseWebDescription;
import enterprises.inwaiders.plames.modules.webcontroller.domain.module.WebDescribedModule;
import enterprises.inwaiders.plames.modules.webcontroller.domain.module.WebDescription;
import enterprises.inwaiders.plames.modules.webcontroller.domain.module.button.Button;
import enterprises.inwaiders.plames.spring.ApplicationContextProvider;

public class WalletModule extends ModuleBase implements WebDescribedModule {

	private static WalletModule INSTANCE = new WalletModule();
	
	private BaseWebDescription webDescription = new BaseWebDescription();
	
	public WalletModule() {
		
		Button walletCurrencies = new Button();
			walletCurrencies.setName("Список валют");
			walletCurrencies.setFontColor("#7892A3");
			walletCurrencies.setBackgroundColor("#BAE1FF");
			walletCurrencies.setBordersColor("#9EBFD8");
			walletCurrencies.setTargetPage("/wallet/currencies");
	
		webDescription.addButton(walletCurrencies);
	}
	
	@Override
	public void preInit() {
	
		WalletHlRepository.setInstance(new WalletImpl.HighLevelRepository());
		CurrencyHlRepository.setRepository(new CurrencyImpl.HighLevelRepository());
		CurrencyAccountHlRepository.setRepository(new CurrencyAccountBase.HighLevelRepository());
	
		CommandRegistry registry = CommandRegistry.getDefaultRegistry();
			registry.registerCommand(new WalletCommand());
	}
	
	@Override
	public void init() {
		
		EventEngine eventEngine = EventEngine.getCommonEngine();
			eventEngine.register(UserCreateEvent.class, EventStage.POST, new CreateUserWalletHandler());
			eventEngine.register(CreateCurrencyEvent.class, EventStage.POST, new CreateCurrencyWalletHandler());
			eventEngine.register(CreateWalletEvent.class, EventStage.POST, new CreateWalletCurrencyHandler());
			
		TransactionImpl.strategy = ApplicationContextProvider.getApplicationContext().getBean(SpringTransactionStrategy.class);
	}
	
	@Override
	public void postInit() {
		
		List<UserImpl> users = UserImpl.getAll();
		
			users.removeIf(user -> WalletImpl.getByOwner(user) != null);
	
		for(UserImpl user : users) {
			
			Wallet wallet = Wallet.create();
				wallet.setOwner(user);
				
			wallet.save();
			
			Logger.getLogger(this.getClass()).debug("Create wallet for "+user.getNickname());
		}
		
		///
	
		List<CurrencyImpl> currencies = CurrencyImpl.getAll();
		List<WalletImpl> wallets = WalletImpl.getAll();
	
		for(WalletImpl wallet : wallets) {
			
			User owner = wallet.getOwner();
			
			for(CurrencyImpl currency : currencies) {
				
				if(wallet.getPrivateAccount(currency) == null) {
					
					PrivateCurrencyAccount account = (PrivateCurrencyAccount) CurrencyAccount.create(currency, owner.getNickname(), "private");
						account.setOwner(owner);
						
					account.save();
						
					wallet.addPrivateAccount(account);
					
					wallet.save();
					
					Logger.getLogger(this.getClass()).debug("Create private currency account("+account.getCurrency().getName()+") for "+owner.getNickname());
				}
			}
		}
	}

	public WebDescription getWebDescription() {
		
		return this.webDescription;
	}
	
	@Override
	public String getName() {
	
		return "Wallet";
	}

	@Override
	public String getVersion() {
		
		return "1V";
	}

	@Override
	public String getDescription() {
	
		return "Модуль \"Wallet\" позволяет создавать валюты для ваших проектов.";
	}

	@Override
	public String getType() {
	
		return "functional";
	}

	@Override
	public String getLicenseKey() {
		
		return null;
	}

	@Override
	public long getSystemVersion() {
	
		return 0;
	}

	@Override
	public long getId() {

		return 891263;
	}
	
	public static SystemProfile getSystemProfile() {
		
		return INSTANCE.getProfile();
	}
	
	public static WalletModule getInstance() {
		
		return INSTANCE;
	}
}
