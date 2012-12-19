package com.ringlord.xs3d;

import java.awt.Color;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

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
 * Face}S. Edges connect two points. Faces are bounded by three
 * or more (3+) edges.</p>
 *
 * <p>It is not necessary to add the points to the Mesh that form the
 * end points of a Mesh, or add the edges to a Mesh that form the
 * bounds of a Face.</p>
 *
 * <p>Edges and Faces have color. Points do not (currently) define
 * a color. Exercise for the aspiring programmer: Add a fourth class
 * that (maybe extends Point) but is rendered with a custom drawing
 * routine (in Viewer3d).</p>
 *
 * <p>A Mesh (with all points, faces, and edges) is focusable by
 * default, but this can be controlled with {@link
 * #setFocusable(boolean)}.</p>
 *
 * @author K. Udo Schuermann
 **/
public class Mesh
{
  public void setFocusable( final boolean isFocusable )
  {
    this.isFocusable = isFocusable;
  }
  public boolean isFocusable()
  {
    return isFocusable;
  }

  public void setSelectable( final boolean isSelectable )
  {
    this.isSelectable = isSelectable;
  }
  public boolean isSelectable()
  {
    return isSelectable;
  }

  public void setVisible( final boolean isVisible )
  {
    this.isVisible = isVisible;
  }
  public boolean isVisible()
  {
    return isVisible;
  }

  /**
   * <p>Add a {@link ChangeListener} to be notified when any Mesh
   * element (point, edge, or face) is added to or removed from the
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
    pointArray = null;

    List<Edge> destroyedEdges = null;
    List<Face> destroyedFaces = null;
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

            for( Face s : faces )
              {
                if( s.contains(e) )
                  {
                    if( s.size() > 3 )
                      {
                        s.remove( e );
                      }
                    else
                      {
                        if( destroyedFaces == null )
                          {
                            destroyedFaces = new ArrayList<Face>();
                          }
                        destroyedFaces.add( s );
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
        edgeArray = null;
      }
    if( destroyedFaces != null )
      {
        for( Face s : destroyedFaces )
          {
            faces.remove( s );
          }
        faceArray = null;
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

    List<Face> destroyedFaces = null;
    for( Face s : faces )
      {
        if( s.contains(e) )
          {
            if( s.size() > 3 )
              {
                s.remove( e );
              }
            else
              {
                if( destroyedFaces == null )
                  {
                    destroyedFaces = new ArrayList<Face>();
                  }
                destroyedFaces.add( s );
              }
          }
      }

    if( destroyedFaces != null )
      {
        for( Face s : destroyedFaces )
          {
            faces.remove( s );
          }
      }
    notifyChangeListeners();
  }

  /**
   * Add a Face to the Mesh.
   **/
  public void add( final Face s )
  {
    faces.add( s );
    notifyChangeListeners();
  }
  /**
   * Remove a Face from the Mesh.
   **/
  public void remove( final Face s )
  {
    faces.remove( s );
    notifyChangeListeners();
  }

  public Point3d[] points()
  {
    if( pointArray == null )
      {
        pointArray = new Point3d[ points.size() ];
        points.toArray( pointArray );
      }
    return pointArray;
  }
  public Edge[] edges()
  {
    if( edgeArray == null )
      {
        edgeArray = new Edge[ edges.size() ];
        edges.toArray( edgeArray );
      }
    return edgeArray;
  }
  public Face[] faces()
  {
    if( faceArray == null )
      {
        faceArray = new Face[ faces.size() ];
        faces.toArray( faceArray );
      }
    return faceArray;
  }


  private void notifyChangeListeners()
  {
    final ChangeEvent e = new ChangeEvent( this );
    for( ChangeListener l : changeListeners )
      {
        l.stateChanged( e );
      }
  }

  public static class Coloring
  {
    public Coloring( final Color normal,
                     final Color focused,
                     final Color selected )
    {
      this.normal = normal;
      this.focused = (focused == null ? normal : focused);
      this.selected = (selected == null ? focused : selected);
    }
    public Color normal()
    {
      return normal;
    }
    public Color focused()
    {
      return focused;
    }
    public Color selected()
    {
      return selected;
    }
    private Color normal, focused, selected;
  }

  // ======================================================================
  // Nested classes (Point3d, Edge, Face)
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
      setXYZ( x, y, z );
    }

    public void setFocused( final boolean isFocused )
    {
      this.isFocused = isFocused;
    }
    public boolean isFocused()
    {
      return isFocused;
    }

    public void setSelected( final boolean isSelected )
    {
      this.isSelected = isSelected;
      System.err.println( "Selected ("+x+", "+y+", "+z+")" );
    }
    public boolean isSelected()
    {
      return isSelected;
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
    public void setXYZ( final double x,
                        final double y,
                        final double z )
    {
      this.x = x;
      this.y = y;
      this.z = z;
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
    private boolean isFocused;
    private boolean isSelected;
    private double x, y, z;
  }

  /**
   * A colored edge in 3D space defined by 2 {@link Point3d}S.
   *
   * @author K. Udo Schuermann
   **/
  public static class Edge
  {
    public Edge( final Coloring coloring,
                 final Point3d head,
                 final Point3d tail )
    {
      super();
      this.coloring = coloring;
      this.head = head;
      this.tail = tail;
    }

    public void setFocused( final boolean isFocused )
    {
      this.isFocused = isFocused;
    }
    public boolean isFocused()
    {
      return isFocused;
    }

    public void setSelected( final boolean isSelected )
    {
      this.isSelected = isSelected;
    }
    public boolean isSelected()
    {
      return isSelected;
    }

    public Point3d getHead()
    {
      return head;
    }
    public Point3d getTail()
    {
      return tail;
    }
    public Coloring getColoring()
    {
      return coloring;
    }
    void setColor( final Coloring coloring )
    {
      this.coloring = coloring;
    }
    private boolean isFocused;
    private boolean isSelected;
    private Coloring coloring;
    private final Point3d head, tail;
  }

  /**
   * A colored face in 3D space defined by 3 or more {@link Edge}S.
   *
   * @author K. Udo Schuermann
   **/
  public static class Face
  {
    public Face( final Coloring coloring,
                 final Edge ... edges )
    {
      super();
      if( edges.length < 3 )
        {
          throw new IllegalArgumentException( "Faces must have at least 3 edges" );
        }
      this.coloring = coloring;
      for( Edge e : edges )
        {
          add( e );
        }
    }
    Face( final Coloring coloring,
          final List<Edge> edges )
    {
      super();
      if( edges.size() < 3 )
        {
          throw new IllegalArgumentException( "Faces must have at least 3 edges" );
        }
      this.coloring = coloring;
      for( Edge e : edges )
        {
          add( e );
        }
    }

    public void setFocused( final boolean isFocused )
    {
      this.isFocused = isFocused;
    }
    public boolean isFocused()
    {
      return isFocused;
    }

    public void setSelected( final boolean isSelected )
    {
      this.isSelected = isSelected;
    }
    public boolean isSelected()
    {
      return isSelected;
    }

    void add( final Edge edge )
    {
      if( edges.isEmpty() ||
          (edges.get( edges.size() - 1 ).getTail() == edge.getHead()) )
        {
          edges.add( edge );
          edgeArray = null;
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
          edgeArray = null;
        }
      else
        {
          throw new IllegalArgumentException( "Cannot reduce edge count to less than 3" );
        }
    }
    public Coloring getColoring()
    {
      return coloring;
    }
    void setColoring( final Coloring coloring )
    {
      this.coloring = coloring;
    }
    public boolean contains( final Edge edge )
    {
      return edges.contains( edge );
    }
    public int size()
    {
      return edges.size();
    }
    public Edge[] edges()
    {
      if( edgeArray == null )
        {
          edgeArray = new Edge[ edges.size() ];
          edges.toArray( edgeArray );
        }
      return edgeArray;
    }
    private boolean isFocused;
    private boolean isSelected;
    private Coloring coloring;
    private Edge[] edgeArray;
    private final List<Edge> edges = new ArrayList<Edge>();
  }

  // The structures (points, edges, faces) contained by the Mesh
  //
  private Point3d[] pointArray;
  private Edge[] edgeArray;
  private Face[] faceArray;
  //
  private final List<Point3d> points = new ArrayList<Point3d>();
  private final List<Edge> edges = new ArrayList<Edge>();
  private final List<Face> faces = new ArrayList<Face>();
  //
  private boolean isFocusable = true;
  private boolean isSelectable = true;
  private boolean isVisible = true;
  // The ChangeListenerS that registered their interest to be informed
  // when the contents of the Mesh are changed (elements are added or
  // removed)
  private final List<ChangeListener> changeListeners = new ArrayList<ChangeListener>();
}
