package com.inwaiders.plames.modules.wallet.domain.account.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.inwaiders.plames.api.user.User;
import com.inwaiders.plames.api.utils.DescribedFunctionResult;
import com.inwaiders.plames.api.utils.DescribedFunctionResult.Status;
import com.inwaiders.plames.domain.user.impl.UserImpl;
import com.inwaiders.plames.modules.wallet.dao.account.CurrencyAccountRepository;
import com.inwaiders.plames.modules.wallet.domain.account.CurrencyAccount;
import com.inwaiders.plames.modules.wallet.domain.account.CurrencyAccountHlRepository;
import com.inwaiders.plames.modules.wallet.domain.currency.Currency;
import com.inwaiders.plames.modules.wallet.domain.currency.impl.CurrencyImpl;

@Entity(name = "CurrencyAccount")
@Table(name = "wallet_currency_accounts")
public class CurrencyAccountImpl implements CurrencyAccount {

	private static transient CurrencyAccountRepository repository = null;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id = null;
	
	@Column(name = "name")
	private String name = null;
	
	@ManyToOne(targetEntity = CurrencyImpl.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "currency_id")
	private Currency currency = null;
	
	@Column(name = "balance")
	private long balance = 0;
	
	@Column(name = "blocked")
	private boolean blocked = false;
	
	@ManyToMany(targetEntity = UserImpl.class)
	@JoinTable(name = "wallet_owners_currency_accounts_mtm", joinColumns = @JoinColumn(name = "currency_account_id"), inverseJoinColumns = @JoinColumn(name = "owner_id"))
	private Set<User> owners = new HashSet<>();
	
	@Column(name = "personal")
	private boolean personal = false;
	
	@Column(name = "deleted")
	private volatile boolean deleted = false;
	
	public CurrencyAccountImpl() {
		
	}
	
	public CurrencyAccountImpl(Currency currency, String name) {
		
		this.currency = currency;
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
		CurrencyAccountImpl other = (CurrencyAccountImpl) obj;
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public DescribedFunctionResult opUser(User admin, User user) {
	
		if(isPersonal()) return new DescribedFunctionResult(Status.ERROR, "$wallet.currency_account.domain.invalid_for_personal");
		if(!owners.contains(admin)) return new DescribedFunctionResult(Status.ERROR, "$wallet.currency_account.domain.rights_nf");
		if(owners.contains(user)) return new DescribedFunctionResult(Status.ERROR, "$wallet.currency_account.domain.op_already", user.getNickname());
	
		owners.add(user);
		
		save();
		
		return new DescribedFunctionResult(Status.OK, "$wallet.currency_account.domain.op_success", user.getNickname(), getDisplayName());
	}
	
	public DescribedFunctionResult deopUser(User admin, User user) {
	
		if(isPersonal()) return new DescribedFunctionResult(Status.ERROR, "$wallet.currency_account.domain.invalid_for_personal");
		if(!owners.contains(admin)) return new DescribedFunctionResult(Status.ERROR, "$wallet.currency_account.domain.rights_nf");
		if(!owners.contains(user)) return new DescribedFunctionResult(Status.ERROR, "$wallet.currency_account.domain.deop_already", user.getNickname());
		
		owners.remove(user);
		
		save();
		
		return new DescribedFunctionResult(Status.OK, "$wallet.currency_account.domain.deop_success", user.getNickname(), getDisplayName());
	}
	
	public String getDisplayName() {
		
		return getName()+" *("+currency.getTag()+")";
	}

	public String getDisplayBalance() {

		return currency.getDisplayAmount(balance);
	}
	
	public long add(long toAdd) {
		
		this.balance += toAdd;
		return this.balance;
	}
	
	public long substract(long toSubstract) {
		
		this.balance -= toSubstract;
		return this.balance;
	}
	
	public void setPersonal(boolean i) {
		
		this.personal = i;
	}
	
	public boolean isPersonal() {
		
		return this.personal;
	}
	
	@Override
	public Currency getCurrency() {
		
		return this.currency;
	}

	@Override
	public void setBalance(long balance) {
		
		this.balance = balance;
	}

	@Override
	public long getBalance() {
		
		return this.balance;
	}

	@Override
	public void setBlocked(boolean blocked) {
		
		this.blocked = blocked;
	}

	@Override
	public boolean isBlocked() {
		
		return this.blocked;
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
	
	public Long getId() {
		
		return this.id;
	}
	
	public void save() {
		
		if(!deleted) {
			
			repository.save(this);
		}
	}
	
	public void delete() {
		
		deleted = true;
		repository.save(this);
	}
	
	public static CurrencyAccountImpl create(Currency currency, String name) {
		
		CurrencyAccountImpl account = new CurrencyAccountImpl(currency, name);
		
			account = repository.saveAndFlush(account);
		
		return account;
	}
	
	public static CurrencyAccountImpl getById(long id) {
		
		return repository.getOne(id);
	}
	
	public static CurrencyAccountImpl getByCurrencyAndName(CurrencyImpl currency, String name) {
		
		return getByCurrencyAndName(currency, name, true);
	}

	public static CurrencyAccountImpl getByCurrencyAndName(CurrencyImpl currency, String name, boolean includePrivate) {
		
		CurrencyAccountImpl account = repository.getByCurrencyAndName(currency, name);
	
		if(!includePrivate && account != null && account.isPersonal()) {
			
			return null;
		}
		
		return account;
	}
	
	public static List<CurrencyAccountImpl> getByCurrency(CurrencyImpl currency) {
	
		return repository.getByCurrency(currency);
	}
	
	public static List<CurrencyAccountImpl> getAll() {
		
		return repository.findAll();
	}
	
	public static void setRepository(CurrencyAccountRepository rep) {
		
		repository = rep;
	}
	
	public static CurrencyAccountImpl parseAccount(CurrencyImpl currency, String name, User user, boolean includePrivate) {
		
		if(includePrivate && user != null && name.equals("private")) {
			
			name = user.getNickname();
		}
		
		return getByCurrencyAndName(currency, name, includePrivate);
	}
	
	public static class HighLevelRepository extends CurrencyAccountHlRepository<CurrencyAccountImpl> {

		@Override
		public CurrencyAccountImpl create(Currency currency, String name) {
			
			return CurrencyAccountImpl.create(currency, name);
		}
		
		public CurrencyAccountImpl getById(Long id) {
			
			return CurrencyAccountImpl.getById(id);
		}
		
		public List<CurrencyAccount> getAll() {
			
			List<CurrencyAccount> result = new ArrayList<>();
			
				result.addAll(CurrencyAccountImpl.getAll());
			
			return result;
		}

		@Override
		public void save(CurrencyAccountImpl entity) {
		
			entity.save();
		}
	}
}