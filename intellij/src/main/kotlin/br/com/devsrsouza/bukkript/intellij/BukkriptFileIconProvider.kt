package br.com.devsrsouza.bukkript.intellij

import com.intellij.ide.FileIconProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

class BukkriptFileIconProvider : FileIconProvider {
    override fun getIcon(file: VirtualFile, flags: Int, project: Project?): Icon? {
        return if(file.name.isBukkriptScript())
            BukkriptFileType.INSTANCE.icon
        else null
    }

}