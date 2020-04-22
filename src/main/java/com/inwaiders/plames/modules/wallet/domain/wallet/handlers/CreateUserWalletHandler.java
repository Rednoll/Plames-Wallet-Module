package com.inwaiders.plames.modules.wallet.domain.wallet.handlers;

import org.jboss.logging.Logger;

import com.inwaiders.plames.modules.wallet.domain.wallet.Wallet;
import com.inwaiders.plames.modules.wallet.domain.wallet.impl.WalletImpl;

import enterprises.inwaiders.plames.api.event.PlamesHandler;
import enterprises.inwaiders.plames.api.locale.PlamesLocale;
import enterprises.inwaiders.plames.api.user.User;
import enterprises.inwaiders.plames.domain.user.events.UserCreateEvent;

public class CreateUserWalletHandler implements PlamesHandler<UserCreateEvent> {

	@Override
	public void run(UserCreateEvent event) {
		
		User user = event.getUser();
		
		Wallet wallet = Wallet.create();
			wallet.setOwner(user);
		
		wallet.save();
		
		Logger.getLogger(this.getClass()).info(PlamesLocale.getSystemMessage("wallet.create_user_handler", user.getNickname()));
	}
}
