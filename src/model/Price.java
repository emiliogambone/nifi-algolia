package processors.ingestor.model;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Price {
    private Integer priority;
    private Double listPrice;
    private Double offerPrice;
    private String currency;
    private String startDate;
    private String endDate;
    private String pricelist;
    private Double percentageDiscount;
    private Double amountOfDiscount;
    private String badge;
    private String segment;
    private Integer precedence;
    private NotNullableArrayList<Price> futurePrices; 
    private Integer priceListPrecedence;


    public Price(){}

    /**
     * @return String return the pricelist
     */
    public String getPricelist() {
        return pricelist;
    }

    /**
     * @param pricelist the pricelist to set
     */
    public void setPricelist(String pricelist) {
        this.pricelist = pricelist;
    }

    // /**
    //  * @return Integer return the percentageDiscount
    //  */
    // public Integer getPercentageDiscount() {
    //     return percentageDiscount;
    // }

    // /**
    //  * @param percentageDiscount the percentageDiscount to set
    //  */
    // public void setPercentageDiscount(Integer percentageDiscount) {
    //     this.percentageDiscount = percentageDiscount;
    // }


    /**
     * @return Double return the amountOfDiscount
     */
    public Double getPercentageDiscount() {
        return percentageDiscount;
    }

    /**
     * @param percentageDiscount the amountOfDiscount to set
     */
    public void setPercentageDiscount(Double percentageDiscount) {
        this.percentageDiscount = percentageDiscount;
    }

    

    /**
     * @return Double return the offerPrice
     */
    public Double getOfferPrice() {
        return offerPrice;
    }

    /**
     * @param offerPrice the offerPrice to set
     */
    public void setOfferPrice(Double offerPrice) {
        this.offerPrice = offerPrice;
    }

    /**
     * @return Double return the amountOfDiscount
     */
    public Double getAmountOfDiscount() {
        return amountOfDiscount;
    }

    /**
     * @param amountOfDiscount the amountOfDiscount to set
     */
    public void setAmountOfDiscount(Double amountOfDiscount) {
        this.amountOfDiscount = amountOfDiscount;
    }

    /**
     * @return String return the endDate
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * @return String return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * @return Integer return the priority
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * @return Double return the listPrice
     */
    public Double getListPrice() {
        return listPrice;
    }

    /**
     * @param listPrice the listPrice to set
     */
    public void setListPrice(Double listPrice) {
        this.listPrice = listPrice;
    }

    /**
     * @return String return the startDate
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }


    /**
     * @return String return the badge
     */
    public String getBadge() {
        return badge;
    }

    /**
     * @param badge the badge to set
     */
    public void setBadge(String badge) {
        this.badge = badge;
    }


    /**
     * @return Integer return the precedence
     */
    public Integer getPrecedence() {
        return precedence;
    }

    /**
     * @param precedence the precedence to set
     */
    public void setPrecedence(Integer precedence) {
        this.precedence = precedence;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }


    /**
     * @return NotNullableArrayList<Price> return the futurePrices
     */
    public NotNullableArrayList<Price> getFuturePrices() {
        return futurePrices;
    }

    /**
     * @param futurePrices the futurePrices to set
     */
    public void setFuturePrices(NotNullableArrayList<Price> futurePrices) {
        this.futurePrices = futurePrices;
    }

        /**
     * @return Integer return the priceListPrecedence
     */
    public Integer getPriceListPrecedence() {
        return priceListPrecedence;
    }

    /**
     * @param priceListPrecedence the priceListPrecedence to set
     */
    public void setPriceListPrecedence(Integer priceListPrecedence) {
        this.priceListPrecedence = priceListPrecedence;
    }
}
