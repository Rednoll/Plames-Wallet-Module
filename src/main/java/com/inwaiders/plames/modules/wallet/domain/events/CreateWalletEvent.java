package com.inwaiders.plames.modules.wallet.domain.events;

import com.inwaiders.plames.api.event.PlamesEvent;
import com.inwaiders.plames.modules.wallet.domain.wallet.Wallet;

public class CreateWalletEvent implements PlamesEvent {

	private Wallet wallet = null;
	
	public CreateWalletEvent() {
	
	}
	
	public CreateWalletEvent(Wallet wallet) {
		
		this.wallet = wallet;
	}
	
	@Override
	public void dispose() {
		
		this.wallet = null;
	}

	public Wallet getWallet() {
		
		return this.wallet;
	}
	
	@Override
	public boolean getAutoDispose() {
		
		return true;
	}
}
