package org.starloco.locos.util.lang.type;

import org.starloco.locos.kernel.Config;
import org.starloco.locos.util.lang.AbstractLang;

/**
 * Created by Locos on 09/12/2015.
 */
public class English extends AbstractLang {

    public final static English singleton = new English();

    public static English getInstance() {
        return singleton;
    }

    public void initialize() {
        int index = 0;
        this.sentences.add(index, "Your overall channel is disabled."); index++;
        this.sentences.add(index, "Some character used in your sentence are disabled."); index++;
        this.sentences.add(index, "You must wait #1 second(s)."); index++;
        this.sentences.add(index, "You have enabled the general channel."); index++;
        this.sentences.add(index, "You have disabled the general channel."); index++;
        this.sentences.add(index, "List of members of staff connected :"); index++;
        this.sentences.add(index, "There is no member of staff connected."); index++;
        this.sentences.add(index, "You are not stuck.."); index++;
        this.sentences.add(index, "<b>" + Config.getInstance().NAME + "</b>\nOnline since : #1j #2h #3m #4s."); index++;
        this.sentences.add(index, "\nPlayers online : #1"); index++;
        this.sentences.add(index, "\nUnique players online : #1"); index++;
        this.sentences.add(index, "\nMost online : #1"); index++;
        this.sentences.add(index, "The available commands are :\n"
                + "<b>.infos</b> - Provides information on the server.\n"
                + "<b>.deblo</b> - Unblocks teleporting you to a free cell.\n"
                + "<b>.staff</b> - Lets see members of staff connected.\n"
                + "<b>.all</b> - Sends a message to all players.\n"
                + "<b>.noall</b> - Can no longer receive the messages of the General channel."); index++;
        this.sentences.add(index, "You can now vote on the <b><a href='" + Config.getInstance().url + "'>site</a></b>.");
    }
}
