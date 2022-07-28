/*
 * This file is part of SQL Workbench/J, https://www.sql-workbench.eu
 *
 * Copyright 2002-2021 Thomas Kellerer.
 *
 * Licensed under a modified Apache License, Version 2.0 (the "License")
 * that restricts the use for certain governments.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.sql-workbench.eu/manual/license.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * To contact the author please send an email to: support@sql-workbench.eu
 */
package workbench.db;

import java.sql.SQLException;

import workbench.db.postgres.PostgresReadOnlyChanger;

/**
 *
 * @author Thomas Kellerer
 */
public interface ReadOnlyChanger
{
  void setReadOnly(WbConnection conn, boolean flag)
    throws SQLException;

  public static class Factory
  {
    public static ReadOnlyChanger createChanger(WbConnection conn)
    {
      switch (DBID.fromConnection(conn))
      {
        case Postgres:
          return new PostgresReadOnlyChanger();
        default:
          return DEFAULT_CHANGER;
      }
    }
  }
  public static ReadOnlyChanger DEFAULT_CHANGER = (WbConnection conn, boolean flag) ->
  {
    if (conn != null)
    {
      // this property can not be changed while a transaction is running
      // so we have to end any pending transaction
      if (!conn.getAutoCommit()) conn.rollbackSilently();
      conn.getSqlConnection().setReadOnly(flag);
    }
  };

}
