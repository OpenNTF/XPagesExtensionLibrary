
Maire Kehoe 2010-07-25

To make this project compile, you need to ensure the plugin
    com.ibm.xsp.test.framework
is in your workspace. That plugin is in source control at:
/extlib/lwp/incubator/extensibility_junit/framework/eclipse/plugins/com.ibm.xsp.test.framework 

To run the JUnit tests:
- First you need to generate the compiled .java files from the .xsp files.
  - Right-click on:
  com.ibm.xsp.extlib.test/src/xsp/extlib/test/page/translator/ExtlibGeneratePagesTest.java
   Run As, JUnit Test.
  - It should fail a message like:
        ExtlibGeneratePagesTest
        xsp.extlib.test.page.translator.ExtlibGeneratePagesTest
        testPagesNotChanged(xsp.extlib.test.page.translator.ExtlibGeneratePagesTest)
        junit.framework.AssertionFailedError: 5 fail(s). :
        [0] * /pages/testChangeDynamicContentAction.xsp File generated, refresh the project.
        [1] * /pages/testDataViewVarPublishing.xsp File generated, refresh the project.
        [2] * /pages/relational/testSerializeSqlParameter_bad1.xsp File generated, refresh the project.
        [3] * /pages/relational/testSerializeSqlParameter_bad2.xsp File generated, refresh the project.
        [4] * /pages/relational/testSerializeSqlParameter_good.xsp File generated, refresh the project.
  - In your workspace, Refresh the plugin com.ibm.xsp.extlib.test
    and note that some .java files appear in the gen/ source folder.
    There is one .java file for each .xsp file in the pages/ folder. 
- Next you should run the test suite (it will fail)
  - Right-click on:
  com.ibm.xsp.extlib.test/src/xsp/extlib/test/ExtlibTestSuite.java
   Run As, JUnit Test.
  - The first 6 tests should pass, but the overall suite will fail with many failures
    because we have not yet fixed all of the problems in the extlib library.
    (as of 2011-07-25 there are 21 failures of 39 tests.)
    If you're still having problems with the first 6 tests, see the other readme at:
      com.ibm.xsp.test.framework/README.txt
- Finally, you should compare the fails you're seeing 
  to the fails encountered by other developers, to ensure you have not introduced any new problems.
    - In the JUnit view, where you see the fails, right-click on a failing test, Copy Failure List.
    - Open the file: com.ibm.xsp.extlib.test/results/junit-results.txt
    - Delete the existing file contents, and paste in your failure list. Add a date-stamp to the top of the file.
    - Right-click on the file and do Compare With, Previous in Repository.
  - If you see differences in every line of the stack traces, 
      you're seeing a tabs-vs-spaces problem - to fix, in the main eclipse menus, 
      Window, Preferences, General, Text Editors,
      check the first check box: "Insert tabs for spaces".
  - If you're seeing differences in some lines of the stack traces, 
      where the line numbers are wrong at the end of the JVM classes.
      You're using a different JRE. In menu Window, Preferences, Java, Installed JREs,
      you should have a check box next to a JRE with path like:
        C:\Program Files\IBM\Java50\
      To install the IBM Java 5 JRE, see Dan O'Connor's doc,
      in the Domino Designer Discussion DB
       "v9.0 Dev Environment Setup",
      which has a link to a share where you can download the JRE.

- If you're working in an area, and you want to fix existing problems, search for your .xsp-config file name in the failure list.

