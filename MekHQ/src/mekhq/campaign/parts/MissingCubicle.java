/*
 * Copyright (c) 2017-2021 - The MegaMek Team. All Rights Reserved.
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
package mekhq.campaign.parts;

import megamek.common.BayType;
import megamek.common.Entity;
import megamek.common.ITechnology;
import megamek.common.annotations.Nullable;
import megamek.logging.MMLogger;
import mekhq.campaign.Campaign;
import mekhq.utilities.MHQXMLUtility;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;
import java.util.Objects;

/**
 * @author Neoancient
 */
public class MissingCubicle extends MissingPart {
    private static final MMLogger logger = MMLogger.create(MissingCubicle.class);

    private BayType bayType;

    public MissingCubicle() {
        this(0, null, null);
    }

    public MissingCubicle(int tonnage, BayType bayType, Campaign c) {
        super(tonnage, false, c);
        this.bayType = bayType;
        if (null != bayType) {
            name = bayType.getDisplayName() + " Cubicle";
        }
    }

    @Override
    public String getName() {
        if (null != parentPart) {
            return parentPart.getName() + " Cubicle";
        }
        return super.getName();
    }

    @Override
    public int getBaseTime() {
        return 3360; // one week
    }

    @Override
    public void updateConditionFromPart() {
        // TODO Auto-generated method stub
    }

    @Override
    public int getLocation() {
        return Entity.LOC_NONE;
    }

    @Override
    public @Nullable String checkFixable() {
        return null;
    }

    @Override
    public int getDifficulty() {
        return -1;
    }

    @Override
    public void fix() {
        Part replacement = findReplacement(false);
        if (null != replacement) {
            Part actualReplacement = replacement.clone();
            unit.addPart(actualReplacement);
            campaign.getQuartermaster().addPart(actualReplacement, 0);
            replacement.decrementQuantity();
            remove(false);
            if (null != parentPart) {
                parentPart.addChildPart(actualReplacement);
                parentPart.updateConditionFromPart();
            }
        }
    }

    @Override
    public boolean isAcceptableReplacement(Part part, boolean refit) {
        return (part instanceof Cubicle)
                && (((Cubicle) part).getBayType() == bayType);
    }

    @Override
    public Part getNewPart() {
        return new Cubicle(getUnitTonnage(), bayType, campaign);
    }

    @Override
    public double getTonnage() {
        return bayType.getWeight();
    }

    @Override
    protected void loadFieldsFromXmlNode(Node wn) {
        NodeList nl = wn.getChildNodes();

        for (int x = 0; x < nl.getLength(); x++) {
            Node wn2 = nl.item(x);
            if (wn2.getNodeName().equalsIgnoreCase("bayType")) {
                // <50.01 compatibility handler
                String bayRawValue = wn2.getTextContent();

                if (Objects.equals(bayRawValue, "MECH")) {
                    bayRawValue = "MEK";
                }

                if (Objects.equals(bayRawValue, "PROTOMECH")) {
                    bayRawValue = "PROTOMEK";
                }

                bayType = BayType.parse(bayRawValue);
                if (null == bayType) {
                    logger.error(String.format("Could not parse bay type %s treating as BayType.Mek",
                        wn2.getTextContent()));
                    bayType = BayType.MEK;
                }
                name = bayType.getDisplayName() + " Cubicle";
            }
        }
    }

    @Override
    public int writeToXMLBegin(final PrintWriter pw, int indent) {
        indent = super.writeToXMLBegin(pw, indent);
        MHQXMLUtility.writeSimpleXMLTag(pw, indent, "bayType", bayType.name());
        return indent;
    }

    @Override
    public String getLocationName() {
        return null;
    }

    @Override
    public ITechnology getTechAdvancement() {
        return bayType;
    }

}
