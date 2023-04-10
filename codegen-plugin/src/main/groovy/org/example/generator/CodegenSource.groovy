package org.example.generator

import groovy.transform.ToString

@ToString(includes = 'name', includePackage = false)
class CodegenSource {
    final String name

    CodegenSource(String name) {
        this.name = name
    }

    GenerateCodeTask code

    GenerateCodeTask code(@DelegatesTo(GenerateCodeTask) Closure closure) {
        code.configure(closure)
        code
    }

    void setInputFile(File inputFile) {
        [code]*.inputFile = inputFile
    }
}
