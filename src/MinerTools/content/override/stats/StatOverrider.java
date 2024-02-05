package MinerTools.content.override.stats;

import MinerTools.*;
import arc.*;
import arc.scene.ui.layout.*;
import mindustry.ctype.*;
import mindustry.world.meta.*;

public abstract class StatOverrider<T extends UnlockableContent>{
    public final Stat stat;

    public StatOverrider(Stat stat){
        this.stat = stat;
    }

    public abstract boolean isValid(T type);

    public abstract void tryOverride(Stats stats, T type);

    protected abstract void override(Stats stats, Table table, T type);

    public static abstract class StatValueOverrider<T extends UnlockableContent> extends StatOverrider<T>{
        protected boolean clearStatValues = false;

        public StatValueOverrider(Stat stat){
            super(stat);
        }

        public void tryOverride(Stats stats, T type){
            if(!isValid(type)) return;

            if(clearStatValues){
                clearStatValues(type);
            }

            stats.add(stat, table -> {
                override(stats, table, type);
            });
        }

        protected final void clearStatValues(T type){
            type.stats.remove(stat);
        }
    }

    public static abstract class StatAdder<T extends UnlockableContent> extends StatValueOverrider<T>{

        public StatAdder(String name){
            this(name, StatCat.general);
        }

        public StatAdder(String name, StatCat category){
            super(getStat(name, category));
        }

        private static Stat getStat(String name, StatCat category){
            Stat stat = Stat.all.find(s -> s.name.equals(name));

            if(stat == null){
                return new MStat(name, category);
            }

            return stat;
        }

    }

    public static class MStat extends Stat{

        public MStat(String name, StatCat category){
            super(name, category);
        }

        public MStat(String name){
            super(name);
        }

        @Override
        public String localized(){
            return Core.bundle.get("stat." + name) + MinerVars.modSymbol;
        }

    }
}