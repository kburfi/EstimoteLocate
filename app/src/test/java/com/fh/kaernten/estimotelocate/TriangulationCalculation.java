package com.fh.kaernten.estimotelocate;

/**
 * For unit test of iLocate - don't run this productively! Calculation is flawed!
 * Copied from iLocate (and adapted only to calculate euclidean distance)
 */
@Deprecated
public class TriangulationCalculation {

    // Berechnen des Punktes X mit Koordinaten von Beacon 1,2,3, Distanzen zwischen B1, B2 und B3 und Distanz zu Device

    @Deprecated
    public static Coordinate getPointX(Coordinate Beacon1, Coordinate Beacon2, Coordinate Beacon3,
                                       double distanceB1, double distanceB2, double distanceB3) {

        double distB1B2 = Beacon1.getEuclideanDistance(Beacon2);
        double distB2B3 = Beacon2.getEuclideanDistance(Beacon3);
        double distB1B3 = Beacon1.getEuclideanDistance(Beacon3);

        // Berechnung der Winkel f?r das Dreieck von Beacon1 und Beacon2 (Cosinussatz)

        double alphaB1B2 = StrictMath.cos((Square(distanceB2) + Square(distB1B2) - Square(distanceB1)) / (2 * distanceB2 * distB1B2));
        double betaB1B2 = StrictMath.cos((Square(distanceB1) + Square(distB1B2) - Square(distanceB2)) / (2 * distanceB1 * distB1B2));
        double gammaB1B2 = StrictMath.cos((Square(distanceB1) + Square(distanceB2) - Square(distB1B2)) / (2 * distanceB1 * distanceB2));

        //double alphaB1B2 = StrictMath.acos((Square(distanceB2) + Square(distB1B2) - Square(distanceB1))/(2*distanceB2*distB1B2));
        //double betaB1B2 = StrictMath.acos((Square(distanceB1) + Square(distB1B2) - Square(distanceB2))/(2*distanceB1*distB1B2));
        //double gammaB1B2 = StrictMath.acos((Square(distanceB1) + Square(distanceB2) - Square(distB1B2))/(2*distanceB1*distanceB2));

        // Gamma1 und Gamma 2 ausrechnen
        double gamma1B1B2 = 180 - 90 - betaB1B2;
        double gamma2B1B2 = 180 - 90 - alphaB1B2;

        // c1 und c2 ausrechnen, c1 = Distanz von B1 bis H?lfte c2 = Distanz von B2 bis H?lfte
        double c1B1B2 = distanceB1 * StrictMath.sin(gamma1B1B2);
        double c2B1B2 = distanceB2 * StrictMath.sin(gamma2B1B2);

        //x1 = a*sin(Beta), x2 = b*sin(Alpha)
        double x1B1B2 = distanceB1 * StrictMath.sin(betaB1B2);
        double x2B1B2 = distanceB2 * StrictMath.sin(alphaB1B2);

        // c1 und c2 umrechhnen in Pixel. C1(px) und c2(px) sind die Abst?nde von den Beacons aus gesehen
        // 0.583 und 0,6 kommt von zb ImageView Room ist width 290 = 300cm , height 307px = 200 cm.
        double c1InPxB1B2 = c1B1B2 * 1.03;
        double c2InPxB1B2 = c2B1B2 * 1.03;

        double x1InPxB1B2 = x1B1B2 * 0.65;
        double x2InPxB1B2 = x2B1B2 * 0.65;

        //Neue x,y Koordinaten von User (B1.x +c1, Beacon1. y + x1) c1, x1 in Pixel
        Beacon1.X = Beacon1.X + c1InPxB1B2;
        Beacon1.Y = Beacon1.Y + x1InPxB1B2;
        Beacon2.X = Beacon2.X - c2InPxB1B2;
        Beacon2.Y = Beacon2.Y + x2InPxB1B2;


        // F?r Beacon 2 und Beacon3
        //double alphaB2B3 = StrictMath.acos((Square(distanceB3) + Square(distB2B3) - Square(distanceB2))/(2*distanceB3*distB2B3));
        //double betaB2B3 = StrictMath.acos((Square(distanceB2) + Square(distB2B3) - Square(distanceB3))/(2*distanceB2*distB2B3));
        //double gammaB2B3 = StrictMath.acos((Square(distanceB2) + Square(distanceB3) - Square(distB2B3))/(2*distanceB2*distanceB3));

        double alphaB2B3 = StrictMath.cos((Square(distanceB3) + Square(distB2B3) - Square(distanceB2)) / (2 * distanceB3 * distB2B3));
        double betaB2B3 = StrictMath.cos((Square(distanceB2) + Square(distB2B3) - Square(distanceB3)) / (2 * distanceB2 * distB2B3));
        double gammaB2B3 = StrictMath.cos((Square(distanceB2) + Square(distanceB3) - Square(distB2B3)) / (2 * distanceB2 * distanceB3));

        double gamma1B2B3 = 180 - 90 - betaB2B3;
        double gamma2B2B3 = 180 - 90 - alphaB2B3;
        double c1B2B3 = distanceB2 * StrictMath.sin(gamma1B2B3);
        double c2B2B3 = distanceB3 * StrictMath.sin(gamma2B2B3);

        double x1B2B3 = distanceB2 * StrictMath.sin(betaB2B3);
        double x2B2B3 = distanceB3 * StrictMath.sin(alphaB2B3);

        double c1InPxB2B3 = c1B2B3 * 1.03;
        double c2InPxB2B3 = c2B2B3 * 1.03;

        double x1InPxB2B3 = x1B2B3 * 0.65;
        double x2InPxB2B3 = x2B2B3 * 0.65;

        Beacon2.X = Beacon2.X + c1InPxB2B3;
        Beacon2.Y = Beacon2.Y + x1InPxB2B3;
        Beacon3.X = Beacon3.X - c2InPxB2B3;
        Beacon3.Y = Beacon3.Y + x2InPxB2B3;

        // F?r Beacon 1 und Beacon3
        //double alphaB1B3 = StrictMath.acos((Square(distanceB1) + Square(distB1B3) - Square(distanceB3))/(2*distanceB1*distB1B3));
        //double betaB1B3 = StrictMath.acos((Square(distanceB3) + Square(distB1B3) - Square(distanceB1))/(2*distanceB3*distB1B3));
        //double gammaB1B3 = StrictMath.acos((Square(distanceB3) + Square(distanceB1) - Square(distB1B3))/(2*distanceB3*distanceB1));

        double alphaB1B3 = StrictMath.cos((Square(distanceB1) + Square(distB1B3) - Square(distanceB3)) / (2 * distanceB1 * distB1B3));
        double betaB1B3 = StrictMath.cos((Square(distanceB3) + Square(distB1B3) - Square(distanceB1)) / (2 * distanceB3 * distB1B3));
        double gammaB1B3 = StrictMath.cos((Square(distanceB3) + Square(distanceB1) - Square(distB1B3)) / (2 * distanceB3 * distanceB1));

        double gamma1B1B3 = 180 - 90 - betaB1B2;
        double gamma2B1B3 = 180 - 90 - alphaB1B2;

        double c1B1B3 = distanceB3 * StrictMath.sin(gamma1B1B3);
        double c2B1B3 = distanceB1 * StrictMath.sin(gamma2B1B3);

        double x1B1B3 = distanceB3 * StrictMath.sin(betaB1B3);
        double x2B1B3 = distanceB1 * StrictMath.sin(alphaB1B3);

        double c1InPxB1B3 = c1B1B3 * 1.03;
        double c2InPxB1B3 = c2B1B3 * 1.03;
        double x1InPxB1B3 = x1B1B3 * 0.65;
        double x2InPxB1B3 = x2B1B3 * 0.65;

        Beacon1.X = Beacon1.X - c1InPxB1B3;
        Beacon1.Y = Beacon1.Y + x1InPxB1B3;
        Beacon3.X = Beacon3.X + c2InPxB1B3;
        Beacon3.Y = Beacon3.Y + x2InPxB1B3;

        // Mittelwert berechnen
        double x = (Beacon1.X + Beacon2.X + Beacon3.X) / 3;
        double y = (Beacon1.Y + Beacon2.Y + Beacon3.Y) / 3;


        // Neue Coordinaten mit Position vom User
        Coordinate result = new Coordinate(x, y);

        return result;
    }

    public static double Square(double x) {
        return Math.pow(x, 2);
    }
}
