/*
   Federal University of Campina Grande
   Distributed Systems Laboratory
   
   Author: Armstrong Mardilson da Silva Goes
   Contact: armstrongmsg@lsd.ufcg.edu.br

   This code is used in a project on the measurement 
   of the intrusiveness of running MapReduce in opportunistic 
   environments.
*/

/*
   memory.c

   This program allocates a certain amount of memory using 
   malloc and keeps this memory allocated for the given 
   amount of time, and then call the free function to free 
   the allocated space.

   usage:

   memory amount timeout

   Parameters:
   
	amount: 
		Amount of memory to be allocated. Value given in bytes.
		This value must be non-negative.   	

	timeout: 
		Amount of time to wait until free the memory. Value given in seconds.
		This value must be non-negative.
*/

# include <stdio.h>
# include <stdlib.h>
# include <unistd.h>
# include <string.h>
# include <signal.h>

/*
  The pointer to the allocated memory in the session
*/
void *allocated;

void print_error_on_allocation(void);
void print_correct_usage(void);
void print_invalid_argument(void);

void on_allocate(int bytes);
void on_free(void);

void check_arguments(int argc, const char *const argv[]);

int get_memory_to_allocate(const char *const argv[]);
int get_timeout(const char *const argv[]);

void print_error_on_allocation(void)
{
	printf("An error occurred when allocating memory.\n");
}

void print_correct_usage(void)
{
	printf("correct usage:\n");
	printf("memory amount timeout\n");
}

void print_invalid_argument(void)
{
	printf("error\nInvalid argument\n");
}

void on_allocate(int bytes)
{
	allocated = malloc(bytes);
	if (!allocated)
	{
		print_error_on_allocation();
		// FIXME review this value
		exit(1);
	}
}

void on_free(void)
{
	free(allocated);
}

void check_arguments(int argc, const char *const argv[])
{
	if (argc != 3)
	{
		print_correct_usage();
		// FIXME review this value
		exit(1);
	}
}

int get_memory_to_allocate(const char *const argv[])
{
	int memory_to_allocate = atoi(argv[1]);
	if (memory_to_allocate < 0)
	{
		print_invalid_argument();
		// FIXME review this value
		exit(1);
	}	
	return memory_to_allocate;
}

int get_timeout(const char *const argv[])
{
	int timeout = atoi(argv[2]);
	if (timeout < 0)
	{
		print_invalid_argument();
		// FIXME review this value
		exit(1);
	}	
	return timeout;
}

/*
   When handling kill, call this function
*/
void kill_handler(int signum)
{
	printf("signal:%d\n", signum);

	on_free();
	exit(signum);
}

int main(int argc, const char *const argv[])
{	
	int memory_to_allocate = 0;
	int timeout = 0;
	check_arguments(argc, argv);

	memory_to_allocate = get_memory_to_allocate(argv);
	timeout = get_timeout(argv);

	signal(SIGTERM, kill_handler);

	on_allocate(memory_to_allocate);
	sleep(timeout);		
	on_free();

	return 0;
}
