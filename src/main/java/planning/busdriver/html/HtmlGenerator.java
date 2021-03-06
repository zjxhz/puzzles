package planning.busdriver.html;

import planning.busdriver.Driver;
import planning.busdriver.Line;
import planning.busdriver.Plan;
import planning.busdriver.Shift;
import planning.busdriver.factory.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by wayne on 7/17/16.
 */
public class HtmlGenerator {
    PrintWriter writer;
    List<Line> lines;
    List<Integer> days;
    List<Driver> drivers;

    HtmlGenerator() throws FileNotFoundException {
        String targetFolder = "build/resources/main/planning/busdriver/html";
        new File(targetFolder).mkdirs();
        writer = new PrintWriter(new File(targetFolder, "puzzle.html"));
        ImageDriversFactory.ImageProperties properties = ImageDriversFactory.ImageProperties.createStandardProperties(
                "planning/busdriver/shifts3.png");
        Plan initPlan = PlanFactory.createPlanFromImage(properties);
        drivers = initPlan.getDrivers();
        lines = initPlan.getLines();
        days = DaysFactory.createDays(14);
    }

    public void generate() {
        generateHead();
        startTable();
        generateHeaders(days);
        generateLines(lines, days);
        generateDrivers(drivers, days);
        endTable();
        generateResult();
        writer.close();
    }

    private void generateResult() {
        writer.println("<p>");
        startTable();
        String[] headers = {"Score", "Shift Preferences", "Dayoff Preferences", "Long Rests",
                "Unassigned Shifts", "Early after late shifts", "Consecutive late shifts",
                "Deviation target late shit"
        };
        writer.println("<tr>");
        for (int i = 0; i < 8; i++) {
            writer.printf("<th>%s</th>", headers[i]);
        }
        writer.println("</tr>");
        writer.println("<tr>");
        for (int i = 0; i < 8; i++) {
            writer.printf("<td id='%s'></td>", "result" + i);
        }
        writer.println("</tr>");
        endTable();
    }

    private void generateDrivers(List<Driver> drivers, List<Integer> days) {
        for (int i = 0; i < drivers.size(); i++) {
            Driver driver = drivers.get(i);
            String qualifiedLines = toSimplifiedLines(driver.getQualifiedLines());
            writer.println("<tr>");
            writer.print("<td>");
            writer.print(driver.getId() + " " + qualifiedLines + " " + driver.getLateShiftCount());
            writer.print("</td>");
            for (int j = 0; j < days.size(); j++) {
                int day = days.get(j);
                writer.printf("<td id='%s_d%s_m' class='%s' data-driver='%s' data-day='%s' data-qualified='%s'></td>" +
                                "<td id='%s_d%s_l' class='%s' data-driver='%s' data-day='%s' data-qualified='%s'></td>",
                        driver.getId(), day, getStyleClass(driver, day, Shift.MORNING), driver.getId(), day, qualifiedLines,
                        driver.getId(), day, getStyleClass(driver, day, Shift.LATE), driver.getId(), day, qualifiedLines);
            }
            writer.println("</tr>");
        }
    }

    private String toSimplifiedLines(Set<Line> lines) {
        return lines.toString().replaceAll("Line ", "");
    }

    private String getStyleClass(Driver driver, int day, Shift shift) {
        if (driver.getOffDays().contains(day)) {
            return "off-day";
        }
        if (driver.getPreferredOffDays().contains(day)) {
            return "preferred-off-day";
        }
        if (driver.isPreferredShift(day, shift)) {
            return "preferred-shift";
        }
        return "";
    }

    private void generateHead() {
        writer.println("<head>");
        writer.println("<link rel='stylesheet' type='text/css' href='puzzle.css'/>");
        writer.println("<script src='puzzle.js'></script>");
        writer.println("</head>");
    }

    private void endTable() {
        writer.println("</tbody>");
        writer.println("</table>");

    }

    private void generateLines(List<Line> lines, List<Integer> days) {
        for (int i = 0; i < lines.size(); i++) {
            Line line = lines.get(i);
            writer.println("<tr>");
            writer.print("<td>");
            writer.print(line);
            writer.print("</td>");
            for (int j = 0; j < days.size(); j++) {
                int day = days.get(j);
                writer.printf("<td id='l%s_d%s_m' class='%s' data-line data-line-day='%s'></td><td id='l%s_d%s_l' class='%s' data-line data-line-day='%s'></td>",
                        i + 1, day, "line-" + (i + 1) + "-morning-shift", day,
                        i + 1, day, "line-" + (i + 1) + "-late-shift", day);
            }
            writer.println("</tr>");
        }

    }

    private void startTable() {
        writer.println("<table>");
        writer.println("<tbody>");
    }

    private void generateHeaders(List<Integer> days) {
        writer.println("<tr>");
        writer.print("<th width='140'>");
        writer.print("");//empty header
        writer.print("</th>");
        for (int day : days) {
            writer.print("<th colspan='2'>");
            writer.print("Day " + day);
            writer.print("</th>");
        }
        writer.println("</tr>");
    }

    public static void main(String[] args) throws FileNotFoundException {
        HtmlGenerator generator = new HtmlGenerator();
        generator.generate();
    }

    private static List<Line> initLines() {
        List<Line> lines = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Line line = new Line(String.valueOf(i));
            lines.add(line);
        }
        return lines;
    }


}
