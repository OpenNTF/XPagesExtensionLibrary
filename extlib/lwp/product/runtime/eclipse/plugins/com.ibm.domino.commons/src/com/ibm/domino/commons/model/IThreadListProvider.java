package com.ibm.domino.commons.model;

public interface IThreadListProvider extends IProviderExtension {

	public int[] getAllNotesInThread(final String customerId, final String subscriberId, final String tua0) throws ModelException;

}
