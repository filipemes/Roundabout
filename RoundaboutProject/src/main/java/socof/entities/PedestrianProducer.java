package main.java.socof.entities;

/**
 * Class provides the implementation of a Pedestrian producer.
 * Generator functions via a thread that creates a new Pedestrian every 70 seconds.
 */
public class PedestrianProducer extends Thread{

    // Roundabout that is occupied by the generated Cars
    private Roundabout roundabout;

    /**
     * Initializes the Producer
     *
     * @param roundabout the roundabout that is occupied by the generated Pedestrians
     */
    public PedestrianProducer(Roundabout roundabout){
        this.roundabout=roundabout;
    }

    /**
     * Generates pedestrians every 7 seconds while the producer is active
     */
    public void run(){
            while(true){
                Pedestrian p=new Pedestrian(this.roundabout);
                p.start();
                try {
                    sleep(70000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    }


}
