package org.arkanoid.audio;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for locating audio resources.
 * Provides paths to SFX and BGM files based on their IDs.
 */
public class ResourceLocator {
    private static final Logger logger = LogManager.getLogger(ResourceLocator.class);
    private static final String SFX_ROOT = "/sounds/sfx/";
    private static final String BGM_ROOT = "/sounds/music/";
    
    /**
     * Gets the path for a sound effect file.
     * 
     * @param id The SFX identifier
     * @return Path to the SFX file
     */
    public Path sfxPath(SfxId id) {
        return Paths.get(SFX_ROOT + id.getFilename());
    }
    
    /**
     * Gets the path for a background music file.
     * 
     * @param id The BGM identifier
     * @return Path to the BGM file
     */
    public Path bgmPath(BgmId id) {
        return Paths.get(BGM_ROOT + id.getFilename());
    }

    /**
     * Common resolver for the resource URL for a given root and filename.
     * @param root The root path
     * @param filename The resource filename
     * @return The resolved resource URL as a string
     */
    private URL resolveResourceUrl(String root, String filename) {
        String resource = root + filename;
        java.net.URL url = getClass().getResource(resource);
        if (url == null) {
            String loaderResource = resource.startsWith("/") ? resource.substring(1) : resource;
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl != null) {
                url = cl.getResource(loaderResource);
            }
            if (url == null) {
                url = ResourceLocator.class.getClassLoader().getResource(loaderResource);
            }
        }
        if (url == null) {
            logger.error("Resource not found on classpath: {}", resource);
            throw new IllegalArgumentException("Resource not found on classpath: " + resource);
        }
        return url;
    }
    
    /**
     * Gets the resource URL for a sound effect file.
     * 
     * @param id The SFX identifier
     * @return Resource URL as string
     */
    public URL sfxResourceUrl(SfxId id) {
        return resolveResourceUrl(SFX_ROOT, id.getFilename());
    }
    
    /**
     * Gets the resource URL for a background music file.
     * 
     * @param id The BGM identifier
     * @return Resource URL as string
     */
    public URL bgmResourceUrl(BgmId id) {
        return resolveResourceUrl(BGM_ROOT, id.getFilename());
    }
}