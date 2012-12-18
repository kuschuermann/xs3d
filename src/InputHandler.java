package com.ringlord.xs3d;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.InputEvent.SHIFT_DOWN_MASK;
import static java.awt.event.InputEvent.ALT_DOWN_MASK;


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
  implements KeyListener,
             MouseListener,
             MouseMotionListener,
             MouseWheelListener
{
  public InputHandler( final Viewer3d view )
  {
    super();
    this.view = view;

    view.addKeyListener( this );
    view.addMouseListener( this );
    view.addMouseMotionListener( this );
    view.addMouseWheelListener( this );
  }

  // ======================================================================
  // KeyListener
  // ======================================================================
  @Override
  public void keyPressed( final KeyEvent e )
  {
    final int keyCode = e.getKeyCode();

    if( keyCode == KeyEvent.VK_L )
      {
        if( (e.getModifiersEx() & CTRL_DOWN_MASK) == CTRL_DOWN_MASK )
          {
            view.reset();
          }
      }
  }
  @Override
  public void keyReleased( final KeyEvent e )
  {
  }
  @Override
  public void keyTyped( final KeyEvent e )
  {
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

  // the last place where a mouse button was pressed or where the
  // mouse was during a drag operation; used for computing drag
  // offsets during scene rotation.
  private int mouseX, mouseY;
  private final Viewer3d view;
}
