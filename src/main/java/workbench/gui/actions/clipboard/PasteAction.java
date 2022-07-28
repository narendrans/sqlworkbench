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
package workbench.gui.actions.clipboard;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import workbench.interfaces.ClipboardSupport;
import workbench.resource.PlatformShortcuts;
import workbench.resource.ResourceMgr;

import workbench.gui.actions.WbAction;

import workbench.util.MacOSHelper;

/**
 * Action to paste the contents of the clipboard into the entry field
 *
 * @author Thomas Kellerer
 */
public class PasteAction
  extends WbAction
{
  private ClipboardSupport client;

  public PasteAction(ClipboardSupport aClient)
  {
    super();
    this.client = aClient;
    KeyStroke alternateKey = null;
    if (!MacOSHelper.isMacOS())
    {
      alternateKey = KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, KeyEvent.SHIFT_DOWN_MASK);
    }
    initMenuDefinition("MnuTxtPaste", PlatformShortcuts.getDefaultPasteShortcut(), alternateKey);
    this.setIcon("paste");
    this.setMenuItemName(ResourceMgr.MNU_TXT_EDIT);
  }

  @Override
  public void executeAction(ActionEvent e)
  {
    this.client.paste();
  }

}
