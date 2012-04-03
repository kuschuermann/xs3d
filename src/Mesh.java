package com.ringlord.xs3d;

import java.awt.Color;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

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
 * <p>A mesh consisting of {@link Point}S, {@link Edge}S, and {@link
 * Surface}S. Edges connect two points. Surfaces are bounded by three
 * or more (3+) edges.</p>
 *
 * <p>It is not necessary to add the points to the Mesh that form the
 * end points of a Mesh, or add the edges to a Mesh that form the
 * bounds of a Surface.</p>
 *
 * <p>Edges and Surfaces have color. Points do not (currently) define
 * a color. Exercise for the aspiring programmer: Add a fourth class
 * that (maybe extends Point) but is rendered with a custom drawing
 * routine (in Viewer3d).</p>
 *
 * @author K. Udo Schuermann
 **/
public class Mesh
{
  /**
   * <p>Add a {@link ChangeListener} to be notified when any Mesh
   * element (point, edge, or surface) is added to or removed from the
   * Mesh.</p>
   *
   * <p>The {@link Viewer3d} registers itself as a ChangeListener so
   * that it can redraw the display when the Mesh contents change.
   **/
  public void addChangeListener( final ChangeListener l )
  {
    changeListeners.add( l );
  }
  /**
   * <p>Remove a {@link ChangeListener} from the Mesh.
   **/
  public void removeChangeListener( final ChangeListener l )
  {
    changeListeners.remove( l );
  }

  /**
   * Add a single Point3d to the Mesh.
   **/
  public void add( final Point3d p )
  {
    points.add( p );
    notifyChangeListeners();
  }
  /**
   * Remove a single Point3d from the Mesh.
   **/
  public void remove( final Point3d p )
  {
    points.remove( p );

    List<Edge> destroyedEdges = null;
    List<Surface> destroyedSurfaces = null;
    for( Edge e : edges )
      {
        if( (p == e.getHead()) ||
            (p == e.getTail()) )
          {
            if( destroyedEdges == null )
              {
                destroyedEdges = new ArrayList<Edge>();
              }
            destroyedEdges.add( e );

            for( Surface s : surfaces )
              {
                if( s.contains(e) )
                  {
                    if( s.size() > 3 )
                      {
                        s.remove( e );
                      }
                    else
                      {
                        if( destroyedSurfaces == null )
                          {
                            destroyedSurfaces = new ArrayList<Surface>();
                          }
                        destroyedSurfaces.add( s );
                      }
                  }
              }
          }
      }

    if( destroyedEdges != null )
      {
        for( Edge e : destroyedEdges )
          {
            edges.remove( e );
          }
      }
    if( destroyedSurfaces != null )
      {
        for( Surface s : destroyedSurfaces )
          {
            surfaces.remove( s );
          }
      }
    notifyChangeListeners();
  }

  /**
   * Add an Edge to the Mesh.
   **/
  public void add( final Edge e )
  {
    edges.add( e );
    notifyChangeListeners();
  }
  /**
   * Remove an Edge from the Mesh.
   **/
  public void remove( final Edge e )
  {
    edges.remove( e );

    List<Surface> destroyedSurfaces = null;
    for( Surface s : surfaces )
      {
        if( s.contains(e) )
          {
            if( s.size() > 3 )
              {
                s.remove( e );
              }
            else
              {
                if( destroyedSurfaces == null )
                  {
                    destroyedSurfaces = new ArrayList<Surface>();
                  }
                destroyedSurfaces.add( s );
              }
          }
      }

    if( destroyedSurfaces != null )
      {
        for( Surface s : destroyedSurfaces )
          {
            surfaces.remove( s );
          }
      }
    notifyChangeListeners();
  }

  /**
   * Add a Surface to the Mesh.
   **/
  public void add( final Surface s )
  {
    surfaces.add( s );
    notifyChangeListeners();
  }
  /**
   * Remove a Surface from the Mesh.
   **/
  public void remove( final Surface s )
  {
    surfaces.remove( s );
    notifyChangeListeners();
  }

  public Collection<Point3d> getPoints()
  {
    return points;
  }
  public Collection<Edge> getEdges()
  {
    return edges;
  }
  public Collection<Surface> getSurfaces()
  {
    return surfaces;
  }


  private void notifyChangeListeners()
  {
    final ChangeEvent e = new ChangeEvent( this );
    for( ChangeListener l : changeListeners )
      {
        l.stateChanged( e );
      }
  }

  // ======================================================================
  // Nested classes (Point3d, Edge, Surface)
  // ======================================================================

  /**
   * A point in 3D space. It has no color (at this time), but the
   * Viewer3d renders it as a little sphere.
   *
   * @author K. Udo Schuermann
   **/
  public static class Point3d
  {
    public Point3d( final double x,
                    final double y,
                    final double z )
    {
      super();
      this.x = x;
      this.y = y;
      this.z = z;
    }
    public double getX()
    {
      return x;
    }
    public double getY()
    {
      return y;
    }
    public double getZ()
    {
      return z;
    }
    public void setX( final double x )
    {
      this.x = x;
    }
    public void setY( final double y )
    {
      this.y = y;
    }
    public void setZ( final double z )
    {
      this.z = z;
    }
    private double x, y, z;
  }

  /**
   * A colored edge in 3D space defined by 2 {@link Point3d}S.
   *
   * @author K. Udo Schuermann
   **/
  public static class Edge
  {
    public Edge( final Color color,
                 final Point3d head,
                 final Point3d tail )
    {
      super();
      this.color = color;
      this.head = head;
      this.tail = tail;
    }
    public Point3d getHead()
    {
      return head;
    }
    public Point3d getTail()
    {
      return tail;
    }
    public Color getColor()
    {
      return color;
    }
    void setColor( final Color color )
    {
      this.color = color;
    }
    private Color color;
    private final Point3d head, tail;
  }

  /**
   * A colored surface in 3D space defined by 3 or more {@link Edge}S.
   *
   * @author K. Udo Schuermann
   **/
  public static class Surface
    implements Iterable<Edge>
  {
    public Surface( final Color color,
                    final Edge ... edges )
    {
      super();
      if( edges.length < 3 )
        {
          throw new IllegalArgumentException( "Surfaces must have at least 3 edges" );
        }
      this.color = color;
      for( Edge e : edges )
        {
          add( e );
        }
    }
    Surface( final Color color,
             final List<Edge> edges )
    {
      super();
      if( edges.size() < 3 )
        {
          throw new IllegalArgumentException( "Surfaces must have at least 3 edges" );
        }
      this.color = color;
      for( Edge e : edges )
        {
          add( e );
        }
    }
    void add( final Edge edge )
    {
      if( edges.isEmpty() ||
          (edges.get( edges.size() - 1 ).getTail() == edge.getHead()) )
        {
          edges.add( edge );
        }
      else
        {
          throw new IllegalArgumentException( "Edge head must match last edge's tail" );
        }
    }
    void remove( final Edge edge )
    {
      if( edges.size() > 3 )
        {
          edges.remove( edge );
        }
      else
        {
          throw new IllegalArgumentException( "Cannot reduce edge count to less than 3" );
        }
    }
    public Color getColor()
    {
      return color;
    }
    void setColor( final Color color )
    {
      this.color = color;
    }
    public boolean contains( final Edge edge )
    {
      return edges.contains( edge );
    }
    public int size()
    {
      return edges.size();
    }
    public Iterator<Edge> iterator()
      {
        return edges.iterator();
      }
    private Color color;
    private final List<Edge> edges = new ArrayList<Edge>();
  }

  // The structures (points, edges, surfaces) contained by the Mesh
  private final List<Point3d> points = new ArrayList<Point3d>();
  private final List<Edge> edges = new ArrayList<Edge>();
  private final List<Surface> surfaces = new ArrayList<Surface>();
  // The ChangeListenerS that registered their interest to be informed
  // when the contents of the Mesh are changed (elements are added or
  // removed)
  private final List<ChangeListener> changeListeners = new ArrayList<ChangeListener>();
}
