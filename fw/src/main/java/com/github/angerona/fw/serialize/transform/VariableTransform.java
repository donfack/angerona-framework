package com.github.angerona.fw.serialize.transform;

import org.simpleframework.xml.transform.Transform;

import com.github.angerona.fw.reflection.BaseVariable;

/**
 * Abstract base of a Simple XML Transform for the Angerona Variable Wrapper. 
 * Base classes have to implement a method to return sub type of BaseVariable. 
 * Reflection is used to generate a new instance of this type and the setContent
 * method allows the initilization.
 * @author Tim Janus
 *
 * @param <T>	Implementation of the abstract Variable Wrapper 'BaseVariable<?>'.
 */
public abstract class VariableTransform<T extends BaseVariable<?>> implements Transform<T> {

	@Override
	public T read(String arg0) throws Exception {
		T reval = getCls().newInstance();
		reval.setContent(arg0);
		return reval;
	}

	@Override
	public String write(T arg0) throws Exception {
		return arg0.toString();
	}
	
	protected abstract Class<T> getCls();
}
