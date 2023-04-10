package org.example.generator

import org.gradle.api.Plugin
import org.gradle.api.Project

class CodeGeneratorPlugin  implements Plugin<Project> {
    @Override
    void apply(Project project) {
        def codegenSources = project.container(CodegenSource)
        project.extensions.add('codegenSources', codegenSources)

        project.configurations.create('codegen')

        project.ext.GenerateSwaggerCode = GenerateCodeTask

        createGenerateCodeTask(project)

        codegenSources.all {
            def codegenSource = delegate as CodegenSource
            codegenSource.code = createGenerateCodeTask(project, codegenSource.name)

            project.tasks.generateCode.dependsOn(codegenSource.code)

            codegenSource.code.outputDir = new File(project.buildDir, "generate-code-${codegenSource.name}")
        }

        project.afterEvaluate {
            project.tasks.withType(GenerateCodeTask) { task ->
                // todo do something
            }
        }
    }

    private static createGenerateCodeTask(Project project, String sourceName = null) {
        project.task("generateCode${sourceName ? sourceName.capitalize() : ''}",
                type: GenerateCodeTask,
                group: 'build',
                description: "Generates a source code from ${sourceName ?: 'demo specification'}.") as GenerateCodeTask
    }
}
