### IY4113 Milestone 1

| Assessment Details | Please Complete All Details                                      |
| ------------------ | ---------------------------------------------------------------- |
| Group              | Group A                                                          |
| Module Title       | Applied Software Engineering using Object Orientated Programming |
| Assessment Type    | java fundamentals part 2                                         |
| Module Tutor Name  | Jonathan Shore                                                   |
| Student ID Number  | P485902                                                          |
| Date of Submission | 22/03/2026                                                       |
| GitHub LInk        | https://github.com/T0485902Vladyslav/IY4113_Java_T0485902_Part_2 |

- [x] *I confirm that this assignment is my own work. Where I have referred to academic sources, I have provided in-text citations and included the sources in
  the final reference list.*

- [x] *Where I have used AI, I have cited and referenced appropriately.

---

### Purpose of the Program

---

To extend CityRide Lite into a more advanced console-based Java transport fare companion application. Building on Part 1's single-session journey tracking, Part 2 introduces persistent storage, user profiles, file import/export, and an administrator role that moving the program closer to a real-world transport management system.

---

### **Core Program Functionality**

**Role selection**: when the program launches, the user chooses between two roles: rider or admin, each with its own separate menu and set of options to do.

**Rider profile management**: a rider can create a new profile or load an existing one. The profile stores their name, passenger type, and default payment option. Profiles are saved and loaded using JSON files, allowing the rider to continue from a previous session.

**Journey management (add / edit / delete)**: the rider can manually add journeys, edit existing ones, or delete them by ID. Deletion recalculates running totals. Invalid IDs are rejected with an error message. Each journey records the same fields as Part 1 (unique ID, date/time, from zone, to zone, time band, passenger type, zones crossed, base fare, discount applied, and charged fare).

**CSV import and export**: the rider can import journeys from a CSV file and export the current day's journeys to CSV, so now that's not the memory-only program from Part 1.

**Fare calculation and daily cap**: fares are calculated the same way as Part 1, zones crossed, peak/off-peak band, passenger discounts from the dataset, and the daily cap for each passenger type still applies.

**End-of-day summary and summary export**: the program produces a detailed end-of-day summary with total journeys, total cost, average cost per journey, most expensive journey by ID, whether the cap was reached and any savings made, and a breakdown by peak/off-peak and zone pair counts. This summary should be exported in two formats: a CSV report with line items, and a human-readable text file saved with the date and rider name.

**Admin configuration management**: an admin logs in to a password-protected menu. From there he can view the active system configuration and add, update, or delete base fares (by zone and peak), passenger discounts, daily caps, and peak time windows. All changes are validated before saving.

**Configuration file loading**: On launch, the system loads its configuration from a file. If the file is missing, it falls back to safe default values, preventing crashes.

**Save on exit**: when the rider exits, the program offers to save their current day's state (profile and journeys) to the file.

---

### **System Constraints**

**Two modes:** the program operates in either Rider or Admin mode, the two roles cannot be active at the same time.

**Password-protected admin access:** the admin menu is only accessible via a correct password, no admin functions are available to the rider.

**File formats:** configuration and profiles are stored as JSON, journeys and reports are stored as CSV. 

**Persistent storage across sessions:** compare to Part 1, data is no longer lost when the program ends, profiles and journeys can be saved and reloaded.

**Single-day scope retained:** the program still tracks journeys for one day at a time, the reset/new day concept from Part 1 carries over.

**Validation on all inputs:** all fare rule changes entered by the admin and all journey inputs from the rider must be validated, on failure the system does not save and displays a clear error message.

**Fare calculation rules:** the same zone range (1–5), time bands (peak/off-peak), passenger type discounts, and daily cap logic same as in Part 1.

**Config missing:** if the configuration file is not found at launch, the program must start with safe defaults values rather than crashing.

**Journey uniqueness:** each journey retains a unique ID within a session, and invalid IDs must be rejected.

---

### Input Process Output Table

<style>
</style>

| Feature / Task           | Inputs                                                                                                                        | Processing                                                                                                                                                                               | Outputs                                                                                         |
| ------------------------ | ----------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------- |
| Start program            | None                                                                                                                          | Load config from JSON file; if file missing use safe defaults; initialise RiderUser; initialise AdminUser; display role selection menu                                                   | Role selection menu displayed                                                                   |
| Role selection           | role (String): "rider" or "admin"                                                                                             | Read user choice; validate input; route to Rider menu or Admin login                                                                                                                     | Rider menu or admin login prompt displayed                                                      |
| Create profile           | name (String); passengerType (PassengerType); defaultPayment (String)                                                         | Validate all fields; create RiderProfile object; save to JSON file                                                                                                                       | Profile saved; confirmation message displayed                                                   |
| Load profile             | fileName (String)                                                                                                             | Read JSON file; parse into RiderProfile object                                                                                                                                           | Profile loaded; rider menu displayed with name                                                  |
| Save profile             | riderProfile (RiderProfile); journeys (List<Journey>)                                                                         | Serialise profile and current journeys to JSON; write to file                                                                                                                            | File saved; confirmation message displayed                                                      |
| Menu selection           | choice (int)                                                                                                                  | Validate using readMenuChoice(min, max); route using switch statement to extended menu options                                                                                           | Selected function executed                                                                      |
| Add journey              | date (LocalDate); fromZone (int); toZone (int); timeBand (TimeBand); passengerType (PassengerType); runningTotal (BigDecimal) | Validate inputs; calculate zonesCrossed; retrieve baseFare; calculate discountedFare; apply daily cap; create Journey object; update totals; increase nextJourneyID                      | Journey stored; confirmation message; journey details displayed; running total updated          |
| Edit journey             | journeyID (int); updated fields: fromZone, toZone, timeBand, passengerType                                                    | Validate ID exists; update journey fields; recalculate zonesCrossed, baseFare, discountedFare; recalculate totals and daily cap for all journeys                                         | Journey updated; updated details displayed; totals recalculated                                 |
| Remove journey           | journeyID (int); confirmation (boolean)                                                                                       | Validate ID; prompt confirmation; remove from list; recalculate totals and daily cap                                                                                                     | Removal confirmation or error message; updated totals displayed                                 |
| List journeys            | journeys (List<Journey>)                                                                                                      | Retrieve all journeys in order entered; display cap/pass applied flag per journey                                                                                                        | All journeys displayed with running totals                                                      |
| Import journeys from CSV | filePath (String)                                                                                                             | Read CSV file line by line; parse each row into Journey fields; validate each record; calculate fares; apply daily cap; add valid journeys to session list; skip and report invalid rows | Journeys added; import summary shown (imported count, skipped count, errors)                    |
| Export journeys to CSV   | journeys (List<Journey>); fileName (String)                                                                                   | Serialise each Journey to CSV row with all fields; write to file with header row                                                                                                         | CSV file saved; confirmation with file path displayed                                           |
| Filter by passenger type | passengerType (PassengerType)                                                                                                 | Loop through journeys; compare passengerType                                                                                                                                             | Filtered list displayed                                                                         |
| Filter by time band      | timeBand (TimeBand)                                                                                                           | Loop through journeys; compare timeBand                                                                                                                                                  | Filtered list displayed                                                                         |
| Filter by zone           | zone (int)                                                                                                                    | Compare fromZone or toZone                                                                                                                                                               | Filtered list displayed                                                                         |
| Filter by date           | date (LocalDate)                                                                                                              | Compare journey date                                                                                                                                                                     | Filtered list displayed                                                                         |
| Daily summary            | journeys (List<Journey>)                                                                                                      | Calculate total journeys; sum chargedFare; calculate average; find highest chargedFare with ID; check capReached per passenger type; calculate savings vs uncapped total                 | Summary displayed: journey count, total cost, average cost, most expensive journey, cap savings |
| Category counts          | journeys (List<Journey>)                                                                                                      | Loop through journeys; count by TimeBand (PEAK / OFF_PEAK); count by zone pair (fromZone-toZone); count zone involment                                                                   | Category counts displayed                                                                       |
| Totals by passenger type | passengerTotals (Map<PassengerType, PassengerTotals>)                                                                         | Count journeys; sum preDiscountedTotal; sum discountedTotal; sum chargedTotal; check capReached                                                                                          | Totals displayed per passenger type                                                             |
| Export summary report    | journeys (List<Journey>); riderName (String); date (LocalDate)                                                                | Generate CSV report with line items; generate human-readable text summary; write both files with date and rider name in filename                                                         | CSV report file saved; text summary file saved; confirmation with file paths displayed          |
| Reset day                | confirmation (boolean)                                                                                                        | Clear journeys list; reset passengerTotals; reset nextJourneyID                                                                                                                          | Reset confirmation message displayed                                                            |
| Save and exit            | confirmation (boolean)                                                                                                        | Prompt user to save; if confirmed save profile and journeys to JSON; terminate program                                                                                                   | Save confirmation or skip message; goodbye message displayed                                    |
| Admin login              | password (String)                                                                                                             | Compare input against stored password; if match give access, if fail show error                                                                                                          | Admin menu displayed or access denied message                                                   |
| View config              | config (SystemConfig)                                                                                                         | Read active config object; format and display all fare rules, discounts, caps, peak windows                                                                                              | Full config displayed in console                                                                |
| Add / update base fare   | zones (int); isPeak (boolean); fareAmount (BigDecimal)                                                                        | Validate zones range (1-5); validate fareAmount > 0; update fare entry in config; save config to JSON                                                                                    | Updated fare saved; confirmation or validation error message                                    |
| Delete base fare         | zones (int); isPeak (boolean); confirmation (boolean)                                                                         | Check entry exists; prompt confirmation; remove from config; save config to JSON                                                                                                         | Deletion confirmation or error message                                                          |
| Add / update discount    | passengerType (PassengerType); discountRate (double)                                                                          | Validate discountRate between 0.0 and 1.0; update discount in config; save config to JSON                                                                                                | Updated discount saved; confirmation or validation error message                                |
| Add / update daily cap   | passengerType (PassengerType); capAmount (BigDecimal)                                                                         | Validate capAmount > 0; update cap in config; save config to JSON                                                                                                                        | Updated cap saved; confirmation or validation error message                                     |
| Add / update peak window | startTime (LocalTime); endTime (LocalTime)                                                                                    | Validate startTime before endTime; update peak window in config; save config to JSON                                                                                                     | Updated peak window saved; confirmation or validation error message                             |
| Config load fallback     | filePath (String)                                                                                                             | Attempt to read config JSON; if file missing or corrupt load default values; log warning to console                                                                                      | System ready with defaults; warning message displayed                                           |

---

## Research (minimum of 1 required, preferrebly 2)

---

*Research existing programs that solve a similar problem. The program does not have to be written in java or object orientated in nature - just solve a similar type of problem.*

*Use the strucutre below to capture your evidence:*

------------------------------------------------------------------------------------------------------------------------------Name of program: BankApp

Reference (link): github.com/BasharShehab/BankApp

What it does well (2-3 features that work effectively):

Clear separation between manager and customer menus, each role only sees options relevant to them with their own logins.

Password-protected manager login, unauthorised users cannot access admin functions

Data is saved to files between sessions so nothing is lost when the program closes

What it does poorly (at least 1 feature):

Not everywhere input validation exists, entering letters where a number is expected crashes the program

To access cusomer menu firstly you need to log in to admin menu and create customer with it's own log in details 

Key design ideas you could reuse (e.g., layout, navigation, input/output, program structure):

The switch statement structure for routing between role menus is clean and easy to follow, I plan to use the same approach in CityRide Lite Part 2 for Rider and Admin routing.

Screenshot (showing the interface/output):

![gfd](/Users/dushesssx/Desktop/research%20BankApp.png)

details to log in to amdin menu

---

Name of program: Console-based Expense Tracker

Reference (link): github.com/yashsanodiya89/Console-based-expense-tracker

What it does well (2-3 features that work effectively):

Each expense is stored with details: ID, amount, category, description and date, and saved to a file for future use, which is very similar to how I plan to store journey records.

The user can delete a specific journey by its ID, after which the updated list is saved to a file, I use the same approach in Remove Journey.

The data is stored in separate files organised by type (expense.txt, categories.txt), a simple and clear way of organising the data

What it does poorly (at least 1 feature):

There is no import function, data can only be saved, but cannot be loaded from an external file. In my program, I need to implement both import and export functions.

Key design ideas you could reuse (e.g., layout, navigation, input/output, program structure):

A pattern where each object is written as a single line to a file and read back at program start-up. I think I can use the same approach for Journey import/export via CSV.

Screenshot (showing the interface/output):

![gdfg](/Users/dushesssx/Desktop/research%20expense_tracker.png)

Main menu and example of how program reads expenses from a file, added in previous program run.

---

### Algorithm design

---

Full algorithm overview (photo with high quality available on GitHub)

![gf](/Users/dushesssx/Downloads/4113_%20full%20algorithm%20overview.jpg)

Main loop and rider menu subroutine

![gfd](/Users/dushesssx/Downloads/4113%20algorithm-3.jpg)

Admin menu

![gd](/Users/dushesssx/Downloads/4113%20algorithm%20-%20Copy%20of%20full%20algorithm%20overview.jpg)

Subroutine add journey, list journey, remove journey and reset day 

![gd](/Users/dushesssx/Downloads/4113%20algorithm%20-%201.jpg) 

Subroutines filter journey, import from CSV file and Export to CSV file 

![gfd](/Users/dushesssx/Downloads/4113%20algorithm%20-%20Frame%206.jpg)

Subroutines edit journey, category counts, passenger totals, export summary report, daily summary

![gfd](/Users/dushesssx/Downloads/4113%20algorithm%20-%20Frame%207-2.jpg)

---

### Class diagram

---

![gdf](/Users/dushesssx/Downloads/IY4113%20class%20diagram_Part%202.png)

---

### Gantt chart

![gfd](/Users/dushesssx/Desktop/Gantt%20chart.png)

---

### Diary Entries

---

### 19/03/2026 - Diary Entry 1 – Understanding the program purpose and constraints, creating Gantt chart

Today I started working on Milestone 1 of the CityRide Lite Part 2 assignment. I began by reading through the full specification to understand what has changed compared to Part 1. I then wrote the purpose of the program, identfied its core functionality and system constraints. I also created a Gantt chart to plan the remaining weeks up to the final submission deadline.

Overall, there weren’t any problems today, it’s much easier to do this the second time. And also, after receiving full feedback on Part 1, I know where my weaknesses were and will try to avoid them in the second part.

### 20/03/2026 - Diary Entry 2 – Input Process Output table

Today I continued working on Milestone 1 of the CityRide Lite Part 2 assignment. I completed the IPO table for all functions in the program, identifying inputs, processing steps and outputs for each feature. This includes both functions carried over from Part 1 and all the new functions introduced in Part 2, such as profile management, file import/export, administration settings and role selection. I have also checked and corrected several input field, for example, I replaced "None" with "journeys (List<Journey>)" for List Journeys and added "config(SystemConfig)"" as the input for View Config. I'm not sure now how I will implement configuration logic but I plan to create a SystemConfig class to store all system settings in one place (fares, discounts, caps, peak times). This will make it easy to pass into methods that need config data, and simple to load and save as a single JSON file.

For some functions that do not require direct user input, such as List Journeys and View Config, I initially left the input field blank. However, I remembered that there was a question mark next to such a field in the feedback for Part 1, so I have now filled them in. 

### 21/03/2026 - Diary Entry 3 – Research similar programs

Today I worked on the research part. I looked for similar programs to prepare my design decisions for my program,. I found and analysed two programs: BankApp (role-based menu system with admin password) and Console-based Expense Tracker (file saving and record management by ID).

Finding suitable programs was harder than expected. Since the previous tasks were easier the second time round, the research turned out to be even more difficult because I was specifically looking for Java applications. Many GitHub projects that looked relevant were too hard to understand. They either had too many dependencies and would not run in IntelliJ without extra setup, that I don't now how to do now, or they did not actually have the features described in their README. For example, one project claimed to have CSV import/export but only had a lot of red errors. I also found that some projects with an admin/user role separation turned out to only have a basic login system with no real role-based menus. In the end I had to try several projects before finding ones that were simple enough to run and relevant enough to my own program.

### 22/03/2026 - Diary Entry 4 – Algorithm and class diagram creating

Today I completed the design section of Milestone 1. I finished drawing the class diagram, extending my Part 1 diagram with new classes including RiderProfile, SystemConfig, AdminService, RiderService, abstract FileHandler, JsonFileHandler, CsvFileHandler, and ReportExporter. I also added inheritance between FileHandler and its two child classes to practice on object-oriented principles covered in class, and I think it's good choice to use inheritance in this case, because they share common behaviour for different type of files. After receiving feedback on the Part 1 class diagram, I fixed my class diagram notation.
I also completed the algorithm design by creating flowcharts for all new subroutines: Start with role selection, Rider menu, , Edit Journey, Import CSV, Export CSV, Export Summary Report,  Admin Menu with all features in 1 algorithm. I kept all existing Part 1 flowcharts unchanged and only added the new ones on top.

Deciding how to structure the class diagram and algorithm took a lot of time. In class diagram specifically with inheritance, cause I want to implement this in my program for practice. I also wanted to implement inheritance in RiderSystem and AdminSystem, but then decided to use it only in file handling. The FileHandler inheritance felt more justified.
