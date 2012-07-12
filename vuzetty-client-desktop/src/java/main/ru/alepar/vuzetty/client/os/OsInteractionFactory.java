package ru.alepar.vuzetty.client.os;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alepar.vuzetty.client.association.Associator;
import ru.alepar.vuzetty.client.run.CmdResolver;
import ru.alepar.vuzetty.client.run.CmdRunner;

public interface OsInteractionFactory {

    Associator getAssociator();
    CmdRunner getCmdRunner();
    CmdResolver getCmdResolver();
    JavaInstallation getJavaInstallation();

    public class Native {

        private static final Logger log = LoggerFactory.getLogger(OsInteractionFactory.Native.class);

        public static OsInteractionFactory create() {
            final OsType osType = recognize();
            log.debug("os recognized as {}", osType);
            switch(osType) {
                case WINDOWS: return new WindowsInteractionFactory();
                case NIX: return new NixInteractionFactory();
            }
            throw new RuntimeException("unknown OsType: " + osType);
        }

        private static OsType recognize() {
            final String osName = System.getProperty("os.name").toLowerCase();
            log.debug("os.name = {}", osName);
            if(osName.contains("windows")) {
                return OsType.WINDOWS;
            } else {
                return OsType.NIX;
            }
        }

    }

}
