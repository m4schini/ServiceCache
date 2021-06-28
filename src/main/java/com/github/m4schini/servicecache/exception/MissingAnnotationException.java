package com.github.m4schini.servicecache.exception;

import java.lang.reflect.Method;

/**
 * Should be thrown when a method is trying to access a annotation at runtime and the annotation doesn't exist
 */
public class MissingAnnotationException extends RuntimeException {
    public MissingAnnotationException(String annotationName, Method method) {
        super("The method " + method.getName() + " requires the @" + annotationName + " Annotation");
    }
}
