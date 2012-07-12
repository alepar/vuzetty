package ru.alepar.vuzetty.client.os;

import java.io.File;

public class OpenjdkJavaInstallation extends CommonJavaInstallation {

    public OpenjdkJavaInstallation(File javaHome) {
        super(javaHome);
    }

    @Override
    public String getJavawsExtArguments() {
        return "-headless";
    }

    @Override
    public String getJavawsOpenArgument() {
        return "-arg";
    }
}
