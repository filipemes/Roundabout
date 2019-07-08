package main.java.socof;

import main.java.socof.entities.*;

public class Main {

	public static void main(String[] args) {
		Roundabout roundabout = new Roundabout();
		new MainFrame(roundabout);
		CarProducer carProducer=new CarProducer(roundabout);
		carProducer.start();
		PedestrianProducer pedestrianProducer=new PedestrianProducer(roundabout);
		pedestrianProducer.start();
	}

}
