@echo off
set CLASSPATH=.;.\classes;.\lib\activemq-client-5.9.0.jar;.\lib\asm-4.2.jar;.\lib\cglib-3.1.jar;.\lib\commons-dbcp-1.4.jar;.\lib\commons-pool-1.5.4.jar;.\lib\fastjson-1.1.38.jar;.\lib\geronimo-j2ee-management_1.1_spec-1.0.1.jar;.\lib\geronimo-jms_1.1_spec-1.1.1.jar;.\lib\hawtbuf-1.9.jar;.\lib\jdom-1.1.3.jar;.\lib\jetsennet-lib-2.1.1-SNAPSHOT.jar;.\lib\joda-time-2.3.jar;.\lib\log4j-1.2.17.jar;.\lib\mysql-connector-java-5.1.29.jar;.\lib\servlet-api-2.5.jar;.\lib\slf4j-api-1.7.5.jar;.\lib\slf4j-log4j12-1.7.6.jar;.\lib\xmemcached-2.0.0.jar

echo %CLASSPATH%
set DEBUG=-XX:+UnlockDiagnosticVMOptions -XX:+PrintInterpreter
set PRINT_GC=-XX:+PrintGCDetails -Xloggc:gc.log -XX:+PrintGCDateStamps -XX:+PrintCommandLineFlags -XX:+PrintTenuringDistribution
set GC_TYPE=-XX:+UseParallelOldGC
set RMI=-Djava.rmi.server.hostname=192.168.8.145 -Dcom.sun.management.jmxremote.port=1090 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false
set OOM=-XX:+HeapDumpOnOutOfMemoryError
set SIZE=-Xmx512m -Xms512m -XX:PermSize=40m -XX:MaxPermSize=40m
set NETBEANS=-agentpath:F:\application\profiler-server-win\lib\deployed\jdk16\windows\profilerinterface.dll=F:\application\profiler-server-win\lib,5140
echo %PRINT_GC% %GC_TYPE% %RMI% jetsennet.jbmp.ui.CollMgrSCFrm
java %SIZE% %PRINT_GC% %GC_TYPE% %RMI% jetsennet.jsmp.nav.syn.ui.DataSynFrame