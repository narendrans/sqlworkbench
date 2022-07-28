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
package workbench.gui.completion;

import workbench.db.GenericObjectDropper;
import workbench.db.IndexDefinition;
import workbench.db.TableIdentifier;
import workbench.db.WbConnection;
import workbench.db.objectcache.Namespace;

import workbench.sql.lexer.SQLLexer;
import workbench.sql.lexer.SQLLexerFactory;
import workbench.sql.lexer.SQLToken;

import workbench.util.CollectionUtil;
import workbench.util.StringUtil;

import static workbench.gui.completion.BaseAnalyzer.*;

/**
 * Analyze a DDL statement regarding the context for the auto-completion.
 *
 * Currently only TRUNCATE and DROP are supported.
 *
 * @author Thomas Kellerer
 * @see CreateAnalyzer
 * @see AlterTableAnalyzer
 */
public class DdlAnalyzer
  extends BaseAnalyzer
{
  public static final String DDL_TYPES_FILE = "ddl_types.txt";

  public DdlAnalyzer(WbConnection conn, String statement, int cursorPos)
  {
    super(conn, statement, cursorPos);
  }

  @Override
  protected void checkContext()
  {
    SQLLexer lexer = SQLLexerFactory.createLexer(dbConnection, this.sql);
    SQLToken verbToken = lexer.getNextToken(false, false);
    if (verbToken == null)
    {
      this.context = NO_CONTEXT;
      return;
    }

    String sqlVerb = verbToken.getContents();

    if ("TRUNCATE".equalsIgnoreCase(sqlVerb))
    {
      context = CONTEXT_TABLE_LIST;
      return;
    }

    SQLToken typeToken = lexer.getNextToken(false, false);
    String type = (typeToken != null ? typeToken.getContents() : null);
    SQLToken nameToken = lexer.getNextToken(false, false);

    String tableName = null;

    if (nameToken != null)
    {
      TableIdentifier tbl = new TableIdentifier(nameToken.getContents());
      this.namespaceForTableList = Namespace.fromTable(tbl, dbConnection);
      tableName = tbl.getTableName();
      if (StringUtil.isEmptyString(tableName))
      {
        tableName = null;
      }
    }

    if (namespaceForTableList == null)
    {
      this.namespaceForTableList = getNamespaceFromCurrentWord();
    }

    String mviewType = dbConnection.getMetadata().getMViewTypeName();
    if ("DROP".equals(sqlVerb))
    {
      if (type == null || between(cursorPos,verbToken.getCharEnd(), typeToken.getCharBegin()))
      {
        context = CONTEXT_KW_LIST;
        keywordFile = DDL_TYPES_FILE;
      }

      boolean showObjectList = typeToken != null && cursorPos >= typeToken.getCharEnd()
        && (nameToken == null || (tableName == null && cursorPos == nameToken.getCharEnd()));
      boolean showDropOption = nameToken != null && cursorPos >  nameToken.getCharEnd();

      // for DROP etc, we'll need to be after the table keyword
      // otherwise it could be a DROP PROCEDURE as well.
      if ("TABLE".equals(type))
      {
        if (showObjectList)
        {
          context = CONTEXT_TABLE_LIST;
          setTableTypeFilter(this.dbConnection.getMetadata().getTableTypes());
        }
        else if (showDropOption)
        {
          // probably after the table name
          context = CONTEXT_KW_LIST;
          keywordFile = "table.drop_options.txt";
        }
      }
      else if ("INDEX".equals(type) && showObjectList)
      {
        context = CONTEXT_INDEX_LIST;
      }
      else if ("VIEW".equals(type) && showObjectList)
      {
        context = CONTEXT_TABLE_LIST;
        setTableTypeFilter(dbConnection.getDbSettings().getViewTypes());
      }
      else if (StringUtil.equalStringIgnoreCase(type, mviewType) && showObjectList )
      {
        context = CONTEXT_TABLE_LIST;
        setTableTypeFilter(CollectionUtil.arrayList(mviewType));
      }
      else if (isDropSchema(type))
      {
        if (showObjectList)
        {
          context = CONTEXT_SCHEMA_LIST;
        }
        else if (showDropOption)
        {
          context = CONTEXT_KW_LIST;
          keywordFile = type.trim().toLowerCase() + ".drop_options.txt";
        }
      }
      else if ("DATABASE".equals(type) && showObjectList)
      {
        context = CONTEXT_CATALOG_LIST;
      }
      else if ("SEQUENCE".equals(type) && showObjectList)
      {
        if (showObjectList)
        {
          context = CONTEXT_SEQUENCE_LIST;
        }
        else if (showDropOption)
        {
          // probably after the table name
          context = CONTEXT_KW_LIST;
          keywordFile = "sequence.drop_options.txt";
        }
      }
    }
    else
    {
      context = NO_CONTEXT;
    }
  }

  private boolean isDropSchema(String type)
  {
    return "SCHEMA".equalsIgnoreCase(type) ||
           dbConnection.getMetadata().isOracle() && "USER".equalsIgnoreCase(type);

  }

  @Override
  public String getPasteValue(Object selectedObject)
  {
    if (selectedObject instanceof IndexDefinition)
    {
      IndexDefinition idx = (IndexDefinition)selectedObject;
      idx = idx.createCopy();

      if (StringUtil.equalStringIgnoreCase(namespaceForTableList.getSchema(), idx.getSchema()))
      {
        idx.setSchema(null);
        idx.getBaseTable().setSchema(null);
      }

      if (StringUtil.equalStringIgnoreCase(namespaceForTableList.getCatalog(), idx.getCatalog()))
      {
        idx.setCatalog(null);
        idx.getBaseTable().setCatalog(null);
      }
      GenericObjectDropper dropper = new GenericObjectDropper();
      dropper.setConnection(dbConnection);
      dropper.setObjectTable(idx.getBaseTable());
      String drop = dropper.getDropForObject(idx).toString();
      return drop.replaceFirst("(?i)drop\\s+index\\s+", "");
    }
    return null;
  }

  @Override
  public boolean needsCommaForMultipleSelection()
  {
    return (context != CONTEXT_KW_LIST);
  }

}
