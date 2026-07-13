---
title: Math Expressions
sidebar_position: 6
---
## Math in eco plugins
Throughout the eco plugins and effects system, you can use math expressions almost anywhere. Most of the time you will use them within effects, such as in chances, cooldowns, levelling and progression, or in multipliers. However, you can also use them in descriptions, lore and messages using the math placeholders: `{{expression}}` and `{^{expression}}` to format the answer nicely.

You can also use placeholders within math. Some eco plugins come with their own numerical placeholders, such as `%level%` which can be used to create scaling effects based on a job, skill, pet or enchantment level. External placeholders are also supported, as long as they provide a numerical result.
## Common Syntax
These are the most commonly used mathematical expressions within eco plugins. To see all math syntaxes available, visit [here](https://github.com/boxbeam/Crunch?tab=readme-ov-file#operations-and-syntax).

| Syntax            | Description                                                             |
| ----------------- | ----------------------------------------------------------------------- |
| `()`              | Create a parenthetical expression to be evaluated first (`3 * (4 + 1)`) |
| `+`               | Add two numbers (`1 + 1`)                                               |
| `-`               | Subtract two numbers (`2 - 1`), or negate one (`-3`)                    |
| `/`               | Divide two numbers (`3 / 4`)                                            |
| `*`               | Multiply two numbers (`5 * 2`)                                          |
| `^`               | Raise one number to the power of another (`3^2`)                        |
| `round`           | Round a number to the nearest integer (`round(1.30)` returns 1)         |
| `ceil`            | Round a number up to the nearest integer (`ceil(1.20)` returns `2`)     |
| `floor`           | Round a number down to the nearest integer (`floor(1.80)` returns `1`)  |
| `random(min,max)` | Selects a random number between two bounds (`random(1,10)`)             |
| `min`             | Returns the lowest value (`min(10,2 * 10)` returns `10`)                |
| `max`             | Returns the highest value (`min(10,2 * 10)` returns `20`)               |
## Examples

In EcoSkills, you might want to provide players with a mining speed multiplier, capping it at 3.0x: `multiplier: 'min(3, (%level% * 0.1))'`. This ensures that the player gains a 0.10x mining speed boost per level, up to a maximum of 3.0x.

In EcoQuests, you might want players to collect a random amount of Coal Ore to complete the task: `xp: 'random(32,128)'` would randomise the task requirements between 32 ores and 128 ores.