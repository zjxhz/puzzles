package planning.busdriver.factory;

import planning.busdriver.Driver;
import planning.busdriver.Line;
import planning.busdriver.Shift;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by wayne on 7/24/16.
 */
public class ImageDriversFactory {
    private static int DAY_WIDTH = 66;
    private static int SHIFT_WIDTH = DAY_WIDTH / 2;
    private static int DAY_HEIGHT = 37;


    private static RGB LINE1_COLOR = new RGB(164, 190, 60);
    private static RGB LINE2_COLOR = new RGB(58, 180, 74);
    private static RGB LINE3_COLOR = new RGB(15, 108, 58);

    private static RGB OFF_DAY_COLOR = new RGB(35, 35, 35);//DARK SIDE
    private static RGB PREFERRED_OFF_DAY_COLOR = new RGB(166, 166, 166);//DARK SIDE
    private static RGB PREFERRED_SHIFT_COLOR = new RGB(255, 255, 105);

    public static List<Driver> create(ImageProperties properties, List<Line> lines) {
        List<Driver> drivers = new ArrayList<>();
        BufferedImage image = null;
        try {
            image = ImageIO.read(ClassLoader.getSystemResourceAsStream(properties.resourceUri));
            System.out.println("Qualified lines:");
            for (int y = 0; y < 11; y++) {
                Set<Line> qualifiedLines = new HashSet<>();
                Set<Integer> offDays = new HashSet<>();
                Set<Integer> preferredOffDays = new HashSet<>();
                Map<Integer, Shift> preferredShifts = new HashMap<>();
                for (int x = 0; x < 14; x++) {
                    int actualX = properties.driverStartX + DAY_WIDTH * x;
                    int actualY = properties.driverStartY + DAY_HEIGHT * y;
                    RGB rgb = getRGB(image, actualX, actualY);
                    int day = x + 1;
                    Shift shift = Shift.MORNING;
//                    printColor(image, x, y, actualX, actualY);
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
//                        printColor(image, x, y, actualX, actualY);
                    }
                }
                Line line1 = getLine(lines, image, properties.line1X, properties.line1Y + DAY_HEIGHT * y);
                Line line2 = getLine(lines, image, properties.line2X, properties.line1Y + DAY_HEIGHT * y);
                qualifiedLines.add(line1);
                if (line2 != null) {
                    qualifiedLines.add(line2);
                }
                Driver driver = new Driver(String.valueOf((char) ('A' + y)), qualifiedLines, offDays);
                driver.setPreferredOffDays(preferredOffDays);
                driver.setPreferredShifts(preferredShifts);
                drivers.add(driver);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to init drivers", e);
        } finally {
            //close resource?
        }
        return drivers;
    }

    private static Line getLine(List<Line> lines, BufferedImage image, int x, int y) {
        RGB rgb = getRGB(image, x, y);
        int lineNumber = getLineNumber(rgb);
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
        return isSameColor(PREFERRED_SHIFT_COLOR, rgb);
    }

    private static boolean isPreferredOffDay(RGB rgb) {
        return isSameColor(PREFERRED_OFF_DAY_COLOR, rgb);
    }

    private static boolean isOffDay(RGB rgb) {
        return isSameColor(OFF_DAY_COLOR, rgb);
    }

    private static boolean isSameColor(RGB rgb1, RGB rgb2) {
        return rgb1.diff(rgb2) < 60;
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
        if (isSameColor(LINE1_COLOR, rgb)) {
            return 1;
        }
        if (isSameColor(LINE2_COLOR, rgb)) {
            return 2;
        }
        if (isSameColor(LINE3_COLOR, rgb)) {
            return 3;
        }
        return -1;
    }

    private static Line findLine(List<Line> lines, int lineNumber) {
        Line temp = new Line(String.valueOf(lineNumber));
        for (Line line : lines) {
            if (temp.equals(line)) {
                return line;
            }
        }
        return null;
    }

    public static class ImageProperties {
        private String resourceUri;
        private int driverStartX;
        private int driverStartY;
        private int line1X;
        private int line1Y;
        private int line2X;

        int optimal;

        public ImageProperties(String resourceUri, int driverStartX, int driverStartY,
                               int line1X, int qualifiedLine1Y, int line2X) {
            this.resourceUri = resourceUri;
            this.driverStartX = driverStartX;
            this.driverStartY = driverStartY;
            this.line1X = line1X;
            this.line1Y = qualifiedLine1Y;
            this.line2X = line2X;
        }
    }

    public static void main(String args[]) throws IOException {
        ImageProperties p1 = new ImageProperties("planning/busdriver/shiftsg.jpg", 413, 250, 330, 270, 352);
        p1.optimal = 159;
        ImageProperties p2 = new ImageProperties("planning/busdriver/shifts3.png", 408, 242, 331, 242, 352);
        p2.optimal = 161;
        List<Driver> drivers = ImageDriversFactory.create(p2, LinesFactory.createLines());
        for (Driver driver : drivers) {
            System.out.println(driver);
        }
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

    public int diff(RGB rgb) {
        return Math.abs(rgb.red - red) + Math.abs(rgb.green - green) + Math.abs(rgb.blue - blue);
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

