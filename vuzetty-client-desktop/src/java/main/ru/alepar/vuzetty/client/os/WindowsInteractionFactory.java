package ru.alepar.vuzetty.client.os;

import ru.alepar.vuzetty.client.association.Associator;
import ru.alepar.vuzetty.client.association.win.WindowsAssociator;
import ru.alepar.vuzetty.client.run.CmdResolver;
import ru.alepar.vuzetty.client.run.CmdRunner;
import ru.alepar.vuzetty.client.run.RuntimeCmdRunner;

class WindowsInteractionFactory extends CommonInteractionFactory {

    @Override
    public Associator getAssociator() {
        return new WindowsAssociator(getJavaInstallation(), getCmdRunner());
    }

    @Override
    public CmdRunner getCmdRunner() {
        return new RuntimeCmdRunner();
    }

    @Override
    public CmdResolver getCmdResolver() {
        throw new UnsupportedOperationException("we should not need this for win yet");
    }

}
