package algolia.ingestor;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VirtualReplicaConf {
    
    private String token;
    private ArrayList<String> customRanking;

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
     * @return ArrayList<String> return the customRanking
     */
    public ArrayList<String> getCustomRanking() {
        return customRanking;
    }

    /**
     * @param customRanking the customRanking to set
     */
    public void setCustomRanking(ArrayList<String> customRanking) {
        this.customRanking = customRanking;
    }

}
