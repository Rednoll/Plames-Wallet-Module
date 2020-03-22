package com.inwaiders.plames.modules.wallet.web.currency.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.inwaiders.plames.modules.wallet.domain.currency.Currency;
import com.inwaiders.plames.modules.wallet.domain.currency.MicrotransactionCurrency;
import com.inwaiders.plames.modules.wallet.domain.currency.impl.CurrencyImpl;

@RestController
@RequestMapping("/api/wallet/rest")
public class CurrencyRestController {

	@Autowired
	private ObjectMapper mapper;
	
	@GetMapping(value = "/currencies", produces = "application/json; charset=UTF-8")
	public ArrayNode getAll() {
		
		List<CurrencyImpl> currencies = CurrencyImpl.getAll();
	
		ArrayNode array = mapper.createArrayNode();
		
			for(CurrencyImpl currency : currencies) {
				
				array.add(get(String.valueOf(currency.getId())));
			}
		
		return array;
	}
	
	@GetMapping(value = "/currencies/{sign}", produces = "application/json; charset=UTF-8")
	public ObjectNode get(@PathVariable String sign) {
		
		Currency currency = null;
		
		try {
			
			currency = Currency.getById(Long.parseLong(sign));
		}
		catch(NumberFormatException e) {
			
			currency = Currency.parseBySign(sign);
		}
			
		ObjectNode node = mapper.createObjectNode();
		
			node.put("name", currency.getName());
			node.put("id", currency.getId());
			node.put("fragmentation_level", currency.getFragmentationLevel());
			node.put("tag", currency.getTag());
			node.put("code", currency.getCode());
			node.put("type", currency.getType());
			
			if(currency instanceof MicrotransactionCurrency) {
				
				node.put("multiplier", (double)((MicrotransactionCurrency) currency).getMultiplier()/100D);
			}
			
		return node;
	}
	
	@PostMapping(value = "/currencies")
	public ObjectNode create(@RequestBody CurrencyImpl currency) {

		currency.save();
		
		return get(String.valueOf(currency.getId()));
	}
	
	@PutMapping(value = "/currencies/{id}") 
	public ResponseEntity save(@PathVariable long id, @RequestBody JsonNode node) {
		
		CurrencyImpl currency = CurrencyImpl.getById(id);
	
			if(currency == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
			
			if(node.has("name") && node.get("name").isTextual()) {
			
				currency.setName(node.get("name").asText());
			}
			
			if(node.has("tag") && node.get("tag").isTextual()) {
				
				currency.setTag(node.get("tag").asText());
			}
			
			if(node.has("code") && node.get("code").isNumber()) {
				
				currency.setCode((byte) node.get("code").asInt());
			}
			
			if(node.has("fragmentation_level") && node.get("fragmentation_level").isNumber()) {
				
				currency.setFragmentationLevel(node.get("fragmentation_level").asInt());
			}
			
		currency.save();
		
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/currencies/{id}")
	public ResponseEntity delete(@PathVariable long id) {
	
		CurrencyImpl currency = CurrencyImpl.getById(id);
		
		if(currency != null) {
			
			currency.delete();
		
			return new ResponseEntity<>(HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
}
