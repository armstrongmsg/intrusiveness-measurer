#!/bin/bash -e

#
# Federal University of Campina Grande
# Distributed Systems Laboratory
#
# Author: Armstrong Mardilson da Silva Goes
# Contact: armstrongmsg@lsd.ufcg.edu.br
#

#
# Data Collector Master
# 
# This program starts a hadoop benchmark and the data collectors
# on the used slaves.
#
# usage:
# master BENCHMARK CONFIGURATION_FILE
#
# Parameters:
# BENCHMARK : the benchmark to be executed.
# CONFIGURATION_FILE : the master configuration file path.
# The structured as follows:
# 
# TIME_BETWEEN_CHECKS=VALUE
# COLLECTOR_DIRECTORY=VALUE
# SLAVE_NAME1
# SLAVE_NAME2
# ...
# SLAVE_NAMEN
#

BENCHMARK=$1
CONFIGURATION_FILE=$2
COLLECTOR_NAME="slave_data_collector.sh"
SLAVES=""
TIME_BETWEEN_CHECKS=0
COLLECTOR_DIRECTORY=""
PROCESS_NAME=""

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
		# TODO if the log file is too big, it must truncate to 0
		# or do something so the file does not grow without limit.
		echo $1	>> $DEBUG_FILE_NAME
	fi
}

# FIXME this function is longer than it should be.
function read_configuration
{
	CONFIGURATION_CONTENT=`cat $CONFIGURATION_FILE | grep -v "#"`
	debug "loaded configuration = $CONFIGURATION_CONTENT"
	CONTENT=($CONFIGURATION_CONTENT)

	TIME_BETWEEN_CHECKS="`echo ${CONTENT[0]} | cut -d = -f2-`"
	COLLECTOR_DIRECTORY="`echo ${CONTENT[1]} | cut -d = -f2-`"

	for i in ${!CONTENT[*]}; do
		if [ ! $i -lt 2 ]; then
			SLAVES="$SLAVES ${CONTENT[$i]}"
		fi
	done

	debug "time between checks = $TIME_BETWEEN_CHECKS"
	debug "collector directory = $COLLECTOR_DIRECTORY"
}

function start_benchmark
{
	BENCHMARK=$1

	debug "run $BENCHMARK"
	case $BENCHMARK in
		"test")
		# FIXME hard coded
			PROCESS_NAME="firefox"
			;;
		# TODO a case per benchmark
		*)
			echo "Invalid benchmark."
			exit
			;;
	esac
}

function start_collector_command
{
	debug "runned command bash $COLLECTOR_DIRECTORY/$COLLECTOR_NAME $PROCESS_NAME $TIME_BETWEEN_CHECKS $1"
	# FIXME this is not working
	# FIXME must find a way to call the script in the slave.
	bash $COLLECTOR_DIRECTORY/$COLLECTOR_NAME $PROCESS_NAME $TIME_BETWEEN_CHECKS $1
}

function start_collector
{
	SLAVE_NAME=$1

	debug "ssh to $SLAVE_NAME"
	ssh $SLAVE_NAME start_collector_command $SLAVE_NAME
}

read_configuration

start_benchmark $BENCHMARK

for slave in $SLAVES; do
	start_collector $slave	
done

