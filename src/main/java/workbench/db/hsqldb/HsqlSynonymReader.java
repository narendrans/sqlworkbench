/*
 * This file is part of SQL Workbench/J, https://www.sql-workbench.eu
 *
 * Copyright 2002-2022, Thomas Kellerer
 *
 * Licensed under a modified Apache License, Version 2.0
 * that restricts the use for certain governments.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at.
 *
 *     https://www.sql-workbench.eu/manual/license.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * To contact the author please send an email to: support@sql-workbench.eu
 *
 */
package workbench.db.hsqldb;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import workbench.log.CallerInfo;
import workbench.log.LogMgr;

import workbench.db.SynonymReader;
import workbench.db.TableIdentifier;
import workbench.db.WbConnection;

import workbench.db.JdbcUtils;
import workbench.util.StringUtil;

/**
 * Retrieve synonyms and their definition from a HSQLDB database.
 *
 * @author Thomas Kellerer
 */
public class HsqlSynonymReader
  implements SynonymReader
{
  public HsqlSynonymReader()
  {
  }

  @Override
  public List<TableIdentifier> getSynonymList(WbConnection con, String catalog, String schema, String namePattern)
    throws SQLException
  {
    List<TableIdentifier> result = new ArrayList<>();
    String sql =
      "SELECT synonym_catalog, \n" +
      "       synonym_schema, \n" +
      "       synonym_name \n " +
      "FROM information_schema.system_synonyms \n" +
      "WHERE synonym_schema = ? \n";

    if (StringUtil.isNonBlank(namePattern))
    {
      sql += " AND synonym_name LIKE ?";
    }

    LogMgr.logMetadataSql(new CallerInfo(){}, "synonyms", sql, schema, namePattern);

    PreparedStatement stmt = null;
    ResultSet rs = null;
    try
    {
      stmt = con.getSqlConnection().prepareStatement(sql);
      stmt.setString(1, schema);
      if (StringUtil.isNonBlank(namePattern)) stmt.setString(2, namePattern);

      rs = stmt.executeQuery();
      while (rs.next())
      {
        String synCat = rs.getString(1);
        String synSchema = rs.getString(2);
        String syn = rs.getString(3);
        if (!rs.wasNull())
        {
          TableIdentifier tbl = new TableIdentifier(synCat, synSchema, syn, false);
          tbl.setType(SYN_TYPE_NAME);
          tbl.setNeverAdjustCase(true);
          result.add(tbl);
        }
      }
    }
    catch (SQLException ex)
    {
      LogMgr.logMetadataError(new CallerInfo(){}, ex, "synonyms", sql, schema, namePattern);
      throw ex;
    }
    finally
    {
      JdbcUtils.closeAll(rs, stmt);
    }

    return result;
  }

  @Override
  public TableIdentifier getSynonymTable(WbConnection con, String catalog, String schema, String synonym)
    throws SQLException
  {
    String sql =
      "SELECT object_catalog, \n" +
      "       object_schema, \n" +
      "       object_name, \n" +
      "       object_type \n" +
      "FROM information_schema.system_synonyms \n" +
      "WHERE synonym_schema = ? \n" +
      "  AND synonym_name = ?";

    LogMgr.logMetadataSql(new CallerInfo(){}, "synonym table", sql, schema, synonym);

    ResultSet rs = null;
    PreparedStatement stmt = null;
    TableIdentifier result = null;
    try
    {
      stmt = con.getSqlConnection().prepareStatement(sql);
      stmt.setString(1, schema);
      stmt.setString(2, synonym);
      rs = stmt.executeQuery();
      if (rs.next())
      {
        String targetCatalog = rs.getString(1);
        String targetSchema = rs.getString(2);
        String targetName = rs.getString(3);
        String type = rs.getString(4);
        result = new TableIdentifier(targetCatalog, targetSchema, targetName, false);
        result.setType(type);
      }
    }
    catch (SQLException ex)
    {
      LogMgr.logMetadataError(new CallerInfo(){}, ex, "synonym table", sql, schema, synonym);
      throw ex;
    }
    finally
    {
      JdbcUtils.closeAll(rs,stmt);
    }
    return result;
  }

  @Override
  public boolean supportsReplace(WbConnection con)
  {
    return false;
  }

}
