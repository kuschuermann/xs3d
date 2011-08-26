package com.ringlord.xs3d;

import java.awt.Color;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * A mesh consisting of points, edges between two points, and surfaces
 * represented by three or more edges. Edges and surfaces have color.
 **/
public class Mesh
{
  /**
   * Our {@link Viewer3d} registers itself as a ChangeListener so that
   * it can redraw the display whenever necessary. At this point the
   * Mesh does not initiate such a call on its own but you might want
   * to make {@link #notifyChangeListeners()} public and call that
   * after making a change to the Mesh, or call Viewer3d.repaint(),
   * instead.
   **/
  public void addChangeListener( final ChangeListener l )
  {
    changeListeners.add( l );
  }
  public void removeChangeListener( final ChangeListener l )
  {
    changeListeners.remove( l );
  }
  private void notifyChangeListeners()
  {
    final ChangeEvent e = new ChangeEvent( this );
    for( ChangeListener l : changeListeners )
      {
        l.stateChanged( e );
      }
  }

  /**
   * Add a single Point3d to the Mesh.
   **/
  public void add( final Point3d p )
  {
    points.add( p );
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
  }

  /**
   * Add an Edge to the Mesh.
   **/
  public void add( final Edge e )
  {
    edges.add( e );
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
  }

  /**
   * Add a Surface to the Mesh.
   **/
  public void add( final Surface s )
  {
    surfaces.add( s );
  }
  /**
   * Remove a Surface from the Mesh.
   **/
  public void remove( final Surface s )
  {
    surfaces.remove( s );
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

  /**
   * A point in 3D space. It has no color, but the Viewer3d renders it
   * as a little sphere.
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
   * A colored edge in 3D space defined by 2 points
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
   * A colored surface in 3D space defined by 3 or more edges.
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

  private final List<Point3d> points = new ArrayList<Point3d>();
  private final List<Edge> edges = new ArrayList<Edge>();
  private final List<Surface> surfaces = new ArrayList<Surface>();
  private final List<ChangeListener> changeListeners = new ArrayList<ChangeListener>();
}
