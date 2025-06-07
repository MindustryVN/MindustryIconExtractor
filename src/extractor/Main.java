package extractor;

import arc.*;
import arc.files.Fi;
import arc.graphics.Pixmap;
import arc.graphics.PixmapIO;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.*;
import mindustry.ctype.Content;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;

public class Main extends Mod {

    private static Fi iconDir = Vars.dataDirectory.child("icon");

    public Main() {
        iconDir.mkdirs();

        Events.on(ClientLoadEvent.class, e -> {
            Vars.ui.menufrag.addButton("Extract Icon", Icon.map, () -> {
                outputContentSprites();
            });
        });
    }

    public static void outputContentSprites() {
        for (Seq<Content> contents : Vars.content.getContentMap()) {
            for (Content content : contents) {
                if (content instanceof UnlockableContent) {
                    String name = ((UnlockableContent) content).name;
                    try {
                        var icon = ((UnlockableContent) content).fullIcon;
                        Fi fi = iconDir.child(name + ".png");
                        Pixmap pixmap = Core.atlas.getPixmap(icon).crop();
                        PixmapIO.writePng(fi, pixmap);
                        pixmap.dispose();
                        Log.info("Saved " + name + " at " + fi.absolutePath());
                    } catch (Exception e) {
                        Log.err("Can not save " + name, e);
                    }
                }
            }
        }
    }
}
