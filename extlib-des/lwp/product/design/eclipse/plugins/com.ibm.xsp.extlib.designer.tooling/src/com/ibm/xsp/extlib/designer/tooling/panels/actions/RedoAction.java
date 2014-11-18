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

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.actions.ActionFactory;

/**
 * @author doconnor
 *
 */
public class RedoAction extends Action {
    private CommandStack stack;
    private UndoAction undo;
    
    private class DummyCommand extends Command{
        
    }

    /**
     * @param text
     */
    public RedoAction(CommandStack stack) {
        super("&Redo"); // $NLX-RedoAction.Redo-1$
        this.stack = stack;
        if(this.stack != null){
            setEnabled(this.stack.canRedo());
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        if(stack != null && stack.canRedo()){
            stack.redo();
        }
        setEnabled(stack.canRedo());
        undo.append(new DummyCommand());
        super.run();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#getActionDefinitionId()
     */
    @Override
    public String getActionDefinitionId() {
        return ActionFactory.REDO.getId();
    }
    
    public void setUndoAction(UndoAction undo){
        this.undo = undo;
    }
}