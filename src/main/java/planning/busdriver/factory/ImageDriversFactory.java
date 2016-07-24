package planning.busdriver.factory;

import planning.busdriver.Driver;
import planning.busdriver.Line;
import planning.busdriver.Shift;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

/**
 * Created by wayne on 7/24/16.
 */
public class ImageDriversFactory {
    private static int START_X = 413;
    private static int START_Y = 250;
    private static int DAY_WIDTH = 66;
    private static int SHIFT_WIDTH = DAY_WIDTH / 2;
    private static int DAY_HEIGHT = 37;
    private static int LINE_START_X = 328;
    private static int LINE2_START_X = 352;
    private static int LINE_START_Y = 270;

    public static List<Driver> create(String resource, List<Line> lines) {
        List<Driver> drivers = new ArrayList<>();
        BufferedImage image = null;
        try {

            image = ImageIO.read(ClassLoader.getSystemResourceAsStream(resource));
            System.out.println("Qualified lines:");
            for (int y = 0; y < 11; y++) {
                RGB rgb = getRGB(image, LINE_START_X, LINE_START_Y + DAY_HEIGHT * y);
                RGB rgb2 = getRGB(image, LINE2_START_X, LINE_START_Y + DAY_HEIGHT * y);

                System.out.printf("%c: %s %d \n",
                        ('A' + y), rgb, getLineNumber(rgb));
                System.out.printf("%c: %s %d \n",
                        ('A' + y), rgb2, getLineNumber(rgb2));
            }


            for (int y = 0; y < 11; y++) {
                Set<Line> qualifiedLines = new HashSet<>();
                Set<Integer> offDays = new HashSet<>();
                Set<Integer> preferredOffDays = new HashSet<>();
                Map<Integer, Shift> preferredShifts = new HashMap<>();
                for (int x = 0; x < 14; x++) {
                    int actualX = START_X + DAY_WIDTH * x;
                    int actualY = START_Y + DAY_HEIGHT * y;
                    RGB rgb = getRGB(image, actualX, actualY);
                    int day = x + 1;
                    Shift shift = Shift.MORNING;
                    printColor(image, x, y, actualX, actualY);
                    if (isOffDay(rgb)) {
                        offDays.add(day);
                    } else if (isPreferredOffDay(rgb)) {
                        preferredOffDays.add(day);
                    } else {
                        if (isPreferredShift(rgb)) {
                            preferredShifts.put(day, shift);
                        }
                        shift = Shift.LATE;
                        actualX += SHIFT_WIDTH;
                        rgb = getRGB(image, actualX, actualY);
                        if (isPreferredShift(rgb)) {
                            preferredShifts.put(day, shift);
                        }
                        printColor(image, x, y, actualX, actualY);
                    }
                }
                Line line1 = getLine(lines, image, LINE_START_X, LINE_START_Y + DAY_HEIGHT * y);
                Line line2 = getLine(lines, image, LINE2_START_X, LINE_START_Y + DAY_HEIGHT * y);
                qualifiedLines.add(line1);
                if(line2 != null){
                    qualifiedLines.add(line2);
                }
                Driver driver = new Driver(String.valueOf((char)('A' + y)), qualifiedLines, offDays);
                driver.setPreferredOffDays(preferredOffDays);
                driver.setPreferredShifts(preferredShifts);
                drivers.add(driver);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to init drivers", e);
        } finally {
            //close resource?
        }
        for(Driver driver : drivers){
            System.out.println(driver);
        }
        return drivers;
    }

    private static Line getLine(List<Line> lines, BufferedImage image, int x, int y) {
        RGB rgb = getRGB(image, x, y);
`        int lineNumber = getLineNumber(rgb);
        return findLine(lines, lineNumber);
    }

    private static RGB getRGB(BufferedImage image, int x, int y) {
        int color = image.getRGB(x, y);
        int red = (color & 0x00ff0000) >> 16;
        int green = (color & 0x0000ff00) >> 8;
        int blue = color & 0x000000ff;
        return new RGB(red, green, blue);
    }

    private static boolean isPreferredShift(RGB rgb) {
        return rgb.sum() < 650; //255, 255, 102-114
    }

    private static boolean isPreferredOffDay(RGB rgb) {
        return rgb.sum() < 600; //165-190 * 3
    }

    private static boolean isOffDay(RGB rgb) {
        return rgb.sum() < 330; //42-102 * 3
    }

    private static void printColor(BufferedImage image, int x, int y, int actualX, int actualY) {
        int clr = image.getRGB(actualX, actualY);
        int red = (clr & 0x00ff0000) >> 16;
        int green = (clr & 0x0000ff00) >> 8;
        int blue = clr & 0x000000ff;
        System.out.printf("((Day %s, %c): %s, (%s, %s, %s ) -> (%s, %s) \n",
                x + 1, ('A' + y), colorToType(red, green, blue),
                red, green, blue, actualX, actualY + 80);
    }

    private static String colorToType(int r, int g, int b) {
        int sum = r + g + b;
        if (sum < 330) { //42-102 * 3
            return "Off";
        }
        if (sum < 600) { //165-190 * 3
            return "Preferred Off";
        }
        if (sum < 650) { //255, 255, 102-114
            return "Preferred Shift";
        }
        return "Normal"; //255,255,255
    }

    private static int getLineNumber(RGB rgb) {
        if (rgb.sum() < 400) {
            return 3;
        }
        if (rgb.sum() < 400) {
            return 2;
        }
        if (rgb.sum() < 600) {
            return 1;
        }
        return -1;
    }

    private static Line findLine(List<Line> lines, int lineNumber){
        Line temp = new Line(String.valueOf(lineNumber));
        for(Line line : lines){
            if(temp.equals(line)){
                return line;
            }
        }
        return null;
    }

    public static void main(String args[]) throws IOException {
        ImageDriversFactory.create("planning/busdriver/screenshot0.jpg", LinesFactory.createLines());
    }
}

class RGB {
    int red;
    int green;
    int blue;

    public RGB(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public int sum() {
        return red + green + blue;
    }

    @Override
    public String toString() {
        return "RGB{" +
                "red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                '}';
    }
}
