package MinerTools;

import MinerTools.input.*;
import MinerTools.io.*;
import MinerTools.ui.*;
import arc.*;
import arc.KeyBinds.*;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.game.*;
import mindustry.game.Schematic.*;
import mindustry.gen.*;
import mindustry.input.*;
import mindustry.input.Placement.*;
import mindustry.ui.dialogs.SettingsMenuDialog.*;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.*;
import mindustry.world.*;
import mindustry.world.blocks.ConstructBlock.*;
import mindustry.world.blocks.storage.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.input.PlaceMode.breaking;

public class MinerVars{
    public static MSettings settings;
    public static MUI ui;

    public static boolean desktop;

    public static void init(){
        settings = new MSettings();
        ui = new MUI();

        desktop = app.isDesktop();

        // update controls
        if(desktop){
            initBindings();
        }

        settings.init();
        ui.init();

        betterUIScaleSetting();
        betterSchemeSize();
    }

    public static void betterUIScaleSetting(){
        if(Core.settings.has("_uiscale")){
            MinerVars.settings.put("_uiscale", Core.settings.getInt("_uiscale"));
            Core.settings.remove("_uiscale");
        }

        int[] lastUiScale = {Core.settings.getInt("uiscale", 100)};
        int index = Vars.ui.settings.graphics.getSettings().indexOf(setting -> setting.name.equals("uiscale"));

        final boolean[] shouldChange = {false};

        Core.settings.put("uiscale", MinerVars.settings.getInt("_uiscale", 100));
        Core.settings.put("uiscalechanged", false);

        if(index != -1){
            Vars.ui.settings.graphics.getSettings().add(new Setting("rebuildListener"){
                @Override
                public void add(SettingsTable table){
                    shouldChange[0] = false;
                }
            });

            Vars.ui.settings.graphics.getSettings().set(index, new SliderSetting("uiscale", 100, 25, 300, 1, s -> {
                if(shouldChange[0]){
                    //if the user changed their UI scale, but then put it back, don't consider it 'changed'
                    Core.settings.put("uiscalechanged", s != lastUiScale[0]);
                }else{
                    shouldChange[0] = true;
                }

                MinerVars.settings.put("_uiscale", s, false, true);

                return s + "%";
            }));
        }
        Vars.ui.settings.graphics.rebuild();

        Scl.setProduct(MinerVars.settings.getInt("_uiscale", 100) / 100f);
    }

    public static void betterSchemeSize(){
        Vars.schematics = new Schematics(){
            {
                load();
            }

            public Schematic create(int x, int y, int x2, int y2){
                NormalizeResult result = Placement.normalizeArea(x, y, x2, y2, 0, false, 128);

                x = result.x;
                y = result.y;
                x2 = result.x2;
                y2 = result.y2;

                int ox = x, oy = y, ox2 = x2, oy2 = y2;

                Seq<Stile> tiles = new Seq<>();

                int minx = x2, miny = y2, maxx = x, maxy = y;
                boolean found = false;
                for(int cx = x; cx <= x2; cx++){
                    for(int cy = y; cy <= y2; cy++){
                        Building linked = world.build(cx, cy);
                        Block realBlock = linked == null ? null : linked instanceof ConstructBuild cons ? cons.current : linked.block;

                        if(linked != null && realBlock != null && (realBlock.isVisible() || realBlock instanceof CoreBlock)){
                            int top = realBlock.size/2;
                            int bot = realBlock.size % 2 == 1 ? -realBlock.size/2 : -(realBlock.size - 1)/2;
                            minx = Math.min(linked.tileX() + bot, minx);
                            miny = Math.min(linked.tileY() + bot, miny);
                            maxx = Math.max(linked.tileX() + top, maxx);
                            maxy = Math.max(linked.tileY() + top, maxy);
                            found = true;
                        }
                    }
                }

                if(found){
                    x = minx;
                    y = miny;
                    x2 = maxx;
                    y2 = maxy;
                }else{
                    return new Schematic(new Seq<>(), new StringMap(), 1, 1);
                }

                int width = x2 - x + 1, height = y2 - y + 1;
                int offsetX = -x, offsetY = -y;
                IntSet counted = new IntSet();
                for(int cx = ox; cx <= ox2; cx++){
                    for(int cy = oy; cy <= oy2; cy++){
                        Building tile = world.build(cx, cy);
                        Block realBlock = tile == null ? null : tile instanceof ConstructBuild cons ? cons.current : tile.block;

                        if(tile != null && !counted.contains(tile.pos()) && realBlock != null
                        && (realBlock.isVisible() || realBlock instanceof CoreBlock)){
                            Object config = tile instanceof ConstructBuild cons ? cons.lastConfig : tile.config();

                            tiles.add(new Stile(realBlock, tile.tileX() + offsetX, tile.tileY() + offsetY, config, (byte)tile.rotation));
                            counted.add(tile.pos());
                        }
                    }
                }

                return new Schematic(tiles, new StringMap(), width, height);
            }
        };

        control.setInput(new DesktopInput(){
            @Override
            public void drawTop(){
//                super.drawTop();

                Lines.stroke(1f);
                int cursorX = World.toTile(Core.input.mouseWorldX());
                int cursorY = World.toTile(Core.input.mouseWorldY());

                //draw break selection
                if(mode == breaking){
                    drawBreakSelection(selectX, selectY, cursorX, cursorY, !Core.input.keyDown(Binding.schematic_select) ? 100 : 128);
                }

                if(Core.input.keyDown(Binding.schematic_select) && !Core.scene.hasKeyboard() && mode != breaking){
                    drawSelection(schemX, schemY, cursorX, cursorY, 128);
                }

                Draw.reset();
            }
        });
    }

    public static void initBindings(){
        KeyBind[] bindings = Reflect.get(keybinds, "definitions");
        KeyBind[] modBindings = ModBinding.values();

        KeyBind[] newBindings = new KeyBind[bindings.length + modBindings.length];
        System.arraycopy(bindings, 0, newBindings, 0, bindings.length);
        System.arraycopy(modBindings, 0, newBindings, bindings.length, modBindings.length);

        keybinds.setDefaults(newBindings);
        Reflect.invoke(keybinds, "load");
        Reflect.invoke(Vars.ui.controls, "setup");
    }
}
