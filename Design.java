package com.lynn.project;
import java.awt.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JFrame;

public class Design
{
   public static void main(String args[])
   {
  	JFrame splash = new JFrame("Impervious Surface Measurer");	
  	splash.setSize(1000, 1000);
  	splash.setLocation(500, 25);
  	splash.setDefaultCloseOperation(
                       JFrame.EXIT_ON_CLOSE);
  	splash.setContentPane(new DesignPanel(splash));
  	splash.setVisible(true);
  	splash.setResizable(true);    	
   }
}
