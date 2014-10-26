package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.mistraltech.smogen.utils.NameUtils.getUnqualifiedName;

public abstract class AbstractBuilder<R extends AbstractBuilder> implements Builder {
    private Set<String> classReferences = new HashSet<String>();
    private Set<String> classMemberReferences = new HashSet<String>();
    private Set<AbstractBuilder> nestedBuilders = new HashSet<AbstractBuilder>();

    protected AbstractBuilder() {
    }

    @SuppressWarnings("unchecked")
    protected R self() {
        return (R) this;
    }

    protected R addNestedBuilder(AbstractBuilder nestedBuilder) {
        this.nestedBuilders.add(nestedBuilder);
        return self();
    }

    public R addNestedBuilders(List<? extends AbstractBuilder> nestedBuilders) {
        this.nestedBuilders.addAll(nestedBuilders);
        return self();
    }

    protected Set<String> getClassReferences() {
        Set<String> allClassReferences = new HashSet<String>(classReferences);
        for (AbstractBuilder<?> nestedBuilder : nestedBuilders) {
            allClassReferences.addAll(nestedBuilder.getClassReferences());
        }
        return allClassReferences;
    }

    protected Set<String> getClassMemberReferences() {
        Set<String> allClassMemberReferences = new HashSet<String>(classMemberReferences);
        for (AbstractBuilder<?> nestedBuilder : nestedBuilders) {
            allClassMemberReferences.addAll(nestedBuilder.getClassMemberReferences());
        }
        return allClassMemberReferences;
    }

    protected String getClassReference(String classFQN) {
        classReferences.add(classFQN);
        return getUnqualifiedName(classFQN);
    }

    protected String getClassMemberReference(String memberFQN) {
        classMemberReferences.add(memberFQN);
        return getUnqualifiedName(memberFQN);
    }
}
