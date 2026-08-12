---
title: Math Expressions
sidebar_position: 6
---
## Math in eco plugins
Throughout the eco plugins and effects system, you can use math expressions almost anywhere. Most of the time you will use them within effects, such as in chances, cooldowns, levelling and progression, or in multipliers. However, you can also use them in descriptions, lore and messages using the math placeholders: `{{expression}}` and `{^{expression}}` to format the answer nicely.

You can also use placeholders within math. Some eco plugins come with their own numerical placeholders, such as `%level%` which can be used to create scaling effects based on a job, skill, pet or enchantment level. External placeholders are also supported, as long as they provide a numerical result.

Expressions are evaluated by eco's own math engine, built in-house rather than relying on a third-party library. Everything on this page is supported natively, with no external dependency.

## Progress bars

`{#{expression}}` turns a percentage (0-100) into a progress bar, e.g. `{#{%libreforge_item_progress_example%}}` renders a 10-character bar showing how far towards the next level an item is. This works with any percentage-shaped placeholder — item levels, job/pet/collection progress, and so on — not just the built-in ones. Values outside 0-100 are clamped.

## Operators

| Syntax  | Description                                                                       |
| ------- | ---------------------------------------------------------------------------------- |
| `()`    | Create a parenthetical expression to be evaluated first (`3 * (4 + 1)`)            |
| `+`     | Add two numbers (`1 + 1`)                                                          |
| `-`     | Subtract two numbers (`2 - 1`), or negate one (`-3`)                               |
| `*`     | Multiply two numbers (`5 * 2`)                                                     |
| `/`     | Divide two numbers (`3 / 4`)                                                       |
| `%`     | Modulus / remainder (`7 % 3` returns `1`)                                          |
| `^`     | Raise one number to the power of another (`3^2`)                                   |
| `E`     | Scientific notation (`3E2` returns `300`, equivalent to `3 * 10^2`)                |
| `!`     | Boolean not (`!1` returns `0`, `!0` returns `1`)                                   |

### Comparison & logic

These all return `1` for true and `0` for false, so they can be fed straight into another expression (e.g. `chance: '(%level% >= 10) * 0.5'`).

| Syntax        | Description                                       |
| ------------- | -------------------------------------------------- |
| `>`           | Greater than (`5 > 3`)                             |
| `<`           | Less than (`3 < 5`)                                |
| `>=`          | Greater than or equal to (`5 >= 5`)                |
| `<=`          | Less than or equal to (`5 <= 5`)                   |
| `=` or `==`   | Equal to (`5 = 5`)                                 |
| `!=`          | Not equal to (`5 != 4`)                            |
| `&` or `&&`   | Boolean and (`1 & 1` returns `1`)                  |
| \| or \|\|    | Boolean or (`0 \| 1` returns `1`)                  |

## Constants

| Syntax  | Description                        |
| ------- | ----------------------------------- |
| `pi`    | π (`3.14159...`)                    |
| `e`     | Euler's number (`2.71828...`)       |
| `true`  | `1`                                 |
| `false` | `0`                                 |

## Common single-argument functions

These are called by prefixing the argument, e.g. `sin(x)`.

| Syntax    | Description                                                             |
| --------- | ------------------------------------------------------------------------ |
| `round`   | Round a number to the nearest integer (`round(1.30)` returns `1`)        |
| `ceil`    | Round a number up to the nearest integer (`ceil(1.20)` returns `2`)      |
| `floor`   | Round a number down to the nearest integer (`floor(1.80)` returns `1`)   |
| `abs`     | Absolute value (`abs(-5)` returns `5`)                                   |
| `sign`    | Sign of a number: `-1`, `0`, or `1` (`sign(-8)` returns `-1`)            |
| `trunc`   | Truncate towards zero, discarding the decimal (`trunc(1.9)` returns `1`) |
| `sqrt`    | Square root (`sqrt(9)` returns `3`)                                      |
| `cbrt`    | Cube root (`cbrt(27)` returns `3`)                                       |
| `log`     | Natural logarithm (base e)                                               |
| `log10`   | Base-10 logarithm                                                        |
| `log2`    | Base-2 logarithm                                                         |
| `exp`     | e raised to the given power (`exp(1)` returns `e`)                       |
| `sin`     | Sine (radians)                                                           |
| `cos`     | Cosine (radians)                                                         |
| `tan`     | Tangent (radians)                                                        |
| `asin`    | Arcsine                                                                  |
| `acos`    | Arccosine                                                                |
| `atan`    | Arctangent                                                               |
| `sinh`    | Hyperbolic sine                                                          |
| `cosh`    | Hyperbolic cosine                                                        |
| `tanh`    | Hyperbolic tangent                                                       |
| `rand`    | Random number between `0` (inclusive) and the argument (exclusive) (`rand(10)`) |

## Common multi-argument functions

| Syntax                             | Description                                                                                     |
| ----------------------------------- | ------------------------------------------------------------------------------------------------- |
| `random(min, max)`                 | Selects a random number between two bounds, in either order (`random(1,10)`)                     |
| `min(...)`                         | Returns the lowest of any number of values (`min(10, 2 * 10)` returns `10`)                       |
| `max(...)`                         | Returns the highest of any number of values (`max(10, 2 * 10)` returns `20`)                      |
| `clamp(x, min, max)`               | Restricts `x` to the `[min, max]` range (`clamp(15, 0, 10)` returns `10`)                         |
| `lerp(a, b, t)`                    | Linearly interpolates between `a` and `b` by `t` (`lerp(0, 10, 0.5)` returns `5`)                 |
| `smoothstep(edge0, edge1, x)`      | Smoothly interpolates between `0` and `1` as `x` moves from `edge0` to `edge1`                    |
| `remap(value, inMin, inMax, outMin, outMax)` | Remaps `value` from the `[inMin, inMax]` range to the `[outMin, outMax]` range (`remap(5, 0, 10, 0, 100)` returns `50`) |
| `step(edge, x)`                    | Returns `0` if `x < edge`, otherwise `1`                                                          |
| `if(condition, a, b)`              | Returns `a` if `condition` is non-zero, otherwise `b` (`if(%level% >= 10, 1, 0)`)                 |
| `pow(x, y)`                        | Raise `x` to the power of `y`, equivalent to `x^y`                                                |
| `atan2(y, x)`                      | The angle, in radians, of the point `(x, y)`                                                      |
| `hypot(x, y)`                      | The length of the hypotenuse of a right triangle with legs `x` and `y`                            |

`min` and `max` require at least one argument; every other function above requires exactly the arguments shown. `if` evaluates all three arguments eagerly, so avoid using it to guard against otherwise-invalid math (e.g. division by zero) in the branch that isn't "taken".

## Examples

In EcoSkills, you might want to provide players with a mining speed multiplier, capping it at 3.0x: `multiplier: 'min(3, (%level% * 0.1))'`. This ensures that the player gains a 0.10x mining speed boost per level, up to a maximum of 3.0x.

In EcoQuests, you might want players to collect a random amount of Coal Ore to complete the task: `xp: 'random(32,128)'` would randomise the task requirements between 32 ores and 128 ores.

You could also smoothly ramp a value in over a level range, rather than a hard cutoff: `chance: 'smoothstep(5, 20, %level%) * 0.75'` ramps from `0` to `0.75` as the player goes from level 5 to level 20, and stays at `0.75` beyond that.
