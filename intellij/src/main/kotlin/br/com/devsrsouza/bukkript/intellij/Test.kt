package br.com.devsrsouza.bukkript.intellij

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewNodeDecorator
import com.intellij.packageDependencies.ui.PackageDependenciesNode
import com.intellij.ui.ColoredTreeCellRenderer

class Test : ProjectViewNodeDecorator {
    override fun decorate(node: ProjectViewNode<*>?, data: PresentationData?) {
        if(node?.name?.endsWith(".bk.kts") == true) {
            node.icon = BukkriptFileType.INSTANCE.icon
            node.validate()
            //node.update()
        }
    }

    override fun decorate(node: PackageDependenciesNode?, cellRenderer: ColoredTreeCellRenderer?) {
        //println("node2: $node")
    }
}