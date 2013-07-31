package com.ringlord.xs3d;

/**
 * Describes what is in focus at a given X,Y location
 **/
public class FocusInfo
{
  /**
   * A {@link Mesh.Face} is in focus.
   **/
  public FocusInfo( final Mesh mesh,
	            final Mesh.Face face )
  {
    this( mesh,
	  face,
	  null,
	  null );
  }


  /**
   * A {@link Mesh.Edge} is in focus.
   **/
  public FocusInfo( final Mesh mesh,
	            final Mesh.Edge edge )
  {
    this( mesh,
	  null,
	  edge,
	  null );
  }


  /**
   * A {@link Mesh.Point3d} is in focus.
   **/
  public FocusInfo( final Mesh mesh,
	            final Mesh.Point3d point )
  {
    this( mesh,
	  null,
	  null,
	  point );
  }


  // ----------------------------------------------------------------------

  public Type getType()
  {
    return type;
  }


  /**
   * @return null if {@link #getType()} is NONE.
   **/
  public Mesh getMesh()
  {
    return mesh;
  }


  /**
   * @return null if {@link #getType()} is not FACE.
   **/
  public Mesh.Face getFace()
  {
    return face;
  }


  /**
   * @return null if {@link #getType()} is note EDGE.
   **/
  public Mesh.Edge getEdge()
  {
    return edge;
  }


  /**
   * @return null if {@link #getType()} is POINT.
   **/
  public Mesh.Point3d getPoint()
  {
    return point;
  }


  public String toString()
  {
    return type.toString();
  }


  // ----------------------------------------------------------------------

  private FocusInfo( final Mesh mesh,
	             final Mesh.Face face,
	             final Mesh.Edge edge,
	             final Mesh.Point3d point )
  {
    this.mesh = mesh;
    if( mesh == null )
      {
	throw new IllegalArgumentException( "FocusInfo must reference a Mesh" );
      }

    if( point != null )
      {
	this.face = null;
	this.edge = null;
	this.point = point;
	this.type = Type.POINT;
      }
    else if( edge != null )
      {
	this.face = null;
	this.edge = edge;
	this.point = null;
	this.type = Type.EDGE;
      }
    else if( face != null )
      {
	this.face = face;
	this.edge = null;
	this.point = null;
	this.type = Type.FACE;
      }
    else
      {
	throw new IllegalStateException( "FocusInfo must have one of Face, Edge, or Point3d" );
      }
  }


  enum Type
  {
    FACE,
    EDGE,
    POINT;
  }

  private final Type type;
  private final Mesh mesh;
  private final Mesh.Face face;
  private final Mesh.Edge edge;
  private final Mesh.Point3d point;
}
