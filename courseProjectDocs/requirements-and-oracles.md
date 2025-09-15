# Requirements and Test Oracles

## Functional Requirements
1. The system shall allow a user to set their character's ability scores.
2. The system shall allow a user to enter basic character information.
3. The system shall generate adjustments to ability modifiers.
4. The system shall generate and display a character sheet containing all relevant information.
5. The system shall allow a user to enter and set their chosen weapon.
6. The system shall generate weapon damage and attack modifiers based on entered information.
7. The system shall allow a user to select feats for their character.
8. The system shall generate adjustments to damage and ability modifiers based on entered feats.
9. The system shall generate available spells based on the applicable entered classes.


## Non-Functional Requirements
1. The system shall be an application accessible via download.
2. The system shall be compatible with Java as well as Windows and Mac operating systems.
3. The system shall be adaptable to future versions of RPGs.


## Test Oracles

| Requirement ID | Requirement Description | Test Oracle (Expected Behavior) |
|-----------------------|-----------------------------------|---------------------------------------------|
| FR-1                   | The system shall allow a user to set their character's ability scores.| After editing ability scores, they shall be updated in the character sheet.|
| FR-2                   | The system shall allow a user to enter basic character information.| When character information is selected, it should be added to the character sheet.|
| FR-3                | The system shall generate adjustments to ability modifiers. | Once ability scores are entered or updated, the system should calculate ability modifiers based on the basic character information and ability scores. |
| FR-4                   | The system shall generate and display a character sheet containing all relevant information. When the character sheet tab is selected, the system shall compile all relevant information and display it. |
| FR-6                   | The system shall generate weapon damage and attack modifiers based on entered information. | When a weapon is selected, the system shall calculate the relevant damage and attack modifiers. |
| NFR-2                   | The system shall be compatible with Java as well as Windows and Mac operating systems. | When the Zip or installer is downloaded, it should be able to be run with Java or on Mac and Windows operating systems.  |
