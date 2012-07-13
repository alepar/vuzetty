package ru.alepar.vuzetty.client.os;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public abstract class CommonInteractionFactory implements OsInteractionFactory {

    private static final Logger log = LoggerFactory.getLogger(CommonInteractionFactory.class);

    private final JavaInstallation javaInstallation = createJavaInstallation();

    @Override
    public JavaInstallation getJavaInstallation() {
        return javaInstallation;
    }

    private static JavaInstallation createJavaInstallation() {
        final File javaHome = new File(System.getProperty("java.home"));
        log.debug("java.home = {}", javaHome.getAbsolutePath());
        final VmType vmType = recognizeVm();
        log.debug("javavm recognized as {}", vmType);
        switch(vmType) {
            case OPENJDK: return new OpenjdkJavaInstallation(javaHome);
            case ORACLE: return new OracleJavaInstallation(javaHome);
            default: throw new RuntimeException("unknown vmType " + vmType);
        }
    }

    private static VmType recognizeVm() {
        final String vmName = System.getProperty("java.vm.name").toLowerCase();
        log.debug("java.vm.name = {}", vmName);
        if(vmName.contains("openjdk")) {
            return VmType.OPENJDK;
        }
        return VmType.ORACLE;
    }


}
