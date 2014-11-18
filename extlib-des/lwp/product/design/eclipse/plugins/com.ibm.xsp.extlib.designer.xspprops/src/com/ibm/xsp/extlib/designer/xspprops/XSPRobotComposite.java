/*
 * © Copyright IBM Corp. 2013
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */
package com.ibm.xsp.extlib.designer.xspprops;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import com.ibm.commons.swt.SWTLayoutUtils;

public class XSPRobotComposite extends Composite {
    private TableViewer table;
    private XSPRobotUserAgents userAgents = new XSPRobotUserAgents("");
    private final String userAgentStr = "User Agent Keyword"; // $NLX-XSPRobotComposite.UserAgentKeyword-1$

    public XSPRobotComposite(Composite parent, boolean isReadOnly, String agents) {
        super(parent, SWT.NONE);        
        createContents(isReadOnly);
        setUserAgents(agents);
    }
    
    public void setUserAgents(String agents) {
        userAgents.set(agents);
        table.refresh();
    }
    
    public String getUserAgents() {
        return userAgents.get();
    }
    
    protected void createContents(boolean isReadOnly) {
        if(isReadOnly) {
            // One column for display on Page
            setLayout(SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(1));          
            
            // Indent the Compsite for display on Page
            GridData gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 1, 1);
            gd.horizontalIndent = 18;            
            gd.horizontalAlignment = GridData.FILL;
            gd.verticalAlignment = GridData.FILL;
            setLayoutData(gd);
        } else {
            // Two columns for Dialog display
            setLayout(SWTLayoutUtils.createLayoutDefaultSpacing(2));
        }

        if (!isReadOnly) {
            Button addRobot = new Button(this, SWT.PUSH);
            addRobot.setText("Add"); // $NLX-XSPRobotComposite.Add-1$
            addRobot.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    userAgents.add(userAgentStr);                  
                    table.refresh();                
                    table.getTable().setSelection(userAgents.getUserDefinedCount()-1);
                }
            });

            Button delRobot = new Button(this, SWT.PUSH);
            delRobot.setText("Remove"); // $NLX-XSPRobotComposite.Remove-1$
            delRobot.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    int index = table.getTable().getSelectionIndex();
                    if(index >= 0) {
                        userAgents.remove(index);
                        table.refresh();
                        if(index < userAgents.getUserDefinedCount()) {
                            table.getTable().setSelection(index);                            
                        } else {
                            // Set index to end of Table
                            table.getTable().setSelection(userAgents.getUserDefinedCount()-1);                            
                        }
                    }
                }
            });
        }
  
        // Create the TableViewer
        table = new TableViewer(this, SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
        GridData gd = new GridData(SWT.DEFAULT, 84);
        if(!isReadOnly) {
            gd.horizontalSpan = 2;
            gd.verticalAlignment = GridData.FILL;
            gd.grabExcessVerticalSpace = true;
        } else {
            gd.horizontalSpan = 1;
        }
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        table.getTable().setLayoutData(gd);

        // Create the column
        TableLayout layout = new TableLayout();
        layout.addColumnData(new ColumnWeightData(100, false));
        table.getTable().setLayout(layout);
        table.getTable().setHeaderVisible(true);
        table.getTable().setLinesVisible(true);
        TableColumn col = new TableColumn(table.getTable(), SWT.LEFT);
        col.setText(userAgentStr); 
        col.setResizable(false);
        table.setContentProvider(new RobotContentProvider());
        table.setInput("");
        
        // Add the in-cell editing
        if(!isReadOnly) {
            table.setCellModifier(new ICellModifier() {
                public boolean canModify(Object element, String property) {
                    return true;
                }
    
                public Object getValue(Object element, String property) {
                    return element;
                }
    
                public void modify(Object element, String property, Object value) {
                    if(element != null){
                        int index = table.getTable().getSelectionIndex();
                        if(index >= 0) {
                            userAgents.set(index, (String)value);
                            table.refresh();
                            if(index < userAgents.getUserDefinedCount()) {
                                table.getTable().setSelection(index);
                            }
                        }
                    }            
                }
            });
            table.setCellEditors(new CellEditor[] {
                    new TextCellEditor(table.getTable())
            });
            table.setColumnProperties(new String[] {
               ""     
            });
        }
    }   
    
    private class RobotContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object arg0) {
            return userAgents.getUserDefinedArray();
        }

        public void dispose() {
        }

        public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
        }
    }        
}
