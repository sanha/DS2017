TARGET1=single
TARGET2=faa
TARGET3=lock
TARGET4=batch
OBJS1=single.o
OBJS2=faa.o
OBJS3=lock.o
OBJS4=batch.o

CC=gcc
CFLAGS=-g -O0 -Wall
LDFLAGS=-lpthread

all: $(TARGET1) $(TARGET2) $(TARGET3) $(TARGET4)

$(TARGET1):$(OBJS1)
		$(CC) $(OBJS1) -o $@

$(TARGET2):$(OBJS2)
		$(CC) $(OBJS2) -o $@ $(LDFLAGS)

$(TARGET3):$(OBJS3)
		$(CC) $(OBJS3) -o $@ $(LDFLAGS)

$(TARGET4):$(OBJS4)
		$(CC) $(OBJS4) -o $@ $(LDFLAGS)

clean:
		rm -rf $(TARGET1) $(TARGET2) $(TARGET3) $(TARGET4) $(OBJS1) $(OBJS2) $(OBJS3) $(OBJS4)

run1: $(TARGET1)
		./single

run2: $(TARGET2)
		./faa 4

run3: $(TARGET3)
		./lock 4

run4: $(TARGET4)
		./batch 4
