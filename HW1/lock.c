#include <stdio.h>
#include <time.h>
#include <pthread.h>
#include <stdlib.h>

clock_t t1, t2;
int thread_num;
int static ITERATION = 1000000000;
volatile int lock;

// Thread calculation function
void *calc(void* arg) {
	int i;
	long *target = (long *)arg;

	for (i = 0; i < ITERATION / thread_num; i++) {
		while(__sync_lock_test_and_set(&lock, 1)) ; // spin lock
		*target = *target + 1;
		__sync_lock_release(&lock);
	}

	pthread_exit(NULL);
}

int main(int argc, char** argv) {

	// Get thread num argument
  if (argc == 1) {
	  thread_num = 1;
	}	else {
	  thread_num = atoi(argv[1]);
	}

	pthread_t threads[thread_num];
	volatile long *target = (long *) malloc(sizeof(long));
	int i;
	*target = 0;
	lock = 0;

	t1 = clock();   

	for (i = 0; i < thread_num; i++) {
		pthread_create(&threads[i], NULL, calc, (void *)target);
	}

	for (i = 0; i < thread_num; i++) {
		pthread_join(threads[i], NULL);
	}

  t2 = clock();   

	float diff = (float)(t2 - t1) / CLOCKS_PER_SEC;   
	printf("Thread: %d, Time: %f, Value: %ld", thread_num, diff, *target);   
	free((void *) target);

	return 0;  
}	
