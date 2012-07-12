package ru.alepar.vuzetty.client.run;

public interface CmdRunner {

    void exec(String[] args);
    int waitForExit();
    String stdout();
    String stderr();

}
