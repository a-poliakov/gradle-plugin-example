package org.example.generator

class Spec {
    def classes = []

    def model(arge) {
        def clazz = new Clazz().named(args.clazz)
        clazz.attributes(args.message)
        classes << clazz
        this
    }

    def generate() {
        def code = ""
        classes.each {Clazz clazz ->
            code << "class ${clazz.name} {\n"
            clazz.attributes.each { attr ->
                code  << "\tdef message = ${attr}\n"
            }
            code << "}"
            code << "\n"
        }
        code
    }

    class Clazz {
        def name
        def attributes = []

        def named(name) {
            this.name = name
            this
        }

        def attributes(attributes) {
            this.attributes  = attributes
        }
    }
}

def spec = new Spec()
def code = spec.model(clazz : "GeneratedClass",message: "Hello World!" ).generate()
println code


