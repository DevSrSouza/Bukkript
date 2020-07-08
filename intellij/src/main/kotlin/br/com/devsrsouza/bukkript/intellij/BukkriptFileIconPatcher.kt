package br.com.devsrsouza.bukkript.intellij

import com.intellij.ide.FileIconPatcher
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

class BukkriptFileIconPatcher : FileIconPatcher {
    override fun patchIcon(baseIcon: Icon, file: VirtualFile, flags: Int, project: Project?): Icon {
        return if(file.name.isBukkriptScript())
            BukkriptFileType.INSTANCE.icon!!
        else baseIcon
    }
}