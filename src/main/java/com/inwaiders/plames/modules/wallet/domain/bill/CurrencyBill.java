package com.inwaiders.plames.modules.wallet.domain.bill;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inwaiders.plames.api.messenger.profile.UserProfile;
import com.inwaiders.plames.api.user.User;
import com.inwaiders.plames.domain.messenger.profile.impl.UserProfileBase;
import com.inwaiders.plames.modules.paygate.domain.billing.BillBase;
import com.inwaiders.plames.modules.wallet.WalletModule;
import com.inwaiders.plames.modules.wallet.domain.account.CurrencyAccount;
import com.inwaiders.plames.modules.wallet.domain.account.impl.CurrencyAccountImpl;
import com.inwaiders.plames.modules.wallet.domain.currency.Currency;
import com.inwaiders.plames.system.utils.MessageUtils;

@Entity(name = "CurrencyBill")
@Table(name = "wallet_currency_bills")
public class CurrencyBill extends BillBase {

	@Column(name = "currency_amount")
	private long currencyAmount = 0;

	@OneToOne(targetEntity = CurrencyAccountImpl.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "account_id")
	private CurrencyAccount account;

	@OneToOne(targetEntity = UserProfileBase.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "profile_id")
	private UserProfile profile = null;
	
	@Override
	public void onSuccess() {
		
		account.add(currencyAmount);

		if(profile != null && profile.isOnline()) {
			
			Currency currency = account.getCurrency();
			
			MessageUtils.send(WalletModule.getSystemProfile(), profile, "$wallet.currency_bill.success", account.getName(), currency.getDisplayAmount(currencyAmount), currency.getTag());
		}
	}
	
	public void setProfile(UserProfile profile) {
		
		this.profile = profile;
	}
	
	public UserProfile getProfile() {
		
		return this.profile;
	}
	
	public void setAccount(CurrencyAccount account) {
		
		this.account = account;
	}
	
	public CurrencyAccount getAccount() {
		
		return this.account;
	}
	
	public void setCurrencyAmount(long amount) {
		
		this.currencyAmount = amount;
	}
	
	public long getCurrencyAmount() {
		
		return this.currencyAmount;
	}
	
	public static CurrencyBill create(User user, int amount) {
		
		CurrencyBill currency = new CurrencyBill();
			currency.user = user;
			currency.amount = amount;
		
			currency = repository.save(currency);
			
		return currency;
	}
	
	public static class HighLevelRepository extends BillBase.HighLevelRepository<CurrencyBill> {
		
		public CurrencyBill create(User user, int amount) {
			
			return CurrencyBill.create(user, amount);
		}
	}
}
