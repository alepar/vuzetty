package ru.alepar.vuzetty.client.association;

import ru.alepar.vuzetty.client.os.JavaInstallation;
import ru.alepar.vuzetty.client.os.OsUtilities;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Template {

    private String result;

    public Template(String path) {
        result = readTemplate(path);
    }

    public void set(String key, String value) {
        result = result.replaceAll(Pattern.quote("%" + key + "%"), Matcher.quoteReplacement(value));
    }

    public void writeToFile(File file) throws IOException {
        final Writer writer = new OutputStreamWriter(new FileOutputStream(file), "utf8");
        try {
            writer.write(result);
        } finally {
            writer.close();
        }
    }

    public String get() {
        return result;
    }

    public static String readTemplate(String templatePath1) {
        String template;
        try {
            final StringBuilder result = new StringBuilder();

            final InputStream is = Template.class.getClassLoader().getResourceAsStream(templatePath1);
            final Reader reader = new InputStreamReader(is);

            int read;
            char[] buf = new char[10240];
            try {
                while((read = reader.read(buf)) != -1) {
                    result.append(buf, 0, read);
                }
            } finally {
                reader.close();
            }

            template = result.toString();
        } catch (Exception e) {
            throw new RuntimeException("failed to read desktop file template", e);
        }
        return template;
    }

    public void populateWith(JavaInstallation javaInstallation) {
        set("JAVAWS", javaInstallation.getJavawsBinaryPath().replaceAll("\\\\", "\\\\\\\\"));
        set("EXT_ARGS", javaInstallation.getJavawsExtArguments());
        set("OPEN_ARG", javaInstallation.getJavawsOpenArgument());
		set("USERHOME", OsUtilities.getUserHome());
    }
}
