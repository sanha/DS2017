#include <stdio.h>
#include <time.h>

clock_t t1, t2;
long target;
int static ITERATION = 1000000000;

int main() {
	target = 0L;
	t1 = clock();   
	int i;

	for(i = 0; i < ITERATION; i++) {   
		target++;
	}   

  t2 = clock();   

	float diff = (float)(t2 - t1) / CLOCKS_PER_SEC;   
	printf("Time: %f, Value: %ld", diff, target);   

	return 0;  
}	
