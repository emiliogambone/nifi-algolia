package processors.ingestor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"unknownFields"})
public class Record implements Serializable {

    private String objectID;
    private String storeId;


    private String catalogId;
    private String languageId;
    private String languageLocale;
    private String productId;
    private String partnumberId;
    private String parentProductId;
    private String parentPartnumberId;
    private Integer inventoryQuantity;
    private NotNullableHashMap<String, Price> prices;
    private NotNullableArrayList<String> segments;
    // private ArrayList<NotNullableHashMap<String,String>> media;
    private NotNullableHashMap<String, String> media;
    private NotNullableHashMap<String, NotNullableArrayList<String>> massoc;
    // private ArrayList<Category> categories;
    // private ArrayList<Category> categories_translated;
    private NotNullableArrayList<String> category_ids;

    private NotNullableArrayList<String> categories;
    private NotNullableArrayList<String> categories_translated;
    private NotNullableHashMap<String, NotNullableArrayList<String>> categories_tree;
    private NotNullableHashMap<String, NotNullableArrayList<String>> categories_tree_translated;
    private NotNullableHashMap<String, NotNullableArrayList<String>> categories_tree_ids;
    // private String canonicalCategory;
    private String x_groupkey;
    // private String variantKey;
    private String longDescription;
    private String url;
    private String remixableUrl;
    private Boolean buyable;
    private Boolean display;
    private NotNullableHashMap<String, Object> attributes;
    private NotNullableHashMap<String, Object> attributes_translated;
    private String modifiedTime;

    private NotNullableArrayList<String> catchall;
    private Boolean lifecycle;

    private HashMap<String, Double> businessRank;

    private Map<String, Object> unknownFields = new HashMap<>();


    public Record() {}


    @JsonAnyGetter
    public Map<String, Object> otherFields() {
        return unknownFields;
    }

    @JsonAnySetter
    public void setOtherField(String name, Object value) {
        if (value instanceof Map) {
            while (((Map) value).values().remove(null));
        }

        unknownFields.put(name, value);
    }

    @Override
    public String toString() {
        return String.format("objectID: %s", this.objectID);
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    /**
     * @return String return the languageLocale
     */
    public String getLanguageLocale() {
        return languageLocale;
    }

    /**
     * @param languageLocale the languageLocale to set
     */
    public void setLanguageLocale(String languageLocale) {
        this.languageLocale = languageLocale;
    }

    /**
     * @return String return the partnumberId
     */
    public String getPartnumberId() {
        return partnumberId;
    }

    /**
     * @param partnumberId the partnumberId to set
     */
    public void setPartnumberId(String partnumberId) {
        this.partnumberId = partnumberId;
    }

    /**
     * @return String return the parentPartnumberId
     */
    public String getParentPartnumberId() {
        return parentPartnumberId;
    }

    /**
     * @param parentPartnumberId the parentPartnumberId to set
     */
    public void setParentPartnumberId(String parentPartnumberId) {
        this.parentPartnumberId = parentPartnumberId;
    }

    // /**
    // * @return String return the canonicalCategory
    // */
    // public String getCanonicalCategory() {
    // return canonicalCategory;
    // }

    // /**
    // * @param canonicalCategory the canonicalCategory to set
    // */
    // public void setCanonicalCategory(String canonicalCategory) {
    // this.canonicalCategory = canonicalCategory;
    // }

    // /**
    // * @return String return the variantKey
    // */
    // public String getVariantKey() {
    // return variantKey;
    // }

    // /**
    // * @param variantKey the variantKey to set
    // */
    // public void setVariantKey(String variantKey) {
    // this.variantKey = variantKey;
    // }

    /**
     * @return String return the longDescription
     */
    public String getLongDescription() {
        return longDescription;
    }

    /**
     * @param longDescription the longDescription to set
     */
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    /**
     * @return String return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    public String getRemixableUrl() {
        return remixableUrl;
    }

    public void setRemixableUrl(String remixableUrl) {
        this.remixableUrl = remixableUrl;
    }

    public String getStoreId() {
        return this.storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getCatalogId() {
        return this.catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public String getLanguageId() {
        return this.languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getProductId() {
        return this.productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getParentProductId() {
        return this.parentProductId;
    }

    public void setParentProductId(String parentProductId) {
        this.parentProductId = parentProductId;
    }

    // /**
    // * @return String return the buyable
    // */
    // public String isBuyable() {
    // return buyable;
    // }

    // /**
    // * @return String return the display
    // */
    // public String isDisplay() {
    // return display;
    // }

    // /**
    // * @return String return the buyable
    // */
    // public String getBuyable() {
    // return buyable;
    // }

    // /**
    // * @param buyable the buyable to set
    // */
    // public void setBuyable(String buyable) {
    // this.buyable = buyable;
    // }

    // /**
    // * @return String return the display
    // */
    // public String getDisplay() {
    // return display;
    // }

    // /**
    // * @param display the display to set
    // */
    // public void setDisplay(String display) {
    // this.display = display;
    // }

    /**
     * @return String return the x_groupkey
     */
    public String getX_groupkey() {
        return x_groupkey;
    }

    /**
     * @param x_groupkey the x_groupkey to set
     */
    public void setX_groupkey(String x_groupkey) {
        this.x_groupkey = x_groupkey;
    }


    /**
     * @return Integer return the inventoryQuantity
     */
    public Integer getInventoryQuantity() {
        return inventoryQuantity;
    }

    /**
     * @param inventoryQuantity the inventoryQuantity to set
     */
    public void setInventoryQuantity(Integer inventoryQuantity) {
        this.inventoryQuantity = inventoryQuantity;
    }

    /**
     * @return Prices return the prices
     */
    public NotNullableHashMap<String, Price> getPrices() {
        return prices;
    }

    /**
     * @param prices the prices to set
     */
    public void setPrices(NotNullableHashMap<String, Price> prices) {
        this.prices = prices;
    }

    // /**
    // * @return ArrayList<Category> return the categories
    // */
    // public ArrayList<Category> getCategories() {
    // return categories;
    // }

    // /**
    // * @param categories the categories to set
    // */
    // public void setCategories(ArrayList<Category> categories) {
    // this.categories = categories;
    // }

    // /**
    // * @return ArrayList<Category> return the categories_translated
    // */
    // public ArrayList<Category> getCategories_translated() {
    // return categories_translated;
    // }

    // /**
    // * @param categories_translated the categories_translated to set
    // */
    // public void setCategories_translated(ArrayList<Category>
    // categories_translated) {
    // this.categories_translated = categories_translated;
    // }

    /**
     * @return NotNullableHashMap<String,String> return the attributes
     */
    public NotNullableHashMap<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(NotNullableHashMap<String, Object> attributes) {
        this.attributes = attributes;
    }

    /**
     * @return NotNullableHashMap<String,String> return the attributes_translated
     */
    public NotNullableHashMap<String, Object> getAttributes_translated() {
        return attributes_translated;
    }

    /**
     * @param attributes_translated the attributes_translated to set
     */
    public void setAttributes_translated(NotNullableHashMap<String, Object> attributes_translated) {
        this.attributes_translated = attributes_translated;
    }

    /**
     * @return ArrayList<String> return the segments
     */
    public ArrayList<String> getSegments() {
        return segments;
    }

    /**
     * @param segments the segments to set
     */
    public void setSegments(NotNullableArrayList<String> segments) {
        this.segments = segments;
    }

    /**
     * @return NotNullableHashMap<String,ArrayList<String>> return the massoc
     */
    public NotNullableHashMap<String, NotNullableArrayList<String>> getMassoc() {
        return massoc;
    }

    /**
     * @param massoc the massoc to set
     */
    public void setMassoc(NotNullableHashMap<String, NotNullableArrayList<String>> massoc) {
        this.massoc = massoc;
    }

    /**
     * @return NotNullableHashMap<String,String> return the media
     */
    public NotNullableHashMap<String, String> getMedia() {
        return media;
    }

    /**
     * @param media the media to set
     */
    public void setMedia(NotNullableHashMap<String, String> media) {
        this.media = media;
    }


    public NotNullableArrayList<String> getCategory_ids() {
        return category_ids;
    }

    public void setCategory_ids(NotNullableArrayList<String> category_ids) {
        this.category_ids = category_ids;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(NotNullableArrayList<String> categories) {
        this.categories = categories;
    }

    public ArrayList<String> getCategories_translated() {
        return categories_translated;
    }

    public void setCategories_translated(NotNullableArrayList<String> categories_translated) {
        this.categories_translated = categories_translated;
    }

    public NotNullableHashMap<String, NotNullableArrayList<String>> getCategories_tree() {
        return categories_tree;
    }

    public void setCategories_tree(
            NotNullableHashMap<String, NotNullableArrayList<String>> categories_tree) {
        this.categories_tree = categories_tree;
    }

    public NotNullableHashMap<String, NotNullableArrayList<String>> getCategories_tree_translated() {
        return categories_tree_translated;
    }

    public void setCategories_tree_translated(
            NotNullableHashMap<String, NotNullableArrayList<String>> categories_tree_translated) {
        this.categories_tree_translated = categories_tree_translated;
    }

    public NotNullableHashMap<String, NotNullableArrayList<String>> getCategories_tree_ids() {
        return categories_tree_ids;
    }

    public void setCategories_tree_ids(
            NotNullableHashMap<String, NotNullableArrayList<String>> categories_tree_ids) {
        this.categories_tree_ids = categories_tree_ids;
    }

    public Boolean getBuyable() {
        return buyable;
    }

    public void setBuyable(Boolean buyable) {
        this.buyable = buyable;
    }

    public Boolean getDisplay() {
        return display;
    }

    public void setDisplay(Boolean display) {
        this.display = display;
    }

    public ArrayList<String> getCatchall() {
        return catchall;
    }

    public void setCatchall(NotNullableArrayList<String> catchall) {
        this.catchall = catchall;
    }

    /**
     * @return Boolean return the lifecycle
     */
    public Boolean isLifecycle() {
        return lifecycle;
    }

    public Boolean isBuyable() {
        return this.buyable;
    }

    public Boolean isDisplay() {
        return this.display;
    }

    public String getModifiedTime() {
        return this.modifiedTime;
    }

    public void setModifiedTime(String modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public Boolean getLifecycle() {
        return this.lifecycle;
    }

    public void setLifecycle(Boolean lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Map<String, Object> getUnknownFields() {
        return this.unknownFields;
    }

    public HashMap<String, Double> getBusinessRank() {
        return businessRank;
    }

    public void setBusinessRank(HashMap<String, Double> businessRank) {
        this.businessRank = businessRank;
    }

    public void setUnknownFields(Map<String, Object> unknownFields) {
        this.unknownFields = unknownFields;
    }

}
