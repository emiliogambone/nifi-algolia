package algolia.ingestor;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrimaryIndexConf {
    
    private String token;
    private String attributeForDistinct;

    /**
     * @return String return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return String return the attributeForDistinct
     */
    public String getAttributeForDistinct() {
        return attributeForDistinct;
    }

    /**
     * @param attributeForDistinct the attributeForDistinct to set
     */
    public void setAttributeForDistinct(String attributeForDistinct) {
        this.attributeForDistinct = attributeForDistinct;
    }

}
