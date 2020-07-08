package br.com.devsrsouza.bukkript.intellij

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.KotlinFileType

class BukkriptCreateNewFileAction : CreateFileFromTemplateAction(
    "Bukkript Script",
    "Bukkript Script File for Bukkit",
    BukkriptFileType.INSTANCE.icon
) {
    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?): String {
        return "Create Bukkript Script"
    }

    override fun createFile(name: String, templateName: String, dir: PsiDirectory): PsiFile? {
        return super.createFile("$name.bk", templateName, dir)
    }

    override fun buildDialog(
        project: Project,
        directory: PsiDirectory,
        builder: CreateFileFromTemplateDialog.Builder
    ) {
        builder.setTitle("Bukkript Script")
            .addKind(
                "Bukkript File",
                BukkriptFileType.INSTANCE.icon,
                "Bukkript Script"
            )

    }
}