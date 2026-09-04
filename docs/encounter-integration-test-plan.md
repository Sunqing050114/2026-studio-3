# Team 2 Encounter Integration and Sprint 2 Acceptance Test Plan

Owner: Guoqing Sun (`@Sunqing050114`)

Related work:

- [Team 2 feature ticket #6](https://github.com/UQcsse3200/2026-studio-3/issues/6)
- [Integration subtask #68](https://github.com/UQcsse3200/2026-studio-3/issues/68)
- [Integration pull request #69](https://github.com/UQcsse3200/2026-studio-3/pull/69)

Sprint 2 acceptance baseline: `main` at `ac6c8ab` (3 September 2026).

## Scope and quality goals

This plan verifies the complete Team 2 boundary from a selected Map node, through a Chance or Shop
encounter, to Player/Card/Deck mutation and the final Map completion callback. The main risks are a
partially applied transaction, an encounter completing more than once, stale callbacks advancing
the wrong node, and incompatible cross-team APIs.

An integration build is acceptable only when:

1. all automated tests and `spotlessCheck` pass;
2. failed Chance and Shop operations leave Player, Deck, stock, and Map state consistent;
3. completion is forwarded exactly once for the active node;
4. both end-to-end routes pass the manual checks below using the final merged team APIs; and
5. the final `main` branch passes CI after the Team 2 feature is merged.

## Automated test traceability

| Requirement / risk | Test class | Evidence |
| --- | --- | --- |
| Chance applies mixed health/currency outcomes atomically | `ChanceOutcomeApplierTest` | success, rejected value, overflow, rollback and rollback-failure cases |
| A Chance choice cannot mutate state or complete twice | `ChanceEncounterSessionTest` | invalid choice, precondition, already-resolved and one-callback cases |
| Shop checks catalogue, funds and deck before committing | `IntegratedShopTransactionGatewayTest` | success, missing card, insufficient funds, deck rejection and rollback cases |
| Shop stock changes only after a successful external transaction | `ShopServiceIntegrationTest` | success and failed gateway cases |
| Map progression follows Chance -> Shop -> return | `EncounterFlowControllerTest` | full round trip with Player, Deck, stock and node assertions |
| Only one encounter can be active | `EncounterFlowControllerTest` | concurrent start is rejected |
| Invalid starts do not poison controller state | `EncounterFlowControllerTest` | null/blank node and failed construction recovery |
| Stale or duplicate callbacks cannot advance Map | `EncounterFlowControllerTest` | stale/duplicate callback test |
| The Map callback may synchronously launch the next encounter | `EncounterFlowControllerTest` | re-entrant next-encounter test |
| Sprint 2 acceptance regressions remain safe through the public encounter APIs | `EncounterAcceptanceRegressionTest` | duplicate Shop completion, insufficient funds, and rejected Deck update |
| Existing inventory-backed Shop API remains compatible | `InventoryComponentTest`, `ShopEncounterTest`, `ShopServiceTest` | legacy constructor and purchase behaviour |

Run from `source/`:

```bash
./gradlew test spotlessCheck
```

For PR #69, the initial GitHub Actions revision ran 257 tests successfully. On revision `617d69f`,
the `Run Unit Tests` step and the separate Java Format workflow both passed. The Sprint 2 regression
class was then verified on review commit `f2032a4`: all three tests passed in
[Game Unit Tests run #14](https://github.com/Sunqing050114/2026-studio-3/actions/runs/33870574788),
and [Java Format run #14](https://github.com/Sunqing050114/2026-studio-3/actions/runs/33870574719)
and Javadoc passed. The full repository suite remains red because 31 existing Battle, Enemy, Map
and RunState tests fail on this baseline; none of the three acceptance regressions failed. The
SonarCloud check on the fork cannot authenticate without the course repository's `SONAR_TOKEN`.
A final run is still required after the team's integration fixes are merged to `main`.

## Manual acceptance tests

Run these checks against the final integrated build. Record the tester, date, build/commit URL and
result in the result table; do not mark a row passed from unit-test evidence alone.

### M1 — Chance success and Map continuation

1. Start the game and select a Chance node from the Map.
2. Record initial health, currency and node states.
3. choose the shrine risk/reward option and continue.
4. Verify that the exact health/currency outcome is applied once.
5. Verify that the Chance node completes and the adjacent Shop node becomes available.

Expected: one Player update, one completion callback, no duplicate reward, and only the intended
neighbour is unlocked.

### M2 — Chance failure/cancellation consistency

1. Launch a Chance encounter with insufficient currency for a negative-currency outcome.
2. Attempt that choice, then cancel/leave the encounter.
3. Compare health, currency and Map state with the recorded starting values.

Expected: the failed outcome retains no partial Player change; cancellation returns control without
advancing Map progression.

### M3 — Shop success

1. Open a Shop node with enough currency and a valid Card Library ID.
2. Record currency, target card count and stock.
3. Buy the item and leave the Shop successfully.

Expected: currency decreases by exactly the listed price, the deck gains one matching card, stock
decreases once, the Shop node completes, and the next node unlocks.

### M4 — Shop failure and rollback

Repeat a purchase with (a) insufficient currency, (b) an unknown Card Library ID, and (c) a Deck
rejection. Compare Player, Deck and stock state before and after each attempt.

Expected: each attempt shows the correct failure reason and leaves currency, Deck and stock
unchanged. No failed attempt advances the Map.

### M5 — lifecycle and UI guard

1. Double-click Continue/Leave during completion.
2. Attempt to start another encounter while one is open.
3. Re-open the Map after completion and select the newly available node.

Expected: no duplicate reward/purchase/completion callback, no overlapping encounter UI, and the
next encounter starts normally.

## Manual result record

| ID | Tester and date | Commit/build | Result | Evidence / defect link |
| --- | --- | --- | --- | --- |
| M1 | Guoqing Sun — pending | `ac6c8ab` baseline | Blocked | `MapScreen` currently opens the placeholder `EncounterScreen`, not the real Chance UI |
| M2 | Guoqing Sun — pending | `ac6c8ab` baseline | Blocked | Requires the final Chance UI and Player wiring |
| M3 | Guoqing Sun — pending | `ac6c8ab` baseline | Blocked | Requires the final Shop UI, CardService and PlayerDeck wiring |
| M4 | Guoqing Sun — pending | `ac6c8ab` baseline | Blocked | Automated state-invariant coverage exists; manual UI evidence still requires final wiring |
| M5 | Guoqing Sun — pending | `ac6c8ab` baseline | Blocked | Requires the real encounter screens and final Map return path |

## Cross-team contract checks

Before Sprint 2 acceptance is signed off, pair with the owning teams and record the relevant PR,
issue comment or test evidence for each boundary:

| Owner | Contract to verify | Current Sprint 2 baseline status |
| --- | --- | --- |
| Team 4 Map | node-ID type and `onEncounterComplete` behaviour | Integer node IDs now align; `MapScreen` still sends every room type to a placeholder screen |
| Team 5 Deck | add/remove semantics and rollback support | `PlayerDeck` is present on `main`; production Shop wiring and failure semantics remain to be verified |
| Team 6 Card Library | canonical ID lookup | `CardService#getCard(String)` is present; production Shop lookup wiring remains to be verified |
| Team 7 Player/Economy | health and currency setters, limits and failure behaviour | component adapter tests exist; the final in-game Player entity wiring remains to be verified |
| Team 3 UI/Game Flow | screen ownership, completion and leave semantics | real Chance/Shop screen routing and return-to-Map acceptance remain outstanding |

## Sprint 2 known issues and limitations

- The baseline `EncounterScreen` is explicitly a placeholder and does not render the real Chance or
  Shop interaction.
- `EncounterFlowController` is covered by automated tests but is not constructed by production
  screen code on the baseline commit.
- `InventoryDeckAdapter` is still documented as temporary; the final Shop-to-`PlayerDeck` wiring is
  owned by the Shop expansion task.
- M1–M5 cannot be marked Passed until the final feature branches are merged and the manual steps are
  run against the resulting build with evidence.
- The acceptance owner may fix small defects found while running M1–M5, but major Map, Player, Card,
  Deck or screen integration remains with the corresponding Sprint 2 task owner.

## Changes to the original plan

Directly importing every other team's implementation was rejected because those APIs were still
changing independently. Small gateway interfaces and adapters were introduced instead, keeping
Chance/Shop rules stable and isolating changes such as Team 4's integer node IDs and Team 5's
`void`-returning deck addition. Transaction rollback was added after analysing the failure window
between adding a card and deducting currency. Re-entrant and stale-callback tests were added because
Map/Game Flow may synchronously launch the next encounter from the completion callback.

The final decision is not complete until the production screens use the merged Team 5/6/7 and Map
interfaces, all manual rows are run, and the resulting commit passes CI on `main`.
