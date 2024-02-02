package MinerTools.content.override;

import MinerTools.content.override.stats.*;
import MinerTools.content.override.stats.unit.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.type.*;
import mindustry.ui.dialogs.*;
import mindustry.world.*;
import mindustry.world.meta.*;

public class MStats{
    private static final ContentOverrider<Block> blockOverrider = new ContentOverrider<>();

    private static final ContentOverrider<UnitType> unitOverrider = new ContentOverrider<>();

    public static void init(){
//        blockOverrider.addOverriders();
        unitOverrider.addOverriders(new MineTierOverrider());

        addOverrideTrigger();
    }

    private static void addOverrideTrigger(){
        if(Vars.ui.content.getClass() == ContentInfoDialog.class){
            Vars.ui.content = new ContentInfoDialog(){
                @Override
                public void show(UnlockableContent content){
                    if(content instanceof Block block){
                        blockOverrider.override(block);
                    }else if(content instanceof UnitType unitType){
                        unitOverrider.override(unitType);
                    }

                    super.show(content);
                }
            };
        }else{
            Log.warn("ContentInfoDialog has been replaced probably by other mod. If you want to enjoy more stat info, please uninstall the conflict mod");
        }
    }

    public static class ContentOverrider<T extends UnlockableContent>{
        private final IntSeq overridden = new IntSeq();
        public ObjectMap<Stat, Seq<StatOverrider<T>>> map = new ObjectMap<>();

        public void override(T type){
            if(overridden.contains(type.id)){
                return;
            }

            if(!type.stats.intialized){
                type.checkStats();
            }

            for(var seq : map.values()){
                for(StatOverrider<T> overrider : seq){
                    overrider.tryOverride(type.stats, type);
                }
            }

            overridden.add(type.id);
        }

        @SuppressWarnings("unchecked")
        public void addOverriders(StatOverrider<T>... values){
            for(StatOverrider<T> overrider : values){
                map.get(overrider.stat, Seq::new).add(overrider);
            }
        }

    }

}
