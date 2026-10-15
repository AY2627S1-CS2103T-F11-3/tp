# SquadLink v1.3 Release Notes

SquadLink is a desktop application for youth football coaches to manage player
profiles, squad membership, and availability through a command-line interface.

## What's New Since AddressBook Level 3

- Replaced general contact records with player profiles containing a squad,
  football position, guardian name, guardian contact, availability, tags, and
  optional remarks.
- Added player-focused `add` and `edit` commands, including full and shorthand
  prefixes for player details.
- Added `filter` to display players by squad, availability, or both, while
  keeping `find` for name-based searching.
- Reworked the main player display into an indexed table with aligned player
  details and colour-coded availability. Table indexes match the indexes used
  by player commands.
- Added `overview` to show each squad's total, available, and unavailable
  player counts, including warnings for squads without any available players.
- Added `main` to return from the overview to the previously displayed player
  list without clearing its active search or filter.
- Improved deletion: coaches can delete multiple players by displayed index or
  by full name, with safeguards for ambiguous names and invalid input.
- Updated sample data with realistic squads, positions, guardians, contacts,
  player availability, and football-related tags.

## Screenshots

### Indexed player table

![Indexed player table](images/Ui.png)

### Club overview

![Club overview](images/overviewCard.png)