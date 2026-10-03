# SquadLink

[![Build Status](https://github.com/AY2627S1-CS2103T-F11-3/tp/actions/workflows/gradle.yml/badge.svg)](https://github.com/AY2627S1-CS2103T-F11-3/tp/actions)

![SquadLink user interface](docs/images/Ui.png)

SquadLink is a desktop application for coaches of small youth football clubs who manage multiple squads and their players' guardians.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Scope](#scope)
- [Getting Started](#getting-started)
- [Documentation](#documentation)
- [Acknowledgements](#acknowledgements)

## Overview

Managing multiple squads through old chats and scattered notes makes it difficult to know who is available and who to contact. SquadLink keeps player details, squad information and guardian contacts in one place, helping coaches select a matchday squad and reach the right people quickly.

## Features

### Player management

- Add players with their squad, position and level.
- Edit player details, including their squad and position.
- Track availability, injury status and consent status.
- Add one short note for each player.
- Mark fees as paid or unpaid.
- Remove or archive players who have left the club.

### Guardian and club contacts

- Link players to their guardians' contact details.
- Record each guardian's preferred contact method.
- Find a guardian using the associated player's name.
- Store other club contacts, such as opposition coaches, referees and venues.

### Search and filtering

- Filter players by squad and availability.
- Search across player names, squads, positions and levels.

## Scope

SquadLink is designed for player, guardian and club-contact management. It does not include:

- Training plans or drills
- Match results or statistics
- Medical records beyond an injury flag
- Fee amounts or balances
- Messaging, fixture scheduling or venue booking
- Sharing with assistants, parents or club administrators
- Contact importing
- Photos or documents

## Getting Started

### Prerequisites

- Java 17 or later
- A Git client

### Running SquadLink

Clone the repository and run the application with Gradle:

```bash
git clone https://github.com/AY2627S1-CS2103T-F11-3/tp.git
cd tp
./gradlew run
```

On Windows, use `gradlew.bat run` instead of `./gradlew run`.

### Running the tests

```bash
./gradlew test
```

## Documentation

For the full user and developer documentation, visit the [SquadLink product website](https://nus-cs2103-ay2627-s1.github.io/tp/).

## Acknowledgements

This project is based on the AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org).
