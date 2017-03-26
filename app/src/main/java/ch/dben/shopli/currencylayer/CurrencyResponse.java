package ch.dben.shopli.currencylayer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyResponse {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("currencies")
    private Map<String, String> currencies;

    boolean isSuccess() {
        return success;
    }

    Map<String, String> getCurrencyMap() {
        return Collections.unmodifiableMap(currencies);
    }
}
