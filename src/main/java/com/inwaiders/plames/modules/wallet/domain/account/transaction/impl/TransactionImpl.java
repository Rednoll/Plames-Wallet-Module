package com.inwaiders.plames.modules.wallet.domain.account.transaction.impl;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.inwaiders.plames.modules.wallet.dao.account.transaction.TransactionRepository;
import com.inwaiders.plames.modules.wallet.domain.account.CurrencyAccount;
import com.inwaiders.plames.modules.wallet.domain.account.impl.CurrencyAccountBase;
import com.inwaiders.plames.modules.wallet.domain.account.transaction.Transaction;
import com.inwaiders.plames.modules.wallet.domain.account.transaction.TransactionHlRepository;
import com.inwaiders.plames.modules.wallet.domain.account.transaction.strategy.TransactionStrategy;
import com.inwaiders.plames.modules.wallet.domain.currency.Currency;
import com.inwaiders.plames.modules.wallet.domain.currency.impl.CurrencyImpl;

import enterprises.inwaiders.plames.api.user.User;
import enterprises.inwaiders.plames.api.utils.DescribedFunctionResult;
import enterprises.inwaiders.plames.domain.user.impl.UserImpl;

@Entity(name = "Transaction")
@Table(name = "wallet_transactions")
public class TransactionImpl implements Transaction{

	private static transient TransactionRepository repository = null;
	
	public static TransactionStrategy strategy = null;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@ManyToOne(targetEntity = UserImpl.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "sender_id")
	public User sender = null;
	
	@ManyToOne(targetEntity = CurrencyAccountBase.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "source_id")
	public CurrencyAccount source = null;
	
	@ManyToOne(targetEntity = CurrencyAccountBase.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "destination_id")
	public CurrencyAccount destination = null;
	
	@ManyToOne(targetEntity = CurrencyImpl.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "currency_id")
	public Currency currency = null;
	
	@Column(name = "amount")
	public long amount = 0;
	
	@Column(name = "complete_time")
	public long completeTime = -1;
	
	@Column(name = "deleted")
	private volatile boolean deleted = false;
	
	@Override
	public synchronized DescribedFunctionResult make() {
	
		return strategy.process(this);
	}
	
	public void setCompleteTime(long i) {
		
		this.completeTime = i;
	}
	
	public long getCompleteTime() {
		
		return this.completeTime;
	}
	
	public void setSender(User user) {
		
		this.sender = user;
	}
	
	@Override
	public User getSender() {
		
		return sender;
	}
	
	public void setSource(CurrencyAccount account) {
		
		this.source = account;
	}
	
	@Override
	public CurrencyAccount getSource() {
		
		return source;
	}
	
	public void setDestination(CurrencyAccount account) {
		
		this.destination = account;
	}

	@Override
	public CurrencyAccount getDestination() {
	
		return destination;
	}
	
	public void setCurrency(Currency currency) {
		
		this.currency = currency;
	}
	
	@Override
	public Currency getCurrency() {
		
		return currency;
	}
	
	public void setAmount(long amount) {
		
		this.amount = amount;
	}
	
	@Override
	public long getAmount() {
		
		return amount;
	}
	
	@Override
	public boolean isComplete() {
		
		return this.completeTime > 0;
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
	
	public static TransactionImpl create() {
		
		TransactionImpl transaction = new TransactionImpl();
		
			transaction = repository.save(transaction);
		
		return transaction;
	}
	
	public static Transaction getById(long id) {
		
		return repository.getOne(id);
	}
	
	public static List<TransactionImpl> getAll() {
		
		return repository.findAll();
	}
	
	public static void setRepository(TransactionRepository rep) {
		
		repository = rep;
	}
	
	public static class TransactionImplFactory extends TransactionHlRepository {

		@Override
		public Transaction create() {
			
			return TransactionImpl.create();
		}
		
		public Transaction getById(long id) {
			
			return TransactionImpl.getById(id);
		}
	}
}
