package com.ringlord.xs3d;

import java.awt.HeadlessException;

import javax.swing.SwingUtilities;

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
 * Entry point of the software, launches the {@link GUI}. Command line
 * arguments are ignored.
 *
 * @author K. Udo Schuermann
 **/
class Main
{
  /**
   * Entry point for XS3D.
   *
   * @param args The command line arguments, currently ignored.
   **/
  public static void main( final String[] args )
  {
    System.err.println( "XS3D 1.0 - The Extremely Simple 3D Rendering Engine\n"+
                        "Copyright (C) 2011 K. Udo Schuermann\n" );
    System.err.println( LICENSE_TEXT );

    try
      {
        SwingUtilities.invokeLater( new GUI() );
      }
    catch( final HeadlessException noGUI )
      {
        System.err.println( "Non-graphical environment?" );
      }
  }
  private static final String LICENSE_TEXT =
    "This program is free software: you can redistribute it and/or modify\n"+
    "it under the terms of the GNU General Public License as published by\n"+
    "the Free Software Foundation, either version 3 of the License, or\n"+
    "(at your option) any later version.\n"+
    "\n"+
    "This program is distributed in the hope that it will be useful,\n"+
    "but WITHOUT ANY WARRANTY; without even the implied warranty of\n"+
    "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"+
    "GNU General Public License for more details.\n"+
    "\n"+
    "You should have received a copy of the GNU General Public License\n"+
    "along with this program.  If not, see <http://www.gnu.org/licenses/>.\n";
}
