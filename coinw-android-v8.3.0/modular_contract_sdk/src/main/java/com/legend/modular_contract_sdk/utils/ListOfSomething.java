package com.legend.modular_contract_sdk.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @link http://stackoverflow.com/questions/14139437/java-type-generic-as-argument-for-gson
 * Created by Spencer on 6/23/18.
 */
public class ListOfSomething<X> implements ParameterizedType {

    private Class<?> wrapped;

    public ListOfSomething(Class<X> wrapped) {
        this.wrapped = wrapped;
    }

    public Type[] getActualTypeArguments() {
        return new Type[]{wrapped};
    }

    public Type getRawType() {
        return List.class;
    }

    public Type getOwnerType() {
        return null;
    }

}
