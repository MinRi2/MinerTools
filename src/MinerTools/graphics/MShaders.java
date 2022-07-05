package MinerTools.graphics;

import arc.*;
import arc.files.*;
import arc.graphics.gl.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;

public class MShaders{
    public static MShader overdriveZone;

    public static void init(){
        overdriveZone = new OverdriveZoneShader();
    }

    static class OverdriveZoneShader extends MShader{

        public OverdriveZoneShader(){
            super("rangeZone", "screenspace");
        }

        @Override
        public void apply(){
            setUniformf("u_dp", Scl.scl(1f));
            setUniformf("u_time", Time.time / Scl.scl(1f));
            setUniformf("u_offset",
            Core.camera.position.x - Core.camera.width / 2,
            Core.camera.position.y - Core.camera.height / 2);
            setUniformf("u_texsize", Core.camera.width, Core.camera.height);
            setUniformf("u_invsize", 1f/Core.camera.width, 1f/Core.camera.height);
        }
    }

    static class MShader extends Shader{

        public MShader(String frag, String vert){
            super(getShaderFi(vert + ".vert"), getShaderFi(frag + ".frag"));
        }

        public static Fi getShaderFi(String file){
            Fi fi = Core.files.internal("shaders/" + file);

            if(!fi.exists()){
                fi = Vars.tree.get("shaders/" + file);

                if(!fi.exists()){
                    throw new ArcRuntimeException("Cannot get shader fi: " + file);
                }
            }

            return fi;
        }

    }

}
