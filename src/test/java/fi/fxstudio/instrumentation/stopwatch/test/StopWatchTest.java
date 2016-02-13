package fi.fxstudio.instrumentation.stopwatch.test;

import fi.fxstudio.instrumentation.stopwatch.StopWatch;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by anttir on 1/31/16.
 */
public class StopWatchTest {

    @Test
    public void simpleTest(){

        StopWatch sw = new StopWatch();
        sw.setName("Simple");

        sw.startInterval("5s");
        sleep(5000);

        sw.stopInterval();

        StopWatch.Result result = sw.getResult();

        String resultString = sw.getResultAsString();
        System.out.println(resultString);
    }

    @Test
    public void simple2IntervalsTest(){

        StopWatch sw = new StopWatch("simple2IntervalsTest");

        sw.startInterval("2s");
        sleep(2000);

        sw.startInterval("2.5s");
        sleep(2500);

        sw.stopInterval();

        StopWatch.Result result = sw.getResult();

        String resultString = sw.getResultAsString();
        System.out.println(resultString);

        String htmlTable = sw.getResultAsHtmlTable();
    }




    @Test
    public void numberFormatterTest(){

        String number = String.format("%1$,.2f", 0.0005D);
    }

    @Test
    public void simpleSubIntervalTest(){
        StopWatch sw = new StopWatch("simpleSubIntervalTest");

        sw.startInterval("1st 2s");
        sleep(2000);

        StopWatch.Interval subInterval = sw.startInterval("2s + 1s + 3s");
        sleep(2000);

        // Here we call some sub method, perhaps a remote HTTP call, and store its duration
        StopWatch subStopWatch = createSimpleSubTask(1000); // This line acts as a submethod call
        subInterval.addSubStopWatch(subStopWatch);


        sleep(3000);

        sw.startInterval("2nd 2s");
        sleep(2000);

        sw.stopInterval();

        String resultString = sw.getResultAsString();
        System.out.println(resultString);

        String htmlTable = sw.getResultAsHtmlTable();
    }

    @Test
    public void simpleLoopSubIntervalTest(){
        StopWatch sw = new StopWatch("simpleLoopSubIntervalTest");

        sw.startInterval("1st 2s");
        sleep(2000);

        StopWatch.Interval subInterval = sw.startInterval("2s + 10*2s + 3s");
        sleep(2000);

        // Simulate a remote call in a loop
        for(int i=0; i<10; i++){
            // Small delay simulating overhead from remote call
            sleep(450);

            // Here we call some sub method, perhaps a remote HTTP call, and store its duration
            StopWatch subStopWatch = createSimpleSubTask(1000); // This line acts as a submethod call
            subInterval.addSubStopWatch(subStopWatch);


            // Small delay simulating overhead from remote call
            sleep(550);
        }


        sleep(3000);

        sw.startInterval("2nd 2s");
        sleep(2000);

        sw.stopInterval();

        String resultString = sw.getResultAsString();
        System.out.println(resultString);

        String htmlTable = sw.getResultAsHtmlTable();
    }

    @Test
    public void simpleSubIntervalTest2(){
        StopWatch sw = new StopWatch("Main routine");

        sw.startInterval("Step 1");
        sleep(1000);

        StopWatch.Interval subInterval = sw.startInterval("Step 2 ( 1.5s + 1s + 2s )");
        sleep(1500);

        // Here we call some sub method, perhaps a remote HTTP call, and store its duration
        StopWatch subStopWatch = createSimpleSubTask(1000); // This line acts as a submethod call
        subInterval.addSubStopWatch(subStopWatch);


        sleep(2000);

        sw.startInterval("Step 3");
        sleep(500);

        sw.stopInterval();

        String resultString = sw.getResultAsString();
        System.out.println(resultString);

        String htmlTable = sw.getResultAsHtmlTable();
    }

    @Test
    public void complexSubIntervalTest(){
        StopWatch sw = new StopWatch("complexSubIntervalTest");

        sw.startInterval("1st 2s");
        sleep(500);

        StopWatch.Interval subInterval = sw.startInterval("8.5s");
        sleep(750);

        // Here we call some sub method, perhaps a remote HTTP call, and store its duration
        StopWatch subStopWatch = createSimpleSubTask(2500); // This line acts as a submethod call
        subInterval.addSubStopWatch(subStopWatch);

        sleep(950);

        // Here we call some sub method, perhaps a remote HTTP call, and store its duration
        StopWatch subStopWatch2 = createComplexSubTask(2500); // This line acts as a submethod call
        subInterval.addSubStopWatch(subStopWatch2);


        sleep(350);

        // Here we call some sub method, perhaps a remote HTTP call, and store its duration
        StopWatch subStopWatch3 = createSimpleSubTask(450); // This line acts as a submethod call
        subInterval.addSubStopWatch(subStopWatch3);


        sleep(3000);

        sw.startInterval("2nd 2s");
        sleep(2000);

        sw.stopInterval();

        String resultString = sw.getResultAsString();
        System.out.println(resultString);
        String htmlTable = sw.getResultAsHtmlTable();

    }

    private StopWatch createSimpleSubTask(long delay){
        StopWatch sw = new StopWatch("createSimpleSubTask [delay:"+ delay + "]");

        sw.startInterval("sub interval");

        sleep(delay);

        sw.stopInterval();

        return sw;
    }

    private StopWatch createComplexSubTask(long delay){
        StopWatch sw = new StopWatch("createComplexSubTask [delay:"+ delay + "]");

        StopWatch.Interval interval = sw.startInterval("sub interval");

        sleep(delay);

        // Here we call some sub method, perhaps a remote HTTP call, and store its duration
        StopWatch subStopWatch = createSimpleSubTask(2500); // This line acts as a submethod call
        interval.addSubStopWatch(subStopWatch);

        sleep(950);

        // Here we call some sub method, perhaps a remote HTTP call, and store its duration
        StopWatch subStopWatch2 = createSimpleSubTask(500); // This line acts as a submethod call
        interval.addSubStopWatch(subStopWatch2);
        //
        StopWatch subStopWatch4 = createSimpleSubTask(850);
        subStopWatch2.getIntervals().get(0).addSubStopWatch(subStopWatch4);


        sleep(350);

        // Here we call some sub method, perhaps a remote HTTP call, and store its duration
        StopWatch subStopWatch3 = createSimpleSubTask(450); // This line acts as a submethod call
        interval.addSubStopWatch(subStopWatch3);

        sw.stopInterval();

        return sw;
    }

    @Ignore
    @Test
    public void loadTest(){

        StopWatch sw = new StopWatch();

        sw.startInterval("load test");
        for(int i=0; i<1000000; i++){
            StopWatch subSw = new StopWatch();

            subSw.startInterval("Eka");
            subSw.stopInterval();
        }

        sw.stopInterval();
        System.out.println(((StopWatch.Interval) sw.getIntervals().get(0)).getIntervalSeconds());

        StopWatch.Result result = sw.getResult();
    }

    @Ignore
    @Test
    public void loadTest2(){

        long startTime = System.currentTimeMillis();

        long iterCount=0;
        do{
            for(int i=0; i<1000; i++) {
                StopWatch subSw = new StopWatch();

                subSw.startInterval("Eka");
                subSw.stopInterval();

                iterCount++;
            }

        } while(System.currentTimeMillis() - startTime < 3000);


        System.out.println(iterCount);
    }

    private void sleep(long milliSeconds){
        try {
            Thread.sleep(milliSeconds);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

}
