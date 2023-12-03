package processors.ingestor.constants;

public enum NiFiUpdateAttribute {
	
    ENV            ("env"),
    LOCALE             ("locale"),
    BRAND           ("brand"),


    ALGOLIA_API_KEY     ("algoliaApiKey"),
    ALGOLIA_APP_ID      ("algoliaAppId");

    private String value;
    
    NiFiUpdateAttribute(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
    
}