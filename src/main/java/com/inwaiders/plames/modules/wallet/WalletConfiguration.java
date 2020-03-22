package com.inwaiders.plames.modules.wallet;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("file:config/modules/wallet/main.properties")
@ConfigurationProperties(prefix="wallet", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class WalletConfiguration {

	private List<String> delimiters = new ArrayList<>();

	public String getDelimetersRow() {
		
		String result = "";
		
		for(String delimiter : delimiters) {
			
			result += delimiter+" ";
		}
		
		return result.trim();
	}
	
	public List<String> getDelimiters() {
		
		return delimiters;
	}
}
