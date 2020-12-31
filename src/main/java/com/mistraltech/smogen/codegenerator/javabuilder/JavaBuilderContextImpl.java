package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.mistraltech.smogen.utils.NameUtils.dropUnqualifiedName;
import static com.mistraltech.smogen.utils.NameUtils.getUnqualifiedName;

public class JavaBuilderContextImpl implements JavaBuilderContext {
    private final Map<String, String> classReferences = new HashMap<>();
    private final Map<String, String> classMemberReferences = new HashMap<>();

    protected Set<String> getClassReferences() {
        return new HashSet<>(classReferences.values());
    }

    protected Set<String> getClassMemberReferences() {
        return new HashSet<>(classMemberReferences.values());
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
        boolean hasTypeParams = typeParams != null && !typeParams.isEmpty();
        boolean keyword = isKeyword(unqualifiedName);
        boolean canUseUnqualifiedName = !hasTypeParams && !keyword;

        if (canUseUnqualifiedName) {
            boolean unqualifiedNameAlreadyRegistered = classMemberReferences.containsKey(unqualifiedName);

            if (unqualifiedNameAlreadyRegistered) {
                String registeredFullyQualifiedName = classMemberReferences.get(unqualifiedName);
                boolean unqualifiedNameIsUnique = memberFQN.equals(registeredFullyQualifiedName);

                if (unqualifiedNameIsUnique) {
                    return unqualifiedName;
                } else {
                    return getQualifiedMemberReference(memberFQN, unqualifiedName, typeParams);
                }
            } else {
                classMemberReferences.put(unqualifiedName, memberFQN);
                return unqualifiedName;
            }
        } else {
            return getQualifiedMemberReference(memberFQN, unqualifiedName, typeParams);
        }
    }

    private boolean isKeyword(String name) {
        return name.equals("class");
    }

    private String getQualifiedMemberReference(String memberFQN, String unqualifiedName, String typeParams) {
        String classFQN = dropUnqualifiedName(memberFQN);
        boolean hasTypeParams = typeParams != null && !typeParams.isEmpty();

        StringBuilder sb = new StringBuilder();

        sb.append(normaliseClassReference(classFQN))
                .append('.');

        if (hasTypeParams) {
            sb.append('<').append(typeParams).append('>');
        }

        sb.append(unqualifiedName);

        return sb.toString();
    }
}
