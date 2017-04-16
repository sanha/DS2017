TARGET1=cas
TARGET2=faa
TARGET3=tas
TARGET4=ttas
OBJS1=cas.o
OBJS2=faa.o
OBJS3=tas.o
OBJS4=ttas.o

CC=gcc
CFLAGS=-g -O0 -Wall
LDFLAGS=-lpthread

all: $(TARGET1) $(TARGET2) $(TARGET3) $(TARGET4)

$(TARGET1):$(OBJS1)
		$(CC) $(OBJS1) -o $@ $(LDFLAGS)

$(TARGET2):$(OBJS2)
		$(CC) $(OBJS2) -o $@ $(LDFLAGS)

$(TARGET3):$(OBJS3)
		$(CC) $(OBJS3) -o $@ $(LDFLAGS)

$(TARGET4):$(OBJS4)
		$(CC) $(OBJS4) -o $@ $(LDFLAGS)

clean:
		rm -rf $(TARGET1) $(TARGET2) $(TARGET3) $(TARGET4) $(OBJS1) $(OBJS2) $(OBJS3) $(OBJS4)

run1: $(TARGET1)
		./cas 4

run2: $(TARGET2)
		./faa 4

run3: $(TARGET3)
		./tas 4

run4: $(TARGET4)
		./ttas 4
