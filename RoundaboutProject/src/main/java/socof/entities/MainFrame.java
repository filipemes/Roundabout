package main.java.socof.entities;

import main.java.socof.entities.Roundabout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainFrame extends JFrame {
    
    private Roundabout roundabout;
    
    public MainFrame(Roundabout roundabout){
        setTitle("Autonomous Car Interaction at Roundabouts");
        setSize(1280,720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.getContentPane().add(roundabout);
        this.pack();
        this.setVisible(true);
    }



}
