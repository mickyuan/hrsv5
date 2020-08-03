#!/bin/bash
cd `dirname $0`
export LANG='zh_CN.UTF-8'

ROOT_DIR=./
YEAR=`date -d now +%Y`
MONTH=`date -d now +%Y%m`
DAY=`date -d now +%Y%m%d`

LOG_YEAR_DIR=$ROOT_DIR$YEAR/
LOG_DIR=$LOG_YEAR_DIR$MONTH/

#标准输出日志
LOG_OUT_FILE=$LOG_DIR$DAY"ControlOut".log
echo "标准输出日志"$LOG_OUT_FILE

#标准输出错误日志
LOG_ERR_FILE=$LOG_DIR$DAY"ControlErr".log
echo "标准输出错误日志"$LOG_ERR_FILE

if [ ! -d $LOG_YEAR_DIR ]; then
 mkdir -m 755 $LOG_YEAR_DIR  
fi

if [ ! -d $LOG_DIR ]; then
 mkdir -m 755 $LOG_DIR  
fi

project_path=$(cd `dirname $0`; pwd)
nohup java -Dorg.eclipse.jetty.server.Request.maxFormContentSize=99900000 -Dproject.name=hrds_Control -Dproject.dir=$project_path -jar hrds_Control-5.0.jar etl.date=$1 sys.code=$2 -CR=$3 -AS=$4 1>>$LOG_OUT_FILE 2>>$LOG_ERR_FILE
exit 0

