package ru.alepar.vuzetty.client.classload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ByteClassLoader extends ClassLoader {

    private static final Logger log = LoggerFactory.getLogger(ByteClassLoader.class);

    private final Map<String, byte[]> map = new HashMap<String, byte[]>();

    public ByteClassLoader(ClassLoader parent, Entry[] entries) {
        super(new MaskingClassLoader(parent, namesFrom(entries)));
        for (Entry entry : entries) {
            map.put(entry.name, entry.bytes);
        }
    }

    private static Set<String> namesFrom(Entry[] entries) {
        final Set<String> names = new HashSet<String>();
        for (Entry e : entries) {
            names.add(e.name);
        }
        return names;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if(!map.containsKey(name)) {
            throw new ClassNotFoundException(name + " is not registered");
        }

        final byte[] bytes = map.get(name);
        return defineClass(name, bytes, 0, bytes.length);
    }

    public static class Entry {
        public final String name;
        public final byte[] bytes;

        public Entry(String name, byte[] bytes) {
            this.name = name;
            this.bytes = bytes;
        }
    }

    private static class MaskingClassLoader extends ClassLoader {

        private final Set<String> maskedClasses;

        private MaskingClassLoader(ClassLoader delegate, Set<String> maskedClasses) {
            super(delegate);
            this.maskedClasses = maskedClasses;
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if(maskedClasses.contains(name)) {
                throw new ClassNotFoundException("class " + name + " is masked");
            }
            return super.loadClass(name, resolve);
        }
    }
}
