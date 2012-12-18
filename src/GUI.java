package com.ringlord.xs3d;

import java.awt.Color;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

// This file is part of XS3D.
//
// XS3D is free software: you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// XS3D is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with XS3D.  If not, see <http://www.gnu.org/licenses/>.

/**
 * The GUI component, launched by Main. It creates a {@link JFrame}
 * and sets its content pane to a {@link Viewer3d} object. Then it
 * builds a {@link Mesh} (so that something is displayed; you'd want
 * to change that, maybe load a Mesh from a 3D object file somehow, in
 * order to make this a more useful program), and adds that Mesh to
 * the Viewer3d object.
 *
 * @author K. Udo Schuermann
 **/
class GUI
  implements Runnable
{
  // ==============================
  // Runnable
  // ==============================
  @Override
  public void run()
  {
    this.frame = new JFrame( "XS3D" );

    final Viewer3d viewer3d = new Viewer3d();
    final InputHandler h = new InputHandler( viewer3d );
    h.addMeshFocusListener( new MeshFocusListener()
      {
        public void meshFocusGained( final MeshFocusEvent e )
        {
          System.out.println( new java.util.Date()+" in focus: "+e.getFocusInfo() );
        }
        public void meshFocusLost( final MeshFocusEvent e )
        {
          System.out.println( new java.util.Date()+" NO FOCUS (was: "+e.getFocusInfo()+")" );
        }
      } );
    viewer3d.requestFocusInWindow();
    this.frame.setContentPane( viewer3d );

    this.frame.pack();

    // You could adjust the initial window position and size if you like

    this.frame.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
    this.frame.addWindowListener( new WindowAdapter()
      {
        public void windowClosing( final WindowEvent e )
          {
            // You could save the window position and size for next
            // time, if you like
            System.exit( 0 );
          }
      } );
    this.frame.setVisible( true );

    // ======================================================================

    System.err.println( "GUI is building something simple to display.\n" );

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

    // Collect the edges into surfaces
    final Mesh.Face s0 = new Mesh.Face( Color.green, e0, e1, e2, e3 );
    final Mesh.Face s1 = new Mesh.Face( Color.blue,  e4, e5, e6, e7 );

    final Mesh mesh = new Mesh();
    // true: add the points so that representations of points are rendered (as little spheres)
    if( true )
      {
        mesh.add( p0 );
        mesh.add( p1 );
        mesh.add( p2 );
        mesh.add( p3 );

        mesh.add( p4 );
        mesh.add( p5 );
        mesh.add( p6 );
        mesh.add( p7 );
      }

    // true: add the edges so that representations of edges are rendered (as lines)
    if( true )
      {
        mesh.add( e0 );
        mesh.add( e1 );
        mesh.add( e2 );
        mesh.add( e3 );

        mesh.add( e4 );
        mesh.add( e5 );
        mesh.add( e6 );
        mesh.add( e7 );
      }

    // true: add the faces so that faces are rendered (filled with solid color)
    if( true )
      {
        mesh.add( s0 );
        mesh.add( s1 );
      }

    // Add the Mesh to the viewer and voilá, instant 3D
    viewer3d.add( mesh );
  }

  private JFrame frame;
}
