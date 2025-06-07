package extractor;

import arc.*;
import arc.files.Fi;
import arc.graphics.Pixmap;
import arc.graphics.PixmapIO;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import mindustry.*;
import mindustry.ctype.Content;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.ui.fragments.MenuFragment.MenuButton;

public class Main extends Mod {

    private static Fi iconDir = Vars.dataDirectory.child("icon");

    public Main() {
        iconDir.mkdirs();

        Events.on(ClientLoadEvent.class, e -> {
            if (Vars.mobile) {
                Vars.ui.menufrag.addButton("Extract Icon", Icon.map, () -> {
                    outputContentSprites();
                });
            } else {
                Vars.ui.menufrag.addButton(new MenuButton("Tools", Icon.wrench, () -> {
                }, //
                        new MenuButton(Core.bundle.format("Extract Icon"), Icon.map, () -> {
                            outputContentSprites();
                        })));
            }
        });
    }

    public static void outputContentSprites() {
        for (Seq<Content> contents : Vars.content.getContentMap()) {
            for (Content content : contents) {
                if (content instanceof UnlockableContent) {
                    String name = ((UnlockableContent) content).name;
                    TextureRegion icon = ((UnlockableContent) content).fullIcon;
                    Fi fi = iconDir.child(name + ".png");
                    Pixmap pixmap = Core.atlas.getPixmap(icon).crop();
                    PixmapIO.writePng(fi, pixmap);
                    pixmap.dispose();
                }
            }
        }
    }
}
