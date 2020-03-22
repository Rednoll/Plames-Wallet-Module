package com.inwaiders.plames.modules.wallet.web.currency.ajax;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.inwaiders.plames.modules.wallet.domain.currency.Currency;
import com.inwaiders.plames.modules.wallet.domain.currency.MicrotransactionCurrency;
import com.inwaiders.plames.modules.wallet.domain.currency.impl.CurrencyImpl;

@RestController
@RequestMapping("web/controller/ajax/wallet/currency")
public class WalletCurrencyWebAjax {

	@Autowired
	private ObjectMapper mapper;
	
	@PostMapping("/create")
	public RedirectView create(@RequestParam String name, @RequestParam MultipartFile icon, @RequestParam String tag, @RequestParam byte code, @RequestParam int fragmentationLevel, @RequestParam String type, @RequestParam(required=false) Double mtMultiplier) throws IOException {
		
		Currency currency = Currency.create(type);
		
		saveData(currency, name, icon, tag, code, fragmentationLevel, mtMultiplier);
		
		return new RedirectView("/wallet/currencies");
	}
	
	@PostMapping("/edit")
	public RedirectView edit(@RequestParam long id, @RequestParam String name, @RequestParam MultipartFile icon, @RequestParam String tag, @RequestParam byte code, @RequestParam int fragmentationLevel, @RequestParam(required=false) Double mtMultiplier) throws IOException {
		
		Currency currency = Currency.getById(id);
		
		saveData(currency, name, icon, tag, code, fragmentationLevel, mtMultiplier);
		
		return new RedirectView("/wallet/currencies");
	}
	
	private void saveData(Currency currency, String name, MultipartFile icon, String tag, byte code, int fragmentationLevel, Double mtMultiplier) throws IOException {
		
		currency.setName(name);
		currency.setTag(tag);
		currency.setCode(code);
		currency.setFragmentationLevel(fragmentationLevel);

		if(currency instanceof MicrotransactionCurrency) {
			
			((MicrotransactionCurrency) currency).setMultiplier(Math.round(mtMultiplier*100D));
		}
		
		currency.save();
		
		if(!icon.isEmpty()) {
		
			byte[] iconData = icon.getBytes();
			
				File iconFile = new File("./data/modules/wallet/currencies/"+currency.getId()+"/icon.svg");
				
					if(!iconFile.getParentFile().exists()) {
						iconFile.getParentFile().mkdirs();
					}
					
					if(iconFile.exists()) {
						
						iconFile.delete();
					}
					
				iconFile.createNewFile();
				
			Files.write(iconFile.toPath(), iconData);
		}
	}
	
	@PostMapping("/{id}/active")
	public ResponseEntity active(@PathVariable long id, @RequestBody JsonNode node) {
		
		if(!node.has("active") || !node.get("active").isBoolean()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
		
		Currency currency = CurrencyImpl.getById(id);
		
		if(currency != null) {
			
			currency.setActive(node.get("active").asBoolean());
			
			currency.save();
			
			return new ResponseEntity(HttpStatus.OK);
		}
	
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}

	@GetMapping("/types")
	public ArrayNode typesList() {
		
		ArrayNode array = mapper.createArrayNode();
		
		List<String> types = Currency.getTypes();
		
		for(String type : types) {
			
			array.add(type);
		}
		
		return array;
	}
}
