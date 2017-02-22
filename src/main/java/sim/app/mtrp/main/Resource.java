package sim.app.mtrp.main;

/**
 * Created by drew on 2/20/17.
 */
public class Resource {
    private static final long serialVersionUID = 1;



    public enum Type {
        PIPE,
        SEAL,
        FLAP;
    }

    double buyPrice;
    double buybackPrice;

    double curQuantity;
    double quantity;
    Type resourceType;

    public Resource(Type resourceType, double quantity, double buyPrice, double buybackPrice) {
        this.resourceType = resourceType;
        this.quantity = quantity;
        this.curQuantity = quantity;
        this.buybackPrice = buybackPrice;
        this.buyPrice = buyPrice;
    }


    @Override
    public String toString() {
        return resourceType.name() + " curQuantity/quantity: " + curQuantity + "/" + quantity;
    }
}
