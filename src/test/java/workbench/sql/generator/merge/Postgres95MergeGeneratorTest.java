/*
 * This file is part of SQL Workbench/J, https://www.sql-workbench.eu
 *
 * Copyright 2002-2022, Thomas Kellerer.
 *
 * Licensed under a modified Apache License, Version 2.0
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
package workbench.sql.generator.merge;

import java.sql.Types;
import java.util.GregorianCalendar;
import java.util.List;

import workbench.WbTestCase;

import workbench.db.ColumnIdentifier;
import workbench.db.TableIdentifier;

import workbench.storage.DataStore;
import workbench.storage.ResultInfo;

import workbench.util.CollectionUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 *
 * @author Thomas Kellerer
 */
public class Postgres95MergeGeneratorTest
  extends WbTestCase
{

  public Postgres95MergeGeneratorTest()
  {
    super("Postgres95MergeGeneratorTest");
  }

  @Before
  public void setUp()
  {
  }

  @After
  public void tearDown()
  {
  }

  @Test
  public void testGenerateMerge()
  {
    ColumnIdentifier id = new ColumnIdentifier("id", Types.INTEGER);
    id.setIsPkColumn(true);
    ColumnIdentifier fname = new ColumnIdentifier("fname", Types.VARCHAR);
    ColumnIdentifier lname = new ColumnIdentifier("lname", Types.VARCHAR);
    ColumnIdentifier dob = new ColumnIdentifier("dob", Types.DATE);
    ResultInfo info = new ResultInfo(new ColumnIdentifier[] { id, fname, lname, dob });

    TableIdentifier tbl = new TableIdentifier("person");
    info.setUpdateTable(tbl);
    DataStore ds = new DataStore(info);
    ds.forceUpdateTable(tbl);
    int row = ds.addRow();
    ds.setValue(row, 0, Integer.valueOf(42));
    ds.setValue(row, 1, "Arthur");
    ds.setValue(row, 2, "Dent");

    ds.setValue(row, 3, new GregorianCalendar(2012, 0, 1).getTime());

    row = ds.addRow();
    ds.setValue(row, 0, Integer.valueOf(24));
    ds.setValue(row, 1, "Ford");
    ds.setValue(row, 2, "Prefect");
    ds.setValue(row, 3, new GregorianCalendar(2012, 0, 2).getTime());

    Postgres95MergeGenerator gen = new Postgres95MergeGenerator();

    String result = gen.generateMerge(ds);
    String expected =
      "INSERT INTO person\n" +
      "  (id, fname, lname, dob)\n" +
      "VALUES\n" +
      "  (42, 'Arthur', 'Dent', DATE '2012-01-01'),\n" +
      "  (24, 'Ford', 'Prefect', DATE '2012-01-02')\n" +
      "ON CONFLICT (id) DO UPDATE\n" +
      "  SET fname = EXCLUDED.fname,\n" +
      "      lname = EXCLUDED.lname,\n" +
      "      dob = EXCLUDED.dob";
    assertEquals(expected, result);

    List<ColumnIdentifier> cols = CollectionUtil.arrayList(id, fname, lname);
    gen.setColumns(cols);
    result = gen.generateMerge(ds);

    expected =
      "INSERT INTO person\n" +
      "  (id, fname, lname)\n" +
      "VALUES\n" +
      "  (42, 'Arthur', 'Dent'),\n" +
      "  (24, 'Ford', 'Prefect')\n" +
      "ON CONFLICT (id) DO UPDATE\n" +
      "  SET fname = EXCLUDED.fname,\n" +
      "      lname = EXCLUDED.lname";
    assertEquals(expected, result);

  }

}
