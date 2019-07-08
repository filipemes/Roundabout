package main.java.socof.entities;

import  main.java.socof.dataStructures.DepthFirstDirectedPaths;
import  main.java.socof.dataStructures.Digraph;
import  main.java.socof.dataStructures.In;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Thread.sleep;

/**
 * Class that represents a Roundabout, acting as common data structure
 */
public class Roundabout extends JPanel {

    // List of entrances to the roundabout
	private List<ConcurrentLinkedQueue<Car>> entranceLists;

	// List of Nodes that make up the roundabout graph
	private List<Node> roadNodesList;

	// Digraph that represents the structure of the roundabout
	private Digraph roadNetwork;

	// Path to file location
	public static final String absPathToResource=Paths.get("src").getFileName().toAbsolutePath().toString()+"/main/resources";

	// List of source Nodes
	public List<Node> sourceNodes;

	// List of destination Nodes
	public List<Node> destinationNodes;

	// List of cars associated to the roundabout
	public List<Car> carList;

	// List of pedestrians associated to the roundabout
	public List<Pedestrian> pedestrianList;

	// Buffered image of the roundabout
	private BufferedImage bufferedImage;

	// Image of the roundabout
	private Image image;

    /**
     * Creates a roundabout with default information
     */
	public Roundabout() {
		roadNodesList=new ArrayList<>();
		entranceLists = new ArrayList<>();
		sourceNodes=new ArrayList<>();
		destinationNodes=new ArrayList<>();
		carList=new ArrayList<>();
		pedestrianList=new ArrayList<>();
		loadEdgesFromFile();
		loadNodesFromFile();
		int i;
		for(i = 0; i < sourceNodes.size(); i++) {
			entranceLists.add(new ConcurrentLinkedQueue<>());
		}
		initGUI();
	}

    /**
     * Receiving a list of Nodes, returns a list of the same Nodes and all Nodes between them
     *
     * @param path original Node list
     * @return complete Node list
     */
	public ArrayList<Node> getFullPath(ArrayList<Node> path) {
		double x,y;
		ArrayList<Node>fullPath=new ArrayList<>();
		for(int i=0;i<path.size(); i++) {
			fullPath.add(new Node(path.get(i).getX(),path.get(i).getY(),path.get(i).getRef(),path.get(i).getType()));
			for (double k = 0; k <= 1; k = 0.01 + k) {
				if (path.size() > (i + 1)) {
					x = lerp(path.get(i).getX(), path.get(i + 1).getX(), k);
					y =  lerp(path.get(i).getY(), path.get(i + 1).getY(), k);

					if(!(x == path.get(i).getX() && y == path.get(i).getY())){
						fullPath.add(new Node(x,y,-1,""));
					}
				}
			}
		}
		return fullPath;
	}

    /**
     * Receiving a center Node and a target Node calculates the angle between them
     *
     * @param centerPt center Node
     * @param targetPt target Node
     * @return angle between Nodes
     */
    public static double calcRotationAngleInDegrees(Node centerPt, Node targetPt) {
        double theta = Math.atan2(targetPt.getY() - centerPt.getY(), targetPt.getX() - centerPt.getX());
        double angle = Math.toDegrees(theta);
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    /**
     * Receiving two points and an alpha, returns a point between them
     *
     * @param point1 the first point
     * @param point2 the second point
     * @param alpha the alpha
     * @return the point between the given points
     */
    private double lerp(double point1, double point2, double alpha) {
    	return point1 + alpha * (point2 - point1);
	}

    /**
     * Receiving a reference returns the Node it is associated to
     *
     * @param ref Node reference
     * @return the Node associated with the reference
     */
	public Node getNodeByRef(int ref){
		for(Node n: this.roadNodesList){
			if(n.getRef()==ref)
				return n;
		}
		return null;
	}

	/**
	 * Inserts a Pedestrian into the roundabout pedestrian list
	 *
	 * @param p the pedestrian to be inserted
	 */
	public synchronized void addPedestrian(Pedestrian p){
		if(!this.pedestrianList.contains(p)){
			this.pedestrianList.add(p);
		}
	}

	/**
	 * Removes a Pedestrian from the roundabout pedestrian list
	 *
	 * @param p the pedestrian to be removed
	 */
	public synchronized void removePedestrian(Pedestrian p){
		if(this.pedestrianList.contains(p)){
			this.pedestrianList.remove(p);
		}
	}

	/**
	 * Receiving a license-plate and coordinates, draws the received licence-plate
	 *
	 * @param licensePlate a Car's license-plate
	 * @param x the x-axis value
	 * @param y the y-axis value
	 */
	private void drawLicensePlate(String licensePlate,double x,double y){
		this.getGraphics().setColor(Color.WHITE);
		this.getGraphics().drawString(licensePlate, (int) x + 15, (int) y);
	}

	/**
	 * Receiving a MouseEvent shows the license-plate of the point given by the event
	 *
	 * @param evt the MouseEvent
	 */
	private synchronized void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
		java.awt.Point p = evt.getPoint();
		for (Car c:this.carList) {
			if(isInside(c.getX(),c.getY(),33,p.getX(),p.getY())){
				drawLicensePlate(c.getLicensePlate(),c.getX(),c.getY());
			}
		}
	}

    /**
     * Initializes the UI representing the roundabout and its cars
     */
	private void initGUI(){
		addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			public void mouseMoved(java.awt.event.MouseEvent evt) {
				formMouseMoved(evt);
			}
		});
		try {
			bufferedImage = ImageIO.read(new File( main.java.socof.entities.Roundabout.absPathToResource+"/roundabout.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.image = bufferedImage.getScaledInstance(1280, 720, Image.SCALE_SMOOTH);
		Dimension size = new Dimension(1280, 720);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		setLayout(null);
	}

    /**
     * Paints the roundabout
     *
     * @param g
     */
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.clearRect(0, 0, this.image.getWidth(null),  this.image.getHeight(null));
		g.drawImage(this.image, 0, 0, null);
		//drawPoints(g);
		drawPedestrians(g);
		drawAllCars(g);
	}

	/**
	 * Draws all pedestrians in roundabout pedestrian list
	 *
	 * @param g
	 */
	private void drawPedestrians(Graphics g){
		for(Pedestrian p:pedestrianList){
			g.fillOval((int) Math.round(p.getX()), (int) Math.round(p.getY()),10,10);
		}
	}

    /**
     * Draws all the Nodes in the roundabout
     *
     * @param g
     */
	private synchronized void drawPoints(Graphics g){
		for(Node n:this.roadNodesList) {
			g.fillOval((int) Math.round(n.getX()), (int) Math.round(n.getY()),10,10);
		}
	}

    /**
     * Draws all cars associated with the roundabout
     *
     * @param g
     */
	private synchronized void drawAllCars(Graphics g){
		BufferedImage bufferedImageCar=null;
		for(Car c:this.carList){
			double rotationRequired = Math.toRadians(c.getAngle());
			bufferedImageCar=c.getSpriteCar();
			Image imageCar = bufferedImageCar.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
			AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, 12.5, 12.5);
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			AffineTransform t = new AffineTransform();
			t.translate(c.getX()-6, c.getY()-6);
			t.scale(1, 1);
			((Graphics2D)g).drawImage(op.filter(toBufferedImage(imageCar), null), t, null);
		}
	}

    /**
     * Converts an Imaged to a BufferedImage
     *
     * @param img Image to be converted
     * @return the resulting BufferedImage
     */
	private static BufferedImage toBufferedImage(Image img)
	{
		if (img instanceof BufferedImage)
			return (BufferedImage) img;

		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();
		return bimage;
	}

    /**
     * Loads Nodes from 'nodes.txt' file
     */
	private void loadNodesFromFile(){
		In in = new In(absPathToResource+"/nodes.txt");
		while(!in.isEmpty()){
			String e=in.readLine();
			String [] node=e.split(";");
			int x = Integer.valueOf(node[0]);
			int y = Integer.valueOf(node[1]);
			int ref = Integer.valueOf(node[2]);
			String role = node[3];
			Node n=new Node(x,y,ref,role);
			roadNodesList.add(n);
			if(role.equals("source")){
				this.sourceNodes.add(n);
			} else if(role.equals("destination")){
				this.destinationNodes.add(n);
			}
		}
	}

    /**
     * Loads Edges from 'edges.txt' file
     */
	private void loadEdgesFromFile(){
		    In in = new In(absPathToResource+"/edges.txt");
		    this.roadNetwork=new Digraph(in);
	}

    /**
     * Receiving the references to a starting and destination Node,
     * determines the path between and including them
     *
     * @param start the reference to the starting Node
     * @param destination the reference to the destination Node
     * @return the path between the given references, including their Nodes
     */
	public Iterable<Node> getShortestPath(int start,int destination){
		DepthFirstDirectedPaths depthFirstDirectedPaths=new DepthFirstDirectedPaths(this.roadNetwork,start);
		Iterable<Integer> shortestPath=depthFirstDirectedPaths.pathTo(destination);
		if(shortestPath==null)return null;
		Iterable<Node> iterableShortestPath=new ArrayList<Node>();
		for(Integer i:shortestPath){
			((ArrayList<Node>) iterableShortestPath).add(this.roadNodesList.get(i));
		}
		return iterableShortestPath;
	}

    /**
     * Inserts a Car into the roundabout car list
     *
     * @param car the car to be inserted
     */
	public synchronized void insertCar(Car car){
		if(!carList.contains(car))
			carList.add(car);
	}

    /**
     * Removes a Car from the roundabout car list
     *
     * @param car the car to be removed
     */
	public synchronized void removeCar(Car car){
		if(carList.contains(car))
			carList.remove(car);
	}

    /**
     * Determines if a given car's license-plate exists in the car list
     *
     * @param car the car to be checked
     * @return true if the license-plate exists in the list, false if it doesn't
     */
	private synchronized boolean containsLicensePlate(Car car) {
		if(carList.isEmpty())
			return false;

		for(Car c: carList) {
			if(c.getLicensePlate() == car.getLicensePlate()){
				return true;
			}
		}

		return false;
	}

    /**
     * Places a given Car in an entrance based on its source Node
     *
     * @param car the Car to be placed
     * @return true if the Car is placed successfully, false if it isn't
     */
	public boolean enterLine(Car car) {
		Node n = car.getSource();
		if(sourceNodes.contains(n)){
			int index = sourceNodes.indexOf(n);
			synchronized(this) {
				if (entranceLists.get(index).size() > 7 || containsLicensePlate(car)) {
					return false;
				}
			}
			if(entranceLists.get(index).add(car)) {
				insertCar(car);
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

    /**
     * For a given point determines if there are any cars inside of its radius
     *
     * @param x the point's x-axis
     * @param y the point's y-axis
     * @return true if there is a car inside the radius, false if not
     */
	private boolean checkRoundaboutPoint(double x,double y){
		for(Car c:this.carList){
			if(isInside(x,y,35,c.getX(),c.getY())) return true;
		}
		return false;
	}

    /**
     * For a given Node, determines if the Nodes that it leads to are free
     *
     * @param node the Node to be checked
     * @return true if one of the Nodes is free, false otherwise
     */
    private boolean handleRoundaboutZone(Node node){
		boolean result1=false;
		boolean result2=false;
		switch (node.getRef()){
			case 4:
				result1=checkRoundaboutPoint(getNodeByRef(35).getX(),getNodeByRef(35).getY());
				result2=checkRoundaboutPoint(getNodeByRef(7).getX(),getNodeByRef(7).getY());
				break;
			case 12:
				result1=checkRoundaboutPoint(getNodeByRef(8).getX(),getNodeByRef(8).getY());
				result2=checkRoundaboutPoint(getNodeByRef(15).getX(),getNodeByRef(15).getY());
				break;
			case 21:
				result1=checkRoundaboutPoint(getNodeByRef(16).getX(),getNodeByRef(16).getY());
				result2=checkRoundaboutPoint(getNodeByRef(24).getX(),getNodeByRef(24).getY());
				break;
			case 28:
				result1=checkRoundaboutPoint(getNodeByRef(27).getX(),getNodeByRef(27).getY());
				result2=checkRoundaboutPoint(getNodeByRef(32).getX(),getNodeByRef(32).getY());
				break;
		}
		if(!result1&&!result2) return false;
		return true;
    }

    /**
     * Receiving a car, a current and next Node, determines if it can move
     *
     * @param nextNode the Car's next Node
     * @param currentNode the Car's current Node
     * @param car the Car being considered
     * @return true if the Car can move, false otherwise
     */
	public synchronized boolean canMove(Node nextNode,Node currentNode,Car car) {
		if(currentNode.getType().compareTo("entrance")==0){
			if(!canEnterRoundabout(currentNode,car)){
				return false;
			}
            car.setState(CarState.INSIDE);
		}
		else if(currentNode.getType().compareTo("exit")==0){
			car.setState(CarState.LEAVING);
		}
		for(Pedestrian p: pedestrianList) {
			if(isInside(nextNode.getX()-6,nextNode.getY()-6,43,p.getX()-6,p.getY()-6)){
				return false;
			}
		}

		for(Car c: carList) {
			if(c!=car && isInside(nextNode.getX()-6,nextNode.getY()-6,33,c.getX()-6,c.getY()-6)){
                return false;
            }
		}

		car.updatePlacement(nextNode.getX(), nextNode.getY());
	    return true;
    }

    /**
     * Returns a random source Node
     *
     * @return a source Node
     */
	public Node randomSource(){
		Random rand = new Random();
		return this.sourceNodes.get(rand.nextInt(this.sourceNodes.size()));
	}

    /**
     * Based on a source Node, returns a destination Node
     *
     * @param source a given source Node
     * @return a destination Node
     */
	public Node randomDestination(Node source){
		for(Node n:this.destinationNodes){
			Iterable<Node>path=this.getShortestPath(source.getRef(),n.getRef());
			if(path!=null&&((ArrayList<Node>) path).size()>1)
				return n;
		}
		return null;
	}

    /**
     * Receiving a Node and a Car, determines if the Car can enter the given Node
     *
     * @param node a entrance Node
     * @param car a given Car
     * @return true if the Car can enter, false otherwise
     */
	private boolean canEnterRoundabout(Node node,Car car) {
		if(handleRoundaboutZone(node))
			return false;
		for(ConcurrentLinkedQueue q : entranceLists){
			if(q.contains(car))
				return q.remove(car);
		}
		return true;
	}

    /**
     * Receiving a center point, a radius and another point, determines if the second point
     * is inside the center point
     *
     * @param circle_x the x-axis for the center point
     * @param circle_y the y-axis for the center point
     * @param radius the radius value
     * @param x the x-axis for the second point
     * @param y the y-axis for the second point
     * @return true if the second point is inside the radius, false otherwise
     */
    private boolean isInside(double circle_x, double circle_y, double radius, double x, double y) {
        if ((x - circle_x) * (x - circle_x) +
                (y - circle_y) * (y - circle_y) <= radius * radius)
            return true;
        else
            return false;
    }

}
