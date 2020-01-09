# Test for 10 02

## Installation:
1. Copy [MbTestStream.java](MbTestStream.java) into `src/pgdp/stream`
2. Add Junit 5.4 to your project, two options:
  a. Hover with your mouse cursor over `@TestFactory` in the File, and choose `add Junit 5.4` under `Quick Fixes`
  b. manually:
    1. IntelliJ: https://www.jetbrains.com/help/idea/configuring-testing-libraries.html
    2. Eclipse: https://www.eclipse.org/community/eclipse_newsletter/2017/october/article5.php
3. Right-click the Class, and select `Run` or `Run as Junit Test`

## Options:
### Global:
- `STATIC_SEED`: The seed to use for Random, set to `-1` to use `System.currentTimeMillis()` as seed
- `N_TESTS`: How often every Test is repeated, excluding edge cases. set to `0` or lower to specify in each test individually
### Each Test:
- `nTests`: How often this test is repeated, if it isn't overriden by `N_TESTS`
