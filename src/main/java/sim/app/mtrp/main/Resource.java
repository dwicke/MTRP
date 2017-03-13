package sim.app.mtrp.main;

/**
 * Created by drew on 2/20/17.
 */
public class Resource implements java.io.Serializable {
    private static final long serialVersionUID = 1;

    double buyPrice;
    double buybackPrice;

    double curQuantity;
    double quantity;
    int resourceType;

    public Resource(int resourceType, double quantity, double buyPrice, double buybackPrice) {
        this.resourceType = resourceType;
        this.quantity = quantity;
        this.curQuantity = quantity;
        this.buybackPrice = buybackPrice;
        this.buyPrice = buyPrice;
    }


    @Override
    public String toString() {
        return resourceType + " curQuantity/quantity: " + curQuantity + "/" + quantity;
    }
}
