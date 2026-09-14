# Requirements and Test Oracles

## Functional Requirements
1. The system shall allow a user to create a player character given their chosen rules system
2. The system shall allow a user to save their character's info as a PCG file
3. The system shall allow a user to roll the stats for a player character
4. The system shall allow a user to add an image representing their player character to their character sheet. 
...

## Non-Functional Requirements
1. The system shall function on Windows, macOS, and Linux if they have the correct versions of Java installed
2. The system shall provide a fair distribution of each value within range when performing a dice roll. 
...

## Test Oracles

| Requirement ID | Requirement Description | Test Oracle (Expected Behavior) |
|-----------------------|-----------------------------------|---------------------------------------------|
| FR-1                   | The system shall allow a user to create a player character given their chosen rules system| After the user selects their fules system and the details of their character, the character should be generated successfully and available for further editing|
| FR-2                   | The system shall allow a user to save their character's info as a PCG file| After saving a character, a PCG file should appear which should allow the user to open their character with the saved information|
| FR-3                   | The system shall allow a user to roll the stats for a player character | After clicking "Roll" within the Ability Scores section, each stat value for the character sheet should be filled in with a random value|
| FR-4                   | The system shall allow a user to add an image representing their player character to their character sheet.  | When uploading an image, it should be saved and viewable on the output of the character sheet. |
| NFR-1                | The system shall function on Windows, macOS, and Linux if they have the correct versions of Java installed | When the system is installed in Windows, macOS, and Linux operating systems it should launch normally and provide its intended functionality regardless of platform. |
| NFR-2                | The system shall provide a fair distribution of each value within range when performing a dice roll.  | After performing 500 dice rolls of a single type, each possible result should occur approximately the same number of times. |

