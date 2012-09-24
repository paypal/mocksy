# The shell script to stop the mock service

echo "Stopping the mockprocess with processid $(cat .pid.txt)"

kill -9 $(cat .pid.txt) &> /dev/null; 

rm .pid.txt &> /dev/null; 
rm nohup.out &> /dev/null; 
echo "Done !!"