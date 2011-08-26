package com.ringlord.xs3d;

import java.awt.Color;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * The GUI component, launched by Main.
 *
 * @author K. Udo Schuermann
 **/
class GUI
  implements Runnable
{
  // ==============================
  // Runnable
  // ==============================
  public void run()
  {
    this.frame = new JFrame( "DDD" );

//  Build your GUI here and finally add it to the frame's content pane
//  this.frame.getContentPane().add( ... );

    final Viewer3d viewer3d = new Viewer3d();

    this.frame.getContentPane().add( viewer3d );

    this.frame.pack();

//  Adjust the initial window position and size if you like

    this.frame.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
    this.frame.addWindowListener( new WindowAdapter()
      {
        public void windowClosing( final WindowEvent e )
          {
            // Save the window position and size for next time
            // and shutdown
            System.exit( 0 );
          }
      } );
    this.frame.setVisible( true );

    // ======================================================================

    // An arrangement of points in 3D space
    final Mesh.Point3d p0 = new Mesh.Point3d(-1,-1,-1);
    final Mesh.Point3d p1 = new Mesh.Point3d(-1,-1, 1);
    final Mesh.Point3d p2 = new Mesh.Point3d(-1, 1,-1);
    final Mesh.Point3d p3 = new Mesh.Point3d(-1, 1, 1);
    final Mesh.Point3d p4 = new Mesh.Point3d( 1,-1,-1);
    final Mesh.Point3d p5 = new Mesh.Point3d( 1,-1, 1);
    final Mesh.Point3d p6 = new Mesh.Point3d( 1, 1,-1);
    final Mesh.Point3d p7 = new Mesh.Point3d( 1, 1, 1);

    // Connect some of the points into edges
    final Mesh.Edge e0 = new Mesh.Edge( Color.red, p0, p1 );
    final Mesh.Edge e1 = new Mesh.Edge( Color.red, p1, p2 );
    final Mesh.Edge e2 = new Mesh.Edge( Color.red, p2, p3 );
    final Mesh.Edge e3 = new Mesh.Edge( Color.red, p3, p0 );

    final Mesh.Edge e4 = new Mesh.Edge( Color.yellow, p4, p5 );
    final Mesh.Edge e5 = new Mesh.Edge( Color.yellow, p5, p6 );
    final Mesh.Edge e6 = new Mesh.Edge( Color.yellow, p6, p7 );
    final Mesh.Edge e7 = new Mesh.Edge( Color.yellow, p7, p4 );

    // Collect some of the edges into surfaces
    final Mesh.Surface s0 = new Mesh.Surface( Color.green, e0, e1, e2, e3 );
    final Mesh.Surface s1 = new Mesh.Surface( Color.blue,  e4, e5, e6, e7 );

    // And finally construct the Mesh by adding all these points,
    // edges, and surfaces to it.
    final Mesh mesh = new Mesh();
    mesh.add( p1 );
    mesh.add( p2 );
    mesh.add( p3 );
    mesh.add( p4 );
    mesh.add( p5 );
    mesh.add( p6 );
    mesh.add( p7 );

    mesh.add( e0 );
    mesh.add( e1 );
    mesh.add( e2 );
    mesh.add( e3 );

    mesh.add( e4 );
    mesh.add( e5 );
    mesh.add( e6 );
    mesh.add( e7 );

    mesh.add( s0 );
    mesh.add( s1 );

    // Add the Mesh to the viewer and voilá, instant 3D
    viewer3d.add( mesh );
  }

  private JFrame frame;
}
