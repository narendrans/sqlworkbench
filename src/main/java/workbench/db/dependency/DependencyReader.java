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
package workbench.db.dependency;

import java.util.List;

import workbench.db.DbObject;
import workbench.db.WbConnection;

/**
 *
 * @author Thomas Kellerer
 */
public interface DependencyReader
{
  /**
   * Return a list of objects that the base object uses.
   *
   *
   * @param base  the base object to check for
   * @return a list of objects that depend on <tt>base</tt> or on which <tt>base</tt> depends on.
   */
  List<DbObject> getUsedObjects(WbConnection connection, DbObject base);

  /**
   * Return a list of objects that are using the base object.
   *
   * @param base  the base object to check for
   * @return a list of objects that depend on <tt>base</tt> or on which <tt>base</tt> depends on.
   */
  List<DbObject> getUsedBy(WbConnection connection, DbObject base);

  boolean supportsUsedByDependency(String objectType);
  boolean supportsIsUsingDependency(String objectType);
}
