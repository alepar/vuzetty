package ru.alepar.vuzetty.client.os;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alepar.vuzetty.client.association.Associator;
import ru.alepar.vuzetty.client.association.nix.XdgMimeAssociator;
import ru.alepar.vuzetty.client.run.CmdResolver;
import ru.alepar.vuzetty.client.run.CmdRunner;
import ru.alepar.vuzetty.client.run.NixCmdResolver;
import ru.alepar.vuzetty.client.run.RuntimeCmdRunner;

import java.awt.*;
import java.lang.reflect.Field;

class NixInteractionFactory extends CommonInteractionFactory {

    private static final Logger log = LoggerFactory.getLogger(NixInteractionFactory.class);

    @Override
    public Associator getAssociator() {
        return new XdgMimeAssociator(getJavaInstallation(), getCmdRunner());
    }

    @Override
    public CmdRunner getCmdRunner() {
        return new RuntimeCmdRunner();
    }

    @Override
    public CmdResolver getCmdResolver() {
        return new NixCmdResolver(getCmdRunner());
    }

    @Override
    public void fixWmClass() {
        try {
            Toolkit xToolkit = Toolkit.getDefaultToolkit();
            Field awtAppClassNameField = xToolkit.getClass().getDeclaredField("awtAppClassName");
            awtAppClassNameField.setAccessible(true);
            awtAppClassNameField.set(xToolkit, "ru-alepar-vuzetty_client");
        } catch (Exception e) {
            log.warn("failed to fix WM_CLASS", e);
        }
    }
}
