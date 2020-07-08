package br.com.devsrsouza.bukkript.intellij

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.fileTypes.ex.FileTypeIdentifiableByVirtualFile
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.idea.KotlinLanguage
import javax.swing.Icon

class BukkriptFileType : LanguageFileType(KotlinLanguage.INSTANCE), FileTypeIdentifiableByVirtualFile {

    companion object {
        @JvmField
        val INSTANCE = BukkriptFileType()
    }

    override fun getIcon(): Icon? = BukkriptIcons.Bukkript

    override fun getName(): String = "Bukkript File"

    override fun getDefaultExtension(): String = "bk.kts"

    override fun getDescription(): String = "Bukkript Script File"

    override fun isMyFileType(file: VirtualFile): Boolean = file.name.isBukkriptScript()
}