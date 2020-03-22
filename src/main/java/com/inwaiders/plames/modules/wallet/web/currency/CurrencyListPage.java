package com.inwaiders.plames.modules.wallet.web.currency;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.inwaiders.plames.modules.wallet.domain.currency.Currency;
import com.inwaiders.plames.modules.wallet.domain.currency.impl.CurrencyImpl;

@Controller
@RequestMapping("/wallet/currencies")
public class CurrencyListPage {

	@GetMapping("")
	public String mainPage(Model model) {
		
		List<CurrencyImpl> currencies = CurrencyImpl.getAll();
		
		model.addAttribute("currencies", currencies);
		model.addAttribute("currencies_types", Currency.getTypes());
		
		return "wallet_currencies";
	}
}
