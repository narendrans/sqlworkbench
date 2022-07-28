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
package workbench.storage.filter;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Thomas Kellerer
 */
public class RegExComparatorTest
{

  public RegExComparatorTest()
  {
  }

  @Test
  public void testEvaluate()
  {
    RegExComparator comp = new RegExComparator();
    assertTrue(comp.needsValue());
    assertTrue(comp.supportsType(String.class));
    assertTrue(comp.supportsIgnoreCase());
    assertFalse(comp.comparesEquality());

    assertTrue(comp.evaluate("[a-z]+", "Arthur", true));
    assertFalse(comp.evaluate("^[a-z]+", "Arthur", false));

    assertTrue(comp.validateInput("^TEST"));
    assertFalse(comp.validateInput("(bla"));
  }
}