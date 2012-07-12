package ru.alepar.vuzetty.client.os;

import java.io.File;

public class OracleJavaInstallation extends CommonJavaInstallation {

    public OracleJavaInstallation(File javaHome) {
        super(javaHome);
    }

    @Override
    public String getJavawsExtArguments() {
        return "";
    }

    @Override
    public String getJavawsOpenArgument() {
        return "-open";
    }
}
