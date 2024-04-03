package ru.merkii.rduels.util;

import java.util.concurrent.TimeUnit;

public class TimeUtil {

    public static String getTimeInMaxUnit(long time) {
        int ch = 0;
        if (TimeUnit.MILLISECONDS.toDays(time) > 0L) {
            ch = (int)TimeUnit.MILLISECONDS.toDays(time);
            return translateByLastDigit(ch, "d");
        } else if (TimeUnit.MILLISECONDS.toHours(time) > 0L) {
            ch = (int)TimeUnit.MILLISECONDS.toHours(time);
            return translateByLastDigit(ch, "h");
        } else if (TimeUnit.MILLISECONDS.toMinutes(time) > 0L) {
            ch = (int)TimeUnit.MILLISECONDS.toMinutes(time);
            return translateByLastDigit(ch, "m");
        } else if (TimeUnit.MILLISECONDS.toSeconds(time) > 0L) {
            ch = (int)TimeUnit.MILLISECONDS.toSeconds(time);
            return translateByLastDigit(ch, "s");
        } else {
            ch = (int)time;
            return ch + "мcек.";
        }
    }

    public static long parseTime(String time, TimeUnit defUnit) {
        String unit;
        String number;
        if (time.matches("([0-9]+)([smhdSMHD]$)")) {
            unit = time.replaceAll("([0-9]+)([smhdSMHD]$)", "$2");
            number = time.replaceAll("([0-9]+)([smhdSMHD]$)", "$1");
        } else {
            unit = defUnit == TimeUnit.DAYS ? "d" : (defUnit == TimeUnit.MINUTES ? "m" : (defUnit == TimeUnit.HOURS ? "h" : (defUnit == TimeUnit.SECONDS ? "s" : "none")));
            number = time;
        }

        if (!isInt(number)) {
            return Long.MIN_VALUE;
        } else {
            int ch = Integer.parseInt(number);
            long result;
            switch(unit) {
                case "s":
                    result = TimeUnit.SECONDS.toMillis((long)ch);
                    break;
                case "m":
                    result = TimeUnit.MINUTES.toMillis((long)ch);
                    break;
                case "h":
                    result = TimeUnit.HOURS.toMillis((long)ch);
                    break;
                case "d":
                    result = TimeUnit.DAYS.toMillis((long)ch);
                    break;
                default:
                    result = defUnit.toMillis(isInt(time) ? (long)Integer.parseInt(time) : (long)ch);
            }

            return result;
        }
    }

    public static boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }

    public static String translateByLastDigit(int ch, String unit2) {
        String unit;
        switch(unit2) {
            case "s":
                if (ch % 10 == 1) {
                    unit = "секунда";
                } else if (ch % 10 <= 1 || ch % 10 >= 5 || ch >= 10 && ch <= 20) {
                    unit = "секунд";
                } else {
                    unit = "секунды";
                }
                break;
            case "m":
                if (ch % 10 == 1) {
                    unit = "минута";
                } else if (ch % 10 <= 1 || ch % 10 >= 5 || ch >= 10 && ch <= 20) {
                    unit = "минут";
                } else {
                    unit = "минуты";
                }
                break;
            case "h":
                if (ch % 10 == 1) {
                    unit = "час";
                } else if (ch % 10 <= 1 || ch % 10 >= 5 || ch >= 10 && ch <= 20) {
                    unit = "часов";
                } else {
                    unit = "часа";
                }
                break;
            case "d":
                if (ch % 10 == 1) {
                    unit = "день";
                } else if (ch % 10 <= 1 || ch % 10 >= 5 || ch >= 10 && ch <= 20) {
                    unit = "дней";
                } else {
                    unit = "дня";
                }
                break;
            default:
                unit = "мсек.";
        }

        return ch + " " + unit;
    }

}
