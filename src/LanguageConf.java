package algolia.ingestor;

import com.algolia.search.models.rules.RenderingContent;
import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class LanguageConf {

    private IndexSettingsConf indexSettings;
    private RenderingContent renderingContent;
    

    /**
     * @return RenderingContent return the renderingContent
     */
    public RenderingContent getRenderingContent() {
        return renderingContent;
    }

    /**
     * @param renderingContent the renderingContent to set
     */
    public void setRenderingContent(RenderingContent renderingContent) {
        this.renderingContent = renderingContent;
    }


    /**
     * @return IndexSettingsConf return the indexSettings
     */
    public IndexSettingsConf getIndexSettings() {
        return indexSettings;
    }

    /**
     * @param indexSettings the indexSettings to set
     */
    public void setIndexSettings(IndexSettingsConf indexSettings) {
        this.indexSettings = indexSettings;
    }

}
