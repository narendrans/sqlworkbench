/*
 * This file is part of SQL Workbench/J, https://www.sql-workbench.eu
 *
 * Copyright 2002-2019 Thomas Kellerer.
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
package workbench.console;

import java.io.PrintWriter;
import java.io.StringWriter;

import workbench.util.StringUtil;

/**
 *
 * @author Thomas Kellerer
 */
public interface TextPrinter
{
  void print(String msg);
  void println(String msg);
  void println();
  void flush();

  public static TextPrinter createPrinter(final PrintWriter pw)
  {
    return new TextPrinter()
    {
      @Override
      public void print(String msg)
      {
        pw.print(msg);
      }

      @Override
      public void println(String msg)
      {
        pw.println(msg);
      }

      @Override
      public void println()
      {
        pw.println();
      }

      @Override
      public void flush()
      {
        pw.flush();
      }
    };
  }
  
  public static TextPrinter createPrinter(final StringWriter writer)
  {
    return new TextPrinter()
    {
      @Override
      public void print(String msg)
      {
        writer.append(msg);
      }

      @Override
      public void println(String msg)
      {
        writer.append(msg);
        writer.append(StringUtil.LINE_TERMINATOR);
      }

      @Override
      public void println()
      {
        writer.append(StringUtil.LINE_TERMINATOR);
      }

      @Override
      public void flush()
      {
        writer.flush();
      }
    };
  }
}
