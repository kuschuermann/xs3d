package com.ringlord.xs3d;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;

public class ViewManipulator
  implements MouseListener,
             MouseMotionListener,
             MouseWheelListener
{
  public ViewManipulator( final Viewer3d view )
  {
    super();
    this.view = view;

    view.addMouseListener( this );
    view.addMouseMotionListener( this );
    view.addMouseWheelListener( this );
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

