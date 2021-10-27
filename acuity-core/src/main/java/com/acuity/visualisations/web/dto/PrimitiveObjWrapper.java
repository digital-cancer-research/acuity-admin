package com.acuity.visualisations.web.dto;

import java.io.Serializable;

public class PrimitiveObjWrapper<T> implements Serializable {
	
	/**
	 * adding default to satisfy implement warnings
	 */
	private static final long serialVersionUID = 1L;
	
	private T obj;

    public PrimitiveObjWrapper(T obj) {
        this.obj = obj;
    }

    public T getObj() {
        return obj;
    }
}
