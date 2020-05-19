package org.pensatocode.simplicity.processor;

import org.pensatocode.simplicity.annotation.SimplicityRepository;
import org.pensatocode.simplicity.processor.model.RepositoryAnnotatedItem;
import org.pensatocode.simplicity.processor.model.RepositoryGenerator;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
//import javax.lang.model.type.TypeKind;
//import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
//import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedAnnotationTypes({"org.pensatocode.simplicity.jdbc.annotation.SimplicityRepository"})
public class RepositoryProcessor extends AbstractProcessor {

//    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;
    private final Set<RepositoryAnnotatedItem> items = new HashSet<>();

    @Override
    public synchronized void init(ProcessingEnvironment env){
        super.init(env);
//        typeUtils = env.getTypeUtils();
        elementUtils = env.getElementUtils();
        filer = env.getFiler();
        messager = env.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // Itearate over all @SimplicityRepository annotated elements
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(SimplicityRepository.class)) {
            // Only a class or an interface can be annotated with @SimplicityRepository
            if (!(annotatedElement instanceof TypeElement)) {
                error(annotatedElement, "Only classes and interfaces can be annotated with @%s",
                        SimplicityRepository.class.getSimpleName());
                return true; // Exit processing
            }
            // Check if TypeElement is not an interface
            if (annotatedElement.getKind() != ElementKind.INTERFACE) {
                error(annotatedElement, "Only interfaces are allowed to be annotated with @%s at the moment",
                        SimplicityRepository.class.getSimpleName());
                return true; // Exit processing
            }
            // Check if TypeElement is a class
//            if (annotatedElement.getKind() == ElementKind.CLASS) {
//
//            }

            // We can cast it, because we know it's true
            TypeElement typeElement = (TypeElement) annotatedElement;

            try {
                RepositoryAnnotatedItem annotatedItem = new RepositoryAnnotatedItem(typeElement, elementUtils);
                if (!isValid(annotatedItem)) {
                    return true; // Error message printed, exit processing
                }
                // Everything is fine, so try to add
                items.add(annotatedItem);

            } catch (IllegalArgumentException e) {
                // One of @SimplicityRepository attributes is empty
                error(typeElement, e.getMessage());
            }
        }

        RepositoryGenerator repositoryGenerator = RepositoryGenerator.getInstance();
        try {
            // Generate code
            for (RepositoryAnnotatedItem item : items) {
                repositoryGenerator.generateCode(item, filer);
            }
            items.clear();

        } catch (IOException e) {
            error(null, e.getMessage());
        }

        return true;
    }

    private boolean isValid(RepositoryAnnotatedItem item) {

        // Cast to TypeElement, has more type specific methods
        TypeElement entityElement = item.getAnnotatedTypeElement();

        if (!entityElement.getModifiers().contains(Modifier.PUBLIC)) {
            error(entityElement, "The class %s is not public.",
                    entityElement.getQualifiedName().toString());
            return false;
        }

        // Check if it's not an abstract class
//        if (! classElement.getModifiers().contains(Modifier.ABSTRACT)) {
//            error(classElement, "The class %s is not abstract. You can't annotate concrete classes with @%",
//                    classElement.getQualifiedName().toString(), SimplicityRepository.class.getSimpleName());
//            return false;
//        }

        // Check inheritance: Class must implement @JdbcRepository;
//        TypeElement jdbcRepositoryElement = elementUtils.getTypeElement(JdbcRepository.class.getCanonicalName());
//
//        if (entityElement.getKind() == ElementKind.INTERFACE) {
//            // Check interface extended
//            if (! entityElement.getInterfaces().contains(jdbcRepositoryElement.asType())) {
//                error(entityElement, "The interace %s annotated with @%s must extend the interface %s",
//                        entityElement.getQualifiedName().toString(), SimplicityRepository.class.getSimpleName(),
//                        JdbcRepository.class.getCanonicalName());
//                return false;
//            }
//        } else if (entityElement.getKind() == ElementKind.CLASS) {
//            // Check interface implemented
//            if (! entityElement.getInterfaces().contains(jdbcRepositoryElement.asType())) {
//                error(entityElement, "The class %s annotated with @%s must implement the interface %s",
//                        entityElement.getQualifiedName().toString(), SimplicityRepository.class.getSimpleName(),
//                        JdbcRepository.class.getCanonicalName());
//                return false;
//            }
//        } else {
//            // Check subclassing
//            TypeElement currentClass = entityElement;
//            while (true) {
//                TypeMirror superClassType = currentClass.getSuperclass();
//
//                if (superClassType.getKind() == TypeKind.NONE) {
//                    // Basis class (java.lang.Object) reached, so exit
//                    error(entityElement, "The class %s annotated with @%s must inherit from %s",
//                            entityElement.getQualifiedName().toString(), SimplicityRepository.class.getSimpleName(),
//                            JdbcRepository.class.getCanonicalName());
//                    return false;
//                }
//
//                if (superClassType.toString().equals(JdbcRepository.class.getCanonicalName())) {
//                    // Required super class found
//                    break;
//                }
//
//                // Moving up in inheritance tree
//                currentClass = (TypeElement) typeUtils.asElement(superClassType);
//            }
//        }

        return true;

        // No empty constructor found
//        error(entityElement, "The class %s must provide an public empty default constructor",
//                entityElement.getQualifiedName().toString());
//        return false;
    }


    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }
}
