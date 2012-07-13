package ru.alepar.vuzetty.client.os;

import ru.alepar.vuzetty.client.association.Associator;
import ru.alepar.vuzetty.client.association.nix.XdgMimeAssociator;
import ru.alepar.vuzetty.client.run.CmdResolver;
import ru.alepar.vuzetty.client.run.CmdRunner;
import ru.alepar.vuzetty.client.run.NixCmdResolver;
import ru.alepar.vuzetty.client.run.RuntimeCmdRunner;

class NixInteractionFactory extends CommonInteractionFactory {

    @Override
    public Associator getAssociator() {
        return new XdgMimeAssociator(getJavaInstallation(), getCmdResolver(), getCmdRunner());
    }

    @Override
    public CmdRunner getCmdRunner() {
        return new RuntimeCmdRunner();
    }

    @Override
    public CmdResolver getCmdResolver() {
        return new NixCmdResolver(getCmdRunner());
    }

}
