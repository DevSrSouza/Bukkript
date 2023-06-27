package br.com.devsrsouza.bukkript.libraryresolver;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PluginLibrariesLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        // TODO: maybe in the future instead of guessing, maybe read spigot plugin.yml and load it here.
        // Applies the Kotlin Stdlib

        BukkriptMavenLibraryResolver resolver = new BukkriptMavenLibraryResolver();

        Optional<String> kotlinVersionOptional = LibrariesReader.load(getClass()).asDependencies()
                .filter(d -> d.getArtifact().getGroupId().equals("org.jetbrains.kotlin"))
                .findAny()
                .map(d -> d.getArtifact().getVersion());

        if(kotlinVersionOptional.isEmpty()) {
            classpathBuilder.getContext().getLogger().info("[Bukkript Paper Loader] Was not able to find any kotlin version on paper-libraries, fallback to 1.8.22. Contact project Author!");
        }

        String kotlinVersion = kotlinVersionOptional.orElse("1.8.22");

        resolver.addDependency(new Dependency(
                new DefaultArtifact("org.jetbrains.kotlin:kotlin-stdlib:" + kotlinVersion), null
        ));

        classpathBuilder.addLibrary(new BukkriptClassPathLibrary(resolver));
    }


}