package com.vulcanth.nyel.helpers;

import java.util.ArrayList;

public class PlayerDataParty {
    private final ArrayList<String> invites = new ArrayList<>();
    private String nick;

    private PlayerDataParty() {
    }

    public static PlayerDataParty of(String nick) {
        PlayerDataParty result = new PlayerDataParty();
        result.nick = nick;
        return result;
    }

    public String getNick() {
        return nick;
    }

    public ArrayList<String> getInvites() {
        return invites;
    }
}
