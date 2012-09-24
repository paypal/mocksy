# The shell script to start the mock service as a daemon process 
# add -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=1044  in the java command below
# to start the mocksy in debug mode

################################ Configurations ######################################

# give the rules file path here
RULES_FILE_PATH=src/test/rules/xml/rules.xml

# configure the port you want the mocksy to run
PORT=7890


############################## Start script - DO NOT EDIT ############################

nohup java -jar target/mocksy-server-0.8-full.jar  --admin --port $PORT --ruleset $RULES_FILE_PATH >>nohup.out 2>&1 &
sleep 2

echo 		
if grep "Unable to access jarfile" nohup.out &> /dev/null; 
then
        echo "mocksy jar not found .. please build the project before starting"
        rm nohup.out	
        
elif tail -12 nohup.out | grep "java.net.BindException: Address already in use" &> /dev/null;
then
		kill -9 $! &> /dev/null # this kills the process which failed to start completely due to the port clash
    	echo $PORT "already in use...pls edit start.sh to start on a different PORT"
    	
elif grep "java.lang.IllegalArgumentException" nohup.out &> /dev/null;
then
		kill -9 $! &> /dev/null # this kills the process which failed to start completely
		rm nohup.out	
    	echo $RULES_FILE_PATH "does not exist...please edit start.sh to set the correct RULES_FILE_PATH"
    	
else
    	MOCK_PID=$!
		echo "found ruleset at " $RULES_FILE_PATH
		echo "started the mocksy process with the processid" $MOCK_PID
		echo $MOCK_PID > .pid.txt
fi
echo
echo "check nohup.out for logs and errors"
echo

#######################################################################################