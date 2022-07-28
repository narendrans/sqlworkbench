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

import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Icon;

import workbench.resource.ResourceMgr;

import workbench.gui.WbSwingUtilities;

/**
 *
 * @author Thomas Kellerer
 */
public class FlatButton
  extends WbButton
{
  private static final Insets SMALL_MARGIN = new Insets(3,5,3,5);
  private boolean useDefaultMargin;
  private Insets customInsets;
  private String enableMsgKey;

  public FlatButton()
  {
    super();
  }

  public FlatButton(Action action)
  {
    super(action);
  }

  public FlatButton(Icon icon)
  {
    super(icon);
  }

  public FlatButton(String label)
  {
    super(label);
  }

  public void showMessageOnEnable(String resourceKey)
  {
    this.enableMsgKey = resourceKey;
  }

  public void setUseDefaultMargin(boolean useDefaultMargin)
  {
    this.useDefaultMargin = useDefaultMargin;
  }

  public void setCustomInsets(Insets insets)
  {
    this.customInsets = insets;
  }

  @Override
  public Insets getInsets()
  {
    if (useDefaultMargin)
    {
      return super.getInsets();
    }
    return customInsets == null ? SMALL_MARGIN : customInsets;
  }

  @Override
  public Insets getMargin()
  {
    if (useDefaultMargin)
    {
      return super.getMargin();
    }
    return customInsets == null ? SMALL_MARGIN : customInsets;
  }

  @Override
  public void setEnabled(boolean flag)
  {
    boolean wasEnabled = this.isEnabled();
    super.setEnabled(flag);
    if (flag && !wasEnabled && this.enableMsgKey != null)
    {
      WbSwingUtilities.showToolTip(this, "<html><p style=\"margin:4px\">" + ResourceMgr.getString(enableMsgKey) + "</p></html>");
    }
  }
}
