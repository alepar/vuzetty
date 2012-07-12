package ru.alepar.vuzetty.client.os;

import ru.alepar.vuzetty.client.association.Associator;
import ru.alepar.vuzetty.client.association.WindowsAssociator;
import ru.alepar.vuzetty.client.run.CmdResolver;
import ru.alepar.vuzetty.client.run.CmdRunner;

class WindowsInteractionFactory extends CommonInteractionFactory {

    @Override
    public Associator getAssociator() {
        return new WindowsAssociator();
    }

    @Override
    public CmdRunner getCmdRunner() {
        throw new RuntimeException("parfenal, implement me!");
    }

    @Override
    public CmdResolver getCmdResolver() {
        throw new RuntimeException("parfenal, implement me!");
    }

    @Override
    public JavaInstallation getJavaInstallation() {
        throw new RuntimeException("parfenal, implement me!");
    }

}
