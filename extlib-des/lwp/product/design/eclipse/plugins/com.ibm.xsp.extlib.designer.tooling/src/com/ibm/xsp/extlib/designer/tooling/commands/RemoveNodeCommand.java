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
package com.ibm.xsp.extlib.designer.tooling.commands;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.etools.xve.command.XVECommand;

/**
 * @author doconnor
 *
 */
public class RemoveNodeCommand extends XVECommand {
    private Node nodeToRemove;
    private boolean undo;
    private boolean removeThisParent;
    public RemoveNodeCommand(Node nodeToRemove){
        super();
        this.nodeToRemove = nodeToRemove;
        this.setLabel("Remove tree node"); // $NLX-RemoveNodeCommand.Removetreenode-1$
    }
    
    public RemoveNodeCommand(Node nodeToRemove, boolean removeParent){
        this(nodeToRemove);
        this.removeThisParent = removeParent;
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.commands.Command#canUndo()
     */
    @Override
    public boolean canUndo() {
        return undo && nodeToRemove != null;
    }
    
    
    /* (non-Javadoc)
     * @see org.eclipse.gef.commands.Command#redo()
     */
    @Override
    public void redo() {
        doExecute();
    }

    /* (non-Javadoc)
     * @see com.ibm.etools.xve.command.XVECommand#doExecute()
     */
    @Override
    protected void doExecute() {
        Node parent = nodeToRemove.getParentNode();
        if(this.removeThisParent){
            boolean deleteParent = true;
            NodeList children = parent.getChildNodes();
            if(children != null){
                for(int i = 0; i < children.getLength(); i++){
                    Node n = children.item(i);
                    if(nodeToRemove.equals(n)){
                        continue;
                    }
                    if(n.getNodeType() != Node.TEXT_NODE){
                        deleteParent = false;
                        break;
                    }
                }
            }
            if(deleteParent){
                nodeToRemove = parent;
                parent = parent.getParentNode();
            }
        }
        parent.removeChild(nodeToRemove);
        undo = true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.commands.Command#undo()
     */
    @Override
    public void undo() {
        Node parent = nodeToRemove.getParentNode();
        parent.appendChild(nodeToRemove);
        undo = false;
    }
}