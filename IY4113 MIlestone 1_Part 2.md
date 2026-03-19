### IY4113 Milestone 1

| Assessment Details | Please Complete All Details                                      |
| ------------------ | ---------------------------------------------------------------- |
| Group              | Group A                                                          |
| Module Title       | Applied Software Engineering using Object Orientated Programming |
| Assessment Type    | java fundamentals part 2                                         |
| Module Tutor Name  | Jonathan Shore                                                   |
| Student ID Number  | P485902                                                          |
| Date of Submission | 22/03/2026                                                       |

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


