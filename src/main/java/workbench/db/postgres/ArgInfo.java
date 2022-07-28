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
package workbench.db.postgres;

import java.util.ArrayList;
import java.util.List;

import workbench.db.ColumnIdentifier;

import workbench.util.StringUtil;

/**
 *
 * @author Thomas Kellerer
 */
public class ArgInfo
{
  private final List<String> argNames;
  private final List<String> argTypes;
  private final List<String> argModes;
  private final int size;

  public ArgInfo(String names, String types, String modes)
  {
    argNames = StringUtil.stringToList(names, ";", true, true);
    argTypes = StringUtil.stringToList(types, ";", true, true);
    size = Math.max(argNames.size(), argTypes.size());
    if (modes == null)
    {
      int num = argTypes.size();
      argModes = new ArrayList(num);
      for (int i = 0; i < num; i++)
      {
        argModes.add("i");
      }
    }
    else
    {
      argModes = StringUtil.stringToList(modes, ";", true, true);
    }
  }

  public int getNumArgs()
  {
    return size;
  }

  public String getArgName(int i)
  {
    if (i >= 0 && i < argNames.size())
    {
      return argNames.get(i);
    }
    return "$" + (i+1);
  }

  public String getArgType(int i)
  {
    if (i >= 0 && i < argTypes.size())
    {
      return argTypes.get(i);
    }
    return null;
  }

  public String getArgMode(int i)
  {
    if (i >= 0 && i < argModes.size())
    {
      return argModes.get(i);
    }
    return null;
  }

  public String getJDBCArgMode(int i)
  {
    return getJDBCArgMode(getArgMode(i));
  }
  
  public String getJDBCArgMode(String pgMode)
  {
    if (pgMode == null) return null;

    switch (pgMode)
    {
      case "i":
        return "IN";
      case "o":
        return "OUT";
      case "b":
        return "INOUT";
      case "v":
        // treat VARIADIC as input parameter
        return "IN";
      case "t":
        return "RETURN";
      default:
        break;
    }
    return null;
  }
}
