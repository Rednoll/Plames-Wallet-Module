package com.inwaiders.plames.modules.wallet.domain.currency.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.inwaiders.plames.api.event.EventEngine;
import com.inwaiders.plames.api.event.EventStage;
import com.inwaiders.plames.api.locale.PlamesLocale;
import com.inwaiders.plames.dao.EntityLink;
import com.inwaiders.plames.modules.wallet.dao.currency.CurrencyRepository;
import com.inwaiders.plames.modules.wallet.domain.account.impl.CurrencyAccountImpl;
import com.inwaiders.plames.modules.wallet.domain.currency.Currency;
import com.inwaiders.plames.modules.wallet.domain.currency.CurrencyHlRepository;
import com.inwaiders.plames.modules.wallet.domain.events.CreateCurrencyEvent;
import com.inwaiders.plames.modules.wallet.spring.WalletSpringPortal;
import com.inwaiders.plames.spring.SpringUtils;

@Entity(name = "Currency")
@Table(name = "wallet_currencies")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class CurrencyImpl implements Currency {

	protected static transient CurrencyRepository repository = null;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id = null;
	
	@Column(name = "active")
	private boolean active = true;
	
	@Column(name = "name")
	private String name = null;
	
	@Column(name = "code")
	private byte code = -1;
	
	@Column(name = "tag")
	private String tag = null;
	
	@JsonAlias("fragmentation_level")
	@Column(name = "fragmentation_level")
	private int fragmentationLevel = 3;
	
	@Column(name = "deleted")
	private volatile boolean deleted = false;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (active ? 1231 : 1237);
		result = prime * result + code;
		result = prime * result + (deleted ? 1231 : 1237);
		result = prime * result + fragmentationLevel;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
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
		CurrencyImpl other = (CurrencyImpl) obj;
		if (active != other.active)
			return false;
		if (code != other.code)
			return false;
		if (deleted != other.deleted)
			return false;
		if (fragmentationLevel != other.fragmentationLevel)
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
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		return true;
	}

	public void setFragmentationLevel(int level) {
	
		this.fragmentationLevel = level;
	}
	
	@Override
	public int getFragmentationLevel() {

		return this.fragmentationLevel;
	}
	
	@Override
	public void setName(String name) {
		
		this.name = name;
	}

	@Override
	public String getName() {
		
		return this.name;
	}

	@Override
	public void setCode(byte code) {
		
		this.code = code;
	}

	@Override
	public byte getCode() {
		
		return this.code;
	}

	@Override
	public void setTag(String tag) {
		
		this.tag = tag;
	}

	@Override
	public String getTag() {
		
		return this.tag;
	}
	
	public void setActive(boolean active) {
		
		this.active = active;
	}
	
	public boolean isActive() {
		
		return this.active;
	}
	
	public String getType() {
		
		return "default";
	}
	
	public Long getId() {
		
		return this.id;
	}
	
	public String getWebDescription() {
		
		return getWebDescription(PlamesLocale.getSystemLocale());
	}
	
	public String getWebDescription(PlamesLocale locale) {

		String result = "";
		
		long rawTotalAmount = calcTotalAmount();
		
		String totalAmount = getDisplayAmount(rawTotalAmount);
		
		result += "- "+locale.getMessage("$wallet.currency.all_on_accounts", totalAmount);
		result += "<br/>";
		
		String fragmentationPattern = "xx";
		
		if(fragmentationLevel > 0) {
			
			fragmentationPattern += WalletSpringPortal.CONFIG.getDelimiters().get(0);
		}
		
		for(int i = 0;i<fragmentationLevel;i++) {
			
			fragmentationPattern += "x";
		}
		
		result += "- "+locale.getMessage("$wallet.currency.all_on_accounts", fragmentationPattern);
		
		return result;
	}
	
	public long calcTotalAmount() {
	
		long result = 0;
		
		List<CurrencyAccountImpl> accounts = CurrencyAccountImpl.getByCurrency(this);
	
		for(CurrencyAccountImpl account : accounts) {
			
			result += account.getBalance();
		}
		
		return result;
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
	
	public String getDisplayAmount(long amount) {
		
		return String.valueOf(((double) amount) / Math.pow(10, fragmentationLevel));
	}
	
	public long parseAmount(String rawAmount) throws NumberFormatException {
		
		List<String> symbols = Arrays.asList(rawAmount.split(""));
		List<String> delimiters = WalletSpringPortal.CONFIG.getDelimiters();
		
		c2 : for(String delimiter : delimiters) {
			
			if(symbols.contains(delimiter)) {

				int delimiterIndex = rawAmount.indexOf(delimiter);
				
				while(rawAmount.length()-delimiterIndex <= fragmentationLevel) {
					
					rawAmount += "0";
				}

				rawAmount = rawAmount.replaceAll("\\"+delimiter, "");
				
				return Long.parseLong(rawAmount);
			}
		}
		
		return (long) (Long.parseLong(rawAmount)*Math.pow(10, fragmentationLevel));
	}
	
	
	public static CurrencyImpl parseBySign(String currencySign) {
			
		CurrencyImpl currency = null;
		
			currency = CurrencyImpl.getByTag(currencySign);
			
			if(currency == null) {
				
				currency = CurrencyImpl.getByName(currencySign);
			}
			
			if(currency == null) {
				
				try {
					
					currency = CurrencyImpl.getByCode(Byte.valueOf(currencySign));
				}
				catch(NumberFormatException e) {
					
				}
			}
			
		return currency;
	}
	
	public static CurrencyImpl create() {
		
		CurrencyImpl currency = new CurrencyImpl();
		
			currency = repository.saveAndFlush(currency);
		
		CreateCurrencyEvent event = new CreateCurrencyEvent(currency);
		
		EventEngine.getCommonEngine().run(event, EventStage.POST);
		
		return currency;
	}
	
	public static CurrencyImpl getById(long id) {
		
		return repository.getOne(id);
	}
	
	public static CurrencyImpl getByCode(byte id) {
		
		return repository.getByCode(id);
	}
	
	public static CurrencyImpl getByName(String name) {
		
		return repository.getByName(name);
	}

	public static CurrencyImpl getByTag(String tag) {
		
		return repository.getByTag(tag);
	}
	
	public static List<CurrencyImpl> getAll() {
		
		return repository.findAll();
	}
	
	public static void setRepository(CurrencyRepository rep) {
		
		repository = rep;
	}
	
	public static class HighLevelRepository extends CurrencyHlRepository<CurrencyImpl> {

		@Override
		public CurrencyImpl create(String type) {
		
			if(type == null) return null;
			
			if(type.equals("default")) {
				
				return CurrencyImpl.create();
			}
			
			if(type.equals("microtransaction")) {
				
				return MicrotransactionCurrencyImpl.create();
			}
			
			return null;
		}	
		
		@Override
		public EntityLink getLink(CurrencyImpl entity) {
			
			return new EntityLink(SpringUtils.getEntityName(entity), entity.getId());
		}
		
		public CurrencyImpl parseBySign(String aliase) {
			
			return CurrencyImpl.parseBySign(aliase);
		}
		
		public CurrencyImpl getByTag(String tag) {
			
			return CurrencyImpl.getByTag(tag);
		}
		
		public CurrencyImpl getById(Long id) {
			
			return CurrencyImpl.getById(id);
		}
		
		public List<String> getTypes() {
			
			List<String> result = new ArrayList<>();
				result.add("default");
				result.add("microtransaction");
				
			return result;
		}
		
		public List<Currency> getAll() {
			
			List<Currency> currencies = new ArrayList<>();
				currencies.addAll(CurrencyImpl.getAll());
			
			return currencies;
		}

		@Override
		public void save(CurrencyImpl entity) {
			
			entity.save();
		}
	}
}
