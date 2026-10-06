package org.arkanoid.audio;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceLocatorTest {
    private final ResourceLocator locator = new ResourceLocator();

    @Test
    void sfxPath_containsFilenameAndSfxFolder() {
        Path p = locator.sfxPath(SfxId.PADDLE_HIT);
        assertNotNull(p);
        String s = p.toString();
        assertTrue(s.endsWith(SfxId.PADDLE_HIT.getFilename()));
        assertTrue(s.contains("sfx") || s.contains("sounds"));
    }

    @Test
    void bgmPath_containsFilenameAndMusicFolder() {
        Path p = locator.bgmPath(BgmId.MENU);
        assertNotNull(p);
        String s = p.toString();
        assertTrue(s.endsWith(BgmId.MENU.getFilename()));
        assertTrue(s.contains("music") || s.contains("sounds"));
    }

    @Test
    void sfxResourceUrl_resolvesClasspathResource() {
        URL url = locator.sfxResourceUrl(SfxId.PADDLE_HIT);
        assertNotNull(url);
        assertTrue(url.toString().contains(SfxId.PADDLE_HIT.getFilename()));
    }

    @Test
    void bgmResourceUrl_resolvesClasspathResource() {
        URL url = locator.bgmResourceUrl(BgmId.MENU);
        assertNotNull(url);
        assertTrue(url.toString().contains(BgmId.MENU.getFilename()));
    }
}
