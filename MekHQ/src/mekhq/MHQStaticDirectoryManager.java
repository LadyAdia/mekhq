/*
 * Copyright (c) 2020-2021 - The MegaMek Team. All Rights Reserved.
 *
 * This file is part of MekHQ.
 *
 * MekHQ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MekHQ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MekHQ. If not, see <http://www.gnu.org/licenses/>.
 */
package mekhq;

import megamek.MegaMek;
import megamek.client.ui.swing.tileset.MMStaticDirectoryManager;
import megamek.common.annotations.Nullable;
import megamek.common.icons.AbstractIcon;
import megamek.common.util.fileUtils.AbstractDirectory;
import megamek.common.util.fileUtils.DirectoryItems;
import megamek.common.util.fileUtils.ImageFileFactory;
import mekhq.campaign.force.Force;
import mekhq.gui.enums.LayeredForceIcon;
import mekhq.io.AwardFileFactory;
import org.apache.logging.log4j.LogManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Vector;

public class MHQStaticDirectoryManager extends MMStaticDirectoryManager {
    //region Variable Declarations
    private static AbstractDirectory forceIconDirectory;
    private static AbstractDirectory awardIconDirectory;
    private static AbstractDirectory storySplashDirectory;
    private static AbstractDirectory userStorySplashDirectory;
    private static AbstractDirectory userStoryPortraitDirectory;

    // Re-parsing Prevention Variables: They are True at startup and when the specified directory
    // should be re-parsed, and are used to avoid re-parsing the directory repeatedly when there's
    // an error.
    private static boolean parseForceIconDirectory = true;
    private static boolean parseAwardIconDirectory = true;
    private static boolean parseStorySplashDirectory = true;
    private static boolean parseUserStorySplashDirectory = true;
    private static boolean parseUserStoryPortraitDirectory = true;
    //endregion Variable Declarations

    //region Constructors
    protected MHQStaticDirectoryManager() {
        // This class is not to be instantiated
    }
    //endregion Constructors

    //region Initialization
    /**
     * This initializes all of the directories under this manager
     */
    public static void initialize() {
        MMStaticDirectoryManager.initialize();
        initializeForceIcons();
        initializeAwardIcons();
        initializeStorySplash();
    }

    /**
     * Parses MekHQ's force icon folder when first called or when it was refreshed.
     *
     * @see #refreshForceIcons()
     */
    private static void initializeForceIcons() {
        // Read in and parse MekHQ's force icon folder only when first called or when refreshed
        if (parseForceIconDirectory) {
            // Set parseForceIconDirectory to false to avoid parsing repeatedly when something fails
            parseForceIconDirectory = false;
            try {
                forceIconDirectory = new DirectoryItems(new File("data/images/force"), // TODO : remove inline file path
                        new ImageFileFactory());
            } catch (Exception e) {
                LogManager.getLogger().error("Could not parse the force icon directory!", e);
            }
        }
    }

    /**
     * Parses MekHQ's awards icon folder when first called or when it was refreshed.
     *
     * @see #refreshAwardIcons()
     */
    private static void initializeAwardIcons() {
        // Read in and parse MekHQ's award icon folder only when first called or when refreshed
        if (parseAwardIconDirectory) {
            // Set parseAwardIconDirectory to false to avoid parsing repeatedly when something fails
            parseAwardIconDirectory = false;
            try {
                awardIconDirectory = new DirectoryItems(new File("data/images/awards"), // TODO : remove inline file path
                        new AwardFileFactory());
            } catch (Exception e) {
                LogManager.getLogger().error("Could not parse the award icon directory!", e);
            }
        }
    }

    /**
     * Parses MekHQ's storyarcs icon folder when first called or when it was refreshed.
     *
     * @see #refreshStorySplash()
     */
    private static void initializeStorySplash() {
        // Read in and parse MekHQ's force icon folder only when first called or when refreshed
        if (parseStorySplashDirectory) {
            // Set parseForceIconDirectory to false to avoid parsing repeatedly when something fails
            parseStorySplashDirectory = false;
            try {
                storySplashDirectory = new DirectoryItems(new File("data/images/storysplash"), // TODO : remove inline file path
                        new ImageFileFactory());
            } catch (Exception e) {
                LogManager.getLogger().error("Could not parse the storyarc icon directory!", e);
            }
        }
    }

    /**
     * Parses the user's Story Arc portraits directory when first called or when it was refreshed
     *
     */
    public static void initializeUserStoryPortraits(String path) {
        // Read in and parse MekHQ's force icon folder only when first called or when refreshed
        if (parseUserStoryPortraitDirectory) {
            // Set parseForceIconDirectory to false to avoid parsing repeatedly when something fails
            parseUserStoryPortraitDirectory = false;
            try {
                userStoryPortraitDirectory = new DirectoryItems(new File(path),
                        new ImageFileFactory());
            } catch (Exception e) {
                LogManager.getLogger().error("Could not parse the storyarc portrait directory!", e);
            }
        }
    }

    /**
     * Parses the user's Story Arc storyarcs directory when first called or when it was refreshed
     *
     */
    public static void initializeUserStorySplash(String path) {
        // Read in and parse MekHQ's force icon folder only when first called or when refreshed
        if (parseUserStorySplashDirectory) {
            // Set parseForceIconDirectory to false to avoid parsing repeatedly when something fails
            parseUserStorySplashDirectory = false;
            try {
                userStorySplashDirectory = new DirectoryItems(new File(path),
                        new ImageFileFactory());
            } catch (Exception e) {
                LogManager.getLogger().error("Could not parse the storyarc directory!", e);
            }
        }
    }
    //endregion Initialization

    //region Getters
    /**
     * Returns an AbstractDirectory object containing all force icon filenames found in MekHQ's
     * force icon folder.
     * @return an AbstractDirectory object with the force icon folders and filenames.
     * May be null if the directory cannot be parsed.
     */
    public static @Nullable AbstractDirectory getForceIcons() {
        initializeForceIcons();
        return forceIconDirectory;
    }

    /**
     * Returns an AbstractDirectory object containing all award icon filenames found in MekHQ's
     * award icon folder.
     * @return an AbstractDirectory object with the award icon folders and filenames.
     * May be null if the directory cannot be parsed.
     */
    public static @Nullable AbstractDirectory getAwardIcons() {
        initializeAwardIcons();
        return awardIconDirectory;
    }

    /**
     * Returns an AbstractDirectory object containing all story icon filenames found in MekHQ's
     * storyarc icon folder.
     * @return an AbstractDirectory object with the story icon folders and filenames.
     * May be null if the directory cannot be parsed.
     */
    public static @Nullable AbstractDirectory getStorySplash() {
        initializeStorySplash();
        return storySplashDirectory;
    }

    /**
     * Returns an AbstractDirectory object containing all story portrait filenames found in the user's
     * storyarc portraits folder.
     * @return an AbstractDirectory object with the story portrait folders and filenames.
     * May be null if the directory cannot be parsed.
     */
    public static @Nullable AbstractDirectory getUserStoryPortraits() {
        //we do not initialize here because initialization requires a specific path
        return userStoryPortraitDirectory;
    }

    /**
     * Returns an AbstractDirectory object containing all story arc image filenames found in the user's
     * storyarc folder.
     * @return an AbstractDirectory object with the story portrait folders and filenames.
     * May be null if the directory cannot be parsed.
     */
    public static @Nullable AbstractDirectory getUserStorySplash() {
        //we do not initialize here because initialization requires a specific path
        return userStorySplashDirectory;
    }
    //endregion Getters

    //region Refreshers
    /**
     * Re-reads MekHQ's force icon folder and returns the updated AbstractDirectory object. This
     * will update the AbstractDirectory object with changes to the force icons (like added image
     * files and folders) while MekHQ is running.
     *
     * @see #getForceIcons()
     */
    public static AbstractDirectory refreshForceIcons() {
        parseForceIconDirectory = true;
        return getForceIcons();
    }

    /**
     * Re-reads MekHQ's award icon folder and returns the updated AbstractDirectory object. This
     * will update the AbstractDirectory object with changes to the award icons (like added image
     * files and folders) while MekHQ is running.
     *
     * @see #getAwardIcons()
     */
    public static AbstractDirectory refreshAwardIcons() {
        parseAwardIconDirectory = true;
        return getAwardIcons();
    }

    /**
     * Re-reads MekHQ's story icon folder and returns the updated AbstractDirectory object. This
     * will update the AbstractDirectory object with changes to the story icons (like added image
     * files and folders) while MekHQ is running.
     *
     * @see #getStorySplash()
     */
    public static AbstractDirectory refreshStorySplash() {
        parseStorySplashDirectory = true;
        return getStorySplash();
    }
    //endregion Refreshers

    //region Force Icon
    public static Image buildForceIcon(String category, String filename,
                                       LinkedHashMap<String, Vector<String>> iconMap) {
        Image retVal = null;

        if (AbstractIcon.ROOT_CATEGORY.equals(category)) {
            category = "";
        }

        // Return a null if the player has selected no force icon file.
        if ((null == category) || (null == filename)
                || (AbstractIcon.DEFAULT_ICON_FILENAME.equals(filename) && !Force.ROOT_LAYERED.equals(category))) {
            filename = "empty.png";
        }

        // Layered force icon
        if (Force.ROOT_LAYERED.equals(category)) {
            GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().getDefaultConfiguration();
            BufferedImage base = null;
            Graphics2D g2d = null;
            try {
                int width = 0;
                int height = 0;
                // Gather height/width
                for (LayeredForceIcon layeredForceIcon : LayeredForceIcon.getInDrawOrder()) {
                    String layer = layeredForceIcon.getLayerPath();
                    if (iconMap.containsKey(layer)) {
                        for (String value : iconMap.get(layer)) {
                            // Load up the image piece
                            BufferedImage image = (BufferedImage) getForceIcons().getItem(layer, value);
                            if (image != null) {
                                width = Math.max(image.getWidth(), width);
                                height = Math.max(image.getHeight(), height);
                            }
                        }
                    }
                }
                base = config.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
                g2d = base.createGraphics();
                for (LayeredForceIcon layeredForceIcon : LayeredForceIcon.getInDrawOrder()) {
                    String layer = layeredForceIcon.getLayerPath();
                    if (iconMap.containsKey(layer)) {
                        for (String value : iconMap.get(layer)) {
                            BufferedImage image = (BufferedImage) getForceIcons().getItem(layer, value);
                            if (image != null) {
                                // Draw the current buffered image onto the base, aligning bottom and right side
                                g2d.drawImage(image, width - image.getWidth() + 1, height - image.getHeight() + 1, null);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LogManager.getLogger().error(e);
            } finally {
                if (null != g2d) {
                    g2d.dispose();
                }
                if (null == base) {
                    try {
                        base = (BufferedImage) getForceIcons().getItem("", "empty.png");
                    } catch (Exception e) {
                        LogManager.getLogger().error(e);
                    }
                }
                retVal = base;
            }
        } else { // Standard force icon
            // Try to get the player's force icon file.
            Image scaledImage;
            try {
                scaledImage = (Image) getForceIcons().getItem(category, filename);
                if (null == scaledImage) {
                    scaledImage = (Image) getForceIcons().getItem("", "empty.png");
                }
                retVal = scaledImage;
            } catch (Exception e) {
                LogManager.getLogger().error(e);
            }
        }

        return retVal;
    }
    //endregion Force Icon
}
