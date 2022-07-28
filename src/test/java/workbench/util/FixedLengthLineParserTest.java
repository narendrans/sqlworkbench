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
package workbench.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author thomas
 */
public class FixedLengthLineParserTest
{

  @Test
  public void testParser()
  {
    List<Integer> cols = new ArrayList<>();
    cols.add(Integer.valueOf(5));
    cols.add(Integer.valueOf(1));
    cols.add(Integer.valueOf(10));
    FixedLengthLineParser parser = new FixedLengthLineParser(cols);
    String line = "12345H1234567890";
    parser.setLine(line);
    String first = parser.getNext();
    assertEquals("12345", first);
    String second = parser.getNext();
    assertEquals("H", second);
    String third = parser.getNext();
    assertEquals("1234567890", third);

    line = "    1H        10";
    parser.setLine(line);
    parser.setTrimValues(true);
    first = parser.getNext();
    assertEquals("1", first);
    second = parser.getNext();
    assertEquals("H", second);
    third = parser.getNext();
    assertEquals("10", third);

    parser.setLine(line);
    parser.setTrimValues(false);
    first = parser.getNext();
    assertEquals("    1", first);
    second = parser.getNext();
    assertEquals("H", second);
    third = parser.getNext();
    assertEquals("        10", third);


    cols.clear();
    cols.add(Integer.valueOf(2));
    cols.add(Integer.valueOf(2));
    cols.add(Integer.valueOf(4));
    parser = new FixedLengthLineParser(cols);
    parser.setLine("1122333");
    parser.setTrimValues(false);
    first = parser.getNext();
    assertEquals("11", first);
    second = parser.getNext();
    assertEquals("22", second);
    third = parser.getNext();
    assertEquals("333", third);

    parser.setLine("112244444444444");
    parser.setTrimValues(false);
    first = parser.getNext();
    assertEquals("11", first);
    second = parser.getNext();
    assertEquals("22", second);
    third = parser.getNext();
    assertEquals("4444", third);

  }
}
