package net.pwall.json.schema.codegen

import kotlin.test.Test
import kotlin.test.expect
import java.io.File
import java.io.StringWriter
import net.pwall.json.JSON
import net.pwall.json.pointer.JSONPointer
import net.pwall.json.schema.codegen.CodeGeneratorTestUtil.OutputDetails
import net.pwall.json.schema.codegen.CodeGeneratorTestUtil.createHeader
import net.pwall.json.schema.codegen.CodeGeneratorTestUtil.dirs
import net.pwall.json.schema.codegen.CodeGeneratorTestUtil.outputCapture

class CodeGeneratorOneOf2Test {

    @Test fun `should generate classes for complex multiple oneOf schemata`() {
        val input = File("src/test/resources/test-oneof-2.schema.json")
        val codeGenerator = CodeGenerator()
        val stringWriterA = StringWriter()
        val outputDetailsA = OutputDetails(TargetFileName("TypeA", "kt", dirs), stringWriterA)
        val stringWriterB = StringWriter()
        val outputDetailsB = OutputDetails(TargetFileName("TypeB", "kt", dirs), stringWriterB)
        val stringWriterC = StringWriter()
        val outputDetailsC = OutputDetails(TargetFileName("TypeC", "kt", dirs), stringWriterC)
        codeGenerator.basePackageName = "com.example"
        codeGenerator.outputResolver = outputCapture(outputDetailsA, outputDetailsB, outputDetailsC)
        codeGenerator.generateAll(JSON.parse(input), JSONPointer("/\$defs"))
        expect(createHeader("TypeA.kt") + expectedTypeA) { stringWriterA.toString() }
        expect(createHeader("TypeB.kt") + expectedTypeB) { stringWriterB.toString() }
        expect(createHeader("TypeC.kt") + expectedTypeC) { stringWriterC.toString() }
    }

    @Test fun `should generate classes for complex multiple oneOf schemata in Java`() {
        val input = File("src/test/resources/test-oneof-2.schema.json")
        val codeGenerator = CodeGenerator(TargetLanguage.JAVA)
        val stringWriterA = StringWriter()
        val outputDetailsA = OutputDetails(TargetFileName("TypeA", "java", dirs), stringWriterA)
        val stringWriterB = StringWriter()
        val outputDetailsB = OutputDetails(TargetFileName("TypeB", "java", dirs), stringWriterB)
        val stringWriterC = StringWriter()
        val outputDetailsC = OutputDetails(TargetFileName("TypeC", "java", dirs), stringWriterC)
        codeGenerator.basePackageName = "com.example"
        codeGenerator.outputResolver = outputCapture(outputDetailsA, outputDetailsB, outputDetailsC)
        codeGenerator.generateAll(JSON.parse(input), JSONPointer("/\$defs"))
        expect(createHeader("TypeA.java") + expectedTypeAJava) { stringWriterA.toString() }
        expect(createHeader("TypeB.java") + expectedTypeBJava) { stringWriterB.toString() }
        expect(createHeader("TypeC.java") + expectedTypeCJava) { stringWriterC.toString() }
    }

    companion object {

        const val expectedTypeA =
"""package com.example

open class TypeA(
    val aaa: Long? = null
) {

    class A(
        aaa: Long? = null,
        val xxx: String? = null
    ) : TypeA(aaa) {

        override fun equals(other: Any?): Boolean = this === other || other is A && super.equals(other) &&
                xxx == other.xxx

        override fun hashCode(): Int = super.hashCode() xor
                xxx.hashCode()

    }

    class B(
        aaa: Long? = null,
        val yyy: String? = null
    ) : TypeA(aaa) {

        override fun equals(other: Any?): Boolean = this === other || other is B && super.equals(other) &&
                yyy == other.yyy

        override fun hashCode(): Int = super.hashCode() xor
                yyy.hashCode()

    }

    class C(
        aaa: Long? = null,
        val zzz: String? = null
    ) : TypeA(aaa) {

        override fun equals(other: Any?): Boolean = this === other || other is C && super.equals(other) &&
                zzz == other.zzz

        override fun hashCode(): Int = super.hashCode() xor
                zzz.hashCode()

    }

    class D(
        aaa: Long? = null,
        val qqq: String? = null
    ) : TypeA(aaa) {

        init {
            if (qqq != null)
                require(qqq.isNotEmpty()) { "qqq length < minimum 1 - ${'$'}{qqq.length}" }
        }

        override fun equals(other: Any?): Boolean = this === other || other is D && super.equals(other) &&
                qqq == other.qqq

        override fun hashCode(): Int = super.hashCode() xor
                qqq.hashCode()

    }

}
"""

        const val expectedTypeB =
"""package com.example

data class TypeB(
    val xxx: String
)
"""

        const val expectedTypeC =
"""package com.example

data class TypeC(
    val yyy: String
)
"""

        const val expectedTypeAJava =
"""package com.example;

public class TypeA {

    private final long aaa;

    public TypeA(
            long aaa
    ) {
        this.aaa = aaa;
    }

    public long getAaa() {
        return aaa;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof TypeA))
            return false;
        TypeA typedOther = (TypeA)other;
        return aaa == typedOther.aaa;
    }

    @Override
    public int hashCode() {
        return (int)aaa;
    }

    public static class A extends TypeA {

        private final String xxx;

        public A(
                long aaa,
                String xxx
        ) {
            super(aaa);
            this.xxx = xxx;
        }

        public String getXxx() {
            return xxx;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other)
                return true;
            if (!(other instanceof A))
                return false;
            if (!super.equals(other))
                return false;
            A typedOther = (A)other;
            return xxx == null ? typedOther.xxx == null : xxx.equals(typedOther.xxx);
        }

        @Override
        public int hashCode() {
            int hash = super.hashCode();
            return hash ^ (xxx != null ? xxx.hashCode() : 0);
        }

    }

    public static class B extends TypeA {

        private final String yyy;

        public B(
                long aaa,
                String yyy
        ) {
            super(aaa);
            this.yyy = yyy;
        }

        public String getYyy() {
            return yyy;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other)
                return true;
            if (!(other instanceof B))
                return false;
            if (!super.equals(other))
                return false;
            B typedOther = (B)other;
            return yyy == null ? typedOther.yyy == null : yyy.equals(typedOther.yyy);
        }

        @Override
        public int hashCode() {
            int hash = super.hashCode();
            return hash ^ (yyy != null ? yyy.hashCode() : 0);
        }

    }

    public static class C extends TypeA {

        private final String zzz;

        public C(
                long aaa,
                String zzz
        ) {
            super(aaa);
            this.zzz = zzz;
        }

        public String getZzz() {
            return zzz;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other)
                return true;
            if (!(other instanceof C))
                return false;
            if (!super.equals(other))
                return false;
            C typedOther = (C)other;
            return zzz == null ? typedOther.zzz == null : zzz.equals(typedOther.zzz);
        }

        @Override
        public int hashCode() {
            int hash = super.hashCode();
            return hash ^ (zzz != null ? zzz.hashCode() : 0);
        }

    }

    public static class D extends TypeA {

        private final String qqq;

        public D(
                long aaa,
                String qqq
        ) {
            super(aaa);
            if (qqq != null && qqq.length() < 1)
                throw new IllegalArgumentException("qqq length < minimum 1 - " + qqq.length());
            this.qqq = qqq;
        }

        public String getQqq() {
            return qqq;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other)
                return true;
            if (!(other instanceof D))
                return false;
            if (!super.equals(other))
                return false;
            D typedOther = (D)other;
            return qqq == null ? typedOther.qqq == null : qqq.equals(typedOther.qqq);
        }

        @Override
        public int hashCode() {
            int hash = super.hashCode();
            return hash ^ (qqq != null ? qqq.hashCode() : 0);
        }

    }

}
"""

        const val expectedTypeBJava =
"""package com.example;

public class TypeB {

    private final String xxx;

    public TypeB(
            String xxx
    ) {
        if (xxx == null)
            throw new IllegalArgumentException("Must not be null - xxx");
        this.xxx = xxx;
    }

    public String getXxx() {
        return xxx;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof TypeB))
            return false;
        TypeB typedOther = (TypeB)other;
        return xxx.equals(typedOther.xxx);
    }

    @Override
    public int hashCode() {
        return xxx.hashCode();
    }

}
"""

        const val expectedTypeCJava =
"""package com.example;

public class TypeC {

    private final String yyy;

    public TypeC(
            String yyy
    ) {
        if (yyy == null)
            throw new IllegalArgumentException("Must not be null - yyy");
        this.yyy = yyy;
    }

    public String getYyy() {
        return yyy;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof TypeC))
            return false;
        TypeC typedOther = (TypeC)other;
        return yyy.equals(typedOther.yyy);
    }

    @Override
    public int hashCode() {
        return yyy.hashCode();
    }

}
"""

    }

}