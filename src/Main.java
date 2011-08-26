package com.ringlord.xs3d;

import java.awt.HeadlessException;

import javax.swing.SwingUtilities;

class Main
{
  public static void main( final String[] args )
  {
    try
      {
        SwingUtilities.invokeLater( new GUI() );
      }
    catch( final HeadlessException noGUI )
      {
        System.err.println( "Non-graphical environment?" );
      }
  }
}
