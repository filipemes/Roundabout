package main.java.socof.entities;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Class that represents a car, which can be used as an autonomous entity
 */
public class Car extends Thread {

	// Car's license plate
	private String licensePlate;

	// The roundabout the car will traverse
	private Roundabout roundAbout;

	// The State the car currently is in
	private CarState state;

	// Node the Car starts at
	private Node source;

	// Node the Car finishes at
	private Node destination;

	// Car's position in the x-axis
	private double x;

	// Car's position in the y-axis
	private double y;

	// Car's angle
	private int angle;

	// Path the Car needs to traverse
	private ArrayList<Node> path;

	// Image used to represent the Car
	private BufferedImage spriteCar;

	private static final Logger LOGGER = Logger.getLogger( Car.class.getName() );

	/**
	 * Creates a Car with a given roundabout
	 *
	 * @param roundabout the roundabout the Car will traverse
	 */
	public Car(Roundabout roundabout) {
		this.y=-20;
		this.x=-20;
		this.licensePlate = generateLicensePlate();
		this.roundAbout = roundabout;
		this.state = CarState.ENTERING;
		this.source=this.roundAbout.randomSource();
		this.destination=this.roundAbout.randomDestination(this.source);
		randomCarSprite();
	}

	/**
	 * Creates a Car with a given license-plate, roundabout, source and destination Node
	 *
	 * @param licensePlate the licence-plate
	 * @param roundabout the roundabout
	 * @param source the source Node
	 * @param destination the destination Node
	 */
	public Car(String licensePlate, Roundabout roundabout,int source,int destination) {
		this.licensePlate = licensePlate;
		this.roundAbout = roundabout;
		this.state = CarState.ENTERING;
		this.source=this.roundAbout.getNodeByRef(source);
		this.destination=this.roundAbout.getNodeByRef(destination);
		randomCarSprite();
	}

	/**
	 * Generates a random number
	 *
	 * @return a number in the form of a char
	 */
	private char numberGenerator(){
		return (char) (Math.random() * 10 + '0');
	}

	/**
	 * Generates a random letter
	 *
	 * @return a letter in the form of a char
	 */
	private char letterGenerator() {
		return (char) (Math.random() * 26 + 'A');
	}

	/**
	 * Generates a license-plate for a Car
	 *
	 * @return a license-plate
	 */
	private String generateLicensePlate(){
		String plate = "";

		plate += numberGenerator();
		plate += numberGenerator();

		plate += '-';

		plate += letterGenerator();
		plate += letterGenerator();

		plate += '-';

		plate += numberGenerator();
		plate += numberGenerator();

		return plate;
	}

	/**
	 * Chooses a random car image to represent the current Car
	 */
	private void randomCarSprite(){
		int random= 1+(int)(4 * Math.random());
		try {
			spriteCar=ImageIO.read(new File( Roundabout.absPathToResource+"/car"+random+".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the image used to represent the Car
	 *
	 * @return the image used to represent the Car
	 */
	public  BufferedImage getSpriteCar(){
		return this.spriteCar;
	}

	/**
	 * Returns the Car's source Node
	 *
	 * @return the Car's source Node
	 */
	public Node getSource() {
		return source;
	}

	/**
	 * Returns the Car's destination Node
	 *
	 * @return the Car's destination Node
	 */
	public Node getDestination() {
		return destination;
	}


	/**
	 * Returns the Car's license-plate
	 *
	 * @return the Car's license-plate
	 */
	public String getLicensePlate() {
		return licensePlate;
	}

	/**
	 * Returns the Car's current state
	 *
	 * @return the Car's current state
	 */
    public CarState getCarState() {
		return state;
	}

	/**
	 * Returns the Car's position in the x-axis
	 *
	 * @return the Car's position in the x-axis
	 */
    public double getX() {
    	return x;
    }

	/**
	 * Returns the Car's position in the y-axis
	 *
	 * @return the Car's position in the y-axis
	 */
    public double getY() {
    	return y;
    }

	/**
	 * Updates the Car's x and y axis values
	 *
	 * @param x new x-axis value
	 * @param y new y-axis value
	 */
	public void updatePlacement(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns the Car's angle
	 *
	 * @return the Car's angle
	 */
	public double getAngle() {
		return this.angle;
	}

	/**
	 * Updates the Car's state
	 *
	 * @param newState the Car's new state
	 */
	public void setState(CarState newState) {
		this.state = newState;
	}

	/**
	 * Updates the Car's angle
	 *
	 * @param angle the new angle
	 */
	public void setAngle(int angle) {
		this.angle = angle;
	}

	/**
	 * Starts the car as an autonomous entity that can move in the given roundabout
	 */
	public void run() {
		while (true) {//forcing queue entry
			LOGGER.info("The car with license plate "+ this.licensePlate +" is trying to enter in the roundabout");
			if (roundAbout.enterLine(this)) {
				LOGGER.info("The car with license plate "+ this.licensePlate +" has entered in the roundabout");
				ArrayList<Node> shortestPath = (ArrayList<Node>) roundAbout.getShortestPath(this.source.getRef(), this.destination.getRef());
				this.path = this.roundAbout.getFullPath(shortestPath);
				Node currentNode = null;
				Node nextNode = null;
				for (int i = 0; i < path.size(); i++) {
					if (i > 0) {
						currentNode = path.get(i - 1);
						nextNode = path.get(i);
					} else {
						currentNode = path.get(i);
						nextNode = path.get(i);
					}
					while (!roundAbout.canMove(nextNode, currentNode, this)) {
						try {
							sleep(1000);//volta a tentar daqui a 1 seg
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					try {
						//if (this.path.size() > i + 1)
							runCar(currentNode, nextNode);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				this.roundAbout.removeCar(this);
				break;//terminate thread execution
			}
			try {
				sleep(1000);//volta a tentar entrar na queue daqui a 1 seg
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Receiving the current and next Node, repaints the car in the roundabout, and alters it's angle
	 *
	 * @param currentNode the current Node
	 * @param nextNode the next Node
	 * @throws InterruptedException
	 */
	private void runCar(Node currentNode,Node nextNode) throws InterruptedException {
		this.x = currentNode.getX();
		this.y = currentNode.getY();
		this.angle = (int)Roundabout.calcRotationAngleInDegrees(currentNode, nextNode);
		this.roundAbout.repaint();
		Thread.sleep(30);
	}

	/**
	 * Returns a String representation of the Car
	 *
	 * @return String representation of the Car
	 */
	@Override
	public String toString() {
		return "Car{" +
				"licensePlate='" + licensePlate + '\'' +
				", state=" + state +
				", x=" + x +
				", y=" + y +
				'}';
	}

}
