package org.skellig.plugin.language.teststep;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.ExtensionTestUtil;
import com.intellij.testFramework.IndexingTestUtil;
import com.intellij.testFramework.TestApplicationManager;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.skellig.plugin.language.teststep.psi.SkelligTestStepFunctionExpression;
import org.skellig.plugin.language.teststep.psi.SkelligTestStepValue;


import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class SkelligTestStepFunctionImplementationReferenceTest {
    private static final String EXECUTOR = "org.skellig.teststep.processing.value.function.FunctionValueExecutor";
    @AfterAll static void releaseLightProject() throws Exception {
        EdtTestUtil.runInEdtAndWait(() -> com.intellij.openapi.application.WriteIntentReadAction.run(
            com.intellij.testFramework.LightPlatformTestCase::closeAndDeleteProject));
    }

    @Test void resolvesJavaAndKotlinExecutorsByReturnedName() throws Exception {
        withFixture("sourceExecutors", fixture -> {
            fixture.addFile("org/skellig/teststep/processing/value/function/FunctionValueExecutor.java",
                "package org.skellig.teststep.processing.value.function; public interface FunctionValueExecutor { String getFunctionName(); }");
            fixture.addFile("JsonPathFunctionExecutor.java", """
                import org.skellig.teststep.processing.value.function.FunctionValueExecutor;
                public class JsonPathFunctionExecutor implements FunctionValueExecutor {
                    public String getFunctionName() { return "jsonPath"; }
                }
                """);
            fixture.addFile("Conversions.kt", """
                import org.skellig.teststep.processing.value.function.FunctionValueExecutor
                abstract class Conversion : FunctionValueExecutor
                class ToIntExecutor : Conversion() {
                    override fun getFunctionName(): String = "toInt"
                }
                class XPathExecutor : FunctionValueExecutor {
                    override fun getFunctionName(): String { return "xpath" }
                }
                class OtherXPathExecutor : FunctionValueExecutor {
                    override fun getFunctionName(): String = "xpath"
                }
                class DynamicExecutor : FunctionValueExecutor {
                    override fun getFunctionName(): String = "dynamic" + System.currentTimeMillis()
                }
                """);
            fixture.addFile("Unrelated.java", "class Unrelated { String getFunctionName() { return \"ignored\"; } }");
            PsiFile file = fixture.addFile("calls.sts", """
                name("caller") {
                    a = msg.jsonPath("a.b.c")
                    b = msg.toInt()
                    c = msg.xpath("path")
                    d = ignored()
                    e = dynamic()
                    f = missing()
                }
                """);
            fixture.ready();
            ReadAction.run(() -> {
                var calls = PsiTreeUtil.findChildrenOfType(file, SkelligTestStepFunctionExpression.class).stream().toList();
                var javaTarget = assertInstanceOf(com.intellij.psi.PsiClass.class, calls.get(0).getFunctionName().getReference().resolve());
                assertEquals("JsonPathFunctionExecutor", javaTarget.getName());
                var kotlinTarget = assertInstanceOf(org.jetbrains.kotlin.psi.KtClass.class, calls.get(1).getFunctionName().getReference().resolve());
                assertEquals("ToIntExecutor", kotlinTarget.getName());
                var xpath = (PsiPolyVariantReference) calls.get(2).getFunctionName().getReference();
                assertNull(xpath.resolve());
                assertEquals(2, xpath.multiResolve(false).length);
                for (int i = 3; i < calls.size(); i++) assertNull(calls.get(i).getFunctionName().getReference().resolve());
                var name = calls.get(0).getFunctionName();
                var reference = file.findReferenceAt(name.getTextOffset() + 2);
                assertNotNull(reference);
                assertSame(javaTarget, reference.resolve());
                assertEquals("jsonPath", reference.getRangeInElement().substring(reference.getElement().getText()));
            });
        });
    }

    @Test void preservesArgumentReferencesAndInvalidatesSourceCache() throws Exception {
        withFixture("executorEdits", fixture -> {
            fixture.addFile("org/skellig/teststep/processing/value/function/FunctionValueExecutor.java",
                "package org.skellig.teststep.processing.value.function; public interface FunctionValueExecutor { String getFunctionName(); }");
            PsiFile executor = fixture.addFile("GetExecutor.java", """
                import org.skellig.teststep.processing.value.function.FunctionValueExecutor;
                class GetExecutor implements FunctionValueExecutor { public String getFunctionName() { return "get"; } }
                """);
            PsiFile sts = fixture.addFile("calls.sts", "name(\"caller\") { id = stepId\n a = get(stepId) }");
            fixture.ready();
            ReadAction.run(() -> {
                var call = PsiTreeUtil.findChildOfType(sts, SkelligTestStepFunctionExpression.class);
                assertNotNull(call.getFunctionName().getReference().resolve());
                assertInstanceOf(SkelligTestStepValue.class, call.getReference().resolve());
                var argument = call.getArgList().get(0);
                assertInstanceOf(SkelligTestStepValue.class, sts.findReferenceAt(argument.getTextOffset()).resolve());
            });
            fixture.replace(executor, executor.getText().replace("return \"get\"", "return \"renamed\""));
            fixture.ready();
            ReadAction.run(() -> {
                var call = PsiTreeUtil.findChildOfType(sts, SkelligTestStepFunctionExpression.class);
                assertNull(call.getFunctionName().getReference().resolve());
                assertNotNull(call.getReference().resolve());
            });
        });
    }

    @Test void resolvesCompiledDependencyWithoutSources() throws Exception {
        java.nio.file.Path jar = java.nio.file.Files.createTempFile("skellig-executors", ".jar");
        try {
            try (var output = new java.util.jar.JarOutputStream(java.nio.file.Files.newOutputStream(jar))) {
                writeClass(output, EXECUTOR.replace('.', '/'), null, true);
                writeClass(output, "library/JsonPathFunctionExecutor", "jsonPath", false);
            }
            withFixture("compiledExecutor", fixture -> {
                fixture.library(jar);
                PsiFile file = fixture.addFile("calls.sts", "name(\"caller\") { a = msg.jsonPath(\"a.b.c\") }");
                fixture.ready();
                ReadAction.run(() -> {
                    var call = PsiTreeUtil.findChildOfType(file, SkelligTestStepFunctionExpression.class);
                    var target = assertInstanceOf(com.intellij.psi.PsiClass.class, call.getFunctionName().getReference().resolve());
                    assertEquals("library.JsonPathFunctionExecutor", target.getQualifiedName());
                    assertTrue(target.getContainingFile().getVirtualFile().getPath().contains(".jar!/"));
                });
            });
        } finally {
            java.nio.file.Files.deleteIfExists(jar);
        }
    }

    @Test void navigatesToAttachedLibrarySources() throws Exception {
        java.nio.file.Path jar = java.nio.file.Files.createTempFile("skellig-executors", ".jar");
        java.nio.file.Path sources = java.nio.file.Files.createTempFile("skellig-executor-sources", ".jar");
        try {
            try (var output = new java.util.jar.JarOutputStream(java.nio.file.Files.newOutputStream(jar))) {
                writeClass(output, EXECUTOR.replace('.', '/'), null, true);
                writeClass(output, "library/JsonPathFunctionExecutor", "jsonPath", false);
            }
            try (var output = new java.util.jar.JarOutputStream(java.nio.file.Files.newOutputStream(sources))) {
                output.putNextEntry(new java.util.jar.JarEntry("library/JsonPathFunctionExecutor.java"));
                output.write(("package library; import " + EXECUTOR + "; public class JsonPathFunctionExecutor implements FunctionValueExecutor { public String getFunctionName() { return \"jsonPath\"; } }")
                    .getBytes(java.nio.charset.StandardCharsets.UTF_8));
                output.closeEntry();
            }
            withFixture("attachedSources", fixture -> {
                fixture.library(jar, sources);
                PsiFile file = fixture.addFile("calls.sts", "name(\"caller\") { a = msg.jsonPath(\"a.b.c\") }");
                fixture.ready();
                ReadAction.run(() -> {
                    var call = PsiTreeUtil.findChildOfType(file, SkelligTestStepFunctionExpression.class);
                    var target = assertInstanceOf(com.intellij.psi.PsiClass.class, call.getFunctionName().getReference().resolve());
                    assertEquals("library.JsonPathFunctionExecutor", target.getQualifiedName());
                    assertEquals("JsonPathFunctionExecutor.java", target.getContainingFile().getName());
                });
            });
        } finally {
            java.nio.file.Files.deleteIfExists(jar);
            java.nio.file.Files.deleteIfExists(sources);
        }
    }

    private static void writeClass(java.util.jar.JarOutputStream output, String className, String functionName, boolean isInterface) throws java.io.IOException {
        var writer = new org.jetbrains.org.objectweb.asm.ClassWriter(0);
        int flags = org.jetbrains.org.objectweb.asm.Opcodes.ACC_PUBLIC;
        if (isInterface) flags |= org.jetbrains.org.objectweb.asm.Opcodes.ACC_INTERFACE | org.jetbrains.org.objectweb.asm.Opcodes.ACC_ABSTRACT;
        writer.visit(org.jetbrains.org.objectweb.asm.Opcodes.V1_8, flags, className, null, "java/lang/Object",
            isInterface ? null : new String[]{EXECUTOR.replace('.', '/')});
        var getter = writer.visitMethod(org.jetbrains.org.objectweb.asm.Opcodes.ACC_PUBLIC | (isInterface ? org.jetbrains.org.objectweb.asm.Opcodes.ACC_ABSTRACT : 0),
            "getFunctionName", "()Ljava/lang/String;", null, null);
        if (!isInterface) {
            getter.visitCode();
            getter.visitLdcInsn(functionName);
            getter.visitInsn(org.jetbrains.org.objectweb.asm.Opcodes.ARETURN);
            getter.visitMaxs(1, 1);
        }
        getter.visitEnd();
        writer.visitEnd();
        output.putNextEntry(new java.util.jar.JarEntry(className + ".class"));
        output.write(writer.toByteArray());
        output.closeEntry();
    }

    private void withFixture(String name, Consumer<Fixture> test) throws Exception {
        EdtTestUtil.runInEdtAndWait(() -> {
            TestApplicationManager.getInstance();
            Disposable activities = Disposer.newDisposable("function implementation test startup activities");
            // Language navigation does not need project startup integrations from
            // the installed IDE (including unrelated Ultimate integrations).
            ExtensionTestUtil.maskExtensions(ExtensionPointName.create("com.intellij.postStartupActivity"),
                List.of(), activities, false);
            Fixture fixture = new Fixture();
            fixture.setName(name);
            try {
                fixture.setUp();
                try {
                    test.accept(fixture);
                } finally {
                    fixture.tearDown();
                }
            } finally {
                Disposer.dispose(activities);
            }
        });
    }

    private static final class Fixture extends BasePlatformTestCase {
        @Override public void setUp() throws Exception { super.setUp(); }
        @Override public void tearDown() throws Exception {
            try {
                com.intellij.openapi.roots.ModuleRootModificationUtil.updateModel(getModule(), model -> {
                    for (var library : model.getModuleLibraryTable().getLibraries()) model.getModuleLibraryTable().removeLibrary(library);
                });
            } finally {
                super.tearDown();
            }
        }
        PsiFile addFile(String name, String text) { return myFixture.addFileToProject(name, text); }
        com.intellij.openapi.project.Project project() { return getProject(); }
        void replace(PsiFile file, String text) {
            com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction(getProject(), () -> {
                var manager = com.intellij.psi.PsiDocumentManager.getInstance(getProject());
                var document = manager.getDocument(file);
                assertNotNull(document);
                document.setText(text);
                manager.commitDocument(document);
            });
        }
        void library(java.nio.file.Path jar) {
            com.intellij.openapi.roots.ModuleRootModificationUtil.addModuleLibrary(getModule(),
                "jar://" + jar.toAbsolutePath() + "!/");
        }
        void library(java.nio.file.Path jar, java.nio.file.Path sources) {
            com.intellij.openapi.roots.ModuleRootModificationUtil.updateModel(getModule(), model -> {
                var library = model.getModuleLibraryTable().createLibrary("skellig-test-step-processing");
                var roots = library.getModifiableModel();
                roots.addRoot("jar://" + jar.toAbsolutePath() + "!/", com.intellij.openapi.roots.OrderRootType.CLASSES);
                roots.addRoot("jar://" + sources.toAbsolutePath() + "!/", com.intellij.openapi.roots.OrderRootType.SOURCES);
                roots.commit();
            });
        }
        void ready() { IndexingTestUtil.waitUntilIndexesAreReady(getProject()); }
    }
}
