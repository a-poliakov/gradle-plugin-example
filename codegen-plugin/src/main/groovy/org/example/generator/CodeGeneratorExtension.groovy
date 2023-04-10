package org.example.generator

import org.gradle.api.NamedDomainObjectContainer

class CodeGeneratorExtension {
    CodeGeneratorExtension(NamedDomainObjectContainer<CodegenSource> code) {
        this.code = code
    }

    final NamedDomainObjectContainer<CodegenSource> code

    NamedDomainObjectContainer<CodegenSource> code(@DelegatesTo(CodegenSource) Closure configuration) {
        code.configure(configuration)
    }
}
