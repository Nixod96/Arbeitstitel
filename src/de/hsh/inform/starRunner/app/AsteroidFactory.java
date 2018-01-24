package de.hsh.inform.starRunner.app;

/**
 * StarRunner
 * Created by Florian on 30.11.2017.
 */
class AsteroidFactory {

    private final int[][] polygonPoints = {
            {2,36,80,90,62,52},{34,2,27,53,85,83},              //länge = 6
            {1,8,36,48,34,10},{16,4,2,18,34,34},                //länge = 6
            {7,11,40,59,14,2},{8,4,3,25,42,35},                 //länge = 6
            {2,20,30,54,44,10},{12,8,2,10,28,25},               //länge = 6
            {2,9,29,66,92,97,83,24},{38,22,3,10,20,42,54,53},   //länge = 8
            {1,23,43,77,106,85,29,6},{32,14,4,2,31,63,71,55},   //länge = 8
            {3,14,44,46,20},{10,2,2,9,20},                      //länge = 5
            {2,16,24,18,4},{12,3,10,26,24},                     //länge = 5
            {2,18,83,120,130,98,35},{62,17,2,35,56,70,90},      //länge = 7
            {2,16,46,75,75,60,26},{35,12,2,8,25,37,49},         //länge = 7
    };

    /**
     * Erstellt Asteroiden für die LevelModel Klasse
     */
    public AsteroidFactory() { }

    /**
     * Erstellt einen spezifischen Asteroiden mit vorher festgelegter Größe, Geschwindigkeit und Koordinaten
     *
     * @param x     x-Achsen-Koordinate
     * @param y     y-Achsen-Koordiante
     * @param type  Größe
     * @param speed Geschwindigkeit
     * @return      Gibt den fertigen Asteroiden als Objekt zurück
     */
    public AsteroidModel createAsteroid(int x, int y, int type, int speed) {
        switch (type) {
            case(1):
                return new AsteroidModel(x, y, polygonPoints[0], polygonPoints[1], 6,2, speed, type);
            case(2):
                return new AsteroidModel(x, y, polygonPoints[2], polygonPoints[3], 6,1, speed, type);
            case(3):
                return new AsteroidModel(x, y, polygonPoints[4], polygonPoints[5], 6,1, speed, type);
            case(4):
                return new AsteroidModel(x, y, polygonPoints[6], polygonPoints[7], 6,1, speed, type);
            case(5):
                return new AsteroidModel(x, y, polygonPoints[8], polygonPoints[9], 8,2, speed, type);
            case(6):
                return new AsteroidModel(x, y, polygonPoints[10], polygonPoints[11], 8,2, speed, type);
            case(7):
                return new AsteroidModel(x, y, polygonPoints[12], polygonPoints[13], 5,1, speed, type);
            case(8):
                return new AsteroidModel(x, y, polygonPoints[14], polygonPoints[15], 5,1, speed, type);
            case(9):
                return new AsteroidModel(x, y, polygonPoints[16], polygonPoints[17], 7,2, speed, type);
            case(10):
                return new AsteroidModel(x, y, polygonPoints[18], polygonPoints[19], 7,2, speed, type);

            default:
                return null;
        }
    }
}
