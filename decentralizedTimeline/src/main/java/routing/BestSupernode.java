package routing;

public class BestSupernode extends BestPath {

    public int id;

    public BestSupernode(int id, int numHops) {
        super(numHops);
        this.id = id;
    }
}
