# Check long running Oozie jobs

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
* u (oozie-url): Mandatory, it contains the URL to connect to the Oozie server in order to check the jobs
and send the kill command
* m (max-time): Optional, you can set the maximum time a job can be running, it should be defined on milliseconds.
The default value is *2700000* (45 minutes)
* l (log-location): Optional, the location of the log file where all the output will be send, the user running the script
should have permissions over that file. The default value is */tmp/long_running_oozie_jobs.log*
* n (name-regex): Optional, you can filter which workflows to kill by a name filter by using a regular expression
(for example, to kill sqoop actions that exceeed maximum time, you can use "^.*?(sqoop)*?$"). The default value is null.

## Building it
You can build a jar file using two different gradle tasks:

* jar
* fatJar

````
./gradlew jar
./gradlew fatJar
````

The *jar* task generates a shell script that runs the the jar and add all its dependencies from a separate 
directory.

The *fatJar* task generates a jar with all the dependencies inside, and you should execute the script using
the java command.
