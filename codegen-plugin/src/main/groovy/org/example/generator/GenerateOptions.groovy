package org.example.generator

import groovy.transform.Canonical

@Canonical
class GenerateOptions {
    Set<File> generatorFiles
    String inputFile
    String outputDir
    String configFile
    List<String> rawOptions
    List<String> jvmArgs
    Map<String, String> systemProperties
}
