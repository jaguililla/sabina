package sabina.backend.undertow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.undertow.server.handlers.resource.*;

/**
 * TODO Change by version with two resourceManagers (better performance ?)
 */
final class ChainResourceManager implements ResourceManager {

    private List<ResourceManager> managers = new ArrayList<> ();

    ChainResourceManager (String aStaticPath, String aFilesPath) {
        if (aStaticPath != null)
            managers.add (new ClassPathResourceManager (
                ClassLoader.getSystemClassLoader (), aStaticPath));

        if (aFilesPath != null)
            managers.add (new FileResourceManager (new File (aFilesPath), 0L));
    }

    @Override public Resource getResource (String path) throws IOException {
        for (ResourceManager rm : managers) {
            Resource res = rm.getResource (path);
            if (res != null)
                return res;
        }

        return null;
    }

    @Override public boolean isResourceChangeListenerSupported () {
        for (ResourceManager rm : managers)
            if (!rm.isResourceChangeListenerSupported ())
                return false;

        return true;
    }

    @Override public void registerResourceChangeListener (
        ResourceChangeListener listener) {

        managers.forEach (manager -> manager.registerResourceChangeListener (listener));
    }

    @Override public void removeResourceChangeListener (
        ResourceChangeListener listener) {

        managers.forEach (manager -> manager.removeResourceChangeListener (listener));
    }

    @Override public void close () throws IOException {
        for (ResourceManager rm : managers)
            rm.close ();
    }
}
