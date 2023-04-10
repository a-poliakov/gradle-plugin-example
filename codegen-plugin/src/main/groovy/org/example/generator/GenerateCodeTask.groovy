package org.example.generator

import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.*
import org.gradle.process.JavaExecSpec

@Slf4j
@CacheableTask
class GenerateCodeTask  extends DefaultTask {
    static final String CLASS_NAME = 'org.example.Main'

    @SkipWhenEmpty @InputFile @PathSensitive(PathSensitivity.NAME_ONLY)
    File inputFile

    @OutputDirectory
    File outputDir

    @Optional
    @Input
    List<String> jvmArgs

    @Optional
    @Input
    def configuration

    @Optional
    @Input
    def components

    @Optional @InputFile @PathSensitive(PathSensitivity.NAME_ONLY)
    File configFile

    GenerateCodeTask() {
        outputDir = new File(project.buildDir, 'generate-code')
    }

    @TaskAction
    void exec() {
        def javaExecOptions = execInternal()
        log.warn("org.example.generator.JavaExecOptions: $javaExecOptions")
        project.javaexec { JavaExecSpec c ->
            c.classpath(javaExecOptions.classpath)
            c.mainClass = javaExecOptions.main
            c.args = javaExecOptions.args
            c.systemProperties(javaExecOptions.systemProperties)
            c.jvmArgs(javaExecOptions.jvmArgs ?: [])
        }
    }

    JavaExecOptions execInternal() {
        assert inputFile, "inputFile should be set in the task $name"
        assert outputDir, "outputDir should be set in the task $name"

        outputDir.mkdirs()

        def generateOptions = new GenerateOptions(
                generatorFiles: Helper.configuration(project, configuration).resolve(),
                inputFile: inputFile.path,
                outputDir: outputDir.path,
                configFile: configFile?.path,
                jvmArgs: this.jvmArgs,
                systemProperties: Helper.systemProperties(components),
        )
        log.warn("org.example.generator.GenerateOptions: $generateOptions")

        generate(generateOptions)
    }

    JavaExecOptions generate(GenerateOptions options) {
        def args = []
        args << 'generate'
        args << '-i' << options.inputFile
        args << '-o' << options.outputDir
        if (options.configFile) {
            args << '-c' << options.configFile
        }
        if (options.rawOptions) {
            args.addAll(options.rawOptions)
        }

        def systemProperties = [:]
        if (options.systemProperties) {
            systemProperties.putAll(options.systemProperties)
        }

        new JavaExecOptions(
                classpath: Helper.findJARs(options.generatorFiles),
                args: args,
                main: CLASS_NAME,
                systemProperties: systemProperties,
                jvmArgs: options.jvmArgs,
        )
    }

    protected static class Helper {
        static Configuration configuration(Project project, configuration) {
            switch (configuration) {
                case null:
                    return project.configurations.codegen
                case String:
                    return project.configurations.getByName(configuration)
                case Configuration:
                    return configuration
            }
            throw new IllegalArgumentException("configuration must be String or org.gradle.api.artifacts.Configuration but unknown type: ${configuration}")
        }

        static Map<String, String> systemProperties(components) {
            if (components instanceof Collection) {
                components.collectEntries { k -> [(k as String): ''] }
            } else if (components instanceof Map) {
                components.collectEntries { k, v ->
                    if (v instanceof Collection) {
                        [(k as String): v.join(',')]
                    } else if (v == true) {
                        [(k as String): '']
                    } else if (v == false || v == null) {
                        [(k as String): 'false']
                    } else {
                        [(k as String): v as String]
                    }
                } as Map<String, String>
            } else if (components == null) {
                [:]
            } else {
                throw new IllegalArgumentException("components must be Collection or Map")
            }
        }

        static Set<File> findJARs(Set<File> generatorFiles) {
            generatorFiles.findAll {
                it.name.toLowerCase().endsWith('.jar')
            }
        }
    }
}
