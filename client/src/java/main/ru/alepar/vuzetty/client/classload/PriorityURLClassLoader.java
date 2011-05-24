package ru.alepar.vuzetty.client.classload;

import java.net.URL;
import java.net.URLClassLoader;

class PriorityURLClassLoader extends URLClassLoader {

    public PriorityURLClassLoader(URL[] classPath, ClassLoader parent) {
        super(classPath, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if ("ru.alepar.vuzetty.client.jmx.SunAttachMonitorLookup".equals(name)) {
            Class c = findClass(name);
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
        return super.loadClass(name, resolve);

    }
}
