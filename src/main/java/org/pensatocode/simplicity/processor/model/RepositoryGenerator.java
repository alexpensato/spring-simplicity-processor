package org.pensatocode.simplicity.processor.model;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;

public enum RepositoryGenerator {

    SINGLETON;

    public static RepositoryGenerator getInstance() {
        return SINGLETON;
    }

    public void generateCode(RepositoryAnnotatedItem item, Filer filer) throws IOException {
        // create the template
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, "./processor/src/main/resources/template");
        velocityEngine.init();

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("packageName", item.getRepoPackageName()); // org.pensatocode.simplicity.sample.repository.impl
        velocityContext.put("qualifiedEntityName", item.getEntityQualifiedName()); // org.pensatocode.simplicity.sample.domain.Book
        velocityContext.put("qualifiedMapperName", item.getMapperQualifiedName()); // org.pensatocode.simplicity.sample.repository.mapper.BookMapper
        velocityContext.put("qualifiedInterfaceName", item.getInterfaceQualifiedName()); // org.pensatocode.simplicity.sample.repository.BookRepository;
        velocityContext.put("repositoryBeanName", item.getRepoSpringBeanName()); // bookRepository
        velocityContext.put("concreteRepositoryName", item.getRepoSimpleName()); // BookRepositoryImpl
        velocityContext.put("interfaceName", item.getInterfaceSimpleName()); // BookRepository
        velocityContext.put("mapperName", item.getMapperSimpleName()); // BookMapper
        velocityContext.put("tableName", item.getTable()); // book
        velocityContext.put("entityClass", item.getEntityClass()); // Book.class
        velocityContext.put("idName", item.getId()); // id

        Template template = velocityEngine.getTemplate("repository.vm");

        // write the class
        JavaFileObject javaFileObject = filer.createSourceFile(item.getPackageName() + ".Repository");
        Writer writer = javaFileObject.openWriter();
        template.merge(velocityContext, writer);
        writer.close();
    }

//    public void generateCode(Elements elementUtils, Filer filer) throws IOException {
//        TypeElement superClassName = elementUtils.getTypeElement(qualifiedClassName);
//        String repositoryClassName = superClassName.getSimpleName() + CLASS_SUFFIX;
//        PackageElement pkg = elementUtils.getPackageOf(superClassName);
//        String packageName = pkg.isUnnamed() ? null : pkg.getQualifiedName().toString() + PKG_SUFFIX;
//        String qualifiedRepositoryClassName = packageName + "." + repositoryClassName;
//
//        MethodSpec.Builder method = MethodSpec.methodBuilder("create")
//                .addModifiers(Modifier.PUBLIC)
//                .addParameter(String.class, "id")
//                .returns(TypeName.get(superClassName.asType()));
//
//        // check if id is null
//        method.beginControlFlow("if (id == null)")
//                .addStatement("throw new IllegalArgumentException($S)", "id is null!")
//                .endControlFlow();
//
//        // Generate items map
//
//        for (RepositoryAnnotatedItem item : itemsMap.values()) {
//            method.beginControlFlow("if ($S.equals(id))", item.getId())
//                    .addStatement("return new $L()", item.getEntityTypeElement().getQualifiedName().toString())
//                    .endControlFlow();
//        }
//
//        method.addStatement("throw new IllegalArgumentException($S + id)", "Unknown id = ");
//
//        TypeSpec typeSpec = TypeSpec.classBuilder(repositoryClassName).addMethod(method.build()).build();
//
//        // Write file
//        JavaFile.builder(packageName, typeSpec).build().writeTo(filer);
//    }
}
