/*
 * Faction.java
 *
 * Copyright (C) 2009-2016 - The MegaMek Team. All Rights Reserved.
 * Copyright (c) 2009 Jay Lawson <jaylawson39 at yahoo.com>. All rights reserved.
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
package mekhq.campaign.universe;

import java.awt.Color;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import megamek.common.Compute;
import megamek.common.annotations.Nullable;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import megamek.common.EquipmentType;
import mekhq.MekHQ;
import mekhq.MekHqXmlUtil;
import mekhq.Utilities;
import mekhq.campaign.Campaign;
import mekhq.campaign.parts.Part;

/**
 *
 * @author Jay Lawson <jaylawson39 at yahoo.com>
 */
public class Faction {
    private String shortName;
    private String fullName;
    private NavigableMap<Integer, String> nameChanges = new TreeMap<>();
    private String[] altNames;
    private String[] alternativeFactionCodes;
    private Color color;
    private String nameGenerator;
    private String startingPlanet;
    private NavigableMap<LocalDate, String> planetChanges = new TreeMap<>();
    private int[] eraMods;
    private Integer id;
    private boolean playable;
    private Set<Tag> tags;
    // Start and end years (inclusive)
    private int start;
    private int end;
    private String currencyCode = ""; // Currency of the faction, if any

    public Faction() {
        this("???", "Unknown");
    }

    public Faction(String shortName, String fullName) {
        this.shortName = shortName;
        this.fullName = fullName;
        nameGenerator = "General";
        color = Color.LIGHT_GRAY;
        startingPlanet = "Terra";
        eraMods = null;
        setPlayable(false);
        tags = EnumSet.noneOf(Faction.Tag.class);
        start = 0;
        end = 9999;
    }

    public String getShortName() {
        return shortName;
    }

    public String getFullName(int year) {
        Map.Entry<Integer,String> change = nameChanges.floorEntry(year);
        if (null == change) {
            return fullName;
        } else {
            return change.getValue();
        }
    }

    public @Nullable String[] getAlternativeFactionCodes() {
        return alternativeFactionCodes;
    }

    public Optional<String> getRandomAlternativeFactionCode() {
        return (getAlternativeFactionCodes() == null) ? Optional.empty()
            : Optional.of(getAlternativeFactionCodes()[Compute.randomInt(getAlternativeFactionCodes().length)]);
    }

    public void setAlternativeFactionCodes(final String... alternativeFactionCodes) {
        this.alternativeFactionCodes = alternativeFactionCodes;
    }

    public Color getColor() {
        return color;
    }

    public boolean isClan() {
        return is(Tag.CLAN);
    }

    public boolean isComStar() {
        return "CS".equals(shortName);
    }

    public boolean isPeriphery() {
        return is(Tag.PERIPHERY);
    }

    public boolean isDeepPeriphery() {
        return is(Tag.DEEP_PERIPHERY);
    }

    public String getNameGenerator() {
        return nameGenerator;
    }

    public String getStartingPlanet(LocalDate year) {
        Map.Entry<LocalDate, String> change = planetChanges.floorEntry(year);
        if (null == change) {
            return startingPlanet;
        } else {
            return change.getValue();
        }
    }

    public int getEraMod(int year) {
        if (eraMods == null) {
            return 0;
        } else {
            if (year < 2570) {
                //Era: Age of War
                return eraMods[0];
            } else if (year < 2598) {
                //Era: RW
                return eraMods[1];
            } else if (year < 2785) {
                //Era: Star League
                return eraMods[2];
            } else if (year < 2828) {
                //Era: 1st SW
                return eraMods[3];
            } else if (year < 2864) {
                //Era: 2nd SW
                return eraMods[4];
            } else if (year < 3028) {
                //Era: 3rd SW
                return eraMods[5];
            } else if (year < 3050) {
                //Era: 4th SW
                return eraMods[6];
            } else if (year < 3067) {
                //Era: Clan Invasion
                return eraMods[7];
            } else {
                //Era: Jihad
                return eraMods[8];
            }
        }
    }

    public int getTechMod(Part part, Campaign campaign) {
        int currentYear = campaign.getGameYear();

        //TODO: This seems hacky - we shouldn't hardcode in universe details
        //like this
        int factionMod = 0;
        if ((part.getTechBase() == Part.T_CLAN) && !isClan()) {
            // Availability of clan tech for IS
            if (currentYear < 3050) {
                // Impossible to buy before clan invasion
                factionMod = 12;
            } else if (currentYear <= 3052) {
                // Between begining of clan invasiuon and tukayyid, very very hard to buy
                factionMod = 5;
            } else if (currentYear <= 3060) {
                // Between tukayyid and great refusal, very hard to buy
                factionMod = 4;
            } else {
                // After great refusal, hard to buy
                factionMod = 3;
            }
        } else if ((part.getTechBase() == Part.T_IS) && isPeriphery()) {
            // Availability of high tech rating equipment in low tech areas (periphery)
            switch (part.getTechRating()) {
                case EquipmentType.RATING_E:
                    factionMod += 1;
                    break;
                case EquipmentType.RATING_F:
                    factionMod += 2;
                    break;
            }
        }

        return factionMod;
    }

    public boolean isPlayable() {
        return playable;
    }

    public void setPlayable(final boolean playable) {
        this.playable = playable;
    }

    public boolean is(Tag tag) {
        return tags.contains(tag);
    }

    public boolean validIn(int year) {
        return (year >= start) && (year <= end);
    }

    public boolean validIn(LocalDate time) {
        return validIn(time.getYear());
    }

    public boolean validBetween(int startYear, int endYear) {
        return (startYear <= end) && (endYear >= start);
    }

    public Integer getId() {
        return id;
    }

    public int getStartYear() {
        return this.start;
    }

    public int getEndYear() {
        return this.end;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public boolean hasName(String name) {
        if (name.equals(fullName)
                || nameChanges.values().stream().anyMatch(n -> n.equals(name))) {
            return true;
        }
        if ((altNames != null) && (altNames.length > 0)) {
            for (String altName : altNames) {
                if (name.equals(altName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Faction getFactionFromXML(Node wn) throws DOMException {
        Faction retVal = new Faction();
        NodeList nl = wn.getChildNodes();

        for (int x = 0; x < nl.getLength(); x++) {
            Node wn2 = nl.item(x);
            if (wn2.getNodeName().equalsIgnoreCase("shortname")) {
                retVal.shortName = wn2.getTextContent();
            } else if (wn2.getNodeName().equalsIgnoreCase("fullname")) {
                retVal.fullName = wn2.getTextContent();
            } else if (wn2.getNodeName().equalsIgnoreCase("alternativeFactionCodes")) {
                retVal.setAlternativeFactionCodes(wn2.getTextContent().trim().split(","));
            } else if (wn2.getNodeName().equalsIgnoreCase("nameGenerator")) {
                retVal.nameGenerator = wn2.getTextContent();
            } else if (wn2.getNodeName().equalsIgnoreCase("startingPlanet")) {
                retVal.startingPlanet = wn2.getTextContent();
            } else if (wn2.getNodeName().equalsIgnoreCase("changePlanet")) {
                retVal.planetChanges.put(
                        MekHqXmlUtil.parseDate(wn2.getAttributes().getNamedItem("year").getTextContent().trim()),
                        wn2.getTextContent());
            } else if (wn2.getNodeName().equalsIgnoreCase("altNamesByYear")) {
                int year = Integer.parseInt(wn2.getAttributes().getNamedItem("year").getTextContent());
                retVal.nameChanges.put(year, wn2.getTextContent());
            } else if (wn2.getNodeName().equalsIgnoreCase("altNames")) {
                retVal.altNames = wn2.getTextContent().split(",", 0);
            } else if (wn2.getNodeName().equalsIgnoreCase("eraMods")) {
                retVal.eraMods = new int[] {0,0,0,0,0,0,0,0,0};
                String[] values = wn2.getTextContent().split(",", -2);
                for (int i = 0; i < values.length; i++) {
                    retVal.eraMods[i] = Integer.parseInt(values[i]);
                }
            } else if (wn2.getNodeName().equalsIgnoreCase("colorRGB")) {
                String[] values = wn2.getTextContent().split(",");
                if (values.length == 3) {
                    int colorRed = Integer.parseInt(values[0]);
                    int colorGreen = Integer.parseInt(values[1]);
                    int colorBlue = Integer.parseInt(values[2]);
                    retVal.color = new Color(colorRed, colorGreen, colorBlue);
                }
            } else if (wn2.getNodeName().equalsIgnoreCase("id")) {
                retVal.id = Integer.valueOf(wn2.getTextContent());
            } else if (wn2.getNodeName().equalsIgnoreCase("playable")) {
                retVal.setPlayable(true);
            } else if (wn2.getNodeName().equalsIgnoreCase("currencyCode")) {
                retVal.currencyCode = wn2.getTextContent();
            } else if (wn2.getNodeName().equalsIgnoreCase("start")) {
                retVal.start = Integer.parseInt(wn2.getTextContent());
            } else if (wn2.getNodeName().equalsIgnoreCase("end")) {
                retVal.end = Integer.parseInt(wn2.getTextContent());
            } else if (wn2.getNodeName().equalsIgnoreCase("tags")) {
                Arrays.stream(wn2.getTextContent().split(",")).map(tag -> tag.toUpperCase(Locale.ROOT))
                    .map(Tag::valueOf).forEach(tag -> retVal.tags.add(tag));
            }
        }

        if ((retVal.eraMods != null) && (retVal.eraMods.length < 9)) {
            MekHQ.getLogger().warning(retVal.fullName + " faction did not have a long enough eraMods vector");
        }

        return retVal;
    }

    /** @return Sorted list of faction names as one string */
    public static String getFactionNames(Collection<Faction> factions, int year) {
        if (null == factions) {
            return "-";
        }
        List<String> factionNames = new ArrayList<>(factions.size());
        for (Faction f : factions) {
            factionNames.add(f.getFullName(year));
        }
        Collections.sort(factionNames);
        return Utilities.combineString(factionNames, "/");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((shortName == null) ? 0 : shortName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Faction) {
            final Faction other = (Faction) obj;
            return (null != shortName) && (shortName.equals(other.shortName));
        }
        return false;
    }

    public enum Tag {
        /** Inner sphere */
        IS, PERIPHERY, DEEP_PERIPHERY, CLAN,
        /** A bunch of dirty pirates */
        PIRATE,
        /** Major mercenary bands */
        MERC,
        /** Major trading company */
        TRADER,
        /** Super Power: the Terran Hegemony, the First Star League, and the Federated Commonwealth. (CamOps p12) */
        SUPER,
        /**
         * Major Power: e.g. Inner Sphere Great Houses, Republic of the Sphere, Terran Alliance,
         * Second Star League, Inner Sphere Clans. (CamOps p12)
         */
        MAJOR,
        /** Faction is limited to a single star system, or potentially just a part of a planet (CamOps p12) */
        MINOR,
        /** Independent world or Small State (CamOps p12) */
        SMALL,
        /** Faction is rebelling against the superior ("parent") faction */
        REBEL,
        /** Faction isn't overtly acting on the political/military scale; think ComStar before clan invasion */
        INACTIVE,
        /** Faction represents empty space */
        ABANDONED,
        /** Faction represents a lack of unified government */
        CHAOS,
        /** Faction is campaign-specific, generated on the fly */
        GENERATED,
        /** Faction is hidden from view */
        HIDDEN,
        /** Faction code is not intended to be for players */
        SPECIAL
    }
}
