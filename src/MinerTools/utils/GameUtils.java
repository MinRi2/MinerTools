/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  arc.graphics.Color
 *  java.lang.Object
 *  java.lang.String
 *  mindustry.game.Team
 */
package MinerTools.utils;

import mindustry.game.*;

public class GameUtils {
    public static String colorMark(Team team) {
        return "[#" + (Object)team.color + "]";
    }

    public static String coloredName(Team team) {
        return GameUtils.colorMark(team) + team.localized() + "[white]";
    }
}

