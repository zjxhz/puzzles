package planning.busdriver.html;

import planning.busdriver.Driver;
import planning.busdriver.Line;
import planning.busdriver.Shift;
import planning.busdriver.factory.DaysFactory;
import planning.busdriver.factory.DriversFactory;
import planning.busdriver.factory.LinesFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wayne on 7/17/16.
 */
public class JsonGenerator {
    PrintWriter writer;
    List<Line> lines;
    List<Integer> days;
    List<Driver> drivers;

    JsonGenerator() throws FileNotFoundException {
        writer = new PrintWriter(new File("html/data.json"));
        lines = LinesFactory.createLines();
        drivers = DriversFactory.create("interviews/quintiq/shifts.txt", lines);
        days = DaysFactory.createDays(14);
    }
    public void generate() {
        writer.println("[");
        for(Driver driver : drivers){
            writer.println("{");
            writer.printf("id: '%s', \n", driver.getId());
            writer.printf("offDays: %s, \n", driver.getOffDays());
            writer.printf("qualifiedLines: %s, \n", driver.getQualifiedLines());
            writer.printf("preferredOffs: %s, \n", driver.getPreferredOffDays());
            writer.printf("preferredShifts: %s, \n", driver.getPreferredShifts());
            writer.println("},");
        }
        writer.println("]");
        writer.close();
    }

    public static void main(String[] args) throws FileNotFoundException {
        new JsonGenerator().generate();
    }

//    private Object printShifts(Map<Integer, Shift> preferredShifts) {
//        List<String> shifts = new ArrayList<>();
//        for(Integer day : preferredShifts.keySet()){
//            String s = day + ""
//        }
//    }
}
