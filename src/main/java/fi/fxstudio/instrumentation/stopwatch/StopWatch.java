package fi.fxstudio.instrumentation.stopwatch;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anttir on 1/31/16.
 */
public class StopWatch implements Serializable {

    private ArrayList<Interval> intervals = new ArrayList<Interval>(20);
    private Interval currentInterval;
    private String name = "";

    private IdentifierForMatchingStopWatches identifierForMatchingStopWatches = new IdentifierForMatchingStopWatches();

    public StopWatch() {
    }

    public StopWatch(String name) {
        this.name = name;
    }

    public Interval startInterval(String intervalName) {
        if (currentInterval != null) {
            stopInterval();
        }

        Interval i = new Interval(intervalName);

        currentInterval = i;
        intervals.add(currentInterval);

        return i;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void stopInterval() {
        currentInterval.stop();
        currentInterval = null;
    }

    public static double roundToSignificantFigures(double num, int n) {
        if (num == 0) {
            return 0;
        }

        final double d = Math.ceil(Math.log10(num < 0 ? -num : num));
        final int power = n - (int) d;

        final double magnitude = Math.pow(10, power);
        final long shifted = Math.round(num * magnitude);
        return shifted / magnitude;
    }

    public String getResultAsHtmlTable() {
        Result resultRecursive = getResult();

        DecimalFormat durationFormat = new DecimalFormat(".000");
        int tdsPerHierarchyLevel = 4;
        int tdsPerIndent = 1;

        StringBuffer sb = new StringBuffer();

        sb.append(generateOddEvenTableRowStyle());

        sb.append("<table class='stopwatch-result'>\n");

        // Headers
        sb.append("<tr>");
        for (int i = 0; i < resultRecursive.getDeepestHierarchyDepth() * tdsPerIndent + tdsPerHierarchyLevel; i++) {
            sb.append("<th/>");
        }
        sb.append("<th class='column-border'>duration [s]</th>");
        sb.append("<th class='column-border'>part of total [%]</th>");
        sb.append("<th class='column-border'>part of total [%]</th>");
        sb.append("</tr>\n\n");

        boolean oddRow = false;
        for (ResultRow rr : resultRecursive.getResultRows()) {
            oddRow = !oddRow;

            sb.append("<tr class='" + (oddRow ? "odd-row" : "even-row") + "'>");
            // Title, title
            if (rr.getHierarchyDepth() > 0) {
                sb.append("<td colspan='" + rr.getHierarchyDepth() * tdsPerIndent + "'/>");
            }
            //
            sb.append("<td colspan='" + tdsPerHierarchyLevel + "' class='" + (rr.isTitleRow() ? "title-text" : "row") + "'>");
            sb.append(rr.getName());

            //
            if (resultRecursive.getDeepestHierarchyDepth() - rr.getHierarchyDepth() > 0) {
                sb.append("<td colspan='" + (resultRecursive.getDeepestHierarchyDepth() - rr.getHierarchyDepth()) * tdsPerIndent + "'/>");
            }


            // Duration
            sb.append("<td class='column-border align-number" + (rr.isTitleRow() ? " title-number" : "") + "'>");
            sb.append(durationFormat.format(rr.getDuration()));
            sb.append("</td>");


            // % of 0-level
            sb.append("<td class='column-border align-number" + (rr.isTitleRow() ? " title-number" : "") + "'>");
            sb.append(durationFormat.format(roundToSignificantFigures(rr.getDurationVersusTopLevel() * 100.0D, 5)));
            sb.append("</td>");

            // %-bar
            sb.append("<td class='column-border" + (rr.isTitleRow() ? " title-number" : "") + "'>");
            for (int i = 0; i < (int) (rr.getDurationVersusTopLevel() * 40.0 + 0.5); i++) {
                sb.append('#');
            }
            sb.append("</td>");

            sb.append("</tr>\n");
        }

        sb.append("</table>\n");

        return sb.toString();
    }

    private String[] splitNumberAtDecimalPoint(String number) {

        String[] value = new String[2];
        value[0] = "0";
        value[1] = "0";

        String[] split = number.split("\\.");
        if (split.length == 2) {
            value[0] = split[0];
            value[1] = split[1];
        } else if (split.length == 1) {
            value[1] = split[0];
        }

        return value;
    }

    private String[] splitNumberAtDecimalPoint(Double number) {
        return splitNumberAtDecimalPoint(number.toString());
    }

    private String generateOddEvenTableRowStyle() {
        String value = "";
        value += "<style>\n";
        value += ".stopwatch-result tr.odd-row {\n";
        value += "\tbackground: #CCC;\n}\n";
        value += "table.stopwatch-result\n{\n";
        value += "\tborder-collapse: collapse;\n}\n";
        value += ".stopwatch-result td\n{\n";
        value += "\tborder: solid 1px;\n";
        value += "\tborder-left: none;\n";
        value += "\tborder-right: none;\n}\n";
        value += ".stopwatch-result .column-border\n{\n";
        value += "\tborder-left: 1px solid #000;\n";
        value += "\tborder-right: 1px solid #000;\n";
        value += "}\n";
        value += ".stopwatch-result th.column-border\n{\n\tpadding: 10px;\n}\n";
        value += ".stopwatch-result .title-text\n{\n\tfont-weight: bold;\n\tpadding-left: 5px;\n\tpadding-right: 5px;\n}\n";
        value += ".stopwatch-result .title-number\n{\n\tfont-weight: bold;\n}\n";
        value += ".stopwatch-result .row\n{\n\tpadding-left: 20px;\n}\n";
        value += ".stopwatch-result .align-number\n{\n\tpadding-right: 10px;\n\ttext-align: right;\n}\n";
        value += "</style>\n\n";

        return value;
    }

    public String getResultAsString() {
        Result resultRecursive = getResult();

        DecimalFormat durationFormat = new DecimalFormat(".000");

        StringBuffer sb = new StringBuffer();

        for (ResultRow rr : resultRecursive.getResultRows()) {
            // Title, title
            sb.append('[');
            sb.append(rr.getName());
            for (int i = 0; i < resultRecursive.getLongestTitleLength() - rr.getName().length(); i++) {
                sb.append(' ');
            }
            sb.append(']');

            // extra indents per hierarhcy
            for (int h = 0; h < rr.getHierarchyDepth(); h++) {
                sb.append('\t');
            }

            // Duration
            sb.append('\t');
            sb.append(durationFormat.format(rr.getDuration()));
            sb.append('s');

            // % of 0-level
            sb.append('\t');
            sb.append(durationFormat.format(roundToSignificantFigures(rr.getDurationVersusTopLevel() * 100.0D, 5)));
            sb.append('%');


            sb.append('\n');
        }

        return sb.toString();
    }

    public Result getResult() {
        Result resultRecursive = getResultRecursive(0);

        // Loop through resultRecursive for modification
        //
        for (ResultRow rr : resultRecursive.getResultRows()) {

            // Get longest title
            if (rr.getName().length() > resultRecursive.longestTitleLength) {
                resultRecursive.longestTitleLength = rr.getName().length();
            }

            // Calculate row's duration compared to top level's duration
            rr.setDurationVersusTopLevel(rr.getDuration() / resultRecursive.getDuration());

            // Find deepest hierarchy
            if (rr.getHierarchyDepth() > resultRecursive.getDeepestHierarchyDepth()) {
                resultRecursive.deepestHierarchyDepth = rr.getHierarchyDepth();
            }
        }

        return resultRecursive;
    }

    private Result getResultRecursive(int hierarchyDepth) {

        Result result = new Result();

        ResultRow rr = new ResultRow();
        rr.setName(this.getName());
        result.addResultRow(rr);
        rr.setTitleRow(true);
        rr.setHierarchyDepth(hierarchyDepth);

        // Duration on StopWatch is sum of all intervals' duration
        Double duration = 0.0;

        for (Interval interval : intervals) {
            Result intervalResult = interval.getResultRecursive(hierarchyDepth);
            duration += intervalResult.getDuration();

            result.getResultRows().addAll(intervalResult.getResultRows());
        }

        result.duration = duration;
        rr.setDuration(result.duration);

        return result;
    }

    public ArrayList<Interval> getIntervals() {
        return intervals;
    }

    public class IdentifierForMatchingIntervals {
        private String declaringClass;
        private int linenumber;

        public IdentifierForMatchingIntervals() {
            this.declaringClass = Thread.currentThread().getStackTrace()[4].getClassName();
            this.linenumber = Thread.currentThread().getStackTrace()[4].getLineNumber();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            IdentifierForMatchingIntervals that = (IdentifierForMatchingIntervals) o;

            if (linenumber != that.linenumber) return false;
            return declaringClass != null ? declaringClass.equals(that.declaringClass) : that.declaringClass == null;
        }
    }

    public class IdentifierForMatchingStopWatches {
        private String declaringClass;
        private int linenumber;

        public IdentifierForMatchingStopWatches() {
            this.declaringClass = Thread.currentThread().getStackTrace()[3].getClassName();
            this.linenumber = Thread.currentThread().getStackTrace()[3].getLineNumber();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            IdentifierForMatchingStopWatches that = (IdentifierForMatchingStopWatches) o;

            if (linenumber != that.linenumber) return false;
            return declaringClass != null ? declaringClass.equals(that.declaringClass) : that.declaringClass == null;
        }
    }

    public class Interval {
        private long startTime;
        private long endTime;
        private double duration;
        private String name;

        private IdentifierForMatchingIntervals identifierForMatchingIntervals = new IdentifierForMatchingIntervals();

        private ArrayList<StopWatch> subStopWatches = new ArrayList<StopWatch>(10);

        public Interval(String name) {
            this.name = name;
            this.startTime = System.currentTimeMillis();
        }

        public Result getResultRecursive(int hierarchyDepth) {

            Result result = new Result();

            ResultRow rr = new ResultRow();
            rr.setName(this.getName());
            result.addResultRow(rr);
            rr.setHierarchyDepth(hierarchyDepth);

            result.duration = duration;
            rr.setDuration(duration);

            Double subStopWatchesDuration = 0.0;
            if (!subStopWatches.isEmpty()) {
                for (StopWatch stopWatch : subStopWatches) {
                    Result resultRecursive = stopWatch.getResultRecursive(hierarchyDepth + 1);

                    subStopWatchesDuration += resultRecursive.getDuration();
                    result.getResultRows().addAll(resultRecursive.getResultRows());
                }
                Double fillerDuration = duration - subStopWatchesDuration;

                // If time difference ( between result row and subStopWatches ) is larger than some errormargin, then create new result row representing the difference. So we know how muct time was lost somewhere else.
                if (fillerDuration >= 0.0D) {
                    ResultRow fillerRow = new ResultRow("* filler");
                    result.addResultRow(fillerRow);
                    fillerRow.setHierarchyDepth(hierarchyDepth + 1);
                    fillerRow.setDuration(fillerDuration);
                    fillerRow.setTitleRow(true);
                }
            }

            return result;
        }

        public void addSubStopWatch(StopWatch subStopWatch) {
            subStopWatches.add(subStopWatch);
        }

        public double getIntervalSeconds() {
            return (endTime - startTime) / 1000.0D;
        }

        public void stop() {
            this.endTime = System.currentTimeMillis();
            this.duration = ((double) (endTime - startTime)) / 1000.0D;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    /**********************************************************/
    /**********************************************************/
    /** SubClass(es) for viewing the results - begins *********/
    /**********************************************************/
    /**********************************************************/
    /**********************************************************/
    public class Result {
        private List<ResultRow> resultRows = new ArrayList<ResultRow>(50);

        private Double duration = 0.0D;
        private int longestTitleLength;
        private int deepestHierarchyDepth;

        public List<ResultRow> addResultRow(ResultRow resultRow) {
            resultRows.add(resultRow);
            return resultRows;
        }

        public int getDeepestHierarchyDepth() {
            return deepestHierarchyDepth;
        }

        public Double getDuration() {
            return duration;
        }

        public List<ResultRow> getResultRows() {
            return resultRows;
        }

        public int getLongestTitleLength() {
            return longestTitleLength;
        }
    }

    public class ResultRow {
        private int hierarchyDepth;
        private Interval interval;
        private String name;
        private Double duration;
        private boolean titleRow;

        private Double durationVersusTopLevel;

        public ResultRow() {
        }

        public ResultRow(String name) {
            this.setName(name);
        }

        public Double getDurationVersusTopLevel() {
            return durationVersusTopLevel;
        }

        public void setDurationVersusTopLevel(Double durationVersusTopLevel) {
            this.durationVersusTopLevel = durationVersusTopLevel;
        }

        public boolean isTitleRow() {
            return titleRow;
        }

        public void setTitleRow(boolean titleRow) {
            this.titleRow = titleRow;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getDuration() {
            return duration;
        }

        public void setDuration(Double duration) {
            this.duration = duration;
        }

        public int getHierarchyDepth() {
            return hierarchyDepth;
        }

        public void setHierarchyDepth(int hierarchyDepth) {
            this.hierarchyDepth = hierarchyDepth;
        }

        public Interval getInterval() {
            return interval;
        }

        public void setInterval(Interval interval) {
            this.interval = interval;
        }
    }

    /**********************************************************/
    /** SubClass(es) for viewing the results - ends ***********/
    /**********************************************************/

}
