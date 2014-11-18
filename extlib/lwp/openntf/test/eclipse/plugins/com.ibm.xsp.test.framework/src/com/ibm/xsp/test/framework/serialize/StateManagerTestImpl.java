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

package com.ibm.xsp.test.framework.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.faces.application.StateManager;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.util.ClassLoaderUtil;
import com.ibm.xsp.util.FacesUtil;

public class StateManagerTestImpl extends StateManager {

	private byte[] _stored = null;
	
    @Override
    protected Object getComponentStateToSave(FacesContext context) {
        // do nothing
		return null;
	}

    @Override
	protected Object getTreeStructureToSave(FacesContext context) {
        // do nothing
		return null;
	}

    @Override
	protected void restoreComponentState(FacesContext context, UIViewRoot root,
			String renderKitId) {
        // do nothing

	}

    @Override
	protected UIViewRoot restoreTreeStructure(FacesContext context, String viewId,
			String renderKitId) {
        // do nothing
		return null;
	}

    @Override
	public UIViewRoot restoreView(FacesContext context, String viewId, String renderKitId) {
		SerializedView serView = restore();
		
		Node node = (Node) serView.getStructure();
		try {
			UIViewRoot root = (UIViewRoot) node.restore(ClassLoaderUtil.getContextClassLoader(StateManagerTestImpl.class));
			FacesUtil.setRestoreRoot(context, root);
			UIViewRoot old = context.getViewRoot();
			try {
				context.setViewRoot(root);
				root.processRestoreState(context, serView.getState());
			} finally {
				context.setViewRoot(old);
			}
            FacesUtil.setRestoreRoot(context, null);
			return root;
		} catch(Exception e) {
			throw new FacesExceptionEx(e);
		}
	}

    @Override
	public SerializedView saveSerializedView(FacesContext context) {
		UIViewRoot root = context.getViewRoot();
		
		Object treeStructure = null;
		if(!root.isTransient()) {
			treeStructure = new Node(root);
		}
		
		Object componentState = root.processSaveState(context);
		
		SerializedView serView = new SerializedView(treeStructure, componentState);
		
		store(serView);
		
		return serView;
	}

    @Override
	public void writeState(FacesContext context, SerializedView state)
			throws IOException {
		// do nothing
	}
	
	private void store(SerializedView serView) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream data = new ObjectOutputStream(out);
			data.writeObject(serView.getStructure());
			SerializeStreamUtil.streamSaveAndRestore(serView.getStructure());
			data.writeObject(serView.getState());
			SerializeStreamUtil.streamSaveAndRestore(serView.getState());
			data.flush();
			_stored = out.toByteArray();
		} catch(IOException e) {
			throw new FacesExceptionEx(e);
		}
	}
	
	private SerializedView restore() {
		try {
			byte[] stored  = _stored;
			_stored = null;
			ByteArrayInputStream in = new ByteArrayInputStream(stored);
			ObjectInputStream data = new ObjectInputStream(in);
			Object structure = data.readObject();
			Object state = data.readObject();
			return new SerializedView(structure, state);
		} catch(IOException e) {
			throw new FacesExceptionEx(e);
		} catch(ClassNotFoundException e) {
			throw new FacesExceptionEx(e);
		}
	}

}
