#!/bin/sh

cd `dirname $0`


ENGINE=hrds_Agent-5.0.jar
MAIN=hrds.main.CommandExecute
CLASSPATH=$CLASSPATH:$ENGINE:resources/:


PROJECTDIR=$(cd `dirname $0`; pwd)
echo $PROJECTDIR

function libjars(){
for file in ./lib/*
do
if [ -f $file ]
then
 #echo $file
 CLASSPATH="$CLASSPATH$file:"
fi
done
}

libjars
# export CLASSPATH
echo $CLASSPATH
java -Xms16m -Xmx48m -Dproject.dir="$PROJECTDIR" -cp $CLASSPATH $MAIN "$@"
