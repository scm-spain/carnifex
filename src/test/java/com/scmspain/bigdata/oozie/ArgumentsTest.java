package com.scmspain.bigdata.oozie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ArgumentsTest
{
    private static final String OOZIE_URL = "http://localhost/oozie";
    private static final String MAX_RUNNING_TIME_MS = "200";
    private static final String LOG_LOCATION = "/tmp/test.log";
    private static final String NAME_REGEX = "^.*?(test).*?$";

    Arguments arguments;

    @Before
    public void setUp() throws Exception
    {
        String[] args = new String[8];
        args[0] = "--oozie-url";
        args[1] = OOZIE_URL;
        args[2] = "--log-location";
        args[3] = LOG_LOCATION;
        args[4] = "--max-time";
        args[5] = MAX_RUNNING_TIME_MS;
        args[6] = "--name-regex";
        args[7] = NAME_REGEX;

        arguments = new Arguments(args);
    }

    @After
    public void tearDown() throws Exception
    {
        arguments = null;
    }

    @Test
    public void testGetArgOozieUrl() throws Exception
    {
        assertEquals(
                "Oozie URL should be the one provided",
                OOZIE_URL,
                arguments.getArgOozieUrl()
        );
    }

    @Test
    public void testGetArgLogLocation() throws Exception
    {
        assertEquals(
                "Log location should be the one provided",
                LOG_LOCATION,
                arguments.getArgLogLocation()
        );
    }

    @Test
    public void testGetArgMaxRunningTime() throws Exception
    {
        assertEquals(
                "Max running time should be the one provided",
                Long.valueOf(MAX_RUNNING_TIME_MS),
                arguments.getArgMaxRunningTime()
        );
    }

    @Test
    public void testGetArgNameRegex() throws Exception
    {
        assertEquals(
                "Name regex should be the one provided",
                NAME_REGEX,
                arguments.getArgNameRegex()
        );
    }

}