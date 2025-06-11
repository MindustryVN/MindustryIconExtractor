package extractor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import arc.*;
import arc.files.Fi;
import arc.graphics.Pixmap;
import arc.graphics.PixmapIO;
import arc.graphics.Pixmaps;
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
            Pixmap pix = Core.atlas.getPixmaps().get(region.texture, () -> region.texture.getTextureData().getPixmap());

            executorService.submit(() -> {
                var pixmap = Pixmaps.crop(pix, region.getX(), region.getY(), region.width, region.height);
                try {

                    if (pixmap.height <= 1 && pixmap.width <= 1) {
                        Log.info("Region: " + name + " x: " + region.getX() + " y: " + region.getY()
                                + " texture width: " + region.texture.width + " texture height: "
                                + region.texture.height
                                + " width: " + region.width + " height: " + region.height
                                + " pixmap width: " + pixmap.width + " pixmap height: " + pixmap.height);
                        return;
                    }

                    Fi fi = iconDir.child(name + ".png");
                    PixmapIO.writePng(fi, pixmap);
                    saved++;
                    if (saved % 100 == 0) {
                        Log.info("Saved " + saved + " icons");
                    }

                    if (saved >= Core.atlas.getRegions().size) {
                        Log.info("Saved all icons: " + saved);
                    }

                    pixmap.dispose();
                } catch (Exception e) {
                    Log.err("Can not save " + name, e);
                }
            });
        });
    }
}
