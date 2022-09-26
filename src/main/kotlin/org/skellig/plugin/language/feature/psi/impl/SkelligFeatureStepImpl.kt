package org.skellig.plugin.language.feature.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiCheckedRenameElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.TokenType
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import org.jetbrains.annotations.NonNls
import org.skellig.plugin.language.feature.psi.*
import org.skellig.plugin.language.feature.steps.AbstractStepDefinition
import java.util.regex.Pattern

class SkelligFeatureStepImpl(node: ASTNode) : SkelligPsiElementBase(node), SkelligFeatureStep, PsiCheckedRenameElement {
    private val LOCK = Any()
    private var mySubstitutions: List<String>? = null
    override fun toString(): String {
        return "SkelligStep:$name"
    }

    override val keyword: ASTNode?
        get() = node.findChildByType(SkelligTokenTypes.Companion.STEP_KEYWORD)

    override val elementText: String
        get() {
            val node = node
            val children = node.getChildren(TEXT_FILTER)
            return StringUtil.join(children, { astNode: ASTNode -> astNode.text }, "").trim { it <= ' ' }
        }

    override val pystring: SkelligPystring?
        get() = PsiTreeUtil.findChildOfType(this, SkelligPystring::class.java)

    override val table: SkelligTable?
        get() {
            val tableNode: ASTNode? = node.findChildByType(SkelligElementTypes.Companion.TABLE)
            return if (tableNode == null) null else tableNode.psi as SkelligTable
        }

    override fun getPresentableText(): String? {
        val keywordNode = keyword
        val prefix = if (keywordNode != null) keywordNode.text + ": " else "Step: "
        return prefix + name
    }

    override fun getReferences(): Array<PsiReference> {
        return CachedValuesManager.getCachedValue(this) {
            CachedValueProvider.Result.create(
                referencesInner, this
            )
        }
    }

    private val referencesInner: Array<PsiReference>
        private get() = ReferenceProvidersRegistry.getReferencesFromProviders(this)

    override fun acceptSkelligElement(skelligElementVisitor: SkelligElementVisitor) {
        skelligElementVisitor.visitStep(this)
    }

    // step name
    override val paramsSubstitutions:

    // pystring

    // table
            List<String>
        get() {
            synchronized(LOCK) {
                if (mySubstitutions == null) {
                    val substitutions = ArrayList<String>()


                    // step name
                    val text = name
                    if (StringUtil.isEmpty(text)) {
                        return emptyList()
                    }
                    addSubstitutionFromText(text, substitutions)

                    // pystring
                    val pystring = pystring
                    val pystringText = pystring?.text
                    if (!StringUtil.isEmpty(pystringText)) {
                        addSubstitutionFromText(pystringText, substitutions)
                    }

                    // table
                    val table = table
                    val tableText = table?.text
                    if (tableText != null) {
                        addSubstitutionFromText(tableText, substitutions)
                    }
                    mySubstitutions = if (substitutions.isEmpty()) emptyList() else substitutions
                }
                return mySubstitutions!!
            }
        }

    override fun subtreeChanged() {
        super.subtreeChanged()
        clearCaches()
    }

    override val stepHolder: SkelligStepsHolder?
        get() {
            val parent = parent
            return if (parent != null) parent as SkelligStepsHolder else null
        }

    private fun clearCaches() {
        synchronized(LOCK) { mySubstitutions = null }
    }

    override val substitutedName: String?
        get() {
            val holder = stepHolder as? SkelligScenarioOutline ?: return name
            return org.skellig.plugin.language.feature.SkelligUtil.substituteTableReferences(name, holder.getOutlineTableMap()).substitution
        }

    @Throws(IncorrectOperationException::class)
    override fun setName(@NonNls name: String): PsiElement {
        val newStep: SkelligFeatureStep? = null //SkelligChangeUtil.createStep(getKeyword().getText() + " " + name, getProject());
        //        replace(newStep);
        return newStep!!
    }

    override fun getName(): String {
        return elementText
    }

    override fun findDefinitions(): Collection<AbstractStepDefinition> {
        //        for (final PsiReference reference : getReferences()) {
//            if (reference instanceof CucumberStepReference) {
//                result.addAll(((CucumberStepReference) reference).resolveToDefinitions());
//            }
//        }
        return ArrayList()
    }

    override fun isRenameAllowed(newName: String?): Boolean {
        val definitions = findDefinitions()
        if (definitions.isEmpty()) {
            return false // No sense to rename step with out of definitions
        }
        for (definition in definitions) {
            if (!definition.supportsRename(newName)) {
                return false //At least one definition does not support renaming
            }
        }
        return true // Nothing prevents us from renaming
    }

    override fun checkSetName(name: String) {
//        if (!isRenameAllowed(name)) {
//            throw new IncorrectOperationException(CucumberBundle.message("cucumber.refactor.rename.bad_symbols"));
//        }
    }

    companion object {
        private val TEXT_FILTER = TokenSet
            .create(
                SkelligTokenTypes.Companion.TEXT,
                SkelligElementTypes.Companion.STEP_PARAMETER,
                TokenType.WHITE_SPACE,
                SkelligTokenTypes.Companion.STEP_PARAMETER_TEXT,
                SkelligTokenTypes.Companion.STEP_PARAMETER_BRACE
            )
        private val PARAMETER_SUBSTITUTION_PATTERN = Pattern.compile("<([^>\n\r]+)>")
        private fun addSubstitutionFromText(text: String?, substitutions: ArrayList<String>) {
            val matcher = PARAMETER_SUBSTITUTION_PATTERN.matcher(text)
            var result = matcher.find()
            if (!result) {
                return
            }
            do {
                val substitution = matcher.group(1)
                if (!StringUtil.isEmpty(substitution) && !substitutions.contains(substitution)) {
                    substitutions.add(substitution)
                }
                result = matcher.find()
            } while (result)
        }
    }
}