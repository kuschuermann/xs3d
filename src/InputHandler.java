package com.ringlord.xs3d;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.InputEvent.SHIFT_DOWN_MASK;
import static java.awt.event.InputEvent.ALT_DOWN_MASK;

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
  @Override
  public void mouseEntered( final MouseEvent e)
  {
  }
  @Override
  public void mouseExited( final MouseEvent e)
  {
  }
  @Override
  public void mousePressed( final MouseEvent e )
  {
    mouseX = e.getX();
    mouseY = e.getY();
  }
  @Override
  public void mouseReleased( final MouseEvent e )
  {
  }
  @Override
  public void mouseClicked( final MouseEvent e)
  {
  }

  // ======================================================================
  // MouseMotionListener
  // ======================================================================
  @Override
  public void mouseMoved( final MouseEvent e )
  {
    mouseX = e.getX();
    mouseY = e.getY();

    if( ! meshFocusListeners.isEmpty() ) // don't check focus if nobody is listening
      {
        final FocusInfo focus = view.getFocusedMesh( mouseX, mouseY );
        if( focus != null )
          {
            if( (curFocus == null) ||
                (focus.getType() != curFocus.getType()) ||
                (focus.getMesh() != curFocus.getMesh()) ||
                (focus.getFace() != curFocus.getFace()) ||
                (focus.getEdge() != curFocus.getEdge()) ||
                (focus.getPoint() != curFocus.getPoint()) )
              {
                notifyMeshFocusGained( focus );
              }
          }
        else if( curFocus != null )
          {
            notifyMeshFocusLost( curFocus ); // lost focus
          }
        curFocus = focus;
      }
  }
  @Override
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
  @Override
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
