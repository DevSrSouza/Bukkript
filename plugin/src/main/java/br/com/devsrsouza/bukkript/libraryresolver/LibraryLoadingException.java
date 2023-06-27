package br.com.devsrsouza.bukkript.libraryresolver;

/**
 * Indicates that an exception has occured while loading a library.
 */
public class LibraryLoadingException extends RuntimeException {

    public LibraryLoadingException(String s) {
        super(s);
    }

    public LibraryLoadingException(String s, Exception e) {
        super(s, e);
    }
}
