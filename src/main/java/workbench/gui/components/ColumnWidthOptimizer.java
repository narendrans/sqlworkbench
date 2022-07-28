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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import workbench.WbManager;
import workbench.resource.GuiSettings;

import workbench.db.ColumnIdentifier;

import workbench.gui.WbSwingUtilities;
import workbench.gui.renderer.SortHeaderRenderer;
import workbench.gui.renderer.WbRenderer;

import workbench.util.StringUtil;

/**
 * A class to adjust the column width of a WbTable to the displayed values.
 *
 * In order for this to calculate string width's correctly, the table must have been added
 * to the Swing component tree.
 *
 * @author Thomas Kellerer
 */
public class ColumnWidthOptimizer
{
  private WbTable table;

  public ColumnWidthOptimizer(WbTable client)
  {
    this.table = client;
  }

  public void optimizeAllColWidth()
  {
    this.optimizeAllColWidth(GuiSettings.getMinColumnWidth(), GuiSettings.getMaxColumnWidth(), GuiSettings.getIncludeHeaderInOptimalWidth());
  }

  public void optimizeAllColWidth(boolean respectColName)
  {
    this.optimizeAllColWidth(GuiSettings.getMinColumnWidth(), GuiSettings.getMaxColumnWidth(), respectColName);
  }

  public void optimizeAllColWidth(int minWidth, int maxWidth, boolean respectColName)
  {
    int count = this.table.getColumnCount();
    for (int i = 0; i < count; i++)
    {
      this.optimizeColWidth(i, minWidth, maxWidth, respectColName);
    }
    WbSwingUtilities.repaintLater(table);
    WbSwingUtilities.repaintLater(table.getTableHeader());
  }

  public void optimizeColWidth(int aColumn, boolean respectColName)
  {
    this.optimizeColWidth(aColumn, GuiSettings.getMinColumnWidth(), GuiSettings.getMaxColumnWidth(), respectColName);
    WbSwingUtilities.repaintLater(table);
    WbSwingUtilities.repaintLater(table.getTableHeader());
  }

  private void optimizeColWidth(int col, int minWidth, int maxWidth, boolean respectColumnName)
  {
    int width = calculateOptimalColumnWidth(col, minWidth, maxWidth, respectColumnName, null);
    WbSwingUtilities.invoke(() -> {
      if (width > 0)
      {
        TableColumnModel colMod = this.table.getColumnModel();
        TableColumn column = colMod.getColumn(col);
        column.setPreferredWidth(width);
      }
    });
  }

  public int calculateOptimalColumnWidth(int col, int minWidth, int maxWidth, boolean respectColumnName, FontMetrics fontInfo)
  {
    if (table == null || col < 0 || col > table.getColumnCount() - 1)
    {
      return -1;
    }

    int optWidth = minWidth;

    if (respectColumnName)
    {
      optWidth = optimizeHeaderColumn(col, fontInfo);
    }

    int rowCount = this.table.getRowCount();
    int maxLines = GuiSettings.getAutRowHeightMaxLines();
    int addWidth = getAdditionalColumnSpace();

    for (int row = 0; row < rowCount; row++)
    {
      String displayValue = null;
      int stringWidth = 0;

      TableCellRenderer rend = this.table.getCellRenderer(row, col);
      Object value = table.getValueAt(row, col);
      JComponent c = (JComponent)rend.getTableCellRendererComponent(this.table, value, false, false, row, col);
      FontMetrics fm = fontInfo;

      // Don't use table.getFont() as the renderer might have changed the font for this cell
      Font f = c.getFont();
      if (fm == null)
      {
        // The component's font metrics aren't really accurate
        // because the component isn't part of the Swing component tree.
        // In that case the font information is not accurate enough
        // (especially on HiDPI screens)
        fm = getFontMetrics(f);
        if (fm == null)
        {
          fm = c.getFontMetrics(f);
        }
      }

      // The value that is displayed in the table through the renderer
      // is not necessarily identical to the String returned by table.getValueAsString()
      // so we'll first ask the Renderer or its component for the displayed value.
      if (c instanceof WbRenderer)
      {
        WbRenderer wb = (WbRenderer)c;
        stringWidth += wb.addToDisplayWidth();
        displayValue = wb.getDisplayValue();
      }
      else if (c instanceof JTextArea)
      {
        JTextArea text = (JTextArea)c;
        String t = text.getText();
        displayValue = StringUtil.getLongestLine(t, maxLines);
      }
      else if (c instanceof JLabel)
      {
        // DefaultCellRenderer is a JLabel
        displayValue = ((JLabel)c).getText();
      }
      else
      {
        displayValue = this.table.getValueAsString(row, col);
      }

      if (displayValue != null)
      {
        String visible = StringUtil.rtrim(displayValue);
        stringWidth += (int)Math.ceil(f.getStringBounds(visible, fm.getFontRenderContext()).getWidth());
        stringWidth += (int)(fm.getMaxAdvance() / 4);
        if (visible.length() < displayValue.length())
        {
          // accommodate for the "..." display if the string is truncated
          stringWidth += fm.getMaxAdvance() * 3;
        }
      }

      optWidth = Math.max(optWidth, stringWidth + addWidth);
    }

    if (maxWidth > 0)
    {
      optWidth = Math.min(optWidth, maxWidth);
    }
    return optWidth;
  }

  private FontMetrics getFontMetrics(Font f)
  {
    FontMetrics fm = table == null ? null : table.getFontMetrics(f);
    if (fm != null) return fm;

    JWindow win = (JWindow)SwingUtilities.getWindowAncestor(table);
    if (win != null)
    {
      fm = win.getFontMetrics(f);
    }
    if (fm != null) return fm;

    JFrame main = WbManager.getInstance().getCurrentWindow();
    if (main != null)
    {
      fm = main.getFontMetrics(f);
    }
    return fm;
  }

  /**
   * Adjust the column header width after sorting.
   */
  public void optimizeHeader()
  {
    if (table == null) return;
    TableColumnModel colMod = this.table.getColumnModel();
    if (colMod == null) return;
    for (int col = 0; col < table.getColumnCount(); col ++)
    {
      TableColumn column = colMod.getColumn(col);

      // This method is only used to adjust the column header after the sort indicators have been displayed.
      // As the current width is most probably already adjusted (and reflects the size of the data in this column)
      // the new width should not be smaller than the old width (because the row data is not evaluated here!)
      int oldWidth = column.getWidth();
      int width = optimizeHeaderColumn(col, null);

      if (width > oldWidth)
      {
        column.setPreferredWidth(width);
      }
    }
  }

  public int optimizeHeaderColumn(int col, FontMetrics fm)
  {
    if (table == null || col < 0 || col > table.getColumnCount() - 1)
    {
      return -1;
    }

    // JTableHeader.getDefaultRenderer() does not return our own sort renderer
    // therefor we use the "cached" instance directly
    // Only if that is not initialized for some reason, the default renderer is used
    TableCellRenderer rend = table.getHeaderRenderer();
    if (rend == null)
    {
      JTableHeader th = table.getTableHeader();
      rend = th.getDefaultRenderer();
    }
    String colName = table.getColumnName(col);

    JComponent c = (JComponent)rend.getTableCellRendererComponent(table, colName, false, false, -1, col);

    int iconWidth = 0;
    if (table.isViewColumnSorted(col))
    {
      iconWidth = (int)(SortHeaderRenderer.getArrowSize(fm, table.isPrimarySortColumn(col)) * 1.05);
    }

    boolean dataTypeVisible = false;
    boolean remarksVisible = false;
    boolean tableNameVisible = false;

    SortHeaderRenderer renderer = table.getHeaderRenderer();
    DataStoreTableModel model = table.getDataStoreTableModel();

    if (renderer != null)
    {
      dataTypeVisible = renderer.getShowDataType();
      remarksVisible = renderer.getShowRemarks();
      tableNameVisible = renderer.getShowColumnTable();
      if (renderer.getShowTableAsColumnPrefix())
      {
        tableNameVisible = false;
        ColumnIdentifier colId = model.getColumn(col);
        if (colId != null)
        {
          colName = StringUtil.concatWithSeparator(".", colId.getSourceTableName(), colName);
        }
      }
    }

    FontMetrics hfm = fm;
    if (hfm == null)
    {
      Font headerFont = c.getFont();
      hfm = c.getFontMetrics(headerFont);
    }
    Insets ins = c.getInsets();

    int addHeaderSpace = getAdditionalColumnSpace() + ins.left + ins.right;
    int headerWidth = hfm.stringWidth(colName) + addHeaderSpace;

    if (renderer == null || model == null) return headerWidth + iconWidth;

    if (dataTypeVisible)
    {
      String typeName = model.getDbmsType(col);
      if (typeName != null)
      {
        int typeWidth = hfm.stringWidth(typeName) + addHeaderSpace;
        if (typeWidth > headerWidth)
        {
          headerWidth = typeWidth;
        }
      }
    }

    if (remarksVisible)
    {
      String remarks = model.getColumnRemarks(col);
      if (StringUtil.isNonBlank(remarks))
      {
        String word = StringUtil.getFirstWord(remarks);
        int commentWidth = hfm.stringWidth(word) + addHeaderSpace;
        if (commentWidth > headerWidth)
        {
          headerWidth = commentWidth;
        }
      }
    }

    if (tableNameVisible)
    {
      String tname = model.getColumnTable(col);
      if (StringUtil.isNonEmpty(tname))
      {
        int tableWidth = hfm.stringWidth(tname) + addHeaderSpace;
        if (tableWidth > headerWidth)
        {
          headerWidth = tableWidth;
        }
      }
    }

    return headerWidth + iconWidth;
  }

  private int getAdditionalColumnSpace()
  {
    int addWidth = table.getIntercellSpacing().width;
    if (table.getShowVerticalLines())
    {
      addWidth += 4;
    }
    addWidth += table.getColumnModel().getColumnMargin();

    if (addWidth == 0)
    {
      addWidth = 4;
    }
    return addWidth;
  }

  /**
   * Adjusts the columns to the width defined from the
   * underlying tables.
   *
   * This will use getColumnWidth() for each column, it does not take the columns content into account
   *
   * @see #optimizeAllColWidth()
   */
  public void adjustColumns(boolean adjustToColumnLabel)
  {
    if (this.table.getModel() == null) return;

    DataStoreTableModel dwModel = this.table.getDataStoreTableModel();
    if (dwModel == null) return;

    Font f = this.table.getFont();
    FontMetrics fm = this.table.getFontMetrics(f);
    int charWidth = Math.max(fm.getMaxAdvance(), fm.charWidth('M'));
    TableColumnModel colMod = this.table.getColumnModel();
    if (colMod == null) return;

    int minWidth = GuiSettings.getMinColumnWidth();
    int maxWidth = GuiSettings.getMaxColumnWidth();

    int addWidth = this.getAdditionalColumnSpace();

    for (int i = 0; i < colMod.getColumnCount(); i++)
    {
      TableColumn col = colMod.getColumn(i);
      int lblWidth = 0;
      if (adjustToColumnLabel)
      {
        String s = dwModel.getColumnName(i);
        lblWidth = fm.stringWidth(s) + addWidth;
      }

      int width = (dwModel.getColumnWidth(i) * charWidth) + addWidth;
      int w = Math.max(width, lblWidth);

      if (maxWidth > 0)
      {
        w = Math.min(w, maxWidth);
      }
      if (minWidth > 0)
      {
        w = Math.max(w, minWidth);
      }
      col.setPreferredWidth(w);
    }
  }
}
