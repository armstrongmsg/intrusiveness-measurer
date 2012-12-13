#!/bin/bash -e

#
# Federal University of Campina Grande
# Distributed Systems Laboratory
#
# Author: Armstrong Mardilson da Silva Goes
# Contact: armstrongmsg@lsd.ufcg.edu.br
#

#
# Slave Data Collector
# 
# This program collects data about CPU and memory usages by  
# the given process.
#
# usage: 
# slave_data_collector PROCESS_PID TIME_BETWEEN_CHECKS OUTPUT_BASE_FILENAME
#
# Parameters:
# PROCESS_PID : the PID of the process to be monitored  
# TIME_BETWEEN_CHECKS : time between data collects. time is given in seconds.
# OUTPUT_BASE_FILENAME : this radical is used to construct the output file names. 
# The program creates two files, one for CPU information and other for memory information. 
# If OUTPUT_BASE_FILENAME is "aaaa", the created files are aaaa.cpu and aaaa.mem
#  

# TODO Arguments checking
# TODO Error handling

PROCESS_PID=$1
TIME_BETWEEN_CHECKS=$2
OUTPUT_BASE_FILENAME=$3
OUTPUT_CPU_FILENAME="$OUTPUT_BASE_FILENAME.cpu"
OUTPUT_MEMORY_FILENAME="$OUTPUT_BASE_FILENAME.mem"

DEBUG=true
DEBUG_FILE_NAME="collector.log"

function debug_startup
{
	if [ $DEBUG ]; then
		touch $DEBUG_FILE_NAME	
	fi
}

function debug
{
	if [ $DEBUG ]; then
		echo -n "`date "+%d-%m-%Y-%H-%M-%S"`    " >> $DEBUG_FILE_NAME
		# TODO if the log file is too big, it must truncate to 0
		# or do something so the file does not grow without limit.
		echo $1	>> $DEBUG_FILE_NAME
	fi
}

function process_is_running
{
	if [ ! "`ps axco pid | grep $PROCESS_PID`" = "" ] ; then
		echo "1"
	else 
		echo "0"
	fi
}

function get_cpu_consumption
{
	echo "`ps -p $PROCESS_PID -o %cpu | sed 1d`"
}

function get_memory_consumption
{
	echo "`ps -p $PROCESS_PID -o %mem | sed 1d`"
}

function write_cpu_consumption
{
	echo "$1" >> $OUTPUT_CPU_FILENAME
}

function write_memory_consumption
{
	echo "$1" >> $OUTPUT_MEMORY_FILENAME
}

function write_file_header
{
	echo "process=$PROCESS_PID" >> "$1"
	echo "start time=`date "+%d-%m-%Y-%H-%M-%S"`" >> "$1"
	echo "time between checks=$TIME_BETWEEN_CHECKS" >> "$1"
}

function write_file_ending
{
	echo "stop time=`date "+%d-%m-%Y-%H-%M-%S"`" >> "$1"
}

debug_startup

touch $OUTPUT_CPU_FILENAME
touch $OUTPUT_MEMORY_FILENAME

debug "process to monitor : $PROCESS_PID"
debug "created output files"

write_file_header $OUTPUT_CPU_FILENAME
write_file_header $OUTPUT_MEMORY_FILENAME

debug "wrote header to output files"

while [ $(process_is_running) -eq 1 ]; do

	CPU_CONSUMPTION=$(get_cpu_consumption)
	MEMORY_CONSUMPTION=$(get_memory_consumption) 

	write_cpu_consumption $CPU_CONSUMPTION
	write_memory_consumption $MEMORY_CONSUMPTION

	debug "going sleep : $TIME_BETWEEN_CHECKS"

	sleep $TIME_BETWEEN_CHECKS
done

debug "process to monitor stopped"

write_file_ending $OUTPUT_CPU_FILENAME
write_file_ending $OUTPUT_MEMORY_FILENAME

debug "wrote ending of output files"
debug "-----------------------------"
debug "-----------------------------"
