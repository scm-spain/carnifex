# Check long running oozie jobs

## Description
The script connects to an Oozie server instance and check the oozie jobs currently running, if one takes too 
long time to execute it is killed.
 
Assuming the job has been coordinated it will be run later.

As an oozie workflow developer you should take care of recovering your process from errors.

## How to use it
You can manually run the script
````
java -jar /home/hadoop/oozie-jar/CheckOozieJobs-*.jar --oozieUrl=http://localhost:11000/oozie
````
Or add it to the crontab so it can execute every X minutes and kill the check the running jobs.

## Parameters available
* oozieUrl: Mandatory, it contains the URL to connect to the Oozie server in order to check the jobs 
and send the kill command
* maxTime: Optional, you can set the maximum time a job can be running, it should be defined on milliseconds.
The default value is *1800000* (30 minutes)
* log: Optional, the location of the log file where all the output will be send, the user running the script
should have permissions over that file. The default value is */tmp/long_running_oozie_jobs.log* 
