package main.java.socof.entities;

/**
 * Enum that provides the different states a Car can adopt during it's life-cycle.
 * A Car can be:
 * 	- ENTERING before it enters the Roundabout;
 * 	- INSIDE when it is inside the Roundabout;
 * 	- LEAVING when the car enters its exit lane.
 */
public enum CarState {
	ENTERING, INSIDE, LEAVING
}
