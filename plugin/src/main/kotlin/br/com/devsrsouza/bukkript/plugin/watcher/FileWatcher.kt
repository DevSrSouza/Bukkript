package br.com.devsrsouza.bukkript.plugin.watcher

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.*
import java.nio.file.WatchService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

sealed class FileEvent(val file: File) {
    class Create(file: File) : FileEvent(file)
    class Modify(file: File) : FileEvent(file)
    class Delete(file: File) : FileEvent(file)
}

fun watchFolder(path: Path): Flow<FileEvent> {
    return callbackFlow<FileEvent> {
        val watchThread = object : Thread() {

            private val running = AtomicBoolean(true)
            private lateinit var watchService: WatchService

            fun unregisterWatcher() {
                running.set(false)

                runCatching {
                    watchService.close()
                }
            }

            override fun run() {
                val fileSystem = FileSystems.getDefault()
                watchService = fileSystem.newWatchService()

                path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE)

                while (running.get()) {
                    val key = watchService.poll(5, TimeUnit.SECONDS) ?: continue
                    val path = key.watchable() as Path

                    for(event in key.pollEvents()) {
                        val file = path.resolve(event.context() as Path).toFile()
                        when(event.kind()) {
                            ENTRY_CREATE -> channel.trySendBlocking(FileEvent.Create(file))
                            ENTRY_MODIFY -> channel.trySendBlocking(FileEvent.Modify(file))
                            ENTRY_DELETE -> channel.trySendBlocking(FileEvent.Delete(file))
                        }
                    }

                    key.reset()
                }
            }
        }

        watchThread.start()

        awaitClose {
            watchThread.unregisterWatcher()
            if(watchThread.isAlive) watchThread.interrupt()
        }
    }
}