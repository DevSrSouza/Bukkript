package br.com.devsrsouza.bukkript.libraryresolver;

import com.google.common.collect.Lists;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.filter.ExclusionsDependencyFilter;
import org.eclipse.aether.util.repository.SimpleArtifactDescriptorPolicy;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BukkriptMavenLibraryResolver {

    private static final Logger logger = Logger.getLogger("BukkriptMavenLibraryResolver");

    // This dependencies is shaded or already loaded before the plugin initialize.
    private static final List<String> excludedDependencies = Lists.newArrayList(
            "br.com.devsrsouza.kotlinbukkitapi:architecture"
    );
    private static final String kotlinStd = "org.jetbrains.kotlin:kotlin-stdlib";


    private final RepositorySystem repository;
    private final DefaultRepositorySystemSession session;
    private final List<RemoteRepository> repositories = new ArrayList<>();
    private final List<Dependency> dependencies = new ArrayList<>();
    private boolean shouldExcludeKotlin = false;

    /**
     * Creates a new maven library resolver instance.
     * <p>
     * The created instance will use the servers {@code libraries} folder to cache fetched libraries in.
     * Notably, the resolver is created without any repository, not even maven central.
     * It is hence crucial that plugins which aim to use this api register all required repositories before
     * submitting the {@link io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver} to the {@link io.papermc.paper.plugin.loader.PluginClasspathBuilder}.
     */
    public BukkriptMavenLibraryResolver() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

        this.repository = locator.getService(RepositorySystem.class);
        this.session = MavenRepositorySystemUtils.newSession();

        this.session.setArtifactDescriptorPolicy(new SimpleArtifactDescriptorPolicy(true, true)); // this is also required for Github repo type.
        this.session.setChecksumPolicy(RepositoryPolicy.CHECKSUM_POLICY_IGNORE); // Ignore instead of FAIL/WARN
        this.session.setLocalRepositoryManager(this.repository.newLocalRepositoryManager(this.session, new LocalRepository("libraries")));
        this.session.setTransferListener(new AbstractTransferListener() {
            @Override
            public void transferInitiated(@NotNull TransferEvent event) throws TransferCancelledException {
                logger.log(Level.INFO, "Downloading {0}", event.getResource().getRepositoryUrl() + event.getResource().getResourceName());
            }
        });
        this.session.setReadOnly();

        // SETUP DEPENDENCIES FROM FILE
        LibrariesReader.PluginLibraries libraries = LibrariesReader.load(BukkriptMavenLibraryResolver.class);
        dependencies.addAll(libraries.asDependencies().collect(Collectors.toList()));
        repositories.addAll(libraries.asRepositories().collect(Collectors.toList()));
    }

    public void addDependency(@NotNull Dependency dependency) {
        this.dependencies.add(dependency);
    }

    public void addRepository(@NotNull RemoteRepository remoteRepository) {
        this.repositories.add(remoteRepository);
    }

    public List<File> download() {
        List<RemoteRepository> repos = this.repository.newResolutionRepositories(this.session, this.repositories);

        DependencyResult result;
        try {
            ArrayList<String> exclusion = Lists.newArrayList(excludedDependencies);
            if(shouldExcludeKotlin) exclusion.add(kotlinStd);

            result = this.repository.resolveDependencies(
                    this.session,
                    new DependencyRequest(
                            new CollectRequest((Dependency) null, this.dependencies, repos),
                            new ExclusionsDependencyFilter(exclusion)
                    )
            );
        } catch (DependencyResolutionException ex) {
            throw new LibraryLoadingException("Error resolving libraries", ex);
        }

        return result.getArtifactResults()
                .stream()
                .map(artifact -> artifact.getArtifact().getFile())
                .collect(Collectors.toList());
    }

    public BukkriptMavenLibraryResolver shouldExcludeKotlin(boolean shouldExcludeKotlin) {
        this.shouldExcludeKotlin = shouldExcludeKotlin;
        return this;
    }
}

