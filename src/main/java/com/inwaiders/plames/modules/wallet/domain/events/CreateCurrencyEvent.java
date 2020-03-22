package com.inwaiders.plames.modules.wallet.domain.events;

import com.inwaiders.plames.api.event.PlamesEvent;
import com.inwaiders.plames.modules.wallet.domain.currency.Currency;

public class CreateCurrencyEvent implements PlamesEvent {

	private Currency currency = null;
	
	public CreateCurrencyEvent(Currency currency) {
	
		this.currency = currency;
	}
	
	@Override
	public void dispose() {
		
		this.currency = null;
	}

	public Currency getCurrency() {
		
		return this.currency;
	}
	
	@Override
	public boolean getAutoDispose() {
		
		return true;
	}
}
