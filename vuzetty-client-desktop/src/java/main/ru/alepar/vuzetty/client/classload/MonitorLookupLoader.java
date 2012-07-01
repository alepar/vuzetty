package ru.alepar.vuzetty.client.classload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alepar.vuzetty.client.jmx.MonitorLookup;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

public class MonitorLookupLoader {

    private static final String LOOKUP_CLASS_PATH = "ru/alepar/vuzetty/client/jmx/SunAttachMonitorLookup.class";
    private static final String LOOKUP_CLASS_NAME = "ru.alepar.vuzetty.client.jmx.SunAttachMonitorLookup";

    private static final Logger log = LoggerFactory.getLogger(MonitorLookupLoader.class);

    public static MonitorLookup loadLookup() {
        try {
            final File toolsJar = findToolsJar();

            final ClassLoader toolsClassLoader = new URLClassLoader(new URL[]{ toolsJar.toURI().toURL() }, MonitorLookupLoader.class.getClassLoader());
            final ClassLoader monitorClassLoader = new ByteClassLoader(toolsClassLoader, new ByteClassLoader.Entry[]{
                    new ByteClassLoader.Entry(LOOKUP_CLASS_NAME, readResource(LOOKUP_CLASS_PATH))
            });

            return (MonitorLookup) monitorClassLoader.loadClass(LOOKUP_CLASS_NAME).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("failed to load MonitorLookup", e);
        }
    }

    private static byte[] readResource(String path) {
        try {
            final InputStream is = MonitorLookupLoader.class.getClassLoader().getResourceAsStream(path);
            try {
                final ByteArrayOutputStream os = new ByteArrayOutputStream();
                final byte[] buffer = new byte[10240];
                int read;
                while((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.close();
                return os.toByteArray();
            } finally {
                is.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("failed to read " + path, e);
        }
    }

    private static File findToolsJar() throws IOException {
        final File toolsJar = new File(jdkHome() + File.separator + "lib" + File.separator + "tools.jar");
        log.debug("using tools.jar = {}", toolsJar.getCanonicalPath());
        if(!toolsJar.exists()) {
            throw new IllegalStateException("this program requires tools.jar to run");
        }
        return toolsJar;
    }

    private static String jdkHome() {
        String javaHome = System.getProperty("java.home");
        if(javaHome.endsWith("jre")) {
            return new File(javaHome).getParent();
        }
        return javaHome;
    }

}
