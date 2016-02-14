# stopwatch
# Simple profiling; a stopwatch to measure time, how long a time does some stuff take.

<hr/>

## Getting the library

Simple commands to compile into local maven repository:

    git clone https://github.com/anttiraisala/stopwatch.git
    cd stopwatch
    mvn clean install -DskipTests=true

Maven depedency:

    <dependency>
        <groupId>fxstudio-instrumentation</groupId>
        <artifactId>stopwatch</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

## Code examples

<table>
<tr><td>
<b>Code</b>
</td></tr>

<tr><td>
<pre>
// Create new StopWatch
StopWatch sw = new StopWatch("simple2IntervalsTest");

// "push start"
sw.startInterval("1s");
sleep(1000);

// "push start"; start a new interval ( and stop the previous one )
sw.startInterval("2.5s");
sleep(2500);

// "push stop"
sw.stopInterval();

// Get HTML-formatted result
String htmlTable = sw.getResultAsHtmlTable();

// Get console-friendly result
String resultString = sw.getResultAsString();
</pre>
</td></tr>


<tr><td>
&nbsp;
</td></tr>

<tr><td>
<b>HTML result:</b>
</td></tr>

<tr><td>
<table class='stopwatch-result'>

<tr><th/><th/><th/><th/><th class='column-border'>duration [s]</th><th class='column-border'>part of total [%]</th><th class='column-border'>part of total [%]</th></tr>



<tr class='odd-row'><td colspan='4' class='title-text'>simple2IntervalsTest<td class='column-border align-number title-number'>3.501</td><td class='column-border align-number title-number'>100.000</td><td class='column-border title-number'>########################################</td></tr>

<tr class='even-row'><td colspan='4' class='row'>1s<td class='column-border align-number'>1.000</td><td class='column-border align-number'>28.563</td><td class='column-border'>###########</td></tr>

<tr class='odd-row'><td colspan='4' class='row'>2.5s<td class='column-border align-number'>2.501</td><td class='column-border align-number'>71.437</td><td class='column-border'>#############################</td></tr>

</table>
</td></tr>

<tr><td>
&nbsp;
</td></tr>


<tr><td>
<b>Console-friendly result:</b>
</td></tr>

<tr><td>
<pre>
[simple2IntervalsTest]	3.501s	100.000%
[1s                  ]	1.000s	28.563%
[2.5s                ]	2.501s	71.4
</pre>
</td></tr>



</table>








#### Links
###### git - the simple guide; just a simple guide for getting started with git. no deep shit ;)
http://rogerdudler.github.io/git-guide/
###### Markdown: Syntax
https://daringfireball.net/projects/markdown/syntax#header
