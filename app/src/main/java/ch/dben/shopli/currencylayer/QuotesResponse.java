package ch.dben.shopli.currencylayer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuotesResponse {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("timestamp")
    private long timestamp;

    @JsonProperty("source")
    private String sourceCurrency;

    @JsonProperty("quotes")
    private Map<String, Double> quotes;

    boolean isSuccess() {
        return success;
    }

    long getTimestampMillis() {
        return timestamp * 1000;
    }

    String getSourceCurrency() {
        return sourceCurrency;
    }

    Map<String, Double> getQuotesMap() {
        return Collections.unmodifiableMap(quotes);
    }
}
