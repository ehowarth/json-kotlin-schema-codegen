/*
 * @(#) CodeGeneratorSwaggerTest.kt
 *
 * json-kotlin-schema-codegen  JSON Schema Code Generation
 * Copyright (c) 2020 Peter Wall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.pwall.json.schema.codegen

import kotlin.test.Test
import kotlin.test.expect

import java.io.File
import java.io.StringWriter

import net.pwall.json.pointer.JSONPointer
import net.pwall.json.schema.codegen.CodeGeneratorTestUtil.OutputDetails
import net.pwall.json.schema.codegen.CodeGeneratorTestUtil.createHeader
import net.pwall.json.schema.codegen.CodeGeneratorTestUtil.outputCapture
import net.pwall.yaml.YAMLSimple

class CodeGeneratorSwaggerTest {

    @Test fun `should generate classes from Swagger file`() {
        val input = File("src/test/resources/test-swagger.yaml")
        val swaggerDoc = YAMLSimple.process(input)
        val codeGenerator = CodeGenerator()
        codeGenerator.baseDirectoryName = "dummy"
        val stringWriter1 = StringWriter()
        val outputDetails1 = OutputDetails("dummy", emptyList(), "QueryResponse", "kt", stringWriter1)
        val stringWriter2 = StringWriter()
        val outputDetails2 = OutputDetails("dummy", emptyList(), "Person", "kt", stringWriter2)
        codeGenerator.outputResolver = outputCapture(outputDetails1, outputDetails2)
        codeGenerator.basePackageName = "com.example"
        codeGenerator.generateAll(swaggerDoc.rootNode, JSONPointer("/definitions"))
        expect(createHeader("QueryResponse") + expectedExample1) { stringWriter1.toString() }
        expect(createHeader("Person") + expectedExample2) { stringWriter2.toString() }
    }

    @Test fun `should generate classes from Swagger file applying filter`() {
        val input = File("src/test/resources/test-swagger.yaml")
        val swaggerDoc = YAMLSimple.process(input)
        val codeGenerator = CodeGenerator()
        codeGenerator.baseDirectoryName = "dummy"
        val stringWriter1 = StringWriter()
        val outputDetails1 = OutputDetails("dummy", emptyList(), "QueryResponse", "kt", stringWriter1)
        val stringWriter2 = StringWriter()
        val outputDetails2 = OutputDetails("dummy", emptyList(), "Person", "kt", stringWriter2)
        codeGenerator.outputResolver = outputCapture(outputDetails1, outputDetails2)
        codeGenerator.basePackageName = "com.example"
        codeGenerator.generateAll(swaggerDoc.rootNode, JSONPointer("/definitions")) { it == "Person" }
        expect("") { stringWriter1.toString() }
        expect(createHeader("Person") + expectedExample2) { stringWriter2.toString() }
    }

    @Test fun `should generate classes from Swagger file in Java`() {
        val input = File("src/test/resources/test-swagger.yaml")
        val swaggerDoc = YAMLSimple.process(input)
        val codeGenerator = CodeGenerator(templates = "java", suffix = "java")
        codeGenerator.baseDirectoryName = "dummy"
        val stringWriter1 = StringWriter()
        val outputDetails1 = OutputDetails("dummy", emptyList(), "QueryResponse", "java", stringWriter1)
        val stringWriter2 = StringWriter()
        val outputDetails2 = OutputDetails("dummy", emptyList(), "Person", "java", stringWriter2)
        codeGenerator.outputResolver = outputCapture(outputDetails1, outputDetails2)
        codeGenerator.basePackageName = "com.example"
        codeGenerator.generateAll(swaggerDoc.rootNode, JSONPointer("/definitions"))
        expect(createHeader("QueryResponse") + expectedExample1Java) { stringWriter1.toString() }
        expect(createHeader("Person") + expectedExample2Java) { stringWriter2.toString() }
    }

    companion object {

        const val expectedExample1 =
"""package com.example

data class QueryResponse(
        val data: Person,
        val message: String? = null
)
"""

        const val expectedExample2 =
"""package com.example

data class Person(
        val id: Int,
        val name: String
) {

    init {
        require(id >= 1) { "id < minimum 1 - ${'$'}id" }
        require(id <= 9999) { "id > maximum 9999 - ${'$'}id" }
        require(name.isNotEmpty()) { "name length < minimum 1 - ${'$'}{name.length}" }
    }

}
"""

        const val expectedExample1Java =
"""package com.example;

public class QueryResponse {

    private final Person data;
    private final String message;

    public QueryResponse(
            Person data,
            String message
    ) {
        if (data == null)
            throw new IllegalArgumentException("Must not be null - data");
        this.data = data;
        this.message = message;
    }

    public Person getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof QueryResponse))
            return false;
        QueryResponse typedOther = (QueryResponse)other;
        if (!data.equals(typedOther.data))
            return false;
        return message == null ? typedOther.message == null : message.equals(typedOther.message);
    }

    @Override
    public int hashCode() {
        int hash = data.hashCode();
        hash ^= message != null ? message.hashCode() : 0;
        return hash;
    }

}
"""

        const val expectedExample2Java =
"""package com.example;

public class Person {

    private final int id;
    private final String name;

    public Person(
            int id,
            String name
    ) {
        if (id < 1)
            throw new IllegalArgumentException("id < minimum 1 - " + id);
        if (id > 9999)
            throw new IllegalArgumentException("id > maximum 9999 - " + id);
        this.id = id;
        if (name == null)
            throw new IllegalArgumentException("Must not be null - name");
        if (name.length() < 1)
            throw new IllegalArgumentException("name length < minimum 1 - " + name.length());
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof Person))
            return false;
        Person typedOther = (Person)other;
        if (id != typedOther.id)
            return false;
        return name.equals(typedOther.name);
    }

    @Override
    public int hashCode() {
        int hash = id;
        hash ^= name.hashCode();
        return hash;
    }

}
"""

    }

}
