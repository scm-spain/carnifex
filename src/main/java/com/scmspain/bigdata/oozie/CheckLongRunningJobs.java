package com.scmspain.bigdata.oozie;

import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;
import org.apache.oozie.client.WorkflowJob;

import java.util.Date;
import java.util.List;
import java.util.logging.*;

public class CheckLongRunningJobs
{

    private static OozieClient oozieClient;
    private static Logger logger;

    public static void main(String[] args) throws Exception
    {
        Arguments arguments = new Arguments(args);

        try {
            oozieClient = new OozieClient(arguments.getArgOozieUrl());
            Long maxRunningTimeMs = arguments.getArgMaxRunningTime();

            logger = Logger.getLogger(CheckLongRunningJobs.class.getName());
            Handler handler = new FileHandler(arguments.getArgLogLocation(), true);
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);

            String nameRegex = arguments.getArgNameRegex();

            List<WorkflowJob> runningJobs = oozieClient.getJobsInfo("status=RUNNING");

            logger.log(Level.INFO, "Found " + runningJobs.size() + " jobs currently running");

            for (WorkflowJob workflowJob : runningJobs) {
                checkRunningJobsIfExceededMaxTime(oozieClient, workflowJob, maxRunningTimeMs, logger, nameRegex);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error trying to process the jobs " + e.getMessage());
        }

    }

    private static void checkRunningJobsIfExceededMaxTime(OozieClient oozieClient, WorkflowJob workflowJob, Long maxRunningTimeMs, Logger logger, String nameRegex) throws OozieClientException
    {
        Long runningTime = ((new Date()).getTime() - workflowJob.getStartTime().getTime());

        String wfName = workflowJob.getAppName();

        logger.log(Level.INFO, "Job id " + workflowJob.getId() + " named " + wfName + " started " + workflowJob.getStartTime()
                + " was modified " + workflowJob.getLastModifiedTime() + " and has been running for "
                + runningTime + "ms");

        Boolean nameFilterMatches = (nameRegex == null) ? true : wfName.matches(nameRegex);

        if (runningTime > maxRunningTimeMs && nameFilterMatches) {
            oozieClient.kill(workflowJob.getId());
            logger.log(Level.SEVERE, "Killed job " + workflowJob.getId() + " running for " + runningTime + " ms");
        }
    }
}
