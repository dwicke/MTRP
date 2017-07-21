package sim.app.mtrp.main;

/**
 * Created by drew on 2/20/17.
 */
public class Resource implements java.io.Serializable {
    private static final long serialVersionUID = 1;

    private double buyPrice;
    double buybackPrice;

    int curQuantity;
    int quantity;
    int resourceType;
    double revenue = 0.0;

    public Resource(int resourceType, int quantity, double buyPrice, double buybackPrice) {
        this.resourceType = resourceType;
        this.quantity = quantity;
        this.curQuantity = quantity;
        this.buybackPrice = buybackPrice;
        this.setBuyPrice(buyPrice);
    }

    public void replenish() {
        curQuantity = quantity;
    }


    public int getResourceType() {
        return resourceType;
    }

    public double buyBack(int quantityBack) {
        revenue -= quantityBack * buybackPrice;
        curQuantity += quantityBack;
        return quantityBack * buybackPrice;
    }

    public double buy(int quantityBuying) {
        revenue += quantityBuying * getBuyPrice();
        curQuantity -= quantityBuying;
        return quantityBuying * getBuyPrice();
    }

    public double getCurQuantity() {
        return curQuantity;
    }

    public double getRevenue() {
        return revenue;
    }

    @Override
    public String toString() {
        return resourceType + " curQuantity/quantity: " + curQuantity + "/" + quantity;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }
}
