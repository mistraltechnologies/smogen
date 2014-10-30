package com.mistraltech.smogen.codegenerator.javabuilder;

public abstract class AbstractBuilder<R extends AbstractBuilder> implements Builder {
    protected AbstractBuilder() {
    }

    @SuppressWarnings("unchecked")
    protected R self() {
        return (R) this;
    }
}
