package com.ringlord.xs3d;

import java.util.EventObject;


public class MeshFocusEvent
  extends EventObject
{
  private static final long serialVersionUID = -8401741572344280303L;
  
  public MeshFocusEvent( final Viewer3d viewer,
                         final FocusInfo info )
  {
    super( viewer );
    this.info = info;
  }
  public FocusInfo getFocusInfo()
  {
    return info;
  }
  private final FocusInfo info;
}
