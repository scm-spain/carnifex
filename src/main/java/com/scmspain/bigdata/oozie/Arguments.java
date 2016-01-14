package com.scmspain.bigdata.oozie;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.HashMap;

class Arguments
{
    private static final String ARG_OOZIE_URL = "oozie-url";
    private static final String ARG_MAX_RUNNING_TIME = "max-time";
    private static final String ARG_LOG_LOCATION = "log-location";
    public static final String ARG_NAME_REGEX = "name-regex";

    // 45 minutes in milliseconds.
    private static final String DEFAULT_MAX_RUNNING_TIME_MS = "2700000";
    private static final String DEFAULT_LOG_LOCATION = "/tmp/long_running_oozie_jobs.log";

    private String[] args;
    private HashMap<String, String> arguments = new HashMap<>();

    public Arguments(String[] args) throws Exception
    {
        this.args = args;
        parseArguments();
    }

    private void parseArguments() throws Exception
    {
        Options opt = new Options();

        opt.addOption("u", ARG_OOZIE_URL, true, "URL to connect to Oozie server");
        opt.addOption("l", ARG_LOG_LOCATION, true, "The log will be saved here");
        opt.addOption("m", ARG_MAX_RUNNING_TIME, true, "Maximum time in milliseconds a job can be in execution");
        opt.addOption("n", ARG_NAME_REGEX, true, "Regex filter");

        try {
            CommandLine cl = new DefaultParser().parse(opt, this.args);

            if (!cl.hasOption("u")) {
                throw new ParseException("Argument Oozie URL is mandatory to run the script");
            }
            arguments.put(ARG_OOZIE_URL, cl.getOptionValue("u"));

            arguments.put(ARG_LOG_LOCATION, cl.hasOption("l") ? cl.getOptionValue("l") : DEFAULT_LOG_LOCATION);
            arguments.put(ARG_MAX_RUNNING_TIME, cl.hasOption("m") ? cl.getOptionValue("m") : DEFAULT_MAX_RUNNING_TIME_MS);
            arguments.put(ARG_NAME_REGEX, cl.hasOption("n") ? cl.getOptionValue("n") : "");
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String getArgOozieUrl()
    {
        return arguments.get(ARG_OOZIE_URL);
    }

    public String getArgLogLocation()
    {
        return arguments.get(ARG_LOG_LOCATION);
    }

    public Long getArgMaxRunningTime()
    {
        return Long.valueOf(arguments.get(ARG_MAX_RUNNING_TIME));
    }

    public String getArgNameRegex()
    {
        return arguments.get(ARG_NAME_REGEX);
    }

}
