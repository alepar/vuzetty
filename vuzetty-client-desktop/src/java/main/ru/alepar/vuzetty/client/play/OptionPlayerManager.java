package ru.alepar.vuzetty.client.play;

import com.google.common.collect.Lists;
import ru.alepar.vuzetty.client.config.Configuration;
import ru.alepar.vuzetty.client.play.upnp.UpnpControl;
import ru.alepar.vuzetty.client.run.RuntimeCmdRunner;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OptionPlayerManager implements PlayerManager {

    private final UpnpControl upnpControl;
    private final Configuration config;
    private final Component component;

    private UrlPlayer lastPlayer;

    public OptionPlayerManager(Configuration config, UpnpControl upnpControl, Component component) {
        this.config = config;
        this.upnpControl = upnpControl;
        this.component = component;

        lastPlayer = createLocalPlayer();
    }

    @Override
    public void play(String url) {
        url = "http://" + config.getServerAddress().getAddress().getHostAddress() + url;

        if (lastPlayer instanceof LocalUrlPlayer) {
            lastPlayer = createLocalPlayer();
        }

        if (!upnpControl.getPlayers().isEmpty()) {
            final List<Object> players = Lists.newArrayList();
            players.add(lastPlayer);
            players.addAll(upnpControl.getPlayers());
            final Object[] possibilities = players.toArray(new Object[players.size()]);
            final UrlPlayer response = (UrlPlayer) JOptionPane.showInputDialog(
                    component,
                    "", "Choose player",
                    JOptionPane.QUESTION_MESSAGE, null, possibilities,
                    lastPlayer);

            if (response != null) {
                lastPlayer = response;
            } else {
                return;
            }
        }

        lastPlayer.play(url);
    }

    public UrlPlayer createLocalPlayer() {
        return new LocalUrlPlayer(new RuntimeCmdRunner(), config.getPlayerVideo());
    }
}
