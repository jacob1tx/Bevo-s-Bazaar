/*
 * EE422C Final Project submission by
 * Jacob Marquardt
 * jgm3339
 * 17150
 * Slip days used: <1>
 * Spring 2023
 */

package src;

public class Item {
    private String price;
    private final String BINPrice;
    private final Integer completionTime;
    private final String title;
    private final String description;
    private String highestBidder;
    private Boolean sold;

    public Item(String title, String price, String BINPrice, Integer completionTime, String highestBidder, String description, Boolean sold) {

        this.price = priceAdjust(price);
        this.BINPrice = priceAdjust(BINPrice);
        this.completionTime = completionTime;
        this.title = title;
        this.description = description;
        this.highestBidder = highestBidder;
        this.sold = sold;
    }

    public String getPrice() {
        return price;
    }

    public Integer getCompletionTime() {
        return completionTime;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setPrice(String price) {
        this.price = priceAdjust(price);
    }

    public String getHighestBidder() {return highestBidder;}

    public void setHighestBidder(String user) {highestBidder = user;}

    public String toString() {
        return title + "," + price + "," + BINPrice + "," + completionTime + "," + highestBidder + "," + description + "," + sold;
    }

    private String priceAdjust(String text) {
        String [] dollarsAndCents = text.split("\\.");
        String cents;
        if (dollarsAndCents.length == 1)
            cents = "00";
        else
            cents = dollarsAndCents[1];
        return dollarsAndCents[0] + "." + cents;
    }

    public String getBINPrice() {
        return BINPrice;
    }

    public boolean isSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }
}

