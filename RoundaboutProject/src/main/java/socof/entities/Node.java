package main.java.socof.entities;

/**
 * Class that represents a point in the graph that represents a Roundabout
 */
public class Node {

    // Position in the x-axis
    private double x;

    // Position in the y-axis
    private double y;

    // The type of a Node (ex: source)
    private String type;

    // A Node identifier
    private int ref;

    /**
     * Creates a Node with only a given x and y
     *
     * @param x the x-axis value
     * @param y the y-axis value
     */
    public Node(double x,double y){
        this.x=x;
        this.y=y;
    }

    /**
     * Creates a Node with a given x, y and type
     *
     * @param x the x-axis value
     * @param y the y-axis value
     * @param type the Node's type
     */
    public Node(double x,double y,String type){
        this.x=x;
        this.y=y;
        this.type=type;
    }

    /**
     * Creates a Node with all it's necessary information
     *
     * @param x the x-axis value
     * @param y the y-axis value
     * @param ref a Node's identification
     * @param type a Node's type
     */
    public Node(double x,double y,int ref,String type){
        this.x=x;
        this.y=y;
        this.type = type;
        this.ref=ref;
    }

    /**
     * Returns the Node's type
     *
     * @return Node type
     */
    public String getType() { return type; }

    /**
     * Returns the Node's x-axis value
     *
     * @return x-axis value
     */
    public double getX() { return x; }

    /**
     * Returns the Node's y-axis value
     *
     * @return y-axis value
     */
    public double getY() { return y; }


    /**
     * Returns the Node's identification
     *
     * @return Node's identification
     */
    public int getRef() { return ref; }

    /**
     * Returns a String representation of the Node
     *
     * @return String representation of the Node
     */
    @Override
    public String toString() {
        return "Node{" +
                "x=" + x +
                ", y=" + y +
                ", ref="+ref+
                ", type= " + type +
                '}';
    }

    /**
     * Determines if a given Node is the same as the current Node based on its axis values
     * @param o given Node
     * @return true if the Nodes are the same, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Double.compare(node.x, x) == 0 &&
                Double.compare(node.y, y) == 0;
    }
}
