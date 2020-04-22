package com.inwaiders.plames.modules.wallet.domain.account.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.inwaiders.plames.modules.wallet.dao.account.CurrencyAccountRepository;
import com.inwaiders.plames.modules.wallet.domain.account.CurrencyAccount;
import com.inwaiders.plames.modules.wallet.domain.account.CurrencyAccountHlRepository;
import com.inwaiders.plames.modules.wallet.domain.account.PrivateCurrencyAccount;
import com.inwaiders.plames.modules.wallet.domain.currency.Currency;
import com.inwaiders.plames.modules.wallet.domain.currency.impl.CurrencyImpl;

import enterprises.inwaiders.plames.api.user.User;
import enterprises.inwaiders.plames.dao.EntityLink;
import enterprises.inwaiders.plames.spring.SpringUtils;

@Entity(name = "CurrencyAccount")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class CurrencyAccountBase implements CurrencyAccount {

	private static transient CurrencyAccountRepository repository = null;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	protected Long id = null;
	
	@ManyToOne(targetEntity = CurrencyImpl.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "currency_id")
	protected Currency currency = null;
	
	@Column(name = "balance")
	protected long balance = 0;
	
	@Column(name = "blocked")
	protected boolean blocked = false;
	
	@Column(name = "deleted")
	protected volatile boolean deleted = false;
	
	public CurrencyAccountBase() {
		super();
		
	}
	
	public CurrencyAccountBase(Currency currency) {
		
		this.currency = currency;
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
	
	public static CurrencyAccountBase create(Currency currency, String name, String type) {
		
		CurrencyAccountBase account = null;
		
		if(type.equalsIgnoreCase("standard")) {
			
			account = new StandardCurrencyAccount();
		}
		
		else if(type.equalsIgnoreCase("private")) {
			
			account = new PrivateCurrencyAccountImpl();
		}
		
		account = repository.saveAndFlush(account);
		
		return account;
	}
	
	public static CurrencyAccountBase getById(long id) {
		
		return repository.getOne(id);
	}
	
	public static CurrencyAccountBase getByCurrencyAndName(CurrencyImpl currency, String name) {
		
		return getByCurrencyAndName(currency, name, true);
	}

	public static CurrencyAccountBase getByCurrencyAndName(CurrencyImpl currency, String name, boolean includePrivate) {
		
		CurrencyAccountBase account = repository.getByCurrencyAndName(currency, name);
	
		if(!includePrivate && account != null && account instanceof PrivateCurrencyAccount) {
			
			return null;
		}
		
		return account;
	}
	
	public static List<CurrencyAccountBase> getByCurrency(CurrencyImpl currency) {
	
		return repository.getByCurrency(currency);
	}
	
	public static List<CurrencyAccountBase> getAll() {
		
		return repository.findAll();
	}
	
	public static void setRepository(CurrencyAccountRepository rep) {
		
		repository = rep;
	}
	
	public static CurrencyAccountBase parseAccount(CurrencyImpl currency, String name, User user, boolean includePrivate) {
		
		if(includePrivate && user != null && name.equals("private")) {
			
			name = user.getNickname();
		}
		
		return getByCurrencyAndName(currency, name, includePrivate);
	}
	
	public static class HighLevelRepository extends CurrencyAccountHlRepository<CurrencyAccountBase> {

		@Override
		public CurrencyAccountBase create(Currency currency, String name, String type) {
			
			return CurrencyAccountBase.create(currency, name, type);
		}
		
		@Override
		public EntityLink getLink(CurrencyAccountBase entity) {
			
			return new EntityLink(SpringUtils.getEntityName(entity.getClass()), entity.getId());
		}
		
		public CurrencyAccountBase getById(Long id) {
			
			return CurrencyAccountBase.getById(id);
		}
		
		public List<CurrencyAccount> getAll() {
			
			List<CurrencyAccount> result = new ArrayList<>();
			
				result.addAll(CurrencyAccountBase.getAll());
			
			return result;
		}

		@Override
		public void save(CurrencyAccountBase entity) {
		
			entity.save();
		}
	}
}