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
# slave_data_collector PROCESS_NAME TIME_BETWEEN_CHECKS OUTPUT_BASE_FILENAME
#
# Parameters:
# PROCESS_NAME : the name of the process to be monitored  
# TIME_BETWEEN_CHECKS : time between data collects.
# OUTPUT_BASE_FILENAME : this radical is used to construct the output file names. 
#The program creates two files, one for CPU information and other for memory information. 
#If OUTPUT_BASE_FILENAME is "aaaa", the created files are aaaa.cpu and aaaa.mem
#  

PROCESS_NAME=$1
TIME_BETWEEN_CHECKS=$2
OUTPUT_BASE_FILENAME=$3
OUTPUT_CPU_FILENAME="$OUTPUT_BASE_FILENAME.cpu"
OUTPUT_MEMORY_FILENAME="$OUTPUT_BASE_FILENAME.mem"

function get_process_pid
{
	echo "`ps axco pid,command | grep $PROCESS_NAME | grep -v grep | cut -d " " -f -1`" 
}

function process_is_running
{
	# I think this is not enough.
	# the process name is not enough to identify the Hadoop task.
	# so I should consider the user and the group.
	if [ ! "`ps axco command | grep $PROCESS_NAME | grep -v grep`" = "" ] ; then
		echo "1"
	else
		echo "0"
	fi
}

function get_cpu_consumption
{
	# TODO TO BE IMPLEMENTED
	echo "1";
}

function get_memory_consumption
{
	# TODO TO BE IMPLEMENTED
	echo "1";
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
	echo "process=$PROCESS_NAME" >> "$1"
	echo "start time=`date "+%d-%m-%Y-%H-%M-%S"`" >> "$1"
	echo "time between checks=$TIME_BETWEEN_CHECKS" >> "$1"
}

function write_file_ending
{
	echo "stop time=`date "+%d-%m-%Y-%H-%M-%S"`" >> "$1"
}

touch $OUTPUT_CPU_FILENAME
touch $OUTPUT_MEMORY_FILENAME

write_file_header $OUTPUT_CPU_FILENAME
write_file_header $OUTPUT_MEMORY_FILENAME

while [ $(process_is_running) -eq 1 ]; do

	CPU_CONSUMPTION=$(get_cpu_consumption)
	MEMORY_CONSUMPTION=$(get_memory_consumption)

	write_cpu_consumption $CPU_CONSUMPTION
	write_memory_consumption $MEMORY_CONSUMPTION
done

write_file_ending $OUTPUT_CPU_FILENAME
write_file_ending $OUTPUT_MEMORY_FILENAME

