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
package workbench.gui.components;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Window;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import workbench.log.CallerInfo;
import workbench.log.LogMgr;
import workbench.resource.ResourceMgr;

import workbench.gui.WbSwingUtilities;

import workbench.util.ClassFinder;
import workbench.util.StringUtil;
import workbench.util.WbThread;

/**
 *
 * @author Thomas Kellerer
 */
public class ClassFinderGUI
{
  private ClassFinder finder;
  private JTextComponent className;
  private List<File> classPath;
  private JLabel statusBar;
  private String statusKey;
  private String selectWindowKey;

  public ClassFinderGUI(ClassFinder clsFinder, JTextComponent target, JLabel status)
  {
    finder = clsFinder;
    className = target;
    statusBar = status;
  }

  public void setClassPath(List<File> libraries)
  {
    if (libraries != null)
    {
      classPath = new ArrayList<>(libraries);
    }
    else
    {
      classPath = null;
    }
  }

  public void setStatusBarKey(String key)
  {
    statusKey = key;
  }

  public void setWindowTitleKey(String key)
  {
    selectWindowKey = key;
  }

  public static String selectEntry(List<String> entries, String toSelect, String windowTitleKey, Window parentWindow)
  {
    JPanel p = new JPanel(new BorderLayout());
    DefaultListModel model = new DefaultListModel();
    for (String s : entries)
    {
      model.addElement(s);
    }
    JList<String> list = new JList<>(model);
    list.setVisibleRowCount(Math.min(10, entries.size() + 1));
    if (StringUtil.isNonBlank(toSelect))
    {
      list.setSelectedValue(toSelect, true);
    }
    JScrollPane scroll = new JScrollPane(list);
    p.add(scroll, BorderLayout.CENTER);
    boolean ok = WbSwingUtilities.getOKCancel(ResourceMgr.getString(windowTitleKey), parentWindow, p);
    if (ok)
    {
      return list.getSelectedValue();
    }
    return null;
  }

  protected String selectEntry(List<String> entries)
  {
    return selectEntry(entries, className.getText(), selectWindowKey, SwingUtilities.getWindowAncestor(className));
  }

  protected void checkFinished(final List<String> classes)
  {
    if (classes == null) return;
    EventQueue.invokeLater(() ->
    {
      statusBar.setText("");
      if (classes.size() == 1)
      {
        className.setText(classes.get(0));
      }
      else if (classes.size() > 0)
      {
        String cls = selectEntry(classes);
        if (cls != null)
        {
          className.setText(cls);
        }
      }
    });
  }

  public void startCheck()
  {
    Thread t = new WbThread("CheckDriver")
    {
      @Override
      public void run()
      {
        statusBar.setText(ResourceMgr.getString(statusKey));
        try
        {
          List<String> drivers = finder.findImplementations(classPath);
          checkFinished(drivers);
        }
        catch (Exception e)
        {
          LogMgr.logError(new CallerInfo(){}, "Could not find JDBC driver class", e);
        }
      }
    };
    t.start();
  }

}
