class Constants
{
   public static final double M_PER_KM                    =          1_000.0;
   public static final double DISTANCE_SUN_TO_EARTH_KM    = 93_000_000_000.0;
   public static final double SPEED_OF_LIGHT_KM_PER_SEC   =      3_000_000.0;
   public static final double SECONDS_IN_MINUTE           = 60;
   public static final double SECONDS_IN_HOUR             = 60.0 * SECONDS_IN_MINUTE;
   public static final double SECONDS_IN_DAY              = 24.0 * SECONDS_IN_HOUR;
   public static final double DAYS_PER_YEAR               = 365.0;
   public static final double SECONDS_IN_YEAR             = DAYS_PER_YEAR * SECONDS_IN_DAY;
   public static final double LIGHT_YEAR_IN_KM            = SPEED_OF_LIGHT_KM_PER_SEC * SECONDS_IN_YEAR;
   public static final double G_FORCE_M_PER_SEC           = 9.81;
   public static final double G_FORCE_KM_PER_SEC          = G_FORCE_M_PER_SEC / M_PER_KM;
   public static final double ALPHA_CENTAURI_LIGHT_YEARS  = 4.2;
   public static final double MAX_SPEED_COMPARED_TO_LIGHT = 0.95; // Cannot go faster than 95% speed of light.
   public static final double MAX_SPEED_KM_PER_SEC        = MAX_SPEED_COMPARED_TO_LIGHT * SPEED_OF_LIGHT_KM_PER_SEC;

}

class SpaceShip
{
   double massKg;
   double initialVelocityKmPerSec;
   double accelerationG;
   double distanceFromStartKm;
   double endingDistanceLightYears;

   double endingTravelTimeSecs;
   double endingDistanceTravelledKm;
   double endingDistanceVelocityKmPerSec;
   //double distanceTravelledLightYear;

   public SpaceShip ()
   {
      massKg                     = 0.0;
      initialVelocityKmPerSec    = 0.0;
      accelerationG              = 0.0;
      endingDistanceLightYears   = 0.0;
      //travelTimeSecs             = 0.0;
      //distanceTravelledKm        = 0.0;
      //distanceTravelledLightYear = 0.0;
   }

   public SpaceShip (double massKg,         double initialVelocityKmPerSec,
                     double accelerationG,  double distanceFromStartKm,
                     double endingDistanceLightYears)
   {
      this();
      this.massKg                   = massKg;
      this.initialVelocityKmPerSec  = initialVelocityKmPerSec;
      this.accelerationG            = accelerationG;
      this.distanceFromStartKm      = distanceFromStartKm;
      this.endingDistanceLightYears = endingDistanceLightYears;
   }

   public double getAccelerationKmPerSec ()
   {
      // F = MA
      // A = F / M
      // massKg is irrelevant here - we have been given an Acceleration G
      // which the spaceship can do (as is, with its current mass) !!
      return accelerationG * Constants.G_FORCE_KM_PER_SEC;
   }

   public double getDistanceKmAtTime (double secs)
   {
      // S = UT + 1/2 AT^2
      return //distanceFromStartKm +
             (initialVelocityKmPerSec * secs) +
             (0.5 * getAccelerationKmPerSec () * secs * secs);
   }

   public double getDistanceLightYearAtTime (double secs)
   {
      return getDistanceKmAtTime (secs) / Constants.LIGHT_YEAR_IN_KM;
   }

   public double getVelocityKmPerSecAtTime (double secs)
   {
      // V^2 = U^2 + 2 AS
      // => V = SQRT (U^2 + 2 AS)

      double velocityKmSec =
          Math.sqrt ((initialVelocityKmPerSec * initialVelocityKmPerSec) +
                     (2.0 * getAccelerationKmPerSec () * getDistanceKmAtTime (secs) ) );

      if (velocityKmSec > Constants.MAX_SPEED_KM_PER_SEC)
         velocityKmSec = Constants.MAX_SPEED_KM_PER_SEC;

      return velocityKmSec;
   }

   public double getPctSpeedOfLight (double secs)
   {
      double velocityKmSec     = getVelocityKmPerSecAtTime  (secs);

      double pctOfSpeedOfLight = 100.0 * velocityKmSec / Constants.SPEED_OF_LIGHT_KM_PER_SEC;

      return pctOfSpeedOfLight;
   }

   public double getPctOfJourney (double secs)
   {
      double distanceLY    = getDistanceLightYearAtTime (secs);

      double pctOfJourney  = 100.0 * distanceLY / endingDistanceLightYears;

      return pctOfJourney;
   }

   public String toString (double secs)
   {
      double days = secs / Constants.SECONDS_IN_DAY;

      double distanceKm        = getDistanceKmAtTime        (secs);
      double velocityKmSec     = getVelocityKmPerSecAtTime  (secs);
      double pctOfJourney      = getPctOfJourney    (secs);
      double pctOfSpeedOfLight = getPctSpeedOfLight (secs);

      String ourStr =
         String.format ("%6.0f", 1.0 * days / Constants.DAYS_PER_YEAR) + " Years   " +
         String.format ("%,24.0f", distanceKm)        + " Km  " +
         String.format ("%6.2f", pctOfJourney)        + "%    " +
         String.format ("%,14.3f", velocityKmSec)     + " Km/sec  " +
         String.format ("%6.2f", pctOfSpeedOfLight)   + "% Light" +
         "\n";

      return ourStr;
   }

   public String toStringTheWholeJourney ()
   {
      String  ourStr = "";
      double  secs        = 0.0;
      double  secsSinceLastOutput = 0.0;
      boolean journeyOver = false;

      while (journeyOver == false)
      {
         if (getPctOfJourney (secs) > 100.0)
         {
            journeyOver = true;

            ourStr += toString (secs);

            endingTravelTimeSecs           = secs;
            endingDistanceTravelledKm      = getDistanceKmAtTime        (secs);
            endingDistanceVelocityKmPerSec = getVelocityKmPerSecAtTime  (secs);
         }

         else if ((secsSinceLastOutput < 0.1) || (secsSinceLastOutput > Constants.SECONDS_IN_YEAR))
         {
            secsSinceLastOutput = 0.0;

            ourStr += toString (secs);
         }

         secsSinceLastOutput += Constants.SECONDS_IN_MINUTE;

         secs                += Constants.SECONDS_IN_MINUTE;
      }

      return ourStr;
   }

   public double getEndingTravelTimeSecs ()
   {
      return endingTravelTimeSecs;
   }

   public double getEndingDistanceTravelledKm ()
   {
      return endingDistanceTravelledKm;
   }

   public double getEndingDistanceVelocityKmPerSec ()
   {
      return endingDistanceVelocityKmPerSec;
   }

}


public class SpaceShipsToAlphaCentauri
{

   public static void main (String[] args)
   {
      System.out.println ("\n\nSpace Ship #1: Impulse Engine" +
                            "\n--------------------------------------------\n");

      SpaceShip ship1 = new SpaceShip (170_000.0, 25.0, 0.01,
                                       Constants.DISTANCE_SUN_TO_EARTH_KM,
                                       Constants.ALPHA_CENTAURI_LIGHT_YEARS);

      String ship1JourneyLog = ship1.toStringTheWholeJourney ();
      System.out.println (ship1JourneyLog);



      System.out.println ("\n\nSpace Ship #2: Solar Sail" +
                            "\n--------------------------------------------\n");

      SpaceShip ship2a = new SpaceShip (50_000.0, 265.0, 60.0,
                                       0,
                                       1_000_000_000.0 / Constants.LIGHT_YEAR_IN_KM);
      System.out.println ("Space Ship #2A: Solar Sail Acceleration Phase");
      String ship2aJourneyLog = ship2a.toStringTheWholeJourney ();
      System.out.println (ship2aJourneyLog);

      SpaceShip ship2b = new SpaceShip (50_000.0, ship2a.getEndingDistanceVelocityKmPerSec (),
                                       0.0,
                                       ship2a.getEndingDistanceTravelledKm (),
                                       Constants.ALPHA_CENTAURI_LIGHT_YEARS);
      System.out.println ("Space Ship #2B: Coasting Phase");
      String ship2bJourneyLog = ship2b.toStringTheWholeJourney ();
      System.out.println (ship2bJourneyLog);
      System.out.println ("*** Plus 2 years for initial manouvering around the Sun.");


      System.out.println ("\n\nSpace Ship #3: Solar Sail + Impulse Engine" +
                            "\n--------------------------------------------\n");

      SpaceShip ship3a = new SpaceShip (200_000.0, 195.0, 16.0,
                                       0,
                                       1_000_000_000.0 / Constants.LIGHT_YEAR_IN_KM);
      System.out.println ("Space Ship #3A: Solar Sail Acceleration Phase");
      String ship3aJourneyLog = ship3a.toStringTheWholeJourney ();
      System.out.println (ship3aJourneyLog);

      SpaceShip ship3b = new SpaceShip (170_000.0, ship3a.getEndingDistanceVelocityKmPerSec (),
                                       0.01,
                                       ship3a.getEndingDistanceTravelledKm (),
                                       Constants.ALPHA_CENTAURI_LIGHT_YEARS);
      System.out.println ("Space Ship #3B: Impulse Engine");
      String ship3bJourneyLog = ship3b.toStringTheWholeJourney ();
      System.out.println (ship3bJourneyLog);
      System.out.println ("*** Plus 2 years for initial manouvering around the Sun.");

   }
}