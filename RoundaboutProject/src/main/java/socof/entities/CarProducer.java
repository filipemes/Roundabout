package main.java.socof.entities;

/**
 * Class provides the implementation of a Car producer.
 * Generator functions via a thread that creates a new Car every 2 seconds.
 */
public class CarProducer extends Thread{

    // Roundabout that is occupied by the generated Cars
    private Roundabout roundabout;

    /**
     * Initializes the Producer
     *
     * @param roundabout the roundabout that is occupied by the generated Cars
     */
    public CarProducer(Roundabout roundabout){
        this.roundabout=roundabout;
    }

    /**
     * Generates cars every 2 seconds while the producer is active
     */
    public void run() {
        while(true){
            Car c = new Car(roundabout);
            c.start();
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
