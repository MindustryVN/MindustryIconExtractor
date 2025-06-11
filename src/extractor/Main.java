package extractor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import arc.*;
import arc.files.Fi;
import arc.graphics.Pixmap;
import arc.graphics.PixmapIO;
import arc.util.Log;
import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;

public class Main extends Mod {

    private static Fi iconDir = Vars.dataDirectory.child("icon");
    private static ExecutorService executorService = Executors.newWorkStealingPool();

    public Main() {
        iconDir.mkdirs();
        iconDir.emptyDirectory();

        Events.on(ClientLoadEvent.class, e -> {
            Vars.ui.menufrag.addButton("Extract Icon", Icon.map, () -> {
                new Thread(Main::outputContentSprites).start();
            });
        });
    }

    public static void outputContentSprites() {

        for (var block : Vars.content.blocks()) {
            String name = block.name;
            try {
                executorService.submit(() -> {
                    var icon = block.fullIcon;
                    Fi fi = iconDir.child(name + ".png");
                    Pixmap pixmap = Core.atlas.getPixmap(icon).crop();
                    PixmapIO.writePng(fi, pixmap);
                    Log.info("Saved block " + name + " at " + fi.absolutePath());
                });
            } catch (Exception e) {
                Log.err("Can not save " + name, e);
            }
        }
    }
}
