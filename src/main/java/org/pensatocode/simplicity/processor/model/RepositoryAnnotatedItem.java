package org.pensatocode.simplicity.processor.model;

import org.pensatocode.simplicity.annotation.SimplicityRepository;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Elements;

public class RepositoryAnnotatedItem {

    private static final String CLASS_EXTENSION = ".class";
    private static final String MAPPER_CLASS_SUFFIX = "Mapper";
    private static final String MAPPER_PKG_SUFFIX = ".mapper";
    private static final String REPO_CLASS_SUFFIX = "Impl";
    private static final String REPO_PKG_SUFFIX = ".impl";

    private final String id;
    private final String table;
    private final TypeElement annotatedTypeElement;

    private String packageName;
    private String entityClass;
    private String entityQualifiedName;
    private String mapperSimpleName;
    private String mapperQualifiedName;
    private String interfaceSimpleName;
    private String interfaceQualifiedName;
    private String repoSpringBeanName;
    private String repoSimpleName;
    private String repoPackageName;


    public RepositoryAnnotatedItem(TypeElement typeElement, Elements elementUtils) throws IllegalArgumentException {
        this.annotatedTypeElement = typeElement;
        SimplicityRepository annotation = typeElement.getAnnotation(SimplicityRepository.class);

        id = annotation.id();
        table = annotation.table();
        Class<?> entity = annotation.entity();

        if (isEmpty(id)) {
            throw new IllegalArgumentException(
                    String.format("id() in @%s for class %s is null or empty! that's not allowed",
                            SimplicityRepository.class.getSimpleName(), typeElement.getQualifiedName().toString()));
        }
        if (isEmpty(table)) {
            throw new IllegalArgumentException(
                    String.format("table() in @%s for class %s is null or empty! that's not allowed",
                            SimplicityRepository.class.getSimpleName(), typeElement.getQualifiedName().toString()));
        }
        if (isEmpty(entity)) {
            throw new IllegalArgumentException(
                    String.format("entity() in @%s for class %s is null or empty! that's not allowed",
                            SimplicityRepository.class.getSimpleName(), typeElement.getQualifiedName().toString()));
        }

        try {
            // package
            PackageElement pkg = elementUtils.getPackageOf(typeElement);
            this.packageName = pkg.isUnnamed() ? "" : pkg.getQualifiedName().toString();
            // entity
            this.entityClass = entity.getSimpleName() + CLASS_EXTENSION;
//            String entitySimpleName = entity.getSimpleName();
            this.entityQualifiedName = entity.getCanonicalName();
            // mapper
            this.mapperSimpleName = entity.getSimpleName() + MAPPER_CLASS_SUFFIX;
            this.mapperQualifiedName = this.packageName + MAPPER_PKG_SUFFIX + "." + this.mapperSimpleName;
            // interface
            this.interfaceSimpleName = typeElement.getSimpleName().toString();
            this.interfaceQualifiedName = typeElement.getQualifiedName().toString();
            // repository
            this.repoSpringBeanName = decapitalize(this.interfaceSimpleName);
            this.repoSimpleName = this.interfaceSimpleName + REPO_CLASS_SUFFIX;
            this.repoPackageName = this.packageName + REPO_PKG_SUFFIX;

        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            this.interfaceSimpleName = classTypeElement.getSimpleName().toString();
            this.interfaceQualifiedName = classTypeElement.getQualifiedName().toString();

        }
    }

    private boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

    private String decapitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

//    /**
//     * Get the entity class as specified in {@link SimplicityRepository#entity()}.
//     * return the entity
//     */
//    public Class<?> getEntity() {
//        return entity;
//    }

    /**
     * Get the table name as specified in {@link SimplicityRepository#table()}.
     * return the table name
     */
    public String getTable() {
        return table;
    }

    /**
     * Get the id name as specified in {@link SimplicityRepository#id()}.
     * return the id name
     */
    public String getId() {
        return id;
    }

    /**
     * The original element that was annotated with @SimplicityRepository
     */
    public TypeElement getAnnotatedTypeElement() {
        return annotatedTypeElement;
    }


    public String getPackageName() {
        return packageName;
    }

    public String getEntityClass() {
        return entityClass;
    }

    public String getEntityQualifiedName() {
        return entityQualifiedName;
    }

//    public String getEntitySimpleName() {
//        return entitySimpleName;
//    }

    public String getMapperQualifiedName() {
        return mapperQualifiedName;
    }

    public String getMapperSimpleName() {
        return mapperSimpleName;
    }

    public String getInterfaceQualifiedName() {
        return interfaceQualifiedName;
    }

    public String getInterfaceSimpleName() {
        return interfaceSimpleName;
    }

    public String getRepoSpringBeanName() {
        return repoSpringBeanName;
    }

//    public String getRepoQualifiedName() {
//        return repoQualifiedName;
//    }

    public String getRepoPackageName() {
        return repoPackageName;
    }

    public String getRepoSimpleName() {
        return repoSimpleName;
    }
}
