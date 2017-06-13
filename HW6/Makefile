CXX             = g++ 
SRCS            = $(wildcard *.cpp)
OBJS            = $(SRCS:.cpp=.o)
TARGET          = comb_tree 

all : $(TARGET)
	$(CXX) -o $(TARGET) $(OBJS)
 
$(TARGET) :
	$(CXX) -c -std=c++11 $(SRCS)
		
clean :
	rm -f $(TARGET)
	rm -f *.o
