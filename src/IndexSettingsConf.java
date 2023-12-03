package algolia.ingestor;

import com.algolia.search.models.settings.IndexSettings;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IndexSettingsConf {
    
    private IndexSettings primaryIndexes;
    private IndexSettings indexesAndReplicas;

    /**
     * @return IndexSettings return the primaryIndexes
     */
    public IndexSettings getPrimaryIndexes() {
        return primaryIndexes;
    }

    /**
     * @param primaryIndexes the primaryIndexes to set
     */
    public void setPrimaryIndexes(IndexSettings primaryIndexes) {
        this.primaryIndexes = primaryIndexes;
    }

    /**
     * @return IndexSettings return the indexesAndReplicas
     */
    public IndexSettings getIndexesAndReplicas() {
        return indexesAndReplicas;
    }

    /**
     * @param indexesAndReplicas the indexesAndReplicas to set
     */
    public void setIndexesAndReplicas(IndexSettings indexesAndReplicas) {
        this.indexesAndReplicas = indexesAndReplicas;
    }

}
