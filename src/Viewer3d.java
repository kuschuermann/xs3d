import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JComponent;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * <p>The 3D Viewer/Renderer for the elements defined by the {@link
 * Mesh}. The component's preferred (initial) size is 480x300
 * pixels.</p>
 *
 * <p>To rotate the view, move mouse button while holding down the
 * left button. To zoom in/out, use the mouse wheel.</p>
 *
 * @author K. Udo Schuermann
 **/
class Viewer3d
  extends JComponent
  implements ChangeListener,
             MouseListener,
             MouseMotionListener,
             MouseWheelListener
{
  /**
   * Indicates whether to render a numeric count (starting at 1) near
   * points, edges, and surfaces as they are being rendered from the
   * rear-most to the front, to help identify the drawing order of
   * {@link Mesh} elements.
   **/
  public static final boolean RENDER_DRAWING_DEPTH = false;

  Viewer3d()
  {
    super();
    setViewAngle( new Vector3d(0.0d, Math.PI, Math.PI) );
    setScreenPosition( new Vector3d(0,0,50) );

    this.modelScale = 1000; // a fudge factor to control distortion

    setOpaque( true );

    addMouseListener( this );
    addMouseMotionListener( this );
    addMouseWheelListener( this );
  }

  public void add( final Mesh mesh )
  {
    meshes.add( mesh );
    mesh.addChangeListener( this );
    repaint();
  }
  public void remove( final Mesh mesh )
  {
    meshes.remove( mesh );
    mesh.removeChangeListener( this );
    repaint();
  }

  public Dimension getPreferredSize()
  {
    return new Dimension( 480, 300 );
  }

  /**
   * Set the view angle. It defaults to (0,pi,pi)
   **/
  public void setViewAngle( final Vector3d viewAngle )
  {
    setViewAngle( viewAngle.x,
                  viewAngle.y,
                  viewAngle.z );
  }
  public void setViewAngle( final double x,
                            final double y,
                            final double z )
  {
    // precalculate the values needed by the project method
    this.CT = Math.cos( x );    // cos theta
    this.ST = Math.sin( x );    // sin theta
    this.CP = Math.cos( y );    // cos phi
    this.SP = Math.sin( y );    // sin phi

    this.viewAngleX = x;
    this.viewAngleY = y;
    this.viewAngleZ = z;

    repaint();
  }

  /**
   * Set the screen position. It defaults to (0,0,50).
   **/
  public void setScreenPosition( final Vector3d screenPosition )
  {
    this.screenPositionX = screenPosition.x;
    this.screenPositionY = screenPosition.y;
    this.screenPositionZ = screenPosition.z;

    repaint();
  }

  /**
   * Project the given point in 3D space to a 2D coordinate on the
   * screen. This is affected by the given screen center coordinate,
   * the {@link #setViewAngle(Vector3d)} and the {@link
   * #setScreenPosition(Vector3d)}.
   **/
  private Point2d project( final double xScreenCenter,
                           final double yScreenCenter,
                           final Mesh.Point3d point )
  {
    final double px = point.getX();
    final double py = point.getY();
    final double pz = point.getZ();

    final double x = (screenPositionX +
                      (px * CT) -
                      (py * ST));
    final double y = (screenPositionY +
                      (px * ST * SP) +
                      (py * CT * SP) +
                      (pz * CP));
    final double z = ((screenPositionZ +
                       (px * ST * CP) +
                       (py * CT * CP) -
                       (pz * SP)));
    final double temp = viewAngleZ / z;

    final Point2d p2d = new Point2d();
    p2d.set( (int)(xScreenCenter + modelScale * temp * x),
             (int)(yScreenCenter - modelScale * temp * y),
             z ); // z is the distance from the viewer
    return p2d;
  }

  public void stateChanged( final ChangeEvent e )
  {
    repaint();
  }

  /**
   * This method is invoked by Swing whenever a repaint event is
   * handled.
   **/
  public void paintComponent( final Graphics g )
  {
    final Graphics2D g2 = (Graphics2D)g;

    final Rectangle bounds = getBounds();

    g2.setColor( Color.black );
    g2.fillRect( 0, 0, bounds.width, bounds.height );

    final double xScreenCenter = bounds.width / 2.0d;
    final double yScreenCenter = bounds.height / 2.0d;

    // Collect ZRef objects which we can sort to ensure drawing from
    // back to front, and therefore effect proper depth perception,
    // especially when it comes to surfaces. It doesn't matter in
    // which order we process the points, edges, and surfaces of a
    // Mesh (in the loop below) but we'll do it in a "natural" order,
    // points first, edges next, and surfaces last.
    final List<ZRef> zref = new ArrayList<ZRef>();
    for( Mesh mesh : meshes )
      {
        for( Mesh.Point3d p : mesh.getPoints() )
          {
            final Point2d p2d = project( xScreenCenter,
                                         yScreenCenter,
                                         p );
            if( p2d.depth > 0 )
              {
                // The ZRef will take the Point2d's z-coordinate to
                // determine the distance from the viewer.
                zref.add( new ZRef(p2d) );
              }
          }
        for( Mesh.Edge e : mesh.getEdges() )
          {
            final Point2d p2d1 = project( xScreenCenter,
                                          yScreenCenter,
                                          e.getHead() );
            final Point2d p2d2 = project( xScreenCenter,
                                          yScreenCenter,
                                          e.getTail() );
            if( p2d1.depth > 0 )
              {
                if( p2d2.depth > 0 )
                  {
                    // The line is fully in front of the viewer; the
                    // average distance of each point's z-coordinate
                    // will determine how far this line is from the
                    // viewer.
                    zref.add( new ZRef(e.getColor(),p2d1,p2d2) );
                  }
                else
                  {
                    // The start of the line is in front of the view,
                    // the end of it is behind

                    // @@@ find intersection in x,y construct a new
                    // Point2d and store that, instead
                  }
              }
            else if( p2d2.depth > 0 )
              {
                // The start of the line is behind the viewer, the end
                // of it is in front

                // @@@ find intersection in x,y construct a new
                // Point2d and store that, instead
              }
          }
        nextMesh:
        for( Mesh.Surface s : mesh.getSurfaces() )
          {
            points.clear(); // start with an empty list of points
            for( Mesh.Edge e : s )
              {
                final Point2d p2d1 = project( xScreenCenter,
                                              yScreenCenter,
                                              e.getHead() );
                final Point2d p2d2 = project( xScreenCenter,
                                              yScreenCenter,
                                              e.getTail() );
                if( (p2d1.depth < 0) ||
                    (p2d2.depth < 0) )
                  {
                    // One or both points of this edge lies behind the
                    // viewer, so let's not render any part of the
                    // surface because it gets really complicated
                    // trying to determine intersection points, and
                    // render only subsections of the surface.
                    continue nextMesh;
                  }
                // As our edges should be defining a CLOSED series of
                // points, we simply capture the first point of each
                // edge
                points.add( p2d1 );
              }
            // If we got here then we didn't do a 'continue nextMesh'
            // in the loop above, meaning that we have a full set of
            // at least 3 points now to enclose the surface.
            final Point2d[] pointList = new Point2d[ points.size() ];
            points.toArray( pointList );
            zref.add( new ZRef(s.getColor(),pointList) );
          }
      }

    // Time to sort the ZRef: The Comparator for the ZRef causes the
    // elements farthest away to be ordered first int the list
    Collections.sort( zref );

    if( RENDER_DRAWING_DEPTH )
      {
        _counter = 0;
      }

    // Render each of the elements in the ZRef structure. The number
    // of points referenced determines whether it's a point (1), an
    // edge (2), or a surface (3+).
    for( ZRef z : zref )
      {
        final Point2d[] points = z.get();
        if( points.length == 1 )
          {
            paintPoint( g2, points[0] );
          }
        else if( points.length == 2 )
          {
            paintEdge( g2, z.getColor(), points[0], points[1] );
          }
        else
          {
            paintSurface( g2, z.getColor(), points );
          }
      }
  }

  /**
   * Paints a point at the indicated Point2d (x,y) coordinate. Points
   * are rendered to appear like small spheres using concentric rings
   * of color from dark on the outer edge to white in the center.
   **/
  private void paintPoint( final Graphics2D g2,
                           final Point2d p )
  {
    g2.setColor( GRAY );
    g2.fillOval( p.x-3, p.y-3, 7, 7 );

    g2.setColor( LGRAY );
    g2.fillOval( p.x-2, p.y-2, 5, 5 );

    g2.setColor( WHITE );
    g2.fillOval( p.x-1, p.y-1, 3, 3 );

    if( RENDER_DRAWING_DEPTH )
      {
        g2.drawString( String.valueOf(++_counter),
                       p.x+5,
                       p.y );
      }
  }

  /**
   * Paints a colored edge (a line between two coordinates).
   *
   * @param head The starting point of the edge
   * @param tail The ending point of the edge
   **/
  private void paintEdge( final Graphics2D g2,
                          final Color c,
                          final Point2d head,
                          final Point2d tail )
  {
    g2.setColor( c );
    g2.drawLine( head.x, head.y,
                 tail.x, tail.y );
    if( RENDER_DRAWING_DEPTH )
      {
        g2.drawString( String.valueOf(++_counter),
                       (head.x+tail.x)/2+5,
                       (head.y+tail.y)/2+5 );
      }
  }

  /**
   * Paints a color-filled surface using three or more points in 3D
   * space.
   *
   * @param pN Three or more points to define the surface corners.
   **/
  private void paintSurface( final Graphics2D g2,
                             final Color c,
                             final Point2d[] pN )
  {
    final int size = pN.length;
    final int[] x = new int[ size ];
    final int[] y = new int[ size ];

    int n = 0;
    for( int i=0; i<pN.length; i++ )
      {
        x[n] = pN[i].x;
        y[n] = pN[i].y;
        n++;
      }

    g2.setColor( c );
    g2.fillPolygon( x, y, size );

    if( RENDER_DRAWING_DEPTH )
      {
        // find the center of the surface, drawString ++_counter there
        // (as in paintPoint and paintEdge above)
      }
  }

  // ======================================================================
  // MouseListener
  // ======================================================================
  public void mouseEntered( final MouseEvent e)
  {
  }
  public void mouseExited( final MouseEvent e)
  {
  }
  public void mousePressed( final MouseEvent e )
  {
    mouseX = e.getX();
    mouseY = e.getY();
  }
  public void mouseReleased( final MouseEvent e )
  {
  }
  public void mouseClicked( final MouseEvent e)
  {
  }

  // ======================================================================
  // MouseMotionListener
  // ======================================================================
  public void mouseMoved( final MouseEvent e )
  {
  }
  public void mouseDragged( final MouseEvent e )
  {
    final int curX = e.getX();
    final int curY = e.getY();

    // Alter the view angle to affect the rotation of the view
    setViewAngle( viewAngleX - (0.01d * (curX - mouseX)),
                  viewAngleY + (0.01d * (curY - mouseY)),
                  viewAngleZ );

    mouseX = curX;
    mouseY = curY;
  }

  // ======================================================================
  // MouseWheelListener
  // ======================================================================
  public void mouseWheelMoved( final MouseWheelEvent e )
  {
    if( e.getWheelRotation() > 0 )
      {
        this.screenPositionZ = this.screenPositionZ * 1.1d;
      }
    else
      {
        this.screenPositionZ = this.screenPositionZ / 1.1d;
      }
    repaint();
  }

  private int mouseX, mouseY;

  /**
   * A vector in 3D space, structurally the same as a 3D coordinate.
   **/
  static class Vector3d
  {
    Vector3d( final double x,
              final double y,
              final double z )
    {
      this.x = x;
      this.y = y;
      this.z = z;
    }
    double x, y, z;
  }

  /**
   * A point in 2D space (on the screen) with associated distance from
   * the viewer that allows for depth sorting.
   **/
  static class Point2d
  {
    void set( final int x,
              final int y,
              final double depth )
    {
      this.x = x;
      this.y = y;
      this.depth = depth;
    }
    int getX()
    {
      return x;
    }
    int getY()
    {
      return y;
    }
    double getDepth()
    {
      return depth;
    }
    private int x,y;
    private double depth;
  }

  /**
   * A ZRef references one or more coordinates (and associated color
   * where applicable) for points, edges, and surfaces. The important
   * feature here is that we've already calculated whereon the screen
   * these points are to be displayed, so we don't need to recalculate
   * that information. If our Mesh elements (Point, Edge, Surface) had
   * more attributes, then we might want to reference them directly
   * here, but for now capturing their color is all we need here.
   **/
  static class ZRef
    implements Comparable<ZRef>
  {
    ZRef( final Point2d ref )
      {
        this.color = null;
        this.refs = new Point2d[]{ref};
        this.avgDepth = ref.depth;
      }
    ZRef( final Color color,
          final Point2d refHead,
          final Point2d refTail )
      {
        this.color = color;
        this.refs = new Point2d[]{refHead,refTail};
        this.avgDepth = (refHead.depth + refTail.depth) / 2.0d;
      }
    ZRef( final Color color,
          final Point2d[] refs )
      {
        this.color = color;
        this.refs = refs;
        fixDepth();
      }
    Point2d[] get()
    {
      return refs;
    }
    Color getColor()
    {
      return color;
    }
    @Override
    public int compareTo( final ZRef other )
    {
      if( avgDepth > other.avgDepth )
        {
          return -1;    // we're farther away, so put us before the other ZRef
        }
      if( avgDepth < other.avgDepth )
        {
          return 1;     // we're nearer, so put us after the other ZRef
        }
      return 0; // same distance
    }
    /**
     * Convenience method for calculating the average depth of the
     * referenced points. We call this only when we have 2 or more,
     * as it's easy enough with 2 points to calculate the average
     * directly.
     **/
    private void fixDepth()
    {
      double d = 0.0d;
      for( Point2d p : refs )
        {
          d += p.depth;
        }
      avgDepth = d / refs.length;
    }
    private final Color color;
    private final Point2d[] refs;
    private double avgDepth;
  }

  private int width, height;
  private double screenPositionX, screenPositionY, screenPositionZ;
  private double viewAngleX, viewAngleY, viewAngleZ;
  private int modelScale;
  private double CT, ST, CP, SP;
  private int _counter;
  //
  /**
   * Reused structure for collecting the edge points of a surface
   **/
  private final List<Point2d> points = new ArrayList<Point2d>();
  /**
   * Reused for calculating the projection of a 3D point to the screen
   **/
  private final Point2d point2d = new Point2d();
  /**
   * The {@link Mesh}es to be rendered.
   **/
  private final List<Mesh> meshes = new ArrayList<Mesh>();
  //
  private static final Color GRAY  = new Color( 127, 127, 127 );
  private static final Color LGRAY = new Color( 191, 191, 191 );
  private static final Color WHITE = new Color( 255, 255, 255 );
}
