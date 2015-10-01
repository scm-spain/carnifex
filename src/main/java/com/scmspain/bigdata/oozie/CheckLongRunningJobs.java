package com.scmspain.bigdata.oozie;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;
import org.apache.oozie.client.WorkflowJob;

import java.util.Date;
import java.util.List;
import java.util.logging.*;

public class CheckLongRunningJobs
{
    private static final String OOZIE_URL_PARAM = "oozieUrl";
    private static final String MAX_RUNNING_TIME_MS_PARAM = "maxTime";
    private static final String LOG_LOCATION_PARAM = "log";
    private static final String NAME_FILTER_PARAM = "";

    // 45 minutes in milliseconds.
    private static final Integer MAX_RUNNING_TIME_MS = 2700000;
    private static final String LOG_LOCATION = "/tmp/long_running_oozie_jobs.log";

    private static OozieClient oozieClient;
    private static Logger logger;

    public static void main(String[] args)
    {
        Options options = new Options();
        options.addOption("u", OOZIE_URL_PARAM, true, "URL to connect to Oozie server");
        options.addOption("t", MAX_RUNNING_TIME_MS_PARAM, true, "Maximum time in milliseconds a job can be in execution");
        options.addOption("l", LOG_LOCATION_PARAM, true, "The log will be saved here");
        options.addOption("f", NAME_FILTER_PARAM, true, "Name filter to kill only the workflows that match with it");

        try {
            oozieClient = new OozieClient(getOozieUrl(args, options));
            Long maxRunningTimeMs = getMaxRunningTimeMs(args, options);

            logger = Logger.getLogger(CheckLongRunningJobs.class.getName());
            Handler handler = new FileHandler(getLogLocation(args, options), true);
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);

            String nameFilter = getNameFilterParam(args, options);

            if (nameFilter != "") nameFilter = ";name=" + nameFilter;

            List<WorkflowJob> runningJobs = oozieClient.getJobsInfo("status=RUNNING" + nameFilter);

            logger.log(Level.INFO, "Found " + runningJobs.size() + " jobs currently running");

            for (WorkflowJob workflowJob : runningJobs) {
                checkRunningJobsIfExceededMaxTime(oozieClient, workflowJob, maxRunningTimeMs, logger);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error trying to process the jobs " + e.getMessage());
        }

    }

    private static String getOozieUrl(String[] args, Options options)
    {
        try {
            CommandLine line = new DefaultParser().parse(options, args);

            if (!line.hasOption(OOZIE_URL_PARAM)) {
                throw new RuntimeException(OOZIE_URL_PARAM + " param is required!");
            }

            return line.getOptionValue(OOZIE_URL_PARAM);
        } catch (ParseException exp) {
            throw new RuntimeException("Error parsing " + OOZIE_URL_PARAM + " param " + exp.getMessage());
        }
    }

    private static Long getMaxRunningTimeMs(String[] args, Options options)
    {
        try {
            CommandLine line = new DefaultParser().parse(options, args);

            return (!line.hasOption(MAX_RUNNING_TIME_MS_PARAM)) ?
                    MAX_RUNNING_TIME_MS : Long.parseLong(line.getOptionValue(MAX_RUNNING_TIME_MS_PARAM));
        } catch (ParseException exp) {
            throw new RuntimeException("Error parsing " + MAX_RUNNING_TIME_MS_PARAM + " param " + exp.getMessage());
        }
    }

    private static String getLogLocation(String[] args, Options options)
    {
        try {
            CommandLine line = new DefaultParser().parse(options, args);

            return (!line.hasOption(LOG_LOCATION_PARAM)) ?
                    LOG_LOCATION : line.getOptionValue(LOG_LOCATION_PARAM);
        } catch (ParseException exp) {
            throw new RuntimeException("Error parsing " + LOG_LOCATION_PARAM + " param " + exp.getMessage());
        }
    }

    private static String getNameFilterParam(String[] args, Options options)
    {
        try {
            CommandLine line = new DefaultParser().parse(options, args);

            return (!line.hasOption(NAME_FILTER_PARAM)) ?
                    NAME_FILTER_PARAM : line.getOptionValue(NAME_FILTER_PARAM);
        } catch (ParseException exp) {
            throw new RuntimeException("Error parsing " + NAME_FILTER_PARAM + " param " + exp.getMessage());
        }
    }

    private static void checkRunningJobsIfExceededMaxTime(OozieClient oozieClient, WorkflowJob workflowJob, Long maxRunningTimeMs, Logger logger) throws OozieClientException
    {
        Long runningTime = ((new Date()).getTime() - workflowJob.getStartTime().getTime());

        logger.log(Level.INFO, "Job id " + workflowJob.getId() + " started " + workflowJob.getStartTime()
                + " was modified " + workflowJob.getLastModifiedTime() + " and has been running for "
                + runningTime + "ms ");

        if (runningTime > maxRunningTimeMs) {
            oozieClient.kill(workflowJob.getId());
            logger.log(Level.SEVERE, "Killed job " + workflowJob.getId() + " running for " + runningTime + " ms");
        }
    }
}
