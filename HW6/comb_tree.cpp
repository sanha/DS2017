#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <mutex>
#include <condition_variable>

using namespace std;

/**
 * This class represents a node of software combining tree.
 **/
class Node {
private:
  enum CStatus{
    IDLE, FIRST, SECOND, RESULT, ROOT
  };

  mutex mtx;
  condition_variable cv;
  bool locked;
  int cStatus;
  int firstValue, secondValue;
  int result;
  Node *parent;

public:

  // Constructor for root node
  Node() {
    cStatus = ROOT;
    locked = false;
    parent = NULL;
    result = 0;
  }

  // Constructor for non-root node
  Node(Node *parent) {
    parent = parent;
    cStatus = IDLE;
    locked = false;
    firstValue = 0;
    secondValue = 0;
    result = 0;
  }

  bool precombine() {
    std::unique_lock<std::mutex> lock(mtx);
    cv.wait(lock, [this](){return !locked;});

    switch (cStatus) {
      case IDLE:
        cStatus = FIRST;
        return true;
      case FIRST:
        locked = true;
        cStatus = SECOND;
        return false;
      case ROOT:
        return false;
      default:
        cout << "unexpected Node state during pre-combining: " + cStatus << endl;
        exit(1);
    }
  }

  int combine(int combined) {
    std::unique_lock<std::mutex> lock(mtx);
    cv.wait(lock, [this](){return !locked;});
    
    locked = true;
    firstValue = combined;

    switch (cStatus) {
      case FIRST:
        return firstValue;
      case SECOND:
        return firstValue + secondValue;
      default:
        cout << "unexpected Node state during combining: " + cStatus << endl;
        exit(1);
    }
  }

  int operation(int combined) {
    std::unique_lock<std::mutex> lock(mtx);

    switch (cStatus) {
      case ROOT:
        int prior;
				prior = result;
        result += combined;
        return prior;
      case SECOND:
        secondValue = combined;
        locked = false;
        cv.notify_all();
        cv.wait(lock, [this](){return cStatus == RESULT;});
        locked = false;
        cv.notify_all();
        cStatus = IDLE;
        return result;
      default:
        cout << "unexpected Node state during operation: " + cStatus << endl;
        exit(1);
    }
  }

  void distribute(int prior) {
    std::unique_lock<std::mutex> lock(mtx);
    
    switch (cStatus) {
      case FIRST:
        cStatus = IDLE;
        locked = false;
        break;
      case SECOND:
        result = prior + firstValue;
        cStatus = RESULT;
        break;
      default:
        cout << "unexpected Node state during distribution: " + cStatus << endl;
				exit(1);
    }
    cv.notify_all();
  }

  Node *getParent() {
    return parent;
  }

  int getResult() {
    return result;
  }
};

int main()
{
  cout << "Hello world!" << endl;  

  return 0;
}
