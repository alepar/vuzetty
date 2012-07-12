package ru.alepar.vuzetty.client.os;

import java.io.File;

public abstract class CommonJavaInstallation implements JavaInstallation {

    protected final File javaHome;

    public CommonJavaInstallation(File javaHome) {
        this.javaHome = javaHome;
    }

    @Override
    public String getJavawsBinaryPath() {
        try {
            return new File(new File(javaHome, "bin"), "javaws").getCanonicalPath();
        } catch (Exception e) {
            throw new RuntimeException("failed to get javaws path", e);
        }
    }

}
