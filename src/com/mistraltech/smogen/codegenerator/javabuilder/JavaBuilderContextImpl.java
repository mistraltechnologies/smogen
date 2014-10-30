package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.mistraltech.smogen.utils.NameUtils.dropUnqualifiedName;
import static com.mistraltech.smogen.utils.NameUtils.getUnqualifiedName;

public class JavaBuilderContextImpl implements JavaBuilderContext {
    private Map<String, String> classReferences = new HashMap<String, String>();
    private Map<String, String> classMemberReferences = new HashMap<String, String>();

    protected Set<String> getClassReferences() {
        return new HashSet<String>(classReferences.values());
    }

    protected Set<String> getClassMemberReferences() {
        return new HashSet<String>(classMemberReferences.values());
    }

    @Override
    public String normaliseClassReference(String classFQN) {
        String packageName = dropUnqualifiedName(classFQN);

        if (packageName.isEmpty()) {
            return classFQN;
        } else {
            String unqualifiedName = getUnqualifiedName(classFQN);
            if (!classReferences.containsKey(unqualifiedName)) {
                classReferences.put(unqualifiedName, classFQN);
                return unqualifiedName;
            } else {
                return classReferences.get(unqualifiedName).equals(classFQN) ? unqualifiedName : classFQN;
            }
        }
    }

    @Override
    public String normaliseClassMemberReference(String memberFQN, String typeParams) {
        String unqualifiedName = getUnqualifiedName(memberFQN);

        if (typeParams == null && !classMemberReferences.containsKey(unqualifiedName)) {
            classMemberReferences.put(unqualifiedName, memberFQN);
            return unqualifiedName;
        }

        if (typeParams == null && classMemberReferences.get(unqualifiedName).equals(memberFQN)) {
            return classMemberReferences.get(unqualifiedName);
        }

        String classFQN = dropUnqualifiedName(memberFQN);

        return normaliseClassReference(classFQN) + ".<" + typeParams + ">" + unqualifiedName;
    }
}
