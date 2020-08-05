package br.com.devsrsouza.bukkript.intellij

import com.intellij.ide.IconProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import javax.swing.Icon

class BukkriptIconProvider : IconProvider(), DumbAware {
    override fun getIcon(element: PsiElement, flags: Int): Icon? {
        if(element is PsiFile && element.name.endsWith(".bk.kts")) {
            return BukkriptFileType.INSTANCE.icon
        } else {
            return null
        }
    }
}