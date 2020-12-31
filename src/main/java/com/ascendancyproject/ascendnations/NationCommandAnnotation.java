package com.ascendancyproject.ascendnations;

import com.ascendancyproject.ascendnations.nation.NationRole;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface NationCommandAnnotation {
    String name();
    String description();
    String[] aliases();

    boolean requiresNation() default true;
    NationRole minimumRole() default NationRole.Citizen;
}
