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
    boolean capApplied;

    public Journey(int journeyID, LocalDate date, LocalTime time, int fromZone, int toZone,
                   TimeBand timeBand, PassengerType passengerType,
                   int zonesCrossed, BigDecimal baseFare,
                   BigDecimal discountedFare, BigDecimal chargedFare, boolean capApplied) {

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
        this.capApplied = capApplied;
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
    public boolean isCapApplied() {
        return capApplied;
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
                "Charged Fare: £" + chargedFare.setScale(2, RoundingMode.HALF_UP).toPlainString() +
                "Cap Applied: " + (capApplied ? "Yes" : "No") + "\n";
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

    public Journey findJourney(int journeyID) {
        Journey result = null;
        for (Journey journey : journeys) {
            if (journey.getJourneyID() == journeyID) {
                result = journey;
                break;
            }
        }
        return result;
    }

    public void editJourney(int journeyID, LocalDate newDate, LocalTime newTime, int newFromZone, int newToZone, PassengerType newPassengerType) {
        for (int i = 0; i < journeys.size(); i++) {
            if (journeys.get(i).getJourneyID() == journeyID) {
                Journey edited = fareCalculator.createJourney(journeyID, newDate, newTime, newFromZone, newToZone, newPassengerType, BigDecimal.ZERO);
                journeys.set(i, edited);
                recalculateAll();
                break;
            }
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

        if (chargedFare.compareTo(discountedFare) < 0) {
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
        sb.append("        Daily Summary \n");
        sb.append("============================\n");
        sb.append("Total Journeys:  ").append(getTotalJourneys()).append("\n");
        sb.append("Total Cost:      £").append(getTotalCost().setScale(2, RoundingMode.HALF_UP)).append("\n");
        sb.append("Average Cost:    £").append(getAverageCost().setScale(2, RoundingMode.HALF_UP)).append("\n");

        Journey mostExpensive = getMostExpensiveJourney();

        if(mostExpensive != null) {
            sb.append("Most expensive: Journey #").append(mostExpensive.getJourneyID()).append(" £").append(mostExpensive.getChargedFare().setScale(2, RoundingMode.HALF_UP)).append("\n");

        }

        sb.append("Cap Savings:     £").append(getCapSavings()).append("\n");

        return sb.toString();
    }

    public BigDecimal getCapSavings() {
        BigDecimal totalDiscounted = BigDecimal.ZERO;
        BigDecimal totalCharged = BigDecimal.ZERO;
        for (Journey journey : journeys) {
            totalDiscounted = totalDiscounted.add(journey.getDiscountedFare());
            totalCharged = totalCharged.add(journey.getChargedFare());
        }
        return totalDiscounted.subtract(totalCharged).setScale(2, RoundingMode.HALF_UP);
    }

    public String getCategoryCounts(List<Journey> journeys) {
        Map<TimeBand, Integer> byTimeBand = new HashMap<>();
        for (TimeBand timeBand : TimeBand.values()) {
            byTimeBand.put(timeBand, 0);
        }
        Map<String, Integer> byZonePair = new HashMap<>();

        for (Journey journey : journeys) {
            byTimeBand.put(journey.getTimeBand(), byTimeBand.get(journey.getTimeBand()) + 1);
            String pair = journey.getFromZone() + "-" + journey.getToZone();
            byZonePair.put(pair, byZonePair.getOrDefault(pair, 0) + 1);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("By Time Band:\n");
        for (TimeBand timeBand : TimeBand.values()) {
            sb.append("  ").append(timeBand).append(": ").append(byTimeBand.get(timeBand)).append(" journey(s)\n");
        }
        sb.append("By Zone Pair:\n");
        for (Map.Entry<String, Integer> entry : byZonePair.entrySet()) {
            String[] zones = entry.getKey().split("-");
            sb.append("  Zone ").append(zones[0]).append(" -> Zone ").append(zones[1]).append(": ").append(entry.getValue()).append(" journey(s)\n");
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

    public Journey createJourney(int journeyID, LocalDate date, LocalTime time, int fromZone,
                                 int toZone, PassengerType passengerType, BigDecimal runningTotal) {
        int zonesCrossed = Math.abs(toZone - fromZone) + 1;
        TimeBand timeBand = config.determineTimeBand(time);

        BigDecimal baseFare = config.getBaseFare(fromZone, toZone, timeBand);
        BigDecimal discountedFare = calculateDiscountedFare(baseFare, passengerType);
        BigDecimal chargedFare = calculateChargedFare(discountedFare, passengerType, runningTotal);

        boolean capApplied = chargedFare.compareTo(discountedFare) < 0;

        return new Journey(journeyID, date, time, fromZone, toZone, timeBand, passengerType,
                zonesCrossed, baseFare, discountedFare, chargedFare, capApplied);
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
        //Oracle(2018)
        //method to compare time
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

    // Resets a single base fare back to its default value
    public void resetBaseFare(int fromZone, int toZone, TimeBand timeBand) {
        String key = fromZone + "-" + toZone + "-" + timeBand.name();
        Map<String, BigDecimal> savedFares = new HashMap<>(baseFares);
        loadDefaultBaseFares();
        BigDecimal defaultFare = baseFares.get(key);
        baseFares = savedFares;
        baseFares.put(key, defaultFare);
    }

    // Resets a passenger discount back to its default
    public void resetDiscountRate(PassengerType passengerType) {
        Map<PassengerType, BigDecimal> savedRates = new HashMap<>(discountRates);
        loadDefaultDiscounts();
        BigDecimal defaultRate = discountRates.get(passengerType);
        discountRates = savedRates;
        discountRates.put(passengerType, defaultRate);
    }

    // Resets a daily cap back to its default
    public void resetDailyCap(PassengerType passengerType) {
        Map<PassengerType, BigDecimal> savedCaps = new HashMap<>(dailyCaps);
        loadDefaultDailyCaps();
        BigDecimal defaultCap = dailyCaps.get(passengerType);
        dailyCaps = savedCaps;
        dailyCaps.put(passengerType, defaultCap);
    }

    // Resets peak hours back to default
    public void resetPeakHours() {
        peakStart = LocalTime.of(7, 0);
        peakEnd = LocalTime.of(19, 0);
    }

    // Resets everything back to defaults
    public void resetAllToDefaults() {
        loadDefaults();
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
    protected boolean validateFile() {
        java.io.File file = new java.io.File(filePath);
        return file.exists() && file.isFile();
    }

    // Each subclass must implement how it reads data
    public abstract String read();

    // Each subclass must implement how it writes data
    public abstract void write(String content);
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

    // Returns raw JSON content from file as string
    @Override
    public String read() {
        String result = null;
        if (validateFile()) {
            try {
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(getFilePath()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                result = sb.toString();
            } catch (Exception e) {
                System.out.println("Error reading file.");
            }
        }
        return result;
    }

    // Writes raw string content to JSON file
    @Override
    public void write(String content) {
        try {
            java.io.FileWriter writer = new java.io.FileWriter(getFilePath());
            writer.write(content);
            writer.close();
        } catch (Exception e) {
            System.out.println("Error writing file.");
        }
    }

    // Loads SystemConfig from JSON file using read() internally
    public SystemConfig loadConfig() {
        SystemConfig result = null;
        String content = read();
        if (content != null) {
            try {
                result = gson.fromJson(content, SystemConfig.class);
                if (result != null && (result.getBaseFares() == null || result.getDiscountRates() == null
                        || result.getDailyCaps() == null || result.getPeakStart() == null
                        || result.getPeakEnd() == null)) {
                    System.out.println("Config file incomplete. Using defaults.");
                    result = null;
                }
            } catch (Exception e) {
                System.out.println("Error loading config file. Using defaults.");
            }
        }
        return result;
    }

    // Saves SystemConfig to JSON file using write() internally
    public void saveConfig(SystemConfig config) {
        write(gson.toJson(config));
        System.out.println("Config saved successfully.");
    }

    // Loads RiderProfile from JSON file using read() internally
    public RiderProfile loadProfile() {
        RiderProfile result = null;
        String content = read();
        if (content != null) {
            try {
                result = gson.fromJson(content, RiderProfile.class);
            } catch (Exception e) {
                System.out.println("Error loading profile.");
            }
        }
        return result;
    }

    // Saves RiderProfile to JSON file using write() internally
    public void saveProfile(RiderProfile profile) {
        write(gson.toJson(profile));
        System.out.println("Profile saved successfully.");
    }
}

// Handles reading and writing CSV files. Used for importing and exporting journeys.
class CsvFileHandler extends FileHandler {

    public CsvFileHandler(String filePath) {
        super(filePath);
    }

    // Returns raw CSV content from file as string
    @Override
    public String read() {
        String result = null;
        if (validateFile()) {
            try {
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(getFilePath()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                reader.close();
                result = sb.toString();
            } catch (Exception e) {
                System.out.println("Error reading CSV file.");
            }
        }
        return result;
    }

    // Writes raw string content to CSV file
    @Override
    public void write(String content) {
        try {
            java.io.FileWriter writer = new java.io.FileWriter(getFilePath());
            writer.write(content);
            writer.close();
        } catch (Exception e) {
            System.out.println("Error writing CSV file.");
        }
    }

    // Imports journeys from CSV file using read() internally
    public List<Journey> importJourneys() {
        List<Journey> journeys = new ArrayList<>();
        String content = read();

        if (content != null) {
            String[] lines = content.split("\n");
            boolean firstLine = true;
            for (String line : lines) {
                if (!firstLine && !line.trim().isEmpty()) {
                    Journey journey = parseJourneyFromLine(line);
                    if (journey != null) {
                        journeys.add(journey);
                    }
                }
                firstLine = false;
            }
            System.out.println(journeys.size() + " journey(s) imported successfully.");
        } else {
            System.out.println("CSV file not found.");
        }

        return journeys;
    }

    // Parses a single CSV line into a Journey object
    private Journey parseJourneyFromLine(String line) {
        Journey result = null;
        try {
            String[] parts = line.split(",");
            int journeyID = Integer.parseInt(parts[0].trim());
            LocalDate date = LocalDate.parse(parts[1].trim());
            LocalTime time = LocalTime.parse(parts[2].trim());
            int fromZone = Integer.parseInt(parts[3].trim());
            int toZone = Integer.parseInt(parts[4].trim());
            TimeBand timeBand = TimeBand.valueOf(parts[5].trim());
            PassengerType passengerType = PassengerType.valueOf(parts[6].trim());
            int zonesCrossed = Integer.parseInt(parts[7].trim());
            BigDecimal baseFare = new BigDecimal(parts[8].trim());
            BigDecimal discountedFare = new BigDecimal(parts[9].trim());
            BigDecimal chargedFare = new BigDecimal(parts[10].trim());
            boolean capApplied = Boolean.parseBoolean(parts[11].trim());   // NEW

            result = new Journey(journeyID, date, time, fromZone, toZone, timeBand, passengerType,
                    zonesCrossed, baseFare, discountedFare, chargedFare, capApplied);
        } catch (Exception e) {
            System.out.println("Skipping invalid line: " + line);
        }
        return result;
    }

    // Exports journeys to CSV file using write() internally
    public void exportJourneys(List<Journey> journeys) {
        StringBuilder sb = new StringBuilder();
        sb.append("id,date,time,fromZone,toZone,timeBand,passengerType,zonesCrossed,baseFare,discountedFare,chargedFare,capApplied\n");

        for (Journey journey : journeys) {
            sb.append(journeyToCsvRow(journey)).append("\n");
        }

        write(sb.toString());
        System.out.println("Journeys exported successfully to " + getFilePath());
    }

    // Converts a Journey object to a CSV row string
    private String journeyToCsvRow(Journey journey) {
        return journey.getJourneyID() + "," +
                journey.getDate() + "," +
                journey.getTime() + "," +
                journey.getFromZone() + "," +
                journey.getToZone() + "," +
                journey.getTimeBand() + "," +
                journey.getPassengerType() + "," +
                journey.getZonesCrossed() + "," +
                journey.getBaseFare().setScale(2, RoundingMode.HALF_UP) + "," +
                journey.getDiscountedFare().setScale(2, RoundingMode.HALF_UP) + "," +
                journey.getChargedFare().setScale(2, RoundingMode.HALF_UP) + "," +
                journey.isCapApplied();
    }
}

class RiderProfile {
    private String name;
    private PassengerType passengerType;
    private String defaultPayment;

    public RiderProfile(String name, PassengerType passengerType, String defaultPayment) {
        this.name = name;
        this.passengerType = passengerType;
        this.defaultPayment = defaultPayment;
    }

    public String getName() {
        return name;
    }

    public PassengerType getPassengerType() {
        return passengerType;
    }

    public String getDefaultPayment() {
        return defaultPayment;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassengerType(PassengerType passengerType) {
        this.passengerType = passengerType;
    }

    public void setDefaultPayment(String defaultPayment) {
        this.defaultPayment = defaultPayment;
    }

    @Override
    public String toString() {
        return "=== Rider Profile ===\n" +
                "Name: " + name + "\n" +
                "Passenger Type: " + passengerType + "\n" +
                "Default Payment: " + defaultPayment + "\n";
    }
}

class InputReader {
    private Scanner scanner;

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    //The most important validation in my code. I created a separate method to avoid repetition in my code, as this piece of code will be used in further validations and for many user inputs.
    //(Claude AI, 2026)
    //Reads a valid integer, re-prompts on blank or non-numeric input.
    public int readInt(String prompt) {
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
    public int readMenuChoice(int min, int max) {
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
    public boolean readYesNo(String prompt) {
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
    public int readZone(String prompt) {
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
    public LocalDate readDate(String prompt) {
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
    public TimeBand readTimeBand() {
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
    public PassengerType readPassengerType() {
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
    public LocalTime readTime(String prompt) {
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

    public BigDecimal readBigDecimal(String prompt) {
        boolean validInput = false;
        BigDecimal result = BigDecimal.ZERO;
        while (!validInput) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Invalid input. Cannot be blank.");
            }else{
                try {
                    result = new BigDecimal(input);
                    validInput = true;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number e.g. 0.25");
                }
            }
        }
        return result;
    }
}

// Generates and saves end-of-day reports in text and CSV formats
class ReportExporter {
    private String riderName;
    private LocalDate date;

    public ReportExporter(String riderName, LocalDate date) {
        this.riderName = riderName;
        this.date = date;
    }

    // Builds the file name using rider name and date
    public String buildFileName(String extension) {
        java.io.File reportsDir = new java.io.File("reports");
        if (!reportsDir.exists()) {
            reportsDir.mkdir();
        }
        return "reports/" + riderName.replace(" ", "_") + "_" + date + "_report." + extension;
    }

    // Exports end-of-day summary as a human-readable text to file
    public void exportTextSummary(List<Journey> journeys) {
        String fileName = buildFileName("txt");
        DailySummary summary = new DailySummary(journeys);

        StringBuilder sb = new StringBuilder();
        sb.append("============================\n");
        sb.append("        Daily Report\n");
        sb.append("============================\n");
        sb.append("Rider: ").append(riderName).append("\n");
        sb.append("Date: ").append(date).append("\n");
        sb.append("============================\n\n");
        sb.append(summary).append("\n");
        sb.append(summary.getCategoryCounts(journeys)).append("\n");

        sb.append("--- Journey Details ---\n");
        for (Journey journey : journeys) {
            sb.append(journey.toString()).append("\n");
            sb.append("--------------------------\n");
        }

        CsvFileHandler fileHandler = new CsvFileHandler(fileName);
        fileHandler.write(sb.toString());
        System.out.println("Text report saved to " + fileName);
    }

    // Exports journeys as a CSV report
    public void exportCsvReport(List<Journey> journeys) {
        String fileName = buildFileName("csv");
        CsvFileHandler csvFileHandler = new CsvFileHandler(fileName);
        csvFileHandler.exportJourneys(journeys);
        System.out.println("CSV report saved to " + fileName);
    }
}

class RiderService {
    private Scanner scanner;
    private JourneyManagement journeyManagement;
    private FareCalculator fareCalculator;
    private JsonFileHandler jsonFileHandler;
    private RiderProfile profile;
    private InputReader inputReader;
    private int nextJourneyID = 1;

    public RiderService(Scanner scanner, JourneyManagement journeyManagement, FareCalculator fareCalculator, JsonFileHandler jsonFileHandler) {
        this.scanner = scanner;
        this.journeyManagement = journeyManagement;
        this.fareCalculator = fareCalculator;
        this.jsonFileHandler = jsonFileHandler;
        this.inputReader = new InputReader(scanner);
    }

    public void showMenu() {
        loadOrCreateProfile();

        boolean running = true;
        while (running) {            // Keeps the console menu running until the user chooses to exit

            System.out.println("\n==============================");
            System.out.println("       CITYRIDE - RIDER");
            System.out.println("==============================");
            System.out.println("1. Add journey");
            System.out.println("2. List all journeys");
            System.out.println("3. Filter journeys");
            System.out.println("4. Edit journey");
            System.out.println("5. Remove journey");
            System.out.println("6. View category counts");
            System.out.println("7. View daily summary");
            System.out.println("8. View totals by passenger type");
            System.out.println("9. Reset day");
            System.out.println("10. Import journeys from CSV");
            System.out.println("11. Export journeys to CSV");
            System.out.println("12. Export daily report");
            System.out.println("13. Exit");
            System.out.print("Choose an option (1-13): ");

            int choice = inputReader.readMenuChoice(1, 13);

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
                    editJourney();
                    break;
                case 5:
                    removeJourney();
                    break;
                case 6:
                    showCategoryCounts();
                    break;
                case 7:
                    showDailySummary();
                    break;
                case 8:
                    showPassengerTotals();
                    break;
                case 9:
                    resetDay();
                    break;
                case 10:
                    importJourneys();
                    break;
                case 11:
                    exportJourneys();
                    break;
                case 12:
                    exportReport();
                    break;
                case 13:
                    running = false;
                    break;
            }
        }

        saveOnExit();
    }

    // Asks user to load existing profile or create a new one
    private void loadOrCreateProfile() {
        System.out.println("\n1. Load existing profile");
        System.out.println("2. Create new profile");
        System.out.print("Choose (1-2): ");

        int choice = inputReader.readMenuChoice(1, 2);

        if (choice == 1) {
            JsonFileHandler profileHandler = new JsonFileHandler("profile.json");
            profile = profileHandler.loadProfile();
            if (profile == null) {
                System.out.println("No profile found. Creating new one.");
                createProfile();
            }else{
                System.out.println("Profile loaded!\n" + profile);
            }
        }else{
            createProfile();
        }
    }

    private void createProfile() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();
        PassengerType passengerType = inputReader.readPassengerType();

        System.out.println("Default payment: ");
        System.out.println("1. Cash");
        System.out.println("2. Card");
        System.out.println("3. Travel card");
        System.out.print("Choose (1-3): ");
        int payChoice = inputReader.readMenuChoice(1, 3);
        String defaultPayment;
        if (payChoice == 1) {
            defaultPayment = "Cash";
        }else if (payChoice == 2) {
            defaultPayment = "Card";
        }else{
            defaultPayment = "Travel card";
        }

        profile = new RiderProfile(name, passengerType, defaultPayment);
        System.out.println("\nProfile created!\n" + profile);
    }

    // Offers to save profile and journeys on exit
    private void saveOnExit() {
        boolean save = inputReader.readYesNo("Save your profile and journeys before exiting?(y/n): ");
        if (save) {
            JsonFileHandler profileHandler = new JsonFileHandler("profile.json");
            profileHandler.saveProfile(profile);
            List<Journey> journeys = journeyManagement.getAllJourneys();
            if (!journeys.isEmpty()) {
                CsvFileHandler csvFileHandler = new CsvFileHandler("journeys.csv");
                csvFileHandler.exportJourneys(journeys);
            }
        }else{
            System.out.println("Profile with journeys not saved.");
        }
        System.out.println("\nGoodbye!");
    }

    private void addJourney() {
        System.out.println("\nAdd Journey details");
        LocalDate date = inputReader.readDate("date(dd/MM/yyyy): ");
        LocalTime time = inputReader.readTime("Time (HH:mm): ");
        int fromZone = inputReader.readZone("fromZone: ");
        int toZone = inputReader.readZone("toZone: ");
        PassengerType passengerType = inputReader.readPassengerType();

        // Running total is used so the FareCalculator can apply the daily cap correctly.
        BigDecimal runningTotal = journeyManagement.getRunningTotal(passengerType);

        Journey journey = fareCalculator.createJourney(nextJourneyID, date, time, fromZone, toZone, passengerType, runningTotal);

        journeyManagement.addJourney(journey);

        nextJourneyID++;
        System.out.println("\nJourney added successfully.\n");
        System.out.println(journey);
    }

    private void listJourneys() {
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
    private void filterJourneys() {
        System.out.println("\n--- Filter Journeys ---");
        System.out.println("1. By passenger type");
        System.out.println("2. By time band");
        System.out.println("3. By zone");
        System.out.println("4. By date");
        System.out.print("Choose (1-4): ");

        int choice = inputReader.readMenuChoice(1, 4);
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
    private List<Journey> getFilteredJourneys(int choice) {
        List<Journey> filtered = new ArrayList<>();
        switch (choice) {
            case 1:
                filtered = journeyManagement.filterByPassengerType(inputReader.readPassengerType());
                break;
            case 2:
                filtered = journeyManagement.filterByTimeBand(inputReader.readTimeBand());
                break;
            case 3:
                filtered = journeyManagement.filterByZones(inputReader.readZone("Enter zone (1-5): "));
                break;
            case 4:
                filtered = journeyManagement.filterByDate(inputReader.readDate("Enter date (dd/MM/yyyy): "));
                break;
        }
        return filtered;
    }

    private void editJourney() {
        int id = inputReader.readInt("Enter journey ID to edit: ");
        Journey existing = journeyManagement.findJourney(id);

        if (existing == null) {
            System.out.println("Journey #" + id + " not found.");
        }else{
            System.out.println("Enter new journey details:");
            LocalDate date = inputReader.readDate("Date (dd/MM/yyyy): ");
            LocalTime time = inputReader.readTime("Time (HH:mm): ");
            int fromZone = inputReader.readZone("From zone (1-5): ");
            int toZone = inputReader.readZone("To zone (1-5): ");
            PassengerType passengerType = inputReader.readPassengerType();
            journeyManagement.editJourney(id, date, time, fromZone, toZone, passengerType);
            System.out.println("Journey #" + id + " updated successfully.");
        }
    }

    private void removeJourney() {
        int id = inputReader.readInt("Enter journey id to remove: ");
        boolean confirmed = inputReader.readYesNo("Are you sure you want to remove journey#" + id + "?(y/n): ");
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

    private void showCategoryCounts() {
        List<Journey> journeys = journeyManagement.getAllJourneys();

        if (journeys.isEmpty()) {
            System.out.println("No journeys yet.");
        }else {
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
    }

    private void showDailySummary() {
        List<Journey> journeys = journeyManagement.getAllJourneys();
        if (journeys.isEmpty()) {
            System.out.println("No journeys yet.");
        } else {
            DailySummary dailySummary = new DailySummary(journeys);
            System.out.println(dailySummary);
        }
    }

    private void showPassengerTotals(){
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

    private void resetDay(){
        boolean confirmed = inputReader.readYesNo("Are you sure you want to reset day?(y/n): ");
        if (confirmed) {
            journeyManagement.resetDay();
            nextJourneyID = 1;
            System.out.println("Day has been reset successfully.");
        }else{
            System.out.println("Day reset cancelled.");
        }
    }

    private void importJourneys() {
        CsvFileHandler csvFileHandler = new CsvFileHandler("journeys.csv");
        List<Journey> imported = csvFileHandler.importJourneys();
        for (Journey journey : imported) {
            BigDecimal runningTotal = journeyManagement.getRunningTotal(journey.getPassengerType());
            Journey recalculated = fareCalculator.createJourney(nextJourneyID, journey.getDate(),
                    journey.getTime(), journey.getFromZone(), journey.getToZone(),
                    journey.getPassengerType(), runningTotal);
            journeyManagement.addJourney(recalculated);
            nextJourneyID++;
        }
    }

    private void exportJourneys() {
        List<Journey> journeys = journeyManagement.getAllJourneys();
        if (journeys.isEmpty()) {
            System.out.println("No journeys to export.");
        }else{
            CsvFileHandler csvFileHandler = new CsvFileHandler("journeys.csv");
            csvFileHandler.exportJourneys(journeys);
        }
    }

    private void exportReport() {
        List<Journey> journeys = journeyManagement.getAllJourneys();
        if (journeys.isEmpty()) {
            System.out.println("No journeys to export.");
        }else{
            ReportExporter exporter = new ReportExporter(profile.getName(), LocalDate.now());
            exporter.exportTextSummary(journeys);
            exporter.exportCsvReport(journeys);
        }
    }
}

class AdminService {
    private Scanner scanner;
    private SystemConfig systemConfig;
    private JsonFileHandler jsonFileHandler;
    private InputReader inputReader;
    private static final String PASSWORD = "admin123";

    public AdminService(Scanner scanner, SystemConfig systemConfig, JsonFileHandler jsonFileHandler) {
        this.scanner = scanner;
        this.systemConfig = systemConfig;
        this.jsonFileHandler = jsonFileHandler;
        this.inputReader = new InputReader(scanner);
    }

    public boolean login() {
        boolean result = false;
        System.out.print("Enter admin password: ");
        String input = scanner.nextLine().trim();
        if (input.equals(PASSWORD)) {
            System.out.println("Access granted.");
            result = true;
        }else{
            System.out.println("Incorrect password. Access denied.");
        }
        return result;
    }

    public void showMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n==============================");
            System.out.println("       CITYRIDE - ADMIN");
            System.out.println("==============================");
            System.out.println("1. View current config");
            System.out.println("2. Update discount rate");
            System.out.println("3. Update daily cap");
            System.out.println("4. Update peak hours");
            System.out.println("5. Update base fare");
            System.out.println("6. Reset to default");
            System.out.println("7. Save config");
            System.out.println("8. Exit");
            System.out.print("Choose an option (1-8): ");

            int choice = inputReader.readMenuChoice(1, 8);

            switch (choice) {
                case 1: viewConfig(); break;
                case 2: updateDiscount(); break;
                case 3: updateDailyCap(); break;
                case 4: updatePeakHours(); break;
                case 5: updateBaseFare(); break;
                case 6: resetToDefault(); break;
                case 7: saveConfig(); break;
                case 8: running = false; break;
            }
        }
    }

    private void resetToDefault() {
        System.out.println("\n--- Reset to Default ---");
        System.out.println("1. Reset a base fare");
        System.out.println("2. Reset a discount rate");
        System.out.println("3. Reset a daily cap");
        System.out.println("4. Reset peak hours");
        System.out.println("5. Reset everything");
        System.out.print("Choose (1-5): ");

        int choice = inputReader.readMenuChoice(1, 5);
        boolean confirmed = inputReader.readYesNo("Are you sure? This cannot be undone. (y/n): ");

        if (!confirmed) {
            System.out.println("Reset cancelled.");
        } else {
            switch (choice) {
                case 1:
                    resetBaseFare();
                    break;
                case 2:
                    PassengerType discountType = inputReader.readPassengerType();
                    systemConfig.resetDiscountRate(discountType);
                    System.out.println("Discount rate for " + discountType + " reset to default.");
                    break;
                case 3:
                    PassengerType capType = inputReader.readPassengerType();
                    systemConfig.resetDailyCap(capType);
                    System.out.println("Daily cap for " + capType + " reset to default.");
                    break;
                case 4:
                    systemConfig.resetPeakHours();
                    System.out.println("Peak hours reset to default (07:00 - 19:00).");
                    break;
                case 5:
                    systemConfig.resetAllToDefaults();
                    System.out.println("All config values reset to defaults.");
                    break;
            }
        }
    }

    private void resetBaseFare() {
        int fromZone = inputReader.readZone("Enter from zone (1-5): ");
        int toZone = inputReader.readZone("Enter to zone (1-5): ");
        System.out.println("Time band: ");
        System.out.println("1. Peak");
        System.out.println("2. Off-peak");
        System.out.print("Choose (1-2): ");
        int bandChoice = inputReader.readMenuChoice(1, 2);
        TimeBand timeBand;
        if (bandChoice == 1) {
            timeBand = TimeBand.PEAK;
        } else {
            timeBand = TimeBand.OFF_PEAK;
        }
        systemConfig.resetBaseFare(fromZone, toZone, timeBand);
        System.out.println("Base fare for " + fromZone + "-" + toZone + " " + timeBand + " reset to default.");
    }

    private void viewConfig() {
        System.out.println("\n-----Current Config-----");
        System.out.println("Peak hours: " + systemConfig.getPeakStart() + " - " + systemConfig.getPeakEnd());

        System.out.println("\nDiscount rates:");
        for (PassengerType type : PassengerType.values()) {
            System.out.println("  " + type + ": " + systemConfig.getDiscountRate(type).multiply(new BigDecimal("100")) + "%");
        }

        System.out.println("\nDaily caps:");
        for (PassengerType type : PassengerType.values()) {
            System.out.println("  " + type + ": £" + systemConfig.getDailyCap(type));
        }
    }

    private void updateDiscount() {
        System.out.println("\nUpdate discount rate");
        PassengerType type = inputReader.readPassengerType();
        BigDecimal rate = inputReader.readBigDecimal("Enter new discount rate (e.g. 0.25 for 25%): ");

        if (rate.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(BigDecimal.ONE) > 0) {
            System.out.println("Invalid rate. Must be between 0.00 and 1.00.");
        }else{
            systemConfig.setDiscountRates(type, rate);
            System.out.println("Discount updated successfully.");
        }
    }

    private void updateDailyCap() {
        System.out.println("\nUpdate daily cap");
        PassengerType type = inputReader.readPassengerType();
        BigDecimal cap = inputReader.readBigDecimal("Enter new daily cap (e.g. 8.00): ");

        if (cap.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Invalid cap. Must be greater than 0.");
        }else{
            systemConfig.setDailyCap(type, cap);
            System.out.println("Daily cap updated successfully.");
        }
    }

    private void updatePeakHours() {
        System.out.println("\nUpdate peak hours");
        LocalTime start = inputReader.readTime("Enter peak start time (HH:mm): ");
        LocalTime end = inputReader.readTime("Enter peak end time (HH:mm): ");

        if (!start.isBefore(end)) {
            System.out.println("Invalid times. Start must be before end.");
        }else{
            systemConfig.setPeakStart(start);
            systemConfig.setPeakEnd(end);
            System.out.println("Peak hours updated successfully.");
        }
    }

    private void updateBaseFare() {
        System.out.println("\nUpdate base fare");
        int fromZone = inputReader.readZone("Enter from zone (1-5): ");
        int toZone = inputReader.readZone("Enter to zone (1-5): ");
        System.out.println("Time band: ");
        System.out.println("1. Peak");
        System.out.println("2. Off-peak");
        System.out.print("Choose (1-2): ");
        int bandChoice = inputReader.readMenuChoice(1, 2);
        TimeBand timeBand;

        if (bandChoice == 1) {
            timeBand = TimeBand.PEAK;
        }else{
            timeBand = TimeBand.OFF_PEAK;
        }

        BigDecimal fare = inputReader.readBigDecimal("Enter new fare (e.g. 3.50): ");
        if (fare.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Invalid fare. Must be greater than 0.");
        }else{
            systemConfig.setBaseFares(fromZone, toZone, timeBand, fare);
            System.out.println("Base fare updated successfully.");
        }
    }

    private void saveConfig() {
        JsonFileHandler configHandler = new JsonFileHandler("config.json");
        configHandler.saveConfig(systemConfig);
    }
}

public class CityRideSystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static final InputReader inputReader = new InputReader(scanner);
    private static final JsonFileHandler jsonFileHandler = new JsonFileHandler("config.json");
    private static final SystemConfig systemConfig = loadSystemConfig();
    private static final FareCalculator fareCalculator = new FareCalculator(systemConfig);
    private static final JourneyManagement journeyManagement = new JourneyManagement(fareCalculator);

    public static void main(String[] args) {
        System.out.println("====Welcome to CityRide Lite===");
        selectRole();
    }

    private static SystemConfig loadSystemConfig() {
        SystemConfig config = jsonFileHandler.loadConfig();
        if (config == null) {
            config = new SystemConfig();
        }
        return config;
    }

    // Asks user to select a role and goes to the appropriate service
    private static void selectRole() {
        boolean running = true;
        while (running) {
            System.out.println("\n1. Rider");
            System.out.println("2. Admin");
            System.out.print("Select role (1-2): ");

            int choice = inputReader.readMenuChoice(1, 2);

            if (choice == 1) {
                RiderService riderService = new RiderService(scanner, journeyManagement, fareCalculator, jsonFileHandler);
                riderService.showMenu();
                running = false;
            }else{
                AdminService adminService = new AdminService(scanner, systemConfig, jsonFileHandler);
                boolean loggedIn = adminService.login();
                if (loggedIn) {
                    adminService.showMenu();
                    running = false;
                }
            }
        }
    }
}