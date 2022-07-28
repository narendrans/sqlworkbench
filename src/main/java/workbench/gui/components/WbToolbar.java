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

import java.awt.Component;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;

import workbench.gui.actions.WbAction;
import workbench.gui.lnf.LnFHelper;

/**
 *
 * @author  Thomas Kellerer
 */
public class WbToolbar
  extends JToolBar
{
  public WbToolbar()
  {
    super();
    this.setFloatable(false);
    this.setRollover(true);
    putClientProperty("Synthetica.opaque", Boolean.FALSE);
  }

  @Override
  public JButton add(Action a)
  {
    JButton button;

    if (a instanceof WbAction)
    {
      button = ((WbAction)a).getToolbarButton();
    }
    else
    {
      button = new WbToolbarButton(a);
    }
    this.add(button);
    button.setRolloverEnabled(true);
    return button;
  }

  public JButton add(WbAction a)
  {
    return add(a, -1);
  }

  public JButton add(WbAction a, int index)
  {
    JButton button = a.getToolbarButton();
    button.setRolloverEnabled(true);
    this.add(button, index);
    return button;
  }

  @Override
  public void addSeparator()
  {
    this.addSeparator(-1);
  }

  public void addSeparator(int index)
  {
    this.add(createSeparator(), index);
  }

  private JComponent createSeparator()
  {
    if (LnFHelper.isWebLaf())
    {
      try
      {
        Class cls = Class.forName("com.alee.laf.toolbar.WebToolBarSeparator");
        return (JComponent)cls.getDeclaredConstructor().newInstance();
      }
      catch (Throwable th)
      {

      }
    }
    return new WbToolbarSeparator();
  }

  public void removeAction(WbAction action)
  {
    if (action == null) return;
    int indexToRemove = -1;

    int count = getComponentCount();
    for (int i=0; i < count; i++)
    {
      Component comp = getComponentAtIndex(i);
      if (comp instanceof AbstractButton)
      {
        Action buttonAction = ((AbstractButton)comp).getAction();
        if (buttonAction == action)
        {
          indexToRemove = i;
          break;
        }
      }
    }
    invalidate();
    remove(indexToRemove);
    revalidate();
  }
}
