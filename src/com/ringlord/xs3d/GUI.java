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
    final JFrame frame = new JFrame( "XS3D" );

    final Viewer3d viewer3d = new Viewer3d();
    final InputHandler h = new InputHandler( viewer3d );
    h.addMeshFocusListener( new MeshFocusListener()
      {
        public void meshFocusGained( final MeshFocusEvent e )
        {
//          System.out.println( new java.util.Date()+" in focus: "+e.getFocusInfo() );
        }
        public void meshFocusLost( final MeshFocusEvent e )
        {
//          System.out.println( new java.util.Date()+" NO FOCUS (was: "+e.getFocusInfo()+")" );
        }
      } );
    viewer3d.requestFocusInWindow();
    frame.setContentPane( viewer3d );

    frame.pack();

    // You could adjust the initial window position and size if you like

    frame.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
    frame.addWindowListener( new WindowAdapter()
      {
        public void windowClosing( final WindowEvent e )
          {
            // You could save the window position and size for next
            // time, if you like
            System.exit( 0 );
          }
      } );
    frame.setVisible( true );

    // ======================================================================

    System.err.println( "GUI is building a simple cube to display.\n" );

    final Color focusColor  = new Color( 191, 0, 0, 212 );
    final Color selectColor = new Color( 191, 0, 0, 240 );
    final Color edgeColor = new Color( 0, 0, 0 );

    final Mesh.Coloring edgeColoring  = new Mesh.Coloring( edgeColor, focusColor, selectColor );

    final Mesh.Coloring faceColoring0 = new Mesh.Coloring( Color.cyan,   focusColor, selectColor );
    final Mesh.Coloring faceColoring1 = new Mesh.Coloring( Color.blue,   focusColor, selectColor );
    final Mesh.Coloring faceColoring2 = new Mesh.Coloring( Color.orange, focusColor, selectColor );
    final Mesh.Coloring faceColoring3 = new Mesh.Coloring( Color.white,  focusColor, selectColor );
    final Mesh.Coloring faceColoring4 = new Mesh.Coloring( Color.green,  focusColor, selectColor );
    final Mesh.Coloring faceColoring5 = new Mesh.Coloring( Color.pink,   focusColor, selectColor );

    // An arrangement of points in 3D space
    final Mesh.Point3d p0 = new Mesh.Point3d(-1,-1,-1);
    final Mesh.Point3d p1 = new Mesh.Point3d(-1,-1, 1);
    final Mesh.Point3d p2 = new Mesh.Point3d(-1, 1, 1);
    final Mesh.Point3d p3 = new Mesh.Point3d(-1, 1,-1);
    final Mesh.Point3d p4 = new Mesh.Point3d( 1,-1,-1);
    final Mesh.Point3d p5 = new Mesh.Point3d( 1,-1, 1);
    final Mesh.Point3d p6 = new Mesh.Point3d( 1, 1, 1);
    final Mesh.Point3d p7 = new Mesh.Point3d( 1, 1,-1);

    // Connect some of the points into edges
    final Mesh.Edge e10 = new Mesh.Edge( edgeColoring, p0, p1 );
    final Mesh.Edge e11 = new Mesh.Edge( edgeColoring, p1, p2 );
    final Mesh.Edge e12 = new Mesh.Edge( edgeColoring, p2, p3 );
    final Mesh.Edge e13 = new Mesh.Edge( edgeColoring, p3, p0 );

    final Mesh.Edge e20 = new Mesh.Edge( null, p4, p5 );
    final Mesh.Edge e21 = new Mesh.Edge( null, p5, p6 );
    final Mesh.Edge e22 = new Mesh.Edge( null, p6, p7 );
    final Mesh.Edge e23 = new Mesh.Edge( null, p7, p4 );

    final Mesh.Edge e30 = new Mesh.Edge( null, p2, p6 );
    final Mesh.Edge e31 = new Mesh.Edge( null, p6, p7 );
    final Mesh.Edge e32 = new Mesh.Edge( null, p7, p3 );
    final Mesh.Edge e33 = new Mesh.Edge( null, p3, p2 );

    final Mesh.Edge e40 = new Mesh.Edge( null, p0, p3 );
    final Mesh.Edge e41 = new Mesh.Edge( null, p3, p7 );
    final Mesh.Edge e42 = new Mesh.Edge( null, p7, p4 );
    final Mesh.Edge e43 = new Mesh.Edge( null, p4, p0 );

    final Mesh.Edge e50 = new Mesh.Edge( null, p1, p2 );
    final Mesh.Edge e51 = new Mesh.Edge( null, p2, p6 );
    final Mesh.Edge e52 = new Mesh.Edge( null, p6, p5 );
    final Mesh.Edge e53 = new Mesh.Edge( null, p5, p1 );

    final Mesh.Edge e60 = new Mesh.Edge( null, p5, p4 );
    final Mesh.Edge e61 = new Mesh.Edge( null, p4, p0 );
    final Mesh.Edge e62 = new Mesh.Edge( null, p0, p1 );
    final Mesh.Edge e63 = new Mesh.Edge( null, p1, p5 );

    // Collect the edges into faces
    final Mesh.Face f0 = new Mesh.Face( faceColoring0, e10, e11, e12, e13 );
    final Mesh.Face f1 = new Mesh.Face( faceColoring1, e20, e21, e22, e23 );
    final Mesh.Face f2 = new Mesh.Face( faceColoring2, e30, e31, e32, e33 );
    final Mesh.Face f3 = new Mesh.Face( faceColoring3, e40, e41, e42, e43 );
    final Mesh.Face f4 = new Mesh.Face( faceColoring4, e50, e51, e52, e53 );
    final Mesh.Face f5 = new Mesh.Face( faceColoring5, e60, e61, e62, e63 );

    // Create a mesh for each face, though we could simply add all the
    // faces to a single Mesh. The benefit of keeping them separated
    // is the ability to remove individual meshes easily. As all of
    // our meshes share the same vertices, however, moving one vertex
    // will not separate the individual faces, but rather bend and
    // twist the cube.
    final Mesh mesh0 = new Mesh();
    final Mesh mesh1 = new Mesh();
    final Mesh mesh2 = new Mesh();
    final Mesh mesh3 = new Mesh();
    final Mesh mesh4 = new Mesh();
    final Mesh mesh5 = new Mesh();

    // true: add the points so that representations of points are rendered (as little spheres)
    if( true )
      {
        mesh0.add( p0 );
        mesh0.add( p1 );
        mesh0.add( p2 );
        mesh0.add( p3 );

        mesh1.add( p4 );
        mesh1.add( p5 );
        mesh1.add( p6 );
        mesh1.add( p7 );
      }

    // true: add the edges so that representations of edges are rendered (as lines)
    if( true )
      {
        mesh0.add( e10 );
        mesh0.add( e11 );
        mesh0.add( e12 );
        mesh0.add( e13 );

        mesh1.add( e20 );
        mesh1.add( e21 );
        mesh1.add( e22 );
        mesh1.add( e23 );
      }

    // true: add the faces so that faces are rendered (filled with solid color)
    if( true )
      {
        mesh0.add( f0 );
        mesh1.add( f1 );
        mesh2.add( f2 );
        mesh3.add( f3 );
        mesh4.add( f4 );
        mesh5.add( f5 );
      }

    // Add the Meshes to the viewer and voilá, instant 3D
    viewer3d.add( mesh0 );
    viewer3d.add( mesh1 );
    viewer3d.add( mesh2 );
    viewer3d.add( mesh3 );
    viewer3d.add( mesh4 );
    viewer3d.add( mesh5 );

    new Thread()
    {
      public void run()
      {
        try
          {
            while( true )
              {
                mesh3.setVisible( ! mesh3.isVisible() );
                viewer3d.repaint();
                Thread.sleep( 1000 );
              }
          }
        catch( InterruptedException x )
          {
          }
      }
    }.start();

    // redundant as Meshes is focusable by default
    mesh0.setFocusable( true );
    //    mesh1.setFocusable( false );
  }
}
