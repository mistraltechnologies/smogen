package com.mistraltech.smogen.codegenerator.javabuilder;

abstract class AbstractBuilder<T extends AbstractBuilder<T>> implements Builder {
    AbstractBuilder() {
    }

    @SuppressWarnings("unchecked")
    T self() {
        return (T) this;
    }
}
