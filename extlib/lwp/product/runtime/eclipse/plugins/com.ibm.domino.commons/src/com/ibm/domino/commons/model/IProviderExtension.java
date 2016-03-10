package com.ibm.domino.commons.model;

/**
 * Interface for loading model classes via an extension point. Provider classes that use the
 * extenstion point must implement this interface. @see {@link ProviderFactory#initExtensions}
 * 
 * @author Steve Nikopoulos
 * 
 */
public interface IProviderExtension {

	/**
	 * Returns the list of model interfaces that this extension point implements. An instance of
	 * this class must implement all interfaces returned by this method.
	 */
	public Class<?>[] provides();

}
