package planning.busdriver.factory;

import planning.busdriver.Driver;
import planning.busdriver.Line;
import planning.busdriver.Shift;

import java.io.File;
import java.util.*;

/**
 * Created by wayne on 7/17/16.
 */
public class DriversFactory {
//    private static Set<Integer> calcOffDays(String id) {
//        Set<Integer> days = new HashSet<>();
//        int [][] offdays = {
//                {6, 7, 13, 14},//shift0; 160, 63%; 250, 212
//                {2, 3, 9, 10},
//                {1, 3, 8,10},
//                {5,7, 12, 14},
//                {4, 6, 11, 13},
//                {2, 5, 9, 12},
//                {3,7,10, 14},
//                {1,2, 8,9},
//                {2, 4, 9, 11},
//                {5, 7, 12, 14},
//                {1, 6, 8, 13},
//                {4,5,11,12},//shifts1, 127, 78.88%; 161, 136.85
//                {1, 7,8, 14},
//                {1, 6,8,13},
//                {3,5,10,12},//D
//                {2,4,9,11},
//                {3, 7, 10,14},
//                {1,5,8,12},//G
//                {6,7,13,14},
//                {2,7,9,14},
//                {3,5,10,12},
//                {4, 6, 11, 13},//K
////                {},
////                {},
////                {},
////                {},
////                {},
////                {},
////                {},
////                {},
////                {},
////                {},
////                {},//K
//
//        };
//        int index = id.charAt(0) - 'A';
//        for(int i : offdays[index]){
//            days.add(i);
//        }
//        return days;
//    }

    private static Set<Integer> calcOffDays(String id) {
        Set<Integer> days = new HashSet<>();
        int base = id.charAt(0) - 'A' + 3;
        days.add(base);
        days.add(base + 1);
        int base2 = base + 7;
        days.add(base2 > 14 ? base2 - 14 : base2);
        days.add(base2 + 1 > 14 ? base2 + 1 - 14 : base2 + 1);
        return days;
    }

    public static List<Driver> create(String resourceName, List<Line> lines){
        List<Driver> drivers = new ArrayList<>();
        try {
            Scanner in = new Scanner(ClassLoader.getSystemResourceAsStream(resourceName));
            while (in.hasNext()) {
                String id = in.nextLine();
                Set<Integer> offDays = calcOffDays(id);
                Set<Line> qualifiedLines = scanLines(lines, in.nextLine());
                Set<Integer> preferredOffDays = scanPrefferedOffDays(in.nextLine());
                Map<Integer, Shift> preferredShifts = scanPreferredShifts(in.nextLine());
                Driver driver = new Driver(id, qualifiedLines, offDays);
                driver.setPreferredOffDays(preferredOffDays);
                driver.setPreferredShifts(preferredShifts);
                drivers.add(driver);
            }
            return drivers;
        } catch (Exception ex) {
            throw new RuntimeException("Error occured when initializing drivers");
        }
    }

    private static Set<Line> scanLines(List<Line> lines, String line) {
        Set<Line> qualifiedLines = new HashSet<>();
        for (String number : line.split(" ")) {
            Line temp = new Line(number);
            qualifiedLines.add(findLine(lines, temp));//we only want to have the same instance
        }
        return qualifiedLines;
    }

    private static Line findLine(List<Line> lines, Line line) {
        for (Line l : lines) {
            if (l.equals(line)) {
                return l;
            }
        }
        return null;
    }


    private static Set<Integer> scanPrefferedOffDays(String line) {
        Set<Integer> days = new HashSet<>();
        for (String number : line.split(" ")) {
            days.add(Integer.valueOf(number));
        }
        return days;
    }

    private static Map<Integer, Shift> scanPreferredShifts(String line) {
        Map<Integer, Shift> preferredShifts = new HashMap<>();

        for (String s : line.split(" ")) {
            Shift shift = s.charAt(s.length() - 1) == 'M' ? Shift.MORNING : Shift.LATE;
            int day = Integer.valueOf(s.substring(0, s.length() - 1));
            preferredShifts.put(day, shift);
        }
        return preferredShifts;
    }

}
