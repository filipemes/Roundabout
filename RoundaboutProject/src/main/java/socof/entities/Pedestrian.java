package main.java.socof.entities;

import java.util.ArrayList;

/**
 * Class that represents a pedestrian, which can be used as an autonomous entity
 */
public class Pedestrian extends Thread{

    // Path the Pedestrian needs to traverse
    private  ArrayList<Node>pedestrianNodes;

    // The roundabout the car will traverse
    private Roundabout roundabout;

    // Pedestrian's position in the x-axis
    private double x;

    // Pedestrian's position in the y-axis
    private double y;

    /**
     *  Creates a Pedestrian with a given roundabout
     *
     * @param roundabout the roundabout the Pedestrian will traverse
     */
    public Pedestrian(Roundabout roundabout){
        this.roundabout=roundabout;
        this.pedestrianNodes=new ArrayList<>();
        pedestrianNodes.add(new Node(1110,440));
        pedestrianNodes.add(new Node(1110,280));
    }

    /**
     * Starts the Pedestrian as an autonomous entity that can move in the given roundabout
     */
    public void run(){
        pedestrianNodes=this.roundabout.getFullPath(this.pedestrianNodes);
        this.roundabout.addPedestrian(this);
            for(Node n:pedestrianNodes){
                this.x=n.getX();
                this.y=n.getY();
                this.roundabout.repaint();
                try {
                    sleep(120);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        this.roundabout.removePedestrian(this);
    }
    /**
     * Returns the Pedestrian's position in the x-axis
     *
     * @return the Pedestrian's position in the x-axis
     */
    public double getX() {
        return this.x;
    }
    /**
     * Returns the Pedestrian's position in the y-axis
     *
     * @return the Pedestrian's position in the y-axis
     */
    public double getY() {
        return this.y;
    }
}
