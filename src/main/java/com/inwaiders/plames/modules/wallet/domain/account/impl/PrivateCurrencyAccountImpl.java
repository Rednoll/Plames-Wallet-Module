package com.inwaiders.plames.modules.wallet.domain.account.impl;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.inwaiders.plames.modules.wallet.domain.account.PrivateCurrencyAccount;

import enterprises.inwaiders.plames.api.user.User;
import enterprises.inwaiders.plames.api.utils.DescribedFunctionResult;
import enterprises.inwaiders.plames.api.utils.DescribedFunctionResult.Status;
import enterprises.inwaiders.plames.domain.user.impl.UserImpl;

public class PrivateCurrencyAccountImpl extends CurrencyAccountBase implements PrivateCurrencyAccount {
	
	@OneToOne(targetEntity = UserImpl.class)
	@JoinColumn(name = "owner_id")
	private User owner = null;
	
	@Override
	public DescribedFunctionResult opUser(User admin, User user) {
		
		return new DescribedFunctionResult(Status.ERROR, "$wallet.currency_account.domain.invalid_for_personal");
	}

	@Override
	public DescribedFunctionResult deopUser(User admin, User user) {
		
		return new DescribedFunctionResult(Status.ERROR, "$wallet.currency_account.domain.invalid_for_personal");	
	}

	@Override
	public void setName(String name) {
		
		throw new RuntimeException("Can't set name to private account!");
	}

	@Override
	public String getName() {
		
		return this.owner.getNickname();
	}

	@Override
	public Set<User> getOwners() {
		
		Set<User> set = new HashSet<>();
			set.add(this.owner);
		
		return set;
	}
	
	public void setOwner(User user) {
		
		this.owner = user;
	}
	
	public User getOwner() {
		
		return this.owner;
	}
}
