package com.inwaiders.plames.modules.wallet.domain.account.impl;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.inwaiders.plames.modules.wallet.domain.currency.Currency;

import enterprises.inwaiders.plames.api.user.User;
import enterprises.inwaiders.plames.api.utils.DescribedFunctionResult;
import enterprises.inwaiders.plames.api.utils.DescribedFunctionResult.Status;
import enterprises.inwaiders.plames.domain.user.impl.UserImpl;

public class StandardCurrencyAccount extends CurrencyAccountBase {
	
	@Column(name = "name")
	protected String name = null;
	
	@ManyToMany(targetEntity = UserImpl.class)
	@JoinTable(name = "wallet_owners_currency_accounts_mtm", joinColumns = @JoinColumn(name = "currency_account_id"), inverseJoinColumns = @JoinColumn(name = "owner_id"))
	protected Set<User> owners = new HashSet<>();
	
	public StandardCurrencyAccount() {
		super();
		
	}
	
	public StandardCurrencyAccount(Currency currency, String name) {
		super(currency);
	
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (balance ^ (balance >>> 32));
		result = prime * result + (blocked ? 1231 : 1237);
		result = prime * result + (deleted ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CurrencyAccountBase other = (CurrencyAccountBase) obj;
		if (balance != other.balance)
			return false;
		if (blocked != other.blocked)
			return false;
		if (deleted != other.deleted)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public DescribedFunctionResult opUser(User admin, User user) {
	
		if(!owners.contains(admin)) return new DescribedFunctionResult(Status.ERROR, "$wallet.currency_account.domain.rights_nf");
		if(owners.contains(user)) return new DescribedFunctionResult(Status.ERROR, "$wallet.currency_account.domain.op_already", user.getNickname());
	
		owners.add(user);
		
		save();
		
		return new DescribedFunctionResult(Status.OK, "$wallet.currency_account.domain.op_success", user.getNickname(), getDisplayName());
	}
	
	public DescribedFunctionResult deopUser(User admin, User user) {
	
		if(!owners.contains(admin)) return new DescribedFunctionResult(Status.ERROR, "$wallet.currency_account.domain.rights_nf");
		if(!owners.contains(user)) return new DescribedFunctionResult(Status.ERROR, "$wallet.currency_account.domain.deop_already", user.getNickname());
		
		owners.remove(user);
		
		save();
		
		return new DescribedFunctionResult(Status.OK, "$wallet.currency_account.domain.deop_success", user.getNickname(), getDisplayName());
	}
	
	@Override
	public Set<User> getOwners() {
		
		return this.owners;
	}

	public void setName(String name) {
		
		this.name = name;
	}
	
	public String getName() {
		
		return this.name;
	}
}
