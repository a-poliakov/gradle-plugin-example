package org.example.generator

import groovy.transform.Canonical

@Canonical
class JavaExecOptions {
    Set<File> classpath
    String main
    List<String> args
    List<String> jvmArgs
    Map<String, String> systemProperties
}
