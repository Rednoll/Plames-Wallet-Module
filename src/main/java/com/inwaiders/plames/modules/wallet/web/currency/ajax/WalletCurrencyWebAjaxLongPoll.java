package com.inwaiders.plames.modules.wallet.web.currency.ajax;

import java.util.concurrent.ForkJoinPool;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.inwaiders.plames.modules.wallet.domain.currency.impl.CurrencyImpl;

@RestController
@RequestMapping("web/controller/ajax/long_poll/wallet/currency")
public class WalletCurrencyWebAjaxLongPoll {
	
	@GetMapping(value = "/{id}/description", produces = "text/plain;charset=UTF-8")
	public DeferredResult<ResponseEntity<String>> description(@PathVariable(name="id") long id) {
	
		DeferredResult<ResponseEntity<String>> output = new DeferredResult<>();
		
		CurrencyImpl currency =	CurrencyImpl.getById(id);
		
		if(currency != null) {

			ForkJoinPool.commonPool().submit(()-> {
				
				String descripton = currency.getWebDescription();
			
				output.setResult(new ResponseEntity<String>(descripton, HttpStatus.OK));
			});
		}
		else {
			
			output.setResult(new ResponseEntity<String>(HttpStatus.NOT_FOUND));
		}
		
		return output;
	}
}
