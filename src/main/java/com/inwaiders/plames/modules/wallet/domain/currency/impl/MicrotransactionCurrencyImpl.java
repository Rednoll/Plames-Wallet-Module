package com.inwaiders.plames.modules.wallet.domain.currency.impl;

import java.text.DecimalFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.inwaiders.plames.api.locale.PlamesLocale;
import com.inwaiders.plames.modules.wallet.domain.currency.MicrotransactionCurrency;

@Entity(name = "MicrotransactionCurrency")
@Table(name = "wallet_microtransaction_currencies")
public class MicrotransactionCurrencyImpl extends CurrencyImpl implements MicrotransactionCurrency {

	@Column(name = "multiplier")
	private long multiplier = 0;
	
	@Override
	public void setMultiplier(long mr) {
		
		this.multiplier = mr;
	}

	@Override
	public long getMultiplier() {
		
		return this.multiplier;
	}
	
	@Override
	public int convertToReal(long amount) {
		
		double realAmount = (double) amount/(double) multiplier;
		
		return (int) Math.round(realAmount*100D);
	}
	
	@Override
	public String getWebDescription(PlamesLocale locale) {
		
		String result = super.getWebDescription();
		
			result += "<br/>";
			result += "- "+locale.getMessage("wallet.microtransaction_currency.description.multiplier", new DecimalFormat("#0.00").format(((double) getMultiplier()/100D)));
	
		return result;
	}
	
	public static MicrotransactionCurrencyImpl create() {
		
		MicrotransactionCurrencyImpl currency = new MicrotransactionCurrencyImpl();
		
		currency = repository.saveAndFlush(currency);
		
		return currency;
	}
	
	public String getType() {
		
		return "microtransaction";
	}
}
