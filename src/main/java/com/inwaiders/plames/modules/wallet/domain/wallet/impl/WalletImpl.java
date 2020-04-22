package com.inwaiders.plames.modules.wallet.domain.wallet.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inwaiders.plames.modules.wallet.dao.wallet.WalletRepository;
import com.inwaiders.plames.modules.wallet.domain.account.PrivateCurrencyAccount;
import com.inwaiders.plames.modules.wallet.domain.account.impl.CurrencyAccountBase;
import com.inwaiders.plames.modules.wallet.domain.currency.Currency;
import com.inwaiders.plames.modules.wallet.domain.currency.impl.CurrencyImpl;
import com.inwaiders.plames.modules.wallet.domain.events.CreateWalletEvent;
import com.inwaiders.plames.modules.wallet.domain.wallet.Wallet;
import com.inwaiders.plames.modules.wallet.domain.wallet.WalletHlRepository;

import enterprises.inwaiders.plames.api.event.EventEngine;
import enterprises.inwaiders.plames.api.event.EventStage;
import enterprises.inwaiders.plames.api.user.User;
import enterprises.inwaiders.plames.domain.user.impl.UserImpl;

@Entity(name = "Wallet")
@Table(name = "wallet_wallets")
public class WalletImpl implements Wallet {

	private static transient WalletRepository repository = null;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id = null;
	
	@OneToOne(targetEntity = UserImpl.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "owner_id")
	private User owner = null;
	
	@OneToMany(cascade = CascadeType.ALL, targetEntity = CurrencyAccountBase.class, orphanRemoval = true, fetch = FetchType.EAGER)
	@MapKeyJoinColumn(name = "currency_id")
	@MapKeyClass(CurrencyImpl.class)
	private Map<Currency, PrivateCurrencyAccount> privateAccounts = new HashMap<>();
	
	@Column(name = "deleted")
	private volatile boolean deleted = false;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (deleted ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
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
		WalletImpl other = (WalletImpl) obj;
		if (deleted != other.deleted)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		return true;
	}

	@Override
	public void setOwner(User user) {
		
		this.owner = user;
	}

	@Override
	public User getOwner() {
		
		return this.owner;
	}

	@Override
	public void addPrivateAccount(PrivateCurrencyAccount account) {
		
		privateAccounts.put(account.getCurrency(), account);
	}
	
	@Override
	public PrivateCurrencyAccount getPrivateAccount(Currency currency) {
		
		return privateAccounts.get(currency);
	}
	
	@Override
	public Collection<PrivateCurrencyAccount> getPrivateAccounts() {
		
		return privateAccounts.values();
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
	
	public static WalletImpl create() {
		
		WalletImpl wallet = new WalletImpl();
		
			wallet = repository.saveAndFlush(wallet);
		
		CreateWalletEvent event = new CreateWalletEvent(wallet);
		
		EventEngine.getCommonEngine().run(event, EventStage.POST);
		
		return wallet;
	}
	
	public static WalletImpl getByOwner(User user) {
		
		return repository.getByOwner(user);
	}
	
	public static WalletImpl getById(long id) {
		
		return repository.getOne(id);
	}
	
	public static List<WalletImpl> getAll() {
		
		return repository.findAll();
	}
	
	public static void setRepository(WalletRepository rep) {
		
		repository = rep;
	}
	
	public static class HighLevelRepository extends WalletHlRepository {
		
		public Wallet create() {

			return WalletImpl.create();
		}
		
		public Wallet getById(long id) {
			
			return WalletImpl.getById(id);
		}
		
		public Wallet getByOwner(User user) {
			
			return WalletImpl.getByOwner(user);
		}
		
		public List<Wallet> getAll() {
			
			List<Wallet> wallets = new ArrayList<Wallet>();
			
				wallets.addAll(WalletImpl.getAll());
			
			return wallets;
		}
	}
}
