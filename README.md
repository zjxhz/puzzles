This project contains puzzles for my personal interests. Currently it has only one puzzle: (bus driver planning)[http://careers.quintiq.com/puzzle.html#the-challenge].

This project uses Gradle. To play with it, follow the steps below:

1. install `gradle` and `git`
2. `git clone https://github.com/zjxhz/puzzles/` 
3. `cd puzzles`
4. to run the tests: `gradle test`. The reports are under build/reports
5. to run the application: `gradle run`. The result can be seen from the output, including the planning and scores. 
6. To play it online, visit: http://xuhuanze.me/puzzle/

It should be noted that the online version was created on a "quick-and-dirty" manner, so you need to be careful when playing it. To be more specific, following things have not been implemented:

1. the late shift count for each driver should be updated
2. the qualified drivers should be highlighted. Also, shifts should be prevented from being assigned to an unqualified driver.
3. shifts should not be assigned to a different day.
4. some javascript errors when clicking outside cells, e.g. on the border or margins
5. morning shifts should not be allowed to be placed on the slots of late shifts
6. I believe there are many others to be discovered.
