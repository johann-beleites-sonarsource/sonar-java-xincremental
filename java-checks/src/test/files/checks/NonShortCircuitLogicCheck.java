class A {
  
  void method1() {
    getTrue() || getFalse();
    getTrue() && getFalse();
    getTrue() | getFalse(); // Noncompliant
    getTrue() & getFalse(); // Noncompliant
    Boolean.TRUE | Boolean.FALSE; // Noncompliant
    Boolean.TRUE & Boolean.FALSE; // Noncompliant
    getInt1() | getInt0();
    getInt1() & getInt0();
    unknown1 & unknown2;
  }

  boolean getTrue() {
    return true;
  }
  
  boolean getFalse() {
    return false;
  }
  
  int getInt1() {
    return 1;
  }
  
  int getInt0() {
    return 0;
  }

}