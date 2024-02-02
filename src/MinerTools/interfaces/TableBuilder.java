package MinerTools.interfaces;

import arc.scene.ui.layout.*;

public interface TableBuilder<T extends Table>{
    void build(T table);
}

