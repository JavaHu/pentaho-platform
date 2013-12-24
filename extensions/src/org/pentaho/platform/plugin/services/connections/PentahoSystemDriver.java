/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License, version 2 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/gpl-2.0.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 *
 * Copyright 2006 - 2013 Pentaho Corporation.  All rights reserved.
 */
package org.pentaho.platform.plugin.services.connections;

import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.services.messages.Messages;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;


//This driver will delegate to drivers found in the Pentaho Object System
//For example it can delegate to the Mondrian 4 Olap4J Driver that lives in an OSGI Bundle
public class PentahoSystemDriver implements Driver {
  static {
    try {
      DriverManager.registerDriver( new PentahoSystemDriver() );
    } catch ( SQLException e ) {
      org.pentaho.platform.util.logging.Logger.warn(
        PentahoSystemDriver.class.getName(),
        Messages.getInstance().getErrorString( "PentahoSystemDriver.ERROR_0001_COULD_NOT_REGISTER_DRIVER" ),
        e );
    }
  }

  List<Driver> getAllDrivers() {
    return PentahoSystem.getAll( Driver.class );
  }

  @Override
  public Connection connect( final String url, final Properties info ) throws SQLException {
    for ( Driver driver : getAllDrivers() ) {
      if ( driver.acceptsURL( url ) ) {
        Connection conn = driver.connect( url, info );
        if ( conn != null ) {
          return conn;
        }
      }
    }
    return null;
  }

  @Override
  public boolean acceptsURL( final String url ) throws SQLException {
    for ( Driver driver : getAllDrivers() ) {
      if ( driver.acceptsURL( url ) ) {
        return true;
      }
    }
    return false;
  }

  @Override
  public DriverPropertyInfo[] getPropertyInfo( final String url, final Properties info ) throws SQLException {
    for ( Driver driver : getAllDrivers() ) {
      if ( driver.acceptsURL( url ) ) {
        return driver.getPropertyInfo( url, info );
      }
    }
    return null;
  }

  @Override
  public int getMajorVersion() {
    return 0;
  }

  @Override
  public int getMinorVersion() {
    return 0;
  }

  @Override
  public boolean jdbcCompliant() {
    return true;
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new SQLFeatureNotSupportedException( "Impossible to know which Driver to fetch the logger from" );
  }
}
