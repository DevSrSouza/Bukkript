package br.com.devsrsouza.bukkript.libraryresolver;

import io.papermc.paper.plugin.loader.library.ClassPathLibrary;
import io.papermc.paper.plugin.loader.library.LibraryLoadingException;
import io.papermc.paper.plugin.loader.library.LibraryStore;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class BukkriptClassPathLibrary implements ClassPathLibrary  {
    private final BukkriptMavenLibraryResolver resolver;

    public BukkriptClassPathLibrary(BukkriptMavenLibraryResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void register(@NotNull LibraryStore store) throws LibraryLoadingException {
        List<File> libraries = resolver.download();
        for (File library : libraries) {
            store.addLibrary(library.toPath());
        }
    }
}
