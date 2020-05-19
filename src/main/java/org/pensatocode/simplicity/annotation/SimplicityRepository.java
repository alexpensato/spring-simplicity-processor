package org.pensatocode.simplicity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface SimplicityRepository {

    /**
     * The name of the entity in the system
     */
    Class<?> entity();

    /**
     * The identifier for the entity's database table
     */
    String table();

    /**
     * The identifier for the primary key column
     */
    String id();
}
