package com.michaelpippolito.fantasy.mlb;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class MLBPlayer {
    private final String name;
    @Setter
    private String overviewUrl;
    @Setter
    private String splitsUrl;
    @Setter
    private double totalWAR;
    private final MLBTeam team;
    private final MLBPositionGroup positionGroup;
    @Setter
    private int plateAppearances;
    @Setter
    private double inningsPitched;
    private double totalInningsPitched;
    private int totalPlateAppearances;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MLBPlayer) {
            return ((MLBPlayer) obj).getName().equals(this.name);
        }
        return false;
    }

    /*
        For the Innings Pitched stat in baseball, the decimal value represents the number of outs out of 3 achieved in
        the next inning. For example, 6.2 Innings Pitched means that the pitcher pitched 6 innings and the first 2
        outs of the 7th inning. Therefore, 6.3 Innings Pitched == 7 Innings Pitched. We must account for this
        when summing the total innings of any pitcher.
     */
    public void updateInningsPitched(double inningsPitched) {
        String inningsPitchedString = String.format("%.1f", this.inningsPitched + inningsPitched);

        double wholeInningsPitched = Double.parseDouble(inningsPitchedString.substring(0, inningsPitchedString.indexOf(".")));
        double decimalInningsPitched = Double.parseDouble(inningsPitchedString.substring(inningsPitchedString.indexOf(".")));

        while (decimalInningsPitched >= 0.3) {
            decimalInningsPitched -= 0.3;
            wholeInningsPitched += 1;
        }

        this.inningsPitched = wholeInningsPitched + decimalInningsPitched;
    }

    public void updatePlateAppearances(int plateAppearances) { this.plateAppearances += plateAppearances; }

    public double getAdjustedWAR() {
        switch (positionGroup) {
            case ROTATION:
            case BULLPEN:
                if (totalInningsPitched == 0 || totalWAR == 0)
                    return totalWAR;
                return (inningsPitched / totalInningsPitched) * totalWAR;
            case INFIELD:
            case OUTFIELD_DH:
                if (totalPlateAppearances == 0 || totalWAR == 0)
                    return totalWAR;
                return (((double) plateAppearances) / totalPlateAppearances) * totalWAR;
            default:
                return totalWAR;
        }
    }

    public void setTotalInningsPitched(double totalInningsPitched) {
        this.totalInningsPitched = totalInningsPitched;
        if (inningsPitched > totalInningsPitched) {
            //TODO Remove this hacky fix to handle players who are traded mid-season
            this.inningsPitched = totalInningsPitched;
        }
    }

    public void setTotalPlateAppearances(int totalPlateAppearances) {
        this.totalPlateAppearances = totalPlateAppearances;
        if (totalPlateAppearances > totalPlateAppearances) {
            //TODO Remove this hacky fix to handle players who are traded mid-season
            this.plateAppearances = totalPlateAppearances;
        }
    }
}
