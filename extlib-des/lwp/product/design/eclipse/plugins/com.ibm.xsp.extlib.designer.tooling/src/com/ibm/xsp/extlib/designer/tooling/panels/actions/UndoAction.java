/*
 * © Copyright IBM Corp. 2011
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
package com.ibm.xsp.extlib.designer.tooling.panels.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.actions.ActionFactory;

/**
 * @author doconnor
 *
 */
public class UndoAction extends Action {
    private CommandStack stack;
    private List<Command> commands;
    private IAction redo;
    /**
     * @param actionID
     * @param text
     */
    public UndoAction(CommandStack stack, IAction redo) {
        super("&Undo"); // $NLX-UndoAction.Undo-1$
        this.stack = stack;
        if(this.stack != null){
            setEnabled(this.stack.canUndo());
        }
        this.redo = redo;
    }
    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        if(stack != null && stack.canUndo()){
            if(commands == null || commands.size() < 1){
                setEnabled(false);
                return;
            }
            stack.undo();
            commands.remove(0);
            setEnabled(stack.canUndo() && commands.size() > 0);
            if(redo != null){
                redo.setEnabled(stack.canRedo());
            }
        }
    }
    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#getActionDefinitionId()
     */
    @Override
    public String getActionDefinitionId() {
        return ActionFactory.UNDO.getId();
    }
    
    public void append(Command cmd){
        if(commands == null){
            commands = new ArrayList<Command>();
        }
        commands.add(0, cmd);
        setEnabled(stack.canUndo());
    }
}