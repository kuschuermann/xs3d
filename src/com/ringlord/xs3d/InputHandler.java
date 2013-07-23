package com.ringlord.xs3d;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;

import java.util.Set;
import java.util.HashSet;

import javax.swing.JComponent;
import javax.swing.KeyStroke;


/**
 * <p>Handles a few simple input events for a {@link Viewer3d}:</p>
 *
 * <ol>
 * <li>ctrl-L resets the view to its initial settings
 * <li>(any) mouse drag (press and move) rotates the view
 * <li>mouse wheel zooms in/out
 * </ol>
 *
 * @author K. Udo Schuermann
 **/
public class InputHandler
  implements MouseListener,
             MouseMotionListener,
             MouseWheelListener
{
  public InputHandler( final Viewer3d view )
  {
    super();
    this.view = view;

    view.addMouseListener( this );
    view.addMouseMotionListener( this );
    view.addMouseWheelListener( this );

    view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put( KeyStroke.getKeyStroke("ctrl L"), "reset" );
  }

  public void addMeshFocusListener( final MeshFocusListener l )
  {
    meshFocusListeners.add( l );
  }
  public void removeMeshFocusListener( final MeshFocusListener l )
  {
    meshFocusListeners.remove( l );
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
    testFocus();
  }
  public void mouseClicked( final MouseEvent ev )
  {
    if( curFocus != null )
      {
        switch( curFocus.getType() )
          {
          case FACE:
            Mesh.Face f = curFocus.getFace();
            f.setSelected( ! f.isSelected() );
            break;

          case EDGE:
            Mesh.Edge e = curFocus.getEdge();
            e.setSelected( ! e.isSelected() );
            break;

          case POINT:
            Mesh.Point3d p = curFocus.getPoint();
            p.setSelected( ! p.isSelected() );
          }
        view.repaint();
      }
  }

  // ======================================================================
  // MouseMotionListener
  // ======================================================================
  public void mouseMoved( final MouseEvent e )
  {
    mouseX = e.getX();
    mouseY = e.getY();

    testFocus();
  }
  public void mouseDragged( final MouseEvent e )
  {
    final int curX = e.getX();
    final int curY = e.getY();

    // Alter the view angle to affect the rotation of the view; 0.01
    // controls the mouse sensitivity: a smaller value requires more
    // motion to effect a change, a larger value makes the mouse more
    // sensitive. A value of 0.01 tends to result in fairly intuitive
    // operations.
    final double viewAngleX = view.getViewAngleX();
    final double viewAngleY = view.getViewAngleY();
    final double viewAngleZ = view.getViewAngleZ();
    view.setViewAngle( viewAngleX - (0.01d * (curX - mouseX)),
                       viewAngleY + (0.01d * (curY - mouseY)),
                       viewAngleZ );

    // Now that we have computed the delta between previous and
    // current mouse position, update the mouse position so that the
    // current one becomes the previous on the next invocation.
    mouseX = curX;
    mouseY = curY;

  }

  // ======================================================================
  // MouseWheelListener
  // ======================================================================
  public void mouseWheelMoved( final MouseWheelEvent e )
  {
    // The factor of 1.1 below controls the sensitivity of the mouse
    // wheel, determining how quickly the z-coordinate of the screen
    // is altered.
    if( e.getWheelRotation() > 0 )
      {
        view.setScreenPositionZ( view.getScreenPositionZ() * 1.05d );
      }
    else
      {
        view.setScreenPositionZ( view.getScreenPositionZ() / 1.05d );
      }
  }

  // ----------------------------------------------------------------------

  private void testFocus()
  {
    // don't check focus if nobody is listening
    if( ! meshFocusListeners.isEmpty() )
      {
        final FocusInfo focus = view.getFocusedMesh( mouseX, mouseY );

        Object prev = null;
        if( curFocus != null )
          {
            switch( curFocus.getType() )
              {
              case FACE:
                Mesh.Face f = curFocus.getFace();
                f.setFocused( false );
                prev = f;
                break;
              case EDGE:
                Mesh.Edge e = curFocus.getEdge();
                e.setFocused( false );
                prev = e;
                break;
              case POINT:
                Mesh.Point3d p = curFocus.getPoint();
                p.setFocused( false );
                prev = p;
                break;
              }
          }

        Object cur = null;
        if( focus != null )
          {
            switch( focus.getType() )
              {
              case FACE:
                Mesh.Face f = focus.getFace();
                f.setFocused( true );
                cur = f;
                break;
              case EDGE:
                Mesh.Edge e = focus.getEdge();
                e.setFocused( true );
                cur = e;
                break;
              case POINT:
                Mesh.Point3d p = focus.getPoint();
                p.setFocused( true );
                cur = p;
                break;
              }
          }

        if( prev != cur )
          {
            if( prev != null )
              {
                notifyMeshFocusLost( curFocus );
              }
            if( cur != null )
              {
                notifyMeshFocusGained( focus );
              }
            view.repaint();
          }

        curFocus = focus;
      }
  }

  private void notifyMeshFocusGained( final FocusInfo info )
  {
    final MeshFocusEvent e = new MeshFocusEvent( view, info );
    for( MeshFocusListener l : meshFocusListeners )
      {
        l.meshFocusGained( e );
      }
  }

  private void notifyMeshFocusLost( final FocusInfo info )
  {
    final MeshFocusEvent e = new MeshFocusEvent( view, info );
    for( MeshFocusListener l : meshFocusListeners )
      {
        l.meshFocusLost( e );
      }
  }

  // ----------------------------------------------------------------------

  // the last place where a mouse button was pressed or where the
  // mouse was during a drag operation; used for computing drag
  // offsets during scene rotation.
  private int mouseX, mouseY;
  private final Viewer3d view;
  private FocusInfo curFocus;
  private final Set<MeshFocusListener> meshFocusListeners = new HashSet<MeshFocusListener>();
}
