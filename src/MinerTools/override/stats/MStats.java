package MinerTools.override.stats;

import MinerTools.*;
import arc.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.ctype.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import java.lang.reflect.*;

public class MStats{
    public static StatOverride<Block> block = new BlockStats();

    public static UnitStats unit = new UnitStats();

    public static class StatOverride<T extends UnlockableContent>{
        public ObjectMap<Stat, Seq<BaseOverrider<T>>> map = new ObjectMap<>();

        public void override(T type){
            type.checkStats();

            for(var seq : map.values()){
                for(BaseOverrider<T> statValue : seq){
                    statValue.tryOverride(type.stats, type);
                }
            }
        }

        public void addValue(BaseOverrider<T> value){
            map.get(value.stat, Seq::new).add(value);
        }

        @SuppressWarnings("unchecked")
        public void addValues(BaseOverrider<T>... values){
            for(BaseOverrider<T> value : values){
                addValue(value);
            }
        }

    }

    public abstract static class BaseOverrider<T extends UnlockableContent>{
        public static Field statsMapField = MinerUtils.getField(mindustry.world.meta.Stats.class, "map");

        public final Stat stat;

        public BaseOverrider(Stat stat){
            this.stat = stat;
        }

        public abstract boolean isValid(T type);

        public abstract void tryOverride(Stats stats, T type);

        public abstract void override(Stats stats, Table table, T type);

        public final void clearStatValues(Stats stats){
            OrderedMap<StatCat, OrderedMap<Stat, Seq<StatValue>>> map = MinerUtils.getValue(statsMapField, stats);

            map.get(stat.category).clear();
        }

    }

    public abstract static class StatValueOverrider<T extends UnlockableContent> extends BaseOverrider<T>{

        public StatValueOverrider(Stat stat){
            super(stat);
        }

        public void tryOverride(Stats stats, T type){
            if(!isValid(type)) return;

            stats.add(stat, inset -> {
                inset.table(table -> {
                    override(stats, table, type);
                }).padRight(3f);
            });
        }

    }

    public abstract static class StatAdder<T extends UnlockableContent> extends StatValueOverrider<T>{

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
            return Core.bundle.get("stat." + name) + "[M]";
        }

    }

}
