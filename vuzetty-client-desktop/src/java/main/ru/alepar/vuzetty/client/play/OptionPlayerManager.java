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

    private volatile String lastPlayer;

    public OptionPlayerManager(Configuration config, UpnpControl upnpControl, Component component) {
        this.config = config;
        this.upnpControl = upnpControl;
        this.component = component;
    }

    @Override
    public void play(String url) {
        url = "http://" + config.getServerAddress().getAddress().getHostAddress() + url;

        final List<UrlPlayer> players = Lists.newArrayList();
        players.add(createLocalPlayer());

        if (!upnpControl.getPlayers().isEmpty()) {
            players.addAll(upnpControl.getPlayers());
        }

        final UrlPlayer playerToUse;
        if (players.size() == 1) {
            playerToUse = players.get(0);
        } else {
            final Object[] possibilities = players.toArray(new Object[players.size()]);
            final UrlPlayer response = (UrlPlayer) JOptionPane.showInputDialog(
                    component,
                    "", "Choose player",
                    JOptionPane.QUESTION_MESSAGE, null, possibilities,
                    findMatchingPlayer(players, lastPlayer));

            if (response != null) {
                playerToUse = response;
            } else {
                return;
            }

        }

        playerToUse.play(url);
        lastPlayer = playerToUse.toString();
    }

    private static UrlPlayer findMatchingPlayer(Iterable<UrlPlayer> players, String playerName) {
        for (UrlPlayer player : players) {
            if (player.toString().equals(playerName)) {
                return player;
            }
        }

        return null;
    }

    private UrlPlayer createLocalPlayer() {
        return new LocalUrlPlayer(new RuntimeCmdRunner(), config.getPlayerVideo());
    }
}
