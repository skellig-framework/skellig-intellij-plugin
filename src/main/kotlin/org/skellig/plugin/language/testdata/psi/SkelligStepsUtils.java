package org.skellig.plugin.language.testdata.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class SkelligStepsUtils {

    @NotNull
    public static String getName(SkelligTestDataTestDefinition element) {
        ASTNode keyNode = element.getNode().findChildByType(SkelligTestDataTypes.TEST_DEFINITION);
        if (keyNode != null) {
            return keyNode.getText().replaceAll("\\\\ ", " ");
        } else {
            return "";
        }
    }

    public static PsiElement getNameIdentifier(SkelligTestDataTestDefinition element) {
        ASTNode keyNode = element.getNode().findChildByType(SkelligTestDataTypes.TEST_DEFINITION);
        if (keyNode != null) {
            return keyNode.getPsi();
        } else {
            return null;
        }
    }
}
