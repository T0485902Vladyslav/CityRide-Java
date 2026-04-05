# IY4113 Milestone 1 Part 2

| Assessment Details | Please Complete All Details                                      |
| ------------------ | ---------------------------------------------------------------- |
| Group              | A                                                                |
| Module Title       | Applied Software Engineering using Object Orientated Programming |
| Assessment Type    | java fundamentals part 2                                         |
| Module Tutor Name  | Jonathan Shore                                                   |
| Student ID Number  | P485902                                                          |
| Date of Submission | 06/04/2026                                                       |
| GitHub Link        | https://github.com/T0485902Vladyslav/IY4113_Java_T0485902_Part_2 |

- [x] *I confirm that this assignment is my own work. Where I have referred to academic sources, I have provided in-text citations and included the sources in
  the final reference list.*

- [x] *Where I have used AI, I have cited and referenced appropriately.

------------------------------------------------------------------------------------------------------------------------------

### Research

------------------------------------------------------------------------------------------------------------------------------

Title of research: java LocalTime and isBefore() method

Reference (link): https://docs.oracle.com/javase/10/docs/api/java/time/LocalTime.html#isBefore(java.time.LocalTime)

How does the research help with coding practise?:

The Oracle documentation explained how LocalTime works and which methods are available. This helped me understand how to compare two time values using the isBefore() method, rather than manually converting the times to numerical values. 

Key coding ideas you could reuse in your program:

Method isBefore() for time comparison.

Screenshot of research:

![gdf](/Users/dushesssx/Desktop/Screenshot%202026-04-02%20at%2022.32.17.png)

------------------------------------------------------------------------------------------------------------------------------

Title of research: java LocalTime.of() method

Reference (link): https://docs.oracle.com/javase/10/docs/api/java/time/LocalTime.html#of(int,int)

How does the research help with coding practise?:

The Oracle documentation also showed how to create a time value using LocalTime.of(hour, minute), which can use to set the default peak start and end times in SystemConfig.

Key coding ideas you could reuse in your program:

Method LocalTime.of(hour, minute)  to set time values.

Screenshot of research:

![fd](/Users/dushesssx/Desktop/Screenshot%202026-04-02%20at%2022.49.16.png)

---

Title of research: JSON Serializers and Deserializers.

Reference (link): https://github.com/google/gson/blob/main/UserGuide.md#custom-serialization-and-deserialization

How does the research help with coding practise?:

The Gson User Guide explained how to handle types that Gson cannot handle automatically by writing a custom adapter. This helped me understand how to save a LocalTime field to a JSON file by converting it to a string, which I applied in the LocalTimeAdapter class.

Key coding ideas you could reuse in your program:

- JsonSerializer: converts a Java object to a JSON element when saving to a file
- JsonDeserializer: converts a JSON element back to a Java object when reading from a file
- registerTypeAdapter(): tells Gson which adapter to use for a specific type

Screenshot of research:

![gdf](/Users/dushesssx/Desktop/Screenshot%202026-04-05%20at%2023.01.35.png)

![gf](/Users/dushesssx/Desktop/Screenshot%202026-04-05%20at%2023.01.50.png)

---

### Program code

---

```java
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;
import java.time.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializationContext;

class Journey{
    private int journeyID;
    private LocalDate date;
    private int fromZone;
    private int toZone;
    private TimeBand timeBand;
    private PassengerType passengerType;
    private int zonesCrossed;
    private BigDecimal baseFare;
    private BigDecimal discountedFare;
    private BigDecimal chargedFare;
    private LocalTime time;

    public Journey(int journeyID, LocalDate date, LocalTime time, int fromZone, int toZone,
                   TimeBand timeBand, PassengerType passengerType,
                   int zonesCrossed, BigDecimal baseFare,
                   BigDecimal discountedFare, BigDecimal chargedFare) {

        this.journeyID = journeyID;
        this.date = date;
        this.time = time;
        this.fromZone = fromZone;
        this.toZone = toZone;
        this.timeBand = timeBand;
        this.passengerType = passengerType;
        this.zonesCrossed = zonesCrossed;
        this.baseFare = baseFare;
        this.discountedFare = discountedFare;
        this.chargedFare = chargedFare;
    }

    public int getJourneyID() {
        return journeyID;
    }
    public LocalDate getDate() {
        return date;
    }
    public int getFromZone() {
        return fromZone;
    }
    public int getToZone() {
        return toZone;
    }
    public TimeBand getTimeBand() {
        return timeBand;
    }
    public PassengerType getPassengerType() {
        return passengerType;
    }
    public int getZonesCrossed() {
        return zonesCrossed;
    }
    public BigDecimal getBaseFare() {
        return baseFare;
    }
    public BigDecimal getDiscountedFare() {
        return discountedFare;
    }
    public BigDecimal getChargedFare() {
        return chargedFare;
    }
    public LocalTime getTime() {
        return time;
    }

    public String toString() {
        return "Journey #" + journeyID + "\n" +
                "Date: " + date + "\n" +
                "Time: " + time + "\n" +
                "From Zone: " + fromZone + "\n" +
                "To Zone: " + toZone + "\n" +
                "Time Band: " + timeBand + "\n" +
                "Passenger Type: " + passengerType + "\n" +
                "Zones Crossed: " + zonesCrossed + "\n" +
                "Base Fare: £" + baseFare.setScale(2, RoundingMode.HALF_UP).toPlainString() + "\n" +
                "Discounted Fare: £" + discountedFare.setScale(2, RoundingMode.HALF_UP).toPlainString() + "\n" +
                "Charged Fare: £" + chargedFare.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

}

class JourneyManagement {
    private List<Journey> journeys;
    private Map<PassengerType, PassengerTotals> passengerTotals;
    private FareCalculator fareCalculator;

    public BigDecimal getRunningTotal(PassengerType passengerType) {
        return passengerTotals.get(passengerType).getChargedTotal();
    }

    public JourneyManagement(FareCalculator fareCalculator) {
        this.fareCalculator = fareCalculator;
        this.journeys = new ArrayList<>();
        this.passengerTotals = new HashMap<>();
        for (PassengerType type : PassengerType.values()) {
            this.passengerTotals.put(type, new PassengerTotals(type));
        }
    }


    public void addJourney(Journey journey) {
        journeys.add(journey);
        passengerTotals.get(journey.getPassengerType()).addJourney(journey.getBaseFare(), journey.getDiscountedFare(), journey.getChargedFare());
    }

    private void recalculateAll() {
        for (PassengerType type : PassengerType.values()) {
            passengerTotals.put(type, new PassengerTotals(type));
        }

        BigDecimal adultRunning = BigDecimal.ZERO;
        BigDecimal studentRunning = BigDecimal.ZERO;
        BigDecimal childRunning = BigDecimal.ZERO;
        BigDecimal seniorRunning = BigDecimal.ZERO;

        for (int i = 0; i < journeys.size(); i++) {
            Journey old = journeys.get(i);
            BigDecimal running;

            if (old.getPassengerType() == PassengerType.ADULT) {
                running = adultRunning;
            } else if (old.getPassengerType() == PassengerType.STUDENT) {
                running = studentRunning;
            } else if (old.getPassengerType() == PassengerType.CHILD) {
                running = childRunning;
            } else {
                running = seniorRunning;
            }

            Journey rebuilt = fareCalculator.createJourney(old.getJourneyID(), old.getDate(), old.getTime(),
                    old.getFromZone(), old.getToZone(), old.getPassengerType(), running);

            journeys.set(i, rebuilt);

            if (rebuilt.getPassengerType() == PassengerType.ADULT) {
                adultRunning = adultRunning.add(rebuilt.getChargedFare());
            } else if (rebuilt.getPassengerType() == PassengerType.STUDENT) {
                studentRunning = studentRunning.add(rebuilt.getChargedFare());
            } else if (rebuilt.getPassengerType() == PassengerType.CHILD) {
                childRunning = childRunning.add(rebuilt.getChargedFare());
            } else {
                seniorRunning = seniorRunning.add(rebuilt.getChargedFare());
            }

            passengerTotals.get(rebuilt.getPassengerType())
                    .addJourney(rebuilt.getBaseFare(), rebuilt.getDiscountedFare(), rebuilt.getChargedFare());
        }
    }

    public Journey removeJourney(int journeyID) {
        Journey removedJourney = null;
        for (int i = 0; i < journeys.size(); i++) {
            Journey journey = journeys.get(i);
            if (journey.getJourneyID() == journeyID) {
                removedJourney = journey;
                journeys.remove(i);
                recalculateAll();
                break;
            }
        }
        return removedJourney;
    }

    public List<Journey> filterByPassengerType(PassengerType passengerType) {
        List<Journey> filtered = new ArrayList<>();
        for (Journey journey : journeys) {
            if (journey.getPassengerType() == passengerType) {
                filtered.add(journey);
            }
        }
        return filtered;
    }

    public List<Journey> filterByTimeBand(TimeBand timeBand) {
        List<Journey> filtered = new ArrayList<>();
        for (Journey journey : journeys) {
            if (journey.getTimeBand() == timeBand) {
                filtered.add(journey);
            }
        }
        return filtered;
    }

    public List<Journey> filterByZones(int zone) {
        List<Journey> filtered = new ArrayList<>();
        for (Journey journey : journeys) {
            if (journey.getFromZone() == zone || journey.getToZone() == zone) {
                filtered.add(journey);
            }
        }
        return filtered;
    }

    public List<Journey> filterByDate(LocalDate date) {
        List<Journey> filtered = new ArrayList<>();
        for (Journey journey : journeys) {
            if (journey.getDate().equals(date)) {
                filtered.add(journey);
            }
        }
        return filtered;
    }

    public List<Journey> getDailySummary() {
        return new ArrayList<>(journeys);
    }

    public List<Journey> getAllJourneys() {
        return new ArrayList<>(journeys);
    }

    public Map<PassengerType, PassengerTotals> getPassengerTotals() {
        return new HashMap<>(passengerTotals);
    }

    public void resetDay() {
        journeys.clear();

        for (PassengerType type : PassengerType.values()) {
            passengerTotals.put(type, new PassengerTotals(type));
        }
    }

}

enum PassengerType{
    ADULT,
    STUDENT,
    CHILD,
    SENIOR_CITIZEN
}
enum TimeBand{
    PEAK,
    OFF_PEAK
}

class PassengerTotals {
    private PassengerType passengerType;
    private int journeyCount;
    private BigDecimal preDiscountedTotal;
    private BigDecimal discountedTotal;
    private BigDecimal chargedTotal;
    private boolean capReached;

    public PassengerTotals(PassengerType passengerType) {
        this.passengerType = passengerType;
        this.journeyCount = 0;
        this.preDiscountedTotal = BigDecimal.ZERO;
        this.discountedTotal = BigDecimal.ZERO;
        this.chargedTotal = BigDecimal.ZERO;
        this.capReached = false;
    }

    public void addJourney(BigDecimal baseFare, BigDecimal discountedFare, BigDecimal chargedFare) {
        journeyCount++;
        this.preDiscountedTotal = preDiscountedTotal.add(baseFare);
        this.discountedTotal = discountedTotal.add(discountedFare);
        this.chargedTotal = chargedTotal.add(chargedFare);

        if (chargedFare.compareTo(BigDecimal.ZERO) == 0) {
            capReached = true;
        }
    }

    public BigDecimal getChargedTotal() {
        return chargedTotal;
    }

    public int getJourneyCount() {
        return journeyCount;
    }

    public String printPassengerTotals() {
        return "----" + passengerType + "----\n" +
                "Journeys: " + journeyCount + "\n" +
                "PreDiscounted total: £" + preDiscountedTotal + "\n" +
                "Discounted total: £" + discountedTotal + "\n" +
                "Charged total: £" + chargedTotal + "\n" +
                "Cap reached: " + (capReached ? "Yes" : "No") + "\n";
    }
}

class DailySummary {
    private List<Journey> journeys;

    public DailySummary(List<Journey> journeys) {
        this.journeys = journeys;
    }

    public int getTotalJourneys() {
        return journeys.size();
    }

    public BigDecimal getTotalCost() {
        BigDecimal total = BigDecimal.ZERO;
        for (Journey journey : journeys) {
            total = total.add(journey.getChargedFare());
        }
        return total;
    }

    public BigDecimal getAverageCost() {
        BigDecimal average = BigDecimal.ZERO;
        if (!journeys.isEmpty()) {
            average = getTotalCost().divide(new BigDecimal(journeys.size()),2, RoundingMode.HALF_UP);
        }
        return average;
    }

    public Journey getMostExpensiveJourney() {
        Journey mostExpensive = null;
        if  (!journeys.isEmpty()) {
            mostExpensive = journeys.get(0);
            for (Journey journey : journeys) {
                if(journey.getChargedFare().compareTo(mostExpensive.getChargedFare()) > 0) {
                    mostExpensive = journey;
                }
            }

        }
        return mostExpensive;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("============================\n");
        sb.append("  Daily Summary: \n");
        sb.append("============================\n");
        sb.append("Total Journeys:  ").append(getTotalJourneys()).append("\n");
        sb.append("Total Cost:      £").append(getTotalCost().setScale(2, RoundingMode.HALF_UP)).append("\n");
        sb.append("Average Cost:    £").append(getAverageCost().setScale(2, RoundingMode.HALF_UP)).append("\n");

        Journey mostExpensive = getMostExpensiveJourney();

        if(mostExpensive != null) {
            sb.append("Most expensive: Journey #").append(mostExpensive.getJourneyID()).append(" £").append(mostExpensive.getChargedFare().setScale(2, RoundingMode.HALF_UP)).append("\n");

        }
        return sb.toString();
    }

}

class FareCalculator{

    private SystemConfig config;

    public FareCalculator(SystemConfig config) {
        this.config = config;
    }

    // Applies the passenger discount to the base fare
    public BigDecimal calculateDiscountedFare(BigDecimal baseFare, PassengerType passengerType) {
        BigDecimal discountedRate = config.getDiscountRate(passengerType);
        BigDecimal discountAmount = baseFare.multiply(discountedRate);
        return baseFare.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
    }

    // Applies the daily cap rule: once the passenger reaches the cap, further journeys cost £0.
    public BigDecimal calculateChargedFare(BigDecimal discountedFare, PassengerType passengerType, BigDecimal runningTotal) {
        BigDecimal cap =  config.getDailyCap(passengerType);
        BigDecimal chargedFare;

        if (runningTotal.compareTo(cap) >= 0) {
            chargedFare = BigDecimal.ZERO;
        }else if(runningTotal.add(discountedFare).compareTo(cap) > 0){
            chargedFare = cap.subtract(runningTotal);
        }else{
            chargedFare = discountedFare;
        }

        return chargedFare.setScale(2, RoundingMode.HALF_UP);
    }

    public Journey createJourney(int journeyID, LocalDate date, LocalTime time, int fromZone, int toZone, PassengerType passengerType, BigDecimal runningTotal){
        int zonesCrossed = Math.abs(toZone - fromZone) + 1;
        TimeBand timeBand = config.determineTimeBand(time);

        BigDecimal baseFare = config.getBaseFare(fromZone, toZone, timeBand);
        BigDecimal discountedFare = calculateDiscountedFare(baseFare, passengerType);
        BigDecimal chargedFare = calculateChargedFare(discountedFare, passengerType, runningTotal);

        return new Journey(journeyID, date, time, fromZone, toZone, timeBand, passengerType, zonesCrossed, baseFare, discountedFare, chargedFare);
    }
}

class SystemConfig{
    public static final int MIN_ZONE = 1;
    public static final int MAX_ZONE = 5;
    private Map<String, BigDecimal> baseFares;
    private Map<PassengerType, BigDecimal> discountRates;
    private Map<PassengerType, BigDecimal> dailyCaps;
    private LocalTime peakStart;
    private LocalTime peakEnd;

    public SystemConfig(){
        loadDefaults();
    }

    private void loadDefaultDiscounts(){
        discountRates = new HashMap<>();
        discountRates.put(PassengerType.ADULT, new BigDecimal("0.00"));
        discountRates.put(PassengerType.STUDENT, new BigDecimal("0.25"));
        discountRates.put(PassengerType.CHILD, new BigDecimal("0.50"));
        discountRates.put(PassengerType.SENIOR_CITIZEN, new BigDecimal("0.30"));
    }
    private void loadDefaultDailyCaps(){
        dailyCaps = new HashMap<>();
        dailyCaps.put(PassengerType.ADULT, new BigDecimal("8.00"));
        dailyCaps.put(PassengerType.STUDENT, new BigDecimal("6.00"));
        dailyCaps.put(PassengerType.CHILD, new BigDecimal("4.00"));
        dailyCaps.put(PassengerType.SENIOR_CITIZEN, new BigDecimal("7.00"));
    }

    //A helper method don't write new BigDecimal(amount) every time
    private void putBaseFare(String key, String amount){
        baseFares.put(key, new BigDecimal(amount));
    }

    private void loadDefaultBaseFares() {
        baseFares = new HashMap<>();

        // Peak fares
        putBaseFare("1-1-PEAK", "2.50"); putBaseFare("1-2-PEAK", "3.20");
        putBaseFare("1-3-PEAK", "3.80"); putBaseFare("1-4-PEAK", "4.40");
        putBaseFare("1-5-PEAK", "5.00");

        putBaseFare("2-1-PEAK", "3.20"); putBaseFare("2-2-PEAK", "2.30");
        putBaseFare("2-3-PEAK", "3.10"); putBaseFare("2-4-PEAK", "3.80");
        putBaseFare("2-5-PEAK", "4.50");

        putBaseFare("3-1-PEAK", "3.80"); putBaseFare("3-2-PEAK", "3.10");
        putBaseFare("3-3-PEAK", "2.10"); putBaseFare("3-4-PEAK", "3.00");
        putBaseFare("3-5-PEAK", "3.70");

        putBaseFare("4-1-PEAK", "4.40"); putBaseFare("4-2-PEAK", "3.80");
        putBaseFare("4-3-PEAK", "3.00"); putBaseFare("4-4-PEAK", "2.00");
        putBaseFare("4-5-PEAK", "2.90");

        putBaseFare("5-1-PEAK", "5.00"); putBaseFare("5-2-PEAK", "4.50");
        putBaseFare("5-3-PEAK", "3.70"); putBaseFare("5-4-PEAK", "2.90");
        putBaseFare("5-5-PEAK", "1.90");

        // Off-peak fares
        putBaseFare("1-1-OFF_PEAK", "2.00"); putBaseFare("1-2-OFF_PEAK", "2.70");
        putBaseFare("1-3-OFF_PEAK", "3.20"); putBaseFare("1-4-OFF_PEAK", "3.70");
        putBaseFare("1-5-OFF_PEAK", "4.20");

        putBaseFare("2-1-OFF_PEAK", "2.70"); putBaseFare("2-2-OFF_PEAK", "1.90");
        putBaseFare("2-3-OFF_PEAK", "2.60"); putBaseFare("2-4-OFF_PEAK", "3.20");
        putBaseFare("2-5-OFF_PEAK", "3.80");

        putBaseFare("3-1-OFF_PEAK", "3.20"); putBaseFare("3-2-OFF_PEAK", "2.60");
        putBaseFare("3-3-OFF_PEAK", "1.70"); putBaseFare("3-4-OFF_PEAK", "2.50");
        putBaseFare("3-5-OFF_PEAK", "3.10");

        putBaseFare("4-1-OFF_PEAK", "3.70"); putBaseFare("4-2-OFF_PEAK", "3.20");
        putBaseFare("4-3-OFF_PEAK", "2.50"); putBaseFare("4-4-OFF_PEAK", "1.60");
        putBaseFare("4-5-OFF_PEAK", "2.40");

        putBaseFare("5-1-OFF_PEAK", "4.20"); putBaseFare("5-2-OFF_PEAK", "3.80");
        putBaseFare("5-3-OFF_PEAK", "3.10"); putBaseFare("5-4-OFF_PEAK", "2.40");
        putBaseFare("5-5-OFF_PEAK", "1.50");
    }

    public void loadDefaults(){
        peakStart = LocalTime.of(7,0);
        peakEnd = LocalTime.of(19,0);
        loadDefaultDiscounts();
        loadDefaultDailyCaps();
        loadDefaultBaseFares();
    }

    public TimeBand determineTimeBand(LocalTime time) {
        TimeBand result;
        if (time.isBefore(peakStart) || !time.isBefore(peakEnd)) {
            result = TimeBand.OFF_PEAK;
        } else {
            result = TimeBand.PEAK;
        }
        return result;
    }

    public BigDecimal getBaseFare(int fromZone, int toZone, TimeBand timeBand) {
        String key = fromZone + "-" + toZone +  "-" + timeBand.name();
        return baseFares.get(key);
    }

    public BigDecimal getDiscountRate(PassengerType passengerType) {
        return discountRates.get(passengerType);
    }

    public BigDecimal getDailyCap(PassengerType passengerType) {
        return dailyCaps.get(passengerType);
    }

    public LocalTime getPeakStart() {
        return peakStart;
    }

    public LocalTime getPeakEnd(){
        return peakEnd;
    }

    public Map<String, BigDecimal> getBaseFares() {
        return baseFares;
    }

    public Map<PassengerType, BigDecimal> getDiscountRates() {
        return discountRates;
    }

    public Map<PassengerType, BigDecimal> getDailyCaps() {
        return dailyCaps;
    }

    public void setPeakStart(LocalTime peakStart) {
        this.peakStart = peakStart;
    }

    public void setPeakEnd(LocalTime peakEnd) {
        this.peakEnd = peakEnd;
    }

    public void setBaseFares(int fromZone, int toZone, TimeBand timeBand, BigDecimal fare) {
        String key = fromZone + "-" + toZone +  "-" + timeBand.name();
        baseFares.put(key, fare);
    }

    public void setDiscountRates(PassengerType type, BigDecimal rate) {
        discountRates.put(type, rate);
    }

    public void setDailyCap(PassengerType passengerType, BigDecimal cap) {
        dailyCaps.put(passengerType, cap);
    }
}

//Abstract base class for all file handling operations. JsonFileHandler and CsvFileHandler will extend this class.
abstract class FileHandler {
    private String filePath;

    public FileHandler(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    // Checks if the file exists and is readable
    public boolean validateFile() {
        java.io.File file = new java.io.File(filePath);
        return file.exists() && file.isFile();
    }

    // Each subclass must implement how it reads data
    public abstract void read();

    // Each subclass must implement how it writes data
    public abstract void write();
}

//Claude(2026)
//Adapter so LocalTime can be handled correctly while using JSON
class LocalTimeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {

    public JsonElement serialize(LocalTime time, java.lang.reflect.Type type, JsonSerializationContext context) {
        return new JsonPrimitive(time.toString());
    }

    public LocalTime deserialize(JsonElement json, java.lang.reflect.Type type, JsonDeserializationContext context) {
        return LocalTime.parse(json.getAsString());
    }
}

//Handles reading and writing JSON files. Used for saving and loading SystemConfig and RiderProfile.
class JsonFileHandler extends FileHandler {
    private Gson gson;

    public JsonFileHandler(String filePath) {
        super(filePath);
        gson = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization()
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter()).create();
    }

    @Override
    public void read() {
        // Reading is handled by specific load methods below
    }

    @Override
    public void write() {
        // Writing is handled by specific save methods below
    }

    // Loads SystemConfig from JSON file, returns null if file not found
    public SystemConfig loadConfig() {
        SystemConfig result = null;
        if (validateFile()) {
            try {
                java.io.FileReader reader = new java.io.FileReader(getFilePath());
                result = gson.fromJson(reader, SystemConfig.class);
                reader.close();
            } catch (Exception e) {
                System.out.println("Error loading config file. Using defaults.");
            }
        }
        return result;
    }

    // Saves SystemConfig to JSON file
    public void saveConfig(SystemConfig config) {
        try {
            java.io.FileWriter writer = new java.io.FileWriter(getFilePath());
            gson.toJson(config, writer);
            writer.close();
            System.out.println("Config saved successfully.");
        } catch (Exception e) {
            System.out.println("Error saving config file.");
        }
    }
}

public class CityRideSystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static final JsonFileHandler jsonFileHandler = new JsonFileHandler("config.json");
    private static final SystemConfig systemConfig = loadSystemConfig();
    private static final FareCalculator fareCalculator = new FareCalculator(systemConfig);
    private static final JourneyManagement journeyManagement = new JourneyManagement(fareCalculator);
    private static int nextJourneyID = 1;

    public static void main(String[] args) {
        System.out.println("====Welcome to CityRide Lite===");

        boolean running =  true;
        while (running) {            // Keeps the console menu running until the user chooses to exit

            System.out.println("\n==============================");
            System.out.println("        CITYRIDE LITE");
            System.out.println("==============================");
            System.out.println("1. Add journey");
            System.out.println("2. List all journeys");
            System.out.println("3. Filter journeys");
            System.out.println("4. Remove journey");
            System.out.println("5. View category counts");
            System.out.println("6. View daily summary");
            System.out.println("7. View totals by passenger type");
            System.out.println("8. Reset day");
            System.out.println("9. Exit");
            System.out.print("Choose an option (1-9): ");

            int choice = readMenuChoice(1,9);

            switch (choice) {
                case 1:
                    addJourney();
                    break;
                case 2:
                    listJourneys();
                    break;
                case 3:
                    filterJourneys();
                    break;
                case 4:
                    removeJourney();
                    break;
                case 5:
                    showCategoryCounts();
                    break;
                case 6:
                    showDailySummary();
                    break;
                case 7:
                    showPassengerTotals();
                    break;
                case 8:
                    resetDay();
                    break;
                case 9:
                    running = false;
                    break;
            }
        }

        System.out.println("\nGoodbye!");
    }

    private static SystemConfig loadSystemConfig() {
        SystemConfig config = jsonFileHandler.loadConfig();
        if (config == null) {
            config = new SystemConfig();
        }
        return config;
    }

    //The most important validation in my code. I created a separate method to avoid repetition in my code, as this piece of code will be used in further validations and for many user inputs.
    //(Claude AI, 2026)
    //Reads a valid integer, re-prompts on blank or non-numeric input.
    private static int readInt(String prompt) {
        boolean validInput = false;
        int result = 0;
        while (!validInput) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Invalid input. Input cannot be blank.");
            } else {
                try {
                    result = Integer.parseInt(input);
                    validInput = true;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number: ");
                }
            }
        }
        return result;
    }

    //I changed this method so that readMenuChoice handle input all at own, without relying on readInt to avoid blank outputs.
    private static int readMenuChoice(int min, int max) {
        boolean validChoice = false;
        int choice = 0;
        while (!validChoice) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.print("Invalid input. Enter a number (" + min + "-" + max + "): ");
            } else {
                try {
                    choice = Integer.parseInt(input);
                    if (choice >= min && choice <= max) {
                        validChoice = true;
                    } else {
                        System.out.print("Invalid choice. Enter a number (" + min + "-" + max + "): ");
                    }
                } catch (NumberFormatException e) {
                    System.out.print("Invalid input. Enter a number (" + min + "-" + max + "): ");
                }
            }
        }
        return choice;
    }

    // I created this method to check the user's input (y/n) and avoid repeated code.
    private static boolean readYesNo(String prompt) {
        boolean validInput = false;
        boolean answer = false;
        while (!validInput) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y")) {
                answer = true;
                validInput = true;
            }else if (input.equals("n")) {
                answer = false;
                validInput = true;
            }else {
                System.out.println("Invalid input. Please enter y or n");
            }
        }
        return answer;
    }

    //I created this method to check whether the inputted zone is correct.
    private static int readZone(String prompt) {
        boolean validChoice = false;
        int zone = 0;
        while (!validChoice) {
            zone = readInt(prompt);
            if (zone >= SystemConfig.MIN_ZONE && zone <= SystemConfig.MAX_ZONE) {   // or alternative option (zone >=1 && zone <=5), but I like my way.
                validChoice = true;
            }else{
                System.out.println("Invalid input. Zone must be between 1 and 5.");
            }
        }
        return zone;
    }

    //(Claude AI, 2026)
    // Reads and parses a date in dd/MM/yyyy format, re-prompts on invalid format.
    //I created this method to check whether the inputted date is correct.
    private static LocalDate readDate(String prompt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        boolean validDate = false;
        LocalDate date = LocalDate.now();
        while (!validDate) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Invalid input. Date cannot be blank.");
            } else {
                try {
                    date = LocalDate.parse(input, formatter);
                    validDate = true;
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid input. Please enter date as dd/MM/yyyy e.g. 13/02/2026");
                }
            }
        }
        return date;
    }

    //I created this method to check whether the inputted time band is correct.
    private static TimeBand readTimeBand() {
        System.out.println("Time band: ");
        System.out.println("1. Peak");
        System.out.println("2. Off-peak");
        System.out.print("Choose (1-2): ");
        boolean validTimeBand = false;
        TimeBand timeBand = TimeBand.PEAK;
        while (!validTimeBand) {
            int choice = readMenuChoice(1, 2);
            switch (choice) {
                case 1:
                    timeBand = TimeBand.PEAK;
                    validTimeBand = true;
                    break;
                case 2:
                    timeBand = TimeBand.OFF_PEAK;
                    validTimeBand = true;
                    break;
            }
        }
        return timeBand;
    }

    //I created this method to check whether the inputed Passenger Type is correct.
    private static PassengerType readPassengerType() {
        System.out.println("Passenger type: ");
        System.out.println("1. Adult");
        System.out.println("2. Student");
        System.out.println("3. Child ");
        System.out.println("4. Senior Citizen");
        System.out.print("Choose (1-4): ");
        boolean validType = false;
        PassengerType passengerType = PassengerType.ADULT;
        while (!validType) {
            int choice = readMenuChoice(1, 4);
            switch (choice) {
                case 1:
                    passengerType = PassengerType.ADULT;
                    validType = true;
                    break;
                case 2:
                    passengerType = PassengerType.STUDENT;
                    validType = true;
                    break;
                case 3:
                    passengerType = PassengerType.CHILD;
                    validType = true;
                    break;
                case 4:
                    passengerType = PassengerType.SENIOR_CITIZEN;
                    validType = true;
                    break;
            }
        }
        return passengerType;
    }

    // I created this method to check whether the inputted time is correct, method re-prompts user if not.
    private static LocalTime readTime(String prompt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        boolean validTime = false;
        LocalTime time = LocalTime.now();
        while (!validTime) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Invalid input. Time cannot be blank.");
            } else {
                try {
                    time = LocalTime.parse(input, formatter);
                    validTime = true;
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid input. Please enter time as HH:mm (e.g. 19:55)");
                }
            }
        }
        return time;
    }

    private static void addJourney() {
        System.out.println("\nAdd Journey details");
        LocalDate date = readDate("date(dd/MM/yyyy): ");
        LocalTime time = readTime("Time (HH:mm): ");
        int fromZone = readZone("fromZone: ");
        int toZone = readZone("toZone: ");
        PassengerType passengerType = readPassengerType();

        // Running total is used so the FareCalculator can apply the daily cap correctly.
        BigDecimal runningTotal = journeyManagement.getRunningTotal(passengerType);

        Journey journey = fareCalculator.createJourney(nextJourneyID, date, time, fromZone, toZone, passengerType, runningTotal);

        journeyManagement.addJourney(journey);

        nextJourneyID++;
        System.out.println("\nJourney added successfully.\n");
        System.out.println(journey);
    }

    private static void listJourneys() {
        List<Journey> journeys = journeyManagement.getDailySummary();
        if(journeys.isEmpty()){
            System.out.println("No journeys yet.");
        }else{
            System.out.println("\n---All journeys---");
            for (Journey journey : journeys) {
                System.out.println("--------------------------");
                System.out.println(journey);
            }
        }
    }

    //I created 2 separate methods for each task to follow the Single Responsibility Principle (SRP).
    //This first for grabbing choice from user and printing filtered list
    private static void filterJourneys() {
        System.out.println("\n--- Filter Journeys ---");
        System.out.println("1. By passenger type");
        System.out.println("2. By time band");
        System.out.println("3. By zone");
        System.out.println("4. By date");
        System.out.print("Choose (1-4): ");

        int choice = readMenuChoice(1, 4);
        List<Journey> filtered = getFilteredJourneys(choice);

        if(filtered.isEmpty()){
            System.out.println("No journeys yet.");
        }else{
            System.out.println("\n" + filtered.size() + " journey(s) filtered");
            for (Journey journey : filtered) {
                System.out.println("----------------------------");
                System.out.println(journey);
            }
            System.out.println("----------------------------");
        }
    }

    //And this second method to get filtered list from journey management.
    private static List<Journey> getFilteredJourneys(int choice) {
        List<Journey> filtered = new ArrayList<>();
        switch (choice) {
            case 1:
                filtered = journeyManagement.filterByPassengerType(readPassengerType());
                break;
            case 2:
                filtered = journeyManagement.filterByTimeBand(readTimeBand());
                break;
            case 3:
                filtered = journeyManagement.filterByZones(readZone("Enter zone (1-5): "));
                break;
            case 4:
                filtered = journeyManagement.filterByDate(readDate("Enter date (dd/MM/yyyy): "));
                break;
        }
        return filtered;
    }

    private static void removeJourney() {
        int id = readInt("Enter journey id to remove: ");
        boolean confirmed = readYesNo("Are you sure you want to remove journey#" + id + "?(y/n): ");
        if (confirmed) {
            Journey removed = journeyManagement.removeJourney(id);

            if (removed == null) {
                System.out.println("journey#" + id + " not found.");
            } else {
                System.out.println("Journey#" + id + " removed successfully.");
            }
        }else{
            System.out.println("Journey removal cancelled.");
        }
    }

    private static void showCategoryCounts() {
        List<Journey> journeys = journeyManagement.getAllJourneys();

        if (journeys.isEmpty()) {
            System.out.println("No journeys yet.");
            return;
        }

        Map<TimeBand, Integer> byTimeBand = new HashMap<>();
        for (TimeBand timeBand : TimeBand.values()) {
            byTimeBand.put(timeBand, 0);
        }

        Map<String, Integer> byZonePair = new HashMap<>();
        Map<Integer, Integer> byZone = new HashMap<>();

        for (Journey journey : journeys) {
            byTimeBand.put(journey.getTimeBand(), byTimeBand.get(journey.getTimeBand()) + 1);

            String pair = journey.getFromZone() + "-" + journey.getToZone();
            byZonePair.put(pair, byZonePair.getOrDefault(pair, 0) + 1);

            byZone.put(journey.getFromZone(), byZone.getOrDefault(journey.getFromZone(), 0) + 1);
            byZone.put(journey.getToZone(), byZone.getOrDefault(journey.getToZone(),   0) + 1);
        }

        System.out.println("\n-----Category counts-----");

        System.out.println("\nBy Time Band:");
        for (TimeBand timeBand : TimeBand.values()) {
            System.out.println("  " + timeBand + ": " + byTimeBand.get(timeBand) + " journey(s)");
        }

        System.out.println("\nBy Zone Pair:");
        for (Map.Entry<String, Integer> entry : byZonePair.entrySet()) {
            String[] zones = entry.getKey().split("-");
            System.out.println("  Zone " + zones[0] + " -> Zone " + zones[1] + ": " + entry.getValue() + " journey(s)");
        }

        System.out.println("\nBy Zone Involvement:");
        for (Map.Entry<Integer, Integer> entry : byZone.entrySet()) {
            System.out.println("  Zone " + entry.getKey() + ": " + entry.getValue() + " journey(s)");
        }
    }

    private static void showDailySummary(){
        List<Journey> journeys = journeyManagement.getAllJourneys();

        if (journeys.isEmpty()) {
            System.out.println("No journeys yet.");
            return;
        }

        DailySummary dailySummary = new DailySummary(journeys);
        System.out.println(dailySummary);
    }

    private static void showPassengerTotals(){
        Map<PassengerType, PassengerTotals> totals = journeyManagement.getPassengerTotals();

        System.out.println("\n-----Totals by Passenger Type-----\n");
        boolean anyJourneys = false;

        for (PassengerType passengerType : PassengerType.values()){
            PassengerTotals passengerTotals = totals.get(passengerType);
            if(passengerTotals.getJourneyCount() > 0){
                anyJourneys = true;
                System.out.println(passengerTotals.printPassengerTotals());
            }

        }

        if(!anyJourneys){
            System.out.println("No journeys yet.");
        }
    }

    private static void resetDay(){
        boolean confirmed = readYesNo("Are you sure you want to reset day?(y/n): ");
        if (confirmed) {
            journeyManagement.resetDay();
            nextJourneyID = 1;
            System.out.println("Day has been reset successfully.");
        }else{
            System.out.println("Day reset cancelled.");
        }
    }
}
```

---

### Updated Gantt Chart

------------------------------------------------------------------------------------------------------------------------------

![gfd](/private/var/folders/60/82cg9ybd2bg7kgrh339d3zpr0000gn/T/TemporaryItems/com.apple.Photos.NSItemProvider/uuid=C923B6E5-73F6-4537-8565-ADDD29F59A86&code=001&library=1&type=1&mode=1&loc=true&cap=true.png/Image%2002-04-2026%20at%2022.52.png)

------------------------------------------------------------------------------------------------------------------------------

### Diary Entries

------------------------------------------------------------------------------------------------------------------------------

### 02/04/2026 - Diary Entry 1 – Research and start of coding

Today, I started by fixing the errors highlighted in my tutor’s feedback. This involved resolving two issues: ensuring that, when a journey is deleted, the cost of the remaining journeys is recalculated correctly, taking the daily cap into account and adding a count of the zones involvement to the category counts method. These fixes required me to restructure the recalculation logic in the JourneyManagement class and add a new map to the showCategoryCounts() method.

I then proceeded to implement the SystemConfig class. The purpose of this class is to replace the CityRideDataset with a configurable system that an administrator can update at runtime. During this process, I studied the LocalTime class from the Oracle Java documentation to learn how to handle peak and off-peak time windows, as this was new to me. Actually, I initially found this information on GeeksForGeeks, but it referenced Oracle documentation, so I also looked at what was there and cited it, as it generally contains all the information I need in a compact form and is the original source. One of the issues I came across was how to write the determineTimeBand() method correctly, I didn’t just want to compare numbers, since I use LocalTime to work with time, but to compare times correctly, though there’s already a method for this called isBefore() that helped me.        

### 03/04/2026 - Diary Entry 2 – Adapting program to use new class SystemConfig

Today I modified the existing program by replacing CityRideDataset with SystemConfig. The CityRideDataset class has been completely removed. As part of the modification, the FareCalculator module has been updated to now pull fares, discounts and restrictions from SystemConfig, and a LocalTime variable has been added to the Journey class and constructor. As the system automatically determines the TimeBand based on the time, the user now enters the time instead of the TimeBand. I also created a helper method called readTime() based on the validation methods I had already written for other variables, such as readDate(). It wasn’t difficult to adapt the existing validations to create a validation for the time entered by user.

Although I didn’t write many lines of code today, it took quite a while because I had to fix loads of errors after deleting the dataset, but the program is now working as before and is already using SystemConfig.

### 05/04/2026 - Diary Entry 3 – Research and adding implementing JsonFileHandler

Today I began implementing the file handling classes. I started by writing the abstract FileHandler class which defines the common structure for all file handlers, then created JsonFileHandler which extends it to handle reading and writing JSON files. However when testing I ran into an issue where the config file was saving nothing, after searching I realised this was caused by Gson not knowing how to deal with the LocalTime type automatically. I researched the Gson User Guide on GitHub which explained how to write custom serialisers and deserialisers, but I struggled to implement it myself. After the program kept crashing I used Claude AI to help me write the LocalTimeAdapter class which solved the problem by converting LocalTime to a string when saving and parsing it back when loading. Using AI wasn’t necessary, but I simply don’t have the time as I have a C++ project submission soon and I’m trying to make everything perfect, and I needed to solve this problem in this project.

------------------------------------------------------------------------------------------------------------------------------
