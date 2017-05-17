package com.zzpj;

import com.zzpj.controller.ApplicationUnitTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ApplicationSecurityTest.class, ApplicationUnitTest.class, PerformanceTest.class })
public class AllTests {

}