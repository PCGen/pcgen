# Requirements and Test Oracles

## Functional Requirements
1. The system shall allow a user to create a player character given their chosen rules system
2. The system shall allow a user to save their character's info as a PCG file
3. The system shall allow a user to load character data from a PCG file
4. The system shall perform random dice rolls when needed according to the specified die and quantity of dice
...

## Non-Functional Requirements
1. The system shall function on Windows, macOS, and Linux if they have the correct versions of Java installed
2. The system shall be able to properly read or migrate files from older versions of PCGen to the current version
...

## Test Oracles

| Requirement ID | Requirement Description | Test Oracle (Expected Behavior) |
|-----------------------|-----------------------------------|---------------------------------------------|
| FR-1                   | The system shall allow a user to create a player character given their chosen rules system| After the user selects their fules system and the details of their character, the character should be generated successfully and available for further editing|
| FR-2                   | The system shall allow a user to save their character's info as a PCG file| After saving a character, a PCG file should appear which should allow the user to open their character with the saved information|
| FR-3 | The system shall allow a user to load character data from a PCG file | After loading a character from a given PCG file, the data of the character should appear in the main browser|
| FR-4 | The system shall perform random dice rolls when needed according to the specified die and quantity of dice | In situations where a random dice roll of specified quanity and dice is required, the system should be able to perform the specified and report the results to the user.|
| NFR-1                | The system shall function on Windows, macOS, and Linux if they have the correct versions of Java installed | When the system is installed in Windows, macOS, and Linux operating systems it should launch normally and provide its intended functionality regardless of platform. |
| NFR-2 | The system shall be able to properly read or migrate files from older versions of PCGen to the current version | When a user attempts to load a file created in an older version of PCGen, it should be able to be loaded, or migrated/updated to support the current version if loading 1:1 is impossible. |

