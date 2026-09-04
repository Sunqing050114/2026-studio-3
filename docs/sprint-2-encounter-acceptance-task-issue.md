# [Team 2][Sprint 2] Encounter Acceptance Testing and Documentation

## Description

**Task:** Encounter Acceptance Testing and Documentation  
**Feature:** #6 — Non-Battle Encounters / Chance Encounters / Shop Encounters

Validate the final Sprint 2 Chance and Shop flow without taking ownership of the major cross-team
adapters. This task adds focused regression coverage, runs and records the M1–M5 manual acceptance
scenarios after the feature owners merge their work, maintains the integration test documentation,
records known issues, and fixes only small defects discovered during acceptance.

## Scope

### In scope

- [ ] Run and record manual acceptance tests M1–M5 on the final integrated build.
- [x] Add three focused regression tests through the encounter and Shop integration APIs.
- [x] Verify duplicate completion callbacks cannot complete an encounter twice.
- [x] Verify insufficient-funds purchases do not change money, Deck contents, or stock.
- [x] Verify rejected Deck updates do not change money, Deck contents, or stock.
- [x] Update the integration test plan with Sprint 2 traceability and current blockers.
- [x] Record remaining limitations and known issues.
- [ ] Fix small defects found during final acceptance, if any.
- [ ] Attach final test/CI and M1–M5 evidence.

### Out of scope

- Major Map/Game Flow lifecycle implementation.
- Team 5 PlayerDeck, Team 6 CardService, or Team 7 Player adapter ownership.
- Chance content/configuration expansion.
- Shop inventory/card integration ownership.
- Chance or Shop UI implementation.

## Dependencies

- [ ] Real EVENT and SHOP node routing is merged by the lifecycle owner.
- [ ] Final Chance content and Player outcome wiring are merged.
- [ ] Final Shop, CardService, PlayerDeck, and economy wiring are merged.
- [ ] Final Chance/Shop UI and return-to-Map interactions are merged.

## Acceptance criteria

- [ ] `./gradlew test spotlessCheck` passes on the review branch.
- [ ] The final merged `main` CI is green.
- [ ] M1–M5 each record tester, date, commit/build, result, and evidence or defect link.
- [ ] Duplicate UI/callback events advance the Map no more than once.
- [ ] Every failed Shop purchase leaves money, Deck, and stock unchanged.
- [ ] Known limitations are linked to the responsible issue or task owner.

## Documentation

- `docs/encounter-integration-test-plan.md`
- `source/core/src/test/com/csse3200/game/encounters/integration/EncounterAcceptanceRegressionTest.java`

## Member

- Guoqing Sun (`@Sunqing050114`)
