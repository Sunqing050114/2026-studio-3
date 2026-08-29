# Card Data Model

This page describes the structure and validation rules of card data: what a card definition contains, which values are valid, and how to add new cards.

> **Owner:** Card Data Model (#31)  
> **Related pages:** [Card Library API](Card-Library-API) · [Initial Cards](Initial-Cards)

## Contents

- [Overview](#overview)
- [CardConfig](#cardconfig)
- [EffectConfig](#effectconfig)
- [Enumerations](#enumerations)
- [Validation contract](#validation-contract)
- [JSON format](#json-format)
- [Adding a new card](#adding-a-new-card)
- [Known limitations](#known-limitations)
- [Test coverage](#test-coverage)

## Overview

Card definitions are stored in [`source/core/assets/configs/cards.json`](../../source/core/assets/configs/cards.json) and deserialised into `CardConfig` objects.

A card's category (`CardType`) is deliberately separate from its behaviour (`effects`). This allows any card type to contain any combination of effects. For example, an `ATTACK` card can both deal damage and apply a debuff.

Adding a card normally requires editing JSON only. Java changes are needed only when the card requires a new effect type.

## CardConfig

**Class:** `com.csse3200.game.cards.configs.CardConfig`

`CardConfig` uses public fields without getters, following the convention used by `PlayerConfig` and `NPCConfigs` elsewhere in the codebase.

| Field | Type | Default | Required | Description |
|---|---|---:|:---:|---|
| `id` | `String` | `""` | Yes | Unique lookup key. Must not be blank or contain leading or trailing whitespace. |
| `name` | `String` | `""` | Yes | Name displayed to the player. |
| `description` | `String` | `""` | Loader only | Rules text displayed to the player. See [Description validation](#description-validation). |
| `cost` | `int` | `0` | No | Energy required to play the card. Must be `>= 0`; zero is valid. |
| `type` | `CardType` | `ATTACK` | Yes | Card category. |
| `rarity` | `Rarity` | `COMMON` | Yes | Rarity used when offering reward cards. |
| `target` | `TargetType` | `SINGLE_ENEMY` | Yes | Target shared by all effects on the card. |
| `effects` | `EffectConfig[]` | Empty array | Yes | Effects applied in array order. At least one entry is required. |
| `texturePath` | `String` | `""` | Yes | Card artwork path relative to the assets directory. |

### Description validation

`CardValidator` does not require `description`, but `CardConfigLoader` rejects JSON in which `description` is missing or `null`.

As a result:

- a manually constructed `CardConfig` with a blank description passes validation;
- the same card loaded from JSON is rejected.

This inconsistency is tracked under [Known limitations](#known-limitations).

### Targeting model

The `target` field belongs to the card, not to individual effects. One target therefore governs every effect on the card.

For example, the current model cannot represent a card that deals damage to an enemy and grants block to the player. Supporting mixed targets would require moving `target` onto `EffectConfig`.

## EffectConfig

**Class:** `com.csse3200.game.cards.configs.EffectConfig`

| Field | Type | Default | Description |
|---|---|---:|---|
| `type` | `EffectType` | `DAMAGE` | Kind of effect. |
| `value` | `int` | `0` | Effect magnitude, interpreted according to `type`. Must be strictly positive. |
| `duration` | `int` | `0` | Number of turns the effect lasts. Used only when `type.usesDuration()` is `true`. |

### Constructors

```java
new EffectConfig();
// DAMAGE, value 0, duration 0

new EffectConfig(EffectType type, int value);
// duration defaults to 0

new EffectConfig(EffectType type, int value, int duration);
```

> [!WARNING]
> The two-argument constructor sets `duration` to `0`. Using it for `POISON` or `VULNERABLE` creates an invalid effect because these types require `duration > 0`. Use the three-argument constructor for duration-based effects.

### Why `effects` is an array

`effects` is intentionally declared as `EffectConfig[]`, rather than `List<EffectConfig>`. The shared static `Json` instance in `FileLoader` cannot resolve the element type of `List<T>` because of generic type erasure, so a list does not deserialise correctly.

`CardConfigTest.shouldDeclareEffectsAsArray` verifies the array declaration through reflection. Changing the field to a list therefore fails the build instead of silently failing at runtime.

## Enumerations

### CardType

| Value | Description |
|---|---|
| `ATTACK` | Offensive card. |
| `SKILL` | Utility or defensive card. |
| `POWER` | Persistent effect for the rest of combat. |
| `STATUS` | Non-standard card added to the deck by game events. |
| `CURSE` | Negative card added to the deck by game events. |

> [!NOTE]
> `STATUS` and `CURSE` are defined, but no cards of either type currently exist in `cards.json`.

### EffectType

Each effect type declares whether it uses `duration` through `EffectType.usesDuration()`. Validation reads this flag instead of maintaining a separate hard-coded list.

| Value | `usesDuration()` | Meaning of `value` | `duration` rule |
|---|:---:|---|---|
| `DAMAGE` | `false` | Damage dealt | Must be `0` |
| `BLOCK` | `false` | Block granted | Must be `0` |
| `HEAL` | `false` | Health restored | Must be `0` |
| `POISON` | `true` | Stacks applied | Must be `> 0` |
| `VULNERABLE` | `true` | Stacks applied | Must be `> 0` |
| `STRENGTH` | `false` | Stacks added | Must be `0` |

`STRENGTH` uses `duration = 0` because it lasts for the whole combat. In this case, zero means “not measured in turns,” not “expires immediately.”

### TargetType

| Value | Description |
|---|---|
| `SELF` | The player. |
| `SINGLE_ENEMY` | One selected enemy. |
| `ALL_ENEMIES` | Every enemy. |

### Rarity

The available rarity values are `COMMON`, `UNCOMMON`, and `RARE`. Rarity is used when cards are offered as rewards.

## Validation contract

`CardValidator.validate(CardConfig)` returns a `List<String>` containing every validation error. `CardValidator.isValid(CardConfig)` returns a boolean.

Validation errors are aggregated, so callers receive all detected problems rather than only the first one.

A card is rejected when any of the following conditions is true:

- `id` is `null`, blank, or contains leading or trailing whitespace;
- `name` is `null` or blank;
- `type`, `rarity`, or `target` is `null`;
- `texturePath` is `null` or blank;
- `effects` is `null` or empty;
- an effect entry is `null`, or its `type` is `null`;
- `cost` is negative;
- an effect's `value` is `<= 0`;
- an effect for which `usesDuration()` is `true` has `duration <= 0`;
- an effect for which `usesDuration()` is `false` has `duration != 0`.

`CardLibrary.register()` applies the complete validation contract. Partially populated configurations—for example, a test stub without `texturePath`—are rejected during registration as well as during file loading.

## JSON format

The configuration file is a wrapper object containing one `cards` array, matching the `NPCConfigs` pattern used elsewhere. It is not a bare JSON array.

```json
{
  "cards": [
    {
      "id": "poison_dagger",
      "name": "Poison Dagger",
      "description": "Deal 4 damage. Apply 3 Poison for 3 turns.",
      "cost": 1,
      "type": "ATTACK",
      "rarity": "UNCOMMON",
      "target": "SINGLE_ENEMY",
      "effects": [
        { "type": "DAMAGE", "value": 4, "duration": 0 },
        { "type": "POISON", "value": 3, "duration": 3 }
      ],
      "texturePath": "images/cards/poison_dagger.png"
    }
  ]
}
```

Effects are applied in array order. Enumeration values must use their exact uppercase constant names; the loader rejects unrecognised values.

## Adding a new card

1. Add a card object to the `cards` array in `source/core/assets/configs/cards.json`.
2. Assign a unique `id` without surrounding whitespace.
3. Include every field, including `description`.
4. Set `duration` according to the selected effect type.
5. Add the artwork at the path specified by `texturePath`.
6. Run the core test suite:

   ```bash
   ./gradlew core:test
   ```

The loader and end-to-end tests validate the real `cards.json`, so malformed card data fails the build. Card-count assertions have been removed; adding cards does not require updating a fixed expected count.

## Known limitations

| Limitation | Details |
|---|---|
| No `STATUS` or `CURSE` cards | The enum values and data model exist, but neither type has been exercised using real entries in `cards.json`. |
| Artwork is not checked on disk | Validation checks only that `texturePath` is non-blank. A card can pass validation while referencing a missing file. |
| Inconsistent `description` rules | `CardConfigLoader` requires the field, while `CardValidator` does not. |
| One target per card | Individual effects cannot target different sides. |
| No energy or deck-management effects | Types such as `GAIN_ENERGY`, `DRAW`, and `DISCARD` are not currently defined in `EffectType`. |

## Test coverage

| Test class | Tests | Coverage |
|---|---:|---|
| `CardConfigTest` | 4 | Field defaults, array declaration, and effect ordering. |
| `EffectConfigTest` | 5 | Field defaults, both constructors, and the two-argument duration behaviour. |
| `CardValidatorTest` | 17 | Every validation rule, including error aggregation. |

Loading and registry behaviour is covered separately by `CardConfigLoaderTest`, `CardLibraryTest`, and `CardsEndToEndTest`.
