package sabina.backend.undertow;

import java.util.EventListener;

import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.api.ListenerInfo;

/**
 * @author jam
 */
final class ApplicationListenerInfo extends ListenerInfo {

    public ApplicationListenerInfo (Class<? extends EventListener> listenerClass) {
        super (listenerClass);
    }

    public ApplicationListenerInfo (Class<? extends EventListener> listenerClass,
        InstanceFactory<? extends EventListener> instanceFactory) {
        super (listenerClass, instanceFactory);
    }
}
