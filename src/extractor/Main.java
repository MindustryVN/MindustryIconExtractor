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

    private static int saved = 0;

    public Main() {
        iconDir.mkdirs();
        iconDir.emptyDirectory();

        Events.on(ClientLoadEvent.class, e -> {
            Vars.ui.menufrag.addButton("Extract Icon", Icon.map, () -> {
                new Thread(Main::outputContentSprites).start();
            });
        });
    }

    public static synchronized void outputContentSprites() {
        saved = 0;
        Core.atlas.getRegionMap().each((name, region) -> {
            executorService.submit(() -> {
                try {
                    Fi fi = iconDir.child(name + ".png");
                    Pixmap pixmap = Core.atlas.getPixmap(region).crop();
                    PixmapIO.writePng(fi, pixmap);
                    Log.info("Saved " + name + " at " + fi.absolutePath());
                    saved++;
                    if (saved % 100 == 0) {
                        Log.info("Saved " + saved + " icons");
                    }

                    if (saved >= Core.atlas.getRegions().size) {
                        Log.info("Saved all icons");
                    }
                } catch (Exception e) {
                    Log.err("Can not save " + name, e);
                }
            });
        });
    }
}
