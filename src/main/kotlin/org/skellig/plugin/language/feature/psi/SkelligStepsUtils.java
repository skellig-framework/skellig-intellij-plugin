package org.skellig.plugin.language.feature.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

public class SkelligStepsUtils {

    public static String getName(SkelligFeatureStep element) {
        ASTNode keyNode = element.getNode().findChildByType(SkelligFeatureTypes.STEP);
        if (keyNode != null) {
            return keyNode.getText();
        } else {
            return "";
        }
    }

    public static PsiElement getNameIdentifier(SkelligFeatureStep element) {
        ASTNode keyNode = element.getNode().findChildByType(SkelligFeatureTypes.STEP);
        if (keyNode != null) {
            return keyNode.getPsi();
        } else {
            return null;
        }
    }
}
