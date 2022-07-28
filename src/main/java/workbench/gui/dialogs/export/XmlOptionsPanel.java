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
package workbench.gui.dialogs.export;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import workbench.resource.ResourceMgr;
import workbench.resource.Settings;

/**
 *
 * @author  Thomas Kellerer
 */
public class XmlOptionsPanel
  extends JPanel
  implements XmlOptions
{

  public XmlOptionsPanel()
  {
    super();
    initComponents();
  }

  public void saveSettings(String type)
  {
    Settings s = Settings.getInstance();
    s.setProperty("workbench." + type + ".xml.usecdata", this.getUseCDATA());
    s.setProperty("workbench." + type + ".xml.verbosexml", this.getUseVerboseXml());
    s.setProperty("workbench." + type + ".xml.xmlversion", getXMLVersion());
  }

  public void restoreSettings(String type)
  {
    Settings s = Settings.getInstance();
    this.setUseCDATA(s.getBoolProperty("workbench." + type + ".xml.usecdata"));
    this.setUseVerboseXml(s.getBoolProperty("workbench." + type + ".xml.verbosexml", true));
    String version = s.getProperty("workbench." + type + ".xml.xmlversion", s.getDefaultXmlVersion());
    if (version.equals("1.0"))
    {
      xml10.setSelected(true);
    }
    else if (version.equals("1.1"))
    {
      xml11.setSelected(true);
    }
  }

  @Override
  public String getXMLVersion()
  {
    if (xml11.isSelected())
    {
      return "1.1";
    }
    return "1.0";
  }

  @Override
  public boolean getUseVerboseXml()
  {
    return this.verboseXmlCheckBox.isSelected();
  }

  @Override
  public void setUseVerboseXml(boolean flag)
  {
    this.verboseXmlCheckBox.setSelected(flag);
  }

  @Override
  public boolean getUseCDATA()
  {
    return useCdata.isSelected();
  }

  @Override
  public void setUseCDATA(boolean flag)
  {
    useCdata.setSelected(flag);
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    GridBagConstraints gridBagConstraints;

    versionGroup = new ButtonGroup();
    useCdata = new JCheckBox();
    verboseXmlCheckBox = new JCheckBox();
    xml10 = new JRadioButton();
    xml11 = new JRadioButton();

    setLayout(new GridBagLayout());

    useCdata.setText(ResourceMgr.getString("LblExportUseCDATA")); // NOI18N
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
    add(useCdata, gridBagConstraints);

    verboseXmlCheckBox.setText(ResourceMgr.getString("LblExportVerboseXml")); // NOI18N
    verboseXmlCheckBox.setToolTipText(ResourceMgr.getString("d_LblExportVerboseXml")); // NOI18N
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    add(verboseXmlCheckBox, gridBagConstraints);

    versionGroup.add(xml10);
    xml10.setSelected(true);
    xml10.setText("XML 1.0");
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
    add(xml10, gridBagConstraints);

    versionGroup.add(xml11);
    xml11.setText("XML 1.1");
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
    gridBagConstraints.weighty = 1.0;
    add(xml11, gridBagConstraints);
  }// </editor-fold>//GEN-END:initComponents


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private JCheckBox useCdata;
  private JCheckBox verboseXmlCheckBox;
  private ButtonGroup versionGroup;
  private JRadioButton xml10;
  private JRadioButton xml11;
  // End of variables declaration//GEN-END:variables

}
