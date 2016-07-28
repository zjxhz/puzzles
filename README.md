## Summary

This project contains puzzles for my personal interests. Currently it has only one puzzle: [bus driver planning](http://careers.quintiq.com/puzzle.html#the-challenge).

This project uses Gradle. To play with it, follow the steps below:

1. install `gradle` and `git`
2. `git clone https://github.com/zjxhz/puzzles/` 
3. `cd puzzles`
4. to run the tests: `gradle test`. The reports are under build/reports
5. to run the application: `gradle run`. The main class is `planning.busdriver.Main`, as specified in `build.gradle`. This application reads data from a screenshot file `planning/busdriver/shifts3.png` as input, and tries to find a highest score by optimizing a group of randomly generated plans. Check the console output to see the result, it should be self-explanatory.
6. To play it online, visit: http://xuhuanze.me/puzzle/

## How it works

Since there are many differet ways to assign drivers to a single shift, brute forcing all posibilities is not practical, if possible at all. Also, simply making best moves for the current shift, without taking the overral planning into consideration, does not produce the optimal result.

Inspired by the genetic algorithm, this application generates randomly a group of possible plans, which avoid violating major rules such as assigning shifts on off days, as the first generation. The _crossover_ is done in a way by re-planning(optimizing) assignments on ramdom day ranges for a given candidate(plan). Since optimizing each candidate are disjoined events, this algorithm can generate a "good enough" solution that reachs the 85% goal usually in seconds. Also, it has been observed that it is possible to reach the optimal target(100%), usually in a few minutes.
