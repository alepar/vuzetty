package ru.alepar.vuzetty.client.classload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alepar.vuzetty.client.jmx.MonitorLookup;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class MonitorLookupLoader {

    private static final Logger log = LoggerFactory.getLogger(MonitorLookupLoader.class);

    public static MonitorLookup loadLookup() {
        try {
            File toolsJar = new File(jdkHome() + File.separator + "lib" + File.separator + "tools.jar");
            log.debug("using tools.jar = {}", toolsJar.getCanonicalPath());
            if(!toolsJar.exists()) {
                throw new IllegalStateException("this program requires tools.jar to run");
            }

            return (MonitorLookup) makeClassLoaderWithSunAttachMonitorLookup(makeClassLoaderWithJar(toolsJar)).loadClass("ru.alepar.vuzetty.client.jmx.SunAttachMonitorLookup").newInstance();
        } catch (Exception e) {
            throw new RuntimeException("failed to load MonitorLookup", e);
        }
    }

    private static ClassLoader makeClassLoaderWithSunAttachMonitorLookup(ClassLoader parent) {
        return new PriorityURLClassLoader(getClassPath(), parent);
    }

    private static URL[] getClassPath() {
        return ((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs();
    }

    private static URLClassLoader makeClassLoaderWithJar(File toolsJar) throws MalformedURLException {
        return new URLClassLoader(new URL[] { toolsJar.toURI().toURL() });
    }

    private static String jdkHome() {
        String javaHome = System.getProperty("java.home");
        if(javaHome.endsWith("jre")) {
            return new File(javaHome).getParent();
        }
        return javaHome;
    }

}
