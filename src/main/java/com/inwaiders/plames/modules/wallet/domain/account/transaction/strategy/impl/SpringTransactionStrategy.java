package com.inwaiders.plames.modules.wallet.domain.account.transaction.strategy.impl;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inwaiders.plames.modules.wallet.domain.account.CurrencyAccount;
import com.inwaiders.plames.modules.wallet.domain.account.transaction.Transaction;
import com.inwaiders.plames.modules.wallet.domain.account.transaction.strategy.TransactionStrategy;
import com.inwaiders.plames.modules.wallet.domain.currency.Currency;

import enterprises.inwaiders.plames.api.user.User;
import enterprises.inwaiders.plames.api.utils.DescribedFunctionResult;
import enterprises.inwaiders.plames.api.utils.DescribedFunctionResult.Status;

@Component("SpringTransactionStrategy")
@Scope("singleton")
public class SpringTransactionStrategy implements TransactionStrategy {

	@Transactional(propagation = Propagation.REQUIRED)
	public DescribedFunctionResult process(Transaction transaction) {
		
		CurrencyAccount source = transaction.getSource();
		CurrencyAccount destination = transaction.getDestination();
	
		User sender = transaction.getSender();
		
		Currency currency = transaction.getCurrency();
		
		long amount = transaction.getAmount();
		
		if(source.isBlocked()) return new DescribedFunctionResult(Status.ERROR, "$wallet.transaction.source_blocked");
		if(destination.isBlocked()) return new DescribedFunctionResult(Status.ERROR, "$wallet.transaction.target_blocked");
		if(!source.getOwners().contains(sender)) return new DescribedFunctionResult(Status.ERROR, "$wallet.transaction.rights_nf", source.getName());
		
		long sourceAmount = source.getBalance();
		
		if(sourceAmount < amount) {
			
			return new DescribedFunctionResult(Status.ERROR, "$wallet.transaction.insufficient_funds");
		}
		
		source.substract(amount);
		destination.add(amount);
		
		source.save();
		destination.save();
		
		transaction.setCompleteTime(System.currentTimeMillis());
		
		return new DescribedFunctionResult(Status.OK, "$wallet.transaction.success", source.getName(), destination.getName(), currency.getDisplayAmount(amount), currency.getTag());
	}
}
